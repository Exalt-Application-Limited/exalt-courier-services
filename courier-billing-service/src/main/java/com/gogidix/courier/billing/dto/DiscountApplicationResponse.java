package com.gogidix.courier.billing.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * Response DTO for discount application.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Schema(description = "Discount application response")
public record DiscountApplicationResponse(
    
    @Schema(description = "Discount percentage applied", example = "10.0")
    BigDecimal discountPercentage,
    
    @Schema(description = "Discount amount", example = "10.00")
    BigDecimal discountAmount,
    
    @Schema(description = "Final amount after discount", example = "90.00")
    BigDecimal finalAmount,
    
    @Schema(description = "Type of discount applied", example = "VOLUME_DISCOUNT")
    String discountType,
    
    @Schema(description = "Discount description", example = "Volume discount applied for 25 shipments")
    String description
) {}