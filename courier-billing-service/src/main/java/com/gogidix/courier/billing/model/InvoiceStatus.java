package com.gogidix.courier.billing.model;

/**
 * Enumeration for invoice status values.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
public enum InvoiceStatus {
    DRAFT,
    SENT,
    PAID,
    PARTIALLY_PAID,
    OVERDUE,
    CANCELLED,
    REFUNDED,
    PARTIALLY_REFUNDED
}