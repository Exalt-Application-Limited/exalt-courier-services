package com.gogidix.courier.billing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Request DTO for creating a new subscription
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSubscriptionRequest {

    @NotBlank(message = "Customer ID is required")
    private String customerId;
    
    @NotBlank(message = "Customer name is required")
    private String customerName;
    
    @NotBlank(message = "Customer email is required")
    private String customerEmail;
    
    @NotNull(message = "Subscription type is required")
    private String subscriptionType; // MONTHLY, QUARTERLY, YEARLY
    
    @NotNull(message = "Monthly fee is required")
    @Positive(message = "Monthly fee must be positive")
    private BigDecimal monthlyFee;
    
    @NotNull(message = "Included shipments is required")
    @Positive(message = "Included shipments must be positive")
    private BigDecimal includedShipments;
    
    @Positive(message = "Overage rate must be positive")
    private BigDecimal overageRatePerShipment;
    
    private LocalDateTime startDate;
    
    @NotBlank(message = "Billing address is required")
    private String billingAddress;
    
    @NotBlank(message = "Payment method ID is required")
    private String paymentMethodId;
    
    private Boolean autoRenewal = true;
    
    private BigDecimal discountPercentage;
    
    private String promoCode;
    
    private String specialTerms;
}