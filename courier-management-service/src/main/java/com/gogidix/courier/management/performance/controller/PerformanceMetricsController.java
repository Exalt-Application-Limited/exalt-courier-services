package com.gogidix.courier.management.assignment.controller;

import com.gogidix.courier.management.performance.dto.PerformanceMetricDTO;
import com.gogidix.courier.management.performance.model.MetricType;
import com.gogidix.courier.management.performance.service.PerformanceMetricsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * REST controller for managing performance metrics.
 */
@RestController
@RequestMapping("/api/v1/performance-metrics")
@Slf4j
public class PerformanceMetricsController {

    

    private final PerformanceMetricsService metricsService;

    @Autowired
    public PerformanceMetricsController(PerformanceMetricsService metricsService) {
        this.metricsService = metricsService;
    }

    /**
     * Create a new performance metric.
     *
     * @param metricDTO the metric data to create
     * @return the created metric
     */
    @PostMapping
    public ResponseEntity<PerformanceMetricDTO> createMetric(@Valid @RequestBody PerformanceMetricDTO metricDTO) {
        logger.info("REST request to create a new performance metric");
        PerformanceMetricDTO createdMetric = metricsService.createMetric(metricDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMetric);
    }

    /**
     * Update an existing performance metric.
     *
     * @param metricId the ID of the metric to update
     * @param metricDTO the updated metric data
     * @return the updated metric
     */
    @PutMapping("/{metricId}")
    public ResponseEntity<PerformanceMetricDTO> updateMetric(
            @PathVariable String metricId,
            @Valid @RequestBody PerformanceMetricDTO metricDTO) {
        logger.info("REST request to update performance metric: {}", metricId);
        try {
            PerformanceMetricDTO updatedMetric = metricsService.updateMetric(metricId, metricDTO);
            return ResponseEntity.ok(updatedMetric);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get a performance metric by ID.
     *
     * @param metricId the ID of the metric to retrieve
     * @return the metric
     */
    @GetMapping("/{metricId}")
    public ResponseEntity<PerformanceMetricDTO> getMetric(@PathVariable String metricId) {
        logger.info("REST request to get performance metric: {}", metricId);
        return metricsService.getMetricById(metricId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete a performance metric.
     *
     * @param metricId the ID of the metric to delete
     * @return no content if successful
     */
    @DeleteMapping("/{metricId}")
    public ResponseEntity<Void> deleteMetric(@PathVariable String metricId) {
        logger.info("REST request to delete performance metric: {}", metricId);
        boolean deleted = metricsService.deleteMetric(metricId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    /**
     * Get all metrics for a courier.
     *
     * @param courierId the ID of the courier
     * @return the list of metrics
     */
    @GetMapping("/courier/{courierId}")
    public ResponseEntity<List<PerformanceMetricDTO>> getMetricsByCourier(@PathVariable String courierId) {
        logger.info("REST request to get all metrics for courier: {}", courierId);
        List<PerformanceMetricDTO> metrics = metricsService.getMetricsByCourier(courierId);
        return ResponseEntity.ok(metrics);
    }

    /**
     * Get paginated metrics for a courier.
     *
     * @param courierId the ID of the courier
     * @param pageable pagination information
     * @return the page of metrics
     */
    @GetMapping("/courier/{courierId}/paginated")
    public ResponseEntity<Page<PerformanceMetricDTO>> getMetricsByCourierPaginated(
            @PathVariable String courierId,
            Pageable pageable) {
        logger.info("REST request to get paginated metrics for courier: {}", courierId);
        Page<PerformanceMetricDTO> metrics = metricsService.getMetricsByCourier(courierId, pageable);
        return ResponseEntity.ok(metrics);
    }

    /**
     * Get metrics for a courier and metric type.
     *
     * @param courierId the ID of the courier
     * @param metricType the type of metric
     * @return the list of metrics
     */
    @GetMapping("/courier/{courierId}/type/{metricType}")
    public ResponseEntity<List<PerformanceMetricDTO>> getMetricsByCourierAndType(
            @PathVariable String courierId,
            @PathVariable MetricType metricType) {
        logger.info("REST request to get metrics for courier: {} and type: {}", courierId, metricType);
        List<PerformanceMetricDTO> metrics = metricsService.getMetricsByCourierAndType(courierId, metricType);
        return ResponseEntity.ok(metrics);
    }

    /**
     * Get metrics for a date range.
     *
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return the list of metrics
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<PerformanceMetricDTO>> getMetricsForDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        logger.info("REST request to get metrics for date range: {} to {}", startDate, endDate);
        List<PerformanceMetricDTO> metrics = metricsService.getMetricsForDateRange(startDate, endDate);
        return ResponseEntity.ok(metrics);
    }

    /**
     * Get metrics for a courier and date range.
     *
     * @param courierId the ID of the courier
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return the list of metrics
     */
    @GetMapping("/courier/{courierId}/date-range")
    public ResponseEntity<List<PerformanceMetricDTO>> getMetricsByCourierAndDateRange(
            @PathVariable String courierId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        logger.info("REST request to get metrics for courier: {} and date range: {} to {}", 
                courierId, startDate, endDate);
        List<PerformanceMetricDTO> metrics = 
                metricsService.getMetricsByCourierAndDateRange(courierId, startDate, endDate);
        return ResponseEntity.ok(metrics);
    }

    /**
     * Calculate average metrics for all couriers.
     *
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return the map of metric types to average values
     */
    @GetMapping("/averages")
    public ResponseEntity<Map<MetricType, Double>> calculateAverageMetrics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        logger.info("REST request to calculate average metrics for date range: {} to {}", startDate, endDate);
        Map<MetricType, Double> averages = metricsService.calculateAverageMetrics(startDate, endDate);
        return ResponseEntity.ok(averages);
    }

    /**
     * Calculate average metrics for a courier.
     *
     * @param courierId the ID of the courier
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return the map of metric types to average values
     */
    @GetMapping("/courier/{courierId}/averages")
    public ResponseEntity<Map<MetricType, Double>> calculateAverageMetricsForCourier(
            @PathVariable String courierId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        logger.info("REST request to calculate average metrics for courier: {} and date range: {} to {}", 
                courierId, startDate, endDate);
        Map<MetricType, Double> averages = 
                metricsService.calculateAverageMetricsForCourier(courierId, startDate, endDate);
        return ResponseEntity.ok(averages);
    }

    /**
     * Calculate performance trends for a courier.
     *
     * @param courierId the ID of the courier
     * @param metricType the type of metric
     * @param numberOfPeriods the number of periods to analyze
     * @param periodType the type of period (day, week, month)
     * @return the list of metrics with trend data
     */
    @GetMapping("/courier/{courierId}/trends")
    public ResponseEntity<List<PerformanceMetricDTO>> calculatePerformanceTrends(
            @PathVariable String courierId,
            @RequestParam MetricType metricType,
            @RequestParam(defaultValue = "7") int numberOfPeriods,
            @RequestParam(defaultValue = "day") String periodType) {
        logger.info("REST request to calculate performance trends for courier: {}, metric type: {}, periods: {}, period type: {}", 
                courierId, metricType, numberOfPeriods, periodType);
        List<PerformanceMetricDTO> trends = 
                metricsService.calculatePerformanceTrends(courierId, metricType, numberOfPeriods, periodType);
        return ResponseEntity.ok(trends);
    }

    /**
     * Generate a performance report for a courier.
     *
     * @param courierId the ID of the courier
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return the performance report data
     */
    @GetMapping("/courier/{courierId}/report")
    public ResponseEntity<Map<String, Object>> generatePerformanceReport(
            @PathVariable String courierId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        logger.info("REST request to generate performance report for courier: {} from {} to {}", 
                courierId, startDate, endDate);
        Map<String, Object> report = metricsService.generatePerformanceReport(courierId, startDate, endDate);
        return ResponseEntity.ok(report);
    }
} 
