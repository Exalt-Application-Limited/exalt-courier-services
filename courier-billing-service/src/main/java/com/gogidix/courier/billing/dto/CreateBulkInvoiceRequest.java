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
 * Request DTO for creating bulk invoices for multiple shipments.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBulkInvoiceRequest {
    
    @NotBlank(message = "Customer ID is required")
    private String customerId;
    
    @NotEmpty(message = "At least one shipment ID is required")
    private List<String> shipmentIds;
    
    @NotNull(message = "Billing period start is required")
    private LocalDate billingPeriodStart;
    
    @NotNull(message = "Billing period end is required")
    private LocalDate billingPeriodEnd;
    
    @NotNull(message = "Due date is required")
    @Future(message = "Due date must be in the future")
    private LocalDate dueDate;
    
    private String description;
    private String poNumber;
    private BigDecimal discountAmount;
    private String discountReason;
    private List<String> additionalCharges;
    private String notes;
    
    @Email(message = "Valid billing email is required")
    private String billingEmail;
    
    private boolean sendImmediately;
    private String currency;
}
