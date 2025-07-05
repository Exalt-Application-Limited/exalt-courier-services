package com.gogidix.courier.routing.api;

import com.gogidix.courier.routing.exception.ResourceNotFoundException;
import com.gogidix.courier.routing.model.Location;
import com.gogidix.courier.routing.model.Route;
import com.gogidix.courier.routing.model.RouteStatus;
import com.gogidix.courier.routing.model.Waypoint;
import com.gogidix.courier.routing.service.RoutingService;
import com.gogidix.courier.routing.service.algorithm.RouteOptimizationAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

/**
 * REST controller for routing operations.
 */
@RestController
@RequestMapping("/api/routing")
@RequiredArgsConstructor
@Slf4j
public class RoutingController {

    private final RoutingService routingService;
    private final RouteOptimizationAlgorithm routeOptimizationAlgorithm;

    @PostMapping("/routes")
    public ResponseEntity<Route> createRoute(@Valid @RequestBody CreateRouteRequest request) {
        log.info("Creating route for courier: {}", request.getCourierId());
        
        Route route = routingService.createRoute(
                request.getCourierId(),
                request.getVehicleId(),
                request.getWaypoints(),
                request.getStartTime()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(route);
    }

    @GetMapping("/routes/{routeId}")
    public ResponseEntity<Route> getRoute(@PathVariable String routeId) {
        log.info("Retrieving route: {}", routeId);
        
        return routingService.getRouteById(routeId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id: " + routeId));
    }

    @GetMapping("/routes/courier/{courierId}")
    public ResponseEntity<List<Route>> getRoutesByCourier(@PathVariable String courierId) {
        log.info("Retrieving routes for courier: {}", courierId);
        
        List<Route> routes = routingService.getRoutesByCourier(courierId);
        return ResponseEntity.ok(routes);
    }

    @GetMapping("/routes/status/{status}")
    public ResponseEntity<List<Route>> getRoutesByStatus(@PathVariable RouteStatus status) {
        log.info("Retrieving routes with status: {}", status);
        
        List<Route> routes = routingService.getRoutesByStatus(status);
        return ResponseEntity.ok(routes);
    }

    @GetMapping("/routes/shipment/{shipmentId}")
    public ResponseEntity<List<Route>> getRoutesByShipment(@PathVariable String shipmentId) {
        log.info("Retrieving routes for shipment: {}", shipmentId);
        
        List<Route> routes = routingService.getRoutesByShipment(shipmentId);
        return ResponseEntity.ok(routes);
    }

    @PutMapping("/routes/{routeId}/status")
    public ResponseEntity<Route> updateRouteStatus(
            @PathVariable String routeId,
            @RequestBody UpdateStatusRequest request) {
        
        log.info("Updating status for route: {} to {}", routeId, request.getStatus());
        
        Route route = routingService.updateRouteStatus(routeId, request.getStatus());
        return ResponseEntity.ok(route);
    }

    @PutMapping("/routes/{routeId}/courier/{courierId}")
    public ResponseEntity<Route> assignCourier(
            @PathVariable String routeId,
            @PathVariable String courierId) {
        
        log.info("Assigning courier: {} to route: {}", courierId, routeId);
        
        Route route = routingService.assignCourier(routeId, courierId);
        return ResponseEntity.ok(route);
    }

    @PostMapping("/routes/{routeId}/start")
    public ResponseEntity<Route> startRoute(@PathVariable String routeId) {
        log.info("Starting route: {}", routeId);
        
        Route route = routingService.startRoute(routeId);
        return ResponseEntity.ok(route);
    }

    @PostMapping("/routes/{routeId}/complete")
    public ResponseEntity<Route> completeRoute(@PathVariable String routeId) {
        log.info("Completing route: {}", routeId);
        
        Route route = routingService.completeRoute(routeId);
        return ResponseEntity.ok(route);
    }

    @PostMapping("/routes/{routeId}/waypoints")
    public ResponseEntity<Route> addWaypoint(
            @PathVariable String routeId,
            @Valid @RequestBody Waypoint waypoint) {
        
        log.info("Adding waypoint to route: {}", routeId);
        
        Route route = routingService.addWaypoint(routeId, waypoint);
        return ResponseEntity.ok(route);
    }

    @DeleteMapping("/routes/{routeId}/waypoints/{waypointId}")
    public ResponseEntity<Route> removeWaypoint(
            @PathVariable String routeId,
            @PathVariable String waypointId) {
        
        log.info("Removing waypoint: {} from route: {}", waypointId, routeId);
        
        Route route = routingService.removeWaypoint(routeId, waypointId);
        return ResponseEntity.ok(route);
    }

    @PostMapping("/routes/{routeId}/optimize")
    public ResponseEntity<Route> optimizeRoute(@PathVariable String routeId) {
        log.info("Optimizing route: {}", routeId);
        
        Route route = routingService.optimizeRoute(routeId);
        return ResponseEntity.ok(route);
    }

    @GetMapping("/couriers/nearest")
    public ResponseEntity<List<String>> findNearestCouriers(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "10.0") Double maxDistanceKm) {
        
        log.info("Finding couriers near location: [{}, {}]", latitude, longitude);
        
        Location location = Location.builder()
                .latitude(latitude)
                .longitude(longitude)
                .build();
        
        List<String> courierIds = routingService.findNearestCouriers(location, maxDistanceKm);
        return ResponseEntity.ok(courierIds);
    }

    @GetMapping("/shipments/{shipmentId}/eta")
    public ResponseEntity<?> calculateETA(@PathVariable String shipmentId) {
        log.info("Calculating ETA for shipment: {}", shipmentId);
        
        LocalDateTime eta = routingService.calculateEstimatedTimeOfArrival(shipmentId);
        
        if (eta == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No ETA available for shipment: " + shipmentId);
        }
        
        return ResponseEntity.ok(new ETAResponse(shipmentId, eta));
    }

    @PostMapping("/optimal-route")
    public ResponseEntity<OptimalRouteResponse> generateOptimalRoute(@Valid @RequestBody OptimalRouteRequest request) {
        log.info("Generating optimal route with {} waypoints", request.getWaypoints().size());
        
        List<Waypoint> optimizedWaypoints = routingService.generateOptimalRoute(
                request.getStartLocation(),
                request.getWaypoints()
        );
        
        // Create enhanced response with additional details
        OptimalRouteResponse response = OptimalRouteResponse.from(
                optimizedWaypoints, 
                request.getStartLocation(), 
                routeOptimizationAlgorithm
        );
        
        log.info("Generated optimal route with {} waypoints, {}m distance, {}min estimated time using {}",
                optimizedWaypoints.size(), response.getTotalDistanceMeters(), 
                response.getEstimatedTimeMinutes(), response.getAlgorithmName());
        
        return ResponseEntity.ok(response);
    }
}
