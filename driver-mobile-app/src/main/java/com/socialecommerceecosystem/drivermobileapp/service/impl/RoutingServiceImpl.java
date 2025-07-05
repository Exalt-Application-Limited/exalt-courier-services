package com.gogidix.courier.drivermobileapp.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.socialecommerceecosystem.drivermobileapp.client.routing.RoutingServiceClient;
import com.socialecommerceecosystem.drivermobileapp.dto.routing.NavigationInstructionDTO;
import com.socialecommerceecosystem.drivermobileapp.dto.routing.RouteDTO;
import com.socialecommerceecosystem.drivermobileapp.exception.ResourceNotFoundException;
import com.socialecommerceecosystem.drivermobileapp.service.RoutingService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Implementation of the RoutingService interface.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RoutingServiceImpl implements RoutingService {
    
    private final RoutingServiceClient routingServiceClient;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    
    // Local cache for brief storage of routes during service disruptions
    private final Map<String, RouteDTO> routeCache = new ConcurrentHashMap<>();
    
    @Override
    @CircuitBreaker(name = "routingService", fallbackMethod = "getRouteFallback")
    public RouteDTO getRoute(String routeId) {
        log.info("Getting route with ID: {}", routeId);
        
        ResponseEntity<Map<String, Object>> response = routingServiceClient.getRoute(routeId);
        
        if (response.getBody() == null) {
            throw new ResourceNotFoundException("Route not found with id: " + routeId);
        }
        
        RouteDTO route = convertToRouteDTO(response.getBody());
        
        // Cache route
        routeCache.put(routeId, route);
        cacheRouteInRedis(route);
        
        return route;
    }
    
    @Override
    @CircuitBreaker(name = "routingService", fallbackMethod = "getRoutesByCourierFallback")
    public List<RouteDTO> getRoutesByCourier(String courierId) {
        log.info("Getting routes for courier: {}", courierId);
        
        ResponseEntity<List<Map<String, Object>>> response = routingServiceClient.getRoutesByCourier(courierId);
        
        if (response.getBody() == null) {
            return Collections.emptyList();
        }
        
        List<RouteDTO> routes = response.getBody().stream()
                .map(this::convertToRouteDTO)
                .collect(Collectors.toList());
        
        // Cache routes
        routes.forEach(route -> {
            routeCache.put(route.getId(), route);
            cacheRouteInRedis(route);
        });
        
        return routes;
    }
    
    @Override
    @CircuitBreaker(name = "routingService", fallbackMethod = "updateRouteStatusFallback")
    public RouteDTO updateRouteStatus(String routeId, String status) {
        log.info("Updating route status for route ID: {} to status: {}", routeId, status);
        
        Map<String, String> statusUpdate = new HashMap<>();
        statusUpdate.put("status", status);
        
        ResponseEntity<Map<String, Object>> response = routingServiceClient.updateRouteStatus(routeId, statusUpdate);
        
        if (response.getBody() == null) {
            throw new ResourceNotFoundException("Route not found with id: " + routeId);
        }
        
        RouteDTO route = convertToRouteDTO(response.getBody());
        
        // Update cache
        routeCache.put(routeId, route);
        cacheRouteInRedis(route);
        
        return route;
    }
    
    @Override
    @CircuitBreaker(name = "routingService", fallbackMethod = "generateOptimalRouteFallback")
    public RouteDTO generateOptimalRoute(String courierId, BigDecimal startLat, BigDecimal startLon, List<String> waypointIds) {
        log.info("Generating optimal route for courier: {}", courierId);
        
        Map<String, Object> request = new HashMap<>();
        request.put("courierId", courierId);
        request.put("startLatitude", startLat);
        request.put("startLongitude", startLon);
        request.put("waypointIds", waypointIds);
        
        ResponseEntity<Map<String, Object>> response = routingServiceClient.generateOptimalRoute(request);
        
        if (response.getBody() == null) {
            throw new ResourceNotFoundException("Could not generate optimal route");
        }
        
        RouteDTO route = convertToRouteDTO(response.getBody());
        
        // Cache route
        routeCache.put(route.getId(), route);
        cacheRouteInRedis(route);
        
        return route;
    }
    
    @Override
    @CircuitBreaker(name = "routingService", fallbackMethod = "calculateEtaFallback")
    public Map<String, Object> calculateEta(BigDecimal startLat, BigDecimal startLon, BigDecimal endLat, BigDecimal endLon) {
        log.info("Calculating ETA from ({}, {}) to ({}, {})", startLat, startLon, endLat, endLon);
        
        Map<String, Object> request = new HashMap<>();
        request.put("startLatitude", startLat);
        request.put("startLongitude", startLon);
        request.put("endLatitude", endLat);
        request.put("endLongitude", endLon);
        
        ResponseEntity<Map<String, Object>> response = routingServiceClient.calculateEta(request);
        
        return response.getBody() != null ? response.getBody() : Map.of();
    }
    
    @Override
    @CircuitBreaker(name = "routingService", fallbackMethod = "getNavigationInstructionsFallback")
    public List<NavigationInstructionDTO> getNavigationInstructions(String routeId, Integer waypointIndex) {
        log.info("Getting navigation instructions for route: {} and waypoint index: {}", routeId, waypointIndex);
        
        ResponseEntity<List<Map<String, Object>>> response = routingServiceClient.getNavigationInstructions(routeId, waypointIndex);
        
        if (response.getBody() == null) {
            return Collections.emptyList();
        }
        
        return response.getBody().stream()
                .map(this::convertToNavigationInstructionDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean cacheRouteLocally(String routeId) {
        log.info("Caching route locally: {}", routeId);
        
        try {
            RouteDTO route = getRoute(routeId);
            cacheRouteInRedis(route);
            return true;
        } catch (Exception e) {
            log.error("Failed to cache route: {}", routeId, e);
            return false;
        }
    }
    
    @Override
    public List<RouteDTO> getCachedRoutes(String courierId) {
        log.info("Getting cached routes for courier: {}", courierId);
        
        // Get all routes for this courier from Redis
        String cacheKeyPattern = "route:" + courierId + ":*";
        Set<String> keys = redisTemplate.keys(cacheKeyPattern);
        
        if (keys == null || keys.isEmpty()) {
            // Fallback to local cache
            return routeCache.values().stream()
                    .filter(r -> courierId.equals(r.getCourierId()))
                    .collect(Collectors.toList());
        }
        
        List<RouteDTO> cachedRoutes = new ArrayList<>();
        keys.forEach(key -> {
            Object obj = redisTemplate.opsForValue().get(key);
            if (obj instanceof RouteDTO) {
                cachedRoutes.add((RouteDTO) obj);
            }
        });
        
        return cachedRoutes;
    }
    
    /**
     * Caches a route in Redis for offline use.
     *
     * @param route the route to cache
     */
    private void cacheRouteInRedis(RouteDTO route) {
        try {
            String key = "route:" + route.getCourierId() + ":" + route.getId();
            redisTemplate.opsForValue().set(key, route, Duration.ofHours(24));
        } catch (Exception e) {
            log.error("Error caching route in Redis: {}", route.getId(), e);
        }
    }
    
    /**
     * Converts a map of route data to a RouteDTO.
     *
     * @param map the map containing route data
     * @return the converted RouteDTO
     */
    private RouteDTO convertToRouteDTO(Map<String, Object> map) {
        try {
            return objectMapper.convertValue(map, RouteDTO.class);
        } catch (Exception e) {
            log.error("Error converting route data: {}", e.getMessage());
            return new RouteDTO();
        }
    }
    
    /**
     * Converts a map of navigation instruction data to a NavigationInstructionDTO.
     *
     * @param map the map containing navigation instruction data
     * @return the converted NavigationInstructionDTO
     */
    private NavigationInstructionDTO convertToNavigationInstructionDTO(Map<String, Object> map) {
        try {
            return objectMapper.convertValue(map, NavigationInstructionDTO.class);
        } catch (Exception e) {
            log.error("Error converting navigation instruction data: {}", e.getMessage());
            return new NavigationInstructionDTO();
        }
    }
    
    // Fallback methods for circuit breaker
    
    public RouteDTO getRouteFallback(String routeId, Exception e) {
        log.warn("Fallback: Getting route from cache: {}, reason: {}", routeId, e.getMessage());
        return routeCache.getOrDefault(routeId, new RouteDTO());
    }
    
    public List<RouteDTO> getRoutesByCourierFallback(String courierId, Exception e) {
        log.warn("Fallback: Getting routes from cache for courier: {}, reason: {}", courierId, e.getMessage());
        return getCachedRoutes(courierId);
    }
    
    public RouteDTO updateRouteStatusFallback(String routeId, String status, Exception e) {
        log.warn("Fallback: Updating route status locally: {}, reason: {}", routeId, e.getMessage());
        
        RouteDTO route = routeCache.get(routeId);
        if (route != null) {
            route.setStatus(status);
            routeCache.put(routeId, route);
        }
        
        return route != null ? route : new RouteDTO();
    }
    
    public RouteDTO generateOptimalRouteFallback(String courierId, BigDecimal startLat, BigDecimal startLon, List<String> waypointIds, Exception e) {
        log.warn("Fallback: Cannot generate optimal route, service unavailable: {}", e.getMessage());
        return new RouteDTO();
    }
    
    public Map<String, Object> calculateEtaFallback(BigDecimal startLat, BigDecimal startLon, BigDecimal endLat, BigDecimal endLon, Exception e) {
        log.warn("Fallback: Cannot calculate ETA, service unavailable: {}", e.getMessage());
        
        // Provide a very rough estimate based on straight-line distance
        double lat1 = startLat.doubleValue();
        double lon1 = startLon.doubleValue();
        double lat2 = endLat.doubleValue();
        double lon2 = endLon.doubleValue();
        
        final double EARTH_RADIUS = 6371.0; // in km
        
        // Convert to radians
        lat1 = Math.toRadians(lat1);
        lon1 = Math.toRadians(lon1);
        lat2 = Math.toRadians(lat2);
        lon2 = Math.toRadians(lon2);
        
        // Haversine formula
        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = EARTH_RADIUS * c;
        
        // Rough estimate: 50 km/h average speed
        double estimatedMinutes = (distance / 50.0) * 60;
        
        Map<String, Object> estimate = new HashMap<>();
        estimate.put("distanceKm", distance);
        estimate.put("estimatedMinutes", estimatedMinutes);
        estimate.put("isEstimate", true);
        
        return estimate;
    }
    
    public List<NavigationInstructionDTO> getNavigationInstructionsFallback(String routeId, Integer waypointIndex, Exception e) {
        log.warn("Fallback: Cannot get navigation instructions, service unavailable: {}", e.getMessage());
        return Collections.emptyList();
    }
}
