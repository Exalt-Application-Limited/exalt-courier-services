package com.gogidix.courier.billing.dto;

import com.gogidix.courier.billing.model.InvoiceStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO for invoice information.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Schema(description = "Invoice information response")
public record InvoiceResponse(
    
    @Schema(description = "Invoice number", example = "INV-20241201-ABC123")
    String invoiceNumber,
    
    @Schema(description = "Customer identifier", example = "CUST-12345")
    String customerId,
    
    @Schema(description = "Customer name", example = "Acme Corporation")
    String customerName,
    
    @Schema(description = "Customer email", example = "billing@acmecorp.com")
    String customerEmail,
    
    @Schema(description = "Billing address")
    String billingAddress,
    
    @Schema(description = "Invoice description")
    String description,
    
    @Schema(description = "Subtotal amount before discounts and taxes", example = "100.00")
    BigDecimal subtotal,
    
    @Schema(description = "Total discount amount", example = "10.00")
    BigDecimal discountAmount,
    
    @Schema(description = "Total tax amount", example = "8.50")
    BigDecimal taxAmount,
    
    @Schema(description = "Total amount due", example = "98.50")
    BigDecimal totalAmount,
    
    @Schema(description = "Currency code", example = "USD")
    String currency,
    
    @Schema(description = "Invoice status")
    InvoiceStatus status,
    
    @Schema(description = "Payment due date")
    LocalDateTime dueDate,
    
    @Schema(description = "Invoice creation date")
    LocalDateTime createdAt,
    
    @Schema(description = "Invoice sent date")
    LocalDateTime sentAt,
    
    @Schema(description = "Invoice paid date")
    LocalDateTime paidAt,
    
    @Schema(description = "Associated shipment ID")
    String shipmentId,
    
    @Schema(description = "Associated subscription ID")
    String subscriptionId,
    
    @Schema(description = "Invoice type", example = "SHIPMENT")
    String invoiceType
) {}