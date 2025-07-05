package com.gogidix.courier.billing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * Request DTO for sending invoices.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
public record SendInvoiceRequest(
        @NotEmpty(message = "At least one recipient is required")
        List<String> recipients,
        
        String customMessage,
        
        boolean attachPdf,
        
        boolean notifyInternalTeam,
        
        @NotBlank(message = "Sent by is required")
        String sentBy
) {}