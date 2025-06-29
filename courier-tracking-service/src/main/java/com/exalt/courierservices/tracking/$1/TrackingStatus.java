package com.exalt.courierservices.tracking.$1;

/**
 * Enum representing the possible statuses of a package in the tracking system.
 */
public enum TrackingStatus {
    /**
     * Package has been created in the system but not yet processed.
     */
    CREATED("Created"),
    
    /**
     * Package information has been received but the physical package hasn't been received.
     */
    INFORMATION_RECEIVED("Information Received"),
    
    /**
     * Package has been received at the origin facility.
     */
    RECEIVED("Received"),
    
    /**
     * Package is being processed at a facility.
     */
    PROCESSING("Processing"),
    
    /**
     * Package is in transit between facilities.
     */
    IN_TRANSIT("In Transit"),
    
    /**
     * Package has arrived at a facility.
     */
    ARRIVED_AT_FACILITY("Arrived at Facility"),
    
    /**
     * Package has departed from a facility.
     */
    DEPARTED_FROM_FACILITY("Departed from Facility"),
    
    /**
     * Package is out for delivery.
     */
    OUT_FOR_DELIVERY("Out for Delivery"),
    
    /**
     * A delivery attempt was made but was unsuccessful.
     */
    DELIVERY_ATTEMPTED("Delivery Attempted"),
    
    /**
     * Package has been delivered successfully.
     */
    DELIVERED("Delivered"),
    
    /**
     * Package has been returned to sender.
     */
    RETURNED_TO_SENDER("Returned to Sender"),
    
    /**
     * Package has been delayed.
     */
    DELAYED("Delayed"),
    
    /**
     * Package has been held at a facility at customer's request.
     */
    HELD("Held"),
    
    /**
     * Package has been rescheduled for delivery.
     */
    RESCHEDULED("Rescheduled"),
    
    /**
     * Package is waiting for customer action.
     */
    WAITING_FOR_CUSTOMER("Waiting for Customer"),
    
    /**
     * Package has been lost.
     */
    LOST("Lost"),
    
    /**
     * Package has been damaged.
     */
    DAMAGED("Damaged"),
    
    /**
     * Package delivery has been cancelled.
     */
    CANCELLED("Cancelled"),
    
    /**
     * Package is available for pickup at a facility.
     */
    AVAILABLE_FOR_PICKUP("Available for Pickup"),
    
    /**
     * Package has been picked up by the customer.
     */
    PICKED_UP("Picked Up"),
    
    /**
     * Package has cleared customs.
     */
    CUSTOMS_CLEARED("Customs Cleared"),
    
    /**
     * Package is held at customs.
     */
    CUSTOMS_HOLD("Customs Hold");
    
    private final String displayName;
    
    TrackingStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Determines if this status represents a final state.
     */
    public boolean isFinalState() {
        return this == DELIVERED || this == RETURNED_TO_SENDER || 
               this == LOST || this == DAMAGED || this == CANCELLED || 
               this == PICKED_UP;
    }
    
    /**
     * Determines if this status represents an active delivery state.
     */
    public boolean isActiveDelivery() {
        return this == IN_TRANSIT || this == OUT_FOR_DELIVERY || 
               this == ARRIVED_AT_FACILITY || this == DEPARTED_FROM_FACILITY;
    }
    
    /**
     * Determines if this status represents a problem state.
     */
    public boolean isProblemState() {
        return this == DELAYED || this == LOST || this == DAMAGED || 
               this == CUSTOMS_HOLD || this == DELIVERY_ATTEMPTED;
    }
} 