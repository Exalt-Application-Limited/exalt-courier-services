package com.gogidix.courier.corporate.customer.onboarding.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Response DTO for corporate onboarding report generation.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Schema(description = "Response for corporate onboarding report")
public record CorporateOnboardingReportResponse(
    
    @Schema(description = "Application reference ID", example = "CORP-ONB-20241201-ABC123")
    String applicationReferenceId,
    
    @Schema(description = "Business name", example = "Acme Corporation Ltd.")
    String businessName,
    
    @Schema(description = "Current onboarding status", example = "ACTIVE")
    String status,
    
    @Schema(description = "Application creation date")
    LocalDateTime createdAt,
    
    @Schema(description = "Account activation date")
    LocalDateTime activatedAt,
    
    @Schema(description = "Total onboarding duration in days", example = "15")
    Long onboardingDurationDays,
    
    @Schema(description = "Executive summary of the onboarding process")
    String summary,
    
    @Schema(description = "URL to download the detailed report")
    String reportUrl,
    
    @Schema(description = "Report generation timestamp")
    LocalDateTime reportGeneratedAt,
    
    @Schema(description = "Onboarding performance metrics")
    OnboardingMetrics metrics,
    
    @Schema(description = "Milestone timeline")
    List<OnboardingMilestone> milestones,
    
    @Schema(description = "Compliance and verification status")
    ComplianceStatus complianceStatus,
    
    @Schema(description = "Financial summary")
    FinancialSummary financialSummary,
    
    @Schema(description = "Service utilization data")
    ServiceUtilization serviceUtilization,
    
    @Schema(description = "Recommendations for improvement")
    List<String> recommendations
) {}

/**
 * Onboarding performance metrics.
 */
@Schema(description = "Onboarding performance metrics")
record OnboardingMetrics(
    
    @Schema(description = "Time to first shipment (days)", example = "3")
    Integer timeToFirstShipment,
    
    @Schema(description = "Time to full activation (days)", example = "15")
    Integer timeToFullActivation,
    
    @Schema(description = "Number of support tickets raised", example = "2")
    Integer supportTicketsRaised,
    
    @Schema(description = "Customer satisfaction score (1-10)", example = "9")
    Integer customerSatisfactionScore,
    
    @Schema(description = "Onboarding efficiency score (0-100)", example = "85")
    Integer efficiencyScore,
    
    @Schema(description = "Number of document resubmissions", example = "0")
    Integer documentResubmissions,
    
    @Schema(description = "Number of status changes", example = "8")
    Integer statusChanges
) {}

/**
 * Onboarding milestone information.
 */
@Schema(description = "Onboarding milestone")
record OnboardingMilestone(
    
    @Schema(description = "Milestone name", example = "Application Submitted")
    String milestoneName,
    
    @Schema(description = "Milestone status", example = "COMPLETED")
    String status,
    
    @Schema(description = "Milestone completion date")
    LocalDateTime completedAt,
    
    @Schema(description = "Days from application start", example = "1")
    Integer daysFromStart,
    
    @Schema(description = "Milestone description")
    String description,
    
    @Schema(description = "Whether milestone was completed on time")
    Boolean onTime
) {}

/**
 * Compliance and verification status.
 */
@Schema(description = "Compliance status")
record ComplianceStatus(
    
    @Schema(description = "KYC verification status", example = "APPROVED")
    String kycStatus,
    
    @Schema(description = "Business registration verification", example = "VERIFIED")
    String businessRegistrationStatus,
    
    @Schema(description = "Tax compliance status", example = "COMPLIANT")
    String taxComplianceStatus,
    
    @Schema(description = "Insurance verification status", example = "VERIFIED")
    String insuranceStatus,
    
    @Schema(description = "Document verification completion rate", example = "100")
    Double documentVerificationRate,
    
    @Schema(description = "Compliance score (0-100)", example = "95")
    Integer complianceScore,
    
    @Schema(description = "Outstanding compliance items")
    List<String> outstandingItems
) {}

/**
 * Financial summary information.
 */
@Schema(description = "Financial summary")
record FinancialSummary(
    
    @Schema(description = "Estimated annual contract value")
    BigDecimal estimatedAnnualValue,
    
    @Schema(description = "Volume discount applied", example = "0.15")
    Double volumeDiscountApplied,
    
    @Schema(description = "Monthly volume committed", example = "5000")
    Integer monthlyVolumeCommitted,
    
    @Schema(description = "Contract duration (months)", example = "12")
    Integer contractDurationMonths,
    
    @Schema(description = "Payment terms", example = "NET_30")
    String paymentTerms,
    
    @Schema(description = "Credit limit assigned")
    BigDecimal creditLimitAssigned,
    
    @Schema(description = "Setup fees charged")
    BigDecimal setupFees
) {}

/**
 * Service utilization data.
 */
@Schema(description = "Service utilization")
record ServiceUtilization(
    
    @Schema(description = "Services activated")
    List<String> activatedServices,
    
    @Schema(description = "Primary service types used")
    Map<String, Integer> serviceTypeUsage,
    
    @Schema(description = "Geographic coverage areas")
    List<String> coverageAreas,
    
    @Schema(description = "Integration methods configured")
    List<String> integrationMethods,
    
    @Schema(description = "API usage statistics")
    Map<String, Object> apiUsageStats,
    
    @Schema(description = "First month shipment volume", example = "1250")
    Integer firstMonthVolume,
    
    @Schema(description = "Volume growth rate", example = "0.15")
    Double volumeGrowthRate
) {}