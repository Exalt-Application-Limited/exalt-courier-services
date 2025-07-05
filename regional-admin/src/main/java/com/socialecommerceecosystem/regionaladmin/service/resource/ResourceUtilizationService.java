package com.gogidix.courier.regionaladmin.service.resource;

import com.socialecommerceecosystem.regionaladmin.model.AllocationStatus;
import com.socialecommerceecosystem.regionaladmin.model.ResourceAllocation;
import com.socialecommerceecosystem.regionaladmin.repository.ResourceAllocationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for tracking and analyzing resource utilization.
 * Provides metrics on resource allocation and usage efficiency.
 */
@Service
public class ResourceUtilizationService {

    private static final Logger logger = LoggerFactory.getLogger(ResourceUtilizationService.class);

    @Autowired
    private ResourceAllocationRepository resourceAllocationRepository;

    /**
     * Get current resource allocation summary by resource type.
     * 
     * @return Map of resource types to their allocation statistics
     */
    public Map<String, Map<String, Object>> getResourceAllocationSummary() {
        logger.info("Generating resource allocation summary");
        
        // Get all active allocations
        List<ResourceAllocation> activeAllocations = resourceAllocationRepository.findEffectiveAllocations(
                AllocationStatus.ACTIVE, LocalDateTime.now());
        
        // Group allocations by resource type
        Map<String, List<ResourceAllocation>> allocationsByType = activeAllocations.stream()
                .collect(Collectors.groupingBy(ResourceAllocation::getResourceType));
        
        // Calculate statistics for each resource type
        Map<String, Map<String, Object>> summaryByType = new HashMap<>();
        
        allocationsByType.forEach((type, allocations) -> {
            Map<String, Object> stats = new HashMap<>();
            
            // Calculate total allocated quantity
            int totalQuantity = allocations.stream()
                    .mapToInt(ResourceAllocation::getQuantity)
                    .sum();
            
            // Get count of source resource pools
            long resourcePoolCount = allocations.stream()
                    .map(ResourceAllocation::getResourcePoolId)
                    .distinct()
                    .count();
            
            // Get allocations with expiry dates
            long withExpiryDate = allocations.stream()
                    .filter(a -> a.getEffectiveTo() != null)
                    .count();
            
            // Calculate average allocation size
            double avgAllocationSize = allocations.stream()
                    .mapToInt(ResourceAllocation::getQuantity)
                    .average()
                    .orElse(0);
            
            stats.put("allocationCount", allocations.size());
            stats.put("totalQuantity", totalQuantity);
            stats.put("resourcePoolCount", resourcePoolCount);
            stats.put("withExpiryDate", withExpiryDate);
            stats.put("withoutExpiryDate", allocations.size() - withExpiryDate);
            stats.put("avgAllocationSize", avgAllocationSize);
            
            summaryByType.put(type, stats);
        });
        
        return summaryByType;
    }
    
    /**
     * Calculate resource utilization metrics.
     * 
     * @return Map containing resource utilization metrics
     */
    public Map<String, Object> calculateUtilizationMetrics() {
        logger.info("Calculating resource utilization metrics");
        
        Map<String, Object> metrics = new HashMap<>();
        
        // Get all active allocations
        List<ResourceAllocation> activeAllocations = resourceAllocationRepository.findEffectiveAllocations(
                AllocationStatus.ACTIVE, LocalDateTime.now());
        
        // Calculate total allocated resources
        int totalAllocated = activeAllocations.stream()
                .mapToInt(ResourceAllocation::getQuantity)
                .sum();
        
        // Calculate allocation efficiency (ratio of allocated to total available)
        // In a real implementation, this would compare to known capacity/limits
        double allocationEfficiency = 0.85; // Simulated value
        
        // Calculate resource diversity (number of different resource types)
        long resourceTypeCount = activeAllocations.stream()
                .map(ResourceAllocation::getResourceType)
                .distinct()
                .count();
        
        // Calculate expiring soon count (allocations expiring in the next 30 days)
        LocalDateTime thirtyDaysFromNow = LocalDateTime.now().plusDays(30);
        long expiringSoon = activeAllocations.stream()
                .filter(a -> a.getEffectiveTo() != null)
                .filter(a -> a.getEffectiveTo().isBefore(thirtyDaysFromNow))
                .count();
        
        // Save metrics
        metrics.put("totalAllocatedResources", totalAllocated);
        metrics.put("allocationEfficiency", allocationEfficiency);
        metrics.put("resourceTypeCount", resourceTypeCount);
        metrics.put("expiringSoonCount", expiringSoon);
        
        // Add resource types breakdown
        Map<String, Integer> quantityByType = activeAllocations.stream()
                .collect(Collectors.groupingBy(
                    ResourceAllocation::getResourceType,
                    Collectors.summingInt(ResourceAllocation::getQuantity)
                ));
        
        metrics.put("allocationByResourceType", quantityByType);
        
        return metrics;
    }
    
    /**
     * Identify potential resource allocation issues.
     * 
     * @return List of resource allocation issues
     */
    public List<Map<String, Object>> identifyAllocationIssues() {
        logger.info("Identifying resource allocation issues");
        
        List<Map<String, Object>> issues = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        // Get all allocations
        List<ResourceAllocation> allAllocations = resourceAllocationRepository.findAll();
        
        // Check for expired but active allocations
        List<ResourceAllocation> expiredAllocations = allAllocations.stream()
                .filter(a -> a.getStatus() == AllocationStatus.ACTIVE)
                .filter(a -> a.getEffectiveTo() != null && a.getEffectiveTo().isBefore(now))
                .collect(Collectors.toList());
        
        for (ResourceAllocation allocation : expiredAllocations) {
            Map<String, Object> issue = new HashMap<>();
            issue.put("allocationId", allocation.getAllocationId());
            issue.put("resourceType", allocation.getResourceType());
            issue.put("issueType", "EXPIRED_BUT_ACTIVE");
            issue.put("description", "Resource allocation has expired on " + allocation.getEffectiveTo() + " but is still active");
            issue.put("remediation", "Deactivate the allocation or extend its effective date");
            issues.add(issue);
        }
        
        // Check for zero-quantity allocations
        List<ResourceAllocation> zeroQuantityAllocations = allAllocations.stream()
                .filter(a -> a.getStatus() == AllocationStatus.ACTIVE)
                .filter(a -> a.getQuantity() == 0)
                .collect(Collectors.toList());
        
        for (ResourceAllocation allocation : zeroQuantityAllocations) {
            Map<String, Object> issue = new HashMap<>();
            issue.put("allocationId", allocation.getAllocationId());
            issue.put("resourceType", allocation.getResourceType());
            issue.put("issueType", "ZERO_QUANTITY");
            issue.put("description", "Resource allocation has zero quantity but is active");
            issue.put("remediation", "Deactivate the allocation or update the quantity");
            issues.add(issue);
        }
        
        // Check for duplicate allocations (same resource pool and type)
        Map<String, List<ResourceAllocation>> byPoolAndType = allAllocations.stream()
                .filter(a -> a.getStatus() == AllocationStatus.ACTIVE)
                .collect(Collectors.groupingBy(
                    a -> a.getResourcePoolId() + "-" + a.getResourceType()
                ));
        
        for (Map.Entry<String, List<ResourceAllocation>> entry : byPoolAndType.entrySet()) {
            if (entry.getValue().size() > 1) {
                // Found multiple allocations for the same resource pool and type
                String[] parts = entry.getKey().split("-");
                Long poolId = Long.valueOf(parts[0]);
                String type = parts[1];
                
                Map<String, Object> issue = new HashMap<>();
                issue.put("resourcePoolId", poolId);
                issue.put("resourceType", type);
                issue.put("issueType", "DUPLICATE_ALLOCATIONS");
                issue.put("description", "Multiple active allocations found for the same resource pool and type");
                issue.put("allocationIds", entry.getValue().stream()
                        .map(ResourceAllocation::getAllocationId)
                        .collect(Collectors.toList()));
                issue.put("remediation", "Consolidate duplicate allocations into a single allocation");
                issues.add(issue);
            }
        }
        
        return issues;
    }
}
