package com.gogidix.courier.regionaladmin.service.integration.impl;

import com.socialecommerceecosystem.regionaladmin.service.integration.TrackingIntegrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the TrackingIntegrationService interface.
 * Integrates with the real-time tracking service.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TrackingIntegrationServiceImpl implements TrackingIntegrationService {

    private final RestTemplate restTemplate;
    private final DiscoveryClient discoveryClient;
    
    private static final String TRACKING_SERVICE_ID = "third-party-integration-service";
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> getCurrentTrackingStatus(String regionCode) {
        log.info("Getting current tracking status for region {}", regionCode);
        
        try {
            String url = buildTrackingServiceUrl("/api/tracking/status")
                    + "?regionCode=" + regionCode;
            
            return restTemplate.getForObject(url, Map.class);
        } catch (Exception e) {
            log.error("Error getting tracking status for region {}: {}", 
                    regionCode, e.getMessage());
            return new HashMap<>();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getActiveDeliveryCount(String regionCode) {
        log.info("Getting active delivery count for region {}", regionCode);
        
        try {
            String url = buildTrackingServiceUrl("/api/tracking/active-count")
                    + "?regionCode=" + regionCode;
            
            Integer count = restTemplate.getForObject(url, Integer.class);
            return count != null ? count : 0;
        } catch (Exception e) {
            log.error("Error getting active delivery count for region {}: {}", 
                    regionCode, e.getMessage());
            return 0;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Map<String, Object>> getTrackingEvents(String regionCode, int limit) {
        log.info("Getting tracking events for region {} with limit {}", regionCode, limit);
        
        try {
            String url = UriComponentsBuilder.fromUriString(buildTrackingServiceUrl("/api/tracking/events"))
                    .queryParam("regionCode", regionCode)
                    .queryParam("limit", limit)
                    .build()
                    .toUriString();
            
            return restTemplate.getForObject(url, List.class);
        } catch (Exception e) {
            log.error("Error getting tracking events for region {}: {}", 
                    regionCode, e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Integer> getTrackingStatusSummary(String regionCode) {
        log.info("Getting tracking status summary for region {}", regionCode);
        
        try {
            String url = buildTrackingServiceUrl("/api/tracking/status-summary")
                    + "?regionCode=" + regionCode;
            
            return restTemplate.getForObject(url, Map.class);
        } catch (Exception e) {
            log.error("Error getting tracking status summary for region {}: {}", 
                    regionCode, e.getMessage());
            return new HashMap<>();
        }
    }
    
    /**
     * Helper method to build the tracking service URL.
     */
    private String buildTrackingServiceUrl(String path) {
        List<org.springframework.cloud.client.ServiceInstance> instances = 
                discoveryClient.getInstances(TRACKING_SERVICE_ID);
        
        if (instances.isEmpty()) {
            log.warn("No instances found for service: {}", TRACKING_SERVICE_ID);
            throw new RuntimeException("Tracking service not available");
        }
        
        org.springframework.cloud.client.ServiceInstance instance = instances.get(0);
        return instance.getUri() + path;
    }
}
