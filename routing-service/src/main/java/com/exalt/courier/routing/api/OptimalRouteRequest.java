package com.exalt.courier.routing.api;

import com.exalt.courier.routing.model.Location;
import com.exalt.courier.routing.model.Waypoint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * Request model for generating an optimal route.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptimalRouteRequest {
    
    @NotNull(message = "Start location is required")
    private Location startLocation;
    
    @NotEmpty(message = "At least one waypoint is required")
    private List<Waypoint> waypoints;
} 
