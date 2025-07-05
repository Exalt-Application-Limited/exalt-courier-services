package com.gogidix.courierservices.management.$1;

/**
 * Represents the possible statuses of an assignment throughout its lifecycle.
 */
public enum AssignmentStatus {
    /**
     * Assignment has been created but not yet assigned to a courier.
     */
    CREATED,
    
    /**
     * Assignment has been assigned to a courier but not yet accepted.
     */
    ASSIGNED,
    
    /**
     * Assignment has been accepted by the courier.
     */
    ACCEPTED,
    
    /**
     * Assignment is pending and ready to be picked up by the courier.
     */
    PENDING,
    
    /**
     * Assignment is currently being worked on by the courier.
     */
    IN_PROGRESS,
    
    /**
     * Assignment has been delayed due to an issue.
     */
    DELAYED,
    
    /**
     * Assignment has been completed successfully.
     */
    COMPLETED,
    
    /**
     * Assignment has been cancelled.
     */
    CANCELLED,
    
    /**
     * Assignment has been rejected by the courier.
     */
    REJECTED,
    
    /**
     * Assignment has been failed.
     */
    FAILED;
    
    /**
     * Checks if the status is a terminal status (i.e., no further state changes expected).
     * 
     * @return true if the status is terminal, false otherwise
     */
    public boolean isTerminal() {
        return this == COMPLETED || this == CANCELLED || this == REJECTED || this == FAILED;
    }
    
    /**
     * Checks if the status is an active status (i.e., the assignment is currently being worked on).
     * 
     * @return true if the status is active, false otherwise
     */
    public boolean isActive() {
        return this == PENDING || this == IN_PROGRESS || this == DELAYED;
    }
} 