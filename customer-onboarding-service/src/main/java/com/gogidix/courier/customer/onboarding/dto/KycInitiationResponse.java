package com.gogidix.courier.customer.onboarding.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response DTO for KYC verification initiation.
 */
@Schema(description = "KYC verification initiation response")
public record KycInitiationResponse(
    
    @Schema(description = "KYC verification ID", example = "KYC-VER-2025-001234")
    String verificationId,
    
    @Schema(description = "KYC verification status", example = "INITIATED")
    String status,
    
    @Schema(description = "Customer reference ID", example = "CUST-ONB-2025-001234")
    String customerReferenceId,
    
    @Schema(description = "Estimated completion time", example = "2025-07-03T10:30:00")
    String estimatedCompletionTime,
    
    @Schema(description = "Next steps for customer", example = "Please upload your government-issued ID")
    String nextSteps,
    
    @Schema(description = "KYC creation timestamp", example = "2025-07-01T15:45:00")
    String createdAt
) {}