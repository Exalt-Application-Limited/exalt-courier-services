package com.gogidix.courier.regionaladmin.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Interface for cross-service data aggregation.
 * Provides methods for aggregating data from multiple services to create comprehensive views.
 */
public interface CrossServiceAggregationService {

    /**
     * Get a comprehensive regional overview by aggregating data from multiple services.
     * 
     * @param regionCode Region code
     * @return Map containing aggregated data
     */
    Map<String, Object> getRegionalOverview(String regionCode);
    
    /**
     * Get performance metrics across multiple services for a time period.
     * 
     * @param regionCode Region code
     * @param startTime Start of time range
     * @param endTime End of time range
     * @param interval Time interval for data points
     * @return Map containing time-series performance data
     */
    Map<String, Object> getPerformanceMetricsTimeSeries(
            String regionCode, LocalDateTime startTime, LocalDateTime endTime, String interval);
    
    /**
     * Get status of all services in a region.
     * 
     * @param regionCode Region code
     * @return Map of service statuses
     */
    Map<String, Object> getServiceStatusOverview(String regionCode);
    
    /**
     * Get operational metrics from multiple services.
     * 
     * @param regionCode Region code
     * @return Map containing operational metrics
     */
    Map<String, Object> getOperationalMetrics(String regionCode);
    
    /**
     * Get financial metrics from multiple services.
     * 
     * @param regionCode Region code
     * @return Map containing financial metrics
     */
    Map<String, Object> getFinancialMetrics(String regionCode);
    
    /**
     * Get customer-related metrics from multiple services.
     * 
     * @param regionCode Region code
     * @return Map containing customer metrics
     */
    Map<String, Object> getCustomerMetrics(String regionCode);
    
    /**
     * Get comparison data between regions.
     * 
     * @param metricName Metric to compare
     * @return Map of region codes to metric values
     */
    Map<String, Double> getRegionComparison(String metricName);
    
    /**
     * Get delivery-related metrics from tracking and other services.
     * 
     * @param regionCode Region code
     * @return Map containing delivery metrics
     */
    Map<String, Object> getDeliveryMetrics(String regionCode);
}