package com.gogidix.courier.management.assignment.model;

/**
 * Represents the different types of tasks that can be part of an assignment.
 */
public enum TaskType {
    /**
     * A task to pick up a package.
     */
    PICKUP,
    
    /**
     * A task to deliver a package.
     */
    DELIVERY,
    
    /**
     * A task to return a package.
     */
    RETURN,
    
    /**
     * A task to inspect a package.
     */
    INSPECTION,
    
    /**
     * A task to sign for a package.
     */
    SIGNATURE,
    
    /**
     * A task to make a payment.
     */
    PAYMENT,
    
    /**
     * A task to verify something.
     */
    VERIFICATION,
    
    /**
     * A task to take a photo.
     */
    PHOTO,
    
    /**
     * A task to make a customer support call.
     */
    SUPPORT_CALL,
    
    /**
     * A custom task type.
     */
    CUSTOM;
    
    /**
     * Checks if the task type is a package handling task.
     * 
     * @return true if the task type is a package handling task, false otherwise
     */
    public boolean isPackageHandlingTask() {
        return this == PICKUP || this == DELIVERY || this == RETURN;
    }
    
    /**
     * Checks if the task type is a verification task.
     * 
     * @return true if the task type is a verification task, false otherwise
     */
    public boolean isVerificationTask() {
        return this == INSPECTION || this == SIGNATURE || this == VERIFICATION || this == PHOTO;
    }
} 