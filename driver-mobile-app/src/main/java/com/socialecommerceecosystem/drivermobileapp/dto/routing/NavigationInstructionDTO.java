package com.gogidix.courier.courier.drivermobileapp.dto.routing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for navigation instructions
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NavigationInstructionDTO {
    
    private String id;
    
    private String routeId;
    
    private Integer waypointIndex;
    
    private Integer stepNumber;
    
    private String instruction;
    
    private String maneuver;
    
    private BigDecimal startLatitude;
    
    private BigDecimal startLongitude;
    
    private BigDecimal endLatitude;
    
    private BigDecimal endLongitude;
    
    private Double distanceKm;
    
    private Double durationMinutes;
    
    private String roadName;
    
    private String polyline;
}
