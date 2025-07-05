package com.gogidix.courier.regionaladmin.controller;

import com.socialecommerceecosystem.regionaladmin.service.CrossServiceAggregationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * REST controller for cross-service data aggregation.
 * Provides endpoints for accessing aggregated data from multiple services.
 */
@RestController
@RequestMapping("/api/aggregation")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Cross-Service Aggregation", description = "APIs for cross-service data aggregation")
public class AggregationController {

    private final CrossServiceAggregationService aggregationService;

    /**
     * Get a comprehensive regional overview.
     *
     * @param regionCode Region code
     * @return Aggregated regional overview data
     */
    @GetMapping("/overview/{regionCode}")
    @Operation(summary = "Get regional overview",
            description = "Returns a comprehensive overview of a region by aggregating data from multiple services")
    public ResponseEntity<Map<String, Object>> getRegionalOverview(
            @PathVariable String regionCode) {
        log.info("Getting regional overview for region {}", regionCode);
        Map<String, Object> overview = aggregationService.getRegionalOverview(regionCode);
        return ResponseEntity.ok(overview);
    }

    /**
     * Get performance metrics time series data.
     *
     * @param regionCode Region code
     * @param startTime Start of time range
     * @param endTime End of time range
     * @param interval Time interval for data points
     * @return Time series performance data
     */
    @GetMapping("/performance/{regionCode}")
    @Operation(summary = "Get performance metrics time series",
            description = "Returns time series performance data for a region")
    public ResponseEntity<Map<String, Object>> getPerformanceMetricsTimeSeries(
            @PathVariable String regionCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "day") String interval) {
        log.info("Getting performance metrics time series for region {} from {} to {} with interval {}",
                regionCode, startTime, endTime, interval);
        
        Map<String, Object> timeSeries = aggregationService.getPerformanceMetricsTimeSeries(
                regionCode, startTime, endTime, interval);
        
        return ResponseEntity.ok(timeSeries);
    }

    /**
     * Get service status overview.
     *
     * @param regionCode Region code
     * @return Service status overview
     */
    @GetMapping("/service-status/{regionCode}")
    @Operation(summary = "Get service status overview",
            description = "Returns an overview of service statuses in a region")
    public ResponseEntity<Map<String, Object>> getServiceStatusOverview(
            @PathVariable String regionCode) {
        log.info("Getting service status overview for region {}", regionCode);
        Map<String, Object> status = aggregationService.getServiceStatusOverview(regionCode);
        return ResponseEntity.ok(status);
    }

    /**
     * Get operational metrics.
     *
     * @param regionCode Region code
     * @return Operational metrics
     */
    @GetMapping("/operational/{regionCode}")
    @Operation(summary = "Get operational metrics",
            description = "Returns operational metrics for a region")
    public ResponseEntity<Map<String, Object>> getOperationalMetrics(
            @PathVariable String regionCode) {
        log.info("Getting operational metrics for region {}", regionCode);
        Map<String, Object> metrics = aggregationService.getOperationalMetrics(regionCode);
        return ResponseEntity.ok(metrics);
    }

    /**
     * Get financial metrics.
     *
     * @param regionCode Region code
     * @return Financial metrics
     */
    @GetMapping("/financial/{regionCode}")
    @Operation(summary = "Get financial metrics",
            description = "Returns financial metrics for a region")
    public ResponseEntity<Map<String, Object>> getFinancialMetrics(
            @PathVariable String regionCode) {
        log.info("Getting financial metrics for region {}", regionCode);
        Map<String, Object> metrics = aggregationService.getFinancialMetrics(regionCode);
        return ResponseEntity.ok(metrics);
    }

    /**
     * Get customer metrics.
     *
     * @param regionCode Region code
     * @return Customer metrics
     */
    @GetMapping("/customer/{regionCode}")
    @Operation(summary = "Get customer metrics",
            description = "Returns customer-related metrics for a region")
    public ResponseEntity<Map<String, Object>> getCustomerMetrics(
            @PathVariable String regionCode) {
        log.info("Getting customer metrics for region {}", regionCode);
        Map<String, Object> metrics = aggregationService.getCustomerMetrics(regionCode);
        return ResponseEntity.ok(metrics);
    }

    /**
     * Get region comparison.
     *
     * @param metricName Metric to compare
     * @return Region comparison data
     */
    @GetMapping("/compare/{metricName}")
    @Operation(summary = "Get region comparison",
            description = "Returns comparison data between regions for a specific metric")
    public ResponseEntity<Map<String, Double>> getRegionComparison(
            @PathVariable String metricName) {
        log.info("Getting region comparison for metric {}", metricName);
        Map<String, Double> comparison = aggregationService.getRegionComparison(metricName);
        return ResponseEntity.ok(comparison);
    }

    /**
     * Get delivery metrics.
     *
     * @param regionCode Region code
     * @return Delivery metrics
     */
    @GetMapping("/delivery/{regionCode}")
    @Operation(summary = "Get delivery metrics",
            description = "Returns delivery-related metrics for a region")
    public ResponseEntity<Map<String, Object>> getDeliveryMetrics(
            @PathVariable String regionCode) {
        log.info("Getting delivery metrics for region {}", regionCode);
        Map<String, Object> metrics = aggregationService.getDeliveryMetrics(regionCode);
        return ResponseEntity.ok(metrics);
    }
}