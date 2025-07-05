package com.gogidix.courier.customer.support.communication.enums;

import java.util.Set;

/**
 * Enumeration representing the various statuses of a customer support ticket.
 * 
 * This enum defines the complete workflow for customer support tickets with proper
 * state transitions and business logic validation.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
public enum TicketStatus {
    DRAFT("Ticket created but not submitted", StatusCategory.INITIAL),
    OPEN("Ticket submitted and awaiting agent assignment", StatusCategory.ACTIVE),
    ASSIGNED("Ticket assigned to support agent", StatusCategory.ACTIVE),
    IN_PROGRESS("Agent is actively working on the ticket", StatusCategory.ACTIVE),
    PENDING_CUSTOMER("Waiting for customer response", StatusCategory.WAITING),
    PENDING_INTERNAL("Waiting for internal department response", StatusCategory.WAITING),
    ESCALATED("Ticket escalated to senior support or management", StatusCategory.ESCALATED),
    RESOLVED("Issue resolved, awaiting customer confirmation", StatusCategory.RESOLUTION),
    CLOSED("Ticket closed and completed", StatusCategory.FINAL),
    REOPENED("Previously closed ticket reopened", StatusCategory.ACTIVE),
    CANCELLED("Ticket cancelled by customer", StatusCategory.FINAL);

    private final String description;
    private final StatusCategory category;

    TicketStatus(String description, StatusCategory category) {
        this.description = description;
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public StatusCategory getCategory() {
        return category;
    }

    /**
     * Status categories for grouping related statuses.
     */
    public enum StatusCategory {
        INITIAL("Initial"),
        ACTIVE("Active"),
        WAITING("Waiting"),
        ESCALATED("Escalated"),
        RESOLUTION("Resolution"),
        FINAL("Final");

        private final String displayName;

        StatusCategory(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Check if this status represents an active ticket.
     */
    public boolean isActive() {
        return category == StatusCategory.ACTIVE || category == StatusCategory.WAITING || 
               category == StatusCategory.ESCALATED;
    }

    /**
     * Check if this status represents a completed ticket.
     */
    public boolean isCompleted() {
        return category == StatusCategory.FINAL;
    }

    /**
     * Check if this status requires customer action.
     */
    public boolean requiresCustomerAction() {
        return this == PENDING_CUSTOMER || this == RESOLVED;
    }

    /**
     * Check if this status requires agent action.
     */
    public boolean requiresAgentAction() {
        return this == OPEN || this == ASSIGNED || this == IN_PROGRESS || 
               this == ESCALATED || this == REOPENED;
    }

    /**
     * Check if ticket can be escalated from current status.
     */
    public boolean canBeEscalated() {
        return this == ASSIGNED || this == IN_PROGRESS || this == PENDING_INTERNAL;
    }

    /**
     * Check if ticket can be closed from current status.
     */
    public boolean canBeClosed() {
        return this == RESOLVED || this == PENDING_CUSTOMER;
    }

    /**
     * Get valid transition statuses from the current status.
     */
    public Set<TicketStatus> getValidTransitions() {
        return switch (this) {
            case DRAFT -> Set.of(OPEN, CANCELLED);
            case OPEN -> Set.of(ASSIGNED, ESCALATED, CANCELLED);
            case ASSIGNED -> Set.of(IN_PROGRESS, PENDING_INTERNAL, ESCALATED, CANCELLED);
            case IN_PROGRESS -> Set.of(PENDING_CUSTOMER, PENDING_INTERNAL, RESOLVED, ESCALATED);
            case PENDING_CUSTOMER -> Set.of(IN_PROGRESS, RESOLVED, CLOSED, ESCALATED);
            case PENDING_INTERNAL -> Set.of(IN_PROGRESS, ESCALATED);
            case ESCALATED -> Set.of(IN_PROGRESS, RESOLVED, CLOSED);
            case RESOLVED -> Set.of(CLOSED, REOPENED);
            case REOPENED -> Set.of(ASSIGNED, IN_PROGRESS, ESCALATED);
            case CLOSED, CANCELLED -> Set.of(REOPENED); // Allow reopening closed tickets
        };
    }

    /**
     * Check if transition to target status is valid.
     */
    public boolean canTransitionTo(TicketStatus targetStatus) {
        return getValidTransitions().contains(targetStatus);
    }

    /**
     * Get the expected response time for this status.
     */
    public String getExpectedResponseTime() {
        return switch (this) {
            case DRAFT -> "N/A - Customer action required";
            case OPEN -> "2 hours during business hours";
            case ASSIGNED -> "1 hour for initial response";
            case IN_PROGRESS -> "4 hours for status update";
            case PENDING_CUSTOMER -> "48 hours for customer response";
            case PENDING_INTERNAL -> "24 hours for internal response";
            case ESCALATED -> "1 hour for escalation review";
            case RESOLVED -> "24 hours for customer confirmation";
            case REOPENED -> "1 hour for agent assignment";
            default -> "N/A";
        };
    }

    /**
     * Get priority level for this status.
     */
    public int getPriorityLevel() {
        return switch (this) {
            case ESCALATED -> 1; // Highest priority
            case REOPENED -> 2;
            case OPEN -> 3;
            case IN_PROGRESS -> 4;
            case ASSIGNED -> 5;
            case PENDING_INTERNAL -> 6;
            case PENDING_CUSTOMER -> 7;
            case RESOLVED -> 8;
            default -> 9; // Lowest priority
        };
    }
}