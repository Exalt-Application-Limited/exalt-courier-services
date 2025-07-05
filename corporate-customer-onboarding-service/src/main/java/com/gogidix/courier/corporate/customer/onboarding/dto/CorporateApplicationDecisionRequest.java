package com.gogidix.courier.corporate.customer.onboarding.dto;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request DTO for corporate application decision (approve/reject).
 */
@Schema(description = "Request for corporate application decision")
public record CorporateApplicationDecisionRequest(
    
    @NotBlank(message = "Decision reason is required")
    @Schema(description = "Reason for the decision", example = "All requirements met, documents verified")
    String reason,
    
    @NotBlank(message = "Reviewer identification is required")
    @Schema(description = "ID of the person making the decision", example = "admin@exaltcourier.com")
    String reviewedBy,
    
    @Schema(description = "Contract terms for approved applications")
    String contractTerms,
    
    @Schema(description = "Volume discount percentage", example = "0.15")
    Double volumeDiscount,
    
    @Schema(description = "Additional notes or comments")
    String notes
) {}