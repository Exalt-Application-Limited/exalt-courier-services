package com.exalt.courier.drivermobileapp.service;

import com.socialecommerceecosystem.drivermobileapp.dto.routing.NavigationInstructionDTO;
import com.socialecommerceecosystem.drivermobileapp.dto.routing.RouteDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Service interface for routing operations.
 */
public interface RoutingService {
    
    /**
     * Get a specific route.
     *
     * @param routeId the route ID
     * @return the route
     */
    RouteDTO getRoute(String routeId);
    
    /**
     * Get routes by courier.
     *
     * @param courierId the courier ID
     * @return list of routes
     */
    List<RouteDTO> getRoutesByCourier(String courierId);
    
    /**
     * Update route status.
     *
     * @param routeId the route ID
     * @param status the new status
     * @return the updated route
     */
    RouteDTO updateRouteStatus(String routeId, String status);
    
    /**
     * Generate an optimal delivery route.
     *
     * @param courierId the courier ID
     * @param startLat start latitude
     * @param startLon start longitude
     * @param waypointIds list of waypoint IDs
     * @return the optimal route
     */
    RouteDTO generateOptimalRoute(String courierId, BigDecimal startLat, BigDecimal startLon, List<String> waypointIds);
    
    /**
     * Calculate the ETA between two points.
     *
     * @param startLat start latitude
     * @param startLon start longitude
     * @param endLat end latitude
     * @param endLon end longitude
     * @return the ETA information
     */
    Map<String, Object> calculateEta(BigDecimal startLat, BigDecimal startLon, BigDecimal endLat, BigDecimal endLon);
    
    /**
     * Get navigation instructions.
     *
     * @param routeId the route ID
     * @param waypointIndex the waypoint index
     * @return navigation instructions
     */
    List<NavigationInstructionDTO> getNavigationInstructions(String routeId, Integer waypointIndex);
    
    /**
     * Cache route locally for offline use.
     *
     * @param routeId the route ID
     * @return true if successful
     */
    boolean cacheRouteLocally(String routeId);
    
    /**
     * Get cached routes.
     *
     * @param courierId the courier ID
     * @return list of cached routes
     */
    List<RouteDTO> getCachedRoutes(String courierId);
}
