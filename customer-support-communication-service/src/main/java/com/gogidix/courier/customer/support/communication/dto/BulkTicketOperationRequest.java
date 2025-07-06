package com.gogidix.courier.customer.support.communication.dto;

import com.gogidix.courier.customer.support.communication.enums.TicketPriority;
import com.gogidix.courier.customer.support.communication.enums.TicketStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Request DTO for performing bulk operations on multiple support tickets.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkTicketOperationRequest {

    @NotNull(message = "Operation type is required")
    private BulkOperationType operationType;

    @NotEmpty(message = "At least one ticket ID is required")
    private List<UUID> ticketIds;

    @Size(max = 50, message = "Performed by agent ID must not exceed 50 characters")
    private String performedByAgentId;

    @Size(max = 100, message = "Performed by agent name must not exceed 100 characters")
    private String performedByAgentName;

    @Size(max = 1000, message = "Operation reason must not exceed 1000 characters")
    private String operationReason;

    // Status update fields
    private TicketStatus newStatus;
    private String statusChangeReason;

    // Priority update fields
    private TicketPriority newPriority;
    private String priorityChangeReason;

    // Assignment fields
    @Size(max = 50, message = "Assigned agent ID must not exceed 50 characters")
    private String assignedAgentId;

    @Size(max = 100, message = "Assigned agent name must not exceed 100 characters")
    private String assignedAgentName;

    @Size(max = 30, message = "Assigned team must not exceed 30 characters")
    private String assignedTeam;

    // Bulk messaging
    @Size(max = 5000, message = "Bulk message content must not exceed 5000 characters")
    private String bulkMessageContent;

    private Boolean sendBulkMessage = false;
    private String messageChannel = "EMAIL";

    // Tag management
    private List<String> tagsToAdd;
    private List<String> tagsToRemove;
    private Boolean replaceAllTags = false;

    // Category updates
    private String newCategory;
    private String categoryChangeReason;

    // Escalation
    private Boolean escalateTickets = false;
    private String escalationType;
    private String escalationReason;

    // Closure operations
    private String closureReason;
    private String resolutionNotes;
    private Boolean requestCustomerFeedback = false;

    // Notification preferences
    private Boolean notifyCustomers = true;
    private Boolean notifyAgents = true;
    private Boolean sendSummaryReport = true;

    // Validation options
    private Boolean validateBeforeOperation = true;
    private Boolean skipInvalidTickets = false;
    private Boolean dryRun = false; // Test operation without making changes

    // Batch processing options
    private Integer batchSize = 50; // Process tickets in batches
    private Integer delayBetweenBatchesMs = 1000;

    // Custom field updates
    private Map<String, Object> customFieldUpdates;

    // Operation metadata
    @Size(max = 500, message = "Operation notes must not exceed 500 characters")
    private String operationNotes;

    private Boolean requireManagerApproval = false;
    private String approvalRequestMessage;

    /**
     * Enumeration for bulk operation types.
     */
    public enum BulkOperationType {
        UPDATE_STATUS("Update Status", "Change status for multiple tickets"),
        UPDATE_PRIORITY("Update Priority", "Change priority for multiple tickets"),
        ASSIGN_TICKETS("Assign Tickets", "Assign multiple tickets to an agent/team"),
        UNASSIGN_TICKETS("Unassign Tickets", "Remove assignment from multiple tickets"),
        ADD_TAGS("Add Tags", "Add tags to multiple tickets"),
        REMOVE_TAGS("Remove Tags", "Remove tags from multiple tickets"),
        REPLACE_TAGS("Replace Tags", "Replace all tags on multiple tickets"),
        SEND_MESSAGE("Send Message", "Send message to multiple tickets"),
        ESCALATE_TICKETS("Escalate Tickets", "Escalate multiple tickets"),
        CLOSE_TICKETS("Close Tickets", "Close multiple tickets"),
        REOPEN_TICKETS("Reopen Tickets", "Reopen multiple closed tickets"),
        UPDATE_CATEGORY("Update Category", "Change category for multiple tickets"),
        MERGE_TICKETS("Merge Tickets", "Merge multiple tickets into one"),
        SPLIT_TICKET("Split Ticket", "Split one ticket into multiple tickets"),
        APPLY_SLA_ADJUSTMENT("Apply SLA Adjustment", "Adjust SLA settings for multiple tickets"),
        BULK_EXPORT("Bulk Export", "Export data for multiple tickets"),
        BULK_ARCHIVE("Bulk Archive", "Archive multiple tickets"),
        CUSTOM_OPERATION("Custom Operation", "Custom bulk operation");

        private final String displayName;
        private final String description;

        BulkOperationType(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Validation result for bulk operations.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BulkOperationValidation {
        private List<UUID> validTicketIds;
        private List<UUID> invalidTicketIds;
        private List<String> validationErrors;
        private Boolean canProceed;
        private String overallValidationMessage;
    }

    /**
     * Check if this operation affects ticket status.
     */
    public boolean affectsStatus() {
        return operationType == BulkOperationType.UPDATE_STATUS ||
               operationType == BulkOperationType.CLOSE_TICKETS ||
               operationType == BulkOperationType.REOPEN_TICKETS;
    }

    /**
     * Check if this operation affects ticket assignment.
     */
    public boolean affectsAssignment() {
        return operationType == BulkOperationType.ASSIGN_TICKETS ||
               operationType == BulkOperationType.UNASSIGN_TICKETS;
    }

    /**
     * Check if this operation sends communications.
     */
    public boolean sendsCommunications() {
        return operationType == BulkOperationType.SEND_MESSAGE ||
               Boolean.TRUE.equals(sendBulkMessage);
    }

    /**
     * Check if this operation requires manager approval.
     */
    public boolean requiresApproval() {
        return Boolean.TRUE.equals(requireManagerApproval) ||
               operationType == BulkOperationType.CLOSE_TICKETS ||
               operationType == BulkOperationType.ESCALATE_TICKETS ||
               operationType == BulkOperationType.MERGE_TICKETS ||
               (ticketIds != null && ticketIds.size() > 100); // Large batch operations
    }

    /**
     * Get estimated processing time in seconds based on operation type and ticket count.
     */
    public int getEstimatedProcessingTimeSeconds() {
        if (ticketIds == null || ticketIds.isEmpty()) {
            return 0;
        }

        int baseTimePerTicket = switch (operationType) {
            case UPDATE_STATUS, UPDATE_PRIORITY, ADD_TAGS, REMOVE_TAGS -> 1;
            case ASSIGN_TICKETS, UNASSIGN_TICKETS, UPDATE_CATEGORY -> 2;
            case SEND_MESSAGE, APPLY_SLA_ADJUSTMENT -> 3;
            case ESCALATE_TICKETS, CLOSE_TICKETS, REOPEN_TICKETS -> 5;
            case MERGE_TICKETS, SPLIT_TICKET -> 10;
            case BULK_EXPORT, BULK_ARCHIVE -> 2;
            case CUSTOM_OPERATION -> 5;
            default -> 3;
        };

        return ticketIds.size() * baseTimePerTicket + (ticketIds.size() / batchSize) * (delayBetweenBatchesMs / 1000);
    }

    /**
     * Validate bulk operation request.
     */
    public BulkOperationValidation validate() {
        BulkOperationValidation.BulkOperationValidationBuilder validation = BulkOperationValidation.builder();
        
        if (ticketIds == null || ticketIds.isEmpty()) {
            validation.canProceed(false)
                     .overallValidationMessage("No ticket IDs provided");
            return validation.build();
        }

        if (ticketIds.size() > 1000) {
            validation.canProceed(false)
                     .overallValidationMessage("Cannot process more than 1000 tickets in a single operation");
            return validation.build();
        }

        // Validate operation-specific requirements
        boolean valid = switch (operationType) {
            case UPDATE_STATUS -> newStatus != null;
            case UPDATE_PRIORITY -> newPriority != null;
            case ASSIGN_TICKETS -> assignedAgentId != null || assignedTeam != null;
            case SEND_MESSAGE -> bulkMessageContent != null && !bulkMessageContent.trim().isEmpty();
            case ESCALATE_TICKETS -> escalationType != null && escalationReason != null;
            case CLOSE_TICKETS -> closureReason != null;
            case UPDATE_CATEGORY -> newCategory != null;
            default -> true;
        };

        validation.canProceed(valid);
        
        if (!valid) {
            validation.overallValidationMessage("Missing required fields for operation type: " + operationType.getDisplayName());
        } else {
            validation.overallValidationMessage("Validation passed");
        }

        return validation.build();
    }

    /**
     * Get operation summary for logging and reporting.
     */
    public String getOperationSummary() {
        return String.format("Bulk %s operation on %d tickets by %s", 
                           operationType.getDisplayName(),
                           ticketIds != null ? ticketIds.size() : 0,
                           performedByAgentName != null ? performedByAgentName : performedByAgentId);
    }
}