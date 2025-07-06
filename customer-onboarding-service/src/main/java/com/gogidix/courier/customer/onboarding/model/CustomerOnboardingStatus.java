package com.gogidix.courier.customer.onboarding.model;

/**
 * Enumeration representing the various statuses of a customer onboarding application.
 */
public enum CustomerOnboardingStatus {
    DRAFT("Application started but not submitted"),
    SUBMITTED("Application submitted and under review"),
    DOCUMENTS_REQUIRED("Additional documents required"),
    DOCUMENTS_UPLOADED("Documents uploaded, pending verification"),
    KYC_IN_PROGRESS("KYC verification in progress"),
    KYC_APPROVED("KYC verification approved"),
    KYC_FAILED("KYC verification failed"),
    UNDER_REVIEW("Application under manual review"),
    APPROVED("Application approved, customer account activated"),
    REJECTED("Application rejected"),
    SUSPENDED("Customer account suspended"),
    REACTIVATED("Customer account reactivated");

    private final String description;

    CustomerOnboardingStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}