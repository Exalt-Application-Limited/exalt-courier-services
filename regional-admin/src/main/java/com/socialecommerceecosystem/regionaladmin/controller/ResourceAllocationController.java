package com.gogidix.courier.regionaladmin.controller;

import com.socialecommerceecosystem.regionaladmin.model.ResourceAllocation;
import com.socialecommerceecosystem.regionaladmin.model.AllocationStatus;
import com.socialecommerceecosystem.regionaladmin.repository.ResourceAllocationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST controller for managing resource allocations in the Regional Admin application.
 */
@RestController
@RequestMapping("/api/allocations")
public class ResourceAllocationController {

    private static final Logger logger = LoggerFactory.getLogger(ResourceAllocationController.class);

    @Autowired
    private ResourceAllocationRepository resourceAllocationRepository;

    /**
     * Get all resource allocations.
     * 
     * @return List of all resource allocations
     */
    @GetMapping
    public ResponseEntity<List<ResourceAllocation>> getAllAllocations() {
        logger.info("Getting all resource allocations");
        List<ResourceAllocation> allocations = resourceAllocationRepository.findAll();
        return ResponseEntity.ok(allocations);
    }

    /**
     * Get a resource allocation by ID.
     * 
     * @param id The allocation ID
     * @return The allocation if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResourceAllocation> getAllocationById(@PathVariable Long id) {
        logger.info("Getting resource allocation with ID: {}", id);
        Optional<ResourceAllocation> allocation = resourceAllocationRepository.findById(id);
        return allocation.map(ResponseEntity::ok)
                       .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get allocations by resource type.
     * 
     * @param type The resource type
     * @return List of allocations of the specified resource type
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<ResourceAllocation>> getAllocationsByType(@PathVariable String type) {
        logger.info("Getting allocations of type: {}", type);
        List<ResourceAllocation> allocations = resourceAllocationRepository.findByResourceType(type);
        return ResponseEntity.ok(allocations);
    }

    /**
     * Get active allocations that are currently effective.
     * 
     * @return List of active and effective allocations
     */
    @GetMapping("/effective")
    public ResponseEntity<List<ResourceAllocation>> getEffectiveAllocations() {
        logger.info("Getting effective allocations");
        List<ResourceAllocation> allocations = resourceAllocationRepository.findEffectiveAllocations(
                AllocationStatus.ACTIVE, LocalDateTime.now());
        return ResponseEntity.ok(allocations);
    }

    /**
     * Get effective allocations of a specific resource type.
     * 
     * @param type The resource type
     * @return List of active and effective allocations of the specified type
     */
    @GetMapping("/effective/type/{type}")
    public ResponseEntity<List<ResourceAllocation>> getEffectiveAllocationsByType(@PathVariable String type) {
        logger.info("Getting effective allocations of type: {}", type);
        List<ResourceAllocation> allocations = resourceAllocationRepository.findEffectiveAllocationsByType(
                type, AllocationStatus.ACTIVE, LocalDateTime.now());
        return ResponseEntity.ok(allocations);
    }

    /**
     * Get allocation statistics including total quantities by resource type.
     * 
     * @return Map of resource type to total quantity
     */
    @GetMapping("/stats")
    public ResponseEntity<String> getAllocationStats() {
        logger.info("Getting allocation statistics");
        
        // Count allocations by status
        long totalAllocations = resourceAllocationRepository.count();
        long activeAllocations = resourceAllocationRepository.findAll().stream()
                                                        .filter(a -> a.getStatus() == AllocationStatus.ACTIVE)
                                                        .count();
        long inactiveAllocations = resourceAllocationRepository.findAll().stream()
                                                          .filter(a -> a.getStatus() == AllocationStatus.INACTIVE)
                                                          .count();
        
        // Sum quantities by resource type for active allocations
        Map<String, Integer> quantityByType = resourceAllocationRepository.findAll().stream()
                                                                      .filter(a -> a.getStatus() == AllocationStatus.ACTIVE)
                                                                      .collect(Collectors.groupingBy(
                                                                          ResourceAllocation::getResourceType,
                                                                          Collectors.summingInt(ResourceAllocation::getQuantity)
                                                                      ));
        
        StringBuilder stats = new StringBuilder();
        stats.append(String.format("Total Allocations: %d\nActive: %d\nInactive: %d\n\n", 
                                totalAllocations, activeAllocations, inactiveAllocations));
        
        stats.append("Allocated Quantities by Resource Type:\n");
        quantityByType.forEach((type, quantity) -> 
            stats.append(String.format("- %s: %d\n", type, quantity))
        );
        
        return ResponseEntity.ok(stats.toString());
    }
}
