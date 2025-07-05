package com.gogidix.courier.regionaladmin.controller;

import com.socialecommerceecosystem.regionaladmin.dto.RegionalMetricsDTO;
import com.socialecommerceecosystem.regionaladmin.service.MetricsAggregationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * REST controller for accessing and managing regional metrics data.
 * Provides endpoints for retrieving metrics by region, category, and time range.
 */
@RestController
@RequestMapping("/api/metrics")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Regional Metrics", description = "APIs for managing regional metrics data")
public class RegionalMetricsController {

    private final MetricsAggregationService metricsService;

    /**
     * Get metrics for a specific region and category.
     *
     * @param regionCode Region code
     * @param category Metric category
     * @return List of metrics for the region and category
     */
    @GetMapping("/region/{regionCode}/category/{category}")
    @Operation(summary = "Get metrics by region and category",
            description = "Returns all metrics for a specific region and category")
    public ResponseEntity<List<RegionalMetricsDTO>> getMetricsByRegionAndCategory(
            @PathVariable String regionCode,
            @PathVariable String category) {
        log.info("Getting metrics for region {} and category {}", regionCode, category);
        List<RegionalMetricsDTO> metrics = metricsService.getMetricsByRegionAndCategory(regionCode, category);
        return ResponseEntity.ok(metrics);
    }

    /**
     * Get metrics for a specific region within a time range.
     *
     * @param regionCode Region code
     * @param startTime Start of time range
     * @param endTime End of time range
     * @return List of metrics for the region in the time range
     */
    @GetMapping("/region/{regionCode}/timerange")
    @Operation(summary = "Get metrics by region and time range",
            description = "Returns all metrics for a specific region within a time range")
    public ResponseEntity<List<RegionalMetricsDTO>> getMetricsForRegionInTimeRange(
            @PathVariable String regionCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        log.info("Getting metrics for region {} from {} to {}", regionCode, startTime, endTime);
        List<RegionalMetricsDTO> metrics = metricsService.getMetricsForRegionInTimeRange(
                regionCode, startTime, endTime);
        return ResponseEntity.ok(metrics);
    }

    /**
     * Save a new regional metric.
     *
     * @param metricsDTO Metrics data to save
     * @return Saved metrics data
     */
    @PostMapping
    @Operation(summary = "Save a new metric",
            description = "Saves a new regional metric")
    public ResponseEntity<RegionalMetricsDTO> saveRegionalMetric(
            @RequestBody RegionalMetricsDTO metricsDTO) {
        log.info("Saving new regional metric for region {}", metricsDTO.getRegionCode());
        RegionalMetricsDTO savedMetrics = metricsService.saveRegionalMetric(metricsDTO);
        return new ResponseEntity<>(savedMetrics, HttpStatus.CREATED);
    }

    /**
     * Get comparative metrics across different regions.
     *
     * @param metricName Name of the metric to compare
     * @param startTime Start of time range
     * @param endTime End of time range
     * @return Map of region codes to their metric values
     */
    @GetMapping("/compare/{metricName}")
    @Operation(summary = "Compare metrics across regions",
            description = "Returns comparative metrics across different regions")
    public ResponseEntity<Map<String, Double>> getComparativeMetricsAcrossRegions(
            @PathVariable String metricName,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        log.info("Comparing metric {} across regions from {} to {}", metricName, startTime, endTime);
        Map<String, Double> metrics = metricsService.getComparativeMetricsAcrossRegions(
                metricName, startTime, endTime);
        return ResponseEntity.ok(metrics);
    }

    /**
     * Trigger the collection and aggregation of metrics on demand.
     *
     * @return Status message
     */
    @PostMapping("/collect")
    @Operation(summary = "Collect and aggregate metrics",
            description = "Triggers the collection and aggregation of metrics from various services")
    public ResponseEntity<String> collectAndAggregateMetrics() {
        log.info("Manually triggering metrics collection and aggregation");
        metricsService.collectAndAggregateMetrics();
        return ResponseEntity.ok("Metrics collection and aggregation initiated");
    }
}
