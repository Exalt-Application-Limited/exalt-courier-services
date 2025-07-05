package com.gogidix.courier.routing.service;

import com.gogidix.courier.routing.model.Location;
import com.gogidix.courier.routing.model.Route;
import com.gogidix.courier.routing.model.RouteStatus;
import com.gogidix.courier.routing.model.Waypoint;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for routing operations.
 */
public interface RoutingService {
    
    /**
     * Create a new route with waypoints
     *
     * @param courierId the ID of the courier assigned to the route
     * @param vehicleId the ID of the vehicle used for the route
     * @param waypoints the list of waypoints in the route
     * @param startTime the scheduled start time for the route
     * @return the created route
     */
    Route createRoute(String courierId, String vehicleId, List<Waypoint> waypoints, LocalDateTime startTime);
    
    /**
     * Get a route by its ID
     *
     * @param routeId the route ID
     * @return an Optional containing the route if found
     */
    Optional<Route> getRouteById(String routeId);
    
    /**
     * Get all routes for a courier
     *
     * @param courierId the courier ID
     * @return list of routes assigned to the courier
     */
    List<Route> getRoutesByCourier(String courierId);
    
    /**
     * Get all routes with a specific status
     *
     * @param status the route status
     * @return list of routes with the specified status
     */
    List<Route> getRoutesByStatus(RouteStatus status);
    
    /**
     * Get routes for a specific shipment
     *
     * @param shipmentId the shipment ID
     * @return list of routes containing the shipment
     */
    List<Route> getRoutesByShipment(String shipmentId);
    
    /**
     * Update the status of a route
     *
     * @param routeId the route ID
     * @param status the new status
     * @return the updated route
     */
    Route updateRouteStatus(String routeId, RouteStatus status);
    
    /**
     * Assign a courier to a route
     *
     * @param routeId the route ID
     * @param courierId the courier ID
     * @return the updated route
     */
    Route assignCourier(String routeId, String courierId);
    
    /**
     * Start a route, updating its status and recording the start time
     *
     * @param routeId the route ID
     * @return the updated route
     */
    Route startRoute(String routeId);
    
    /**
     * Complete a route, updating its status and recording the end time
     *
     * @param routeId the route ID
     * @return the updated route
     */
    Route completeRoute(String routeId);
    
    /**
     * Add a waypoint to an existing route
     *
     * @param routeId the route ID
     * @param waypoint the waypoint to add
     * @return the updated route
     */
    Route addWaypoint(String routeId, Waypoint waypoint);
    
    /**
     * Remove a waypoint from a route
     *
     * @param routeId the route ID
     * @param waypointId the ID of the waypoint to remove
     * @return the updated route
     */
    Route removeWaypoint(String routeId, String waypointId);
    
    /**
     * Optimize a route to minimize travel time or distance
     *
     * @param routeId the route ID
     * @return the optimized route
     */
    Route optimizeRoute(String routeId);
    
    /**
     * Find the nearest courier to a location
     *
     * @param location the location
     * @param maxDistanceKm the maximum distance to search
     * @return list of active courier IDs near the location, ordered by proximity
     */
    List<String> findNearestCouriers(Location location, double maxDistanceKm);
    
    /**
     * Calculate the estimated time of arrival for a shipment
     *
     * @param shipmentId the shipment ID
     * @return the estimated time of arrival, or null if not found
     */
    LocalDateTime calculateEstimatedTimeOfArrival(String shipmentId);
    
    /**
     * Generate an optimal delivery route for a set of waypoints
     *
     * @param startLocation the starting location
     * @param waypoints the waypoints to visit
     * @return an ordered list of waypoints for the optimal route
     */
    List<Waypoint> generateOptimalRoute(Location startLocation, List<Waypoint> waypoints);
} 