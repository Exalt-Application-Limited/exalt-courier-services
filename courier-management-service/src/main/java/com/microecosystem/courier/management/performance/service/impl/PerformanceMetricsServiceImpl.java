package com.exalt.courierservices.management.$1;

import com.exalt.courier.management.courier.model.Courier;
import com.exalt.courier.management.courier.repository.CourierRepository;
import com.exalt.courier.management.exception.BusinessException;
import com.exalt.courier.management.exception.ResourceNotFoundException;
import com.exalt.courier.management.performance.dto.PerformanceMetricDTO;
import com.exalt.courier.management.performance.mapper.PerformanceMetricMapper;
import com.exalt.courier.management.performance.model.MetricType;
import com.exalt.courier.management.performance.model.PerformanceMetric;
import com.exalt.courier.management.performance.repository.PerformanceMetricRepository;
import com.exalt.courier.management.performance.service.PerformanceMetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementation of the PerformanceMetricsService interface.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PerformanceMetricsServiceImpl implements PerformanceMetricsService {

    private final PerformanceMetricRepository metricRepository;
    private final CourierRepository courierRepository;
    private final PerformanceMetricMapper metricMapper;

    @Override
    @Transactional
    public PerformanceMetricDTO createMetric(PerformanceMetricDTO metricDTO) {
        log.info("Creating new performance metric for courier ID: {}", metricDTO.getCourierId());
        
        Courier courier = getCourierById(metricDTO.getCourierId());
        
        PerformanceMetric metric = metricMapper.toEntity(metricDTO);
        metric.setCourier(courier);
        metric.setMetricId(UUID.randomUUID().toString());
        
        // Calculate if target is met
        if (metric.getTargetValue() != null && metric.getValue() != null) {
            boolean isTargetMet = metric.getValue() >= metric.getTargetValue();
            metric.setIsTargetMet(isTargetMet);
        }
        
        PerformanceMetric savedMetric = metricRepository.save(metric);
        return metricMapper.toDto(savedMetric);
    }

    @Override
    @Transactional
    public PerformanceMetricDTO updateMetric(String metricId, PerformanceMetricDTO metricDTO) {
        log.info("Updating performance metric with ID: {}", metricId);
        
        PerformanceMetric existingMetric = metricRepository.findByMetricId(metricId)
                .orElseThrow(() -> new ResourceNotFoundException("Performance metric not found with ID: " + metricId));
        
        // Update fields from DTO
        existingMetric.setMetricType(metricDTO.getMetricType());
        existingMetric.setDate(metricDTO.getDate());
        existingMetric.setValue(metricDTO.getValue());
        existingMetric.setDescription(metricDTO.getDescription());
        existingMetric.setTargetValue(metricDTO.getTargetValue());
        
        // Recalculate if target is met
        if (existingMetric.getTargetValue() != null && existingMetric.getValue() != null) {
            boolean isTargetMet = existingMetric.getValue() >= existingMetric.getTargetValue();
            existingMetric.setIsTargetMet(isTargetMet);
        }
        
        // Update courier if changed
        if (!existingMetric.getCourier().getId().equals(metricDTO.getCourierId())) {
            Courier courier = getCourierById(metricDTO.getCourierId());
            existingMetric.setCourier(courier);
        }
        
        PerformanceMetric updatedMetric = metricRepository.save(existingMetric);
        return metricMapper.toDto(updatedMetric);
    }

    @Override
    public Optional<PerformanceMetricDTO> getMetricById(String metricId) {
        return metricRepository.findByMetricId(metricId).map(metricMapper::toDto);
    }

    @Override
    @Transactional
    public boolean deleteMetric(String metricId) {
        log.info("Deleting performance metric with ID: {}", metricId);
        
        return metricRepository.findByMetricId(metricId)
                .map(metric -> {
                    metricRepository.delete(metric);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public List<PerformanceMetricDTO> getMetricsByCourier(String courierId) {
        log.info("Getting all performance metrics for courier ID: {}", courierId);
        
        Courier courier = getCourierById(courierId);
        List<PerformanceMetric> metrics = metricRepository.findByCourier(courier);
        
        return metrics.stream()
                .map(metricMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<PerformanceMetricDTO> getMetricsByCourier(String courierId, Pageable pageable) {
        log.info("Getting paged performance metrics for courier ID: {}", courierId);
        
        Courier courier = getCourierById(courierId);
        Page<PerformanceMetric> metricsPage = metricRepository.findByCourier(courier, pageable);
        
        List<PerformanceMetricDTO> dtos = metricsPage.getContent().stream()
                .map(metricMapper::toDto)
                .collect(Collectors.toList());
        
        return new PageImpl<>(dtos, pageable, metricsPage.getTotalElements());
    }

    @Override
    public List<PerformanceMetricDTO> getMetricsByCourierAndType(String courierId, MetricType metricType) {
        log.info("Getting performance metrics for courier ID: {} and type: {}", courierId, metricType);
        
        Courier courier = getCourierById(courierId);
        List<PerformanceMetric> metrics = metricRepository.findByCourierAndMetricType(courier, metricType);
        
        return metrics.stream()
                .map(metricMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PerformanceMetricDTO> getMetricsForDateRange(LocalDate startDate, LocalDate endDate) {
        log.info("Getting performance metrics for date range: {} to {}", startDate, endDate);
        
        validateDateRange(startDate, endDate);
        
        List<PerformanceMetric> metrics = metricRepository.findByDateBetween(startDate, endDate);
        
        return metrics.stream()
                .map(metricMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PerformanceMetricDTO> getMetricsByCourierAndDateRange(
            String courierId, LocalDate startDate, LocalDate endDate) {
        log.info("Getting performance metrics for courier ID: {} and date range: {} to {}", 
                courierId, startDate, endDate);
        
        validateDateRange(startDate, endDate);
        
        Courier courier = getCourierById(courierId);
        List<PerformanceMetric> metrics = metricRepository.findByCourierAndDateBetween(courier, startDate, endDate);
        
        return metrics.stream()
                .map(metricMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Map<MetricType, Double> calculateAverageMetrics(LocalDate startDate, LocalDate endDate) {
        log.info("Calculating average metrics for date range: {} to {}", startDate, endDate);
        
        validateDateRange(startDate, endDate);
        
        Map<MetricType, Double> averages = new HashMap<>();
        
        for (MetricType metricType : MetricType.values()) {
            Double average = metricRepository.calculateAverageForMetricType(metricType, startDate, endDate);
            if (average != null) {
                averages.put(metricType, average);
            }
        }
        
        return averages;
    }

    @Override
    public Map<MetricType, Double> calculateAverageMetricsForCourier(
            String courierId, LocalDate startDate, LocalDate endDate) {
        log.info("Calculating average metrics for courier ID: {} and date range: {} to {}", 
                courierId, startDate, endDate);
        
        validateDateRange(startDate, endDate);
        
        Courier courier = getCourierById(courierId);
        Map<MetricType, Double> averages = new HashMap<>();
        
        for (MetricType metricType : MetricType.values()) {
            Double average = metricRepository.calculateAverageForCourierAndMetricType(
                    courier, metricType, startDate, endDate);
            if (average != null) {
                averages.put(metricType, average);
            }
        }
        
        return averages;
    }

    @Override
    public List<PerformanceMetricDTO> calculatePerformanceTrends(
            String courierId, MetricType metricType, int numberOfPeriods, String periodType) {
        log.info("Calculating performance trends for courier ID: {} and metric type: {}", courierId, metricType);
        
        if (numberOfPeriods <= 0) {
            throw new BusinessException("Number of periods must be positive");
        }
        
        Courier courier = getCourierById(courierId);
        LocalDate endDate = LocalDate.now();
        LocalDate startDate;
        
        // Determine start date based on period type
        switch (periodType.toLowerCase()) {
            case "day":
                startDate = endDate.minusDays(numberOfPeriods - 1);
                break;
            case "week":
                startDate = endDate.minusWeeks(numberOfPeriods - 1);
                break;
            case "month":
                startDate = endDate.minusMonths(numberOfPeriods - 1);
                break;
            default:
                throw new BusinessException("Invalid period type. Must be 'day', 'week', or 'month'");
        }
        
        List<PerformanceMetric> metrics = metricRepository.findByCourierAndMetricTypeAndDateBetween(
                courier, metricType, startDate, endDate);
        
        // Sort metrics by date
        metrics.sort(Comparator.comparing(PerformanceMetric::getDate));
        
        // Calculate trends
        List<PerformanceMetricDTO> trendsWithMetrics = new ArrayList<>();
        PerformanceMetric previousMetric = null;
        
        for (PerformanceMetric metric : metrics) {
            // Calculate trend percentage if there's a previous metric
            if (previousMetric != null && previousMetric.getValue() != 0) {
                double trendPercentage = ((metric.getValue() - previousMetric.getValue()) / 
                        Math.abs(previousMetric.getValue())) * 100;
                metric.setTrendPercentage(trendPercentage);
            }
            
            trendsWithMetrics.add(metricMapper.toDto(metric));
            previousMetric = metric;
        }
        
        return trendsWithMetrics;
    }

    @Override
    public Map<String, Object> generatePerformanceReport(String courierId, LocalDate startDate, LocalDate endDate) {
        log.info("Generating performance report for courier ID: {} and date range: {} to {}", 
                courierId, startDate, endDate);
        
        validateDateRange(startDate, endDate);
        
        Courier courier = getCourierById(courierId);
        Map<String, Object> report = new HashMap<>();
        
        // Basic courier information
        report.put("courierId", courier.getId());
        report.put("courierName", courier.getFirstName() + " " + courier.getLastName());
        report.put("reportPeriod", Map.of("startDate", startDate, "endDate", endDate));
        
        // Get all metrics for the courier in the date range
        List<PerformanceMetric> metrics = metricRepository.findByCourierAndDateBetween(courier, startDate, endDate);
        
        // Group metrics by type
        Map<MetricType, List<PerformanceMetric>> metricsByType = metrics.stream()
                .collect(Collectors.groupingBy(PerformanceMetric::getMetricType));
        
        // Calculate averages and add to report
        Map<MetricType, Double> averages = new HashMap<>();
        Map<MetricType, Boolean> targetAchievement = new HashMap<>();
        Map<MetricType, Double> trends = new HashMap<>();
        
        for (MetricType type : metricsByType.keySet()) {
            List<PerformanceMetric> typeMetrics = metricsByType.get(type);
            
            // Calculate average
            double average = typeMetrics.stream()
                    .mapToDouble(PerformanceMetric::getValue)
                    .average()
                    .orElse(0.0);
            averages.put(type, average);
            
            // Calculate target achievement rate
            long metricsWithTarget = typeMetrics.stream()
                    .filter(m -> m.getTargetValue() != null)
                    .count();
            
            if (metricsWithTarget > 0) {
                long targetsAchieved = typeMetrics.stream()
                        .filter(m -> m.getIsTargetMet() != null && m.getIsTargetMet())
                        .count();
                
                targetAchievement.put(type, (double) targetsAchieved / metricsWithTarget >= 0.5);
            }
            
            // Calculate overall trend
            if (typeMetrics.size() >= 2) {
                typeMetrics.sort(Comparator.comparing(PerformanceMetric::getDate));
                PerformanceMetric first = typeMetrics.get(0);
                PerformanceMetric last = typeMetrics.get(typeMetrics.size() - 1);
                
                if (first.getValue() != 0) {
                    double trendPercentage = ((last.getValue() - first.getValue()) / 
                            Math.abs(first.getValue())) * 100;
                    trends.put(type, trendPercentage);
                }
            }
        }
        
        // Add calculated data to report
        report.put("metrics", metricsByType.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey(),
                        entry -> entry.getValue().stream()
                                .map(metricMapper::toDto)
                                .collect(Collectors.toList())
                )));
        report.put("averages", averages.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey(),
                        Map.Entry::getValue
                )));
        report.put("targetAchievement", targetAchievement.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey(),
                        Map.Entry::getValue
                )));
        report.put("trends", trends.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey(),
                        Map.Entry::getValue
                )));
        
        // Add summary statistics
        report.put("summary", Map.of(
                "totalMetrics", metrics.size(),
                "uniqueMetricTypes", metricsByType.size(),
                "overallPerformance", targetAchievement.values().stream().filter(v -> v).count() >= 
                        targetAchievement.size() / 2.0 ? "Good" : "Needs Improvement"
        ));
        
        return report;
    }
    
    /**
     * Gets a courier by ID or throws an exception if not found.
     *
     * @param courierId the ID of the courier
     * @return the courier
     * @throws ResourceNotFoundException if the courier is not found
     */
    private Courier getCourierById(String courierId) {
        return courierRepository.findByCourierId(courierId)
                .orElseThrow(() -> new ResourceNotFoundException("Courier not found with ID: " + courierId));
    }
    
    /**
     * Validates that the start date is not after the end date.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @throws BusinessException if the start date is after the end date
     */
    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new BusinessException("Start date cannot be after end date");
        }
    }
} 