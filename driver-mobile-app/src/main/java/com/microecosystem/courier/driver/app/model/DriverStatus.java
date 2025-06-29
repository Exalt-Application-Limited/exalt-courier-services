package com.microecosystem.courier.driver.app.model;

/**
 * Enumeration of possible driver statuses.
 */
public enum DriverStatus {
    /**
     * Driver account is inactive
     */
    INACTIVE,
    
    /**
     * Driver is available for deliveries
     */
    AVAILABLE,
    
    /**
     * Driver is currently on a delivery
     */
    ON_DELIVERY,
    
    /**
     * Driver is on break
     */
    ON_BREAK,
    
    /**
     * Driver is offline
     */
    OFFLINE,
    
    /**
     * Driver account is suspended
     */
    SUSPENDED
} 