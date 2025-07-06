package com.gogidix.courier.customer.onboarding.dto;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request DTO for application decision (approve/reject).
 */
@Schema(description = "Request for application decision")
public record ApplicationDecisionRequest(
    
    @NotBlank(message = "Decision reason is required")
    @Schema(description = "Reason for the decision", example = "All requirements met")
    String reason,
    
    @Schema(description = "Additional notes", example = "Customer documents verified successfully")
    String notes,
    
    @NotBlank(message = "Reviewer information is required")
    @Schema(description = "Who made the decision", example = "admin@exaltcourier.com")
    String reviewedBy
) {}