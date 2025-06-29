package com.exalt.courier.regionaladmin.service;

import com.socialecommerceecosystem.regionaladmin.model.ResourceAllocation;
import com.socialecommerceecosystem.regionaladmin.model.AllocationStatus;
import com.socialecommerceecosystem.regionaladmin.repository.ResourceAllocationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service for synchronizing resource allocation data from HQ Admin.
 * Handles resource allocation events and updates the local allocation database.
 */
@Service
public class ResourceAllocationSyncService {

    private static final Logger logger = LoggerFactory.getLogger(ResourceAllocationSyncService.class);

    @Autowired
    private ResourceAllocationRepository resourceAllocationRepository;

    /**
     * Handles a resource allocation created event from Kafka.
     * 
     * @param allocationData The allocation event data
     */
    @Transactional
    public void handleAllocationCreated(Map<String, Object> allocationData) {
        logger.info("Processing resource allocation created event");
        
        Long allocationId = Long.valueOf(allocationData.get("id").toString());
        Long resourcePoolId = Long.valueOf(allocationData.get("resourcePoolId").toString());
        String resourceType = allocationData.get("resourceType").toString();
        Integer quantity = Integer.valueOf(allocationData.get("quantity").toString());
        
        // Check if the allocation already exists (shouldn't normally happen)
        Optional<ResourceAllocation> existingAllocation = resourceAllocationRepository.findByAllocationId(allocationId);
        if (existingAllocation.isPresent()) {
            logger.warn("Resource allocation already exists with ID: {}. Updating instead of creating.", allocationId);
            updateExistingAllocation(existingAllocation.get(), allocationData);
            return;
        }
        
        // Create and save a new allocation
        ResourceAllocation allocation = new ResourceAllocation();
        allocation.setAllocationId(allocationId);
        allocation.setResourcePoolId(resourcePoolId);
        allocation.setResourceType(resourceType);
        allocation.setQuantity(quantity);
        
        // Set optional fields if present
        if (allocationData.containsKey("allocatedAt")) {
            allocation.setAllocatedAt(parseDateTime(allocationData.get("allocatedAt").toString()));
        } else {
            allocation.setAllocatedAt(LocalDateTime.now());
        }
        
        if (allocationData.containsKey("effectiveFrom")) {
            allocation.setEffectiveFrom(parseDateTime(allocationData.get("effectiveFrom").toString()));
        }
        
        if (allocationData.containsKey("effectiveTo")) {
            allocation.setEffectiveTo(parseDateTime(allocationData.get("effectiveTo").toString()));
        }
        
        if (allocationData.containsKey("notes")) {
            allocation.setNotes(allocationData.get("notes").toString());
        }
        
        if (allocationData.containsKey("status")) {
            allocation.setStatus(AllocationStatus.valueOf(allocationData.get("status").toString()));
        } else {
            allocation.setStatus(AllocationStatus.PENDING);
        }
        
        // Save the allocation
        allocation = resourceAllocationRepository.save(allocation);
        logger.info("Created new resource allocation: {}", allocation);
    }

    /**
     * Handles a resource allocation updated event from Kafka.
     * 
     * @param allocationData The allocation event data
     */
    @Transactional
    public void handleAllocationUpdated(Map<String, Object> allocationData) {
        logger.info("Processing resource allocation updated event");
        
        Long allocationId = Long.valueOf(allocationData.get("id").toString());
        
        // Find the allocation
        Optional<ResourceAllocation> optionalAllocation = resourceAllocationRepository.findByAllocationId(allocationId);
        if (!optionalAllocation.isPresent()) {
            logger.warn("Cannot update resource allocation with ID: {}. Allocation not found. Creating instead.", allocationId);
            handleAllocationCreated(allocationData);
            return;
        }
        
        // Update the allocation
        ResourceAllocation allocation = optionalAllocation.get();
        updateExistingAllocation(allocation, allocationData);
    }

    /**
     * Handles a resource allocation activated event from Kafka.
     * 
     * @param allocationData The allocation event data
     */
    @Transactional
    public void handleAllocationActivated(Map<String, Object> allocationData) {
        logger.info("Processing resource allocation activated event");
        
        Long allocationId = Long.valueOf(allocationData.get("id").toString());
        
        // Find the allocation
        Optional<ResourceAllocation> optionalAllocation = resourceAllocationRepository.findByAllocationId(allocationId);
        if (!optionalAllocation.isPresent()) {
            logger.warn("Cannot activate resource allocation with ID: {}. Allocation not found.", allocationId);
            return;
        }
        
        // Activate the allocation
        ResourceAllocation allocation = optionalAllocation.get();
        allocation.setStatus(AllocationStatus.ACTIVE);
        allocation = resourceAllocationRepository.save(allocation);
        logger.info("Activated resource allocation with ID: {}", allocationId);
    }

    /**
     * Handles a resource allocation deactivated event from Kafka.
     * 
     * @param allocationData The allocation event data
     */
    @Transactional
    public void handleAllocationDeactivated(Map<String, Object> allocationData) {
        logger.info("Processing resource allocation deactivated event");
        
        Long allocationId = Long.valueOf(allocationData.get("id").toString());
        
        // Find the allocation
        Optional<ResourceAllocation> optionalAllocation = resourceAllocationRepository.findByAllocationId(allocationId);
        if (!optionalAllocation.isPresent()) {
            logger.warn("Cannot deactivate resource allocation with ID: {}. Allocation not found.", allocationId);
            return;
        }
        
        // Deactivate the allocation
        ResourceAllocation allocation = optionalAllocation.get();
        allocation.setStatus(AllocationStatus.INACTIVE);
        allocation = resourceAllocationRepository.save(allocation);
        logger.info("Deactivated resource allocation with ID: {}", allocationId);
    }

    /**
     * Handles a resource allocation expired event from Kafka.
     * 
     * @param allocationData The allocation event data
     */
    @Transactional
    public void handleAllocationExpired(Map<String, Object> allocationData) {
        logger.info("Processing resource allocation expired event");
        
        Long allocationId = Long.valueOf(allocationData.get("id").toString());
        
        // Find the allocation
        Optional<ResourceAllocation> optionalAllocation = resourceAllocationRepository.findByAllocationId(allocationId);
        if (!optionalAllocation.isPresent()) {
            logger.warn("Cannot expire resource allocation with ID: {}. Allocation not found.", allocationId);
            return;
        }
        
        // Expire the allocation
        ResourceAllocation allocation = optionalAllocation.get();
        allocation.setStatus(AllocationStatus.EXPIRED);
        allocation = resourceAllocationRepository.save(allocation);
        logger.info("Expired resource allocation with ID: {}", allocationId);
    }

    /**
     * Handles a resource allocation plan executed event from Kafka.
     * This event indicates that multiple allocations were created or updated as part of a plan.
     * 
     * @param planData The allocation plan event data
     */
    @Transactional
    public void handleAllocationPlanExecuted(Map<String, Object> planData) {
        logger.info("Processing resource allocation plan executed event");
        
        // Get the plan details
        Long planId = Long.valueOf(planData.get("planId").toString());
        String planName = planData.get("planName").toString();
        logger.info("Processing allocation plan: {} (ID: {})", planName, planId);
        
        // Check if the plan data contains allocations
        if (!planData.containsKey("allocations")) {
            logger.warn("Allocation plan data does not contain allocations. Nothing to process.");
            return;
        }
        
        // Get the allocations from the plan
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> allocations = (List<Map<String, Object>>) planData.get("allocations");
        
        // Process each allocation
        logger.info("Processing {} allocations from plan", allocations.size());
        for (Map<String, Object> allocationData : allocations) {
            try {
                handleAllocationCreated(allocationData);
            } catch (Exception e) {
                logger.error("Error processing allocation from plan: {}", e.getMessage(), e);
                // Continue with the next allocation
            }
        }
        
        logger.info("Completed processing allocation plan with ID: {}", planId);
    }

    /**
     * Helper method to update an existing allocation.
     * 
     * @param allocation The allocation to update
     * @param allocationData The allocation data
     * @return The updated allocation
     */
    private ResourceAllocation updateExistingAllocation(ResourceAllocation allocation, Map<String, Object> allocationData) {
        // Update fields if present in the data
        if (allocationData.containsKey("resourcePoolId")) {
            allocation.setResourcePoolId(Long.valueOf(allocationData.get("resourcePoolId").toString()));
        }
        
        if (allocationData.containsKey("resourceType")) {
            allocation.setResourceType(allocationData.get("resourceType").toString());
        }
        
        if (allocationData.containsKey("quantity")) {
            allocation.setQuantity(Integer.valueOf(allocationData.get("quantity").toString()));
        }
        
        if (allocationData.containsKey("effectiveFrom")) {
            allocation.setEffectiveFrom(parseDateTime(allocationData.get("effectiveFrom").toString()));
        }
        
        if (allocationData.containsKey("effectiveTo")) {
            allocation.setEffectiveTo(parseDateTime(allocationData.get("effectiveTo").toString()));
        }
        
        if (allocationData.containsKey("notes")) {
            allocation.setNotes(allocationData.get("notes").toString());
        }
        
        if (allocationData.containsKey("status")) {
            allocation.setStatus(AllocationStatus.valueOf(allocationData.get("status").toString()));
        }
        
        // Save the allocation
        allocation = resourceAllocationRepository.save(allocation);
        logger.info("Updated resource allocation: {}", allocation);
        return allocation;
    }

    /**
     * Helper method to parse a datetime string.
     * 
     * @param dateTimeStr The datetime string
     * @return The LocalDateTime
     */
    private LocalDateTime parseDateTime(String dateTimeStr) {
        try {
            return LocalDateTime.parse(dateTimeStr);
        } catch (Exception e) {
            logger.warn("Failed to parse datetime string: {}", dateTimeStr, e);
            return null;
        }
    }
}
