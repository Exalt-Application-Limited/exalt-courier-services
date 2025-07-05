package com.gogidix.courier.customer.support.communication.enums;

/**
 * Enumeration representing ticket priority levels for customer support.
 * 
 * Priority determines response time SLAs and agent assignment rules.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
public enum TicketPriority {
    LOW("Low Priority", 72, "Non-urgent general inquiries", "#28a745"),
    NORMAL("Normal Priority", 24, "Standard support requests", "#007bff"),
    HIGH("High Priority", 4, "Urgent issues affecting service", "#ffc107"),
    CRITICAL("Critical Priority", 1, "Service outages or critical failures", "#dc3545");

    private final String displayName;
    private final int responseTimeHours;
    private final String description;
    private final String colorCode;

    TicketPriority(String displayName, int responseTimeHours, String description, String colorCode) {
        this.displayName = displayName;
        this.responseTimeHours = responseTimeHours;
        this.description = description;
        this.colorCode = colorCode;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getResponseTimeHours() {
        return responseTimeHours;
    }

    public String getDescription() {
        return description;
    }

    public String getColorCode() {
        return colorCode;
    }

    /**
     * Get priority level as numeric value (1 = highest, 4 = lowest).
     */
    public int getLevel() {
        return switch (this) {
            case CRITICAL -> 1;
            case HIGH -> 2;
            case NORMAL -> 3;
            case LOW -> 4;
        };
    }

    /**
     * Check if this priority requires immediate escalation.
     */
    public boolean requiresImmediateEscalation() {
        return this == CRITICAL;
    }

    /**
     * Check if this priority requires senior agent assignment.
     */
    public boolean requiresSeniorAgent() {
        return this == CRITICAL || this == HIGH;
    }

    /**
     * Get escalation threshold hours - tickets should be escalated if not resolved within this time.
     */
    public int getEscalationThresholdHours() {
        return switch (this) {
            case CRITICAL -> 2;
            case HIGH -> 8;
            case NORMAL -> 48;
            case LOW -> 168; // 1 week
        };
    }

    /**
     * Determine priority based on keywords in ticket content.
     */
    public static TicketPriority determinePriorityFromContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            return NORMAL;
        }
        
        String lowerContent = content.toLowerCase();
        
        // Critical keywords
        if (lowerContent.contains("outage") || lowerContent.contains("down") || 
            lowerContent.contains("critical") || lowerContent.contains("emergency") ||
            lowerContent.contains("urgent") || lowerContent.contains("immediately")) {
            return CRITICAL;
        }
        
        // High priority keywords
        if (lowerContent.contains("error") || lowerContent.contains("failed") ||
            lowerContent.contains("problem") || lowerContent.contains("issue") ||
            lowerContent.contains("broken") || lowerContent.contains("not working")) {
            return HIGH;
        }
        
        // Low priority keywords
        if (lowerContent.contains("question") || lowerContent.contains("inquiry") ||
            lowerContent.contains("information") || lowerContent.contains("help") ||
            lowerContent.contains("how to") || lowerContent.contains("tutorial")) {
            return LOW;
        }
        
        return NORMAL;
    }
}