package com.gogidix.courierservices.tracking.$1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

/**
 * Fallback implementation for the RoutingServiceClient.
 */
@Component
@Slf4j
public class RoutingServiceClientFallback implements RoutingServiceClient {
    
    
    
    @Override
    public Integer getEstimatedDeliveryTime(Long routeId) {
        log.warn("Fallback: Unable to get estimated delivery time for route {}", routeId);
        return null;
    }
    
    @Override
    public Map<String, Object> getRouteInfo(Long routeId) {
        log.warn("Fallback: Unable to get route information for route {}", routeId);
        return Collections.emptyMap();
    }
    
    @Override
    public boolean isLocationOnRoute(Long routeId, Double latitude, Double longitude) {
        log.warn("Fallback: Unable to check if location ({}, {}) is on route {}", 
                latitude, longitude, routeId);
        return false;
    }
} 
