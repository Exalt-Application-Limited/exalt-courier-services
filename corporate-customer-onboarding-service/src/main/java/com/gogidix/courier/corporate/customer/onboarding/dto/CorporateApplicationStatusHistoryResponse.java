package com.gogidix.courier.corporate.customer.onboarding.dto;

import com.gogidix.courier.corporate.customer.onboarding.model.CorporateOnboardingStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for corporate application status history.
 */
@Schema(description = "Corporate application status history entry")
public record CorporateApplicationStatusHistoryResponse(
    
    @Schema(description = "Status history entry ID")
    UUID id,
    
    @Schema(description = "Previous status")
    CorporateOnboardingStatus fromStatus,
    
    @Schema(description = "New status")
    CorporateOnboardingStatus toStatus,
    
    @Schema(description = "Reason for status change")
    String changeReason,
    
    @Schema(description = "Additional notes")
    String notes,
    
    @Schema(description = "Person who made the change")
    String changedBy,
    
    @Schema(description = "Timestamp of the status change")
    LocalDateTime changedAt
) {}