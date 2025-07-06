package com.gogidix.courier.customer.onboarding.dto;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request DTO for customer account actions (suspend, etc.).
 */
@Schema(description = "Request for customer account action")
public record CustomerAccountActionRequest(
    
    @NotBlank(message = "Action reason is required")
    @Schema(description = "Reason for the action", example = "Policy violation")
    String reason,
    
    @Schema(description = "Additional notes", example = "Customer violated terms of service")
    String notes,
    
    @NotBlank(message = "Action performer is required")
    @Schema(description = "Who performed the action", example = "admin@exaltcourier.com")
    String actionedBy
) {}