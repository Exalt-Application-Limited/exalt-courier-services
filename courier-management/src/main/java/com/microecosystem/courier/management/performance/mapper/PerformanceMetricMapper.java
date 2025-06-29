package com.exalt.courier.management.performance.mapper;

import com.exalt.courier.management.courier.model.Courier;
import com.exalt.courier.management.performance.dto.PerformanceMetricDTO;
import com.exalt.courier.management.performance.model.PerformanceMetric;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Mapper for converting between PerformanceMetric entities and DTOs.
 */
@Component
public class PerformanceMetricMapper {

    /**
     * Convert a PerformanceMetric entity to a DTO.
     *
     * @param metric the entity to convert
     * @return the DTO
     */
    public PerformanceMetricDTO toDto(PerformanceMetric metric) {
        if (metric == null) {
            return null;
        }

        return PerformanceMetricDTO.builder()
                .id(metric.getId())
                .metricId(metric.getMetricId())
                .courierId(metric.getCourier().getId())
                .courierName(metric.getCourier().getFullName())
                .metricType(metric.getMetricType())
                .date(metric.getDate())
                .value(metric.getValue())
                .description(metric.getDescription())
                .targetValue(metric.getTargetValue())
                .isTargetMet(metric.getIsTargetMet())
                .trendPercentage(metric.getTrendPercentage())
                .createdAt(metric.getCreatedAt())
                .updatedAt(metric.getUpdatedAt())
                .build();
    }

    /**
     * Convert a DTO to a PerformanceMetric entity.
     *
     * @param dto the DTO to convert
     * @return the entity
     */
    public PerformanceMetric toEntity(PerformanceMetricDTO dto) {
        if (dto == null) {
            return null;
        }

        PerformanceMetric metric = new PerformanceMetric();
        
        // Set fields from DTO
        metric.setId(dto.getId());
        metric.setMetricId(dto.getMetricId() != null ? dto.getMetricId() : UUID.randomUUID().toString());
        metric.setMetricType(dto.getMetricType());
        metric.setDate(dto.getDate());
        metric.setValue(dto.getValue());
        metric.setDescription(dto.getDescription());
        metric.setTargetValue(dto.getTargetValue());
        metric.setIsTargetMet(dto.getIsTargetMet());
        metric.setTrendPercentage(dto.getTrendPercentage());
        
        return metric;
    }

    /**
     * Update an existing PerformanceMetric entity with data from a DTO.
     *
     * @param metric the entity to update
     * @param dto the DTO with updated data
     * @return the updated entity
     */
    public PerformanceMetric updateEntityFromDto(PerformanceMetric metric, PerformanceMetricDTO dto) {
        if (dto == null) {
            return metric;
        }
        
        // Only update fields that can be changed
        if (dto.getMetricType() != null) {
            metric.setMetricType(dto.getMetricType());
        }
        if (dto.getDate() != null) {
            metric.setDate(dto.getDate());
        }
        if (dto.getValue() != null) {
            metric.setValue(dto.getValue());
        }
        if (dto.getDescription() != null) {
            metric.setDescription(dto.getDescription());
        }
        if (dto.getTargetValue() != null) {
            metric.setTargetValue(dto.getTargetValue());
        }
        
        // Recalculate if target is met
        if (metric.getTargetValue() != null && metric.getValue() != null) {
            boolean isTargetMet = metric.getValue() >= metric.getTargetValue();
            metric.setIsTargetMet(isTargetMet);
        }
        
        return metric;
    }
} 