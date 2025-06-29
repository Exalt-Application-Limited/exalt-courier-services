package com.exalt.courier.drivermobileapp.dto.routing;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for waypoint information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaypointDTO {
    
    private String id;
    
    private String routeId;
    
    @NotBlank(message = "Waypoint type is required")
    private String type;
    
    @NotBlank(message = "Status is required")
    private String status;
    
    private Integer sequenceNumber;
    
    @NotBlank(message = "Address is required")
    private String address;
    
    @NotNull(message = "Latitude is required")
    private BigDecimal latitude;
    
    @NotNull(message = "Longitude is required")
    private BigDecimal longitude;
    
    private String name;
    
    private String description;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime estimatedArrivalTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime actualArrivalTime;
    
    private Double distanceFromPreviousKm;
    
    private Double durationFromPreviousMinutes;
    
    private String trackingNumber;
    
    private String packageId;
    
    private String reference;
}
