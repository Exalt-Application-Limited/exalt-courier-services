package com.gogidix.courier.customer.onboarding.service;

import com.gogidix.courier.customer.onboarding.dto.*;
import com.gogidix.courier.customer.onboarding.model.CustomerOnboardingApplication;
import com.gogidix.courier.customer.onboarding.model.CustomerOnboardingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for Customer Onboarding operations.
 * 
 * This service orchestrates the customer onboarding process including:
 * - Application creation and management
 * - KYC verification integration
 * - Account creation and activation
 * - Status tracking and notifications
 */
public interface CustomerOnboardingService {

    /**
     * Creates a new customer onboarding application
     */
    CustomerOnboardingApplicationResponse createApplication(CreateCustomerOnboardingApplicationRequest request);

    /**
     * Retrieves an application by its reference ID
     */
    CustomerOnboardingApplicationResponse getApplicationByReferenceId(String referenceId);

    /**
     * Updates an existing application
     */
    CustomerOnboardingApplicationResponse updateApplication(String referenceId, UpdateCustomerOnboardingApplicationRequest request);

    /**
     * Submits an application for review
     */
    void submitApplication(String referenceId);

    /**
     * Initiates KYC verification for an application
     */
    KycInitiationResponse initiateKycVerification(String referenceId);

    /**
     * Gets the current KYC status for an application
     */
    KycStatusResponse getKycStatus(String referenceId);

    /**
     * Approves a customer onboarding application
     */
    void approveApplication(String referenceId, ApplicationDecisionRequest request);

    /**
     * Rejects a customer onboarding application
     */
    void rejectApplication(String referenceId, ApplicationDecisionRequest request);

    /**
     * Retrieves the status history for an application
     */
    List<ApplicationStatusHistoryResponse> getApplicationStatusHistory(String referenceId);

    /**
     * Retrieves all applications with pagination and optional filtering
     */
    Page<CustomerOnboardingApplicationResponse> getAllApplications(Pageable pageable, String status);
    
    /**
     * Checks if an email is available for registration
     */
    boolean isEmailAvailable(String email);
    
    /**
     * Verifies customer email using verification token
     */
    void verifyEmail(String token);

    /**
     * Activates a customer account after approval
     */
    void activateCustomerAccount(String referenceId);

    /**
     * Suspends a customer account
     */
    void suspendCustomerAccount(String referenceId, CustomerAccountActionRequest request);

    /**
     * Updates application status with history tracking
     */
    void updateApplicationStatus(CustomerOnboardingApplication application, 
                                CustomerOnboardingStatus newStatus, 
                                String reason, 
                                String changedBy);

    /**
     * Sends notification to customer about status changes
     */
    void sendStatusNotification(CustomerOnboardingApplication application, CustomerOnboardingStatus newStatus);

    /**
     * Validates if application can proceed to the next status
     */
    boolean validateStatusTransition(CustomerOnboardingStatus currentStatus, CustomerOnboardingStatus newStatus);

    /**
     * Creates customer profile in auth service
     */
    String createCustomerAuthProfile(CustomerOnboardingApplication application);

    /**
     * Creates billing profile for the customer
     */
    String createCustomerBillingProfile(CustomerOnboardingApplication application);
}