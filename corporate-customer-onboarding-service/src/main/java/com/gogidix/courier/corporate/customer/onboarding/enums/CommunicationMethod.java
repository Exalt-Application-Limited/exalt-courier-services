package com.gogidix.courier.corporate.customer.onboarding.enums;

/**
 * Enumeration of preferred communication methods for corporate customers.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
public enum CommunicationMethod {
    EMAIL("Email"),
    PHONE("Phone"),
    SMS("SMS/Text"),
    PORTAL("Customer Portal"),
    API("API Notifications"),
    WEBHOOK("Webhook"),
    SLACK("Slack Integration"),
    TEAMS("Microsoft Teams");

    private final String displayName;

    CommunicationMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Check if this communication method supports real-time notifications.
     */
    public boolean supportsRealTime() {
        return this == SMS || this == API || this == WEBHOOK || this == SLACK || this == TEAMS;
    }

    /**
     * Check if this communication method requires technical integration.
     */
    public boolean requiresTechnicalIntegration() {
        return this == API || this == WEBHOOK || this == SLACK || this == TEAMS;
    }

    /**
     * Check if this communication method is available 24/7.
     */
    public boolean isAvailable24x7() {
        return this == EMAIL || this == SMS || this == PORTAL || this == API || this == WEBHOOK;
    }

    /**
     * Get the typical response time for this communication method.
     */
    public String getTypicalResponseTime() {
        return switch (this) {
            case EMAIL -> "1-4 hours";
            case PHONE -> "Immediate during business hours";
            case SMS -> "5-15 minutes";
            case PORTAL -> "Real-time updates";
            case API, WEBHOOK -> "Immediate";
            case SLACK, TEAMS -> "5-30 minutes";
        };
    }

    /**
     * Check if this method supports file attachments.
     */
    public boolean supportsFileAttachments() {
        return this == EMAIL || this == PORTAL || this == SLACK || this == TEAMS;
    }
}