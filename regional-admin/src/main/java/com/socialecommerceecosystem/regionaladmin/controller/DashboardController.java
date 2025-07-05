package com.gogidix.courier.regionaladmin.controller;

import com.socialecommerceecosystem.regionaladmin.service.dashboard.PerformanceDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * REST controller for the regional performance dashboard.
 * Provides endpoints for retrieving dashboard data and metrics.
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Performance Dashboard", description = "APIs for regional performance dashboard")
public class DashboardController {

    private final PerformanceDashboardService dashboardService;

    /**
     * Get the dashboard summary for a region.
     *
     * @param regionCode Region code
     * @return Dashboard summary data
     */
    @GetMapping("/summary/{regionCode}")
    @Operation(summary = "Get dashboard summary",
            description = "Returns a summary of dashboard data for a region")
    public ResponseEntity<Map<String, Object>> getDashboardSummary(
            @PathVariable String regionCode) {
        log.info("Getting dashboard summary for region {}", regionCode);
        Map<String, Object> summary = dashboardService.getDashboardSummary(regionCode);
        return ResponseEntity.ok(summary);
    }

    /**
     * Get regional KPIs.
     *
     * @param regionCode Region code
     * @return Map of KPI names to values
     */
    @GetMapping("/kpi/{regionCode}")
    @Operation(summary = "Get regional KPIs",
            description = "Returns key performance indicators for a region")
    public ResponseEntity<Map<String, Double>> getRegionalKPIs(
            @PathVariable String regionCode) {
        log.info("Getting KPIs for region {}", regionCode);
        Map<String, Double> kpis = dashboardService.getRegionalKPIs(regionCode);
        return ResponseEntity.ok(kpis);
    }

    /**
     * Get delivery performance trend.
     *
     * @param regionCode Region code
     * @param startTime Start of time range
     * @param endTime End of time range
     * @param interval Time interval for data points
     * @return Time series data of delivery performance metrics
     */
    @GetMapping("/trend/delivery/{regionCode}")
    @Operation(summary = "Get delivery performance trend",
            description = "Returns time series data of delivery performance metrics")
    public ResponseEntity<Map<LocalDateTime, Double>> getDeliveryPerformanceTrend(
            @PathVariable String regionCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "day") String interval) {
        log.info("Getting delivery performance trend for region {} from {} to {} with interval {}",
                regionCode, startTime, endTime, interval);
        
        Map<LocalDateTime, Double> trend = dashboardService.getDeliveryPerformanceTrend(
                regionCode, startTime, endTime, interval);
        
        return ResponseEntity.ok(trend);
    }

    /**
     * Get driver efficiency trend.
     *
     * @param regionCode Region code
     * @param startTime Start of time range
     * @param endTime End of time range
     * @param interval Time interval for data points
     * @return Time series data of driver efficiency metrics
     */
    @GetMapping("/trend/driver/{regionCode}")
    @Operation(summary = "Get driver efficiency trend",
            description = "Returns time series data of driver efficiency metrics")
    public ResponseEntity<Map<LocalDateTime, Double>> getDriverEfficiencyTrend(
            @PathVariable String regionCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "day") String interval) {
        log.info("Getting driver efficiency trend for region {} from {} to {} with interval {}",
                regionCode, startTime, endTime, interval);
        
        Map<LocalDateTime, Double> trend = dashboardService.getDriverEfficiencyTrend(
                regionCode, startTime, endTime, interval);
        
        return ResponseEntity.ok(trend);
    }

    /**
     * Get financial performance trend.
     *
     * @param regionCode Region code
     * @param startTime Start of time range
     * @param endTime End of time range
     * @param interval Time interval for data points
     * @return Time series data of financial performance metrics
     */
    @GetMapping("/trend/financial/{regionCode}")
    @Operation(summary = "Get financial performance trend",
            description = "Returns time series data of financial performance metrics")
    public ResponseEntity<Map<String, Map<LocalDateTime, Double>>> getFinancialPerformanceTrend(
            @PathVariable String regionCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "day") String interval) {
        log.info("Getting financial performance trend for region {} from {} to {} with interval {}",
                regionCode, startTime, endTime, interval);
        
        Map<String, Map<LocalDateTime, Double>> trend = dashboardService.getFinancialPerformanceTrend(
                regionCode, startTime, endTime, interval);
        
        return ResponseEntity.ok(trend);
    }

    /**
     * Get customer satisfaction trend.
     *
     * @param regionCode Region code
     * @param startTime Start of time range
     * @param endTime End of time range
     * @param interval Time interval for data points
     * @return Time series data of customer satisfaction metrics
     */
    @GetMapping("/trend/satisfaction/{regionCode}")
    @Operation(summary = "Get customer satisfaction trend",
            description = "Returns time series data of customer satisfaction metrics")
    public ResponseEntity<Map<LocalDateTime, Double>> getCustomerSatisfactionTrend(
            @PathVariable String regionCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "day") String interval) {
        log.info("Getting customer satisfaction trend for region {} from {} to {} with interval {}",
                regionCode, startTime, endTime, interval);
        
        Map<LocalDateTime, Double> trend = dashboardService.getCustomerSatisfactionTrend(
                regionCode, startTime, endTime, interval);
        
        return ResponseEntity.ok(trend);
    }

    /**
     * Get top-performing branches in a region.
     *
     * @param regionCode Region code
     * @param metricName Metric to rank by
     * @param limit Number of top branches to return
     * @return List of top-performing branches with their metrics
     */
    @GetMapping("/top-branches/{regionCode}")
    @Operation(summary = "Get top-performing branches",
            description = "Returns a list of top-performing branches in a region")
    public ResponseEntity<List<Map<String, Object>>> getTopPerformingBranches(
            @PathVariable String regionCode,
            @RequestParam(defaultValue = "delivery-success-rate") String metricName,
            @RequestParam(defaultValue = "5") int limit) {
        log.info("Getting top {} performing branches in region {} by {}",
                limit, regionCode, metricName);
        
        List<Map<String, Object>> branches = dashboardService.getTopPerformingBranches(
                regionCode, metricName, limit);
        
        return ResponseEntity.ok(branches);
    }
}
