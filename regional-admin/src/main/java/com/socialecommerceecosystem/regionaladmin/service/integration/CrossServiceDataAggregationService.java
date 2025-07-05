package com.gogidix.courier.regionaladmin.service.integration;

import com.microecosystem.courier.shared.dashboard.DashboardDataAggregationService;
import com.microecosystem.courier.shared.dashboard.DashboardDataTransfer;
import com.socialecommerceecosystem.regionaladmin.model.RegionalMetrics;
import com.socialecommerceecosystem.regionaladmin.repository.RegionalMetricsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Service responsible for aggregating data from multiple services.
 * Integrates with all courier-related services to collect and consolidate metrics.
 */
@Service
public class CrossServiceDataAggregationService {

    private static final Logger logger = LoggerFactory.getLogger(CrossServiceDataAggregationService.class);
    
    @Autowired
    private DiscoveryClient discoveryClient;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private RegionalMetricsRepository metricsRepository;
    
    @Autowired
    private DashboardDataAggregationService dashboardAggregationService;
    
    @Value("${regional.admin.region-code}")
    private String regionCode;
    
    @Value("${regional.admin.region-name}")
    private String regionName;
    
    /**
     * Map of services to their data endpoints.
     * This defines which endpoints to call on each service to collect metrics.
     */
    private static final Map<String, String[]> SERVICE_DATA_ENDPOINTS = new HashMap<>();
    
    static {
        SERVICE_DATA_ENDPOINTS.put("courier-management", new String[]{
                "/api/v1/metrics/performance",
                "/api/v1/metrics/utilization"
        });
        
        SERVICE_DATA_ENDPOINTS.put("branch-courier-app", new String[]{
                "/api/v1/branch/metrics",
                "/api/v1/branch/performance"
        });
        
        SERVICE_DATA_ENDPOINTS.put("routing-service", new String[]{
                "/api/v1/routing/metrics",
                "/api/v1/routing/performance"
        });
        
        SERVICE_DATA_ENDPOINTS.put("commission-service", new String[]{
                "/api/v1/commission/metrics"
        });
        
        SERVICE_DATA_ENDPOINTS.put("third-party-integration", new String[]{
                "/api/v1/integration/metrics",
                "/api/v1/integration/provider-stats"
        });
        
        SERVICE_DATA_ENDPOINTS.put("real-time-tracking", new String[]{
                "/api/v1/tracking/metrics",
                "/api/v1/tracking/performance"
        });
        
        SERVICE_DATA_ENDPOINTS.put("advanced-reporting", new String[]{
                "/api/v1/reporting/metrics"
        });
    }

    /**
     * Scheduled task to collect and aggregate data from all integrated services.
     * Runs every 15 minutes by default.
     */
    @Scheduled(fixedDelayString = "${regional.admin.service-integration.aggregation-interval-ms:900000}")
    public void aggregateDataFromAllServices() {
        logger.info("Starting cross-service data aggregation...");
        
        for (Map.Entry<String, String[]> serviceEntry : SERVICE_DATA_ENDPOINTS.entrySet()) {
            String serviceName = serviceEntry.getKey();
            String[] endpoints = serviceEntry.getValue();
            
            aggregateDataFromService(serviceName, endpoints);
        }
        
        logger.info("Cross-service data aggregation completed");
    }
    
    /**
     * Aggregate data from a specific service using its metrics endpoints.
     * 
     * @param serviceName The name of the service to collect data from
     * @param endpoints The endpoints to call on the service
     */
    private void aggregateDataFromService(String serviceName, String[] endpoints) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
        
        if (instances.isEmpty()) {
            logger.warn("No instances found for service: {}", serviceName);
            return;
        }
        
        // Use the first instance - in production, you might want to implement load balancing
        ServiceInstance instance = instances.get(0);
        String baseUrl = instance.getUri().toString();
        
        for (String endpoint : endpoints) {
            try {
                String url = baseUrl + endpoint;
                logger.debug("Fetching data from: {}", url);
                
                // The actual data structure would depend on your API design
                // For this example, we'll assume a map of metrics
                @SuppressWarnings("unchecked")
                Map<String, Object> serviceData = restTemplate.getForObject(url, Map.class);
                
                if (serviceData != null) {
                    processServiceData(serviceName, endpoint, serviceData);
                }
            } catch (Exception e) {
                logger.error("Error fetching data from service {} at endpoint {}: {}", 
                        serviceName, endpoint, e.getMessage(), e);
            }
        }
    }
    
    /**
     * Process the data collected from a service and store it as regional metrics.
     * 
     * @param serviceName The name of the service the data was collected from
     * @param endpoint The endpoint the data was collected from
     * @param serviceData The collected data
     */
    @SuppressWarnings("unchecked")
    private void processServiceData(String serviceName, String endpoint, Map<String, Object> serviceData) {
        logger.debug("Processing data from service: {}, endpoint: {}", serviceName, endpoint);
        
        String metricCategory = determineMetricCategory(serviceName, endpoint);
        LocalDateTime timestamp = LocalDateTime.now();
        
        // Process each metric in the service data
        for (Map.Entry<String, Object> metricEntry : serviceData.entrySet()) {
            String metricName = metricEntry.getKey();
            Object metricValue = metricEntry.getValue();
            
            // Skip non-numeric metrics for simplicity
            // In a real implementation, you might handle different types differently
            if (metricValue instanceof Number) {
                Double value = ((Number) metricValue).doubleValue();
                
                // Create and save a regional metric
                RegionalMetrics metric = RegionalMetrics.builder()
                        .regionCode(regionCode)
                        .regionName(regionName)
                        .metricCategory(metricCategory)
                        .metricName(getServicePrefixedMetricName(serviceName, metricName))
                        .metricValue(value)
                        .dataTimestamp(timestamp)
                        .isActive(true)
                        .build();
                
                // Add metadata for source tracking
                Map<String, String> attributes = new HashMap<>();
                attributes.put("source", serviceName);
                attributes.put("endpoint", endpoint);
                metric.setAttributes(attributes);
                
                metricsRepository.save(metric);
                
                // Also publish to the dashboard aggregation service for real-time updates
                publishToDashboard(metricCategory, metricName, value, serviceName);
            } else if (metricValue instanceof Map) {
                // For nested metrics, process recursively with prefixed names
                Map<String, Object> nestedMetrics = (Map<String, Object>) metricValue;
                for (Map.Entry<String, Object> nestedEntry : nestedMetrics.entrySet()) {
                    String nestedName = metricName + "." + nestedEntry.getKey();
                    Object nestedValue = nestedEntry.getValue();
                    
                    if (nestedValue instanceof Number) {
                        Double value = ((Number) nestedValue).doubleValue();
                        
                        RegionalMetrics metric = RegionalMetrics.builder()
                                .regionCode(regionCode)
                                .regionName(regionName)
                                .metricCategory(metricCategory)
                                .metricName(getServicePrefixedMetricName(serviceName, nestedName))
                                .metricValue(value)
                                .dataTimestamp(timestamp)
                                .isActive(true)
                                .build();
                        
                        Map<String, String> attributes = new HashMap<>();
                        attributes.put("source", serviceName);
                        attributes.put("endpoint", endpoint);
                        attributes.put("parent", metricName);
                        metric.setAttributes(attributes);
                        
                        metricsRepository.save(metric);
                        
                        // Also publish to the dashboard aggregation service for real-time updates
                        publishToDashboard(metricCategory, nestedName, value, serviceName);
                    }
                }
            }
        }
    }
    
    /**
     * Determine the metric category based on the service name and endpoint.
     * 
     * @param serviceName The name of the service
     * @param endpoint The endpoint
     * @return The determined metric category
     */
    private String determineMetricCategory(String serviceName, String endpoint) {
        if (endpoint.contains("performance")) {
            return "DRIVER_PERFORMANCE";
        } else if (endpoint.contains("utilization")) {
            return "OPERATIONAL";
        } else if (serviceName.equals("commission-service")) {
            return "FINANCIAL";
        } else if (endpoint.contains("tracking")) {
            return "DELIVERY";
        } else if (endpoint.contains("metrics")) {
            // Default to OPERATIONAL for general metrics endpoints
            return "OPERATIONAL";
        } else {
            return "OTHER";
        }
    }
    
    /**
     * Create a service-prefixed metric name to avoid naming conflicts.
     * 
     * @param serviceName The name of the service
     * @param metricName The original metric name
     * @return The prefixed metric name
     */
    private String getServicePrefixedMetricName(String serviceName, String metricName) {
        return serviceName.replace("-", ".") + "." + metricName;
    }
    
    /**
     * Publish a metric to the dashboard aggregation service for real-time updates.
     * 
     * @param category The metric category
     * @param name The metric name
     * @param value The metric value
     * @param sourceName The source service name
     */
    private void publishToDashboard(String category, String name, Double value, String sourceName) {
        // Create data and metadata for the dashboard
        Map<String, Object> data = new HashMap<>();
        data.put(name, value);
        
        Map<String, String> metadata = new HashMap<>();
        metadata.put("source", sourceName);
        metadata.put("timestamp", LocalDateTime.now().toString());
        metadata.put("region", regionName);
        
        // Store the data for dashboard aggregation
        dashboardAggregationService.storeAggregatedData(category, data, metadata);
        
        // Also fire an event for real-time listeners
        DashboardDataTransfer transfer = new DashboardDataTransfer();
        transfer.setData(data);
        transfer.setMetadata(metadata);
        
        dashboardAggregationService.publishDataUpdate(category, transfer);
    }
}
