package com.gogidix.courier.regionaladmin.service.integration.impl;

import com.socialecommerceecosystem.regionaladmin.service.integration.TracingIntegrationService;
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
 * Implementation of the TracingIntegrationService interface.
 * Integrates with the distributed tracing system.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TracingIntegrationServiceImpl implements TracingIntegrationService {

    private final RestTemplate restTemplate;
    private final DiscoveryClient discoveryClient;
    
    private static final String TRACING_SERVICE_ID = "shared-config";
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> getServiceTraceSummary(String regionCode) {
        log.info("Getting service trace summary for region {}", regionCode);
        
        try {
            String url = buildTracingServiceUrl("/api/tracing/summary")
                    + "?regionCode=" + regionCode;
            
            return restTemplate.getForObject(url, Map.class);
        } catch (Exception e) {
            log.error("Error getting service trace summary for region {}: {}", 
                    regionCode, e.getMessage());
            return new HashMap<>();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Map<String, Object>> getRecentTraces(String regionCode, int limit) {
        log.info("Getting recent traces for region {} with limit {}", regionCode, limit);
        
        try {
            String url = UriComponentsBuilder.fromUriString(buildTracingServiceUrl("/api/tracing/recent"))
                    .queryParam("regionCode", regionCode)
                    .queryParam("limit", limit)
                    .build()
                    .toUriString();
            
            return restTemplate.getForObject(url, List.class);
        } catch (Exception e) {
            log.error("Error getting recent traces for region {}: {}", 
                    regionCode, e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> getTraceDetails(String traceId) {
        log.info("Getting trace details for trace ID {}", traceId);
        
        try {
            String url = buildTracingServiceUrl("/api/tracing/details")
                    + "?traceId=" + traceId;
            
            return restTemplate.getForObject(url, Map.class);
        } catch (Exception e) {
            log.error("Error getting trace details for trace ID {}: {}", 
                    traceId, e.getMessage());
            return new HashMap<>();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> getLatencyMetrics(String regionCode) {
        log.info("Getting latency metrics for region {}", regionCode);
        
        try {
            String url = buildTracingServiceUrl("/api/tracing/latency")
                    + "?regionCode=" + regionCode;
            
            return restTemplate.getForObject(url, Map.class);
        } catch (Exception e) {
            log.error("Error getting latency metrics for region {}: {}", 
                    regionCode, e.getMessage());
            return new HashMap<>();
        }
    }
    
    /**
     * Helper method to build the tracing service URL.
     */
    private String buildTracingServiceUrl(String path) {
        List<org.springframework.cloud.client.ServiceInstance> instances = 
                discoveryClient.getInstances(TRACING_SERVICE_ID);
        
        if (instances.isEmpty()) {
            log.warn("No instances found for service: {}", TRACING_SERVICE_ID);
            throw new RuntimeException("Tracing service not available");
        }
        
        org.springframework.cloud.client.ServiceInstance instance = instances.get(0);
        return instance.getUri() + path;
    }
}
