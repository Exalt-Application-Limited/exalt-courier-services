package com.gogidix.courier.regionaladmin.service.dashboard;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Interface for performance metrics dashboard data services.
 * Provides methods for retrieving performance data for dashboard display.
 */
public interface PerformanceDashboardService {

    /**
     * Get delivery performance metrics for a region over time.
     * 
     * @param regionCode Region code
     * @param startTime Start of time range
     * @param endTime End of time range
     * @param interval Time interval for data points (e.g., "hour", "day", "week")
     * @return Map of time points to metric values
     */
    Map<LocalDateTime, Double> getDeliveryPerformanceTrend(
            String regionCode, LocalDateTime startTime, LocalDateTime endTime, String interval);
    
    /**
     * Get driver efficiency metrics for a region over time.
     * 
     * @param regionCode Region code
     * @param startTime Start of time range
     * @param endTime End of time range
     * @param interval Time interval for data points
     * @return Map of time points to metric values
     */
    Map<LocalDateTime, Double> getDriverEfficiencyTrend(
            String regionCode, LocalDateTime startTime, LocalDateTime endTime, String interval);
    
    /**
     * Get financial performance metrics for a region over time.
     * 
     * @param regionCode Region code
     * @param startTime Start of time range
     * @param endTime End of time range
     * @param interval Time interval for data points
     * @return Map of time points to revenue and cost values
     */
    Map<String, Map<LocalDateTime, Double>> getFinancialPerformanceTrend(
            String regionCode, LocalDateTime startTime, LocalDateTime endTime, String interval);
    
    /**
     * Get customer satisfaction metrics for a region over time.
     * 
     * @param regionCode Region code
     * @param startTime Start of time range
     * @param endTime End of time range
     * @param interval Time interval for data points
     * @return Map of time points to satisfaction scores
     */
    Map<LocalDateTime, Double> getCustomerSatisfactionTrend(
            String regionCode, LocalDateTime startTime, LocalDateTime endTime, String interval);
    
    /**
     * Get top-performing branches in a region.
     * 
     * @param regionCode Region code
     * @param metricName Metric to rank by
     * @param limit Number of top branches to return
     * @return List of branch IDs and their metric values
     */
    List<Map<String, Object>> getTopPerformingBranches(
            String regionCode, String metricName, int limit);
    
    /**
     * Get dashboard summary data for a region.
     * 
     * @param regionCode Region code
     * @return Map of metric names to current values
     */
    Map<String, Object> getDashboardSummary(String regionCode);
    
    /**
     * Get current regional performance KPIs.
     * 
     * @param regionCode Region code
     * @return Map of KPI names to current values
     */
    Map<String, Double> getRegionalKPIs(String regionCode);
}
