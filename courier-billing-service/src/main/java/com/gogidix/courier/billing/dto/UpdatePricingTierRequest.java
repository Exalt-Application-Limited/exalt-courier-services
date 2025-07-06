package com.gogidix.courier.billing.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for updating customer pricing tier
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePricingTierRequest {

    @NotNull(message = "Pricing tier is required")
    private String pricingTier; // STANDARD, PREMIUM, ENTERPRISE
    
    @Positive(message = "Discount percentage must be positive")
    private BigDecimal discountPercentage;
    
    @Positive(message = "Volume threshold must be positive")
    private Integer monthlyVolumeThreshold;
    
    private BigDecimal customRatePerKg;
    
    private BigDecimal customRatePerKm;
    
    private String specialTerms;
    
    private Boolean enableCreditTerms;
    
    private Integer creditDaysLimit;
    
    @Positive(message = "Credit limit must be positive")
    private BigDecimal creditLimit;
}