package com.gogidix.courier.corporate.customer.onboarding.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

/**
 * Request DTO for corporate account renewal.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Schema(description = "Request for corporate account renewal")
public record CorporateRenewalRequest(
    
    @NotBlank(message = "New contract terms are required")
    @Schema(description = "Updated contract terms", example = "Extended 24-month contract with enhanced SLA")
    String newContractTerms,
    
    @Positive(message = "Expected volume must be positive")
    @Schema(description = "New expected monthly volume", example = "7500")
    Integer newExpectedVolume,
    
    @Schema(description = "New annual contract value")
    BigDecimal newAnnualValue,
    
    @Schema(description = "Requested volume discount", example = "0.18")
    Double requestedVolumeDiscount,
    
    @Schema(description = "New service requirements")
    String newServiceRequirements,
    
    @Schema(description = "Renewal reason", example = "Business expansion requiring higher volume")
    String renewalReason
) {}