package com.gogidix.courier.routing.integration;

import com.gogidix.courier.routing.model.Location;
import com.gogidix.courier.routing.model.Waypoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Integration with external mapping services for route optimization
 * and distance calculations.
 */
@Service
public class ExternalMapServiceClient {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalMapServiceClient.class);
    
    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String baseUrl;
    
    public ExternalMapServiceClient(
            RestTemplate restTemplate,
            @Value("${map.service.api-key}") String apiKey,
            @Value("${map.service.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
    }
    
    /**
     * Calculate a route between multiple waypoints
     *
     * @param waypoints the list of waypoints
     * @return map containing route data (distance, duration, geometry)
     */
    public Map<String, Object> calculateRoute(List<Waypoint> waypoints) {
        try {
            String waypointString = buildWaypointString(waypoints);
            String url = String.format("%s/directions?waypoints=%s&key=%s", 
                    baseUrl, waypointString, apiKey);
            
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                LOGGER.info("Successfully calculated route with {} waypoints", waypoints.size());
                return response.getBody();
            } else {
                LOGGER.warn("Failed to calculate route: {}", response.getStatusCode());
                return new HashMap<>();
            }
        } catch (Exception e) {
            LOGGER.error("Error calling external map service", e);
            return new HashMap<>();
        }
    }
    
    /**
     * Get estimated travel time between two locations
     *
     * @param origin the origin location
     * @param destination the destination location
     * @return estimated travel time in seconds, or -1 if calculation fails
     */
    public int getEstimatedTravelTime(Location origin, Location destination) {
        try {
            String url = String.format("%s/distancematrix?origins=%f,%f&destinations=%f,%f&key=%s",
                    baseUrl, 
                    origin.getLatitude(), origin.getLongitude(),
                    destination.getLatitude(), destination.getLongitude(),
                    apiKey);
            
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // Extract travel time from the response
                Map<String, Object> body = response.getBody();
                if (body.containsKey("rows")) {
                    List<Map<String, Object>> rows = (List<Map<String, Object>>) body.get("rows");
                    if (!rows.isEmpty()) {
                        Map<String, Object> row = rows.get(0);
                        List<Map<String, Object>> elements = (List<Map<String, Object>>) row.get("elements");
                        if (!elements.isEmpty()) {
                            Map<String, Object> element = elements.get(0);
                            Map<String, Object> duration = (Map<String, Object>) element.get("duration");
                            if (duration != null && duration.containsKey("value")) {
                                return ((Number) duration.get("value")).intValue();
                            }
                        }
                    }
                }
                LOGGER.warn("Could not extract travel time from response");
                return -1;
            } else {
                LOGGER.warn("Failed to get travel time: {}", response.getStatusCode());
                return -1;
            }
        } catch (Exception e) {
            LOGGER.error("Error calling external map service for travel time", e);
            return -1;
        }
    }
    
    /**
     * Get traffic conditions for a specific location
     *
     * @param location the location
     * @param radiusMeters radius to check for traffic conditions
     * @return map with traffic data
     */
    public Map<String, Object> getTrafficConditions(Location location, int radiusMeters) {
        try {
            String url = String.format("%s/traffic?location=%f,%f&radius=%d&key=%s",
                    baseUrl, location.getLatitude(), location.getLongitude(), radiusMeters, apiKey);
            
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                LOGGER.info("Successfully retrieved traffic conditions");
                return response.getBody();
            } else {
                LOGGER.warn("Failed to get traffic conditions: {}", response.getStatusCode());
                return new HashMap<>();
            }
        } catch (Exception e) {
            LOGGER.error("Error calling external map service for traffic conditions", e);
            return new HashMap<>();
        }
    }
    
    private String buildWaypointString(List<Waypoint> waypoints) {
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < waypoints.size(); i++) {
            Waypoint waypoint = waypoints.get(i);
            Location location = waypoint.getLocation();
            
            sb.append(location.getLatitude())
              .append(",")
              .append(location.getLongitude());
            
            if (i < waypoints.size() - 1) {
                sb.append("|");
            }
        }
        
        return sb.toString();
    }
}
