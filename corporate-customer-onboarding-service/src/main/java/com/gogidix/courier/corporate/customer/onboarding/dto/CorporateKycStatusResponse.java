package com.gogidix.courier.corporate.customer.onboarding.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Response DTO for corporate KYC verification status.
 */
@Schema(description = "Response for corporate KYC verification status")
public record CorporateKycStatusResponse(
    
    @Schema(description = "KYC verification ID", example = "KYC-CORP-20241201-ABC123")
    String verificationId,
    
    @Schema(description = "Current KYC status", example = "IN_PROGRESS")
    String status,
    
    @Schema(description = "Verification progress percentage", example = "65")
    Integer progress,
    
    @Schema(description = "Last status update timestamp")
    LocalDateTime lastUpdated,
    
    @Schema(description = "Detailed status message")
    String statusMessage,
    
    @Schema(description = "Whether manual review is required")
    Boolean requiresManualReview,
    
    @Schema(description = "Next action required")
    String nextAction,
    
    @Schema(description = "Business verification details")
    Map<String, Object> businessVerificationDetails,
    
    @Schema(description = "Compliance check results")
    Map<String, String> complianceChecks,
    
    @Schema(description = "Estimated completion time")
    String estimatedCompletionTime
) {}