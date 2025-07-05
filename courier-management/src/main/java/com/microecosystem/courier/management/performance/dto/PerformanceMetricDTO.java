package com.gogidix.courier.management.performance.dto;

import com.gogidix.courier.management.performance.model.MetricType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for performance metrics.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceMetricDTO {

    private Long id;
    
    private String metricId;
    
    @NotBlank(message = "Courier ID is required")
    private String courierId;
    
    private String courierName;
    
    @NotNull(message = "Metric type is required")
    private MetricType metricType;
    
    @NotNull(message = "Date is required")
    @PastOrPresent(message = "Date cannot be in the future")
    private LocalDate date;
    
    @NotNull(message = "Value is required")
    private Double value;
    
    private String description;
    
    @PositiveOrZero(message = "Target value must be positive or zero")
    private Double targetValue;
    
    private Boolean isTargetMet;
    
    private Double trendPercentage;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
} 