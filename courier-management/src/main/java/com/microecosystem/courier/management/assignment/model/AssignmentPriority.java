package com.exalt.courier.management.assignment.model;

/**
 * Represents the priority levels for an assignment.
 */
public enum AssignmentPriority {
    /**
     * Low priority assignment, can be delivered with some flexibility.
     */
    LOW(1),
    
    /**
     * Standard priority assignment, should be delivered within normal timeframes.
     */
    NORMAL(2),
    
    /**
     * High priority assignment, should be given preference over normal assignments.
     */
    HIGH(3),
    
    /**
     * Urgent priority assignment, should be delivered as soon as possible.
     */
    URGENT(4),
    
    /**
     * Critical priority assignment, must be delivered immediately.
     */
    CRITICAL(5);
    
    private final int level;
    
    AssignmentPriority(int level) {
        this.level = level;
    }
    
    /**
     * Gets the numeric priority level.
     * 
     * @return the priority level (higher number means higher priority)
     */
    public int getLevel() {
        return level;
    }
    
    /**
     * Checks if this priority is higher than another priority.
     * 
     * @param other the other priority to compare with
     * @return true if this priority is higher than the other, false otherwise
     */
    public boolean isHigherThan(AssignmentPriority other) {
        return this.level > other.level;
    }
    
    /**
     * Checks if this priority is lower than another priority.
     * 
     * @param other the other priority to compare with
     * @return true if this priority is lower than the other, false otherwise
     */
    public boolean isLowerThan(AssignmentPriority other) {
        return this.level < other.level;
    }
} 