package com.gogidix.courier.regionaladmin.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO for performance dashboard data.
 * Contains all the metrics data needed to render the performance dashboard UI.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceDashboardDTO {

    private String regionCode;
    private String regionName;
    private LocalDateTime timestamp;
    
    // Metric categories
    private Map<String, Object> deliveryMetrics;
    private Map<String, Object> driverPerformance;
    private Map<String, Object> financialMetrics;
    private Map<String, Object> operationalMetrics;
    private Map<String, Object> customerSatisfaction;
    private Map<String, Object> systemHealth;
    
    // Cross-service integrated metrics
    private Map<String, Object> crossServiceMetrics;
    
    // Historical data for trends
    private Map<String, Object> historicalTrends;
}
