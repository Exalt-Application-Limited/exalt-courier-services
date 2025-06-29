package com.exalt.courier.location.model;

/**
 * Represents the status of a payment transaction in the system.
 * Tracks the payment lifecycle from initiation to completion or failure.
 */
public enum PaymentStatus {
    /**
     * Payment has been initiated but not processed yet.
     */
    PENDING("Pending"),
    
    /**
     * Payment is currently being processed.
     */
    PROCESSING("Processing"),
    
    /**
     * Payment has been successfully completed.
     */
    COMPLETED("Completed"),
    
    /**
     * Payment has failed due to an error.
     */
    FAILED("Failed"),
    
    /**
     * Payment has been canceled before processing.
     */
    CANCELED("Canceled"),
    
    /**
     * Payment is on hold pending review.
     */
    ON_HOLD("On Hold"),
    
    /**
     * Payment has been partially or fully refunded.
     */
    REFUNDED("Refunded"),
    
    /**
     * Payment has been declined by the payment processor.
     */
    DECLINED("Declined"),
    
    /**
     * Payment has been disputed by the customer.
     */
    DISPUTED("Disputed"),
    
    /**
     * Payment is awaiting confirmation or verification.
     */
    AWAITING_CONFIRMATION("Awaiting Confirmation");
    
    private final String displayName;
    
    /**
     * Constructor for PaymentStatus enum.
     * 
     * @param displayName The human-readable name for the status
     */
    PaymentStatus(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * Gets the display name of the payment status.
     * 
     * @return The human-readable name for UI display
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Checks if this status indicates that the payment is still in process.
     * 
     * @return true if the payment is in process, false otherwise
     */
    public boolean isInProcess() {
        return this == PENDING || this == PROCESSING || this == AWAITING_CONFIRMATION;
    }
    
    /**
     * Checks if this status indicates that the payment has concluded successfully.
     * 
     * @return true if the payment is successful, false otherwise
     */
    public boolean isSuccessful() {
        return this == COMPLETED;
    }
    
    /**
     * Checks if this status indicates a terminal state.
     * 
     * @return true if the payment is in a terminal state, false otherwise
     */
    public boolean isTerminal() {
        return this == COMPLETED || this == FAILED || this == CANCELED || 
               this == REFUNDED || this == DECLINED;
    }
    
    /**
     * Checks if this status indicates an issue that needs attention.
     * 
     * @return true if the payment has an issue, false otherwise
     */
    public boolean hasIssue() {
        return this == FAILED || this == ON_HOLD || this == DECLINED || 
               this == DISPUTED;
    }
}
