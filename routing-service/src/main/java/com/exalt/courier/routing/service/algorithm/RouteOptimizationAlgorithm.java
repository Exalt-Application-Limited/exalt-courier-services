package com.exalt.courier.routing.service.algorithm;

import com.exalt.courier.routing.model.Location;
import com.exalt.courier.routing.model.Waypoint;

import java.util.List;

/**
 * Interface for route optimization algorithms.
 * Different implementations can provide various strategies for optimizing delivery routes.
 */
public interface RouteOptimizationAlgorithm {
    
    /**
     * Optimizes the order of waypoints for an efficient delivery route.
     * 
     * @param startLocation The starting location of the route (e.g., warehouse or courier's position)
     * @param endLocation The end location (nullable, if courier should return to start)
     * @param waypoints The list of waypoints that need to be visited
     * @param timeConstraints Whether to consider time windows for deliveries
     * @return An ordered list of waypoints representing the optimized route
     */
    List<Waypoint> optimizeRoute(Location startLocation, Location endLocation, List<Waypoint> waypoints, boolean timeConstraints);
    
    /**
     * Gets the name of the algorithm for reporting and diagnostic purposes.
     * 
     * @return The name of the algorithm
     */
    String getAlgorithmName();
    
    /**
     * Calculates the total distance of the route in meters.
     * 
     * @param orderedWaypoints The ordered list of waypoints
     * @param startLocation The starting location
     * @param endLocation The end location (nullable)
     * @return The total route distance in meters
     */
    double calculateRouteDistance(List<Waypoint> orderedWaypoints, Location startLocation, Location endLocation);
    
    /**
     * Estimates the delivery time based on distance, traffic conditions, and other factors.
     * 
     * @param orderedWaypoints The ordered list of waypoints
     * @param startLocation The starting location
     * @param endLocation The end location (nullable)
     * @return The estimated delivery time in minutes
     */
    int estimateDeliveryTime(List<Waypoint> orderedWaypoints, Location startLocation, Location endLocation);
}
