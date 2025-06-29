package com.microecosystem.courier.driver.app.model.assignment;

/**
 * Enumeration of possible statuses for a task within an assignment.
 */
public enum TaskStatus {
    PENDING,
    IN_PROGRESS,
    ARRIVED,
    COMPLETED,
    FAILED,
    SKIPPED,
    CANCELLED,
    RESCHEDULED
}
