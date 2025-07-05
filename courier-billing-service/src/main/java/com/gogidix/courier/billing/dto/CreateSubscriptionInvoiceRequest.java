package com.gogidix.courier.billing.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Request DTO for creating subscription invoices.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSubscriptionInvoiceRequest {
    
    @NotBlank(message = "Subscription ID is required")
    private String subscriptionId;
    
    @NotNull(message = "Billing period start is required")
    private LocalDate billingPeriodStart;
    
    @NotNull(message = "Billing period end is required")
    private LocalDate billingPeriodEnd;
    
    @NotNull(message = "Due date is required")
    @Future(message = "Due date must be in the future")
    private LocalDate dueDate;
    
    private String description;
    private BigDecimal prorationAmount;
    private String prorationReason;
    private List<String> additionalServices;
    private BigDecimal adjustmentAmount;
    private String adjustmentReason;
    private String notes;
    
    @Email(message = "Valid billing email is required")
    private String billingEmail;
    
    private boolean sendImmediately;
    private String currency;
    private boolean includeUsageCharges;
}
