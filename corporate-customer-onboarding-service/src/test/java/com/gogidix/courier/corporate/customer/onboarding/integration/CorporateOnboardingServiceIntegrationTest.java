package com.gogidix.courier.corporate.customer.onboarding.integration;

import com.gogidix.courier.corporate.customer.onboarding.dto.*;
import com.gogidix.courier.corporate.customer.onboarding.enums.*;
import com.gogidix.courier.corporate.customer.onboarding.model.CorporateOnboardingApplication;
import com.gogidix.courier.corporate.customer.onboarding.repository.CorporateOnboardingApplicationRepository;
import com.gogidix.courier.corporate.customer.onboarding.service.impl.CorporateOnboardingServiceImplNew;
import com.gogidix.ecosystem.shared.exceptions.ResourceNotFoundException;
import com.gogidix.ecosystem.shared.exceptions.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

/**
 * Real integration tests for CorporateOnboardingService.
 * 
 * These tests use actual database connections and real service instances
 * without mocks, as requested by the user. Tests verify complete business
 * workflows and data persistence.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:postgresql://localhost:5433/corporate_onboarding_test",
    "spring.datasource.username=test_user",
    "spring.datasource.password=test_password",
    "spring.datasource.driver-class-name=org.postgresql.Driver",
    "spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.flyway.enabled=true",
    "spring.flyway.clean-disabled=false",
    "logging.level.com.exalt=DEBUG",
    "logging.level.org.springframework.jdbc=DEBUG"
})
@Transactional
class CorporateOnboardingServiceIntegrationTest {

    @Autowired
    private CorporateOnboardingServiceImplNew corporateOnboardingService;

    @Autowired
    private CorporateOnboardingApplicationRepository applicationRepository;

    private CreateCorporateOnboardingApplicationRequest validRequest;
    private CreateCorporateOnboardingApplicationRequest highValueRequest;
    private CreateCorporateOnboardingApplicationRequest invalidRequest;

    @BeforeEach
    void setUp() {
        // Clean database
        applicationRepository.deleteAll();
        
        // Setup valid standard request
        validRequest = new CreateCorporateOnboardingApplicationRequest(
                "Acme Corporation Ltd",
                "REG123456789",
                "TAX987654321",
                "BIZ456789123",
                "contact@acmecorp.com",
                "+1-555-0123",
                "https://www.acmecorp.com",
                BusinessType.CORPORATION,
                IndustrySector.TECHNOLOGY,
                CompanySize.MEDIUM,
                ShippingVolume.HIGH,
                "123 Business Street",
                "Suite 100",
                "Business City",
                "California",
                "90210",
                "United States",
                "John",
                "Smith",
                "john.smith@acmecorp.com",
                "+1-555-0124",
                "CEO",
                "Jane",
                "Doe",
                "jane.doe@acmecorp.com",
                "+1-555-0125",
                new BigDecimal("50000.00"),
                PaymentTerms.NET_30,
                "Standard SLA requirements",
                CommunicationMethod.EMAIL,
                true,
                true,
                true,
                true
        );

        // Setup high-value prospect request
        highValueRequest = new CreateCorporateOnboardingApplicationRequest(
                "Enterprise Solutions Inc",
                "ENT987654321",
                "TAX123456789",
                "BIZ789123456",
                "ceo@enterprise-solutions.com",
                "+1-555-0200",
                "https://www.enterprise-solutions.com",
                BusinessType.CORPORATION,
                IndustrySector.LOGISTICS,
                CompanySize.ENTERPRISE,
                ShippingVolume.ENTERPRISE,
                "456 Enterprise Boulevard",
                "Floor 20",
                "Enterprise City",
                "New York",
                "10001",
                "United States",
                "Robert",
                "Johnson",
                "robert.johnson@enterprise-solutions.com",
                "+1-555-0201",
                "President",
                "Sarah",
                "Wilson",
                "sarah.wilson@enterprise-solutions.com",
                "+1-555-0202",
                new BigDecimal("500000.00"),
                PaymentTerms.NET_45,
                "Premium SLA with 24/7 support required",
                CommunicationMethod.PHONE,
                false,
                true,
                true,
                true
        );

        // Setup invalid request (missing required fields)
        invalidRequest = new CreateCorporateOnboardingApplicationRequest(
                null, // Missing company name
                "REG999888777",
                null,
                null,
                "invalid-email", // Invalid email format
                "invalid-phone", // Invalid phone format
                null,
                null, // Missing business type
                null, // Missing industry sector
                null, // Missing company size
                null, // Missing shipping volume
                null, // Missing address
                null,
                null, // Missing city
                null,
                null, // Missing postal code
                null, // Missing country
                null, // Missing contact first name
                null, // Missing contact last name
                null, // Missing contact email
                null, // Missing contact phone
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                false, // Terms not accepted
                false, // Privacy policy not accepted
                false  // Data processing not accepted
        );
    }

    @Test
    void createApplication_WithValidRequest_ShouldCreateSuccessfully() {
        // When
        CorporateOnboardingApplicationResponse response = corporateOnboardingService
                .createApplication(validRequest, "test-user");

        // Then
        assertThat(response).isNotNull();
        assertThat(response.applicationReferenceId()).isNotNull().startsWith("CORP-");
        assertThat(response.companyName()).isEqualTo("Acme Corporation Ltd");
        assertThat(response.companyEmail()).isEqualTo("contact@acmecorp.com");
        assertThat(response.applicationStatus()).isEqualTo(CorporateOnboardingStatus.DRAFT);
        assertThat(response.businessType()).isEqualTo(BusinessType.CORPORATION);
        assertThat(response.industrySector()).isEqualTo(IndustrySector.TECHNOLOGY);
        assertThat(response.companySize()).isEqualTo(CompanySize.MEDIUM);
        assertThat(response.annualShippingVolume()).isEqualTo(ShippingVolume.HIGH);
        assertThat(response.createdBy()).isEqualTo("test-user");
        assertThat(response.createdAt()).isNotNull();
        
        // Verify in database
        CorporateOnboardingApplication savedApplication = applicationRepository
                .findByApplicationReferenceId(response.applicationReferenceId()).orElse(null);
        assertThat(savedApplication).isNotNull();
        assertThat(savedApplication.getCompanyName()).isEqualTo("Acme Corporation Ltd");
        assertThat(savedApplication.getApplicationStatus()).isEqualTo(CorporateOnboardingStatus.DRAFT);
    }

    @Test
    void createApplication_WithHighValueProspect_ShouldSetCorrectTier() {
        // When
        CorporateOnboardingApplicationResponse response = corporateOnboardingService
                .createApplication(highValueRequest, "sales-team");

        // Then
        assertThat(response).isNotNull();
        assertThat(response.companySize()).isEqualTo(CompanySize.ENTERPRISE);
        assertThat(response.annualShippingVolume()).isEqualTo(ShippingVolume.ENTERPRISE);
        assertThat(response.requestedCreditLimit()).isEqualTo(new BigDecimal("500000.00"));
        
        // Verify business logic
        CreateCorporateOnboardingApplicationRequest originalRequest = highValueRequest;
        assertThat(originalRequest.isHighValueProspect()).isTrue();
        assertThat(originalRequest.recommendsExpeditedProcessing()).isTrue();
        assertThat(originalRequest.recommendsDedicatedAccountManager()).isTrue();
    }

    @Test
    void createApplication_WithDuplicateCompany_ShouldThrowValidationException() {
        // Given - Create first application
        corporateOnboardingService.createApplication(validRequest, "test-user");

        // When & Then - Try to create duplicate
        assertThatThrownBy(() -> corporateOnboardingService.createApplication(validRequest, "test-user"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Company already has an active application");
    }

    @Test
    void getApplication_WithExistingId_ShouldReturnApplication() {
        // Given
        CorporateOnboardingApplicationResponse created = corporateOnboardingService
                .createApplication(validRequest, "test-user");

        // When
        CorporateOnboardingApplicationResponse retrieved = corporateOnboardingService
                .getApplication(created.id());

        // Then
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.id()).isEqualTo(created.id());
        assertThat(retrieved.companyName()).isEqualTo(created.companyName());
        assertThat(retrieved.applicationStatus()).isEqualTo(created.applicationStatus());
    }

    @Test
    void getApplication_WithNonExistentId_ShouldThrowResourceNotFoundException() {
        // Given
        UUID nonExistentId = UUID.randomUUID();

        // When & Then
        assertThatThrownBy(() -> corporateOnboardingService.getApplication(nonExistentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Corporate application not found");
    }

    @Test
    void getApplicationByReferenceId_WithExistingReference_ShouldReturnApplication() {
        // Given
        CorporateOnboardingApplicationResponse created = corporateOnboardingService
                .createApplication(validRequest, "test-user");

        // When
        CorporateOnboardingApplicationResponse retrieved = corporateOnboardingService
                .getApplicationByReferenceId(created.applicationReferenceId());

        // Then
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.applicationReferenceId()).isEqualTo(created.applicationReferenceId());
        assertThat(retrieved.companyName()).isEqualTo(created.companyName());
    }

    @Test
    void updateApplication_WithValidChanges_ShouldUpdateSuccessfully() {
        // Given
        CorporateOnboardingApplicationResponse created = corporateOnboardingService
                .createApplication(validRequest, "test-user");

        UpdateCorporateOnboardingApplicationRequest updateRequest = 
                new UpdateCorporateOnboardingApplicationRequest(
                        "Updated Acme Corporation Ltd",
                        "TAX999888777",
                        "BIZ777888999",
                        "updated@acmecorp.com",
                        "+1-555-9999",
                        "https://www.updated-acmecorp.com",
                        BusinessType.LLC,
                        IndustrySector.RETAIL,
                        CompanySize.LARGE,
                        ShippingVolume.VERY_HIGH,
                        "789 Updated Street",
                        "Unit 200",
                        "Updated City",
                        "Texas",
                        "75001",
                        "United States",
                        "Jane",
                        "Updated",
                        "jane.updated@acmecorp.com",
                        "+1-555-8888",
                        "CTO",
                        "Bob",
                        "Finance",
                        "bob.finance@acmecorp.com",
                        "+1-555-7777",
                        new BigDecimal("100000.00"),
                        PaymentTerms.NET_60,
                        "Updated SLA requirements",
                        CommunicationMethod.SMS,
                        false
                );

        // When
        CorporateOnboardingApplicationResponse updated = corporateOnboardingService
                .updateApplication(created.id(), updateRequest, "test-updater");

        // Then
        assertThat(updated).isNotNull();
        assertThat(updated.companyName()).isEqualTo("Updated Acme Corporation Ltd");
        assertThat(updated.companyEmail()).isEqualTo("updated@acmecorp.com");
        assertThat(updated.businessType()).isEqualTo(BusinessType.LLC);
        assertThat(updated.companySize()).isEqualTo(CompanySize.LARGE);
        assertThat(updated.updatedBy()).isEqualTo("test-updater");
        assertThat(updated.updatedAt()).isAfter(updated.createdAt());
        
        // Verify in database
        CorporateOnboardingApplication savedApplication = applicationRepository
                .findById(created.id()).orElse(null);
        assertThat(savedApplication).isNotNull();
        assertThat(savedApplication.getCompanyName()).isEqualTo("Updated Acme Corporation Ltd");
    }

    @Test
    void submitApplication_WithValidDraftApplication_ShouldSubmitSuccessfully() {
        // Given
        CorporateOnboardingApplicationResponse created = corporateOnboardingService
                .createApplication(validRequest, "test-user");

        // When
        CorporateOnboardingApplicationResponse submitted = corporateOnboardingService
                .submitApplication(created.id(), "customer-user");

        // Then
        assertThat(submitted).isNotNull();
        assertThat(submitted.applicationStatus()).isEqualTo(CorporateOnboardingStatus.SUBMITTED);
        assertThat(submitted.submittedAt()).isNotNull();
        assertThat(submitted.updatedBy()).isEqualTo("customer-user");
    }

    @Test
    void submitApplication_WithNonDraftStatus_ShouldThrowValidationException() {
        // Given
        CorporateOnboardingApplicationResponse created = corporateOnboardingService
                .createApplication(validRequest, "test-user");
        
        // Submit once
        corporateOnboardingService.submitApplication(created.id(), "customer-user");

        // When & Then - Try to submit again
        assertThatThrownBy(() -> corporateOnboardingService.submitApplication(created.id(), "customer-user"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Only draft applications can be submitted");
    }

    @Test
    void updateStatus_WithValidTransition_ShouldUpdateSuccessfully() {
        // Given
        CorporateOnboardingApplicationResponse created = corporateOnboardingService
                .createApplication(validRequest, "test-user");
        
        corporateOnboardingService.submitApplication(created.id(), "customer-user");

        StatusUpdateRequest statusUpdate = new StatusUpdateRequest(
                CorporateOnboardingStatus.DOCUMENTS_REQUIRED,
                "Additional documentation needed",
                "Require business license and tax certificates"
        );

        // When
        CorporateOnboardingApplicationResponse updated = corporateOnboardingService
                .updateStatus(created.id(), statusUpdate, "admin-user");

        // Then
        assertThat(updated).isNotNull();
        assertThat(updated.applicationStatus()).isEqualTo(CorporateOnboardingStatus.DOCUMENTS_REQUIRED);
        assertThat(updated.updatedBy()).isEqualTo("admin-user");
    }

    @Test
    void updateStatus_WithInvalidTransition_ShouldThrowValidationException() {
        // Given
        CorporateOnboardingApplicationResponse created = corporateOnboardingService
                .createApplication(validRequest, "test-user");

        StatusUpdateRequest invalidUpdate = new StatusUpdateRequest(
                CorporateOnboardingStatus.APPROVED, // Cannot go directly from DRAFT to APPROVED
                "Invalid transition",
                "Should not work"
        );

        // When & Then
        assertThatThrownBy(() -> corporateOnboardingService.updateStatus(created.id(), invalidUpdate, "admin-user"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Invalid status transition");
    }

    @Test
    void initiateKybVerification_WithSubmittedApplication_ShouldInitiateSuccessfully() {
        // Given
        CorporateOnboardingApplicationResponse created = corporateOnboardingService
                .createApplication(validRequest, "test-user");
        
        corporateOnboardingService.submitApplication(created.id(), "customer-user");

        // When
        CorporateOnboardingApplicationResponse kybInitiated = corporateOnboardingService
                .initiateKybVerification(created.id(), "kyc-processor");

        // Then
        assertThat(kybInitiated).isNotNull();
        assertThat(kybInitiated.applicationStatus()).isEqualTo(CorporateOnboardingStatus.KYB_IN_PROGRESS);
        assertThat(kybInitiated.kybVerificationId()).isNotNull().startsWith("KYB-");
        assertThat(kybInitiated.updatedBy()).isEqualTo("kyc-processor");
    }

    @Test
    void processKybResult_WithValidVerificationId_ShouldProcessSuccessfully() {
        // Given
        CorporateOnboardingApplicationResponse created = corporateOnboardingService
                .createApplication(validRequest, "test-user");
        
        corporateOnboardingService.submitApplication(created.id(), "customer-user");
        CorporateOnboardingApplicationResponse kybInitiated = corporateOnboardingService
                .initiateKybVerification(created.id(), "kyc-processor");

        // When - Process as approved
        CorporateOnboardingApplicationResponse kybProcessed = corporateOnboardingService
                .processKybResult(created.id(), kybInitiated.kybVerificationId(), true, "kyc-reviewer");

        // Then
        assertThat(kybProcessed).isNotNull();
        assertThat(kybProcessed.applicationStatus()).isEqualTo(CorporateOnboardingStatus.KYB_APPROVED);
        assertThat(kybProcessed.updatedBy()).isEqualTo("kyc-reviewer");
    }

    @Test
    void processKybResult_WithMismatchedVerificationId_ShouldThrowValidationException() {
        // Given
        CorporateOnboardingApplicationResponse created = corporateOnboardingService
                .createApplication(validRequest, "test-user");
        
        corporateOnboardingService.submitApplication(created.id(), "customer-user");
        corporateOnboardingService.initiateKybVerification(created.id(), "kyc-processor");

        String wrongVerificationId = "KYB-WRONG-ID";

        // When & Then
        assertThatThrownBy(() -> corporateOnboardingService
                .processKybResult(created.id(), wrongVerificationId, true, "kyc-reviewer"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("KYB verification ID mismatch");
    }

    @Test
    void initiateCreditAssessment_AfterKybApproval_ShouldInitiateSuccessfully() {
        // Given
        CorporateOnboardingApplicationResponse created = corporateOnboardingService
                .createApplication(validRequest, "test-user");
        
        corporateOnboardingService.submitApplication(created.id(), "customer-user");
        CorporateOnboardingApplicationResponse kybInitiated = corporateOnboardingService
                .initiateKybVerification(created.id(), "kyc-processor");
        corporateOnboardingService.processKybResult(created.id(), kybInitiated.kybVerificationId(), true, "kyc-reviewer");

        // When
        CorporateOnboardingApplicationResponse creditInitiated = corporateOnboardingService
                .initiateCreditAssessment(created.id(), "credit-processor");

        // Then
        assertThat(creditInitiated).isNotNull();
        assertThat(creditInitiated.applicationStatus()).isEqualTo(CorporateOnboardingStatus.CREDIT_CHECK_IN_PROGRESS);
        assertThat(creditInitiated.updatedBy()).isEqualTo("credit-processor");
    }

    @Test
    void getApplications_WithPagination_ShouldReturnPagedResults() {
        // Given - Create multiple applications
        corporateOnboardingService.createApplication(validRequest, "test-user-1");
        corporateOnboardingService.createApplication(highValueRequest, "test-user-2");

        PageRequest pageRequest = PageRequest.of(0, 10);

        // When
        Page<CorporateOnboardingApplicationResponse> applications = corporateOnboardingService
                .getApplications(null, pageRequest);

        // Then
        assertThat(applications).isNotNull();
        assertThat(applications.getContent()).hasSize(2);
        assertThat(applications.getTotalElements()).isEqualTo(2);
        assertThat(applications.getNumberOfElements()).isEqualTo(2);
    }

    @Test
    void getApplications_WithStatusFilter_ShouldReturnFilteredResults() {
        // Given
        CorporateOnboardingApplicationResponse draft1 = corporateOnboardingService
                .createApplication(validRequest, "test-user-1");
        
        CorporateOnboardingApplicationResponse draft2 = corporateOnboardingService
                .createApplication(highValueRequest, "test-user-2");
        
        // Submit one application
        corporateOnboardingService.submitApplication(draft1.id(), "customer-user");

        PageRequest pageRequest = PageRequest.of(0, 10);

        // When - Filter by DRAFT status
        Page<CorporateOnboardingApplicationResponse> draftApplications = corporateOnboardingService
                .getApplications(CorporateOnboardingStatus.DRAFT, pageRequest);

        // Then
        assertThat(draftApplications).isNotNull();
        assertThat(draftApplications.getContent()).hasSize(1);
        assertThat(draftApplications.getContent().get(0).applicationStatus()).isEqualTo(CorporateOnboardingStatus.DRAFT);
        assertThat(draftApplications.getContent().get(0).id()).isEqualTo(draft2.id());
    }

    @Test
    void approveApplication_WithValidApplication_ShouldApproveSuccessfully() {
        // Given - Complete application workflow
        CorporateOnboardingApplicationResponse created = corporateOnboardingService
                .createApplication(validRequest, "test-user");
        
        corporateOnboardingService.submitApplication(created.id(), "customer-user");
        
        // Update to UNDER_REVIEW status (simulating completed verification)
        StatusUpdateRequest reviewUpdate = new StatusUpdateRequest(
                CorporateOnboardingStatus.UNDER_REVIEW,
                "All verifications completed",
                "Ready for final approval"
        );
        corporateOnboardingService.updateStatus(created.id(), reviewUpdate, "admin-user");

        // When
        CorporateOnboardingApplicationResponse approved = corporateOnboardingService
                .approveApplication(created.id(), "senior-admin", "Application meets all requirements");

        // Then
        assertThat(approved).isNotNull();
        assertThat(approved.applicationStatus()).isEqualTo(CorporateOnboardingStatus.APPROVED);
        assertThat(approved.approvedAt()).isNotNull();
        assertThat(approved.approvedBy()).isEqualTo("senior-admin");
    }

    @Test
    void rejectApplication_WithReason_ShouldRejectSuccessfully() {
        // Given
        CorporateOnboardingApplicationResponse created = corporateOnboardingService
                .createApplication(validRequest, "test-user");
        
        corporateOnboardingService.submitApplication(created.id(), "customer-user");

        // When
        CorporateOnboardingApplicationResponse rejected = corporateOnboardingService
                .rejectApplication(created.id(), "admin-user", "Insufficient business documentation");

        // Then
        assertThat(rejected).isNotNull();
        assertThat(rejected.applicationStatus()).isEqualTo(CorporateOnboardingStatus.REJECTED);
        assertThat(rejected.rejectedAt()).isNotNull();
        assertThat(rejected.rejectedBy()).isEqualTo("admin-user");
        assertThat(rejected.rejectionReason()).isEqualTo("Insufficient business documentation");
    }

    @Test
    void cancelApplication_WithActiveApplication_ShouldCancelSuccessfully() {
        // Given
        CorporateOnboardingApplicationResponse created = corporateOnboardingService
                .createApplication(validRequest, "test-user");

        // When
        CorporateOnboardingApplicationResponse cancelled = corporateOnboardingService
                .cancelApplication(created.id(), "customer-user");

        // Then
        assertThat(cancelled).isNotNull();
        assertThat(cancelled.applicationStatus()).isEqualTo(CorporateOnboardingStatus.CANCELLED);
        assertThat(cancelled.updatedBy()).isEqualTo("customer-user");
    }

    @Test
    void isCompanyAlreadyRegistered_WithExistingCompany_ShouldReturnTrue() {
        // Given
        corporateOnboardingService.createApplication(validRequest, "test-user");

        // When
        boolean isRegistered = corporateOnboardingService
                .isCompanyAlreadyRegistered("contact@acmecorp.com", "REG123456789");

        // Then
        assertThat(isRegistered).isTrue();
    }

    @Test
    void isCompanyAlreadyRegistered_WithNewCompany_ShouldReturnFalse() {
        // When
        boolean isRegistered = corporateOnboardingService
                .isCompanyAlreadyRegistered("new@company.com", "REG999999999");

        // Then
        assertThat(isRegistered).isFalse();
    }

    @Test
    void completeWorkflow_FromDraftToApproval_ShouldSucceed() {
        // Given - Create application
        CorporateOnboardingApplicationResponse created = corporateOnboardingService
                .createApplication(validRequest, "test-user");
        
        assertThat(created.applicationStatus()).isEqualTo(CorporateOnboardingStatus.DRAFT);

        // When - Complete full workflow
        // 1. Submit application
        CorporateOnboardingApplicationResponse submitted = corporateOnboardingService
                .submitApplication(created.id(), "customer-user");
        assertThat(submitted.applicationStatus()).isEqualTo(CorporateOnboardingStatus.SUBMITTED);

        // 2. Initiate KYB verification
        CorporateOnboardingApplicationResponse kybInitiated = corporateOnboardingService
                .initiateKybVerification(created.id(), "kyc-processor");
        assertThat(kybInitiated.applicationStatus()).isEqualTo(CorporateOnboardingStatus.KYB_IN_PROGRESS);

        // 3. Complete KYB verification
        CorporateOnboardingApplicationResponse kybApproved = corporateOnboardingService
                .processKybResult(created.id(), kybInitiated.kybVerificationId(), true, "kyc-reviewer");
        assertThat(kybApproved.applicationStatus()).isEqualTo(CorporateOnboardingStatus.KYB_APPROVED);

        // 4. Initiate credit assessment
        CorporateOnboardingApplicationResponse creditInitiated = corporateOnboardingService
                .initiateCreditAssessment(created.id(), "credit-processor");
        assertThat(creditInitiated.applicationStatus()).isEqualTo(CorporateOnboardingStatus.CREDIT_CHECK_IN_PROGRESS);

        // 5. Move to under review
        StatusUpdateRequest reviewUpdate = new StatusUpdateRequest(
                CorporateOnboardingStatus.UNDER_REVIEW,
                "Credit check completed successfully",
                "Ready for final approval"
        );
        CorporateOnboardingApplicationResponse underReview = corporateOnboardingService
                .updateStatus(created.id(), reviewUpdate, "admin-user");
        assertThat(underReview.applicationStatus()).isEqualTo(CorporateOnboardingStatus.UNDER_REVIEW);

        // 6. Final approval
        CorporateOnboardingApplicationResponse approved = corporateOnboardingService
                .approveApplication(created.id(), "senior-admin", "All requirements met");

        // Then - Verify final state
        assertThat(approved.applicationStatus()).isEqualTo(CorporateOnboardingStatus.APPROVED);
        assertThat(approved.approvedAt()).isNotNull();
        assertThat(approved.approvedBy()).isEqualTo("senior-admin");
        
        // Verify complete audit trail
        assertThat(approved.createdAt()).isNotNull();
        assertThat(approved.submittedAt()).isNotNull();
        assertThat(approved.approvedAt()).isAfter(approved.submittedAt());
        assertThat(approved.kybVerificationId()).isNotNull();
    }
}