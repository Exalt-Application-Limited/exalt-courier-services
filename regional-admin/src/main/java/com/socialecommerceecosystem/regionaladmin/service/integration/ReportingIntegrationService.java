package com.gogidix.courier.regionaladmin.service.integration;

import java.util.List;
import java.util.Map;

/**
 * Interface for reporting service integration.
 * Provides methods for integrating with the advanced reporting system.
 */
public interface ReportingIntegrationService {

    /**
     * Get available report types.
     * 
     * @return List of report types
     */
    List<String> getAvailableReportTypes();
    
    /**
     * Generate a report for a region.
     * 
     * @param regionCode Region code
     * @param reportType Report type
     * @param parameters Report parameters
     * @return Report data
     */
    Map<String, Object> generateReport(String regionCode, String reportType, Map<String, Object> parameters);
    
    /**
     * Get scheduled reports for a region.
     * 
     * @param regionCode Region code
     * @return List of scheduled reports
     */
    List<Map<String, Object>> getScheduledReports(String regionCode);
    
    /**
     * Schedule a new report.
     * 
     * @param regionCode Region code
     * @param reportType Report type
     * @param schedule Schedule information
     * @param parameters Report parameters
     * @return Scheduled report ID
     */
    String scheduleReport(String regionCode, String reportType, 
                       Map<String, Object> schedule, Map<String, Object> parameters);
}
