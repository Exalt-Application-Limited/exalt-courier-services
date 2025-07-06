package com.gogidix.courier.corporate.customer.onboarding.model;

/**
 * Enumeration representing the various statuses of a corporate customer onboarding application.
 */
public enum CorporateOnboardingStatus {
    DRAFT("Application started but not submitted"),
    SUBMITTED("Application submitted and under review"),
    DOCUMENTS_REQUIRED("Additional documents required"),
    DOCUMENTS_UPLOADED("Documents uploaded, pending verification"),
    KYB_IN_PROGRESS("KYB verification in progress"),
    KYB_APPROVED("KYB verification approved"),
    KYB_FAILED("KYB verification failed"),
    CREDIT_CHECK_IN_PROGRESS("Credit assessment in progress"),
    CREDIT_CHECK_APPROVED("Credit assessment approved"),
    CREDIT_CHECK_FAILED("Credit assessment failed"),
    COMMERCIAL_REVIEW("Commercial terms under review"),
    LEGAL_REVIEW("Legal documentation under review"),
    CONTRACT_PREPARATION("Service contract being prepared"),
    CONTRACT_SENT("Service contract sent for signature"),
    CONTRACT_SIGNED("Service contract signed"),
    UNDER_REVIEW("Application under manual review"),
    APPROVED("Application approved, corporate account activated"),
    REJECTED("Application rejected"),
    SUSPENDED("Corporate account suspended"),
    REACTIVATED("Corporate account reactivated"),
    ON_HOLD("Application on hold pending additional information");

    private final String description;

    CorporateOnboardingStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}