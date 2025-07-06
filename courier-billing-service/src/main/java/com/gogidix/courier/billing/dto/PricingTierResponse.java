package com.gogidix.courier.billing.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * Response DTO for customer pricing tier information.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Schema(description = "Customer pricing tier information")
public record PricingTierResponse(
    
    @Schema(description = "Pricing tier name", example = "PREMIUM")
    String tierName,
    
    @Schema(description = "Discount percentage", example = "15.0")
    BigDecimal discountPercentage,
    
    @Schema(description = "Tier description", example = "Premium customers with volume discounts")
    String description,
    
    @Schema(description = "Minimum monthly volume for this tier", example = "100")
    Integer minimumVolume,
    
    @Schema(description = "Whether customer qualifies for expedited processing")
    Boolean expeditedProcessing
) {}