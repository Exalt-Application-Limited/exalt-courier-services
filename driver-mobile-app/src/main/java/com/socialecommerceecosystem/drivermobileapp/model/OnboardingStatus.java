package com.gogidix.courier.drivermobileapp.model;

/**
 * Represents the various stages of courier onboarding process.
 */
public enum OnboardingStatus {
    REGISTERED,
    PROFILE_COMPLETED,
    DOCUMENTS_SUBMITTED,
    BACKGROUND_CHECK_PENDING,
    BACKGROUND_CHECK_APPROVED,
    TRAINING_PENDING,
    TRAINING_COMPLETED,
    VEHICLE_INSPECTION_PENDING,
    VEHICLE_INSPECTION_PASSED,
    ACTIVATED,
    REJECTED
} 