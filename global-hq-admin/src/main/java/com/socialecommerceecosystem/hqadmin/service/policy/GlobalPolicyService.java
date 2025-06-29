package com.exalt.courier.hqadmin.service.policy;

import com.socialecommerceecosystem.hqadmin.model.policy.ApprovalStatus;
import com.socialecommerceecosystem.hqadmin.model.policy.GlobalPolicy;
import com.socialecommerceecosystem.hqadmin.model.policy.PolicyType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service interface for managing global policies.
 */
public interface GlobalPolicyService {

    /**
     * Get all policies.
     */
    List<GlobalPolicy> getAllPolicies();
    
    /**
     * Get a policy by ID.
     */
    Optional<GlobalPolicy> getPolicyById(Long id);
    
    /**
     * Get a policy by key.
     */
    Optional<GlobalPolicy> getPolicyByKey(String policyKey);
    
    /**
     * Create a new policy.
     */
    GlobalPolicy createPolicy(GlobalPolicy policy);
    
    /**
     * Update an existing policy.
     */
    GlobalPolicy updatePolicy(Long id, GlobalPolicy policyDetails);
    
    /**
     * Delete a policy.
     */
    void deletePolicy(Long id);
    
    /**
     * Get all active policies.
     */
    List<GlobalPolicy> getAllActivePolicies();
    
    /**
     * Get all policies of a specific type.
     */
    List<GlobalPolicy> getPoliciesByType(PolicyType policyType);
    
    /**
     * Get all policies for a specific region.
     */
    List<GlobalPolicy> getPoliciesByRegion(Long regionId);
    
    /**
     * Get all policies for a specific region and type.
     */
    List<GlobalPolicy> getPoliciesByRegionAndType(Long regionId, PolicyType policyType);
    
    /**
     * Get all global policies (not associated with any region).
     */
    List<GlobalPolicy> getGlobalPolicies();
    
    /**
     * Get all mandatory policies.
     */
    List<GlobalPolicy> getMandatoryPolicies();
    
    /**
     * Get all policies by approval status.
     */
    List<GlobalPolicy> getPoliciesByApprovalStatus(ApprovalStatus approvalStatus);
    
    /**
     * Search policies by name.
     */
    List<GlobalPolicy> searchPoliciesByName(String searchText);
    
    /**
     * Search policies by content.
     */
    List<GlobalPolicy> searchPoliciesByContent(String searchText);
    
    /**
     * Get currently effective policies.
     */
    List<GlobalPolicy> getEffectivePolicies();
    
    /**
     * Get policies that will be effective in the future.
     */
    List<GlobalPolicy> getFuturePolicies();
    
    /**
     * Get expired policies.
     */
    List<GlobalPolicy> getExpiredPolicies();
    
    /**
     * Count policies by type.
     */
    Map<PolicyType, Long> countPoliciesByType();
    
    /**
     * Approve a policy.
     */
    GlobalPolicy approvePolicy(Long id, String approvedBy);
    
    /**
     * Reject a policy.
     */
    GlobalPolicy rejectPolicy(Long id, String rejectedBy, String rejectionReason);
    
    /**
     * Submit a policy for approval.
     */
    GlobalPolicy submitPolicyForApproval(Long id);
    
    /**
     * Create a new version of an existing policy.
     */
    GlobalPolicy createNewPolicyVersion(Long existingPolicyId, GlobalPolicy newPolicyDetails);
    
    /**
     * Apply a global policy to a specific region.
     */
    GlobalPolicy applyGlobalPolicyToRegion(String policyKey, Long regionId);
    
    /**
     * Apply multiple global policies to a specific region.
     */
    List<GlobalPolicy> applyGlobalPoliciesToRegion(List<String> policyKeys, Long regionId);
}
