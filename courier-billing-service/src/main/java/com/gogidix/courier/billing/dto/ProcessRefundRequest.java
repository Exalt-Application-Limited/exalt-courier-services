package com.gogidix.courier.billing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * Request DTO for processing refunds.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
public record ProcessRefundRequest(
        @NotNull(message = "Refund amount is required")
        @Positive(message = "Refund amount must be positive")
        BigDecimal refundAmount,
        
        @NotBlank(message = "Reason is required")
        String reason,
        
        @NotBlank(message = "Processed by is required")
        String processedBy,
        
        String notes,
        
        RefundType refundType,
        
        boolean notifyCustomer
) {
    public ProcessRefundRequest {
        if (refundAmount != null && refundAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Refund amount must be positive");
        }
    }
    
    public enum RefundType {
        FULL,
        PARTIAL,
        CHARGEBACK,
        GOODWILL
    }
}