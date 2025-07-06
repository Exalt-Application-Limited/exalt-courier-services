package com.gogidix.courier.courier.drivermobileapp.client.routing;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Feign client for interacting with the Routing service.
 */
@FeignClient(name = "routing-service", path = "/routing-service")
public interface RoutingServiceClient {
    
    /**
     * Get a specific route.
     *
     * @param routeId the route ID
     * @return the route
     */
    @GetMapping("/api/routing/routes/{routeId}")
    ResponseEntity<Map<String, Object>> getRoute(@PathVariable("routeId") String routeId);
    
    /**
     * Get routes by courier.
     *
     * @param courierId the courier ID
     * @return list of routes
     */
    @GetMapping("/api/routing/routes/courier/{courierId}")
    ResponseEntity<List<Map<String, Object>>> getRoutesByCourier(@PathVariable("courierId") String courierId);
    
    /**
     * Update route status.
     *
     * @param routeId the route ID
     * @param statusUpdate status update request
     * @return the updated route
     */
    @PutMapping("/api/routing/routes/{routeId}/status")
    ResponseEntity<Map<String, Object>> updateRouteStatus(
            @PathVariable("routeId") String routeId,
            @RequestBody Map<String, String> statusUpdate);
    
    /**
     * Generate an optimal delivery route.
     *
     * @param request the route optimization request
     * @return the optimal route
     */
    @PostMapping("/api/routing/optimal-route")
    ResponseEntity<Map<String, Object>> generateOptimalRoute(@RequestBody Map<String, Object> request);
    
    /**
     * Calculate the ETA between two points.
     *
     * @param request the ETA calculation request
     * @return the ETA information
     */
    @PostMapping("/api/routing/eta")
    ResponseEntity<Map<String, Object>> calculateEta(@RequestBody Map<String, Object> request);
    
    /**
     * Get navigation instructions.
     *
     * @param routeId the route ID
     * @param waypointIndex the waypoint index
     * @return navigation instructions
     */
    @GetMapping("/api/routing/routes/{routeId}/navigation/{waypointIndex}")
    ResponseEntity<List<Map<String, Object>>> getNavigationInstructions(
            @PathVariable("routeId") String routeId,
            @PathVariable("waypointIndex") Integer waypointIndex);
}
