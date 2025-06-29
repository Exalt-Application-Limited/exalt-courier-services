package com.exalt.courier.hqadmin.service.region.impl;

import com.socialecommerceecosystem.hqadmin.config.MultiRegionConfig;
import com.socialecommerceecosystem.hqadmin.model.policy.GlobalPolicy;
import com.socialecommerceecosystem.hqadmin.repository.policy.GlobalPolicyRepository;
import com.socialecommerceecosystem.hqadmin.service.policy.GlobalPolicyService;
import com.socialecommerceecosystem.hqadmin.service.region.RegionSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Implementation of the RegionSyncService for multi-region policy synchronization.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RegionSyncServiceImpl implements RegionSyncService {

    private final GlobalPolicyService globalPolicyService;
    private final GlobalPolicyRepository globalPolicyRepository;
    private final MultiRegionConfig multiRegionConfig;
    
    private final Map<String, RegionStatus> regionStatusMap = new ConcurrentHashMap<>();
    private final RestTemplate restTemplate;
    
    /**
     * Constructor with RestTemplateBuilder for configuring timeouts.
     */
    public RegionSyncServiceImpl(
            GlobalPolicyService globalPolicyService,
            GlobalPolicyRepository globalPolicyRepository,
            MultiRegionConfig multiRegionConfig,
            RestTemplateBuilder restTemplateBuilder) {
        this.globalPolicyService = globalPolicyService;
        this.globalPolicyRepository = globalPolicyRepository;
        this.multiRegionConfig = multiRegionConfig;
        
        // Configure RestTemplate with timeouts
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(10))
                .build();
        
        // Initialize region status map
        initializeRegionStatus();
    }
    
    private void initializeRegionStatus() {
        for (MultiRegionConfig.RegionProperties region : multiRegionConfig.getRegions()) {
            if (!region.getCode().equals(multiRegionConfig.getDefaultRegionCode())) {
                RegionStatus status = new RegionStatus();
                status.setRegionCode(region.getCode());
                status.setRegionName(region.getName());
                status.setActive(region.isActive());
                status.setHealthy(false); // Will be updated by health check
                status.setLastSyncTimestamp(0L);
                status.setPendingSyncItems(0);
                
                regionStatusMap.put(region.getCode(), status);
            }
        }
    }

    @Override
    public Map<String, Boolean> pushPolicyToAllRegions(String policyKey) {
        log.info("Pushing policy with key {} to all regions", policyKey);
        
        Map<String, Boolean> results = new HashMap<>();
        
        Optional<GlobalPolicy> policyOpt = globalPolicyService.getPolicyByKey(policyKey);
        if (policyOpt.isEmpty()) {
            log.warn("Policy with key {} not found", policyKey);
            return Collections.emptyMap();
        }
        
        GlobalPolicy policy = policyOpt.get();
        
        // Only synchronize global policies (not region-specific ones)
        if (policy.getGlobalRegion() != null) {
            log.warn("Policy with key {} is region-specific and cannot be synchronized globally", policyKey);
            return Collections.emptyMap();
        }
        
        for (MultiRegionConfig.RegionProperties region : multiRegionConfig.getRegions()) {
            // Skip the default/global region
            if (region.getCode().equals(multiRegionConfig.getDefaultRegionCode()) || !region.isActive()) {
                continue;
            }
            
            boolean success = pushPolicyToRegion(policyKey, region.getCode());
            results.put(region.getCode(), success);
        }
        
        return results;
    }

    @Override
    public boolean pushPolicyToRegion(String policyKey, String regionCode) {
        log.info("Pushing policy with key {} to region {}", policyKey, regionCode);
        
        // Get the region properties
        Optional<MultiRegionConfig.RegionProperties> regionOpt = multiRegionConfig.getRegions().stream()
                .filter(r -> r.getCode().equals(regionCode) && r.isActive())
                .findFirst();
        
        if (regionOpt.isEmpty()) {
            log.warn("Region with code {} not found or not active", regionCode);
            return false;
        }
        
        MultiRegionConfig.RegionProperties region = regionOpt.get();
        
        // Get the policy
        Optional<GlobalPolicy> policyOpt = globalPolicyService.getPolicyByKey(policyKey);
        if (policyOpt.isEmpty()) {
            log.warn("Policy with key {} not found", policyKey);
            return false;
        }
        
        GlobalPolicy policy = policyOpt.get();
        
        // Only synchronize global policies (not region-specific ones)
        if (policy.getGlobalRegion() != null) {
            log.warn("Policy with key {} is region-specific and cannot be synchronized", policyKey);
            return false;
        }
        
        try {
            // Build the URL for the regional API
            String url = region.getApiEndpoint() + "/api/v1/policies/sync";
            
            // Push the policy to the region
            ResponseEntity<String> response = restTemplate.postForEntity(url, policy, String.class);
            
            boolean success = response.getStatusCode().is2xxSuccessful();
            
            // Update region status
            updateRegionStatus(regionCode, success, null);
            
            return success;
        } catch (RestClientException e) {
            log.error("Error pushing policy to region {}: {}", regionCode, e.getMessage());
            updateRegionStatus(regionCode, false, e.getMessage());
            return false;
        }
    }

    @Override
    public Map<String, Boolean> pushPoliciesToRegion(List<String> policyKeys, String regionCode) {
        log.info("Pushing {} policies to region {}", policyKeys.size(), regionCode);
        
        Map<String, Boolean> results = new HashMap<>();
        
        for (String policyKey : policyKeys) {
            boolean success = pushPolicyToRegion(policyKey, regionCode);
            results.put(policyKey, success);
        }
        
        return results;
    }

    @Override
    public Optional<GlobalPolicy> pullPolicyFromRegion(String policyKey, String regionCode) {
        log.info("Pulling policy with key {} from region {}", policyKey, regionCode);
        
        // Get the region properties
        Optional<MultiRegionConfig.RegionProperties> regionOpt = multiRegionConfig.getRegions().stream()
                .filter(r -> r.getCode().equals(regionCode) && r.isActive())
                .findFirst();
        
        if (regionOpt.isEmpty()) {
            log.warn("Region with code {} not found or not active", regionCode);
            return Optional.empty();
        }
        
        MultiRegionConfig.RegionProperties region = regionOpt.get();
        
        try {
            // Build the URL for the regional API
            String url = region.getApiEndpoint() + "/api/v1/policies/key/" + policyKey;
            
            // Pull the policy from the region
            ResponseEntity<GlobalPolicy> response = restTemplate.getForEntity(url, GlobalPolicy.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // Update region status
                updateRegionStatus(regionCode, true, null);
                
                return Optional.of(response.getBody());
            }
            
            return Optional.empty();
        } catch (RestClientException e) {
            log.error("Error pulling policy from region {}: {}", regionCode, e.getMessage());
            updateRegionStatus(regionCode, false, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<GlobalPolicy> pullAllPoliciesFromRegion(String regionCode) {
        log.info("Pulling all policies from region {}", regionCode);
        
        // Get the region properties
        Optional<MultiRegionConfig.RegionProperties> regionOpt = multiRegionConfig.getRegions().stream()
                .filter(r -> r.getCode().equals(regionCode) && r.isActive())
                .findFirst();
        
        if (regionOpt.isEmpty()) {
            log.warn("Region with code {} not found or not active", regionCode);
            return Collections.emptyList();
        }
        
        MultiRegionConfig.RegionProperties region = regionOpt.get();
        
        try {
            // Build the URL for the regional API
            String url = region.getApiEndpoint() + "/api/v1/policies";
            
            // Pull all policies from the region
            ResponseEntity<GlobalPolicy[]> response = restTemplate.getForEntity(url, GlobalPolicy[].class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // Update region status
                updateRegionStatus(regionCode, true, null);
                
                return List.of(response.getBody());
            }
            
            return Collections.emptyList();
        } catch (RestClientException e) {
            log.error("Error pulling policies from region {}: {}", regionCode, e.getMessage());
            updateRegionStatus(regionCode, false, e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public Map<String, Boolean> checkRegionalHealth() {
        log.info("Checking health of all regions");
        
        Map<String, Boolean> results = new HashMap<>();
        
        for (MultiRegionConfig.RegionProperties region : multiRegionConfig.getRegions()) {
            // Skip the default/global region
            if (region.getCode().equals(multiRegionConfig.getDefaultRegionCode()) || !region.isActive()) {
                continue;
            }
            
            boolean isHealthy = checkRegionHealth(region.getCode());
            results.put(region.getCode(), isHealthy);
            
            // Update region status
            if (regionStatusMap.containsKey(region.getCode())) {
                regionStatusMap.get(region.getCode()).setHealthy(isHealthy);
            }
        }
        
        return results;
    }
    
    private boolean checkRegionHealth(String regionCode) {
        // Get the region properties
        Optional<MultiRegionConfig.RegionProperties> regionOpt = multiRegionConfig.getRegions().stream()
                .filter(r -> r.getCode().equals(regionCode) && r.isActive())
                .findFirst();
        
        if (regionOpt.isEmpty()) {
            return false;
        }
        
        MultiRegionConfig.RegionProperties region = regionOpt.get();
        
        try {
            // Build the URL for the regional API health check
            String url = region.getApiEndpoint() + "/actuator/health";
            
            // Check the health of the region
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            
            return response.getStatusCode().is2xxSuccessful() &&
                   response.getBody() != null &&
                   "UP".equals(response.getBody().get("status"));
        } catch (Exception e) {
            log.error("Error checking health of region {}: {}", regionCode, e.getMessage());
            return false;
        }
    }

    @Override
    public Map<String, RegionStatus> getRegionalStatus() {
        return new HashMap<>(regionStatusMap);
    }

    @Override
    public Map<String, SyncResult> forceSyncAllRegions() {
        log.info("Forcing sync of all policies to all regions");
        
        Map<String, SyncResult> results = new HashMap<>();
        
        // Get all global policies
        List<GlobalPolicy> globalPolicies = globalPolicyService.getGlobalPolicies();
        
        for (MultiRegionConfig.RegionProperties region : multiRegionConfig.getRegions()) {
            // Skip the default/global region
            if (region.getCode().equals(multiRegionConfig.getDefaultRegionCode()) || !region.isActive()) {
                continue;
            }
            
            SyncResult result = new SyncResult();
            result.setTotalPolicies(globalPolicies.size());
            result.setSuccessfulPolicies(0);
            result.setFailedPolicies(0);
            result.setErrors(new ArrayList<>());
            
            for (GlobalPolicy policy : globalPolicies) {
                try {
                    boolean success = pushPolicyToRegion(policy.getPolicyKey(), region.getCode());
                    
                    if (success) {
                        result.setSuccessfulPolicies(result.getSuccessfulPolicies() + 1);
                    } else {
                        result.setFailedPolicies(result.getFailedPolicies() + 1);
                        result.getErrors().add("Failed to push policy " + policy.getPolicyKey() + " to region " + region.getCode());
                    }
                } catch (Exception e) {
                    result.setFailedPolicies(result.getFailedPolicies() + 1);
                    result.getErrors().add("Error pushing policy " + policy.getPolicyKey() + " to region " + region.getCode() + ": " + e.getMessage());
                }
            }
            
            result.setSuccess(result.getFailedPolicies() == 0);
            results.put(region.getCode(), result);
            
            // Update region status
            if (regionStatusMap.containsKey(region.getCode())) {
                RegionStatus status = regionStatusMap.get(region.getCode());
                status.setLastSyncTimestamp(System.currentTimeMillis());
                status.setPendingSyncItems(result.getFailedPolicies());
                if (!result.getErrors().isEmpty()) {
                    status.setLastError(result.getErrors().get(0));
                }
            }
        }
        
        return results;
    }

    @Override
    public Map<String, SyncResult> syncPolicyTypeToAllRegions(String policyType) {
        log.info("Syncing policies of type {} to all regions", policyType);
        
        Map<String, SyncResult> results = new HashMap<>();
        
        // Get all global policies of the specified type
        List<GlobalPolicy> globalPolicies = globalPolicyService.getGlobalPolicies().stream()
                .filter(p -> p.getPolicyType().name().equals(policyType))
                .collect(Collectors.toList());
        
        if (globalPolicies.isEmpty()) {
            log.warn("No policies found for type {}", policyType);
            return Collections.emptyMap();
        }
        
        for (MultiRegionConfig.RegionProperties region : multiRegionConfig.getRegions()) {
            // Skip the default/global region
            if (region.getCode().equals(multiRegionConfig.getDefaultRegionCode()) || !region.isActive()) {
                continue;
            }
            
            SyncResult result = new SyncResult();
            result.setTotalPolicies(globalPolicies.size());
            result.setSuccessfulPolicies(0);
            result.setFailedPolicies(0);
            result.setErrors(new ArrayList<>());
            
            for (GlobalPolicy policy : globalPolicies) {
                try {
                    boolean success = pushPolicyToRegion(policy.getPolicyKey(), region.getCode());
                    
                    if (success) {
                        result.setSuccessfulPolicies(result.getSuccessfulPolicies() + 1);
                    } else {
                        result.setFailedPolicies(result.getFailedPolicies() + 1);
                        result.getErrors().add("Failed to push policy " + policy.getPolicyKey() + " to region " + region.getCode());
                    }
                } catch (Exception e) {
                    result.setFailedPolicies(result.getFailedPolicies() + 1);
                    result.getErrors().add("Error pushing policy " + policy.getPolicyKey() + " to region " + region.getCode() + ": " + e.getMessage());
                }
            }
            
            result.setSuccess(result.getFailedPolicies() == 0);
            results.put(region.getCode(), result);
        }
        
        return results;
    }
    
    /**
     * Scheduled job to check the health of all regions.
     */
    @Scheduled(fixedRateString = "${global-hq-admin.multi-region.health-check-interval-seconds:60}000")
    public void scheduledHealthCheck() {
        if (multiRegionConfig.isEnabled()) {
            log.debug("Running scheduled health check for all regions");
            checkRegionalHealth();
        }
    }
    
    /**
     * Scheduled job to synchronize policies to all regions.
     */
    @Scheduled(cron = "0 0 * * * *") // Every hour
    public void scheduledPolicySync() {
        if (multiRegionConfig.isEnabled() && 
                MultiRegionConfig.SyncStrategy.PUSH.equals(multiRegionConfig.getSyncStrategy()) ||
                MultiRegionConfig.SyncStrategy.BIDIRECTIONAL.equals(multiRegionConfig.getSyncStrategy())) {
            log.info("Running scheduled policy sync to all regions");
            forceSyncAllRegions();
        }
    }
    
    private void updateRegionStatus(String regionCode, boolean success, String errorMessage) {
        if (regionStatusMap.containsKey(regionCode)) {
            RegionStatus status = regionStatusMap.get(regionCode);
            
            if (success) {
                status.setLastSyncTimestamp(System.currentTimeMillis());
                status.setLastError(null);
            } else if (errorMessage != null) {
                status.setLastError(errorMessage);
            }
        }
    }
    
    /**
     * Status class for region information.
     */
    public static class RegionStatus {
        private String regionCode;
        private String regionName;
        private boolean isActive;
        private boolean isHealthy;
        private long lastSyncTimestamp;
        private int pendingSyncItems;
        private String lastError;
        
        // getters and setters
        public String getRegionCode() {
            return regionCode;
        }
        
        public void setRegionCode(String regionCode) {
            this.regionCode = regionCode;
        }
        
        public String getRegionName() {
            return regionName;
        }
        
        public void setRegionName(String regionName) {
            this.regionName = regionName;
        }
        
        public boolean isActive() {
            return isActive;
        }
        
        public void setActive(boolean active) {
            isActive = active;
        }
        
        public boolean isHealthy() {
            return isHealthy;
        }
        
        public void setHealthy(boolean healthy) {
            isHealthy = healthy;
        }
        
        public long getLastSyncTimestamp() {
            return lastSyncTimestamp;
        }
        
        public void setLastSyncTimestamp(long lastSyncTimestamp) {
            this.lastSyncTimestamp = lastSyncTimestamp;
        }
        
        public int getPendingSyncItems() {
            return pendingSyncItems;
        }
        
        public void setPendingSyncItems(int pendingSyncItems) {
            this.pendingSyncItems = pendingSyncItems;
        }
        
        public String getLastError() {
            return lastError;
        }
        
        public void setLastError(String lastError) {
            this.lastError = lastError;
        }
    }
    
    /**
     * Result class for synchronization operations.
     */
    public static class SyncResult {
        private boolean success;
        private int totalPolicies;
        private int successfulPolicies;
        private int failedPolicies;
        private List<String> errors;
        
        // getters and setters
        public boolean isSuccess() {
            return success;
        }
        
        public void setSuccess(boolean success) {
            this.success = success;
        }
        
        public int getTotalPolicies() {
            return totalPolicies;
        }
        
        public void setTotalPolicies(int totalPolicies) {
            this.totalPolicies = totalPolicies;
        }
        
        public int getSuccessfulPolicies() {
            return successfulPolicies;
        }
        
        public void setSuccessfulPolicies(int successfulPolicies) {
            this.successfulPolicies = successfulPolicies;
        }
        
        public int getFailedPolicies() {
            return failedPolicies;
        }
        
        public void setFailedPolicies(int failedPolicies) {
            this.failedPolicies = failedPolicies;
        }
        
        public List<String> getErrors() {
            return errors;
        }
        
        public void setErrors(List<String> errors) {
            this.errors = errors;
        }
    }
}
