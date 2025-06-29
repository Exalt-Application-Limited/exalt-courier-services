package com.exalt.courier.regionaladmin.service.impl;

import com.socialecommerceecosystem.regionaladmin.service.CrossServiceAggregationService;
import com.socialecommerceecosystem.regionaladmin.service.MetricsAggregationService;
import com.socialecommerceecosystem.regionaladmin.service.dashboard.PerformanceDashboardService;
import com.socialecommerceecosystem.regionaladmin.service.integration.ReportingIntegrationService;
import com.socialecommerceecosystem.regionaladmin.service.integration.TracingIntegrationService;
import com.socialecommerceecosystem.regionaladmin.service.integration.TrackingIntegrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Implementation of the CrossServiceAggregationService interface.
 * Aggregates data from multiple services to create comprehensive views.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CrossServiceAggregationServiceImpl implements CrossServiceAggregationService {

    private final MetricsAggregationService metricsService;
    private final PerformanceDashboardService dashboardService;
    private final TrackingIntegrationService trackingService;
    private final ReportingIntegrationService reportingService;
    private final TracingIntegrationService tracingService;

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "regionalOverview", key = "#regionCode", unless = "#result == null")
    public Map<String, Object> getRegionalOverview(String regionCode) {
        log.info("Aggregating regional overview data for region {}", regionCode);
        Map<String, Object> result = new HashMap<>();

        // Use CompletableFuture to parallelize service calls
        CompletableFuture<Map<String, Object>> dashboardFuture = CompletableFuture
                .supplyAsync(() -> dashboardService.getDashboardSummary(regionCode));
        
        CompletableFuture<Map<String, Double>> kpisFuture = CompletableFuture
                .supplyAsync(() -> dashboardService.getRegionalKPIs(regionCode));
        
        CompletableFuture<Map<String, Object>> trackingFuture = CompletableFuture
                .supplyAsync(() -> trackingService.getCurrentTrackingStatus(regionCode));
        
        CompletableFuture<Map<String, Object>> tracingFuture = CompletableFuture
                .supplyAsync(() -> tracingService.getServiceTraceSummary(regionCode));

        try {
            // Wait for all futures to complete and combine results
            result.put("dashboardSummary", dashboardFuture.get());
            result.put("kpis", kpisFuture.get());
            result.put("tracking", trackingFuture.get());
            result.put("tracing", tracingFuture.get());
            
            // Add region metadata
            result.put("regionCode", regionCode);
            result.put("regionName", getRegionNameFromCode(regionCode));
            result.put("timestamp", LocalDateTime.now());
            
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error aggregating regional overview data: {}", e.getMessage());
            Thread.currentThread().interrupt();
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> getPerformanceMetricsTimeSeries(
            String regionCode, LocalDateTime startTime, LocalDateTime endTime, String interval) {
        log.info("Aggregating performance metrics time series for region {} from {} to {} with interval {}",
                regionCode, startTime, endTime, interval);
        
        Map<String, Object> result = new HashMap<>();
        
        // Get delivery performance trend
        Map<LocalDateTime, Double> deliveryTrend = dashboardService.getDeliveryPerformanceTrend(
                regionCode, startTime, endTime, interval);
        
        // Get driver efficiency trend
        Map<LocalDateTime, Double> driverTrend = dashboardService.getDriverEfficiencyTrend(
                regionCode, startTime, endTime, interval);
        
        // Get financial performance trend
        Map<String, Map<LocalDateTime, Double>> financialTrend = dashboardService.getFinancialPerformanceTrend(
                regionCode, startTime, endTime, interval);
        
        // Get customer satisfaction trend
        Map<LocalDateTime, Double> satisfactionTrend = dashboardService.getCustomerSatisfactionTrend(
                regionCode, startTime, endTime, interval);
        
        // Combine results
        result.put("deliveryPerformance", deliveryTrend);
        result.put("driverEfficiency", driverTrend);
        result.put("financialPerformance", financialTrend);
        result.put("customerSatisfaction", satisfactionTrend);
        
        // Add metadata
        result.put("regionCode", regionCode);
        result.put("startTime", startTime);
        result.put("endTime", endTime);
        result.put("interval", interval);
        
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> getServiceStatusOverview(String regionCode) {
        log.info("Aggregating service status overview for region {}", regionCode);
        
        Map<String, Object> result = new HashMap<>();
        
        // Get service trace summary from tracing service
        Map<String, Object> traceSummary = tracingService.getServiceTraceSummary(regionCode);
        
        // Get latency metrics from tracing service
        Map<String, Object> latencyMetrics = tracingService.getLatencyMetrics(regionCode);
        
        // Combine results
        result.put("serviceTraces", traceSummary);
        result.put("latencyMetrics", latencyMetrics);
        
        // Add metadata
        result.put("regionCode", regionCode);
        result.put("timestamp", LocalDateTime.now());
        
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "operationalMetrics", key = "#regionCode", unless = "#result == null")
    public Map<String, Object> getOperationalMetrics(String regionCode) {
        log.info("Aggregating operational metrics for region {}", regionCode);
        
        Map<String, Object> result = new HashMap<>();
        
        // Get dashboard summary for operational metrics
        Map<String, Object> dashboardSummary = dashboardService.getDashboardSummary(regionCode);
        if (dashboardSummary.containsKey("operationalMetrics")) {
            result.put("metrics", dashboardSummary.get("operationalMetrics"));
        }
        
        // Get active delivery count from tracking service
        int activeDeliveries = trackingService.getActiveDeliveryCount(regionCode);
        result.put("activeDeliveries", activeDeliveries);
        
        // Get tracking status summary
        Map<String, Integer> trackingStatusSummary = trackingService.getTrackingStatusSummary(regionCode);
        result.put("trackingStatusSummary", trackingStatusSummary);
        
        // Add metadata
        result.put("regionCode", regionCode);
        result.put("timestamp", LocalDateTime.now());
        
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "financialMetrics", key = "#regionCode", unless = "#result == null")
    public Map<String, Object> getFinancialMetrics(String regionCode) {
        log.info("Aggregating financial metrics for region {}", regionCode);
        
        Map<String, Object> result = new HashMap<>();
        
        // Get dashboard summary for financial metrics
        Map<String, Object> dashboardSummary = dashboardService.getDashboardSummary(regionCode);
        if (dashboardSummary.containsKey("financialMetrics")) {
            result.put("metrics", dashboardSummary.get("financialMetrics"));
        }
        
        // Add metadata
        result.put("regionCode", regionCode);
        result.put("timestamp", LocalDateTime.now());
        
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "customerMetrics", key = "#regionCode", unless = "#result == null")
    public Map<String, Object> getCustomerMetrics(String regionCode) {
        log.info("Aggregating customer metrics for region {}", regionCode);
        
        Map<String, Object> result = new HashMap<>();
        
        // Get dashboard summary for customer metrics
        Map<String, Object> dashboardSummary = dashboardService.getDashboardSummary(regionCode);
        if (dashboardSummary.containsKey("customerMetrics")) {
            result.put("metrics", dashboardSummary.get("customerMetrics"));
        }
        
        // Add metadata
        result.put("regionCode", regionCode);
        result.put("timestamp", LocalDateTime.now());
        
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Double> getRegionComparison(String metricName) {
        log.info("Getting region comparison for metric {}", metricName);
        
        // Get current time and one week ago
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneWeekAgo = now.minusWeeks(1);
        
        // Get comparative metrics across regions
        return metricsService.getComparativeMetricsAcrossRegions(
                metricName, oneWeekAgo, now);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "deliveryMetrics", key = "#regionCode", unless = "#result == null")
    public Map<String, Object> getDeliveryMetrics(String regionCode) {
        log.info("Aggregating delivery metrics for region {}", regionCode);
        
        Map<String, Object> result = new HashMap<>();
        
        // Get dashboard summary for delivery metrics
        Map<String, Object> dashboardSummary = dashboardService.getDashboardSummary(regionCode);
        if (dashboardSummary.containsKey("deliveryMetrics")) {
            result.put("metrics", dashboardSummary.get("deliveryMetrics"));
        }
        
        // Get tracking status from tracking service
        Map<String, Object> trackingStatus = trackingService.getCurrentTrackingStatus(regionCode);
        result.put("trackingStatus", trackingStatus);
        
        // Get recent tracking events
        List<Map<String, Object>> trackingEvents = trackingService.getTrackingEvents(regionCode, 10);
        result.put("recentEvents", trackingEvents);
        
        // Add metadata
        result.put("regionCode", regionCode);
        result.put("timestamp", LocalDateTime.now());
        
        return result;
    }

    /**
     * Helper method to get region name from region code.
     */
    private String getRegionNameFromCode(String regionCode) {
        // This would typically look up the region name from a database or config
        // For now, we'll just return a formatted version of the code
        return "Region " + regionCode.toUpperCase();
    }
}