package com.gogidix.courier.customer.support.communication.enums;

/**
 * Enumeration representing types of messages in customer support communications.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
public enum MessageType {
    CUSTOMER_INQUIRY("Customer Inquiry", "Initial message from customer", true, false),
    AGENT_RESPONSE("Agent Response", "Response from support agent", true, true),
    INTERNAL_NOTE("Internal Note", "Internal note between agents", false, false),
    SYSTEM_UPDATE("System Update", "Automated system message", true, false),
    ESCALATION_NOTE("Escalation Note", "Message related to ticket escalation", false, true),
    RESOLUTION_UPDATE("Resolution Update", "Update about ticket resolution", true, true),
    SLA_WARNING("SLA Warning", "SLA breach warning message", false, false),
    AUTO_REPLY("Auto Reply", "Automated acknowledgment", true, false),
    FOLLOW_UP("Follow Up", "Follow-up message from agent", true, true),
    CLOSURE_NOTIFICATION("Closure Notification", "Ticket closure notification", true, false),
    FEEDBACK_REQUEST("Feedback Request", "Request for customer feedback", true, false),
    CALLBACK_SCHEDULED("Callback Scheduled", "Callback appointment confirmation", true, false),
    PRIORITY_CHANGE("Priority Change", "Priority level change notification", false, true),
    ASSIGNMENT_CHANGE("Assignment Change", "Agent assignment change", false, true),
    STATUS_UPDATE("Status Update", "Ticket status change notification", true, true),
    ATTACHMENT_ADDED("Attachment Added", "New attachment notification", true, true),
    TEMPLATE_MESSAGE("Template Message", "Template-based response", true, true),
    SURVEY_INVITATION("Survey Invitation", "Customer satisfaction survey", true, false),
    REMINDER_MESSAGE("Reminder Message", "Reminder about pending actions", true, true),
    QUALITY_REVIEW("Quality Review", "Quality assurance message", false, false);

    private final String displayName;
    private final String description;
    private final boolean customerVisible;
    private final boolean requiresResponse;

    MessageType(String displayName, String description, boolean customerVisible, boolean requiresResponse) {
        this.displayName = displayName;
        this.description = description;
        this.customerVisible = customerVisible;
        this.requiresResponse = requiresResponse;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCustomerVisible() {
        return customerVisible;
    }

    public boolean isRequiresResponse() {
        return requiresResponse;
    }

    /**
     * Check if this message type is system-generated.
     */
    public boolean isSystemGenerated() {
        return this == SYSTEM_UPDATE || this == SLA_WARNING || this == AUTO_REPLY ||
               this == CLOSURE_NOTIFICATION || this == FEEDBACK_REQUEST || 
               this == CALLBACK_SCHEDULED || this == SURVEY_INVITATION ||
               this == REMINDER_MESSAGE;
    }

    /**
     * Check if this message type is internal only.
     */
    public boolean isInternalOnly() {
        return !customerVisible;
    }

    /**
     * Check if this message type indicates ticket activity.
     */
    public boolean isTicketActivity() {
        return this == PRIORITY_CHANGE || this == ASSIGNMENT_CHANGE ||
               this == STATUS_UPDATE || this == ESCALATION_NOTE ||
               this == RESOLUTION_UPDATE;
    }

    /**
     * Get the expected response time for this message type in hours.
     */
    public int getExpectedResponseTimeHours() {
        return switch (this) {
            case CUSTOMER_INQUIRY -> 2; // 2 hours for customer inquiries
            case AGENT_RESPONSE -> 0; // No response expected to agent responses
            case ESCALATION_NOTE -> 1; // 1 hour for escalation responses
            case FOLLOW_UP -> 4; // 4 hours for follow-up responses
            case FEEDBACK_REQUEST -> 24; // 24 hours for feedback
            case SURVEY_INVITATION -> 72; // 3 days for survey completion
            case REMINDER_MESSAGE -> 8; // 8 hours for reminder follow-up
            default -> 0; // No response expected
        };
    }

    /**
     * Get notification priority for this message type.
     */
    public int getNotificationPriority() {
        return switch (this) {
            case SLA_WARNING, ESCALATION_NOTE -> 5; // Highest priority
            case CUSTOMER_INQUIRY, PRIORITY_CHANGE -> 4; // High priority
            case AGENT_RESPONSE, FOLLOW_UP, STATUS_UPDATE -> 3; // Medium priority
            case SYSTEM_UPDATE, RESOLUTION_UPDATE -> 2; // Low priority
            case INTERNAL_NOTE, QUALITY_REVIEW -> 1; // Lowest priority
            default -> 2; // Default medium-low priority
        };
    }

    /**
     * Check if this message type should trigger email notification.
     */
    public boolean shouldTriggerEmailNotification() {
        return this == CUSTOMER_INQUIRY || this == AGENT_RESPONSE ||
               this == ESCALATION_NOTE || this == RESOLUTION_UPDATE ||
               this == CLOSURE_NOTIFICATION || this == FEEDBACK_REQUEST ||
               this == SURVEY_INVITATION;
    }

    /**
     * Check if this message type should trigger SMS notification.
     */
    public boolean shouldTriggerSmsNotification() {
        return this == SLA_WARNING || this == ESCALATION_NOTE ||
               this == CALLBACK_SCHEDULED;
    }

    /**
     * Get appropriate channels for this message type.
     */
    public String[] getSupportedChannels() {
        return switch (this) {
            case CUSTOMER_INQUIRY -> new String[]{"EMAIL", "CHAT", "PORTAL", "PHONE"};
            case AGENT_RESPONSE, FOLLOW_UP -> new String[]{"EMAIL", "CHAT", "PORTAL"};
            case INTERNAL_NOTE, ESCALATION_NOTE, QUALITY_REVIEW -> new String[]{"INTERNAL"};
            case SYSTEM_UPDATE, AUTO_REPLY -> new String[]{"EMAIL", "PORTAL", "SMS"};
            case CALLBACK_SCHEDULED -> new String[]{"EMAIL", "SMS", "PHONE"};
            case SURVEY_INVITATION -> new String[]{"EMAIL", "SMS"};
            default -> new String[]{"EMAIL", "PORTAL"};
        };
    }

    /**
     * Determine message type based on content and context.
     */
    public static MessageType determineTypeFromContext(String content, String senderRole, boolean isAutomated) {
        if (isAutomated) {
            if (content != null && content.toLowerCase().contains("sla")) {
                return SLA_WARNING;
            }
            if (content != null && content.toLowerCase().contains("survey")) {
                return SURVEY_INVITATION;
            }
            return SYSTEM_UPDATE;
        }

        if ("CUSTOMER".equals(senderRole)) {
            return CUSTOMER_INQUIRY;
        }

        if ("AGENT".equals(senderRole)) {
            if (content != null && content.toLowerCase().contains("internal")) {
                return INTERNAL_NOTE;
            }
            return AGENT_RESPONSE;
        }

        return SYSTEM_UPDATE;
    }
}