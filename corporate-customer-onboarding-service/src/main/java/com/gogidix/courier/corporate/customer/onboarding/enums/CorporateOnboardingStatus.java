package com.gogidix.courier.corporate.customer.onboarding.enums;

import java.util.List;
import java.util.Set;

/**
 * Enumeration representing the various statuses of a corporate customer onboarding application.
 * 
 * This enum defines the complete workflow for corporate customer onboarding with proper
 * state transitions and business logic validation.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
public enum CorporateOnboardingStatus {
    DRAFT("Application started but not submitted", StatusCategory.INITIAL),
    SUBMITTED("Application submitted and under review", StatusCategory.REVIEW),
    DOCUMENTS_REQUIRED("Additional documents required", StatusCategory.DOCUMENT),
    DOCUMENTS_UPLOADED("Documents uploaded, pending verification", StatusCategory.DOCUMENT),
    KYB_IN_PROGRESS("KYB verification in progress", StatusCategory.VERIFICATION),
    KYB_APPROVED("KYB verification approved", StatusCategory.VERIFICATION),
    KYB_FAILED("KYB verification failed", StatusCategory.VERIFICATION),
    CREDIT_CHECK_IN_PROGRESS("Credit assessment in progress", StatusCategory.VERIFICATION),
    CREDIT_CHECK_APPROVED("Credit assessment approved", StatusCategory.VERIFICATION),
    CREDIT_CHECK_FAILED("Credit assessment failed", StatusCategory.VERIFICATION),
    COMMERCIAL_REVIEW("Commercial terms under review", StatusCategory.REVIEW),
    LEGAL_REVIEW("Legal documentation under review", StatusCategory.REVIEW),
    CONTRACT_PREPARATION("Service contract being prepared", StatusCategory.CONTRACT),
    CONTRACT_SENT("Service contract sent for signature", StatusCategory.CONTRACT),
    CONTRACT_SIGNED("Service contract signed", StatusCategory.CONTRACT),
    UNDER_REVIEW("Application under manual review", StatusCategory.REVIEW),
    APPROVED("Application approved, corporate account activated", StatusCategory.FINAL),
    REJECTED("Application rejected", StatusCategory.FINAL),
    SUSPENDED("Corporate account suspended", StatusCategory.FINAL),
    REACTIVATED("Corporate account reactivated", StatusCategory.FINAL),
    ON_HOLD("Application on hold pending additional information", StatusCategory.REVIEW),
    CANCELLED("Application cancelled by customer", StatusCategory.FINAL);

    private final String description;
    private final StatusCategory category;

    CorporateOnboardingStatus(String description, StatusCategory category) {
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
        DOCUMENT("Documentation"),
        VERIFICATION("Verification"),
        REVIEW("Review"),
        CONTRACT("Contract"),
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
     * Check if this status represents a completed application.
     */
    public boolean isCompleted() {
        return this == APPROVED || this == REJECTED || this == CANCELLED;
    }

    /**
     * Check if this status represents an active application.
     */
    public boolean isActive() {
        return !isCompleted() && this != SUSPENDED;
    }

    /**
     * Check if this status requires customer action.
     */
    public boolean requiresCustomerAction() {
        return this == DOCUMENTS_REQUIRED || this == CONTRACT_SENT || this == ON_HOLD;
    }

    /**
     * Check if this status requires admin action.
     */
    public boolean requiresAdminAction() {
        return this == SUBMITTED || this == DOCUMENTS_UPLOADED || this == UNDER_REVIEW ||
               this == COMMERCIAL_REVIEW || this == LEGAL_REVIEW;
    }

    /**
     * Check if this status allows document upload.
     */
    public boolean allowsDocumentUpload() {
        return this == DRAFT || this == DOCUMENTS_REQUIRED || this == DOCUMENTS_UPLOADED ||
               this == KYB_IN_PROGRESS || this == ON_HOLD;
    }

    /**
     * Get valid transition statuses from the current status.
     */
    public Set<CorporateOnboardingStatus> getValidTransitions() {
        return switch (this) {
            case DRAFT -> Set.of(SUBMITTED, CANCELLED);
            case SUBMITTED -> Set.of(DOCUMENTS_REQUIRED, KYB_IN_PROGRESS, UNDER_REVIEW, ON_HOLD, REJECTED);
            case DOCUMENTS_REQUIRED -> Set.of(DOCUMENTS_UPLOADED, CANCELLED);
            case DOCUMENTS_UPLOADED -> Set.of(KYB_IN_PROGRESS, DOCUMENTS_REQUIRED, REJECTED);
            case KYB_IN_PROGRESS -> Set.of(KYB_APPROVED, KYB_FAILED, DOCUMENTS_REQUIRED);
            case KYB_APPROVED -> Set.of(CREDIT_CHECK_IN_PROGRESS, COMMERCIAL_REVIEW, CONTRACT_PREPARATION);
            case KYB_FAILED -> Set.of(DOCUMENTS_REQUIRED, REJECTED);
            case CREDIT_CHECK_IN_PROGRESS -> Set.of(CREDIT_CHECK_APPROVED, CREDIT_CHECK_FAILED);
            case CREDIT_CHECK_APPROVED -> Set.of(COMMERCIAL_REVIEW, CONTRACT_PREPARATION);
            case CREDIT_CHECK_FAILED -> Set.of(COMMERCIAL_REVIEW, REJECTED);
            case COMMERCIAL_REVIEW -> Set.of(LEGAL_REVIEW, CONTRACT_PREPARATION, ON_HOLD, REJECTED);
            case LEGAL_REVIEW -> Set.of(CONTRACT_PREPARATION, COMMERCIAL_REVIEW, ON_HOLD, REJECTED);
            case CONTRACT_PREPARATION -> Set.of(CONTRACT_SENT, LEGAL_REVIEW);
            case CONTRACT_SENT -> Set.of(CONTRACT_SIGNED, CONTRACT_PREPARATION, ON_HOLD);
            case CONTRACT_SIGNED -> Set.of(APPROVED);
            case UNDER_REVIEW -> Set.of(APPROVED, REJECTED, DOCUMENTS_REQUIRED, ON_HOLD);
            case ON_HOLD -> Set.of(SUBMITTED, DOCUMENTS_REQUIRED, UNDER_REVIEW, CANCELLED);
            case APPROVED -> Set.of(SUSPENDED);
            case SUSPENDED -> Set.of(REACTIVATED, REJECTED);
            case REACTIVATED -> Set.of(APPROVED, SUSPENDED);
            case REJECTED, CANCELLED -> Set.of(); // Terminal states
        };
    }

    /**
     * Check if transition to target status is valid.
     */
    public boolean canTransitionTo(CorporateOnboardingStatus targetStatus) {
        return getValidTransitions().contains(targetStatus);
    }

    /**
     * Get the expected processing time for this status.
     */
    public String getExpectedProcessingTime() {
        return switch (this) {
            case DRAFT -> "N/A - Customer action required";
            case SUBMITTED -> "1-2 business days";
            case DOCUMENTS_REQUIRED, DOCUMENTS_UPLOADED -> "2-3 business days";
            case KYB_IN_PROGRESS -> "3-5 business days";
            case CREDIT_CHECK_IN_PROGRESS -> "1-3 business days";
            case COMMERCIAL_REVIEW -> "2-5 business days";
            case LEGAL_REVIEW -> "3-7 business days";
            case CONTRACT_PREPARATION -> "1-2 business days";
            case CONTRACT_SENT -> "N/A - Customer action required";
            case UNDER_REVIEW -> "3-7 business days";
            case ON_HOLD -> "N/A - Pending additional information";
            default -> "N/A";
        };
    }

    /**
     * Get statuses that are considered "in progress".
     */
    public static List<CorporateOnboardingStatus> getInProgressStatuses() {
        return List.of(SUBMITTED, DOCUMENTS_REQUIRED, DOCUMENTS_UPLOADED, KYB_IN_PROGRESS,
                      CREDIT_CHECK_IN_PROGRESS, COMMERCIAL_REVIEW, LEGAL_REVIEW,
                      CONTRACT_PREPARATION, CONTRACT_SENT, UNDER_REVIEW, ON_HOLD);
    }

    /**
     * Get statuses that are considered "pending customer action".
     */
    public static List<CorporateOnboardingStatus> getPendingCustomerActionStatuses() {
        return List.of(DOCUMENTS_REQUIRED, CONTRACT_SENT, ON_HOLD);
    }

    /**
     * Get statuses that are considered "pending admin action".
     */
    public static List<CorporateOnboardingStatus> getPendingAdminActionStatuses() {
        return List.of(SUBMITTED, DOCUMENTS_UPLOADED, UNDER_REVIEW, COMMERCIAL_REVIEW, LEGAL_REVIEW);
    }
}