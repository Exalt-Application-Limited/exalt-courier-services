package com.gogidix.courier.billing.model;

/**
 * Enumeration for payment status values.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
public enum PaymentStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED,
    CANCELLED,
    REFUNDED,
    DISPUTED
}