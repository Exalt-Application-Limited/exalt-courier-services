package com.exalt.courier.hqadmin.service;

import com.socialecommerceecosystem.hqadmin.model.GlobalPerformanceMetrics;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service interface defining operations for managing global performance metrics.
 */
public interface GlobalPerformanceMetricsService {
    
    /**
     * Get all global performance metrics
     * 
     * @return List of all global performance metrics
     */
    List<GlobalPerformanceMetrics> getAllMetrics();
    
    /**
     * Get a global performance metric by ID
     * 
     * @param id The metric ID
     * @return The global performance metric if found
     */
    Optional<GlobalPerformanceMetrics> getMetricById(Long id);
    
    /**
     * Create a new global performance metric
     * 
     * @param metric The global performance metric to create
     * @return The created global performance metric
     */
    GlobalPerformanceMetrics createMetric(GlobalPerformanceMetrics metric);
    
    /**
     * Update an existing global performance metric
     * 
     * @param id The metric ID to update
     * @param metricDetails The updated metric details
     * @return The updated global performance metric
     * @throws RuntimeException if metric not found
     */
    GlobalPerformanceMetrics updateMetric(Long id, GlobalPerformanceMetrics metricDetails);
    
    /**
     * Delete a global performance metric
     * 
     * @param id The metric ID to delete
     * @throws RuntimeException if metric not found
     */
    void deleteMetric(Long id);
    
    /**
     * Get all performance metrics for a specific date
     * 
     * @param date The date to search for
     * @return List of performance metrics for the specified date
     */
    List<GlobalPerformanceMetrics> getMetricsByDate(LocalDate date);
    
    /**
     * Get all performance metrics for a specific global region
     * 
     * @param regionId The global region ID
     * @return List of performance metrics for the region
     * @throws RuntimeException if region not found
     */
    List<GlobalPerformanceMetrics> getMetricsByRegion(Long regionId);
    
    /**
     * Get all performance metrics for a specific metric type
     * 
     * @param metricType The metric type
     * @return List of performance metrics of the specified type
     */
    List<GlobalPerformanceMetrics> getMetricsByType(String metricType);
    
    /**
     * Get all performance metrics for a specific date range
     * 
     * @param startDate The start date of the range
     * @param endDate The end date of the range
     * @return List of performance metrics within the date range
     */
    List<GlobalPerformanceMetrics> getMetricsByDateRange(LocalDate startDate, LocalDate endDate);
    
    /**
     * Get metrics for a region within a date range
     * 
     * @param regionId The global region ID
     * @param startDate The start date
     * @param endDate The end date
     * @return List of metrics for the region within the date range
     * @throws RuntimeException if region not found
     */
    List<GlobalPerformanceMetrics> getMetricsByRegionAndDateRange(
            Long regionId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Get all metrics with average rating above a threshold
     * 
     * @param ratingThreshold The minimum rating threshold
     * @return List of metrics with average rating above the threshold
     */
    List<GlobalPerformanceMetrics> getMetricsWithHighRating(BigDecimal ratingThreshold);
    
    /**
     * Get all metrics with on-time delivery percentage above a threshold
     * 
     * @param threshold The on-time percentage threshold
     * @return List of metrics with on-time delivery percentage above the threshold
     */
    List<GlobalPerformanceMetrics> getMetricsWithHighOnTimePercentage(float threshold);
    
    /**
     * Get all metrics with profit margin above a threshold
     * 
     * @param marginThreshold The profit margin threshold
     * @return List of metrics with profit margin above the threshold
     */
    List<GlobalPerformanceMetrics> getMetricsWithHighProfitMargin(BigDecimal marginThreshold);
    
    /**
     * Calculate average metrics across all regions for a date range
     * 
     * @param startDate The start date
     * @param endDate The end date
     * @return Map of metric name to average value
     */
    Map<String, Object> aggregateMetricsForDateRange(LocalDate startDate, LocalDate endDate);
    
    /**
     * Get trend of metrics by date for a specific region
     * 
     * @param regionId The global region ID
     * @param startDate The start date
     * @param endDate The end date
     * @return List of metric trends by date
     * @throws RuntimeException if region not found
     */
    List<Map<String, Object>> getMetricsTrendByDate(Long regionId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Get performance metrics for walk-in customers above a threshold
     * 
     * @param threshold The walk-in customers threshold
     * @return List of metrics with walk-in customers above the threshold
     */
    List<GlobalPerformanceMetrics> getMetricsForHighWalkInCustomers(Integer threshold);
    
    /**
     * Import metrics from regional systems for a specific date
     * 
     * @param date The date to import metrics for
     * @return List of imported metrics
     */
    List<GlobalPerformanceMetrics> importMetricsFromRegionalSystems(LocalDate date);
    
    /**
     * Get comparison of metrics between regions for a specific date
     * 
     * @param date The date to compare
     * @return List of metrics by region for comparison
     */
    List<Map<String, Object>> compareRegionsForDate(LocalDate date);
    
    /**
     * Calculate KPIs for a specific date range
     * 
     * @param startDate The start date
     * @param endDate The end date
     * @return Map of KPI name to value
     */
    Map<String, Object> calculateKPIsForDateRange(LocalDate startDate, LocalDate endDate);
}
