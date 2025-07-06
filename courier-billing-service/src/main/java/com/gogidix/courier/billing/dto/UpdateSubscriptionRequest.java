package com.gogidix.courier.billing.dto;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Request DTO for updating an existing subscription
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSubscriptionRequest {

    private String subscriptionType; // MONTHLY, QUARTERLY, YEARLY
    
    private String status; // ACTIVE, PAUSED, CANCELLED, EXPIRED
    
    @Positive(message = "Monthly fee must be positive")
    private BigDecimal monthlyFee;
    
    @Positive(message = "Included shipments must be positive")
    private BigDecimal includedShipments;
    
    @Positive(message = "Overage rate must be positive")
    private BigDecimal overageRatePerShipment;
    
    private LocalDateTime endDate;
    
    private String billingAddress;
    
    private String paymentMethodId;
    
    private Boolean autoRenewal;
    
    private BigDecimal discountPercentage;
    
    private String promoCode;
    
    private String specialTerms;
    
    private String updateReason;
}