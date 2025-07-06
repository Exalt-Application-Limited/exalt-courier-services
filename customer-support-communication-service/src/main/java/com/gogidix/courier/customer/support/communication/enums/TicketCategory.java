package com.gogidix.courier.customer.support.communication.enums;

/**
 * Enumeration representing customer support ticket categories.
 * 
 * Categories help route tickets to appropriate support teams and specialists.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
public enum TicketCategory {
    SHIPMENT_TRACKING("Shipment Tracking", "Issues with tracking packages and deliveries", "logistics-team"),
    DELIVERY_ISSUES("Delivery Issues", "Problems with delivery attempts and completion", "delivery-team"),
    BILLING_INQUIRY("Billing & Payments", "Questions about invoices, payments, and billing", "billing-team"),
    ACCOUNT_MANAGEMENT("Account Management", "Account settings, profile updates, and access issues", "account-team"),
    SERVICE_DISRUPTION("Service Disruption", "Service outages and technical problems", "technical-team"),
    DAMAGE_CLAIMS("Damage Claims", "Reporting damaged or lost packages", "claims-team"),
    REFUND_REQUEST("Refund & Returns", "Refund requests and return processing", "refund-team"),
    FEATURE_REQUEST("Feature Request", "Suggestions for new features or improvements", "product-team"),
    TECHNICAL_SUPPORT("Technical Support", "Website, app, and system technical issues", "technical-team"),
    GENERAL_INQUIRY("General Inquiry", "General questions and information requests", "general-support"),
    COMPLAINT("Complaint", "Service complaints and feedback", "quality-team"),
    COMPLIMENT("Compliment", "Positive feedback and compliments", "quality-team");

    private final String displayName;
    private final String description;
    private final String assignedTeam;

    TicketCategory(String displayName, String description, String assignedTeam) {
        this.displayName = displayName;
        this.description = description;
        this.assignedTeam = assignedTeam;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public String getAssignedTeam() {
        return assignedTeam;
    }

    /**
     * Get default priority for this category.
     */
    public TicketPriority getDefaultPriority() {
        return switch (this) {
            case SERVICE_DISRUPTION -> TicketPriority.CRITICAL;
            case DELIVERY_ISSUES, DAMAGE_CLAIMS -> TicketPriority.HIGH;
            case SHIPMENT_TRACKING, BILLING_INQUIRY, REFUND_REQUEST, TECHNICAL_SUPPORT -> TicketPriority.NORMAL;
            case ACCOUNT_MANAGEMENT, GENERAL_INQUIRY, COMPLAINT -> TicketPriority.NORMAL;
            case FEATURE_REQUEST, COMPLIMENT -> TicketPriority.LOW;
        };
    }

    /**
     * Get expected resolution time in hours for this category.
     */
    public int getExpectedResolutionHours() {
        return switch (this) {
            case SERVICE_DISRUPTION -> 2;
            case DELIVERY_ISSUES, DAMAGE_CLAIMS -> 8;
            case SHIPMENT_TRACKING, TECHNICAL_SUPPORT -> 4;
            case BILLING_INQUIRY, REFUND_REQUEST -> 24;
            case ACCOUNT_MANAGEMENT -> 12;
            case COMPLAINT -> 24;
            case GENERAL_INQUIRY -> 8;
            case FEATURE_REQUEST -> 168; // 1 week
            case COMPLIMENT -> 4; // Quick acknowledgment
        };
    }

    /**
     * Check if this category requires specialist knowledge.
     */
    public boolean requiresSpecialist() {
        return this == DAMAGE_CLAIMS || this == REFUND_REQUEST || 
               this == SERVICE_DISRUPTION || this == FEATURE_REQUEST;
    }

    /**
     * Check if this category can be auto-resolved.
     */
    public boolean canBeAutoResolved() {
        return this == SHIPMENT_TRACKING || this == GENERAL_INQUIRY;
    }

    /**
     * Get escalation team for complex issues in this category.
     */
    public String getEscalationTeam() {
        return switch (this) {
            case SERVICE_DISRUPTION, TECHNICAL_SUPPORT -> "senior-technical-team";
            case DAMAGE_CLAIMS, REFUND_REQUEST -> "senior-claims-team";
            case DELIVERY_ISSUES -> "operations-manager";
            case BILLING_INQUIRY -> "finance-manager";
            case COMPLAINT -> "quality-manager";
            default -> "supervisor";
        };
    }

    /**
     * Determine category based on keywords in ticket content.
     */
    public static TicketCategory determineCategoryFromContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            return GENERAL_INQUIRY;
        }
        
        String lowerContent = content.toLowerCase();
        
        // Service disruption keywords
        if (lowerContent.contains("outage") || lowerContent.contains("down") || 
            lowerContent.contains("not working") || lowerContent.contains("system error")) {
            return SERVICE_DISRUPTION;
        }
        
        // Tracking keywords
        if (lowerContent.contains("track") || lowerContent.contains("tracking") ||
            lowerContent.contains("where is") || lowerContent.contains("status")) {
            return SHIPMENT_TRACKING;
        }
        
        // Delivery keywords
        if (lowerContent.contains("delivery") || lowerContent.contains("delivered") ||
            lowerContent.contains("not received") || lowerContent.contains("delivery attempt")) {
            return DELIVERY_ISSUES;
        }
        
        // Billing keywords
        if (lowerContent.contains("bill") || lowerContent.contains("invoice") ||
            lowerContent.contains("payment") || lowerContent.contains("charge")) {
            return BILLING_INQUIRY;
        }
        
        // Damage keywords
        if (lowerContent.contains("damage") || lowerContent.contains("broken") ||
            lowerContent.contains("lost") || lowerContent.contains("missing")) {
            return DAMAGE_CLAIMS;
        }
        
        // Refund keywords
        if (lowerContent.contains("refund") || lowerContent.contains("return") ||
            lowerContent.contains("money back")) {
            return REFUND_REQUEST;
        }
        
        // Account keywords
        if (lowerContent.contains("account") || lowerContent.contains("login") ||
            lowerContent.contains("password") || lowerContent.contains("profile")) {
            return ACCOUNT_MANAGEMENT;
        }
        
        // Technical keywords
        if (lowerContent.contains("app") || lowerContent.contains("website") ||
            lowerContent.contains("technical") || lowerContent.contains("bug")) {
            return TECHNICAL_SUPPORT;
        }
        
        // Complaint keywords
        if (lowerContent.contains("complaint") || lowerContent.contains("dissatisfied") ||
            lowerContent.contains("poor service") || lowerContent.contains("terrible")) {
            return COMPLAINT;
        }
        
        // Compliment keywords
        if (lowerContent.contains("thank") || lowerContent.contains("excellent") ||
            lowerContent.contains("great") || lowerContent.contains("compliment")) {
            return COMPLIMENT;
        }
        
        return GENERAL_INQUIRY;
    }
}