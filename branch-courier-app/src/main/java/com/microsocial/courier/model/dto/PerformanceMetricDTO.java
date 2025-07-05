package com.gogidix.courier.courier.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for performance metrics information from courier management service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceMetricDTO {
    private Long id;
    private Long courierId;
    private String metricType;
    private Double value;
    private LocalDateTime recordedAt;
    private String period;
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
}