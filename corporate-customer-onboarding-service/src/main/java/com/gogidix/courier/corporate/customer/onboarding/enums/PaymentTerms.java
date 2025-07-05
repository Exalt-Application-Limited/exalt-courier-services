package com.gogidix.courier.corporate.customer.onboarding.enums;

/**
 * Enumeration of payment terms for corporate customers.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
public enum PaymentTerms {
    PREPAID("Prepaid"),
    NET_15("Net 15 days"),
    NET_30("Net 30 days"),
    NET_45("Net 45 days"),
    NET_60("Net 60 days"),
    NET_90("Net 90 days"),
    CUSTOM("Custom terms");

    private final String displayName;

    PaymentTerms(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get the number of days for payment.
     */
    public Integer getPaymentDays() {
        return switch (this) {
            case PREPAID -> 0;
            case NET_15 -> 15;
            case NET_30 -> 30;
            case NET_45 -> 45;
            case NET_60 -> 60;
            case NET_90 -> 90;
            case CUSTOM -> null; // Determined separately
        };
    }

    /**
     * Check if credit approval is required for these terms.
     */
    public boolean requiresCreditApproval() {
        return this != PREPAID;
    }

    /**
     * Check if these are standard terms.
     */
    public boolean isStandardTerms() {
        return this == NET_15 || this == NET_30 || this == NET_45;
    }

    /**
     * Get the risk level associated with these payment terms.
     */
    public String getRiskLevel() {
        return switch (this) {
            case PREPAID -> "NONE";
            case NET_15, NET_30 -> "LOW";
            case NET_45, NET_60 -> "MEDIUM";
            case NET_90 -> "HIGH";
            case CUSTOM -> "VARIABLE";
        };
    }
}