package com.gogidix.courier.regionaladmin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Data Transfer Object for regional metrics data.
 * Used for transferring metrics data between services and layers.
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
    private Map<String, String> attributes = new HashMap<>();
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataTimestamp;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
    
    private Boolean isActive;
    private Integer branchCount;
    private String aggregationMethod;
}
