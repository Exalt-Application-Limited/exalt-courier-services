package com.gogidix.courier.management.courier.model;

/**
 * Represents the possible statuses of a courier.
 */
public enum CourierStatus {
    /**
     * Courier is offline and not working.
     */
    OFFLINE,
    
    /**
     * Courier is online and available for assignments.
     */
    AVAILABLE,
    
    /**
     * Courier is busy with an assignment.
     */
    BUSY,
    
    /**
     * Courier is on a break.
     */
    ON_BREAK,
    
    /**
     * Courier is on a delivery.
     */
    ON_DELIVERY,
    
    /**
     * Courier is temporarily unavailable.
     */
    UNAVAILABLE;
    
    /**
     * Checks if the courier is active and can receive new assignments.
     * 
     * @return true if the courier can accept new assignments, false otherwise
     */
    public boolean canAcceptAssignments() {
        return this == AVAILABLE;
    }
    
    /**
     * Checks if the courier is online and working.
     * 
     * @return true if the courier is online, false otherwise
     */
    public boolean isOnline() {
        return this != OFFLINE;
    }
} 