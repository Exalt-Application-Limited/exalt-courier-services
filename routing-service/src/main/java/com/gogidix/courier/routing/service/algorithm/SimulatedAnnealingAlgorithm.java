package com.gogidix.courier.routing.service.algorithm;

import com.gogidix.courier.routing.model.Location;
import com.gogidix.courier.routing.model.Waypoint;
import com.gogidix.courier.routing.service.algorithm.util.GeoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Implementation of the Simulated Annealing algorithm for route optimization.
 * This probabilistic technique can escape local optima to find better global solutions.
 */
@Component
public class SimulatedAnnealingAlgorithm implements RoutingAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(SimulatedAnnealingAlgorithm.class);
    private static final String ALGORITHM_NAME = "Simulated Annealing";
    private static final double DEFAULT_AVERAGE_SPEED_KMH = 30.0;
    
    // Simulated Annealing parameters
    private static final double INITIAL_TEMPERATURE = 10000.0;
    private static final double COOLING_RATE = 0.9995;
    private static final double FINAL_TEMPERATURE = 0.01;
    private static final int ITERATIONS_PER_TEMPERATURE = 100;
    
    private final Random random = new Random();
    
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
        
        logger.info("Optimizing route with {} waypoints using Simulated Annealing algorithm", waypoints.size());
        
        // Start with a random solution
        List<Waypoint> currentSolution = new ArrayList<>(waypoints);
        Collections.shuffle(currentSolution);
        
        // Initialize best solution
        List<Waypoint> bestSolution = new ArrayList<>(currentSolution);
        
        // Calculate initial route distance
        double currentDistance = calculateRouteDistance(startLocation, currentSolution);
        double bestDistance = currentDistance;
        
        logger.info("Initial route distance: {:.2f} km", currentDistance);
        
        // Simulated annealing
        double temperature = INITIAL_TEMPERATURE;
        int iteration = 0;
        
        while (temperature > FINAL_TEMPERATURE) {
            for (int i = 0; i < ITERATIONS_PER_TEMPERATURE; i++) {
                // Create a new solution by swapping two random waypoints
                List<Waypoint> newSolution = new ArrayList<>(currentSolution);
                int index1 = random.nextInt(newSolution.size());
                int index2 = random.nextInt(newSolution.size());
                Collections.swap(newSolution, index1, index2);
                
                // Calculate the new distance
                double newDistance = calculateRouteDistance(startLocation, newSolution);
                
                // Decide whether to accept the new solution
                double delta = newDistance - currentDistance;
                if (delta < 0 || Math.exp(-delta / temperature) > random.nextDouble()) {
                    currentSolution = newSolution;
                    currentDistance = newDistance;
                    
                    // If current solution is better than the best, update the best solution
                    if (currentDistance < bestDistance) {
                        bestSolution = new ArrayList<>(currentSolution);
                        bestDistance = currentDistance;
                        logger.debug("Found better solution at iteration {}, temperature {:.2f}, distance {:.2f} km",
                                iteration, temperature, bestDistance);
                    }
                }
                
                iteration++;
            }
            
            // Cool down the temperature
            temperature *= COOLING_RATE;
        }
        
        logger.info("Route optimization complete. Original distance: {:.2f} km, Optimized distance: {:.2f} km, Improvement: {:.2f}%",
                calculateRouteDistance(startLocation, waypoints),
                bestDistance,
                (1 - bestDistance / calculateRouteDistance(startLocation, waypoints)) * 100);
        
        return bestSolution;
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
} 