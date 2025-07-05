package com.gogidix.courier.shared.external;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import com.gogidix.courier.shared.resilience.CircuitBreakerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Client for external mapping service with circuit breaker implementation.
 * This class demonstrates how to use the circuit breaker pattern when calling
 * external services from the courier domain.
 */
@Component
public class ExternalMappingServiceClient {
    private static final Logger logger = LoggerFactory.getLogger(ExternalMappingServiceClient.class);
    
    private final RestTemplate restTemplate;
    private final CircuitBreakerUtil circuitBreakerUtil;
    private final String mappingServiceBaseUrl;
    
    @Autowired
    public ExternalMappingServiceClient(
            RestTemplate restTemplate,
            CircuitBreakerUtil circuitBreakerUtil,
            @Value("${courier.external.mapping-service.base-url}") String mappingServiceBaseUrl) {
        this.restTemplate = restTemplate;
        this.circuitBreakerUtil = circuitBreakerUtil;
        this.mappingServiceBaseUrl = mappingServiceBaseUrl;
    }
    
    /**
     * Gets an optimal route between two locations from the external mapping service.
     * Uses circuit breaker pattern to handle failures gracefully.
     *
     * @param startLat the starting latitude
     * @param startLng the starting longitude
     * @param endLat the ending latitude
     * @param endLng the ending longitude
     * @return an Optional containing the route points if available, or empty if not available
     */
    public Optional<List<Map<String, Double>>> getOptimalRoute(
            double startLat, double startLng, double endLat, double endLng) {
        
        String url = mappingServiceBaseUrl + "/routes/optimal" +
                "?startLat=" + startLat +
                "&startLng=" + startLng +
                "&endLat=" + endLat +
                "&endLng=" + endLng;
        
        try {
            // Use the externalProvider circuit breaker configuration
            return circuitBreakerUtil.executeWithCircuitBreaker(
                    "externalProvider",
                    "Get optimal route from mapping service",
                    () -> {
                        ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);
                        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                            return Optional.of(response.getBody());
                        }
                        return Optional.empty();
                    },
                    Optional.empty()
            );
        } catch (Exception e) {
            logger.error("Error getting optimal route from mapping service", e);
            return Optional.empty();
        }
    }
    
    /**
     * Gets an estimated travel time between two locations from the external mapping service.
     * Uses circuit breaker pattern to handle failures gracefully.
     *
     * @param startLat the starting latitude
     * @param startLng the starting longitude
     * @param endLat the ending latitude
     * @param endLng the ending longitude
     * @return an Optional containing the estimated travel time in seconds, or empty if not available
     */
    public Optional<Integer> getEstimatedTravelTime(
            double startLat, double startLng, double endLat, double endLng) {
        
        String url = mappingServiceBaseUrl + "/routes/eta" +
                "?startLat=" + startLat +
                "&startLng=" + startLng +
                "&endLat=" + endLat +
                "&endLng=" + endLng;
        
        try {
            // Use the externalProvider circuit breaker configuration
            return circuitBreakerUtil.executeWithCircuitBreaker(
                    "externalProvider",
                    "Get estimated travel time from mapping service",
                    () -> {
                        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
                        if (response.getStatusCode().is2xxSuccessful() && 
                            response.getBody() != null && 
                            response.getBody().containsKey("estimatedSeconds")) {
                            return Optional.of(((Number) response.getBody().get("estimatedSeconds")).intValue());
                        }
                        return Optional.empty();
                    },
                    Optional.empty()
            );
        } catch (CallNotPermittedException e) {
            logger.warn("Circuit breaker open for mapping service ETA call", e);
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Error getting estimated travel time from mapping service", e);
            return Optional.empty();
        }
    }
    
    /**
     * Gets the traffic conditions along a route from the external mapping service.
     * Uses circuit breaker pattern to handle failures gracefully.
     *
     * @param routeId the ID of the route to check
     * @return a Map containing traffic information, or an empty map if not available
     */
    public Map<String, Object> getTrafficConditions(String routeId) {
        String url = mappingServiceBaseUrl + "/traffic/conditions?routeId=" + routeId;
        
        try {
            // Use the externalProvider circuit breaker configuration with fallback
            return circuitBreakerUtil.executeWithCircuitBreaker(
                    "externalProvider",
                    "Get traffic conditions from mapping service",
                    () -> {
                        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
                        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                            return response.getBody();
                        }
                        return Collections.emptyMap();
                    },
                    Collections.emptyMap()
            );
        } catch (Exception e) {
            logger.error("Error getting traffic conditions from mapping service", e);
            return Collections.emptyMap();
        }
    }
} 