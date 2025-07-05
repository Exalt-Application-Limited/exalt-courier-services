package com.gogidix.courier.billing.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO for refund operations.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
public record RefundResponse(
        String refundId,
        String originalPaymentId,
        String invoiceNumber,
        BigDecimal refundAmount,
        String currency,
        String status,
        String gatewayTransactionId,
        LocalDateTime processedAt,
        String reason,
        String processedBy,
        boolean isFullRefund
) {}