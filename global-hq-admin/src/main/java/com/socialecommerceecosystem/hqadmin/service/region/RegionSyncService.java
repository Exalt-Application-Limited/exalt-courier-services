package com.gogidix.courier.courier.hqadmin.service.region;

import com.socialecommerceecosystem.hqadmin.model.policy.GlobalPolicy;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service interface for multi-region policy synchronization.
 */
public interface RegionSyncService {

    /**
     * Push a specific global policy to all active regions.
     * 
     * @param policyKey The policy key to synchronize
     * @return Map of region codes to success/failure status
     */
    Map<String, Boolean> pushPolicyToAllRegions(String policyKey);
    
    /**
     * Push a policy to a specific region.
     * 
     * @param policyKey The policy key to synchronize
     * @param regionCode The region code to push to
     * @return True if successful, false otherwise
     */
    boolean pushPolicyToRegion(String policyKey, String regionCode);
    
    /**
     * Push multiple policies to a specific region.
     * 
     * @param policyKeys List of policy keys to synchronize
     * @param regionCode The region code to push to
     * @return Map of policy keys to success/failure status
     */
    Map<String, Boolean> pushPoliciesToRegion(List<String> policyKeys, String regionCode);
    
    /**
     * Pull a regional policy override from a specific region.
     * 
     * @param policyKey The policy key to retrieve
     * @param regionCode The region code to pull from
     * @return The policy if found
     */
    Optional<GlobalPolicy> pullPolicyFromRegion(String policyKey, String regionCode);
    
    /**
     * Pull all policy overrides from a specific region.
     * 
     * @param regionCode The region code to pull from
     * @return List of regional policies
     */
    List<GlobalPolicy> pullAllPoliciesFromRegion(String regionCode);
    
    /**
     * Check the health of all regional connections.
     * 
     * @return Map of region codes to health status
     */
    Map<String, Boolean> checkRegionalHealth();
    
    /**
     * Get detailed status of all regions including last sync time.
     * 
     * @return Map of region codes to status information
     */
    Map<String, RegionStatus> getRegionalStatus();
    
    /**
     * Force a full synchronization of all global policies to all regions.
     * 
     * @return Map of region codes to sync status
     */
    Map<String, SyncResult> forceSyncAllRegions();
    
    /**
     * Sync policies for a specific policy type to all regions.
     * 
     * @param policyType The policy type to synchronize
     * @return Map of region codes to sync status
     */
    Map<String, SyncResult> syncPolicyTypeToAllRegions(String policyType);
    
    /**
     * Status class for region information.
     */
    class RegionStatus {
        private String regionCode;
        private String regionName;
        private boolean isActive;
        private boolean isHealthy;
        private long lastSyncTimestamp;
        private int pendingSyncItems;
        private String lastError;
        
        // getters and setters
    }
    
    /**
     * Result class for synchronization operations.
     */
    class SyncResult {
        private boolean success;
        private int totalPolicies;
        private int successfulPolicies;
        private int failedPolicies;
        private List<String> errors;
        
        // getters and setters
    }
}
