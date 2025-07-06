package com.gogidix.courier.billing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * Request DTO for pricing calculation.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Schema(description = "Request for pricing calculation")
public record PricingCalculationRequest(
    
    @NotBlank(message = "Service type is required")
    @Schema(description = "Type of courier service", example = "NEXT_DAY")
    String serviceType,
    
    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be positive")
    @Schema(description = "Package weight in kg", example = "2.5")
    BigDecimal weight,
    
    @Schema(description = "Package dimensions (L x W x H in cm)", example = "30x20x15")
    String dimensions,
    
    @NotBlank(message = "Origin is required")
    @Schema(description = "Origin address or zone", example = "Los Angeles, CA")
    String origin,
    
    @NotBlank(message = "Destination is required")
    @Schema(description = "Destination address or zone", example = "New York, NY")
    String destination,
    
    @Schema(description = "Declared value for insurance pricing", example = "500.00")
    BigDecimal declaredValue,
    
    @Schema(description = "Customer ID for pricing tier lookup", example = "CUST-12345")
    String customerId
) {}