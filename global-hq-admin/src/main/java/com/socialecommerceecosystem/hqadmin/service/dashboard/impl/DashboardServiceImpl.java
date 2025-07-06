package com.gogidix.courier.courier.hqadmin.service.dashboard.impl;

import com.socialecommerceecosystem.hqadmin.config.MultiRegionConfig;
import com.socialecommerceecosystem.hqadmin.model.policy.ApprovalStatus;
import com.socialecommerceecosystem.hqadmin.model.policy.GlobalPolicy;
import com.socialecommerceecosystem.hqadmin.model.policy.PolicyType;
import com.socialecommerceecosystem.hqadmin.repository.policy.GlobalPolicyRepository;
import com.socialecommerceecosystem.hqadmin.service.dashboard.DashboardService;
import com.socialecommerceecosystem.hqadmin.service.policy.GlobalPolicyService;
import com.socialecommerceecosystem.hqadmin.service.region.RegionSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/**
 * Implementation of the DashboardService for global health dashboard.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final GlobalPolicyService globalPolicyService;
    private final GlobalPolicyRepository globalPolicyRepository;
    private final RegionSyncService regionSyncService;
    private final MultiRegionConfig multiRegionConfig;
    private final List<HealthIndicator> healthIndicators;
    private final MetricsEndpoint metricsEndpoint;
    
    // Cache for system health
    private Map<String, HealthStatus> systemHealthCache = new ConcurrentHashMap<>();
    
    // Cache for performance metrics
    private Map<String, Object> performanceMetricsCache = new ConcurrentHashMap<>();
    
    // Queue for recent policy activities
    private final ConcurrentLinkedQueue<PolicyActivity> recentPolicyActivities = new ConcurrentLinkedQueue<>();
    
    // Queue for system alerts
    private final ConcurrentLinkedQueue<SystemAlert> systemAlerts = new ConcurrentLinkedQueue<>();
    
    // Active sync operations
    private final Map<String, SyncOperation> activeSyncOperations = new ConcurrentHashMap<>();

    @Override
    public Map<String, HealthStatus> getSystemHealth() {
        return new HashMap<>(systemHealthCache);
    }

    @Override
    public Map<String, Object> getPerformanceMetrics() {
        return new HashMap<>(performanceMetricsCache);
    }

    @Override
    public Map<String, RegionSummary> getRegionalStatusSummary() {
        Map<String, RegionSummary> summaries = new HashMap<>();
        
        // Get region statuses
        Map<String, RegionSyncService.RegionStatus> regionStatuses = regionSyncService.getRegionalStatus();
        
        for (MultiRegionConfig.RegionProperties region : multiRegionConfig.getRegions()) {
            // Skip the default/global region
            if (region.getCode().equals(multiRegionConfig.getDefaultRegionCode())) {
                continue;
            }
            
            RegionSummary summary = new RegionSummary();
            summary.setRegionCode(region.getCode());
            summary.setRegionName(region.getName());
            
            // Set health status based on region status
            if (regionStatuses.containsKey(region.getCode())) {
                RegionSyncService.RegionStatus status = regionStatuses.get(region.getCode());
                
                if (!region.isActive()) {
                    summary.setStatus(HealthStatus.DOWN);
                } else if (status.isHealthy()) {
                    summary.setStatus(HealthStatus.UP);
                } else {
                    summary.setStatus(HealthStatus.DEGRADED);
                }
                
                summary.setLastSyncTimestamp(status.getLastSyncTimestamp());
            } else {
                summary.setStatus(HealthStatus.UNKNOWN);
            }
            
            // Fetch policy counts for the region
            try {
                int totalPolicies = globalPolicyRepository.findByGlobalRegion_Code(region.getCode()).size();
                int activePolicies = globalPolicyRepository.findByGlobalRegion_CodeAndIsActiveTrue(region.getCode()).size();
                
                summary.setTotalPolicies(totalPolicies);
                summary.setActivePolicies(activePolicies);
            } catch (Exception e) {
                log.error("Error fetching policy counts for region {}: {}", region.getCode(), e.getMessage());
                summary.setTotalPolicies(0);
                summary.setActivePolicies(0);
            }
            
            summaries.put(region.getCode(), summary);
        }
        
        return summaries;
    }

    @Override
    public List<PolicyActivity> getRecentPolicyActivities(int limit) {
        return recentPolicyActivities.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getPolicyStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // Total policies
            stats.put("totalPolicies", globalPolicyRepository.count());
            
            // Policies by status
            stats.put("activePolicies", globalPolicyRepository.findByIsActiveTrue().size());
            stats.put("inactivePolicies", globalPolicyRepository.findByIsActiveFalse().size());
            
            // Policies by approval status
            stats.put("draftPolicies", globalPolicyRepository.findByApprovalStatus(ApprovalStatus.DRAFT).size());
            stats.put("pendingApprovalPolicies", globalPolicyRepository.findByApprovalStatus(ApprovalStatus.PENDING_APPROVAL).size());
            stats.put("approvedPolicies", globalPolicyRepository.findByApprovalStatus(ApprovalStatus.APPROVED).size());
            stats.put("rejectedPolicies", globalPolicyRepository.findByApprovalStatus(ApprovalStatus.REJECTED).size());
            
            // Global vs regional policies
            stats.put("globalPolicies", globalPolicyRepository.findByGlobalRegionIsNull().size());
            stats.put("regionalPolicies", globalPolicyRepository.findByGlobalRegionIsNotNull().size());
            
            // Policies by type
            Map<PolicyType, Long> policyTypeCount = globalPolicyService.countPoliciesByType();
            for (Map.Entry<PolicyType, Long> entry : policyTypeCount.entrySet()) {
                stats.put("policies_" + entry.getKey().name(), entry.getValue());
            }
            
            // Mandatory policies
            stats.put("mandatoryPolicies", globalPolicyRepository.findByIsMandatoryTrue().size());
            
            // Effective policies count
            stats.put("effectivePolicies", globalPolicyService.getEffectivePolicies().size());
            stats.put("futurePolicies", globalPolicyService.getFuturePolicies().size());
            stats.put("expiredPolicies", globalPolicyService.getExpiredPolicies().size());
        } catch (Exception e) {
            log.error("Error calculating policy statistics: {}", e.getMessage());
        }
        
        return stats;
    }

    @Override
    public List<SyncOperation> getActiveSyncOperations() {
        return new ArrayList<>(activeSyncOperations.values());
    }

    @Override
    public List<SystemAlert> getSystemAlerts() {
        return new ArrayList<>(systemAlerts);
    }
    
    /**
     * Add a policy activity.
     */
    public void addPolicyActivity(PolicyActivity activity) {
        // Add to the front of the queue
        recentPolicyActivities.add(activity);
        
        // Keep only the most recent 100 activities
        while (recentPolicyActivities.size() > 100) {
            recentPolicyActivities.poll();
        }
    }
    
    /**
     * Add a system alert.
     */
    public void addSystemAlert(SystemAlert alert) {
        systemAlerts.add(alert);
        
        // Keep only the most recent 100 alerts
        while (systemAlerts.size() > 100) {
            systemAlerts.poll();
        }
    }
    
    /**
     * Add or update a sync operation.
     */
    public void updateSyncOperation(SyncOperation operation) {
        activeSyncOperations.put(operation.getId(), operation);
    }
    
    /**
     * Remove a sync operation.
     */
    public void removeSyncOperation(String operationId) {
        activeSyncOperations.remove(operationId);
    }
    
    /**
     * Scheduled job to update system health.
     */
    @Scheduled(fixedRate = 60000) // Every minute
    public void updateSystemHealth() {
        log.debug("Updating system health cache");
        
        Map<String, HealthStatus> health = new HashMap<>();
        
        // Check application health from health indicators
        for (HealthIndicator indicator : healthIndicators) {
            try {
                String name = indicator.getClass().getSimpleName().replace("HealthIndicator", "");
                Health indicatorHealth = indicator.health();
                
                if (Health.up().equals(indicatorHealth)) {
                    health.put(name, HealthStatus.UP);
                } else if (Health.down().equals(indicatorHealth)) {
                    health.put(name, HealthStatus.DOWN);
                } else {
                    health.put(name, HealthStatus.DEGRADED);
                }
            } catch (Exception e) {
                log.error("Error checking health indicator: {}", e.getMessage());
            }
        }
        
        // Check regional health
        Map<String, Boolean> regionHealth = regionSyncService.checkRegionalHealth();
        for (Map.Entry<String, Boolean> entry : regionHealth.entrySet()) {
            health.put("Region_" + entry.getKey(), entry.getValue() ? HealthStatus.UP : HealthStatus.DOWN);
        }
        
        // Add overall status
        boolean hasDown = health.values().contains(HealthStatus.DOWN);
        boolean hasDegraded = health.values().contains(HealthStatus.DEGRADED);
        
        if (hasDown) {
            health.put("Overall", HealthStatus.DOWN);
        } else if (hasDegraded) {
            health.put("Overall", HealthStatus.DEGRADED);
        } else {
            health.put("Overall", HealthStatus.UP);
        }
        
        // Update cache
        systemHealthCache = health;
    }
    
    /**
     * Scheduled job to update performance metrics.
     */
    @Scheduled(fixedRate = 60000) // Every minute
    public void updatePerformanceMetrics() {
        log.debug("Updating performance metrics cache");
        
        Map<String, Object> metrics = new HashMap<>();
        
        // Add JVM metrics
        try {
            MetricsEndpoint.MetricResponse jvmMemoryUsed = metricsEndpoint.metric("jvm.memory.used", null);
            if (jvmMemoryUsed != null && !jvmMemoryUsed.getMeasurements().isEmpty()) {
                metrics.put("jvmMemoryUsed", jvmMemoryUsed.getMeasurements().get(0).getValue());
            }
            
            MetricsEndpoint.MetricResponse jvmMemoryMax = metricsEndpoint.metric("jvm.memory.max", null);
            if (jvmMemoryMax != null && !jvmMemoryMax.getMeasurements().isEmpty()) {
                metrics.put("jvmMemoryMax", jvmMemoryMax.getMeasurements().get(0).getValue());
            }
            
            MetricsEndpoint.MetricResponse processCpuUsage = metricsEndpoint.metric("process.cpu.usage", null);
            if (processCpuUsage != null && !processCpuUsage.getMeasurements().isEmpty()) {
                metrics.put("processCpuUsage", processCpuUsage.getMeasurements().get(0).getValue());
            }
            
            MetricsEndpoint.MetricResponse systemCpuUsage = metricsEndpoint.metric("system.cpu.usage", null);
            if (systemCpuUsage != null && !systemCpuUsage.getMeasurements().isEmpty()) {
                metrics.put("systemCpuUsage", systemCpuUsage.getMeasurements().get(0).getValue());
            }
        } catch (Exception e) {
            log.error("Error fetching JVM metrics: {}", e.getMessage());
        }
        
        // Add HTTP metrics
        try {
            MetricsEndpoint.MetricResponse httpServerRequests = metricsEndpoint.metric("http.server.requests", null);
            if (httpServerRequests != null && !httpServerRequests.getMeasurements().isEmpty()) {
                metrics.put("httpRequestCount", httpServerRequests.getMeasurements().get(0).getValue());
            }
        } catch (Exception e) {
            log.error("Error fetching HTTP metrics: {}", e.getMessage());
        }
        
        // Add database metrics
        try {
            metrics.put("activePolicies", globalPolicyRepository.findByIsActiveTrue().size());
            metrics.put("totalPolicies", globalPolicyRepository.count());
            metrics.put("pendingApprovalPolicies", globalPolicyRepository.findByApprovalStatus(ApprovalStatus.PENDING_APPROVAL).size());
        } catch (Exception e) {
            log.error("Error fetching database metrics: {}", e.getMessage());
        }
        
        // Add timestamp
        metrics.put("timestamp", System.currentTimeMillis());
        
        // Update cache
        performanceMetricsCache = metrics;
    }
    
    /**
     * Scheduled job to clean up old alerts.
     */
    @Scheduled(fixedRate = 3600000) // Every hour
    public void cleanupOldAlerts() {
        log.debug("Cleaning up old alerts");
        
        long cutoffTime = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000); // 7 days
        
        // Remove alerts older than the cutoff time
        systemAlerts.removeIf(alert -> alert.getTimestamp() < cutoffTime);
    }
    
    /**
     * Scheduled job to clean up completed sync operations.
     */
    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void cleanupCompletedSyncOperations() {
        log.debug("Cleaning up completed sync operations");
        
        // Remove sync operations that are marked as complete
        activeSyncOperations.entrySet().removeIf(entry -> 
            "COMPLETED".equals(entry.getValue().getStatus()) || 
            "FAILED".equals(entry.getValue().getStatus()));
    }
    
    /**
     * Scheduled job to check for stale sync operations.
     */
    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void checkStaleSyncOperations() {
        log.debug("Checking for stale sync operations");
        
        long staleCutoffTime = System.currentTimeMillis() - (30 * 60 * 1000); // 30 minutes
        
        for (SyncOperation operation : activeSyncOperations.values()) {
            if (operation.getStartTimestamp() < staleCutoffTime && 
                !("COMPLETED".equals(operation.getStatus()) || "FAILED".equals(operation.getStatus()))) {
                
                // Mark as failed
                operation.setStatus("FAILED");
                operation.setDetails("Operation timed out after 30 minutes");
                
                // Add an alert
                SystemAlert alert = new SystemAlert();
                alert.setId(UUID.randomUUID().toString());
                alert.setType("SYNC_TIMEOUT");
                alert.setSeverity("WARNING");
                alert.setMessage("Sync operation " + operation.getId() + " timed out after 30 minutes");
                alert.setTimestamp(System.currentTimeMillis());
                alert.setAcknowledged(false);
                alert.setComponent("RegionSync");
                
                addSystemAlert(alert);
            }
        }
    }
}
