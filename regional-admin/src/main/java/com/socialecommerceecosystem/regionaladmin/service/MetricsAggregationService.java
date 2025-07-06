package com.gogidix.courier.regionaladmin.service;

import com.socialecommerceecosystem.regionaladmin.dto.RegionalMetricsDTO;
import com.socialecommerceecosystem.regionaladmin.model.RegionalMetrics;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Service interface for regional metrics aggregation.
 * Defines methods for collecting, aggregating, and retrieving metrics data.
 */
public interface MetricsAggregationService {

    /**
     * Aggregate metrics data from multiple sources/branches.
     * 
     * @param sourceData Map of source/branch identifiers to their metrics data
     * @param metricName The name of the metric to aggregate
     * @param regionCode The region code for the aggregated metric
     * @param aggregationMethod The method to use for aggregation (SUM, AVG, MIN, MAX)
     * @return The aggregated metric
     */
    RegionalMetricsDTO aggregateMetrics(Map<String, Double> sourceData, 
                                    String metricName, 
                                    String regionCode, 
                                    String aggregationMethod);
    
    /**
     * Collect metrics from various services and aggregate them.
     * This scheduled method will pull data from different services.
     */
    void collectAndAggregateMetrics();
    
    /**
     * Get metrics for a specific region and category.
     * 
     * @param regionCode The region code
     * @param category The metric category
     * @return List of metrics for the region and category
     */
    List<RegionalMetricsDTO> getMetricsByRegionAndCategory(String regionCode, String category);
    
    /**
     * Get metrics for a specific region within a time range.
     * 
     * @param regionCode The region code
     * @param startTime The start of the time range
     * @param endTime The end of the time range
     * @return List of metrics for the region in the time range
     */
    List<RegionalMetricsDTO> getMetricsForRegionInTimeRange(String regionCode, 
                                                        LocalDateTime startTime, 
                                                        LocalDateTime endTime);
    
    /**
     * Save a new regional metric.
     * 
     * @param metricsDTO The metrics data to save
     * @return The saved metrics data
     */
    RegionalMetricsDTO saveRegionalMetric(RegionalMetricsDTO metricsDTO);
    
    /**
     * Get comparative metrics across different regions.
     * 
     * @param metricName The name of the metric to compare
     * @param startTime The start of the time range
     * @param endTime The end of the time range
     * @return Map of region codes to their metric values
     */
    Map<String, Double> getComparativeMetricsAcrossRegions(String metricName, 
                                                       LocalDateTime startTime, 
                                                       LocalDateTime endTime);
}
