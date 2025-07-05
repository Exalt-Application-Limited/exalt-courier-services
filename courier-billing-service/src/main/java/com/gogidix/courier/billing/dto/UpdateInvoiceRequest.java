package com.gogidix.courier.billing.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Request DTO for updating invoices.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
public record UpdateInvoiceRequest(
        String customerName,
        String customerEmail,
        String billingAddress,
        String description,
        LocalDateTime dueDate,
        String currency,
        Map<String, Object> metadata,
        
        @NotBlank(message = "Updated by is required")
        String updatedBy
) {}