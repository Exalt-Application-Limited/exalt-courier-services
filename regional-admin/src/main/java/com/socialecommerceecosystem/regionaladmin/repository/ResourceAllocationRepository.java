package com.exalt.courier.regionaladmin.repository;

import com.socialecommerceecosystem.regionaladmin.model.ResourceAllocation;
import com.socialecommerceecosystem.regionaladmin.model.AllocationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing ResourceAllocation entities.
 */
@Repository
public interface ResourceAllocationRepository extends JpaRepository<ResourceAllocation, Long> {
    
    /**
     * Find a resource allocation by its allocationId.
     * 
     * @param allocationId The allocation ID from HQ
     * @return Optional containing the allocation if found
     */
    Optional<ResourceAllocation> findByAllocationId(Long allocationId);
    
    /**
     * Find allocations by their resource type.
     * 
     * @param resourceType The resource type
     * @return List of allocations of the specified resource type
     */
    List<ResourceAllocation> findByResourceType(String resourceType);
    
    /**
     * Find allocations by their resource pool ID.
     * 
     * @param resourcePoolId The resource pool ID
     * @return List of allocations from the specified resource pool
     */
    List<ResourceAllocation> findByResourcePoolId(Long resourcePoolId);
    
    /**
     * Find active allocations that are currently effective.
     * An allocation is considered effective if its status is ACTIVE and the current time
     * is between the effective date range (or the dates are null).
     * 
     * @param status The allocation status (typically ACTIVE)
     * @param currentTime The current time to check against
     * @return List of active and effective allocations
     */
    @Query("SELECT a FROM ResourceAllocation a WHERE a.status = :status " +
           "AND (a.effectiveFrom IS NULL OR a.effectiveFrom <= :currentTime) " +
           "AND (a.effectiveTo IS NULL OR a.effectiveTo >= :currentTime)")
    List<ResourceAllocation> findEffectiveAllocations(@Param("status") AllocationStatus status, 
                                                     @Param("currentTime") LocalDateTime currentTime);
    
    /**
     * Find active allocations of a specific resource type that are currently effective.
     * 
     * @param resourceType The resource type
     * @param status The allocation status (typically ACTIVE)
     * @param currentTime The current time to check against
     * @return List of matching allocations
     */
    @Query("SELECT a FROM ResourceAllocation a WHERE a.resourceType = :resourceType " +
           "AND a.status = :status " +
           "AND (a.effectiveFrom IS NULL OR a.effectiveFrom <= :currentTime) " +
           "AND (a.effectiveTo IS NULL OR a.effectiveTo >= :currentTime)")
    List<ResourceAllocation> findEffectiveAllocationsByType(@Param("resourceType") String resourceType, 
                                                          @Param("status") AllocationStatus status, 
                                                          @Param("currentTime") LocalDateTime currentTime);
                                                          
    /**
     * Calculate the total allocated quantity for a specific resource type that is currently effective.
     * 
     * @param resourceType The resource type
     * @param status The allocation status (typically ACTIVE)
     * @param currentTime The current time to check against
     * @return The total allocated quantity
     */
    @Query("SELECT SUM(a.quantity) FROM ResourceAllocation a WHERE a.resourceType = :resourceType " +
           "AND a.status = :status " +
           "AND (a.effectiveFrom IS NULL OR a.effectiveFrom <= :currentTime) " +
           "AND (a.effectiveTo IS NULL OR a.effectiveTo >= :currentTime)")
    Integer sumEffectiveAllocationQuantityByType(@Param("resourceType") String resourceType, 
                                               @Param("status") AllocationStatus status, 
                                               @Param("currentTime") LocalDateTime currentTime);
}
