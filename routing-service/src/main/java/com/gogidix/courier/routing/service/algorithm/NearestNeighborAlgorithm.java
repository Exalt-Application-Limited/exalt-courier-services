package com.gogidix.courier.routing.service.algorithm;

import com.gogidix.courier.routing.model.Location;
import com.gogidix.courier.routing.model.Waypoint;
import com.gogidix.courier.routing.service.algorithm.util.GeoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementation of the Nearest Neighbor algorithm for route optimization.
 * This is a greedy algorithm that always selects the closest unvisited waypoint
 * as the next destination.
 */
@Component
public class NearestNeighborAlgorithm implements RoutingAlgorithm {
    
    private static final Logger logger = LoggerFactory.getLogger(NearestNeighborAlgorithm.class);
    private static final String ALGORITHM_NAME = "Nearest Neighbor";
    private static final double DEFAULT_AVERAGE_SPEED_KMH = 30.0;
    
    @Override
    public List<Waypoint> optimizeRoute(Location startLocation, List<Waypoint> waypoints) {
        if (waypoints == null || waypoints.isEmpty()) {
            logger.warn("Empty waypoint list provided for route optimization");
            return new ArrayList<>();
        }
        
        if (startLocation == null) {
            logger.error("Start location cannot be null");
            throw new IllegalArgumentException("Start location cannot be null");
        }
        
        logger.info("Optimizing route with {} waypoints using Nearest Neighbor algorithm", waypoints.size());
        
        List<Waypoint> optimizedRoute = new ArrayList<>(waypoints.size());
        Set<Waypoint> unvisitedWaypoints = new HashSet<>(waypoints);
        
        // Current location starts at the start location
        Location currentLocation = startLocation;
        
        // Build the route by repeatedly finding the nearest unvisited waypoint
        while (!unvisitedWaypoints.isEmpty()) {
            Waypoint nearest = findNearestWaypoint(currentLocation, unvisitedWaypoints);
            optimizedRoute.add(nearest);
            unvisitedWaypoints.remove(nearest);
            currentLocation = nearest.getLocation();
        }
        
        double totalDistance = calculateRouteDistance(startLocation, optimizedRoute);
        logger.info("Route optimization complete. Total distance: {:.2f} km", totalDistance);
        
        return optimizedRoute;
    }
    
    @Override
    public double calculateRouteDistance(Location startLocation, List<Waypoint> waypoints) {
        if (waypoints == null || waypoints.isEmpty()) {
            return 0.0;
        }
        
        double totalDistance = 0.0;
        Location previousLocation = startLocation;
        
        for (Waypoint waypoint : waypoints) {
            Location waypointLocation = waypoint.getLocation();
            double segmentDistance = GeoUtils.calculateHaversineDistance(previousLocation, waypointLocation);
            totalDistance += segmentDistance;
            previousLocation = waypointLocation;
        }
        
        return totalDistance;
    }
    
    @Override
    public double calculateEstimatedTravelTime(Location startLocation, List<Waypoint> waypoints, double averageSpeedKmh) {
        if (averageSpeedKmh <= 0) {
            averageSpeedKmh = DEFAULT_AVERAGE_SPEED_KMH;
            logger.warn("Invalid average speed provided, using default: {} km/h", DEFAULT_AVERAGE_SPEED_KMH);
        }
        
        double totalDistance = calculateRouteDistance(startLocation, waypoints);
        
        // Convert to minutes: (distance / speed) * 60
        double travelTimeMinutes = (totalDistance / averageSpeedKmh) * 60.0;
        
        // Add estimated service time at each waypoint (assuming 5 minutes per waypoint)
        double serviceTimeMinutes = waypoints.size() * 5.0;
        
        return travelTimeMinutes + serviceTimeMinutes;
    }
    
    @Override
    public String getAlgorithmName() {
        return ALGORITHM_NAME;
    }
    
    /**
     * Find the nearest unvisited waypoint from the current location.
     *
     * @param currentLocation The current location
     * @param unvisitedWaypoints Set of unvisited waypoints
     * @return The nearest waypoint
     */
    private Waypoint findNearestWaypoint(Location currentLocation, Set<Waypoint> unvisitedWaypoints) {
        Waypoint nearest = null;
        double minDistance = Double.MAX_VALUE;
        
        for (Waypoint waypoint : unvisitedWaypoints) {
            double distance = GeoUtils.calculateHaversineDistance(currentLocation, waypoint.getLocation());
            
            if (distance < minDistance) {
                minDistance = distance;
                nearest = waypoint;
            }
        }
        
        return nearest;
    }
}
