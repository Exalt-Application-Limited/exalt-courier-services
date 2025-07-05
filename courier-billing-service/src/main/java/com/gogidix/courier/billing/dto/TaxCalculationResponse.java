package com.gogidix.courier.billing.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Response DTO for tax calculation.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Schema(description = "Tax calculation response")
public record TaxCalculationResponse(
    
    @Schema(description = "Total tax amount", example = "8.50")
    BigDecimal totalTax,
    
    @Schema(description = "Tax rate applied", example = "8.5")
    BigDecimal taxRate,
    
    @Schema(description = "Tax jurisdiction", example = "California, USA")
    String taxJurisdiction,
    
    @Schema(description = "Breakdown of different tax types")
    Map<String, BigDecimal> taxBreakdown,
    
    @Schema(description = "Whether tax is included in the amount")
    Boolean taxIncluded,
    
    @Schema(description = "Tax calculation method used", example = "DESTINATION_BASED")
    String calculationMethod
) {}