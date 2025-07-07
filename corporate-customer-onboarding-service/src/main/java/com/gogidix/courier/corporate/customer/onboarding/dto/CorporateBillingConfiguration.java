package com.gogidix.courier.corporate.customer.onboarding.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CorporateBillingConfiguration {
    
    @NotNull(message = "Billing cycle is required")
    @Pattern(regexp = "DAILY|WEEKLY|MONTHLY|QUARTERLY|YEARLY", message = "Invalid billing cycle")
    private String billingCycle;
    
    @NotNull(message = "Payment method is required")
    @Pattern(regexp = "CREDIT|BANK_TRANSFER|AUTO_DEBIT|CHECK", message = "Invalid payment method")
    private String paymentMethod;
    
    @DecimalMin(value = "0.0", message = "Credit limit must be positive")
    @DecimalMax(value = "10000000.0", message = "Credit limit exceeded maximum allowed")
    private BigDecimal creditLimit;
    
    @NotNull(message = "Currency is required")
    @Pattern(regexp = "[A-Z]{3}", message = "Currency must be 3-letter ISO code")
    private String currency;
    
    private Boolean autoRenewal;
    
    @DecimalMin(value = "0.0", message = "Discount percentage must be positive")
    @DecimalMax(value = "100.0", message = "Discount percentage cannot exceed 100%")
    private BigDecimal discountPercentage;
    
    private LocalDate contractStartDate;
    
    private LocalDate contractEndDate;
    
    private List<String> invoiceDeliveryEmails;
    
    private Map<String, BigDecimal> volumeDiscountTiers;
    
    private String taxId;
    
    private String billingAddressId;
    
    private Boolean consolidatedBilling;
    
    private Integer paymentTermsDays;
    
    private BigDecimal lateFeePercentage;
    
    private Map<String, Object> customBillingRules;
}