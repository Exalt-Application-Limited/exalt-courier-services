package com.gogidix.courier.courier.hqadmin.controller.region;

import com.socialecommerceecosystem.hqadmin.config.MultiRegionConfig;
import com.socialecommerceecosystem.hqadmin.model.policy.GlobalPolicy;
import com.socialecommerceecosystem.hqadmin.service.region.RegionSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST controller for managing multi-region operations.
 */
@RestController
@RequestMapping("/api/v1/regions")
@RequiredArgsConstructor
@Slf4j
public class RegionController {

    private final RegionSyncService regionSyncService;
    private final MultiRegionConfig multiRegionConfig;

    /**
     * GET /api/v1/regions : Get all configured regions
     * 
     * @return the ResponseEntity with status 200 (OK) and the list of regions
     */
    @GetMapping
    public ResponseEntity<List<RegionDTO>> getAllRegions() {
        log.debug("REST request to get all regions");
        
        List<RegionDTO> regions = multiRegionConfig.getRegions().stream()
            .map(region -> {
                RegionDTO dto = new RegionDTO();
                dto.setCode(region.getCode());
                dto.setName(region.getName());
                dto.setApiEndpoint(region.getApiEndpoint());
                dto.setActive(region.isActive());
                dto.setSyncIntervalSeconds(region.getSyncIntervalSeconds());
                
                // Add status info if available
                Map<String, RegionSyncService.RegionStatus> statusMap = regionSyncService.getRegionalStatus();
                if (statusMap.containsKey(region.getCode())) {
                    RegionSyncService.RegionStatus status = statusMap.get(region.getCode());
                    dto.setHealthy(status.isHealthy());
                    dto.setLastSyncTimestamp(status.getLastSyncTimestamp());
                    dto.setPendingSyncItems(status.getPendingSyncItems());
                    dto.setLastError(status.getLastError());
                }
                
                return dto;
            })
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(regions);
    }

    /**
     * GET /api/v1/regions/{code} : Get a specific region
     * 
     * @param code the code of the region to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the region, or with status 404 (Not Found)
     */
    @GetMapping("/{code}")
    public ResponseEntity<RegionDTO> getRegion(@PathVariable String code) {
        log.debug("REST request to get region : {}", code);
        
        return multiRegionConfig.getRegions().stream()
            .filter(region -> region.getCode().equals(code))
            .findFirst()
            .map(region -> {
                RegionDTO dto = new RegionDTO();
                dto.setCode(region.getCode());
                dto.setName(region.getName());
                dto.setApiEndpoint(region.getApiEndpoint());
                dto.setActive(region.isActive());
                dto.setSyncIntervalSeconds(region.getSyncIntervalSeconds());
                
                // Add status info if available
                Map<String, RegionSyncService.RegionStatus> statusMap = regionSyncService.getRegionalStatus();
                if (statusMap.containsKey(region.getCode())) {
                    RegionSyncService.RegionStatus status = statusMap.get(region.getCode());
                    dto.setHealthy(status.isHealthy());
                    dto.setLastSyncTimestamp(status.getLastSyncTimestamp());
                    dto.setPendingSyncItems(status.getPendingSyncItems());
                    dto.setLastError(status.getLastError());
                }
                
                return ResponseEntity.ok(dto);
            })
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Region not found with code: " + code));
    }

    /**
     * GET /api/v1/regions/health : Check the health of all regions
     * 
     * @return the ResponseEntity with status 200 (OK) and the map of region codes to health status
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Boolean>> checkRegionalHealth() {
        log.debug("REST request to check health of all regions");
        return ResponseEntity.ok(regionSyncService.checkRegionalHealth());
    }

    /**
     * GET /api/v1/regions/status : Get detailed status of all regions
     * 
     * @return the ResponseEntity with status 200 (OK) and the map of region codes to status information
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, RegionSyncService.RegionStatus>> getRegionalStatus() {
        log.debug("REST request to get detailed status of all regions");
        return ResponseEntity.ok(regionSyncService.getRegionalStatus());
    }

    /**
     * POST /api/v1/regions/sync : Force a full synchronization of all global policies to all regions
     * 
     * @return the ResponseEntity with status 200 (OK) and the map of region codes to sync status
     */
    @PostMapping("/sync")
    public ResponseEntity<Map<String, RegionSyncService.SyncResult>> forceSyncAllRegions() {
        log.debug("REST request to force sync of all policies to all regions");
        return ResponseEntity.ok(regionSyncService.forceSyncAllRegions());
    }

    /**
     * POST /api/v1/regions/sync/{code} : Force a full synchronization of all global policies to a specific region
     * 
     * @param code the code of the region to sync
     * @return the ResponseEntity with status 200 (OK) and the sync status
     */
    @PostMapping("/sync/{code}")
    public ResponseEntity<RegionSyncService.SyncResult> forceSyncRegion(@PathVariable String code) {
        log.debug("REST request to force sync of all policies to region: {}", code);
        
        if (!multiRegionConfig.getRegions().stream().anyMatch(r -> r.getCode().equals(code) && r.isActive())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Region not found or not active: " + code);
        }
        
        Map<String, RegionSyncService.SyncResult> results = regionSyncService.forceSyncAllRegions();
        
        if (!results.containsKey(code)) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to sync region: " + code);
        }
        
        return ResponseEntity.ok(results.get(code));
    }

    /**
     * POST /api/v1/regions/sync/policy/{policyKey} : Synchronize a specific policy to all regions
     * 
     * @param policyKey the key of the policy to synchronize
     * @return the ResponseEntity with status 200 (OK) and the map of region codes to sync status
     */
    @PostMapping("/sync/policy/{policyKey}")
    public ResponseEntity<Map<String, Boolean>> syncPolicyToAllRegions(@PathVariable String policyKey) {
        log.debug("REST request to sync policy {} to all regions", policyKey);
        return ResponseEntity.ok(regionSyncService.pushPolicyToAllRegions(policyKey));
    }

    /**
     * POST /api/v1/regions/sync/policy/{policyKey}/region/{code} : Synchronize a specific policy to a specific region
     * 
     * @param policyKey the key of the policy to synchronize
     * @param code the code of the region to sync
     * @return the ResponseEntity with status 200 (OK) and the sync status
     */
    @PostMapping("/sync/policy/{policyKey}/region/{code}")
    public ResponseEntity<Boolean> syncPolicyToRegion(@PathVariable String policyKey, @PathVariable String code) {
        log.debug("REST request to sync policy {} to region {}", policyKey, code);
        
        if (!multiRegionConfig.getRegions().stream().anyMatch(r -> r.getCode().equals(code) && r.isActive())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Region not found or not active: " + code);
        }
        
        boolean result = regionSyncService.pushPolicyToRegion(policyKey, code);
        return ResponseEntity.ok(result);
    }

    /**
     * POST /api/v1/regions/sync/policies/region/{code} : Synchronize multiple policies to a specific region
     * 
     * @param code the code of the region to sync
     * @param policyKeys the list of policy keys to synchronize
     * @return the ResponseEntity with status 200 (OK) and the map of policy keys to sync status
     */
    @PostMapping("/sync/policies/region/{code}")
    public ResponseEntity<Map<String, Boolean>> syncPoliciesToRegion(
            @PathVariable String code,
            @RequestBody List<String> policyKeys) {
        log.debug("REST request to sync policies to region {}: {}", code, policyKeys);
        
        if (!multiRegionConfig.getRegions().stream().anyMatch(r -> r.getCode().equals(code) && r.isActive())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Region not found or not active: " + code);
        }
        
        Map<String, Boolean> results = regionSyncService.pushPoliciesToRegion(policyKeys, code);
        return ResponseEntity.ok(results);
    }

    /**
     * GET /api/v1/regions/{code}/policies : Pull all policies from a specific region
     * 
     * @param code the code of the region to pull from
     * @return the ResponseEntity with status 200 (OK) and the list of regional policies
     */
    @GetMapping("/{code}/policies")
    public ResponseEntity<List<GlobalPolicy>> pullAllPoliciesFromRegion(@PathVariable String code) {
        log.debug("REST request to pull all policies from region {}", code);
        
        if (!multiRegionConfig.getRegions().stream().anyMatch(r -> r.getCode().equals(code) && r.isActive())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Region not found or not active: " + code);
        }
        
        List<GlobalPolicy> policies = regionSyncService.pullAllPoliciesFromRegion(code);
        return ResponseEntity.ok(policies);
    }

    /**
     * Region DTO for returning region information.
     */
    public static class RegionDTO {
        private String code;
        private String name;
        private String apiEndpoint;
        private boolean active;
        private int syncIntervalSeconds;
        private boolean healthy;
        private long lastSyncTimestamp;
        private int pendingSyncItems;
        private String lastError;
        
        // Getters and setters
        public String getCode() {
            return code;
        }
        
        public void setCode(String code) {
            this.code = code;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getApiEndpoint() {
            return apiEndpoint;
        }
        
        public void setApiEndpoint(String apiEndpoint) {
            this.apiEndpoint = apiEndpoint;
        }
        
        public boolean isActive() {
            return active;
        }
        
        public void setActive(boolean active) {
            this.active = active;
        }
        
        public int getSyncIntervalSeconds() {
            return syncIntervalSeconds;
        }
        
        public void setSyncIntervalSeconds(int syncIntervalSeconds) {
            this.syncIntervalSeconds = syncIntervalSeconds;
        }
        
        public boolean isHealthy() {
            return healthy;
        }
        
        public void setHealthy(boolean healthy) {
            this.healthy = healthy;
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
}
