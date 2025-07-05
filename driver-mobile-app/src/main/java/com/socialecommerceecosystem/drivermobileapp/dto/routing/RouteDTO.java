package com.gogidix.courier.drivermobileapp.dto.routing;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for route information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteDTO {
    
    private String id;
    
    @NotBlank(message = "Courier ID is required")
    private String courierId;
    
    @NotBlank(message = "Status is required")
    private String status;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startedAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime completedAt;
    
    @NotEmpty(message = "Route must have at least one waypoint")
    private List<WaypointDTO> waypoints;
    
    private Double totalDistanceKm;
    
    private Double estimatedDurationMinutes;
    
    private String polyline;
    
    private String mapUrl;
    
    private String notes;
}
