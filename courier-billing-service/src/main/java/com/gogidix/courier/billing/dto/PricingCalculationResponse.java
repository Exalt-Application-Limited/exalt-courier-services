package com.gogidix.courier.billing.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Response DTO for pricing calculation.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Schema(description = "Pricing calculation response")
public record PricingCalculationResponse(
    
    @Schema(description = "Base amount before service fees", example = "25.00")
    BigDecimal baseAmount,
    
    @Schema(description = "Additional service fees", example = "5.00")
    BigDecimal serviceFees,
    
    @Schema(description = "Total amount before taxes", example = "30.00")
    BigDecimal totalAmount,
    
    @Schema(description = "Service type", example = "NEXT_DAY")
    String serviceType,
    
    @Schema(description = "Customer pricing tier", example = "PREMIUM")
    String pricingTier,
    
    @Schema(description = "Detailed pricing breakdown")
    Map<String, BigDecimal> pricingBreakdown
) {}