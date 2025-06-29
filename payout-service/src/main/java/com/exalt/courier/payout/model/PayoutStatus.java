package com.exalt.courierservices.payout.$1;

/**
 * Enum representing the possible statuses of a payout.
 */
public enum PayoutStatus {
    /**
     * Payout is pending processing
     */
    PENDING,
    
    /**
     * Payout is being processed
     */
    PROCESSING,
    
    /**
     * Payout has been successfully completed
     */
    COMPLETED,
    
    /**
     * Payout has failed
     */
    FAILED,
    
    /**
     * Payout has been cancelled
     */
    CANCELLED,
    
    /**
     * Payout is on hold (e.g., pending review)
     */
    ON_HOLD,
    
    /**
     * Payout has been rejected (e.g., failed validation)
     */
    REJECTED
}
