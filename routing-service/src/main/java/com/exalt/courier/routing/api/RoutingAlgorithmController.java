package com.exalt.courier.routing.api;

import com.exalt.courier.routing.model.Location;
import com.exalt.courier.routing.model.Waypoint;
import com.exalt.courier.routing.service.algorithm.RoutingAlgorithm;
import com.exalt.courier.routing.service.algorithm.RoutingAlgorithmFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/routing/algorithms")
@Tag(name = "Routing Algorithms", description = "API for routing algorithm operations")
public class RoutingAlgorithmController {

    private static final Logger logger = LoggerFactory.getLogger(RoutingAlgorithmController.class);
    
    @Autowired
    private RoutingAlgorithmFactory algorithmFactory;
    
    @GetMapping
    @Operation(summary = "List available routing algorithms")
    public ResponseEntity<List<String>> listAlgorithms() {
        Map<String, RoutingAlgorithm> algorithms = algorithmFactory.getAllAlgorithms();
        List<String> algorithmNames = algorithms.keySet().stream()
                .sorted()
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(algorithmNames);
    }
    
    @PostMapping("/{algorithmName}/optimize")
    @Operation(summary = "Optimize a route using a specific algorithm")
    public ResponseEntity<OptimizationResult> optimizeRoute(
            @Parameter(description = "Name of the algorithm to use")
            @PathVariable String algorithmName,
            
            @Parameter(description = "Optimization request with start location and waypoints")
            @RequestBody OptimizationRequest request) {
        
        logger.info("Optimizing route with algorithm: {}, waypoints: {}", 
                algorithmName, request.getWaypoints().size());
        
        RoutingAlgorithm algorithm = algorithmFactory.getAlgorithm(algorithmName);
        
        // Optimize the route
        List<Waypoint> optimizedWaypoints = algorithm.optimizeRoute(
                request.getStartLocation(), request.getWaypoints());
        
        // Calculate metrics
        double distance = algorithm.calculateRouteDistance(
                request.getStartLocation(), optimizedWaypoints);
        
        double travelTime = algorithm.calculateEstimatedTravelTime(
                request.getStartLocation(), optimizedWaypoints, request.getAverageSpeedKmh());
        
        OptimizationResult result = new OptimizationResult();
        result.setAlgorithmName(algorithm.getAlgorithmName());
        result.setOptimizedWaypoints(optimizedWaypoints);
        result.setTotalDistanceKm(distance);
        result.setEstimatedTravelTimeMinutes(travelTime);
        
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/compare")
    @Operation(summary = "Compare different routing algorithms for the same route")
    public ResponseEntity<List<AlgorithmComparisonResult>> compareAlgorithms(
            @Parameter(description = "Optimization request with start location and waypoints")
            @RequestBody OptimizationRequest request) {
        
        logger.info("Comparing routing algorithms with {} waypoints", request.getWaypoints().size());
        
        Map<String, RoutingAlgorithm> algorithms = algorithmFactory.getAllAlgorithms();
        
        List<AlgorithmComparisonResult> results = algorithms.values().stream()
                .map(algorithm -> {
                    // Run optimization with this algorithm
                    long startTime = System.currentTimeMillis();
                    List<Waypoint> optimizedWaypoints = algorithm.optimizeRoute(
                            request.getStartLocation(), request.getWaypoints());
                    long endTime = System.currentTimeMillis();
                    
                    // Calculate metrics
                    double distance = algorithm.calculateRouteDistance(
                            request.getStartLocation(), optimizedWaypoints);
                    double travelTime = algorithm.calculateEstimatedTravelTime(
                            request.getStartLocation(), optimizedWaypoints, request.getAverageSpeedKmh());
                    long computationTime = endTime - startTime;
                    
                    // Create comparison result
                    AlgorithmComparisonResult result = new AlgorithmComparisonResult();
                    result.setAlgorithmName(algorithm.getAlgorithmName());
                    result.setTotalDistanceKm(distance);
                    result.setEstimatedTravelTimeMinutes(travelTime);
                    result.setComputationTimeMs(computationTime);
                    
                    return result;
                })
                .sorted((r1, r2) -> Double.compare(r1.getTotalDistanceKm(), r2.getTotalDistanceKm()))
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(results);
    }
    
    /**
     * Request class for route optimization.
     * Converted to use Lombok annotations for reduced boilerplate.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OptimizationRequest {
        private Location startLocation;
        private List<Waypoint> waypoints;
        
        @Builder.Default
        private double averageSpeedKmh = 30.0;
    }
    
    /**
     * Result class for route optimization.
     * Converted to use Lombok annotations for reduced boilerplate.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OptimizationResult {
        private String algorithmName;
        private List<Waypoint> optimizedWaypoints;
        private double totalDistanceKm;
        private double estimatedTravelTimeMinutes;
    }
    
    /**
     * Result class for algorithm comparison.
     * Converted to use Lombok annotations for reduced boilerplate.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AlgorithmComparisonResult {
        private String algorithmName;
        private double totalDistanceKm;
        private double estimatedTravelTimeMinutes;
        private long computationTimeMs;
    }
}