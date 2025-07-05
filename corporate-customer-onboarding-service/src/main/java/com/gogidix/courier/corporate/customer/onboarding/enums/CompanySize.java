package com.gogidix.courier.corporate.customer.onboarding.enums;

/**
 * Enumeration of company sizes for corporate customer segmentation.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
public enum CompanySize {
    STARTUP("Startup (1-10 employees)"),
    SMALL("Small (11-50 employees)"),
    MEDIUM("Medium (51-200 employees)"),
    LARGE("Large (201-1000 employees)"),
    ENTERPRISE("Enterprise (1000+ employees)");

    private final String displayName;

    CompanySize(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get the minimum employee count for this company size.
     */
    public int getMinEmployeeCount() {
        return switch (this) {
            case STARTUP -> 1;
            case SMALL -> 11;
            case MEDIUM -> 51;
            case LARGE -> 201;
            case ENTERPRISE -> 1000;
        };
    }

    /**
     * Get the maximum employee count for this company size.
     */
    public Integer getMaxEmployeeCount() {
        return switch (this) {
            case STARTUP -> 10;
            case SMALL -> 50;
            case MEDIUM -> 200;
            case LARGE -> 999;
            case ENTERPRISE -> null; // No upper limit
        };
    }

    /**
     * Check if the company size qualifies for enterprise features.
     */
    public boolean qualifiesForEnterpriseFeatures() {
        return this == LARGE || this == ENTERPRISE;
    }

    /**
     * Get the default credit limit tier for this company size.
     */
    public String getDefaultCreditTier() {
        return switch (this) {
            case STARTUP -> "BASIC";
            case SMALL -> "STANDARD";
            case MEDIUM -> "ENHANCED";
            case LARGE -> "PREMIUM";
            case ENTERPRISE -> "ENTERPRISE";
        };
    }

    /**
     * Check if dedicated account management is included.
     */
    public boolean includesDedicatedAccountManager() {
        return this == LARGE || this == ENTERPRISE;
    }
}