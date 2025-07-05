package com.gogidix.courier.drivermobileapp.controller;

import com.socialecommerceecosystem.drivermobileapp.dto.routing.NavigationInstructionDTO;
import com.socialecommerceecosystem.drivermobileapp.dto.routing.RouteDTO;
import com.socialecommerceecosystem.drivermobileapp.service.RoutingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * REST controller for routing operations in the driver mobile app.
 */
@RestController
@RequestMapping("/api/v1/routing")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Routing", description = "Route management and navigation operations for mobile app")
public class RoutingController {
    
    private final RoutingService routingService;
    
    /**
     * Get a specific route.
     *
     * @param routeId the route ID
     * @return the route
     */
    @GetMapping("/routes/{routeId}")
    @Operation(
        summary = "Get route",
        description = "Retrieves details of a specific route",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "Route retrieved successfully",
                content = @Content(schema = @Schema(implementation = RouteDTO.class))
            ),
            @ApiResponse(responseCode = "404", description = "Route not found")
        }
    )
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<RouteDTO> getRoute(@PathVariable String routeId) {
        log.info("REST request to get route: {}", routeId);
        return ResponseEntity.ok(routingService.getRoute(routeId));
    }
    
    /**
     * Get routes by courier.
     *
     * @param courierId the courier ID
     * @return list of routes
     */
    @GetMapping("/routes/courier/{courierId}")
    @Operation(
        summary = "Get routes by courier",
        description = "Retrieves all routes assigned to a specific courier",
        responses = {
            @ApiResponse(responseCode = "200", description = "Routes retrieved successfully")
        }
    )
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<List<RouteDTO>> getRoutesByCourier(@PathVariable String courierId) {
        log.info("REST request to get routes for courier: {}", courierId);
        return ResponseEntity.ok(routingService.getRoutesByCourier(courierId));
    }
    
    /**
     * Update route status.
     *
     * @param routeId the route ID
     * @param statusUpdate the status update
     * @return the updated route
     */
    @PutMapping("/routes/{routeId}/status")
    @Operation(
        summary = "Update route status",
        description = "Updates the status of a route (e.g., START, COMPLETE, CANCEL)",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "Route status updated successfully",
                content = @Content(schema = @Schema(implementation = RouteDTO.class))
            ),
            @ApiResponse(responseCode = "404", description = "Route not found")
        }
    )
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<RouteDTO> updateRouteStatus(
            @PathVariable String routeId,
            @Valid @RequestBody Map<String, String> statusUpdate) {
        
        log.info("REST request to update route status: {}", routeId);
        return ResponseEntity.ok(routingService.updateRouteStatus(routeId, statusUpdate.get("status")));
    }
    
    /**
     * Generate an optimal delivery route.
     *
     * @param request the route optimization request
     * @return the optimal route
     */
    @PostMapping("/optimal-route")
    @Operation(
        summary = "Generate optimal route",
        description = "Generates an optimal delivery route for a set of waypoints",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "Optimal route generated successfully",
                content = @Content(schema = @Schema(implementation = RouteDTO.class))
            )
        }
    )
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<RouteDTO> generateOptimalRoute(@Valid @RequestBody Map<String, Object> request) {
        log.info("REST request to generate optimal route for courier: {}", request.get("courierId"));
        
        String courierId = String.valueOf(request.get("courierId"));
        BigDecimal startLat = new BigDecimal(String.valueOf(request.get("startLatitude")));
        BigDecimal startLon = new BigDecimal(String.valueOf(request.get("startLongitude")));
        
        @SuppressWarnings("unchecked")
        List<String> waypointIds = (List<String>) request.get("waypointIds");
        
        return ResponseEntity.ok(routingService.generateOptimalRoute(courierId, startLat, startLon, waypointIds));
    }
    
    /**
     * Calculate the ETA between two points.
     *
     * @param request the ETA calculation request
     * @return the ETA information
     */
    @PostMapping("/eta")
    @Operation(
        summary = "Calculate ETA",
        description = "Calculates the estimated time of arrival between two points",
        responses = {
            @ApiResponse(responseCode = "200", description = "ETA calculated successfully")
        }
    )
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<Map<String, Object>> calculateEta(@Valid @RequestBody Map<String, Object> request) {
        log.info("REST request to calculate ETA between points");
        
        BigDecimal startLat = new BigDecimal(String.valueOf(request.get("startLatitude")));
        BigDecimal startLon = new BigDecimal(String.valueOf(request.get("startLongitude")));
        BigDecimal endLat = new BigDecimal(String.valueOf(request.get("endLatitude")));
        BigDecimal endLon = new BigDecimal(String.valueOf(request.get("endLongitude")));
        
        return ResponseEntity.ok(routingService.calculateEta(startLat, startLon, endLat, endLon));
    }
    
    /**
     * Get navigation instructions.
     *
     * @param routeId the route ID
     * @param waypointIndex the waypoint index
     * @return navigation instructions
     */
    @GetMapping("/routes/{routeId}/navigation/{waypointIndex}")
    @Operation(
        summary = "Get navigation instructions",
        description = "Retrieves turn-by-turn navigation instructions for a route segment",
        responses = {
            @ApiResponse(responseCode = "200", description = "Navigation instructions retrieved successfully")
        }
    )
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<List<NavigationInstructionDTO>> getNavigationInstructions(
            @PathVariable String routeId,
            @PathVariable Integer waypointIndex) {
        
        log.info("REST request to get navigation instructions for route: {} and waypoint index: {}", routeId, waypointIndex);
        return ResponseEntity.ok(routingService.getNavigationInstructions(routeId, waypointIndex));
    }
    
    /**
     * Cache route for offline use.
     *
     * @param routeId the route ID
     * @return success status
     */
    @PostMapping("/routes/{routeId}/cache")
    @Operation(
        summary = "Cache route for offline use",
        description = "Marks a route to be cached locally for offline operations",
        responses = {
            @ApiResponse(responseCode = "200", description = "Route cached successfully"),
            @ApiResponse(responseCode = "404", description = "Route not found")
        }
    )
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<Map<String, Boolean>> cacheRoute(@PathVariable String routeId) {
        log.info("REST request to cache route: {}", routeId);
        boolean success = routingService.cacheRouteLocally(routeId);
        return ResponseEntity.ok(Map.of("success", success));
    }
    
    /**
     * Get cached routes.
     *
     * @param courierId the courier ID
     * @return list of cached routes
     */
    @GetMapping("/routes/cached/courier/{courierId}")
    @Operation(
        summary = "Get cached routes",
        description = "Retrieves all routes that have been cached for offline use",
        responses = {
            @ApiResponse(responseCode = "200", description = "Cached routes retrieved successfully")
        }
    )
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<List<RouteDTO>> getCachedRoutes(@PathVariable String courierId) {
        log.info("REST request to get cached routes for courier: {}", courierId);
        return ResponseEntity.ok(routingService.getCachedRoutes(courierId));
    }
}
