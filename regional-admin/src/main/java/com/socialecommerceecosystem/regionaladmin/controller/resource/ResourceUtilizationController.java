package com.gogidix.courier.regionaladmin.controller.resource;

import com.socialecommerceecosystem.regionaladmin.service.resource.ResourceUtilizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * REST controller for resource utilization reporting.
 * Provides endpoints for analyzing resource allocation and usage efficiency.
 */
@RestController
@RequestMapping("/api/resources/utilization")
public class ResourceUtilizationController {

    private static final Logger logger = LoggerFactory.getLogger(ResourceUtilizationController.class);

    @Autowired
    private ResourceUtilizationService resourceUtilizationService;

    /**
     * Get resource allocation summary by resource type.
     * 
     * @return Map of resource types to their allocation statistics
     */
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Map<String, Object>>> getResourceAllocationSummary() {
        logger.info("Getting resource allocation summary");
        Map<String, Map<String, Object>> summary = resourceUtilizationService.getResourceAllocationSummary();
        return ResponseEntity.ok(summary);
    }

    /**
     * Get resource utilization metrics.
     * 
     * @return Map containing resource utilization metrics
     */
    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getUtilizationMetrics() {
        logger.info("Getting resource utilization metrics");
        Map<String, Object> metrics = resourceUtilizationService.calculateUtilizationMetrics();
        return ResponseEntity.ok(metrics);
    }

    /**
     * Get a list of resource allocation issues.
     * 
     * @return List of resource allocations with identified issues
     */
    @GetMapping("/issues")
    public ResponseEntity<List<Map<String, Object>>> getAllocationIssues() {
        logger.info("Getting resource allocation issues");
        List<Map<String, Object>> issues = resourceUtilizationService.identifyAllocationIssues();
        return ResponseEntity.ok(issues);
    }

    /**
     * Get a utilization dashboard summary with key metrics.
     * 
     * @return Map containing key utilization metrics
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getUtilizationDashboard() {
        logger.info("Getting utilization dashboard");
        
        // Get utilization metrics
        Map<String, Object> metrics = resourceUtilizationService.calculateUtilizationMetrics();
        
        // Get issues count
        List<Map<String, Object>> issues = resourceUtilizationService.identifyAllocationIssues();
        
        // Create summarized dashboard data
        Map<String, Object> dashboard = Map.of(
            "totalAllocatedResources", metrics.get("totalAllocatedResources"),
            "allocationEfficiency", metrics.get("allocationEfficiency"),
            "resourceTypeCount", metrics.get("resourceTypeCount"),
            "expiringSoonCount", metrics.get("expiringSoonCount"),
            "issuesCount", issues.size(),
            "resourceTypeDistribution", metrics.get("allocationByResourceType")
        );
        
        return ResponseEntity.ok(dashboard);
    }
}
