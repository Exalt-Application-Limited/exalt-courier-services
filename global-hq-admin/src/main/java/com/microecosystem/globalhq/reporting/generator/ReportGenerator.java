package com.microecosystem.globalhq.reporting.generator;

import com.microecosystem.globalhq.reporting.model.AdvancedReport;
import java.time.LocalDate;

/**
 * Interface for report generators that create advanced reports
 * for the Global HQ Admin application.
 */
public interface ReportGenerator {
    
    /**
     * Generates a complete report for the specified date range and branch.
     *
     * @param startDate The start date of the report period
     * @param endDate The end date of the report period
     * @param branchId The branch ID for which to generate the report, or null for all branches
     * @return The generated report
     */
    AdvancedReport generateReport(LocalDate startDate, LocalDate endDate, String branchId);
}
