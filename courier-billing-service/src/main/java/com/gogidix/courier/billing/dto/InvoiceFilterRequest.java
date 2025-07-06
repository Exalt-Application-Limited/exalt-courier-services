package com.gogidix.courier.billing.dto;

import com.gogidix.courier.billing.model.InvoiceStatus;

import java.time.LocalDateTime;

/**
 * Request DTO for filtering invoices.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
public record InvoiceFilterRequest(
        InvoiceStatus status,
        LocalDateTime fromDate,
        LocalDateTime toDate,
        String invoiceType,
        String currency,
        Boolean overdue,
        int page,
        int size,
        String sortBy,
        String sortDirection
) {
    public InvoiceFilterRequest {
        if (page < 0) {
            throw new IllegalArgumentException("Page cannot be negative");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be positive");
        }
    }
}