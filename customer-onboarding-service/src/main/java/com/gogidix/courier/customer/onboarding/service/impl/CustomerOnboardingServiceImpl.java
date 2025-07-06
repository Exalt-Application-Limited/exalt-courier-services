package com.gogidix.courier.customer.onboarding.service.impl;

import com.gogidix.courier.customer.onboarding.client.AuthServiceClient;
import com.gogidix.courier.customer.onboarding.client.KycServiceClient;
import com.gogidix.courier.customer.onboarding.dto.*;
import com.gogidix.courier.customer.onboarding.model.*;
import com.gogidix.courier.customer.onboarding.repository.CustomerOnboardingApplicationRepository;
import com.gogidix.courier.customer.onboarding.repository.CustomerApplicationStatusHistoryRepository;
import com.gogidix.courier.customer.onboarding.repository.CustomerProfileRepository;
import com.gogidix.courier.customer.onboarding.service.CustomerOnboardingService;
import com.gogidix.ecosystem.shared.exceptions.ResourceNotFoundException;
import com.gogidix.ecosystem.shared.exceptions.ValidationException;
import com.gogidix.ecosystem.shared.exceptions.BusinessException;
import com.gogidix.ecosystem.shared.model.user.User;
import com.gogidix.ecosystem.shared.model.user.UserRole;
import com.gogidix.ecosystem.shared.model.user.UserStatus;
import com.gogidix.ecosystem.shared.utilities.IdGenerator;
import com.gogidix.ecosystem.shared.utilities.EmailValidator;
import com.gogidix.ecosystem.shared.utilities.PhoneValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of Customer Onboarding Service.
 * 
 * This service orchestrates the complete customer onboarding workflow
 * leveraging shared infrastructure services for authentication, KYC, and notifications.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CustomerOnboardingServiceImpl implements CustomerOnboardingService {

    private final CustomerOnboardingApplicationRepository applicationRepository;
    private final CustomerApplicationStatusHistoryRepository statusHistoryRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final AuthServiceClient authServiceClient;
    private final KycServiceClient kycServiceClient;
    
    // Configuration constants
    private static final String APPLICATION_PREFIX = "CUST-ONB";
    private static final String CUSTOMER_PREFIX = "CUST";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String DEFAULT_CUSTOMER_TIER = "BRONZE";
    private static final String DEFAULT_CUSTOMER_SEGMENT = "INDIVIDUAL";
    
    // Validation messages
    private static final String EMAIL_ALREADY_EXISTS = "Customer with email %s already exists";
    private static final String PHONE_ALREADY_EXISTS = "Customer with phone %s already exists";
    private static final String APPLICATION_NOT_FOUND = "Application with reference ID %s not found";
    private static final String INVALID_STATUS_TRANSITION = "Cannot transition from %s to %s";
    
    // Business rules
    private static final Map<CustomerOnboardingStatus, List<CustomerOnboardingStatus>> VALID_TRANSITIONS = Map.of(
        CustomerOnboardingStatus.DRAFT, List.of(CustomerOnboardingStatus.SUBMITTED, CustomerOnboardingStatus.CANCELLED),
        CustomerOnboardingStatus.SUBMITTED, List.of(CustomerOnboardingStatus.KYC_IN_PROGRESS, CustomerOnboardingStatus.APPROVED, CustomerOnboardingStatus.REJECTED),
        CustomerOnboardingStatus.KYC_IN_PROGRESS, List.of(CustomerOnboardingStatus.KYC_COMPLETED, CustomerOnboardingStatus.KYC_FAILED, CustomerOnboardingStatus.REJECTED),
        CustomerOnboardingStatus.KYC_COMPLETED, List.of(CustomerOnboardingStatus.APPROVED, CustomerOnboardingStatus.REJECTED),
        CustomerOnboardingStatus.KYC_FAILED, List.of(CustomerOnboardingStatus.KYC_IN_PROGRESS, CustomerOnboardingStatus.REJECTED),
        CustomerOnboardingStatus.APPROVED, List.of(CustomerOnboardingStatus.ACTIVATED, CustomerOnboardingStatus.SUSPENDED),
        CustomerOnboardingStatus.REJECTED, List.of(CustomerOnboardingStatus.DRAFT),
        CustomerOnboardingStatus.ACTIVATED, List.of(CustomerOnboardingStatus.SUSPENDED, CustomerOnboardingStatus.DEACTIVATED),
        CustomerOnboardingStatus.SUSPENDED, List.of(CustomerOnboardingStatus.ACTIVATED, CustomerOnboardingStatus.DEACTIVATED),
        CustomerOnboardingStatus.DEACTIVATED, List.of(CustomerOnboardingStatus.ACTIVATED),
        CustomerOnboardingStatus.CANCELLED, List.of(CustomerOnboardingStatus.DRAFT)
    );

    @Override
    public CustomerOnboardingApplicationResponse createApplication(CreateCustomerOnboardingApplicationRequest request) {
        log.info("Creating customer onboarding application for email: {}", request.customerEmail());
        
        // Validate input using shared utilities
        validateCreateApplicationRequest(request);
        
        // Check for existing applications
        if (applicationRepository.existsByCustomerEmail(request.customerEmail())) {
            throw new BusinessException(String.format(EMAIL_ALREADY_EXISTS, request.customerEmail()));
        }
        
        if (applicationRepository.existsByCustomerPhone(request.customerPhone())) {
            throw new BusinessException(String.format(PHONE_ALREADY_EXISTS, request.customerPhone()));
        }
        
        // Generate unique application reference ID
        String applicationReferenceId = generateApplicationReferenceId();
        
        // Create the application entity
        CustomerOnboardingApplication application = CustomerOnboardingApplication.builder()
                .applicationReferenceId(applicationReferenceId)
                .customerEmail(request.customerEmail())
                .customerPhone(request.customerPhone())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .dateOfBirth(request.dateOfBirth())
                .nationalId(request.nationalId())
                .addressLine1(request.addressLine1())
                .addressLine2(request.addressLine2())
                .city(request.city())
                .stateProvince(request.stateProvince())
                .postalCode(request.postalCode())
                .country(request.country())
                .applicationStatus(CustomerOnboardingStatus.DRAFT)
                .preferredCommunicationMethod(request.preferredCommunicationMethod() != null ? 
                    request.preferredCommunicationMethod() : "EMAIL")
                .marketingConsent(request.marketingConsent() != null ? request.marketingConsent() : false)
                .termsAccepted(request.termsAccepted() != null ? request.termsAccepted() : false)
                .privacyPolicyAccepted(request.privacyPolicyAccepted() != null ? request.privacyPolicyAccepted() : false)
                .build();
        
        // Set audit fields
        String currentUser = getCurrentUsername();
        application.setCreatedBy(currentUser);
        application.setUpdatedBy(currentUser);
        
        // Save application
        application = applicationRepository.save(application);
        
        // Create initial status history
        createStatusHistory(application, null, CustomerOnboardingStatus.DRAFT, 
                           "Application created", currentUser);
        
        log.info("Customer onboarding application created successfully with reference ID: {}", 
                 applicationReferenceId);
        
        return mapToApplicationResponse(application);
    }

    @Override
    public CustomerOnboardingApplicationResponse getApplicationByReferenceId(String referenceId) {
        log.info("Retrieving customer onboarding application: {}", referenceId);
        
        CustomerOnboardingApplication application = applicationRepository
                .findByApplicationReferenceId(referenceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer onboarding application not found: " + referenceId));
        
        return mapToApplicationResponse(application);
    }

    @Override
    public CustomerOnboardingApplicationResponse updateApplication(String referenceId, 
                                                                 UpdateCustomerOnboardingApplicationRequest request) {
        log.info("Updating customer onboarding application: {}", referenceId);
        
        CustomerOnboardingApplication application = applicationRepository
                .findByApplicationReferenceId(referenceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer onboarding application not found: " + referenceId));
        
        // Only allow updates in DRAFT status
        if (application.getApplicationStatus() != CustomerOnboardingStatus.DRAFT) {
            throw new CustomerOnboardingException(
                    "Application can only be updated in DRAFT status. Current status: " + 
                    application.getApplicationStatus());
        }
        
        // Update fields if provided
        if (request.customerEmail() != null) {
            application.setCustomerEmail(request.customerEmail());
        }
        if (request.customerPhone() != null) {
            application.setCustomerPhone(request.customerPhone());
        }
        if (request.firstName() != null) {
            application.setFirstName(request.firstName());
        }
        if (request.lastName() != null) {
            application.setLastName(request.lastName());
        }
        if (request.dateOfBirth() != null) {
            application.setDateOfBirth(request.dateOfBirth());
        }
        if (request.nationalId() != null) {
            application.setNationalId(request.nationalId());
        }
        if (request.addressLine1() != null) {
            application.setAddressLine1(request.addressLine1());
        }
        if (request.addressLine2() != null) {
            application.setAddressLine2(request.addressLine2());
        }
        if (request.city() != null) {
            application.setCity(request.city());
        }
        if (request.stateProvince() != null) {
            application.setStateProvince(request.stateProvince());
        }
        if (request.postalCode() != null) {
            application.setPostalCode(request.postalCode());
        }
        if (request.country() != null) {
            application.setCountry(request.country());
        }
        if (request.preferredCommunicationMethod() != null) {
            application.setPreferredCommunicationMethod(request.preferredCommunicationMethod());
        }
        if (request.marketingConsent() != null) {
            application.setMarketingConsent(request.marketingConsent());
        }
        
        application.setUpdatedBy("CUSTOMER");
        CustomerOnboardingApplication updatedApplication = applicationRepository.save(application);
        
        log.info("Customer onboarding application updated: {}", referenceId);
        
        return mapToApplicationResponse(updatedApplication);
    }

    @Override
    public void submitApplication(String referenceId) {
        log.info("Submitting customer onboarding application: {}", referenceId);
        
        CustomerOnboardingApplication application = applicationRepository
                .findByApplicationReferenceId(referenceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer onboarding application not found: " + referenceId));
        
        // Validate application is in DRAFT status
        if (application.getApplicationStatus() != CustomerOnboardingStatus.DRAFT) {
            throw new CustomerOnboardingException(
                    "Application can only be submitted from DRAFT status. Current status: " + 
                    application.getApplicationStatus());
        }
        
        // Validate required fields
        validateApplicationForSubmission(application);
        
        // Update status to SUBMITTED
        updateApplicationStatus(application, CustomerOnboardingStatus.SUBMITTED, 
                              "Application submitted for review", "CUSTOMER");
        
        application.setSubmittedAt(LocalDateTime.now());
        applicationRepository.save(application);
        
        // Send notification
        sendStatusNotification(application, CustomerOnboardingStatus.SUBMITTED);
        
        log.info("Customer onboarding application submitted: {}", referenceId);
    }

    @Override
    public KycInitiationResponse initiateKycVerification(String referenceId) {
        log.info("Initiating KYC verification for application: {}", referenceId);
        
        CustomerOnboardingApplication application = applicationRepository
                .findByApplicationReferenceId(referenceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer onboarding application not found: " + referenceId));
        
        // Validate application is in correct status
        if (application.getApplicationStatus() != CustomerOnboardingStatus.SUBMITTED &&
            application.getApplicationStatus() != CustomerOnboardingStatus.DOCUMENTS_UPLOADED) {
            throw new CustomerOnboardingException(
                    "KYC can only be initiated after application submission. Current status: " + 
                    application.getApplicationStatus());
        }
        
        // Call KYC service to initiate verification
        KycServiceClient.InitiateKycRequest kycRequest = new KycServiceClient.InitiateKycRequest(
                application.getApplicationReferenceId(),
                application.getFirstName(),
                application.getLastName(),
                application.getDateOfBirth(),
                application.getNationalId(),
                application.getCustomerEmail(),
                application.getCustomerPhone(),
                application.getAddressLine1() + " " + application.getCity(),
                application.getCity(),
                application.getCountry(),
                "INDIVIDUAL"
        );
        
        KycServiceClient.KycVerificationResponse kycResponse = kycServiceClient.initiateKycVerification(kycRequest);
        
        // Update application with KYC verification ID
        application.setKycVerificationId(kycResponse.verificationId());
        updateApplicationStatus(application, CustomerOnboardingStatus.KYC_IN_PROGRESS,
                              "KYC verification initiated", "SYSTEM");
        
        applicationRepository.save(application);
        
        log.info("KYC verification initiated for application: {} with KYC ID: {}", 
                referenceId, kycResponse.verificationId());
        
        return new KycInitiationResponse(
                kycResponse.verificationId(),
                kycResponse.status(),
                kycResponse.customerReferenceId(),
                kycResponse.estimatedCompletionTime(),
                "Please upload your government-issued ID and proof of address documents",
                kycResponse.createdAt()
        );
    }

    @Override
    public KycStatusResponse getKycStatus(String referenceId) {
        log.info("Getting KYC status for application: {}", referenceId);
        
        CustomerOnboardingApplication application = applicationRepository
                .findByApplicationReferenceId(referenceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer onboarding application not found: " + referenceId));
        
        if (application.getKycVerificationId() == null) {
            throw new CustomerOnboardingException("KYC verification has not been initiated for this application");
        }
        
        // Get KYC status from KYC service
        KycServiceClient.KycStatusResponse kycStatus = kycServiceClient.getKycStatus(application.getKycVerificationId());
        
        // Update application status based on KYC status
        if ("APPROVED".equals(kycStatus.status())) {
            updateApplicationStatus(application, CustomerOnboardingStatus.KYC_APPROVED,
                                  "KYC verification completed successfully", "SYSTEM");
        } else if ("REJECTED".equals(kycStatus.status())) {
            updateApplicationStatus(application, CustomerOnboardingStatus.KYC_FAILED,
                                  "KYC verification failed", "SYSTEM");
        }
        
        return new KycStatusResponse(
                kycStatus.verificationId(),
                kycStatus.status(),
                kycStatus.progress(),
                kycStatus.lastUpdated(),
                kycStatus.statusMessage(),
                kycStatus.requiresManualReview(),
                kycStatus.nextAction(),
                kycStatus.lastUpdated() // Using lastUpdated as estimatedCompletionTime
        );
    }

    @Override
    public void approveApplication(String referenceId, ApplicationDecisionRequest request) {
        log.info("Approving customer onboarding application: {}", referenceId);
        
        CustomerOnboardingApplication application = applicationRepository
                .findByApplicationReferenceId(referenceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer onboarding application not found: " + referenceId));
        
        // Validate KYC is approved
        if (application.getApplicationStatus() != CustomerOnboardingStatus.KYC_APPROVED &&
            application.getApplicationStatus() != CustomerOnboardingStatus.UNDER_REVIEW) {
            throw new CustomerOnboardingException(
                    "Application cannot be approved in current status: " + 
                    application.getApplicationStatus());
        }
        
        // Create auth service user account
        String authUserId = createCustomerAuthProfile(application);
        application.setAuthServiceUserId(authUserId);
        
        // Create billing profile
        String billingCustomerId = createCustomerBillingProfile(application);
        application.setBillingCustomerId(billingCustomerId);
        
        // Update application status
        updateApplicationStatus(application, CustomerOnboardingStatus.APPROVED,
                              request.reason(), request.reviewedBy());
        
        application.setApprovedAt(LocalDateTime.now());
        applicationRepository.save(application);
        
        // Send approval notification
        sendStatusNotification(application, CustomerOnboardingStatus.APPROVED);
        
        log.info("Customer onboarding application approved: {}", referenceId);
    }

    @Override
    public void rejectApplication(String referenceId, ApplicationDecisionRequest request) {
        log.info("Rejecting customer onboarding application: {}", referenceId);
        
        CustomerOnboardingApplication application = applicationRepository
                .findByApplicationReferenceId(referenceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer onboarding application not found: " + referenceId));
        
        // Update application status
        updateApplicationStatus(application, CustomerOnboardingStatus.REJECTED,
                              request.reason(), request.reviewedBy());
        
        application.setRejectedAt(LocalDateTime.now());
        application.setRejectionReason(request.reason());
        applicationRepository.save(application);
        
        // Send rejection notification
        sendStatusNotification(application, CustomerOnboardingStatus.REJECTED);
        
        log.info("Customer onboarding application rejected: {}", referenceId);
    }

    @Override
    public List<ApplicationStatusHistoryResponse> getApplicationStatusHistory(String referenceId) {
        log.info("Getting status history for application: {}", referenceId);
        
        CustomerOnboardingApplication application = applicationRepository
                .findByApplicationReferenceId(referenceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer onboarding application not found: " + referenceId));
        
        List<CustomerApplicationStatusHistory> history = statusHistoryRepository
                .findByApplicationOrderByChangedAtDesc(application);
        
        return history.stream()
                .map(this::mapStatusHistoryToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<CustomerOnboardingApplicationResponse> getAllApplications(Pageable pageable, String status) {
        log.info("Getting all customer onboarding applications - page: {}, size: {}, status: {}", 
                 pageable.getPageNumber(), pageable.getPageSize(), status);
        
        Page<CustomerOnboardingApplication> applications;
        if (StringUtils.hasText(status)) {
            try {
                CustomerOnboardingStatus statusEnum = CustomerOnboardingStatus.valueOf(status.toUpperCase());
                applications = applicationRepository.findByApplicationStatus(statusEnum, pageable);
            } catch (IllegalArgumentException e) {
                throw new ValidationException("Invalid status: " + status);
            }
        } else {
            applications = applicationRepository.findAllByOrderByCreatedAtDesc(pageable);
        }
        
        return applications.map(this::mapToApplicationResponse);
    }
    
    @Override
    public boolean isEmailAvailable(String email) {
        log.debug("Checking email availability for: {}", email);
        
        if (!EmailValidator.isValid(email)) {
            throw new ValidationException("Invalid email format: " + email);
        }
        
        return !applicationRepository.existsByCustomerEmail(email);
    }
    
    @Override
    public void verifyEmail(String token) {
        log.info("Verifying email with token: {}", token);
        
        if (!StringUtils.hasText(token)) {
            throw new ValidationException("Verification token is required");
        }
        
        // TODO: Implement email verification logic with token validation
        // This would typically involve:
        // 1. Decode/validate the token
        // 2. Extract email from token
        // 3. Find application by email
        // 4. Mark email as verified
        // 5. Update application status if needed
        
        throw new BusinessException("Email verification not yet implemented");
    }

    @Override
    public void activateCustomerAccount(String referenceId) {
        log.info("Activating customer account for application: {}", referenceId);
        
        CustomerOnboardingApplication application = applicationRepository
                .findByApplicationReferenceId(referenceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer onboarding application not found: " + referenceId));
        
        if (application.getApplicationStatus() != CustomerOnboardingStatus.APPROVED) {
            throw new CustomerOnboardingException(
                    "Customer account can only be activated after approval. Current status: " + 
                    application.getApplicationStatus());
        }
        
        if (application.getAuthServiceUserId() == null) {
            throw new CustomerOnboardingException("Auth service user ID not found for application");
        }
        
        // Activate user in auth service
        authServiceClient.activateUser(application.getAuthServiceUserId());
        
        // Send welcome notification
        sendStatusNotification(application, CustomerOnboardingStatus.APPROVED);
        
        log.info("Customer account activated for application: {}", referenceId);
    }

    @Override
    public void suspendCustomerAccount(String referenceId, CustomerAccountActionRequest request) {
        log.info("Suspending customer account for application: {}", referenceId);
        
        CustomerOnboardingApplication application = applicationRepository
                .findByApplicationReferenceId(referenceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer onboarding application not found: " + referenceId));
        
        if (application.getAuthServiceUserId() == null) {
            throw new CustomerOnboardingException("Auth service user ID not found for application");
        }
        
        // Suspend user in auth service
        authServiceClient.suspendUser(application.getAuthServiceUserId());
        
        // Update application status
        updateApplicationStatus(application, CustomerOnboardingStatus.SUSPENDED,
                              request.reason(), request.actionedBy());
        
        applicationRepository.save(application);
        
        log.info("Customer account suspended for application: {}", referenceId);
    }

    @Override
    public void updateApplicationStatus(CustomerOnboardingApplication application,
                                      CustomerOnboardingStatus newStatus,
                                      String reason,
                                      String changedBy) {
        CustomerOnboardingStatus oldStatus = application.getApplicationStatus();
        
        if (!validateStatusTransition(oldStatus, newStatus)) {
            throw new CustomerOnboardingException(
                    String.format("Invalid status transition from %s to %s", oldStatus, newStatus));
        }
        
        application.setApplicationStatus(newStatus);
        application.setUpdatedBy(changedBy);
        
        createStatusHistory(application, oldStatus, newStatus, reason, changedBy);
        
        log.info("Application {} status updated from {} to {}", 
                application.getApplicationReferenceId(), oldStatus, newStatus);
    }

    @Override
    public void sendStatusNotification(CustomerOnboardingApplication application, 
                                     CustomerOnboardingStatus newStatus) {
        // TODO: Integrate with notification service to send emails/SMS
        log.info("Sending notification for application {} with status {}", 
                application.getApplicationReferenceId(), newStatus);
    }

    @Override
    public boolean validateStatusTransition(CustomerOnboardingStatus currentStatus, 
                                          CustomerOnboardingStatus newStatus) {
        // Define valid status transitions
        switch (currentStatus) {
            case DRAFT:
                return newStatus == CustomerOnboardingStatus.SUBMITTED;
            case SUBMITTED:
                return newStatus == CustomerOnboardingStatus.DOCUMENTS_REQUIRED ||
                       newStatus == CustomerOnboardingStatus.KYC_IN_PROGRESS ||
                       newStatus == CustomerOnboardingStatus.REJECTED;
            case DOCUMENTS_REQUIRED:
                return newStatus == CustomerOnboardingStatus.DOCUMENTS_UPLOADED ||
                       newStatus == CustomerOnboardingStatus.REJECTED;
            case DOCUMENTS_UPLOADED:
                return newStatus == CustomerOnboardingStatus.KYC_IN_PROGRESS ||
                       newStatus == CustomerOnboardingStatus.REJECTED;
            case KYC_IN_PROGRESS:
                return newStatus == CustomerOnboardingStatus.KYC_APPROVED ||
                       newStatus == CustomerOnboardingStatus.KYC_FAILED ||
                       newStatus == CustomerOnboardingStatus.UNDER_REVIEW;
            case KYC_APPROVED:
                return newStatus == CustomerOnboardingStatus.UNDER_REVIEW ||
                       newStatus == CustomerOnboardingStatus.APPROVED;
            case KYC_FAILED:
                return newStatus == CustomerOnboardingStatus.REJECTED ||
                       newStatus == CustomerOnboardingStatus.DOCUMENTS_REQUIRED;
            case UNDER_REVIEW:
                return newStatus == CustomerOnboardingStatus.APPROVED ||
                       newStatus == CustomerOnboardingStatus.REJECTED ||
                       newStatus == CustomerOnboardingStatus.DOCUMENTS_REQUIRED;
            case APPROVED:
                return newStatus == CustomerOnboardingStatus.SUSPENDED;
            case REJECTED:
                return false; // No transitions from REJECTED
            case SUSPENDED:
                return newStatus == CustomerOnboardingStatus.REACTIVATED;
            case REACTIVATED:
                return newStatus == CustomerOnboardingStatus.SUSPENDED;
            default:
                return false;
        }
    }

    @Override
    public String createCustomerAuthProfile(CustomerOnboardingApplication application) {
        log.info("Creating customer auth profile for application: {}", 
                application.getApplicationReferenceId());
        
        // Generate temporary password
        String temporaryPassword = UUID.randomUUID().toString().substring(0, 8);
        
        AuthServiceClient.CreateCustomerUserRequest authRequest = new AuthServiceClient.CreateCustomerUserRequest(
                application.getCustomerEmail(),
                application.getCustomerPhone(),
                application.getFirstName(),
                application.getLastName(),
                temporaryPassword,
                "CUSTOMER",
                application.getApplicationReferenceId()
        );
        
        AuthServiceClient.CustomerUserResponse authResponse = authServiceClient.createCustomerUser(authRequest);
        
        // Send password reset email
        authServiceClient.initiatePasswordReset(
                new AuthServiceClient.PasswordResetRequest(application.getCustomerEmail())
        );
        
        log.info("Customer auth profile created with user ID: {}", authResponse.userId());
        
        return authResponse.userId();
    }

    @Override
    public String createCustomerBillingProfile(CustomerOnboardingApplication application) {
        log.info("Creating customer billing profile for application: {}", 
                application.getApplicationReferenceId());
        
        // TODO: Integrate with billing service to create customer billing profile
        // For now, return a mock billing customer ID
        String billingCustomerId = "BILL-" + UUID.randomUUID().toString().substring(0, 8);
        
        log.info("Customer billing profile created with ID: {}", billingCustomerId);
        
        return billingCustomerId;
    }

    /**
     * Validates application fields before submission.
     */
    private void validateApplicationForSubmission(CustomerOnboardingApplication application) {
        if (!StringUtils.hasText(application.getCustomerEmail())) {
            throw new ValidationException("Customer email is required for submission");
        }
        if (!StringUtils.hasText(application.getCustomerPhone())) {
            throw new ValidationException("Customer phone is required for submission");
        }
        if (!StringUtils.hasText(application.getFirstName())) {
            throw new ValidationException("First name is required for submission");
        }
        if (!StringUtils.hasText(application.getLastName())) {
            throw new ValidationException("Last name is required for submission");
        }
        if (!StringUtils.hasText(application.getCountry())) {
            throw new ValidationException("Country is required for submission");
        }
        if (!Boolean.TRUE.equals(application.getTermsAccepted())) {
            throw new ValidationException("Terms and conditions must be accepted");
        }
        if (!Boolean.TRUE.equals(application.getPrivacyPolicyAccepted())) {
            throw new ValidationException("Privacy policy must be accepted");
        }
    }
    
    // ========== HELPER METHODS ==========
    
    /**
     * Validates the create application request using shared utilities.
     */
    private void validateCreateApplicationRequest(CreateCustomerOnboardingApplicationRequest request) {
        if (!EmailValidator.isValid(request.customerEmail())) {
            throw new ValidationException("Invalid email format: " + request.customerEmail());
        }
        
        if (!PhoneValidator.isValid(request.customerPhone())) {
            throw new ValidationException("Invalid phone format: " + request.customerPhone());
        }
        
        if (!StringUtils.hasText(request.firstName())) {
            throw new ValidationException("First name is required");
        }
        
        if (!StringUtils.hasText(request.lastName())) {
            throw new ValidationException("Last name is required");
        }
        
        if (!StringUtils.hasText(request.country())) {
            throw new ValidationException("Country is required");
        }
    }
    
    /**
     * Generates a unique application reference ID.
     */
    private String generateApplicationReferenceId() {
        return IdGenerator.generateId(APPLICATION_PREFIX, LocalDateTime.now());
    }
    
    /**
     * Generates a unique customer reference ID.
     */
    private String generateCustomerReferenceId() {
        return IdGenerator.generateId(CUSTOMER_PREFIX, LocalDateTime.now());
    }
    
    /**
     * Gets the current authenticated username.
     */
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : "SYSTEM";
    }
    
    /**
     * Creates a status history record.
     */
    private void createStatusHistory(CustomerOnboardingApplication application,
                                   CustomerOnboardingStatus fromStatus,
                                   CustomerOnboardingStatus toStatus,
                                   String reason,
                                   String changedBy) {
        CustomerApplicationStatusHistory history = CustomerApplicationStatusHistory.builder()
                .application(application)
                .fromStatus(fromStatus)
                .toStatus(toStatus)
                .changeReason(reason)
                .changedBy(changedBy)
                .changedAt(LocalDateTime.now())
                .build();
        
        String currentUser = getCurrentUsername();
        history.setCreatedBy(currentUser);
        history.setUpdatedBy(currentUser);
        
        statusHistoryRepository.save(history);
    }
    
    /**
     * Maps application entity to response DTO.
     */
    private CustomerOnboardingApplicationResponse mapToApplicationResponse(CustomerOnboardingApplication application) {
        // Count verification documents
        int documentCount = application.getVerificationDocuments() != null ? 
                          application.getVerificationDocuments().size() : 0;
        
        // Map verification documents
        List<VerificationDocumentResponse> documentResponses = application.getVerificationDocuments() != null ?
                application.getVerificationDocuments().stream()
                        .map(this::mapDocumentToResponse)
                        .collect(Collectors.toList()) : 
                List.of();
        
        return new CustomerOnboardingApplicationResponse(
                application.getApplicationReferenceId(),
                application.getCustomerEmail(),
                application.getCustomerPhone(),
                application.getFirstName(),
                application.getLastName(),
                application.getDateOfBirth(),
                application.getNationalId(),
                application.getAddressLine1(),
                application.getAddressLine2(),
                application.getCity(),
                application.getStateProvince(),
                application.getPostalCode(),
                application.getCountry(),
                application.getApplicationStatus(),
                application.getKycVerificationId(),
                application.getAuthServiceUserId(),
                application.getBillingCustomerId(),
                application.getPreferredCommunicationMethod(),
                application.getMarketingConsent(),
                application.getTermsAccepted(),
                application.getPrivacyPolicyAccepted(),
                application.getSubmittedAt(),
                application.getApprovedAt(),
                application.getRejectedAt(),
                application.getRejectionReason(),
                application.getCreatedAt(),
                application.getUpdatedAt(),
                documentCount,
                documentResponses
        );
    }
    
    private ApplicationStatusHistoryResponse mapStatusHistoryToResponse(CustomerApplicationStatusHistory history) {
        return new ApplicationStatusHistoryResponse(
                history.getId(),
                history.getFromStatus(),
                history.getToStatus(),
                history.getChangeReason(),
                history.getNotes(),
                history.getChangedBy(),
                history.getChangedAt()
        );
    }
    
    private VerificationDocumentResponse mapDocumentToResponse(CustomerVerificationDocument document) {
        return new VerificationDocumentResponse(
                document.getId(),
                document.getDocumentType(),
                document.getDocumentReferenceId(),
                document.getFileName(),
                document.getFileSize(),
                document.getMimeType(),
                document.getVerificationStatus(),
                document.getVerificationNotes(),
                document.getVerifiedBy(),
                document.getVerifiedAt(),
                document.getUploadedAt(),
                document.getUploadedBy()
        );
    }
}