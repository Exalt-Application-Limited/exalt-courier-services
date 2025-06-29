package com.exalt.courier.location.model;

/**
 * Represents the current status of a shipment in the courier system.
 * These statuses track the shipment's progress from creation to delivery.
 */
public enum ShipmentStatus {
    /**
     * Shipment has been created in the system but not yet processed.
     */
    CREATED("Created"),
    
    /**
     * Shipment has been processed and accepted at a courier location.
     */
    ACCEPTED("Accepted"),
    
    /**
     * Shipment is in the process of being sorted.
     */
    SORTING("Sorting"),
    
    /**
     * Shipment is in transit between locations.
     */
    IN_TRANSIT("In Transit"),
    
    /**
     * Shipment has arrived at a distribution hub or local office.
     */
    ARRIVED_AT_FACILITY("Arrived at Facility"),
    
    /**
     * Shipment is out for delivery to the recipient.
     */
    OUT_FOR_DELIVERY("Out for Delivery"),
    
    /**
     * Delivery attempt was made but was unsuccessful.
     */
    DELIVERY_ATTEMPTED("Delivery Attempted"),
    
    /**
     * Shipment is available for pickup at a courier location.
     */
    READY_FOR_PICKUP("Ready for Pickup"),
    
    /**
     * Shipment has been successfully delivered to the recipient.
     */
    DELIVERED("Delivered"),
    
    /**
     * Shipment is being returned to the sender.
     */
    RETURNING("Returning"),
    
    /**
     * Shipment has been successfully returned to the sender.
     */
    RETURNED("Returned"),
    
    /**
     * There is an issue with the shipment that needs to be resolved.
     */
    EXCEPTION("Exception"),
    
    /**
     * The shipment has been lost in the system.
     */
    LOST("Lost"),
    
    /**
     * The shipment has been damaged during transit.
     */
    DAMAGED("Damaged"),
    
    /**
     * The shipment has been held by customs.
     */
    HELD_BY_CUSTOMS("Held by Customs"),
    
    /**
     * The shipment has been canceled before processing.
     */
    CANCELED("Canceled");
    
    private final String displayName;
    
    /**
     * Constructor for ShipmentStatus enum.
     * 
     * @param displayName The human-readable name for the status
     */
    ShipmentStatus(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * Gets the display name of the shipment status.
     * 
     * @return The human-readable name for UI display
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Checks if this status indicates that the shipment is in an active transit state.
     * 
     * @return true if the shipment is actively moving, false otherwise
     */
    public boolean isInTransit() {
        return this == IN_TRANSIT || this == OUT_FOR_DELIVERY;
    }
    
    /**
     * Checks if this status indicates that the shipment has reached a final state.
     * 
     * @return true if the shipment is in a terminal status, false otherwise
     */
    public boolean isFinalState() {
        return this == DELIVERED || this == RETURNED || this == LOST || 
               this == DAMAGED || this == CANCELED;
    }
    
    /**
     * Checks if this status indicates an issue that needs attention.
     * 
     * @return true if the shipment has an issue, false otherwise
     */
    public boolean hasIssue() {
        return this == EXCEPTION || this == LOST || this == DAMAGED || 
               this == HELD_BY_CUSTOMS || this == DELIVERY_ATTEMPTED;
    }
}
