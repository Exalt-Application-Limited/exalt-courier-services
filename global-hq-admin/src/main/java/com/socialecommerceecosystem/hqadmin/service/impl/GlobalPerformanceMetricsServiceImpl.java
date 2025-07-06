package com.gogidix.courier.courier.hqadmin.service.impl;

import com.socialecommerceecosystem.hqadmin.model.GlobalPerformanceMetrics;
import com.socialecommerceecosystem.hqadmin.model.GlobalRegion;
import com.socialecommerceecosystem.hqadmin.repository.GlobalPerformanceMetricsRepository;
import com.socialecommerceecosystem.hqadmin.repository.GlobalRegionRepository;
import com.socialecommerceecosystem.hqadmin.service.GlobalPerformanceMetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of the GlobalPerformanceMetricsService interface.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class GlobalPerformanceMetricsServiceImpl implements GlobalPerformanceMetricsService {

    private final GlobalPerformanceMetricsRepository globalPerformanceMetricsRepository;
    private final GlobalRegionRepository globalRegionRepository;

    @Override
    public List<GlobalPerformanceMetrics> getAllMetrics() {
        return globalPerformanceMetricsRepository.findAll();
    }

    @Override
    public Optional<GlobalPerformanceMetrics> getMetricById(Long id) {
        return globalPerformanceMetricsRepository.findById(id);
    }

    @Override
    @Transactional
    public GlobalPerformanceMetrics createMetric(GlobalPerformanceMetrics metric) {
        log.info("Creating new global performance metric for date: {} and region: {}", 
                 metric.getMetricDate(), 
                 metric.getGlobalRegion() != null ? metric.getGlobalRegion().getName() : "Global");
        
        // Verify that the global region exists if provided
        if (metric.getGlobalRegion() != null && metric.getGlobalRegion().getId() != null) {
            GlobalRegion region = globalRegionRepository.findById(metric.getGlobalRegion().getId())
                .orElseThrow(() -> new IllegalArgumentException("Global region not found with id: " + metric.getGlobalRegion().getId()));
            metric.setGlobalRegion(region);
        }
        
        return globalPerformanceMetricsRepository.save(metric);
    }

    @Override
    @Transactional
    public GlobalPerformanceMetrics updateMetric(Long id, GlobalPerformanceMetrics metricDetails) {
        log.info("Updating global performance metric with id: {}", id);
        
        GlobalPerformanceMetrics existingMetric = globalPerformanceMetricsRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Global performance metric not found with id: " + id));
        
        // Update fields
        existingMetric.setMetricDate(metricDetails.getMetricDate());
        existingMetric.setMetricType(metricDetails.getMetricType());
        existingMetric.setMetricCategory(metricDetails.getMetricCategory());
        existingMetric.setTotalDeliveries(metricDetails.getTotalDeliveries());
        existingMetric.setSuccessfulDeliveries(metricDetails.getSuccessfulDeliveries());
        existingMetric.setFailedDeliveries(metricDetails.getFailedDeliveries());
        existingMetric.setOnTimeDeliveries(metricDetails.getOnTimeDeliveries());
        existingMetric.setDelayedDeliveries(metricDetails.getDelayedDeliveries());
        existingMetric.setAverageDeliveryTimeMinutes(metricDetails.getAverageDeliveryTimeMinutes());
        existingMetric.setMedianDeliveryTimeMinutes(metricDetails.getMedianDeliveryTimeMinutes());
        existingMetric.setAverageDistanceKm(metricDetails.getAverageDistanceKm());
        existingMetric.setAverageRating(metricDetails.getAverageRating());
        existingMetric.setTotalRatings(metricDetails.getTotalRatings());
        existingMetric.setCustomerComplaints(metricDetails.getCustomerComplaints());
        existingMetric.setTotalRevenue(metricDetails.getTotalRevenue());
        existingMetric.setTotalCost(metricDetails.getTotalCost());
        existingMetric.setProfitMargin(metricDetails.getProfitMargin());
        existingMetric.setActiveCouriers(metricDetails.getActiveCouriers());
        existingMetric.setAverageDeliveriesPerCourier(metricDetails.getAverageDeliveriesPerCourier());
        existingMetric.setCourierUtilizationPercentage(metricDetails.getCourierUtilizationPercentage());
        existingMetric.setWalkInCustomers(metricDetails.getWalkInCustomers());
        existingMetric.setWalkInShipments(metricDetails.getWalkInShipments());
        existingMetric.setTotalRequestsRouted(metricDetails.getTotalRequestsRouted());
        existingMetric.setRoutingSuccessRate(metricDetails.getRoutingSuccessRate());
        
        // Update global region if provided
        if (metricDetails.getGlobalRegion() != null && metricDetails.getGlobalRegion().getId() != null) {
            GlobalRegion newRegion = globalRegionRepository.findById(metricDetails.getGlobalRegion().getId())
                .orElseThrow(() -> new IllegalArgumentException("Global region not found with id: " + metricDetails.getGlobalRegion().getId()));
            existingMetric.setGlobalRegion(newRegion);
        } else {
            existingMetric.setGlobalRegion(null);
        }
        
        return globalPerformanceMetricsRepository.save(existingMetric);
    }

    @Override
    @Transactional
    public void deleteMetric(Long id) {
        log.info("Deleting global performance metric with id: {}", id);
        
        GlobalPerformanceMetrics metric = globalPerformanceMetricsRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Global performance metric not found with id: " + id));
        
        globalPerformanceMetricsRepository.delete(metric);
    }

    @Override
    public List<GlobalPerformanceMetrics> getMetricsByDate(LocalDate date) {
        return globalPerformanceMetricsRepository.findByMetricDate(date);
    }

    @Override
    public List<GlobalPerformanceMetrics> getMetricsByRegion(Long regionId) {
        log.debug("Getting performance metrics for region id: {}", regionId);
        
        GlobalRegion region = globalRegionRepository.findById(regionId)
            .orElseThrow(() -> new IllegalArgumentException("Global region not found with id: " + regionId));
        
        return globalPerformanceMetricsRepository.findByGlobalRegion(region);
    }

    @Override
    public List<GlobalPerformanceMetrics> getMetricsByType(String metricType) {
        return globalPerformanceMetricsRepository.findByMetricType(metricType);
    }

    @Override
    public List<GlobalPerformanceMetrics> getMetricsByDateRange(LocalDate startDate, LocalDate endDate) {
        return globalPerformanceMetricsRepository.findByMetricDateBetween(startDate, endDate);
    }

    @Override
    public List<GlobalPerformanceMetrics> getMetricsByRegionAndDateRange(
            Long regionId, LocalDate startDate, LocalDate endDate) {
        log.debug("Getting performance metrics for region id: {} between dates {} and {}", 
                 regionId, startDate, endDate);
        
        GlobalRegion region = globalRegionRepository.findById(regionId)
            .orElseThrow(() -> new IllegalArgumentException("Global region not found with id: " + regionId));
        
        return globalPerformanceMetricsRepository.findByGlobalRegionAndMetricDateBetween(region, startDate, endDate);
    }

    @Override
    public List<GlobalPerformanceMetrics> getMetricsWithHighRating(BigDecimal ratingThreshold) {
        return globalPerformanceMetricsRepository.findByAverageRatingGreaterThanEqual(ratingThreshold);
    }

    @Override
    public List<GlobalPerformanceMetrics> getMetricsWithHighOnTimePercentage(float threshold) {
        return globalPerformanceMetricsRepository.findByOnTimePercentageAbove(threshold);
    }

    @Override
    public List<GlobalPerformanceMetrics> getMetricsWithHighProfitMargin(BigDecimal marginThreshold) {
        return globalPerformanceMetricsRepository.findByProfitMarginGreaterThanEqual(marginThreshold);
    }

    @Override
    public Map<String, Object> aggregateMetricsForDateRange(LocalDate startDate, LocalDate endDate) {
        log.debug("Aggregating performance metrics between dates {} and {}", startDate, endDate);
        
        Object aggregateResult = globalPerformanceMetricsRepository.aggregateMetricsForDateRange(startDate, endDate);
        Map<String, Object> result = new HashMap<>();
        
        if (aggregateResult != null) {
            Object[] resultArray = (Object[]) aggregateResult;
            result.put("averageRating", resultArray[0]);
            result.put("averageDeliveryTimeMinutes", resultArray[1]);
            result.put("averageProfitMargin", resultArray[2]);
            result.put("totalDeliveries", resultArray[3]);
            result.put("successfulDeliveries", resultArray[4]);
            result.put("failedDeliveries", resultArray[5]);
            result.put("walkInCustomers", resultArray[6]);
            result.put("averageRoutingSuccessRate", resultArray[7]);
            
            // Calculate derived metrics
            if (resultArray[3] != null && resultArray[4] != null) {
                BigDecimal totalDeliveries = new BigDecimal(resultArray[3].toString());
                BigDecimal successfulDeliveries = new BigDecimal(resultArray[4].toString());
                
                if (totalDeliveries.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal successRate = successfulDeliveries
                        .multiply(BigDecimal.valueOf(100))
                        .divide(totalDeliveries, 2, RoundingMode.HALF_UP);
                    result.put("successRate", successRate);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<Map<String, Object>> getMetricsTrendByDate(Long regionId, LocalDate startDate, LocalDate endDate) {
        log.debug("Getting metrics trend for region id: {} between dates {} and {}", 
                 regionId, startDate, endDate);
        
        GlobalRegion region = globalRegionRepository.findById(regionId)
            .orElseThrow(() -> new IllegalArgumentException("Global region not found with id: " + regionId));
        
        List<Object[]> trendResults = globalPerformanceMetricsRepository.getMetricsTrendByDate(region, startDate, endDate);
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (Object[] trendData : trendResults) {
            Map<String, Object> dailyTrend = new HashMap<>();
            dailyTrend.put("date", trendData[0]);
            dailyTrend.put("totalDeliveries", trendData[1]);
            dailyTrend.put("successfulDeliveries", trendData[2]);
            dailyTrend.put("averageRating", trendData[3]);
            dailyTrend.put("totalRevenue", trendData[4]);
            dailyTrend.put("totalCost", trendData[5]);
            dailyTrend.put("averageProfitMargin", trendData[6]);
            
            result.add(dailyTrend);
        }
        
        return result;
    }

    @Override
    public List<GlobalPerformanceMetrics> getMetricsForHighWalkInCustomers(Integer threshold) {
        return globalPerformanceMetricsRepository.findByWalkInCustomersGreaterThanEqual(threshold);
    }

    @Override
    @Transactional
    public List<GlobalPerformanceMetrics> importMetricsFromRegionalSystems(LocalDate date) {
        log.info("Importing performance metrics from regional systems for date: {}", date);
        
        // In a real implementation, this would call regional admin systems to fetch metrics
        // For now, we'll return an empty list as this is just a placeholder
        log.warn("This is a placeholder implementation. No actual import is performed.");
        return new ArrayList<>();
    }

    @Override
    public List<Map<String, Object>> compareRegionsForDate(LocalDate date) {
        log.debug("Comparing regions for date: {}", date);
        
        List<Object[]> comparisonResults = globalPerformanceMetricsRepository.compareRegionsForDate(date);
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (Object[] comparisonData : comparisonResults) {
            Map<String, Object> regionComparison = new HashMap<>();
            regionComparison.put("regionName", comparisonData[0]);
            regionComparison.put("totalDeliveries", comparisonData[1]);
            regionComparison.put("successfulDeliveries", comparisonData[2]);
            regionComparison.put("averageRating", comparisonData[3]);
            regionComparison.put("walkInCustomers", comparisonData[4]);
            regionComparison.put("routingSuccessRate", comparisonData[5]);
            
            // Calculate success rate
            if (comparisonData[1] != null && comparisonData[2] != null) {
                Long totalDeliveries = ((Number) comparisonData[1]).longValue();
                Long successfulDeliveries = ((Number) comparisonData[2]).longValue();
                
                if (totalDeliveries > 0) {
                    BigDecimal successRate = BigDecimal.valueOf(successfulDeliveries)
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(totalDeliveries), 2, RoundingMode.HALF_UP);
                    regionComparison.put("successRate", successRate);
                }
            }
            
            result.add(regionComparison);
        }
        
        return result;
    }

    @Override
    public Map<String, Object> calculateKPIsForDateRange(LocalDate startDate, LocalDate endDate) {
        log.debug("Calculating KPIs for date range: {} to {}", startDate, endDate);
        
        // Get aggregate metrics first
        Map<String, Object> aggregateMetrics = aggregateMetricsForDateRange(startDate, endDate);
        Map<String, Object> kpis = new HashMap<>(aggregateMetrics);
        
        // Calculate additional KPIs
        
        // 1. Average revenue per delivery
        if (aggregateMetrics.containsKey("totalDeliveries") && aggregateMetrics.get("totalDeliveries") != null) {
            List<GlobalPerformanceMetrics> metricsInRange = getMetricsByDateRange(startDate, endDate);
            BigDecimal totalRevenue = metricsInRange.stream()
                .map(m -> m.getTotalRevenue() != null ? m.getTotalRevenue() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
                
            Number totalDeliveries = (Number) aggregateMetrics.get("totalDeliveries");
            if (totalDeliveries.longValue() > 0) {
                BigDecimal avgRevenuePerDelivery = totalRevenue.divide(
                    BigDecimal.valueOf(totalDeliveries.longValue()), 
                    2, RoundingMode.HALF_UP);
                kpis.put("averageRevenuePerDelivery", avgRevenuePerDelivery);
            }
        }
        
        // 2. Customer complaint rate
        List<GlobalPerformanceMetrics> metricsInRange = getMetricsByDateRange(startDate, endDate);
        Integer totalComplaints = metricsInRange.stream()
            .map(m -> m.getCustomerComplaints() != null ? m.getCustomerComplaints() : 0)
            .reduce(0, Integer::sum);
            
        if (aggregateMetrics.containsKey("totalDeliveries") && aggregateMetrics.get("totalDeliveries") != null) {
            Number totalDeliveries = (Number) aggregateMetrics.get("totalDeliveries");
            if (totalDeliveries.longValue() > 0) {
                BigDecimal complaintRate = BigDecimal.valueOf(totalComplaints)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalDeliveries.longValue()), 2, RoundingMode.HALF_UP);
                kpis.put("complaintRate", complaintRate);
            }
        }
        
        // 3. Percentage of walk-in customers
        Integer totalWalkIns = metricsInRange.stream()
            .map(m -> m.getWalkInCustomers() != null ? m.getWalkInCustomers() : 0)
            .reduce(0, Integer::sum);
            
        Integer totalWalkInShipments = metricsInRange.stream()
            .map(m -> m.getWalkInShipments() != null ? m.getWalkInShipments() : 0)
            .reduce(0, Integer::sum);
            
        if (aggregateMetrics.containsKey("totalDeliveries") && aggregateMetrics.get("totalDeliveries") != null) {
            Number totalDeliveries = (Number) aggregateMetrics.get("totalDeliveries");
            if (totalDeliveries.longValue() > 0 && totalWalkInShipments > 0) {
                BigDecimal walkInPercentage = BigDecimal.valueOf(totalWalkInShipments)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalDeliveries.longValue()), 2, RoundingMode.HALF_UP);
                kpis.put("walkInPercentage", walkInPercentage);
            }
        }
        
        kpis.put("totalWalkInCustomers", totalWalkIns);
        kpis.put("totalWalkInShipments", totalWalkInShipments);
        kpis.put("totalComplaints", totalComplaints);
        
        return kpis;
    }
}
