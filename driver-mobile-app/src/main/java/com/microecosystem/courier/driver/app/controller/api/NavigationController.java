package com.microecosystem.courier.driver.app.controller.api;

import com.microecosystem.courier.driver.app.service.navigation.NavigationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST API controller for navigation services.
 * This controller provides endpoints for routing, directions, and geocoding.
 */
@RestController
@RequestMapping("/api/v1/navigation")
@Tag(name = "Navigation", description = "APIs for routing, directions, and geocoding services")
public class NavigationController {

    private static final Logger logger = LoggerFactory.getLogger(NavigationController.class);
    
    private final NavigationService navigationService;
    
    public NavigationController(NavigationService navigationService) {
        this.navigationService = navigationService;
    }
    
    /**
     * Get directions between two points.
     * 
     * @param params Map containing start and end coordinates
     * @return Directions data
     */
    @PostMapping("/directions")
    @Operation(summary = "Get directions", description = "Retrieves directions between two points")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<Map<String, Object>> getDirections(
            @Parameter(description = "Coordinates for start and end points", required = true)
            @RequestBody Map<String, Double> params) {
            
        logger.info("Directions request with params: {}", params);
        
        double startLat = params.get("startLat");
        double startLng = params.get("startLng");
        double endLat = params.get("endLat");
        double endLng = params.get("endLng");
        
        Map<String, Object> directions = navigationService.getDirections(startLat, startLng, endLat, endLng);
        
        if (directions.containsKey("error") && (boolean) directions.get("error")) {
            return ResponseEntity.badRequest().body(directions);
        } else {
            return ResponseEntity.ok(directions);
        }
    }
    
    /**
     * Get an optimized route for multiple waypoints.
     * 
     * @param request Map containing start location and waypoints
     * @return Optimized route data
     */
    @PostMapping("/route/optimize")
    @Operation(summary = "Get optimized route", description = "Retrieves an optimized route for multiple waypoints")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<Map<String, Object>> getOptimizedRoute(
            @Parameter(description = "Start location and waypoints", required = true)
            @RequestBody Map<String, Object> request) {
            
        logger.info("Optimized route request");
        
        double startLat = ((Number) request.get("startLat")).doubleValue();
        double startLng = ((Number) request.get("startLng")).doubleValue();
        
        @SuppressWarnings("unchecked")
        List<Map<String, Double>> waypoints = (List<Map<String, Double>>) request.get("waypoints");
        
        Map<String, Object> route = navigationService.getOptimizedRoute(startLat, startLng, waypoints);
        
        if (route.containsKey("error") && (boolean) route.get("error")) {
            return ResponseEntity.badRequest().body(route);
        } else {
            return ResponseEntity.ok(route);
        }
    }
    
    /**
     * Prefetch map data for a specific area.
     * 
     * @param params Map containing center coordinates and radius
     * @return Result of the prefetch operation
     */
    @PostMapping("/map/prefetch")
    @Operation(summary = "Prefetch map data", description = "Prefetches map data for a specific area for offline use")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<Map<String, Object>> prefetchMapData(
            @Parameter(description = "Center coordinates and radius", required = true)
            @RequestBody Map<String, Object> params) {
            
        logger.info("Map prefetch request with params: {}", params);
        
        double centerLat = ((Number) params.get("centerLat")).doubleValue();
        double centerLng = ((Number) params.get("centerLng")).doubleValue();
        int radiusKm = params.containsKey("radiusKm") ? 
                       ((Number) params.get("radiusKm")).intValue() : 5;
        
        Map<String, Object> result = navigationService.prefetchMapData(centerLat, centerLng, radiusKm);
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * Geocode an address to coordinates.
     * 
     * @param params Map containing the address to geocode
     * @return Geocode information
     */
    @PostMapping("/geocode")
    @Operation(summary = "Geocode address", description = "Converts an address to coordinates")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<Map<String, Object>> geocodeAddress(
            @Parameter(description = "Address to geocode", required = true)
            @RequestBody Map<String, String> params) {
            
        logger.info("Geocode request for address: {}", params.get("address"));
        
        String address = params.get("address");
        Map<String, Object> result = navigationService.geocodeAddress(address);
        
        if (result.containsKey("error") && (boolean) result.get("error")) {
            return ResponseEntity.badRequest().body(result);
        } else {
            return ResponseEntity.ok(result);
        }
    }
    
    /**
     * Reverse geocode coordinates to an address.
     * 
     * @param params Map containing coordinates to reverse geocode
     * @return Address information
     */
    @PostMapping("/reverse-geocode")
    @Operation(summary = "Reverse geocode", description = "Converts coordinates to an address")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<Map<String, Object>> reverseGeocode(
            @Parameter(description = "Coordinates to reverse geocode", required = true)
            @RequestBody Map<String, Double> params) {
            
        logger.info("Reverse geocode request for coordinates: ({},{})", 
                  params.get("lat"), params.get("lng"));
        
        double lat = params.get("lat");
        double lng = params.get("lng");
        
        Map<String, Object> result = navigationService.reverseGeocode(lat, lng);
        
        if (result.containsKey("error") && (boolean) result.get("error")) {
            return ResponseEntity.badRequest().body(result);
        } else {
            return ResponseEntity.ok(result);
        }
    }
}
