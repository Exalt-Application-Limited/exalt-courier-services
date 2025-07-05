package com.gogidix.courier.corporate.customer.onboarding.enums;

import java.math.BigDecimal;

/**
 * Enumeration of annual shipping volumes for corporate customer pricing.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
public enum ShippingVolume {
    LOW("Low (< 100 shipments/year)", new BigDecimal("0"), new BigDecimal("99")),
    MODERATE("Moderate (100-500 shipments/year)", new BigDecimal("100"), new BigDecimal("500")),
    HIGH("High (501-2000 shipments/year)", new BigDecimal("501"), new BigDecimal("2000")),
    VERY_HIGH("Very High (2001-10000 shipments/year)", new BigDecimal("2001"), new BigDecimal("10000")),
    ENTERPRISE("Enterprise (10000+ shipments/year)", new BigDecimal("10000"), null);

    private final String displayName;
    private final BigDecimal minShipments;
    private final BigDecimal maxShipments;

    ShippingVolume(String displayName, BigDecimal minShipments, BigDecimal maxShipments) {
        this.displayName = displayName;
        this.minShipments = minShipments;
        this.maxShipments = maxShipments;
    }

    public String getDisplayName() {
        return displayName;
    }

    public BigDecimal getMinShipments() {
        return minShipments;
    }

    public BigDecimal getMaxShipments() {
        return maxShipments;
    }

    /**
     * Get the expected discount percentage for this volume tier.
     */
    public BigDecimal getDiscountPercentage() {
        return switch (this) {
            case LOW -> new BigDecimal("0.0");
            case MODERATE -> new BigDecimal("2.5");
            case HIGH -> new BigDecimal("5.0");
            case VERY_HIGH -> new BigDecimal("7.5");
            case ENTERPRISE -> new BigDecimal("10.0");
        };
    }

    /**
     * Get the volume discount tier name.
     */
    public String getDiscountTierName() {
        return switch (this) {
            case LOW -> "BASIC";
            case MODERATE -> "BRONZE";
            case HIGH -> "SILVER";
            case VERY_HIGH -> "GOLD";
            case ENTERPRISE -> "PLATINUM";
        };
    }

    /**
     * Check if this volume qualifies for priority support.
     */
    public boolean qualifiesForPrioritySupport() {
        return this == HIGH || this == VERY_HIGH || this == ENTERPRISE;
    }

    /**
     * Check if this volume qualifies for dedicated account management.
     */
    public boolean qualifiesForDedicatedAccountManager() {
        return this == VERY_HIGH || this == ENTERPRISE;
    }

    /**
     * Get the recommended payment terms for this volume.
     */
    public String getRecommendedPaymentTerms() {
        return switch (this) {
            case LOW, MODERATE -> "NET_15";
            case HIGH -> "NET_30";
            case VERY_HIGH -> "NET_45";
            case ENTERPRISE -> "NET_60";
        };
    }

    /**
     * Determine shipping volume based on annual shipment count.
     */
    public static ShippingVolume fromShipmentCount(int annualShipments) {
        if (annualShipments < 100) return LOW;
        if (annualShipments <= 500) return MODERATE;
        if (annualShipments <= 2000) return HIGH;
        if (annualShipments <= 10000) return VERY_HIGH;
        return ENTERPRISE;
    }
}