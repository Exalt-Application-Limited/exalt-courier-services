package com.gogidix.courierservices.payout.$1;

/**
 * Enum representing the types of earnings entries.
 */
public enum EarningsType {
    /**
     * Base pay for delivery
     */
    BASE_PAY,
    
    /**
     * Distance-based earnings
     */
    DISTANCE_BONUS,
    
    /**
     * Time-based earnings
     */
    TIME_BONUS,
    
    /**
     * Bonus for peak hours
     */
    PEAK_HOUR_BONUS,
    
    /**
     * Bonus for working in a high-demand zone
     */
    HIGH_DEMAND_ZONE_BONUS,
    
    /**
     * Tips from customers
     */
    TIPS,
    
    /**
     * Performance-based bonus
     */
    PERFORMANCE_BONUS,
    
    /**
     * Surge pricing bonus
     */
    SURGE_PRICING,
    
    /**
     * Adjustment (e.g., correction, compensation)
     */
    ADJUSTMENT,
    
    /**
     * Other type of earnings not listed
     */
    OTHER
}
