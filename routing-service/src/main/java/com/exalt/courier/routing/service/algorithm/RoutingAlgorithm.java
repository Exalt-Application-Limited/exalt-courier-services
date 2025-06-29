package com.exalt.courier.routing.service.algorithm;

import com.exalt.courier.routing.model.Location;
import com.exalt.courier.routing.model.Waypoint;

import java.util.List;

/**
 * Interface for routing algorithms.
 * Different implementations can provide various strategies for route optimization.
 */
public interface RoutingAlgorithm {
    
    /**
     * Optimize a route by reordering waypoints to minimize travel distance or time.
     * The starting location is fixed, and the algorithm will determine the best order
     * to visit all waypoints.
     *
     * @param startLocation The starting location for the route
     * @param waypoints The waypoints to be visited (in any order)
     * @return An ordered list of waypoints representing the optimized route
     */
    List<Waypoint> optimizeRoute(Location startLocation, List<Waypoint> waypoints);
    
    /**
     * Calculate the total distance of a route.
     *
     * @param startLocation The starting location
     * @param waypoints The ordered waypoints in the route
     * @return The total distance in kilometers
     */
    double calculateRouteDistance(Location startLocation, List<Waypoint> waypoints);
    
    /**
     * Calculate the estimated travel time for a route.
     *
     * @param startLocation The starting location
     * @param waypoints The ordered waypoints in the route
     * @param averageSpeedKmh The average travel speed in km/h
     * @return The estimated travel time in minutes
     */
    double calculateEstimatedTravelTime(Location startLocation, List<Waypoint> waypoints, double averageSpeedKmh);
    
    /**
     * Get the name of the algorithm for identification.
     *
     * @return The algorithm name
     */
    String getAlgorithmName();
} 