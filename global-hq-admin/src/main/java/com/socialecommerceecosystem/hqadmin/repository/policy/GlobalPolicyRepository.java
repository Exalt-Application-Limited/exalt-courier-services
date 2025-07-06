package com.gogidix.courier.courier.hqadmin.repository.policy;

import com.socialecommerceecosystem.hqadmin.model.GlobalRegion;
import com.socialecommerceecosystem.hqadmin.model.policy.ApprovalStatus;
import com.socialecommerceecosystem.hqadmin.model.policy.GlobalPolicy;
import com.socialecommerceecosystem.hqadmin.model.policy.PolicyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing GlobalPolicy entities.
 */
@Repository
public interface GlobalPolicyRepository extends JpaRepository<GlobalPolicy, Long> {

    /**
     * Find policy by its unique key.
     */
    Optional<GlobalPolicy> findByPolicyKey(String policyKey);
    
    /**
     * Find all active policies.
     */
    List<GlobalPolicy> findByIsActiveTrue();
    
    /**
     * Find all mandatory policies.
     */
    List<GlobalPolicy> findByIsMandatoryTrue();
    
    /**
     * Find policies by type.
     */
    List<GlobalPolicy> findByPolicyType(PolicyType policyType);
    
    /**
     * Find policies by approval status.
     */
    List<GlobalPolicy> findByApprovalStatus(ApprovalStatus approvalStatus);
    
    /**
     * Find policies for a specific region.
     */
    List<GlobalPolicy> findByGlobalRegion(GlobalRegion globalRegion);
    
    /**
     * Find policies with null region (global policies).
     */
    List<GlobalPolicy> findByGlobalRegionIsNull();
    
    /**
     * Find policies by region and type.
     */
    List<GlobalPolicy> findByGlobalRegionAndPolicyType(GlobalRegion globalRegion, PolicyType policyType);
    
    /**
     * Find currently effective policies (based on effective and expiration dates).
     */
    @Query("SELECT p FROM GlobalPolicy p WHERE p.isActive = true AND " +
           "(p.effectiveDate IS NULL OR p.effectiveDate <= :currentDateTime) AND " +
           "(p.expirationDate IS NULL OR p.expirationDate > :currentDateTime)")
    List<GlobalPolicy> findEffectivePolicies(LocalDateTime currentDateTime);
    
    /**
     * Find policies that will be effective in the future.
     */
    @Query("SELECT p FROM GlobalPolicy p WHERE p.isActive = true AND p.effectiveDate > :currentDateTime")
    List<GlobalPolicy> findFuturePolicies(LocalDateTime currentDateTime);
    
    /**
     * Find expired policies.
     */
    @Query("SELECT p FROM GlobalPolicy p WHERE p.expirationDate < :currentDateTime")
    List<GlobalPolicy> findExpiredPolicies(LocalDateTime currentDateTime);
    
    /**
     * Search policies by name (case-insensitive).
     */
    List<GlobalPolicy> findByNameContainingIgnoreCase(String searchText);
    
    /**
     * Search policies by content (case-insensitive).
     */
    @Query("SELECT p FROM GlobalPolicy p WHERE LOWER(p.policyContent) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    List<GlobalPolicy> findByPolicyContentContainingIgnoreCase(String searchText);
    
    /**
     * Count policies by type.
     */
    @Query("SELECT p.policyType, COUNT(p) FROM GlobalPolicy p GROUP BY p.policyType")
    List<Object[]> countPoliciesByType();
}
