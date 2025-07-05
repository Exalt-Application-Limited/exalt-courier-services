package com.gogidix.courier.corporate.customer.onboarding.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * Response DTO for corporate account renewal.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Schema(description = "Response for corporate account renewal")
public record CorporateRenewalResponse(
    
    @Schema(description = "Renewal status", example = "RENEWED", allowableValues = {"RENEWED", "PENDING", "REJECTED"})
    String status,
    
    @Schema(description = "Updated contract terms")
    String contractTerms,
    
    @Schema(description = "New expected monthly volume", example = "7500")
    Integer expectedVolume,
    
    @Schema(description = "Applied volume discount", example = "0.18")
    Double volumeDiscount,
    
    @Schema(description = "New contract effective date")
    LocalDateTime renewalDate
) {}