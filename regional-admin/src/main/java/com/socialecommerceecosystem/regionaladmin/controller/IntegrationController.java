package com.exalt.courier.regionaladmin.controller;

import com.socialecommerceecosystem.regionaladmin.service.integration.ReportingIntegrationService;
import com.socialecommerceecosystem.regionaladmin.service.integration.TracingIntegrationService;
import com.socialecommerceecosystem.regionaladmin.service.integration.TrackingIntegrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for service integrations.
 * Provides endpoints for integrating with other services in the ecosystem.
 */
@RestController
@RequestMapping("/api/integration")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Service Integration", description = "APIs for service integration")
public class IntegrationController {

    private final TrackingIntegrationService trackingService;
    private final ReportingIntegrationService reportingService;
    private final TracingIntegrationService tracingService;
    
    /**
     * Get data from multiple integrated services for a comprehensive dashboard.
     * 
     * @param regionCode Region code
     * @return Aggregated data from multiple services
     */
    @GetMapping("/dashboard/{regionCode}")
    @Operation(summary = "Get integrated dashboard data", 
            description = "Returns aggregated data from multiple services for a region")
    public ResponseEntity<Map<String, Object>> getIntegratedDashboardData(
            @PathVariable String regionCode) {
        log.info("Getting integrated dashboard data for region {}", regionCode);
        
        Map<String, Object> result = new HashMap<>();
        
        // Get data from tracking service
        try {
            result.put("tracking", trackingService.getCurrentTrackingStatus(regionCode));
            result.put("trackingStatusSummary", trackingService.getTrackingStatusSummary(regionCode));
            result.put("activeDeliveries", trackingService.getActiveDeliveryCount(regionCode));
            result.put("recentTrackingEvents", trackingService.getTrackingEvents(regionCode, 5));
        } catch (Exception e) {
            log.error("Error integrating with tracking service: {}", e.getMessage());
            result.put("trackingError", "Unable to fetch tracking data: " + e.getMessage());
        }
        
        // Get data from tracing service
        try {
            result.put("tracingSummary", tracingService.getServiceTraceSummary(regionCode));
            result.put("latencyMetrics", tracingService.getLatencyMetrics(regionCode));
            result.put("recentTraces", tracingService.getRecentTraces(regionCode, 5));
        } catch (Exception e) {
            log.error("Error integrating with tracing service: {}", e.getMessage());
            result.put("tracingError", "Unable to fetch tracing data: " + e.getMessage());
        }
        
        // Get data from reporting service
        try {
            result.put("availableReports", reportingService.getAvailableReportTypes());
            result.put("scheduledReports", reportingService.getScheduledReports(regionCode));
        } catch (Exception e) {
            log.error("Error integrating with reporting service: {}", e.getMessage());
            result.put("reportingError", "Unable to fetch reporting data: " + e.getMessage());
        }
        
        // Add metadata
        result.put("regionCode", regionCode);
        result.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * Get tracking data for a region.
     * 
     * @param regionCode Region code
     * @return Tracking data
     */
    @GetMapping("/tracking/{regionCode}")
    @Operation(summary = "Get tracking data", 
            description = "Returns tracking data for a region")
    public ResponseEntity<Map<String, Object>> getTrackingData(
            @PathVariable String regionCode) {
        log.info("Getting tracking data for region {}", regionCode);
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", trackingService.getCurrentTrackingStatus(regionCode));
        result.put("statusSummary", trackingService.getTrackingStatusSummary(regionCode));
        result.put("activeCount", trackingService.getActiveDeliveryCount(regionCode));
        result.put("events", trackingService.getTrackingEvents(regionCode, 10));
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * Get tracing data for a region.
     * 
     * @param regionCode Region code
     * @return Tracing data
     */
    @GetMapping("/tracing/{regionCode}")
    @Operation(summary = "Get tracing data", 
            description = "Returns tracing data for a region")
    public ResponseEntity<Map<String, Object>> getTracingData(
            @PathVariable String regionCode) {
        log.info("Getting tracing data for region {}", regionCode);
        
        Map<String, Object> result = new HashMap<>();
        result.put("summary", tracingService.getServiceTraceSummary(regionCode));
        result.put("latency", tracingService.getLatencyMetrics(regionCode));
        result.put("recentTraces", tracingService.getRecentTraces(regionCode, 10));
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * Get reporting data for a region.
     * 
     * @param regionCode Region code
     * @return Reporting data
     */
    @GetMapping("/reporting/{regionCode}")
    @Operation(summary = "Get reporting data", 
            description = "Returns reporting data for a region")
    public ResponseEntity<Map<String, Object>> getReportingData(
            @PathVariable String regionCode) {
        log.info("Getting reporting data for region {}", regionCode);
        
        Map<String, Object> result = new HashMap<>();
        result.put("availableReports", reportingService.getAvailableReportTypes());
        result.put("scheduledReports", reportingService.getScheduledReports(regionCode));
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * Generate a report for a region.
     * 
     * @param regionCode Region code
     * @param reportType Report type
     * @param parameters Report parameters
     * @return Generated report
     */
    @PostMapping("/reporting/{regionCode}/generate")
    @Operation(summary = "Generate a report", 
            description = "Generates a report for a region")
    public ResponseEntity<Map<String, Object>> generateReport(
            @PathVariable String regionCode,
            @RequestParam String reportType,
            @RequestBody Map<String, Object> parameters) {
        log.info("Generating report of type {} for region {}", reportType, regionCode);
        
        Map<String, Object> report = reportingService.generateReport(
                regionCode, reportType, parameters);
        
        return ResponseEntity.ok(report);
    }
    
    /**
     * Schedule a report for a region.
     * 
     * @param regionCode Region code
     * @param reportType Report type
     * @param requestBody Request containing schedule and parameters
     * @return Scheduled report ID
     */
    @PostMapping("/reporting/{regionCode}/schedule")
    @Operation(summary = "Schedule a report", 
            description = "Schedules a report for a region")
    public ResponseEntity<Map<String, Object>> scheduleReport(
            @PathVariable String regionCode,
            @RequestParam String reportType,
            @RequestBody Map<String, Object> requestBody) {
        log.info("Scheduling report of type {} for region {}", reportType, regionCode);
        
        Map<String, Object> schedule = (Map<String, Object>) requestBody.get("schedule");
        Map<String, Object> parameters = (Map<String, Object>) requestBody.get("parameters");
        
        String reportId = reportingService.scheduleReport(
                regionCode, reportType, schedule, parameters);
        
        Map<String, Object> result = new HashMap<>();
        result.put("reportId", reportId);
        result.put("status", "scheduled");
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * Get trace details by trace ID.
     * 
     * @param traceId Trace ID
     * @return Trace details
     */
    @GetMapping("/tracing/trace/{traceId}")
    @Operation(summary = "Get trace details", 
            description = "Returns details for a specific trace")
    public ResponseEntity<Map<String, Object>> getTraceDetails(
            @PathVariable String traceId) {
        log.info("Getting trace details for trace ID {}", traceId);
        
        Map<String, Object> traceDetails = tracingService.getTraceDetails(traceId);
        
        return ResponseEntity.ok(traceDetails);
    }
}
