package com.gogidix.courier.management.performance.service;

import com.gogidix.courier.management.performance.dto.PerformanceMetricDTO;
import com.gogidix.courier.management.performance.model.MetricType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service interface for managing performance metrics.
 */
public interface PerformanceMetricsService {

    /**
     * Create a new performance metric.
     *
     * @param metricDTO the metric data
     * @return the created metric
     */
    PerformanceMetricDTO createMetric(PerformanceMetricDTO metricDTO);

    /**
     * Update an existing performance metric.
     *
     * @param metricId the ID of the metric to update
     * @param metricDTO the updated metric data
     * @return the updated metric
     */
    PerformanceMetricDTO updateMetric(String metricId, PerformanceMetricDTO metricDTO);

    /**
     * Get a performance metric by its ID.
     *
     * @param metricId the ID of the metric
     * @return an Optional containing the metric if found
     */
    Optional<PerformanceMetricDTO> getMetricById(String metricId);

    /**
     * Delete a performance metric.
     *
     * @param metricId the ID of the metric to delete
     * @return true if deleted, false if not found
     */
    boolean deleteMetric(String metricId);

    /**
     * Get all metrics for a specific courier.
     *
     * @param courierId the ID of the courier
     * @return a list of metrics for the courier
     */
    List<PerformanceMetricDTO> getMetricsByCourier(String courierId);

    /**
     * Get all metrics for a specific courier with pagination.
     *
     * @param courierId the ID of the courier
     * @param pageable pagination information
     * @return a page of metrics for the courier
     */
    Page<PerformanceMetricDTO> getMetricsByCourier(String courierId, Pageable pageable);

    /**
     * Get metrics for a specific courier and metric type.
     *
     * @param courierId the ID of the courier
     * @param metricType the type of metric
     * @return a list of metrics matching the criteria
     */
    List<PerformanceMetricDTO> getMetricsByCourierAndType(String courierId, MetricType metricType);

    /**
     * Get metrics for a specific date range.
     *
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return a list of metrics within the date range
     */
    List<PerformanceMetricDTO> getMetricsForDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Get metrics for a specific courier and date range.
     *
     * @param courierId the ID of the courier
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return a list of metrics matching the criteria
     */
    List<PerformanceMetricDTO> getMetricsByCourierAndDateRange(
            String courierId, LocalDate startDate, LocalDate endDate);

    /**
     * Calculate average metrics for all couriers.
     *
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return a map of metric types to their average values
     */
    Map<MetricType, Double> calculateAverageMetrics(LocalDate startDate, LocalDate endDate);

    /**
     * Calculate average metrics for a specific courier.
     *
     * @param courierId the ID of the courier
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return a map of metric types to their average values
     */
    Map<MetricType, Double> calculateAverageMetricsForCourier(
            String courierId, LocalDate startDate, LocalDate endDate);

    /**
     * Calculate performance trends for a courier.
     *
     * @param courierId the ID of the courier
     * @param metricType the type of metric
     * @param numberOfPeriods the number of periods to analyze
     * @param periodType the type of period (day, week, month)
     * @return a list of metrics with trend data
     */
    List<PerformanceMetricDTO> calculatePerformanceTrends(
            String courierId, MetricType metricType, int numberOfPeriods, String periodType);

    /**
     * Generate a performance report for a courier.
     *
     * @param courierId the ID of the courier
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return a map containing report data
     */
    Map<String, Object> generatePerformanceReport(String courierId, LocalDate startDate, LocalDate endDate);
} 