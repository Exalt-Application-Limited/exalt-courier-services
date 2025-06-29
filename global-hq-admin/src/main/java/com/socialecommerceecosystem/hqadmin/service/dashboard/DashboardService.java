package com.exalt.courier.hqadmin.service.dashboard;

import java.util.List;
import java.util.Map;

/**
 * Service interface for global health dashboard.
 */
public interface DashboardService {

    /**
     * Get system health summary.
     * 
     * @return a map of component names to health status
     */
    Map<String, HealthStatus> getSystemHealth();
    
    /**
     * Get key performance metrics.
     * 
     * @return a map of metric names to values
     */
    Map<String, Object> getPerformanceMetrics();
    
    /**
     * Get regional status summary.
     * 
     * @return a map of region codes to status summaries
     */
    Map<String, RegionSummary> getRegionalStatusSummary();
    
    /**
     * Get recent policy activities.
     * 
     * @param limit the maximum number of activities to return
     * @return a list of recent policy activities
     */
    List<PolicyActivity> getRecentPolicyActivities(int limit);
    
    /**
     * Get policy statistics.
     * 
     * @return a map of statistic names to values
     */
    Map<String, Object> getPolicyStatistics();
    
    /**
     * Get active sync operations.
     * 
     * @return a list of active sync operations
     */
    List<SyncOperation> getActiveSyncOperations();
    
    /**
     * Get system alerts.
     * 
     * @return a list of system alerts
     */
    List<SystemAlert> getSystemAlerts();
    
    /**
     * Health status enum.
     */
    enum HealthStatus {
        UP,
        DOWN,
        DEGRADED,
        UNKNOWN
    }
    
    /**
     * Region summary class.
     */
    class RegionSummary {
        private String regionCode;
        private String regionName;
        private HealthStatus status;
        private int totalPolicies;
        private int activePolicies;
        private long lastSyncTimestamp;
        
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
        
        public HealthStatus getStatus() {
            return status;
        }
        
        public void setStatus(HealthStatus status) {
            this.status = status;
        }
        
        public int getTotalPolicies() {
            return totalPolicies;
        }
        
        public void setTotalPolicies(int totalPolicies) {
            this.totalPolicies = totalPolicies;
        }
        
        public int getActivePolicies() {
            return activePolicies;
        }
        
        public void setActivePolicies(int activePolicies) {
            this.activePolicies = activePolicies;
        }
        
        public long getLastSyncTimestamp() {
            return lastSyncTimestamp;
        }
        
        public void setLastSyncTimestamp(long lastSyncTimestamp) {
            this.lastSyncTimestamp = lastSyncTimestamp;
        }
    }
    
    /**
     * Policy activity class.
     */
    class PolicyActivity {
        private String policyKey;
        private String policyName;
        private String activityType;
        private String user;
        private long timestamp;
        private String details;
        
        // getters and setters
        public String getPolicyKey() {
            return policyKey;
        }
        
        public void setPolicyKey(String policyKey) {
            this.policyKey = policyKey;
        }
        
        public String getPolicyName() {
            return policyName;
        }
        
        public void setPolicyName(String policyName) {
            this.policyName = policyName;
        }
        
        public String getActivityType() {
            return activityType;
        }
        
        public void setActivityType(String activityType) {
            this.activityType = activityType;
        }
        
        public String getUser() {
            return user;
        }
        
        public void setUser(String user) {
            this.user = user;
        }
        
        public long getTimestamp() {
            return timestamp;
        }
        
        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
        
        public String getDetails() {
            return details;
        }
        
        public void setDetails(String details) {
            this.details = details;
        }
    }
    
    /**
     * Sync operation class.
     */
    class SyncOperation {
        private String id;
        private String type;
        private String status;
        private String source;
        private String target;
        private long startTimestamp;
        private Integer progress;
        private String details;
        
        // getters and setters
        public String getId() {
            return id;
        }
        
        public void setId(String id) {
            this.id = id;
        }
        
        public String getType() {
            return type;
        }
        
        public void setType(String type) {
            this.type = type;
        }
        
        public String getStatus() {
            return status;
        }
        
        public void setStatus(String status) {
            this.status = status;
        }
        
        public String getSource() {
            return source;
        }
        
        public void setSource(String source) {
            this.source = source;
        }
        
        public String getTarget() {
            return target;
        }
        
        public void setTarget(String target) {
            this.target = target;
        }
        
        public long getStartTimestamp() {
            return startTimestamp;
        }
        
        public void setStartTimestamp(long startTimestamp) {
            this.startTimestamp = startTimestamp;
        }
        
        public Integer getProgress() {
            return progress;
        }
        
        public void setProgress(Integer progress) {
            this.progress = progress;
        }
        
        public String getDetails() {
            return details;
        }
        
        public void setDetails(String details) {
            this.details = details;
        }
    }
    
    /**
     * System alert class.
     */
    class SystemAlert {
        private String id;
        private String type;
        private String severity;
        private String message;
        private long timestamp;
        private boolean acknowledged;
        private String component;
        
        // getters and setters
        public String getId() {
            return id;
        }
        
        public void setId(String id) {
            this.id = id;
        }
        
        public String getType() {
            return type;
        }
        
        public void setType(String type) {
            this.type = type;
        }
        
        public String getSeverity() {
            return severity;
        }
        
        public void setSeverity(String severity) {
            this.severity = severity;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
        
        public long getTimestamp() {
            return timestamp;
        }
        
        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
        
        public boolean isAcknowledged() {
            return acknowledged;
        }
        
        public void setAcknowledged(boolean acknowledged) {
            this.acknowledged = acknowledged;
        }
        
        public String getComponent() {
            return component;
        }
        
        public void setComponent(String component) {
            this.component = component;
        }
    }
}
