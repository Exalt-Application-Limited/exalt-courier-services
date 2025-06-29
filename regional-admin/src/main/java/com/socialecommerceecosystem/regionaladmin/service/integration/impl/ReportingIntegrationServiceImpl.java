package com.exalt.courier.regionaladmin.service.integration.impl;

import com.socialecommerceecosystem.regionaladmin.service.integration.ReportingIntegrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the ReportingIntegrationService interface.
 * Integrates with the advanced reporting system.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ReportingIntegrationServiceImpl implements ReportingIntegrationService {

    private final RestTemplate restTemplate;
    private final DiscoveryClient discoveryClient;
    
    private static final String REPORTING_SERVICE_ID = "hq-admin";
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getAvailableReportTypes() {
        log.info("Getting available report types");
        
        try {
            String url = buildReportingServiceUrl("/api/reports/types");
            
            return restTemplate.getForObject(url, List.class);
        } catch (Exception e) {
            log.error("Error getting available report types: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> generateReport(String regionCode, String reportType, 
                                         Map<String, Object> parameters) {
        log.info("Generating report of type {} for region {}", reportType, regionCode);
        
        try {
            String url = buildReportingServiceUrl("/api/reports/generate");
            
            // Create request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("regionCode", regionCode);
            requestBody.put("reportType", reportType);
            requestBody.put("parameters", parameters);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            return restTemplate.postForObject(url, entity, Map.class);
        } catch (Exception e) {
            log.error("Error generating report for region {}: {}", 
                    regionCode, e.getMessage());
            return new HashMap<>();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Map<String, Object>> getScheduledReports(String regionCode) {
        log.info("Getting scheduled reports for region {}", regionCode);
        
        try {
            String url = buildReportingServiceUrl("/api/reports/scheduled")
                    + "?regionCode=" + regionCode;
            
            return restTemplate.getForObject(url, List.class);
        } catch (Exception e) {
            log.error("Error getting scheduled reports for region {}: {}", 
                    regionCode, e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String scheduleReport(String regionCode, String reportType, 
                             Map<String, Object> schedule, Map<String, Object> parameters) {
        log.info("Scheduling report of type {} for region {}", reportType, regionCode);
        
        try {
            String url = buildReportingServiceUrl("/api/reports/schedule");
            
            // Create request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("regionCode", regionCode);
            requestBody.put("reportType", reportType);
            requestBody.put("schedule", schedule);
            requestBody.put("parameters", parameters);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            return restTemplate.postForObject(url, entity, String.class);
        } catch (Exception e) {
            log.error("Error scheduling report for region {}: {}", 
                    regionCode, e.getMessage());
            return null;
        }
    }
    
    /**
     * Helper method to build the reporting service URL.
     */
    private String buildReportingServiceUrl(String path) {
        List<org.springframework.cloud.client.ServiceInstance> instances = 
                discoveryClient.getInstances(REPORTING_SERVICE_ID);
        
        if (instances.isEmpty()) {
            log.warn("No instances found for service: {}", REPORTING_SERVICE_ID);
            throw new RuntimeException("Reporting service not available");
        }
        
        org.springframework.cloud.client.ServiceInstance instance = instances.get(0);
        return instance.getUri() + path;
    }
}
