package com.gogidix.courier.corporate.customer.onboarding.dto;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request DTO for corporate account actions (suspend, activate, etc.).
 */
@Schema(description = "Request for corporate account actions")
public record CorporateAccountActionRequest(
    
    @NotBlank(message = "Action reason is required")
    @Schema(description = "Reason for the action", example = "Suspected fraudulent activity")
    String reason,
    
    @NotBlank(message = "Actioned by is required")
    @Schema(description = "Person performing the action", example = "admin@exaltcourier.com")
    String actionedBy,
    
    @Schema(description = "Additional notes")
    String notes,
    
    @Schema(description = "Whether to notify all corporate users")
    Boolean notifyUsers
) {}