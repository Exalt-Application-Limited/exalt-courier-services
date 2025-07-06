package com.gogidix.courier.customer.onboarding.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response DTO for KYC verification status inquiry.
 */
@Schema(description = "KYC verification status response")
public record KycStatusResponse(
    
    @Schema(description = "KYC verification ID", example = "KYC-VER-2025-001234")
    String verificationId,
    
    @Schema(description = "Current KYC status", example = "IN_PROGRESS")
    String status,
    
    @Schema(description = "Progress percentage", example = "75")
    String progress,
    
    @Schema(description = "Last update timestamp", example = "2025-07-01T16:30:00")
    String lastUpdated,
    
    @Schema(description = "Status message", example = "Document verification in progress")
    String statusMessage,
    
    @Schema(description = "Whether manual review is required", example = "false")
    Boolean requiresManualReview,
    
    @Schema(description = "Next action required", example = "Please wait for document verification to complete")
    String nextAction,
    
    @Schema(description = "Estimated completion time", example = "2025-07-03T10:30:00")
    String estimatedCompletionTime
) {}