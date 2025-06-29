package com.microecosystem.globalhq.reporting.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents an advanced report in the Global HQ Admin application.
 * Converted to use Lombok annotations for reduced boilerplate.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdvancedReport {
    private String id;
    private String title;
    private LocalDateTime generatedDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private String branchId;
    private String reportType;
    private List<ReportSection> sections;
    private String createdBy;
    
    @Builder.Default
    private boolean archived = false;
    
    /**
     * Factory method to create a new report with default values
     * 
     * @param title the report title
     * @param reportType the type of report
     * @param branchId the branch ID
     * @param startDate the start date for the report period
     * @param endDate the end date for the report period
     * @param createdBy the user who created the report
     * @return a new AdvancedReport instance
     */
    public static AdvancedReport createReport(String title, String reportType, String branchId,
                                              LocalDate startDate, LocalDate endDate, String createdBy) {
        return AdvancedReport.builder()
                .title(title)
                .reportType(reportType)
                .branchId(branchId)
                .startDate(startDate)
                .endDate(endDate)
                .createdBy(createdBy)
                .generatedDate(LocalDateTime.now())
                .archived(false)
                .build();
    }
}