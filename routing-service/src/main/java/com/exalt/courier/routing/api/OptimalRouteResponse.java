package com.exalt.courier.routing.api;

import com.exalt.courier.routing.model.Location;
import com.exalt.courier.routing.model.Waypoint;
import com.exalt.courier.routing.service.algorithm.RouteOptimizationAlgorithm;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response model for optimal route generation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptimalRouteResponse {
    
    private List<Waypoint> waypoints;
    private double totalDistanceMeters;
    private int estimatedTimeMinutes;
    private String algorithmName;
    
    // Utility method to create response from waypoints and algorithm
    public static OptimalRouteResponse from(
            List<Waypoint> waypoints, 
            Location startLocation, 
            RouteOptimizationAlgorithm algorithm) {
        
        return OptimalRouteResponse.builder()
                .waypoints(waypoints)
                .totalDistanceMeters(algorithm.calculateRouteDistance(waypoints, startLocation, null))
                .estimatedTimeMinutes(algorithm.estimateDeliveryTime(waypoints, startLocation, null))
                .algorithmName(algorithm.getAlgorithmName())
                .build();
    }
}
