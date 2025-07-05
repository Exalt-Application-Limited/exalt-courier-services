package com.gogidix.courier.hqadmin.service.reporting;

import com.socialecommerceecosystem.hqadmin.model.reporting.AdvancedReport;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Interface for report generators
 */
public interface ReportGenerator {
    
    /**
     * Generate a report for the specified time period and regions/branches
     */
    AdvancedReport generateReport(
            LocalDateTime startDate,
            LocalDateTime endDate,
            List<String> regions,
            List<String> branches);
}