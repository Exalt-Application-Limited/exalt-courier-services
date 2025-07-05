package com.gogidix.courier.billing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * Request DTO for tax calculation.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Schema(description = "Request for tax calculation")
public record TaxCalculationRequest(
    
    @NotBlank(message = "Billing address is required")
    @Schema(description = "Billing address for tax jurisdiction", 
            example = "123 Main St, Los Angeles, CA 90210, USA")
    String billingAddress,
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    @Schema(description = "Amount to calculate tax on", example = "100.00")
    BigDecimal amount,
    
    @NotBlank(message = "Service type is required")
    @Schema(description = "Type of service for tax classification", example = "COURIER_SERVICE")
    String serviceType,
    
    @Schema(description = "Tax classification", example = "STANDARD", 
            allowableValues = {"STANDARD", "EXPEDITED", "INTERNATIONAL", "FREIGHT"})
    String taxClassification
) {}