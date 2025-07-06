package com.gogidix.courier.corporate.customer.onboarding.enums;

/**
 * Enumeration of business types for corporate customer onboarding.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
public enum BusinessType {
    CORPORATION("Corporation"),
    LLC("Limited Liability Company"),
    PARTNERSHIP("Partnership"),
    SOLE_PROPRIETORSHIP("Sole Proprietorship"),
    NON_PROFIT("Non-Profit Organization"),
    GOVERNMENT("Government Entity"),
    COOPERATIVE("Cooperative"),
    FRANCHISE("Franchise"),
    OTHER("Other");

    private final String displayName;

    BusinessType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Check if the business type requires special documentation.
     */
    public boolean requiresSpecialDocumentation() {
        return this == NON_PROFIT || this == GOVERNMENT || this == COOPERATIVE;
    }

    /**
     * Check if the business type is eligible for volume discounts.
     */
    public boolean isEligibleForVolumeDiscounts() {
        return this != SOLE_PROPRIETORSHIP;
    }
}