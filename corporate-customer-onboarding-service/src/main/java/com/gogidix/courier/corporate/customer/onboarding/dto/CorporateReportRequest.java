package com.gogidix.courier.corporate.customer.onboarding.dto;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Request DTO for generating corporate onboarding reports.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Schema(description = "Request for corporate onboarding report generation")
public record CorporateReportRequest(
    
    @NotBlank(message = "Report type is required")
    @Schema(description = "Type of report to generate", example = "ONBOARDING_SUMMARY", 
            allowableValues = {"ONBOARDING_SUMMARY", "PERFORMANCE_METRICS", "COMPLIANCE_REPORT", "FINANCIAL_SUMMARY", "CUSTOM"})
    String reportType,
    
    @Schema(description = "Report start date")
    LocalDateTime fromDate,
    
    @Schema(description = "Report end date") 
    LocalDateTime toDate,
    
    @Schema(description = "Report format", example = "PDF", allowableValues = {"PDF", "EXCEL", "CSV", "JSON"})
    String format,
    
    @Schema(description = "Report delivery method", example = "EMAIL", allowableValues = {"EMAIL", "DOWNLOAD", "API"})
    String deliveryMethod,
    
    @Schema(description = "Report recipient email addresses")
    List<String> recipients,
    
    @Schema(description = "Specific metrics to include in the report")
    List<String> includeMetrics,
    
    @Schema(description = "Data filters for the report")
    ReportFilters filters,
    
    @Schema(description = "Custom report parameters")
    CustomReportParameters customParameters
) {}

/**
 * Report filters for data selection.
 */
@Schema(description = "Report filters")
record ReportFilters(
    
    @Schema(description = "Filter by business types")
    List<String> businessTypes,
    
    @Schema(description = "Filter by onboarding status")
    List<String> onboardingStatuses,
    
    @Schema(description = "Filter by volume ranges")
    VolumeRange volumeRange,
    
    @Schema(description = "Filter by geographic regions")
    List<String> regions,
    
    @Schema(description = "Filter by industry sectors")
    List<String> industrySectors,
    
    @Schema(description = "Minimum contract value filter")
    Double minContractValue,
    
    @Schema(description = "Maximum contract value filter")
    Double maxContractValue
) {}

/**
 * Volume range filter.
 */
@Schema(description = "Volume range filter")
record VolumeRange(
    
    @Schema(description = "Minimum monthly volume")
    Integer minVolume,
    
    @Schema(description = "Maximum monthly volume")
    Integer maxVolume
) {}

/**
 * Custom report parameters.
 */
@Schema(description = "Custom report parameters")
record CustomReportParameters(
    
    @Schema(description = "Custom report title")
    String reportTitle,
    
    @Schema(description = "Include executive summary")
    Boolean includeExecutiveSummary,
    
    @Schema(description = "Include detailed breakdowns")
    Boolean includeDetailedBreakdowns,
    
    @Schema(description = "Include charts and graphs")
    Boolean includeVisualizations,
    
    @Schema(description = "Include recommendations")
    Boolean includeRecommendations,
    
    @Schema(description = "Custom report sections")
    List<String> customSections,
    
    @Schema(description = "Report template to use")
    String reportTemplate
) {}