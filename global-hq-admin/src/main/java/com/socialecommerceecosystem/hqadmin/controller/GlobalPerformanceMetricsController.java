package com.gogidix.courier.courier.hqadmin.controller;

import com.socialecommerceecosystem.hqadmin.model.GlobalPerformanceMetrics;
import com.socialecommerceecosystem.hqadmin.service.GlobalPerformanceMetricsService;
import com.socialecommerceecosystem.hqadmin.service.GlobalRegionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * REST controller for managing global performance metrics.
 */
@RestController
@RequestMapping("/api/v1/metrics")
@RequiredArgsConstructor
@Slf4j
public class GlobalPerformanceMetricsController {

    private final GlobalPerformanceMetricsService globalPerformanceMetricsService;
    private final GlobalRegionService globalRegionService;

    /**
     * GET /api/v1/metrics : Get all performance metrics
     * 
     * @return the ResponseEntity with status 200 (OK) and the list of performance metrics
     */
    @GetMapping
    public ResponseEntity<List<GlobalPerformanceMetrics>> getAllMetrics() {
        log.debug("REST request to get all global performance metrics");
        return ResponseEntity.ok(globalPerformanceMetricsService.getAllMetrics());
    }

    /**
     * GET /api/v1/metrics/{id} : Get a performance metric by id
     * 
     * @param id the id of the performance metric to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the performance metric, or with status 404 (Not Found)
     */
    @GetMapping("/{id}")
    public ResponseEntity<GlobalPerformanceMetrics> getMetric(@PathVariable Long id) {
        log.debug("REST request to get global performance metric : {}", id);
        return globalPerformanceMetricsService.getMetricById(id)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Performance metric not found with id: " + id));
    }

    /**
     * GET /api/v1/metrics/date/{date} : Get performance metrics for a specific date
     * 
     * @param date the date of metrics to retrieve
     * @return the ResponseEntity with status 200 (OK) and the list of performance metrics for the date
     */
    @GetMapping("/date/{date}")
    public ResponseEntity<List<GlobalPerformanceMetrics>> getMetricsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.debug("REST request to get global performance metrics for date: {}", date);
        return ResponseEntity.ok(globalPerformanceMetricsService.getMetricsByDate(date));
    }

    /**
     * GET /api/v1/metrics/region/{regionId} : Get performance metrics for a region
     * 
     * @param regionId the id of the region to filter by
     * @return the ResponseEntity with status 200 (OK) and the list of performance metrics for the region
     */
    @GetMapping("/region/{regionId}")
    public ResponseEntity<List<GlobalPerformanceMetrics>> getMetricsByRegion(@PathVariable Long regionId) {
        log.debug("REST request to get global performance metrics for region: {}", regionId);
        
        try {
            List<GlobalPerformanceMetrics> metrics = globalPerformanceMetricsService.getMetricsByRegion(regionId);
            return ResponseEntity.ok(metrics);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * GET /api/v1/metrics/type/{metricType} : Get performance metrics by type
     * 
     * @param metricType the type of metrics to retrieve
     * @return the ResponseEntity with status 200 (OK) and the list of performance metrics of the specified type
     */
    @GetMapping("/type/{metricType}")
    public ResponseEntity<List<GlobalPerformanceMetrics>> getMetricsByType(@PathVariable String metricType) {
        log.debug("REST request to get global performance metrics by type: {}", metricType);
        return ResponseEntity.ok(globalPerformanceMetricsService.getMetricsByType(metricType));
    }

    /**
     * GET /api/v1/metrics/date-range : Get performance metrics for a date range
     * 
     * @param startDate the start date of the range
     * @param endDate the end date of the range
     * @return the ResponseEntity with status 200 (OK) and the list of performance metrics within the date range
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<GlobalPerformanceMetrics>> getMetricsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.debug("REST request to get global performance metrics between dates {} and {}", startDate, endDate);
        return ResponseEntity.ok(globalPerformanceMetricsService.getMetricsByDateRange(startDate, endDate));
    }

    /**
     * GET /api/v1/metrics/region/{regionId}/date-range : Get metrics for a region within a date range
     * 
     * @param regionId the id of the region to filter by
     * @param startDate the start date of the range
     * @param endDate the end date of the range
     * @return the ResponseEntity with status 200 (OK) and the list of metrics for the region within the date range
     */
    @GetMapping("/region/{regionId}/date-range")
    public ResponseEntity<List<GlobalPerformanceMetrics>> getMetricsByRegionAndDateRange(
            @PathVariable Long regionId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.debug("REST request to get global performance metrics for region: {} between dates {} and {}", 
                 regionId, startDate, endDate);
        
        try {
            List<GlobalPerformanceMetrics> metrics = globalPerformanceMetricsService.getMetricsByRegionAndDateRange(
                regionId, startDate, endDate);
            return ResponseEntity.ok(metrics);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * GET /api/v1/metrics/high-rating/{ratingThreshold} : Get metrics with average rating above a threshold
     * 
     * @param ratingThreshold the minimum rating threshold
     * @return the ResponseEntity with status 200 (OK) and the list of metrics with average rating above the threshold
     */
    @GetMapping("/high-rating/{ratingThreshold}")
    public ResponseEntity<List<GlobalPerformanceMetrics>> getMetricsWithHighRating(@PathVariable BigDecimal ratingThreshold) {
        log.debug("REST request to get global performance metrics with high rating (>= {})", ratingThreshold);
        return ResponseEntity.ok(globalPerformanceMetricsService.getMetricsWithHighRating(ratingThreshold));
    }

    /**
     * GET /api/v1/metrics/high-on-time/{threshold} : Get metrics with on-time delivery percentage above a threshold
     * 
     * @param threshold the on-time percentage threshold
     * @return the ResponseEntity with status 200 (OK) and the list of metrics with on-time percentage above the threshold
     */
    @GetMapping("/high-on-time/{threshold}")
    public ResponseEntity<List<GlobalPerformanceMetrics>> getMetricsWithHighOnTimePercentage(@PathVariable float threshold) {
        log.debug("REST request to get global performance metrics with high on-time percentage (>= {}%)", threshold);
        return ResponseEntity.ok(globalPerformanceMetricsService.getMetricsWithHighOnTimePercentage(threshold));
    }

    /**
     * GET /api/v1/metrics/high-profit/{marginThreshold} : Get metrics with profit margin above a threshold
     * 
     * @param marginThreshold the profit margin threshold
     * @return the ResponseEntity with status 200 (OK) and the list of metrics with profit margin above the threshold
     */
    @GetMapping("/high-profit/{marginThreshold}")
    public ResponseEntity<List<GlobalPerformanceMetrics>> getMetricsWithHighProfitMargin(@PathVariable BigDecimal marginThreshold) {
        log.debug("REST request to get global performance metrics with high profit margin (>= {}%)", marginThreshold);
        return ResponseEntity.ok(globalPerformanceMetricsService.getMetricsWithHighProfitMargin(marginThreshold));
    }

    /**
     * GET /api/v1/metrics/aggregate : Calculate average metrics across all regions for a date range
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @return the ResponseEntity with status 200 (OK) and the map of metric name to average value
     */
    @GetMapping("/aggregate")
    public ResponseEntity<Map<String, Object>> aggregateMetricsForDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.debug("REST request to aggregate global performance metrics between dates {} and {}", startDate, endDate);
        return ResponseEntity.ok(globalPerformanceMetricsService.aggregateMetricsForDateRange(startDate, endDate));
    }

    /**
     * GET /api/v1/metrics/trend : Get trend of metrics by date for a specific region
     * 
     * @param regionId the id of the region
     * @param startDate the start date
     * @param endDate the end date
     * @return the ResponseEntity with status 200 (OK) and the list of metric trends by date
     */
    @GetMapping("/trend")
    public ResponseEntity<List<Map<String, Object>>> getMetricsTrendByDate(
            @RequestParam Long regionId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.debug("REST request to get trend of global performance metrics for region: {} between dates {} and {}", 
                 regionId, startDate, endDate);
        
        try {
            List<Map<String, Object>> trend = globalPerformanceMetricsService.getMetricsTrendByDate(
                regionId, startDate, endDate);
            return ResponseEntity.ok(trend);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * GET /api/v1/metrics/walk-in/{threshold} : Get metrics for walk-in customers above a threshold
     * 
     * @param threshold the walk-in customers threshold
     * @return the ResponseEntity with status 200 (OK) and the list of metrics with walk-in customers above the threshold
     */
    @GetMapping("/walk-in/{threshold}")
    public ResponseEntity<List<GlobalPerformanceMetrics>> getMetricsForHighWalkInCustomers(@PathVariable Integer threshold) {
        log.debug("REST request to get global performance metrics with high walk-in customers (>= {})", threshold);
        return ResponseEntity.ok(globalPerformanceMetricsService.getMetricsForHighWalkInCustomers(threshold));
    }

    /**
     * GET /api/v1/metrics/compare-regions/{date} : Compare metrics between regions for a specific date
     * 
     * @param date the date to compare
     * @return the ResponseEntity with status 200 (OK) and the list of metrics by region for comparison
     */
    @GetMapping("/compare-regions/{date}")
    public ResponseEntity<List<Map<String, Object>>> compareRegionsForDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.debug("REST request to compare global performance metrics between regions for date: {}", date);
        return ResponseEntity.ok(globalPerformanceMetricsService.compareRegionsForDate(date));
    }

    /**
     * GET /api/v1/metrics/kpis : Calculate KPIs for a specific date range
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @return the ResponseEntity with status 200 (OK) and the map of KPI name to value
     */
    @GetMapping("/kpis")
    public ResponseEntity<Map<String, Object>> calculateKPIsForDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.debug("REST request to calculate KPIs for global performance metrics between dates {} and {}", startDate, endDate);
        return ResponseEntity.ok(globalPerformanceMetricsService.calculateKPIsForDateRange(startDate, endDate));
    }

    /**
     * POST /api/v1/metrics : Create a new performance metric
     * 
     * @param metric the performance metric to create
     * @return the ResponseEntity with status 201 (Created) and with body the new performance metric
     */
    @PostMapping
    public ResponseEntity<GlobalPerformanceMetrics> createMetric(@Valid @RequestBody GlobalPerformanceMetrics metric) {
        log.debug("REST request to save global performance metric : {}", metric);
        if (metric.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A new performance metric cannot already have an ID");
        }
        
        try {
            GlobalPerformanceMetrics result = globalPerformanceMetricsService.createMetric(metric);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * PUT /api/v1/metrics/{id} : Update an existing performance metric
     * 
     * @param id the id of the performance metric to update
     * @param metric the performance metric to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated performance metric
     */
    @PutMapping("/{id}")
    public ResponseEntity<GlobalPerformanceMetrics> updateMetric(
            @PathVariable Long id, 
            @Valid @RequestBody GlobalPerformanceMetrics metric) {
        log.debug("REST request to update global performance metric : {}", metric);
        if (metric.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Performance metric ID must not be null");
        }
        if (!id.equals(metric.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "IDs don't match");
        }
        
        try {
            GlobalPerformanceMetrics result = globalPerformanceMetricsService.updateMetric(id, metric);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * DELETE /api/v1/metrics/{id} : Delete a performance metric
     * 
     * @param id the id of the performance metric to delete
     * @return the ResponseEntity with status 204 (NO_CONTENT)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMetric(@PathVariable Long id) {
        log.debug("REST request to delete global performance metric : {}", id);
        try {
            globalPerformanceMetricsService.deleteMetric(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * POST /api/v1/metrics/import/{date} : Import metrics from regional systems for a specific date
     * 
     * @param date the date to import metrics for
     * @return the ResponseEntity with status 200 (OK) and with body the list of imported metrics
     */
    @PostMapping("/import/{date}")
    public ResponseEntity<List<GlobalPerformanceMetrics>> importMetricsFromRegionalSystems(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.debug("REST request to import global performance metrics from regional systems for date: {}", date);
        return ResponseEntity.ok(globalPerformanceMetricsService.importMetricsFromRegionalSystems(date));
    }
}
