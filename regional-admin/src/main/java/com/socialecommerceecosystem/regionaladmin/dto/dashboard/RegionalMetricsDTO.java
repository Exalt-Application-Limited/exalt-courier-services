package com.gogidix.courier.regionaladmin.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO for RegionalMetrics entity.
 * Used for transferring regional metrics data between layers.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegionalMetricsDTO {

    private Long id;
    private String regionCode;
    private String regionName;
    private String metricCategory;
    private String metricName;
    private Double metricValue;
    private String unit;
    private String description;
    private Map<String, String> attributes;
    private LocalDateTime dataTimestamp;
}
