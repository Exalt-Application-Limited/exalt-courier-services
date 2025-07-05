package com.gogidix.courier.regionaladmin.service.integration;

import java.util.List;
import java.util.Map;

/**
 * Interface for distributed tracing integration.
 * Provides methods for integrating with the distributed tracing system.
 */
public interface TracingIntegrationService {

    /**
     * Get service trace summary for a region.
     * 
     * @param regionCode Region code
     * @return Map of service names to trace statistics
     */
    Map<String, Object> getServiceTraceSummary(String regionCode);
    
    /**
     * Get recent traces for a region.
     * 
     * @param regionCode Region code
     * @param limit Maximum number of traces to return
     * @return List of trace data
     */
    List<Map<String, Object>> getRecentTraces(String regionCode, int limit);
    
    /**
     * Get trace details by trace ID.
     * 
     * @param traceId Trace ID
     * @return Trace details
     */
    Map<String, Object> getTraceDetails(String traceId);
    
    /**
     * Get latency metrics for services in a region.
     * 
     * @param regionCode Region code
     * @return Map of service names to latency metrics
     */
    Map<String, Object> getLatencyMetrics(String regionCode);
}
