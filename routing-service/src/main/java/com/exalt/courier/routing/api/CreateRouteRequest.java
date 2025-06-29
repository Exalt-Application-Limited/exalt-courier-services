package com.exalt.courier.routing.api;

import com.exalt.courier.routing.model.Waypoint;
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
 * Request model for creating a new route.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRouteRequest {
    
    @NotBlank(message = "Courier ID is required")
    private String courierId;
    
    private String vehicleId;
    
    @NotEmpty(message = "At least one waypoint is required")
    private List<Waypoint> waypoints;
    
    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;
} 
