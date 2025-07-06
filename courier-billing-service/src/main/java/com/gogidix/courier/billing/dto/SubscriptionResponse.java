package com.gogidix.courier.billing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO for subscription operations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionResponse {

    private String subscriptionId;
    
    private String customerId;
    
    private String customerName;
    
    private String subscriptionType; // MONTHLY, QUARTERLY, YEARLY
    
    private String status; // ACTIVE, PAUSED, CANCELLED, EXPIRED
    
    private BigDecimal monthlyFee;
    
    private BigDecimal includedShipments;
    
    private BigDecimal overageRatePerShipment;
    
    private LocalDateTime startDate;
    
    private LocalDateTime endDate;
    
    private LocalDateTime nextBillingDate;
    
    private BigDecimal currentPeriodUsage;
    
    private BigDecimal currentPeriodCharges;
    
    private String billingAddress;
    
    private String paymentMethodId;
    
    private Boolean autoRenewal;
    
    private BigDecimal discountPercentage;
    
    private String promoCode;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}