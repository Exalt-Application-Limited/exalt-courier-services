package com.gogidix.courierservices.management.$1;

/**
 * Represents the possible statuses of a task throughout its lifecycle.
 */
public enum TaskStatus {
    /**
     * Task is pending and waiting to be started.
     */
    PENDING,
    
    /**
     * Task is currently being worked on.
     */
    IN_PROGRESS,
    
    /**
     * Task has been completed successfully.
     */
    COMPLETED,
    
    /**
     * Task has been failed.
     */
    FAILED,
    
    /**
     * Task has been cancelled.
     */
    CANCELLED,
    
    /**
     * Task has been skipped.
     */
    SKIPPED,
    
    /**
     * Task is blocked by another task or constraint.
     */
    BLOCKED,
    
    /**
     * Task has been delayed but can still be resumed.
     */
    DELAYED;
    
    /**
     * Checks if the status is a terminal status (i.e., no further state changes expected).
     * 
     * @return true if the status is terminal, false otherwise
     */
    public boolean isTerminal() {
        return this == COMPLETED || this == FAILED || this == CANCELLED || this == SKIPPED;
    }
    
    /**
     * Checks if the status is an active status (i.e., the task is currently being worked on).
     * 
     * @return true if the status is active, false otherwise
     */
    public boolean isActive() {
        return this == PENDING || this == IN_PROGRESS || this == DELAYED;
    }
} 