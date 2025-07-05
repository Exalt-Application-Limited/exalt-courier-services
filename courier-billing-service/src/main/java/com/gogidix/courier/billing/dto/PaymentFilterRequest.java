package com.gogidix.courier.billing.dto;

import com.gogidix.courier.billing.model.PaymentStatus;

import java.time.LocalDateTime;

/**
 * Request DTO for filtering payments.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
public record PaymentFilterRequest(
        PaymentStatus status,
        LocalDateTime fromDate,
        LocalDateTime toDate,
        String paymentMethodType,
        String currency,
        int page,
        int size,
        String sortBy,
        String sortDirection
) {
    public PaymentFilterRequest {
        if (page < 0) {
            throw new IllegalArgumentException("Page cannot be negative");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be positive");
        }
    }
}