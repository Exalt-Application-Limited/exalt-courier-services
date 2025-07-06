package com.gogidix.courier.billing.dto;

import com.gogidix.courier.billing.model.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO for payment information.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Schema(description = "Payment information response")
public record PaymentResponse(
    
    @Schema(description = "Payment identifier", example = "PAY-20241201-XYZ789")
    String paymentId,
    
    @Schema(description = "Associated invoice number", example = "INV-20241201-ABC123")
    String invoiceNumber,
    
    @Schema(description = "Customer identifier", example = "CUST-12345")
    String customerId,
    
    @Schema(description = "Payment amount", example = "98.50")
    BigDecimal amount,
    
    @Schema(description = "Payment currency", example = "USD")
    String currency,
    
    @Schema(description = "Payment method type", example = "CREDIT_CARD")
    String paymentMethodType,
    
    @Schema(description = "Payment status")
    PaymentStatus status,
    
    @Schema(description = "Gateway transaction ID", example = "TXN_567890123")
    String gatewayTransactionId,
    
    @Schema(description = "Payment processing timestamp")
    LocalDateTime processedAt,
    
    @Schema(description = "Failure reason if payment failed")
    String failureReason
) {}