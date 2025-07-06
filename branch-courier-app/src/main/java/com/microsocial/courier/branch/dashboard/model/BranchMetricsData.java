package com.gogidix.courier.courier.branch.dashboard.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Model class representing metrics data collected from a branch.
 * This class encapsulates all metrics that are reported from the Branch level to the Regional level.
 * 
 * Converted to use Lombok annotations for reduced boilerplate.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BranchMetricsData {
    
    // Unique identifier for this metrics data collection
    private String metricsId;
    
    // ID of the branch this metrics data is from
    private String branchId;
    
    // ID of the region this branch belongs to
    private String regionId;
    
    // Timestamp when the metrics were collected
    private LocalDateTime timestamp;
    
    // Delivery-related metrics
    private DeliveryMetrics deliveryMetrics;
    
    // Courier performance metrics
    private PerformanceMetrics performanceMetrics;
    
    // Resource utilization metrics
    private ResourceMetrics resourceMetrics;
}