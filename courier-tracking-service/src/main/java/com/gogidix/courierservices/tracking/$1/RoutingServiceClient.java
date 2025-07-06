package com.gogidix.courierservices.tracking.$1;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * Feign client for interacting with the Routing Service.
 */
@FeignClient(name = "routing-service", fallback = RoutingServiceClientFallback.class)
public interface RoutingServiceClient {

    /**
     * Get estimated delivery time for a route.
     */
    @GetMapping("/api/v1/routing/routes/{routeId}/eta")
    Integer getEstimatedDeliveryTime(@PathVariable("routeId") Long routeId);
    
    /**
     * Get route information.
     */
    @GetMapping("/api/v1/routing/routes/{routeId}")
    Map<String, Object> getRouteInfo(@PathVariable("routeId") Long routeId);
    
    /**
     * Check if a location is on a route.
     */
    @GetMapping("/api/v1/routing/routes/{routeId}/check-location")
    boolean isLocationOnRoute(
            @PathVariable("routeId") Long routeId,
            @RequestParam("latitude") Double latitude,
            @RequestParam("longitude") Double longitude);
} 