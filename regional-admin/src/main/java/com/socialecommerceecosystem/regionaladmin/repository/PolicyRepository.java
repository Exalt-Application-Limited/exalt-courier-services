package com.gogidix.courier.regionaladmin.repository;

import com.socialecommerceecosystem.regionaladmin.model.Policy;
import com.socialecommerceecosystem.regionaladmin.model.PolicyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing Policy entities.
 */
@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {
    
    /**
     * Find a policy by its policyId.
     * 
     * @param policyId The policy ID from HQ
     * @return Optional containing the policy if found
     */
    Optional<Policy> findByPolicyId(Long policyId);
    
    /**
     * Find policies by their type.
     * 
     * @param policyType The policy type
     * @return List of policies of the specified type
     */
    List<Policy> findByPolicyType(String policyType);
    
    /**
     * Find active policies that are currently effective.
     * A policy is considered effective if its status is ACTIVE and the current time
     * is between the effective date range (or the dates are null).
     * 
     * @param currentTime The current time to check against
     * @return List of active and effective policies
     */
    @Query("SELECT p FROM Policy p WHERE p.status = :status " +
           "AND (p.effectiveFrom IS NULL OR p.effectiveFrom <= :currentTime) " +
           "AND (p.effectiveTo IS NULL OR p.effectiveTo >= :currentTime)")
    List<Policy> findEffectivePolicies(@Param("status") PolicyStatus status, @Param("currentTime") LocalDateTime currentTime);
    
    /**
     * Find active policies of a specific type that are currently effective.
     * 
     * @param policyType The policy type
     * @param status The policy status (typically ACTIVE)
     * @param currentTime The current time to check against
     * @return List of matching policies
     */
    @Query("SELECT p FROM Policy p WHERE p.policyType = :policyType " +
           "AND p.status = :status " +
           "AND (p.effectiveFrom IS NULL OR p.effectiveFrom <= :currentTime) " +
           "AND (p.effectiveTo IS NULL OR p.effectiveTo >= :currentTime) " +
           "ORDER BY p.priority DESC")
    List<Policy> findEffectivePoliciesByType(@Param("policyType") String policyType, 
                                            @Param("status") PolicyStatus status, 
                                            @Param("currentTime") LocalDateTime currentTime);
}
