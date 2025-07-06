package com.gogidix.courier.corporate.customer.onboarding.enums;

import java.math.BigDecimal;

/**
 * Enumeration of volume discount tiers for corporate customers.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
public enum VolumeDiscountTier {
    BASIC("Basic", new BigDecimal("0.0")),
    BRONZE("Bronze", new BigDecimal("2.5")),
    SILVER("Silver", new BigDecimal("5.0")),
    GOLD("Gold", new BigDecimal("7.5")),
    PLATINUM("Platinum", new BigDecimal("10.0")),
    DIAMOND("Diamond", new BigDecimal("12.5")),
    ENTERPRISE("Enterprise", new BigDecimal("15.0"));

    private final String displayName;
    private final BigDecimal discountPercentage;

    VolumeDiscountTier(String displayName, BigDecimal discountPercentage) {
        this.displayName = displayName;
        this.discountPercentage = discountPercentage;
    }

    public String getDisplayName() {
        return displayName;
    }

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }

    /**
     * Get the minimum annual volume required for this tier.
     */
    public BigDecimal getMinimumAnnualVolume() {
        return switch (this) {
            case BASIC -> new BigDecimal("0");
            case BRONZE -> new BigDecimal("10000");
            case SILVER -> new BigDecimal("25000");
            case GOLD -> new BigDecimal("50000");
            case PLATINUM -> new BigDecimal("100000");
            case DIAMOND -> new BigDecimal("250000");
            case ENTERPRISE -> new BigDecimal("500000");
        };
    }

    /**
     * Check if this tier includes priority handling.
     */
    public boolean includesPriorityHandling() {
        return ordinal() >= GOLD.ordinal();
    }

    /**
     * Check if this tier includes dedicated account management.
     */
    public boolean includesDedicatedAccountManager() {
        return ordinal() >= PLATINUM.ordinal();
    }

    /**
     * Check if this tier includes custom SLA options.
     */
    public boolean includesCustomSLA() {
        return ordinal() >= DIAMOND.ordinal();
    }

    /**
     * Get the next tier up from the current tier.
     */
    public VolumeDiscountTier getNextTier() {
        VolumeDiscountTier[] tiers = values();
        int currentIndex = ordinal();
        if (currentIndex < tiers.length - 1) {
            return tiers[currentIndex + 1];
        }
        return this; // Already at highest tier
    }

    /**
     * Determine the appropriate tier based on annual shipping volume.
     */
    public static VolumeDiscountTier fromAnnualVolume(BigDecimal annualVolume) {
        if (annualVolume.compareTo(new BigDecimal("500000")) >= 0) return ENTERPRISE;
        if (annualVolume.compareTo(new BigDecimal("250000")) >= 0) return DIAMOND;
        if (annualVolume.compareTo(new BigDecimal("100000")) >= 0) return PLATINUM;
        if (annualVolume.compareTo(new BigDecimal("50000")) >= 0) return GOLD;
        if (annualVolume.compareTo(new BigDecimal("25000")) >= 0) return SILVER;
        if (annualVolume.compareTo(new BigDecimal("10000")) >= 0) return BRONZE;
        return BASIC;
    }
}