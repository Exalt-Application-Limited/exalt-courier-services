package com.gogidix.courier.corporate.customer.onboarding.enums;

/**
 * Enumeration of industry sectors for corporate customer classification.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
public enum IndustrySector {
    TECHNOLOGY("Technology"),
    HEALTHCARE("Healthcare"),
    FINANCE("Finance & Banking"),
    RETAIL("Retail & E-commerce"),
    MANUFACTURING("Manufacturing"),
    LOGISTICS("Logistics & Transportation"),
    EDUCATION("Education"),
    GOVERNMENT("Government"),
    HOSPITALITY("Hospitality & Tourism"),
    REAL_ESTATE("Real Estate"),
    AUTOMOTIVE("Automotive"),
    ENERGY("Energy & Utilities"),
    TELECOMMUNICATIONS("Telecommunications"),
    MEDIA("Media & Entertainment"),
    AGRICULTURE("Agriculture"),
    CONSTRUCTION("Construction"),
    CONSULTING("Consulting & Professional Services"),
    NON_PROFIT("Non-Profit"),
    OTHER("Other");

    private final String displayName;

    IndustrySector(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Check if the industry sector has high shipping volume potential.
     */
    public boolean hasHighShippingVolume() {
        return this == RETAIL || this == MANUFACTURING || this == LOGISTICS || 
               this == TECHNOLOGY || this == HEALTHCARE;
    }

    /**
     * Check if the industry sector requires special handling or compliance.
     */
    public boolean requiresSpecialHandling() {
        return this == HEALTHCARE || this == GOVERNMENT || this == FINANCE || this == ENERGY;
    }

    /**
     * Get the typical discount tier for this industry sector.
     */
    public String getTypicalDiscountTier() {
        if (hasHighShippingVolume()) {
            return "PREMIUM";
        } else if (this == GOVERNMENT || this == NON_PROFIT) {
            return "STANDARD";
        } else {
            return "BASIC";
        }
    }
}