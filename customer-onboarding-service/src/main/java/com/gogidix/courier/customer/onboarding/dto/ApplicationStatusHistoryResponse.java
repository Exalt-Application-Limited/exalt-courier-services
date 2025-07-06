package com.gogidix.courier.customer.onboarding.dto;

import com.gogidix.courier.customer.onboarding.model.CustomerOnboardingStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Response DTO for application status history.
 */
@Schema(description = "Application status change history")
public record ApplicationStatusHistoryResponse(
    
    @Schema(description = "Status change ID")
    Long id,
    
    @Schema(description = "Previous status")
    CustomerOnboardingStatus fromStatus,
    
    @Schema(description = "New status")
    CustomerOnboardingStatus toStatus,
    
    @Schema(description = "Reason for status change", example = "KYC verification completed")
    String changeReason,
    
    @Schema(description = "Additional notes", example = "All documents verified successfully")
    String notes,
    
    @Schema(description = "Who made the change", example = "system")
    String changedBy,
    
    @Schema(description = "When the change occurred")
    LocalDateTime changedAt
) {}