package com.exalt.courier.regionaladmin.service.dashboard.impl;

import com.socialecommerceecosystem.regionaladmin.model.RegionalMetrics;
import com.socialecommerceecosystem.regionaladmin.repository.RegionalMetricsRepository;
import com.socialecommerceecosystem.regionaladmin.service.dashboard.PerformanceDashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the PerformanceDashboardService interface.
 * Provides methods for retrieving performance data for dashboard display.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PerformanceDashboardServiceImpl implements PerformanceDashboardService {

    private final RegionalMetricsRepository metricsRepository;
    private final RestTemplate restTemplate;

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<LocalDateTime, Double> getDeliveryPerformanceTrend(
            String regionCode, LocalDateTime startTime, LocalDateTime endTime, String interval) {
        log.info("Getting delivery performance trend for region {} from {} to {} with interval {}",
                regionCode, startTime, endTime, interval);

        // Get all delivery metrics for the region in the time range
        List<RegionalMetrics> metrics = metricsRepository.findMetricsForRegionInTimeRange(
                regionCode, startTime, endTime);

        // Filter for delivery-related metrics
        List<RegionalMetrics> deliveryMetrics = metrics.stream()
                .filter(m -> 
                    "delivery-time".equals(m.getMetricName()) || 
                    "delivery-success-rate".equals(m.getMetricName()))
                .collect(Collectors.toList());

        // Convert to time series data with the given interval
        Map<LocalDateTime, Double> timeSeriesData = aggregateTimeSeriesData(
                deliveryMetrics, startTime, endTime, interval);

        return timeSeriesData;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<LocalDateTime, Double> getDriverEfficiencyTrend(
            String regionCode, LocalDateTime startTime, LocalDateTime endTime, String interval) {
        log.info("Getting driver efficiency trend for region {} from {} to {} with interval {}",
                regionCode, startTime, endTime, interval);

        // Get all metrics for the region in the time range
        List<RegionalMetrics> metrics = metricsRepository.findMetricsForRegionInTimeRange(
                regionCode, startTime, endTime);

        // Filter for driver-related metrics
        List<RegionalMetrics> driverMetrics = metrics.stream()
                .filter(m -> 
                    "driver-utilization".equals(m.getMetricName()) ||
                    "driver-productivity".equals(m.getMetricName()) ||
                    "driver-efficiency".equals(m.getMetricName()))
                .collect(Collectors.toList());

        // Convert to time series data with the given interval
        Map<LocalDateTime, Double> timeSeriesData = aggregateTimeSeriesData(
                driverMetrics, startTime, endTime, interval);

        return timeSeriesData;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Map<LocalDateTime, Double>> getFinancialPerformanceTrend(
            String regionCode, LocalDateTime startTime, LocalDateTime endTime, String interval) {
        log.info("Getting financial performance trend for region {} from {} to {} with interval {}",
                regionCode, startTime, endTime, interval);

        // Get all metrics for the region in the time range
        List<RegionalMetrics> metrics = metricsRepository.findMetricsForRegionInTimeRange(
                regionCode, startTime, endTime);

        // Filter for financial metrics
        List<RegionalMetrics> revenueMetrics = metrics.stream()
                .filter(m -> "revenue".equals(m.getMetricName()))
                .collect(Collectors.toList());

        List<RegionalMetrics> costMetrics = metrics.stream()
                .filter(m -> "operational-cost".equals(m.getMetricName()))
                .collect(Collectors.toList());

        // Convert to time series data with the given interval
        Map<LocalDateTime, Double> revenueData = aggregateTimeSeriesData(
                revenueMetrics, startTime, endTime, interval);

        Map<LocalDateTime, Double> costData = aggregateTimeSeriesData(
                costMetrics, startTime, endTime, interval);

        // Combine into a single result
        Map<String, Map<LocalDateTime, Double>> result = new HashMap<>();
        result.put("revenue", revenueData);
        result.put("cost", costData);

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<LocalDateTime, Double> getCustomerSatisfactionTrend(
            String regionCode, LocalDateTime startTime, LocalDateTime endTime, String interval) {
        log.info("Getting customer satisfaction trend for region {} from {} to {} with interval {}",
                regionCode, startTime, endTime, interval);

        // Get all metrics for the region in the time range
        List<RegionalMetrics> metrics = metricsRepository.findMetricsForRegionInTimeRange(
                regionCode, startTime, endTime);

        // Filter for customer satisfaction metrics
        List<RegionalMetrics> satisfactionMetrics = metrics.stream()
                .filter(m -> "customer-satisfaction".equals(m.getMetricName()))
                .collect(Collectors.toList());

        // Convert to time series data with the given interval
        Map<LocalDateTime, Double> timeSeriesData = aggregateTimeSeriesData(
                satisfactionMetrics, startTime, endTime, interval);

        return timeSeriesData;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Map<String, Object>> getTopPerformingBranches(
            String regionCode, String metricName, int limit) {
        log.info("Getting top {} performing branches in region {} by {}",
                limit, regionCode, metricName);

        // In a real implementation, this would query branch data
        // For now, we'll generate some sample data
        List<Map<String, Object>> results = new ArrayList<>();

        for (int i = 1; i <= limit; i++) {
            Map<String, Object> branchData = new HashMap<>();
            branchData.put("branchId", "branch-" + i);
            branchData.put("branchName", "Branch " + i);
            branchData.put("metricValue", 90.0 - (i * 2.5)); // Descending values
            results.add(branchData);
        }

        return results;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> getDashboardSummary(String regionCode) {
        log.info("Getting dashboard summary for region {}", regionCode);

        Map<String, Object> summary = new HashMap<>();

        // Get the latest metrics for different categories
        List<RegionalMetrics> deliveryMetrics = metricsRepository.findMostRecentMetricsByRegionAndCategory(
                regionCode, "DELIVERY");
        
        List<RegionalMetrics> financialMetrics = metricsRepository.findMostRecentMetricsByRegionAndCategory(
                regionCode, "FINANCIAL");
        
        List<RegionalMetrics> operationalMetrics = metricsRepository.findMostRecentMetricsByRegionAndCategory(
                regionCode, "OPERATIONAL");
        
        List<RegionalMetrics> customerMetrics = metricsRepository.findMostRecentMetricsByRegionAndCategory(
                regionCode, "CUSTOMER_SATISFACTION");

        // Add metrics to summary
        summary.put("deliveryMetrics", deliveryMetrics.stream()
                .collect(Collectors.toMap(RegionalMetrics::getMetricName, RegionalMetrics::getMetricValue)));
        
        summary.put("financialMetrics", financialMetrics.stream()
                .collect(Collectors.toMap(RegionalMetrics::getMetricName, RegionalMetrics::getMetricValue)));
        
        summary.put("operationalMetrics", operationalMetrics.stream()
                .collect(Collectors.toMap(RegionalMetrics::getMetricName, RegionalMetrics::getMetricValue)));
        
        summary.put("customerMetrics", customerMetrics.stream()
                .collect(Collectors.toMap(RegionalMetrics::getMetricName, RegionalMetrics::getMetricValue)));

        // Add region metadata
        summary.put("regionCode", regionCode);
        summary.put("regionName", getRegionNameFromCode(regionCode));
        summary.put("lastUpdated", LocalDateTime.now());
        summary.put("branchCount", 10); // Sample branch count

        return summary;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Double> getRegionalKPIs(String regionCode) {
        log.info("Getting regional KPIs for region {}", regionCode);

        Map<String, Double> kpis = new HashMap<>();

        // Get the latest metrics for KPIs
        List<RegionalMetrics> metrics = metricsRepository.findByRegionCode(regionCode);

        // Add specific KPIs
        metrics.stream()
                .filter(m -> isKpiMetric(m.getMetricName()))
                .forEach(m -> kpis.put(m.getMetricName(), m.getMetricValue()));

        return kpis;
    }

    /**
     * Helper method to determine if a metric is a KPI.
     */
    private boolean isKpiMetric(String metricName) {
        List<String> kpiMetrics = List.of(
                "delivery-success-rate", "customer-satisfaction", 
                "driver-utilization", "revenue", "operational-cost"
        );
        return kpiMetrics.contains(metricName);
    }

    /**
     * Helper method to aggregate time series data.
     */
    private Map<LocalDateTime, Double> aggregateTimeSeriesData(
            List<RegionalMetrics> metrics, LocalDateTime startTime, 
            LocalDateTime endTime, String interval) {
        
        // Determine time unit based on interval
        ChronoUnit timeUnit = parseTimeUnit(interval);
        long intervalCount = timeUnit.between(startTime, endTime);
        
        // Initialize result map with time points
        Map<LocalDateTime, Double> result = new TreeMap<>();
        for (long i = 0; i <= intervalCount; i++) {
            LocalDateTime timePoint = startTime.plus(i, timeUnit);
            if (timePoint.isAfter(endTime)) break;
            result.put(timePoint, 0.0);
        }
        
        // For each metric, add it to the appropriate time bucket
        for (RegionalMetrics metric : metrics) {
            LocalDateTime metricTime = metric.getDataTimestamp();
            if (metricTime.isBefore(startTime) || metricTime.isAfter(endTime)) {
                continue;
            }
            
            // Find the closest time point in our result map
            LocalDateTime bucket = startTime;
            for (long i = 0; i <= intervalCount; i++) {
                LocalDateTime timePoint = startTime.plus(i, timeUnit);
                if (timePoint.isAfter(endTime)) break;
                
                if (!metricTime.isBefore(timePoint) && 
                    (i == intervalCount || metricTime.isBefore(startTime.plus(i + 1, timeUnit)))) {
                    bucket = timePoint;
                    break;
                }
            }
            
            // Add the metric value to the bucket
            result.put(bucket, result.getOrDefault(bucket, 0.0) + metric.getMetricValue());
        }
        
        return result;
    }

    /**
     * Helper method to parse time unit from interval string.
     */
    private ChronoUnit parseTimeUnit(String interval) {
        switch (interval.toLowerCase()) {
            case "minute":
                return ChronoUnit.MINUTES;
            case "hour":
                return ChronoUnit.HOURS;
            case "day":
                return ChronoUnit.DAYS;
            case "week":
                return ChronoUnit.WEEKS;
            case "month":
                return ChronoUnit.MONTHS;
            default:
                return ChronoUnit.DAYS; // Default to days
        }
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
