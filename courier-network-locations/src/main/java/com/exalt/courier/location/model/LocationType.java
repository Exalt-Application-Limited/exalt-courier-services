package com.exalt.courier.location.model;

/**
 * Represents the type of a physical courier network location.
 * Different location types may have different capabilities and operation models.
 */
public enum LocationType {
    /**
     * Main branch office with full service capabilities.
     */
    BRANCH_OFFICE("Branch Office"),
    
    /**
     * Central sorting facility for packages.
     */
    SORTING_CENTER("Sorting Center"),
    
    /**
     * Distribution hub for handling large volumes of packages.
     */
    DISTRIBUTION_HUB("Distribution Hub"),
    
    /**
     * Small location for package pickup and drop-off only.
     */
    PICKUP_POINT("Pickup Point"),
    
    /**
     * Self-service automated location with lockers for package pickup.
     */
    LOCKER_STATION("Locker Station"),
    
    /**
     * Third-party vendor location offering courier services.
     */
    PARTNER_LOCATION("Partner Location"),
    
    /**
     * Temporary or mobile location for package handling.
     */
    MOBILE_POINT("Mobile Point"),
    
    /**
     * Specialized location for handling international shipments and customs.
     */
    INTERNATIONAL_GATEWAY("International Gateway");
    
    private final String displayName;
    
    /**
     * Constructor for LocationType enum.
     * 
     * @param displayName The human-readable name for the location type
     */
    LocationType(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * Gets the display name of the location type.
     * 
     * @return The human-readable name for UI display
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Checks if this location type supports walk-in customers.
     * 
     * @return true if walk-in customers are supported, false otherwise
     */
    public boolean supportsWalkIn() {
        return this == BRANCH_OFFICE || 
               this == PICKUP_POINT || 
               this == PARTNER_LOCATION;
    }
    
    /**
     * Checks if this location type supports full services including shipping and receiving.
     * 
     * @return true if full services are supported, false otherwise
     */
    public boolean supportsFullServices() {
        return this == BRANCH_OFFICE || 
               this == DISTRIBUTION_HUB || 
               this == INTERNATIONAL_GATEWAY;
    }
    
    /**
     * Checks if this location type supports staff operations.
     * 
     * @return true if the location is staffed, false for automated locations
     */
    public boolean isStaffed() {
        return this != LOCKER_STATION;
    }
}
