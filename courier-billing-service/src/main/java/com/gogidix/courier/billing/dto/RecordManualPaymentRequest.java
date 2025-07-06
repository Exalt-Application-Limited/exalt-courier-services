package com.gogidix.courier.billing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * Request DTO for recording manual payments.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
public record RecordManualPaymentRequest(
        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be positive")
        BigDecimal amount,
        
        @NotBlank(message = "Currency is required")
        String currency,
        
        @NotBlank(message = "Payment method is required")
        String paymentMethod,
        
        String notes,
        
        @NotBlank(message = "Recorded by is required")
        String recordedBy,
        
        String referenceNumber,
        
        String receiptNumber
) {
    public RecordManualPaymentRequest {
        if (amount != null && amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }
}