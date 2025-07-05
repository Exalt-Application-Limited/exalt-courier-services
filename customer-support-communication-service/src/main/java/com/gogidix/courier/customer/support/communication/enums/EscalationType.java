package com.gogidix.courier.customer.support.communication.enums;

/**
 * Enumeration representing types of ticket escalations in customer support.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
public enum EscalationType {
    LOW("Low Priority Escalation", "Standard escalation for non-urgent issues", 1, 48, false),
    STANDARD("Standard Escalation", "Normal escalation following standard procedures", 2, 24, false),
    HIGH("High Priority Escalation", "High priority escalation requiring urgent attention", 3, 8, true),
    URGENT("Urgent Escalation", "Urgent escalation requiring immediate attention", 4, 2, true),
    CRITICAL("Critical Escalation", "Critical escalation for system-wide issues", 5, 1, true),
    SLA_BREACH("SLA Breach Escalation", "Automatic escalation due to SLA breach", 4, 1, true),
    CUSTOMER_REQUEST("Customer Requested", "Escalation specifically requested by customer", 3, 4, true),
    QUALITY_ISSUE("Quality Issue", "Escalation due to service quality concerns", 3, 8, true),
    TECHNICAL_COMPLEXITY("Technical Complexity", "Escalation due to technical complexity", 2, 12, false),
    MANAGEMENT_REVIEW("Management Review", "Escalation requiring management attention", 4, 2, true),
    INTERNAL("Internal Escalation", "Internal team escalation (not customer-facing)", 2, 12, false),
    COMPLIANCE("Compliance Issue", "Escalation due to compliance or legal concerns", 5, 1, true),
    SECURITY("Security Issue", "Escalation due to security concerns", 5, 0.5, true),
    ESCALATION_TIMEOUT("Escalation Timeout", "Automatic escalation due to timeout", 3, 4, true);

    private final String displayName;
    private final String description;
    private final int priority; // 1-5, 5 being highest
    private final double maxResponseTimeHours;
    private final boolean requiresImmedateAttention;

    EscalationType(String displayName, String description, int priority, 
                   double maxResponseTimeHours, boolean requiresImmedateAttention) {
        this.displayName = displayName;
        this.description = description;
        this.priority = priority;
        this.maxResponseTimeHours = maxResponseTimeHours;
        this.requiresImmedateAttention = requiresImmedateAttention;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public int getPriority() {
        return priority;
    }

    public double getMaxResponseTimeHours() {
        return maxResponseTimeHours;
    }

    public boolean isRequiresImmedateAttention() {
        return requiresImmedateAttention;
    }

    /**
     * Check if this escalation type requires manager approval.
     */
    public boolean requiresManagerApproval() {
        return this == CRITICAL || this == SECURITY || this == COMPLIANCE ||
               this == MANAGEMENT_REVIEW || priority >= 4;
    }

    /**
     * Check if this escalation type should notify senior management.
     */
    public boolean shouldNotifyManagement() {
        return this == CRITICAL || this == SECURITY || this == COMPLIANCE ||
               this == MANAGEMENT_REVIEW;
    }

    /**
     * Check if this escalation type is system-generated.
     */
    public boolean isSystemGenerated() {
        return this == SLA_BREACH || this == ESCALATION_TIMEOUT;
    }

    /**
     * Check if this escalation type requires 24/7 coverage.
     */
    public boolean requires24x7Coverage() {
        return this == CRITICAL || this == SECURITY || this == URGENT ||
               maxResponseTimeHours <= 2;
    }

    /**
     * Get the target escalation team based on type.
     */
    public String getTargetTeam() {
        return switch (this) {
            case CRITICAL, URGENT -> "senior-support-team";
            case SECURITY -> "security-team";
            case COMPLIANCE -> "compliance-team";
            case TECHNICAL_COMPLEXITY -> "technical-specialist-team";
            case QUALITY_ISSUE -> "quality-assurance-team";
            case MANAGEMENT_REVIEW -> "management-team";
            case SLA_BREACH, ESCALATION_TIMEOUT -> "escalation-management-team";
            default -> "senior-support-team";
        };
    }

    /**
     * Get escalation level (1-4, 4 being highest).
     */
    public int getEscalationLevel() {
        return switch (this) {
            case LOW, STANDARD -> 1;
            case HIGH, QUALITY_ISSUE, TECHNICAL_COMPLEXITY, INTERNAL -> 2;
            case URGENT, CUSTOMER_REQUEST, ESCALATION_TIMEOUT -> 3;
            case CRITICAL, SLA_BREACH, MANAGEMENT_REVIEW, COMPLIANCE, SECURITY -> 4;
        };
    }

    /**
     * Get notification channels for this escalation type.
     */
    public String[] getNotificationChannels() {
        return switch (this) {
            case CRITICAL, SECURITY -> new String[]{"EMAIL", "SMS", "PHONE", "SLACK"};
            case URGENT, SLA_BREACH -> new String[]{"EMAIL", "SMS", "SLACK"};
            case HIGH, CUSTOMER_REQUEST, QUALITY_ISSUE -> new String[]{"EMAIL", "SLACK"};
            case MANAGEMENT_REVIEW, COMPLIANCE -> new String[]{"EMAIL", "PHONE"};
            default -> new String[]{"EMAIL"};
        };
    }

    /**
     * Get maximum escalation queue time in minutes.
     */
    public int getMaxQueueTimeMinutes() {
        return switch (this) {
            case CRITICAL, SECURITY -> 5;
            case URGENT, SLA_BREACH -> 15;
            case HIGH, CUSTOMER_REQUEST -> 30;
            case MANAGEMENT_REVIEW, COMPLIANCE -> 60;
            default -> 120;
        };
    }

    /**
     * Check if escalation requires documentation.
     */
    public boolean requiresDocumentation() {
        return this == CRITICAL || this == SECURITY || this == COMPLIANCE ||
               this == MANAGEMENT_REVIEW || this == QUALITY_ISSUE;
    }

    /**
     * Get the follow-up interval in hours.
     */
    public int getFollowUpIntervalHours() {
        return switch (this) {
            case CRITICAL, SECURITY -> 1;
            case URGENT, SLA_BREACH -> 2;
            case HIGH, CUSTOMER_REQUEST -> 4;
            case MANAGEMENT_REVIEW, COMPLIANCE -> 8;
            default -> 24;
        };
    }

    /**
     * Determine escalation type based on ticket conditions.
     */
    public static EscalationType determineEscalationType(TicketPriority priority, 
                                                        boolean isSLABreached, 
                                                        boolean isCustomerRequested,
                                                        boolean hasSecurityConcerns,
                                                        boolean hasComplianceIssues) {
        if (hasSecurityConcerns) {
            return SECURITY;
        }
        
        if (hasComplianceIssues) {
            return COMPLIANCE;
        }
        
        if (isSLABreached) {
            return SLA_BREACH;
        }
        
        if (isCustomerRequested) {
            return CUSTOMER_REQUEST;
        }
        
        return switch (priority) {
            case CRITICAL -> CRITICAL;
            case HIGH -> HIGH;
            case NORMAL -> STANDARD;
            case LOW -> LOW;
        };
    }

    /**
     * Get auto-escalation threshold in hours.
     */
    public double getAutoEscalationThresholdHours() {
        return switch (this) {
            case CRITICAL, SECURITY -> 0.5;
            case URGENT, SLA_BREACH -> 1;
            case HIGH, CUSTOMER_REQUEST -> 4;
            case MANAGEMENT_REVIEW, COMPLIANCE -> 8;
            default -> 24;
        };
    }
}