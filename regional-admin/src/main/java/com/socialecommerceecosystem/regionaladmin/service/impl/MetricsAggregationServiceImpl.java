package com.gogidix.courier.regionaladmin.service.impl;

import com.socialecommerceecosystem.regionaladmin.config.dashboard.RegionalMetricsDataProvider;
import com.socialecommerceecosystem.regionaladmin.dto.RegionalMetricsDTO;
import com.socialecommerceecosystem.regionaladmin.model.RegionalMetrics;
import com.socialecommerceecosystem.regionaladmin.repository.RegionalMetricsRepository;
import com.socialecommerceecosystem.regionaladmin.service.MetricsAggregationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of the MetricsAggregationService interface.
 * Handles collection, aggregation, and retrieval of regional metrics data.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MetricsAggregationServiceImpl implements MetricsAggregationService {

    private final RegionalMetricsRepository metricsRepository;
    private final RegionalMetricsDataProvider metricsDataProvider;
    private final RestTemplate restTemplate;
    private final DiscoveryClient discoveryClient;
    private final ModelMapper modelMapper;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public RegionalMetricsDTO aggregateMetrics(Map<String, Double> sourceData, 
                                           String metricName, 
                                           String regionCode, 
                                           String aggregationMethod) {
        log.info("Aggregating metrics for {} in region {} using method {}", 
                metricName, regionCode, aggregationMethod);
        
        // Get region name from code (assuming a lookup service or a map)
        String regionName = getRegionNameFromCode(regionCode);
        
        Double aggregatedValue = 0.0;
        
        // Perform aggregation based on the specified method
        switch (aggregationMethod.toUpperCase()) {
            case "SUM":
                aggregatedValue = sourceData.values().stream().mapToDouble(Double::doubleValue).sum();
                break;
            case "AVG":
                aggregatedValue = sourceData.values().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
                break;
            case "MIN":
                aggregatedValue = sourceData.values().stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
                break;
            case "MAX":
                aggregatedValue = sourceData.values().stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
                break;
            default:
                log.warn("Unknown aggregation method: {}. Using SUM as default.", aggregationMethod);
                aggregatedValue = sourceData.values().stream().mapToDouble(Double::doubleValue).sum();
        }
        
        // Create and save the aggregated metric
        RegionalMetrics metric = RegionalMetrics.builder()
                .regionCode(regionCode)
                .regionName(regionName)
                .metricName(metricName)
                .metricValue(aggregatedValue)
                .dataTimestamp(LocalDateTime.now())
                .branchCount(sourceData.size())
                .aggregationMethod(aggregationMethod.toUpperCase())
                .isActive(true)
                .build();
        
        RegionalMetrics savedMetric = metricsRepository.save(metric);
        return modelMapper.map(savedMetric, RegionalMetricsDTO.class);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Scheduled(fixedRateString = "${metrics.aggregation.interval:300000}")
    public void collectAndAggregateMetrics() {
        log.info("Starting scheduled metrics collection and aggregation");
        
        // Get all branch/courier service instances
        List<String> branchServices = discoveryClient.getServices().stream()
                .filter(s -> s.startsWith("branch-") || s.startsWith("courier-"))
                .collect(Collectors.toList());
        
        // List of metrics to collect and aggregate
        List<String> metricsToCollect = List.of(
                "delivery-time", "delivery-success-rate", "driver-utilization",
                "customer-satisfaction", "operational-cost", "revenue"
        );
        
        // For each region code that this regional admin is responsible for
        List<String> regionCodes = getResponsibleRegionCodes();
        
        for (String regionCode : regionCodes) {
            for (String metricName : metricsToCollect) {
                // Collect data from each service
                Map<String, Double> sourceData = collectMetricFromServices(branchServices, metricName, regionCode);
                
                // Determine appropriate aggregation method based on metric
                String aggregationMethod = determineAggregationMethod(metricName);
                
                // Aggregate and save
                aggregateMetrics(sourceData, metricName, regionCode, aggregationMethod);
            }
        }
        
        log.info("Finished scheduled metrics collection and aggregation");
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<RegionalMetricsDTO> getMetricsByRegionAndCategory(String regionCode, String category) {
        List<RegionalMetrics> metrics = metricsRepository.findByRegionCodeAndMetricCategory(regionCode, category);
        return metrics.stream()
                .map(m -> modelMapper.map(m, RegionalMetricsDTO.class))
                .collect(Collectors.toList());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<RegionalMetricsDTO> getMetricsForRegionInTimeRange(String regionCode, 
                                                              LocalDateTime startTime, 
                                                              LocalDateTime endTime) {
        List<RegionalMetrics> metrics = metricsRepository.findMetricsForRegionInTimeRange(
                regionCode, startTime, endTime);
        
        return metrics.stream()
                .map(m -> modelMapper.map(m, RegionalMetricsDTO.class))
                .collect(Collectors.toList());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public RegionalMetricsDTO saveRegionalMetric(RegionalMetricsDTO metricsDTO) {
        RegionalMetrics metrics = modelMapper.map(metricsDTO, RegionalMetrics.class);
        RegionalMetrics savedMetrics = metricsRepository.save(metrics);
        return modelMapper.map(savedMetrics, RegionalMetricsDTO.class);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Double> getComparativeMetricsAcrossRegions(String metricName, 
                                                             LocalDateTime startTime, 
                                                             LocalDateTime endTime) {
        // Get all region codes that this admin is responsible for
        List<String> regionCodes = getResponsibleRegionCodes();
        
        // Collect latest value for the metric for each region
        Map<String, Double> regionMetricsMap = new HashMap<>();
        
        for (String regionCode : regionCodes) {
            List<RegionalMetrics> metrics = metricsRepository.findByMetricNameAndRegionCodeAndDataTimestampBetween(
                    metricName, regionCode, startTime, endTime);
            
            // If we have metrics, use the most recent value
            if (!metrics.isEmpty()) {
                RegionalMetrics mostRecent = metrics.stream()
                        .max((m1, m2) -> m1.getDataTimestamp().compareTo(m2.getDataTimestamp()))
                        .orElse(null);
                
                if (mostRecent != null) {
                    regionMetricsMap.put(regionCode, mostRecent.getMetricValue());
                }
            }
        }
        
        return regionMetricsMap;
    }
    
    /**
     * Helper method to get region name from region code.
     * In a real implementation, this might use a lookup service or a repository.
     */
    private String getRegionNameFromCode(String regionCode) {
        // This would typically look up the region name from a database or config
        // For now, we'll just return a formatted version of the code
        return "Region " + regionCode.toUpperCase();
    }
    
    /**
     * Helper method to determine the appropriate aggregation method for a metric.
     */
    private String determineAggregationMethod(String metricName) {
        // Different metrics might require different aggregation methods
        switch (metricName.toLowerCase()) {
            case "delivery-time":
                return "AVG";
            case "delivery-success-rate":
                return "AVG";
            case "driver-utilization":
                return "AVG";
            case "customer-satisfaction":
                return "AVG";
            case "operational-cost":
                return "SUM";
            case "revenue":
                return "SUM";
            default:
                return "AVG";
        }
    }
    
    /**
     * Helper method to get the list of region codes this regional admin is responsible for.
     */
    private List<String> getResponsibleRegionCodes() {
        // In a real implementation, this would fetch from configuration or database
        // For now, we'll return a static list
        return List.of("north", "south", "east", "west");
    }
    
    /**
     * Helper method to collect a specific metric from multiple services.
     */
    private Map<String, Double> collectMetricFromServices(List<String> serviceIds, 
                                                      String metricName, 
                                                      String regionCode) {
        Map<String, Double> results = new HashMap<>();
        
        for (String serviceId : serviceIds) {
            try {
                // Get service instances for this service ID
                List<org.springframework.cloud.client.ServiceInstance> instances = 
                        discoveryClient.getInstances(serviceId);
                
                if (!instances.isEmpty()) {
                    // Use the first instance (in production, might use load balancing)
                    org.springframework.cloud.client.ServiceInstance instance = instances.get(0);
                    
                    // Build URL for metrics endpoint
                    String url = instance.getUri() + "/api/metrics/" + metricName + 
                            "?regionCode=" + regionCode;
                    
                    // Call the service and get the metric value
                    Double value = restTemplate.getForObject(url, Double.class);
                    
                    if (value != null) {
                        results.put(serviceId, value);
                    }
                }
            } catch (Exception e) {
                log.error("Error collecting metric {} from service {}: {}", 
                        metricName, serviceId, e.getMessage());
            }
        }
        
        return results;
    }
}
