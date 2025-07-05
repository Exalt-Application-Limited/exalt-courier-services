package com.gogidix.courier.corporate.customer.onboarding.service;

import com.gogidix.courier.corporate.customer.onboarding.dto.*;
import com.gogidix.courier.corporate.customer.onboarding.model.CorporateOnboardingApplication;
import com.gogidix.courier.corporate.customer.onboarding.model.CorporateOnboardingStatus;

import java.util.List;

/**
 * Service interface for Corporate Customer Onboarding operations.
 * 
 * This service handles the specialized onboarding process for corporate customers including:
 * - Multi-step application process with business documentation
 * - Corporate KYC verification with business compliance
 * - Volume-based pricing and contract negotiation
 * - Multi-user account setup with role-based access
 * - Corporate billing and invoicing setup
 * - Integration with corporate communication systems
 */
public interface CorporateOnboardingService {

    /**
     * Creates a new corporate customer onboarding application
     */
    CorporateOnboardingApplicationResponse createApplication(CreateCorporateOnboardingApplicationRequest request);

    /**
     * Retrieves a corporate application by its reference ID
     */
    CorporateOnboardingApplicationResponse getApplicationByReferenceId(String referenceId);

    /**
     * Updates an existing corporate application
     */
    CorporateOnboardingApplicationResponse updateApplication(String referenceId, UpdateCorporateOnboardingApplicationRequest request);

    /**
     * Submits a corporate application for review
     */
    void submitApplication(String referenceId);

    /**
     * Initiates corporate KYB verification including business compliance checks
     */
    KybInitiationResponse initiateKybVerification(String referenceId);

    /**
     * Gets the current corporate KYB status
     */
    KybStatusResponse getKybStatus(String referenceId);

    /**
     * Initiates credit assessment for corporate customers
     */
    CreditAssessmentResponse initiateCreditAssessment(String referenceId, CreditAssessmentRequest request);

    /**
     * Sets commercial terms for corporate customer
     */
    void setCommercialTerms(String referenceId, CommercialTermsRequest request);

    /**
     * Generates service contract for corporate customer
     */
    ContractGenerationResponse generateServiceContract(String referenceId);

    /**
     * Records contract signature
     */
    void recordContractSignature(String referenceId, ContractSignatureRequest request);

    /**
     * Generates pricing proposal for corporate customer
     */
    PricingProposalResponse generatePricingProposal(String referenceId, PricingProposalRequest request);

    /**
     * Uploads and validates business documents
     */
    DocumentUploadResponse uploadBusinessDocument(String referenceId, BusinessDocumentUploadRequest request);

    /**
     * Validates business registration and legal compliance
     */
    BusinessValidationResponse validateBusinessRegistration(String referenceId, BusinessValidationRequest request);

    /**
     * Initiates contract negotiation and volume pricing setup
     */
    ContractNegotiationResponse initiateContractNegotiation(String referenceId, ContractNegotiationRequest request);

    /**
     * Approves a corporate onboarding application with contract terms
     */
    void approveApplication(String referenceId, ApplicationDecisionRequest request);

    /**
     * Rejects a corporate onboarding application
     */
    void rejectApplication(String referenceId, ApplicationDecisionRequest request);

    /**
     * Sets up multi-user corporate account with role assignments
     */
    CorporateAccountSetupResponse setupCorporateAccount(String referenceId, CorporateAccountSetupRequest request);

    /**
     * Configures corporate billing and invoicing preferences
     */
    CorporateBillingSetupResponse setupCorporateBilling(String referenceId, CorporateBillingSetupRequest request);

    /**
     * Activates corporate account after all approvals
     */
    void activateCorporateAccount(String referenceId);

    /**
     * Suspends corporate account and all associated users
     */
    void suspendCorporateAccount(String referenceId, CorporateAccountActionRequest request);

    /**
     * Retrieves the status history for a corporate application
     */
    List<CorporateApplicationStatusHistoryResponse> getApplicationStatusHistory(String referenceId);

    /**
     * Retrieves all corporate applications with filtering options
     */
    List<CorporateOnboardingApplicationResponse> getAllApplications(int page, int size, String status, String businessType);

    /**
     * Updates corporate application status with history tracking
     */
    void updateApplicationStatus(CorporateOnboardingApplication application, 
                                CorporateOnboardingStatus newStatus, 
                                String reason, 
                                String changedBy);

    /**
     * Sends notification to corporate contacts about status changes
     */
    void sendStatusNotification(CorporateOnboardingApplication application, CorporateOnboardingStatus newStatus);

    /**
     * Validates if corporate application can proceed to the next status
     */
    boolean validateStatusTransition(CorporateOnboardingStatus currentStatus, CorporateOnboardingStatus newStatus);

    /**
     * Creates corporate profile in auth service with multiple users
     */
    String createCorporateAuthProfile(CorporateOnboardingApplication application, List<CorporateUserRequest> users);

    /**
     * Creates corporate billing profile with volume pricing
     */
    String createCorporateBillingProfile(CorporateOnboardingApplication application, CorporateBillingConfiguration config);

    /**
     * Generates corporate service agreement and contracts
     */
    ContractGenerationResponse generateServiceAgreement(String referenceId, ContractGenerationRequest request);

    /**
     * Handles corporate account renewal and contract updates
     */
    CorporateRenewalResponse handleAccountRenewal(String referenceId, CorporateRenewalRequest request);

    /**
     * Manages corporate user access and permissions
     */
    CorporateUserManagementResponse manageCorporateUsers(String referenceId, CorporateUserManagementRequest request);

    /**
     * Generates corporate onboarding reports and analytics
     */
    CorporateOnboardingReportResponse generateOnboardingReport(String referenceId, CorporateReportRequest request);
}