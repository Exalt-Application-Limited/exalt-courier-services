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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

/**
 * Real PostgreSQL integration tests for CorporateOnboardingService using Testcontainers.
 * 
 * These tests use actual PostgreSQL database via Docker containers to ensure
 * production parity and test real database-specific features like UUID generation,
 * triggers, and PostgreSQL-specific SQL functions.
 * 
 * Key benefits of using PostgreSQL over H2:
 * 1. Production Parity - Tests against same database as production
 * 2. UUID Support - Native PostgreSQL UUID generation and handling
 * 3. Database Functions - Tests PostgreSQL-specific functions and triggers
 * 4. SQL Dialect - Validates PostgreSQL-specific SQL syntax
 * 5. Real Constraints - Tests actual database constraints and validation
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@Transactional
class CorporateOnboardingServicePostgreSQLIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("corporate_onboarding_test")
            .withUsername("test_user")
            .withPassword("test_password")
            .withInitScript("test-schema-init.sql"); // Optional initialization script

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQLDialect");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("spring.flyway.clean-disabled", () -> "false");
    }

    @Autowired
    private CorporateOnboardingServiceImplNew corporateOnboardingService;

    @Autowired
    private CorporateOnboardingApplicationRepository applicationRepository;

    private CreateCorporateOnboardingApplicationRequest validRequest;
    private CreateCorporateOnboardingApplicationRequest highValueRequest;

    @BeforeEach
    void setUp() {
        // Clean database using PostgreSQL-specific operations
        applicationRepository.deleteAll();
        
        // Verify PostgreSQL-specific features work
        applicationRepository.flush(); // Force database sync
        
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
    }

    @Test
    void testPostgreSQLUUIDGeneration() {
        // When - Create application to test UUID generation
        CorporateOnboardingApplicationResponse response = corporateOnboardingService
                .createApplication(validRequest, "test-user");

        // Then - Verify PostgreSQL UUID is properly generated
        assertThat(response.id()).isNotNull();
        assertThat(response.id().toString()).hasSize(36); // Standard UUID format
        
        // Verify database has proper UUID
        CorporateOnboardingApplication savedApplication = applicationRepository
                .findById(response.id()).orElse(null);
        assertThat(savedApplication).isNotNull();
        assertThat(savedApplication.getId()).isEqualTo(response.id());
        
        // Verify application reference ID follows our pattern
        assertThat(response.applicationReferenceId())
                .isNotNull()
                .startsWith("CORP-")
                .matches("CORP-\\d{8}-[A-Z0-9]{8}");
    }

    @Test
    void testPostgreSQLTimestampTriggers() {
        // When - Create and update application
        CorporateOnboardingApplicationResponse created = corporateOnboardingService
                .createApplication(validRequest, "test-user");
        
        // Get initial timestamps
        assertThat(created.createdAt()).isNotNull();
        assertThat(created.updatedAt()).isNotNull();
        
        // Wait a moment and update
        try {
            Thread.sleep(100); // Small delay to ensure timestamp difference
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        UpdateCorporateOnboardingApplicationRequest updateRequest = 
                new UpdateCorporateOnboardingApplicationRequest(
                        "Updated Company Name", null, null, null, null, null,
                        null, null, null, null, null, null, null, null, null, null,
                        null, null, null, null, null, null, null, null, null,
                        null, null, null, null, null
                );
        
        CorporateOnboardingApplicationResponse updated = corporateOnboardingService
                .updateApplication(created.id(), updateRequest, "test-updater");

        // Then - Verify PostgreSQL triggers updated the timestamp
        assertThat(updated.updatedAt()).isAfter(created.updatedAt());
        assertThat(updated.createdAt()).isEqualTo(created.createdAt()); // Should not change
        
        // Verify in database that trigger worked
        CorporateOnboardingApplication dbRecord = applicationRepository
                .findById(created.id()).orElse(null);
        assertThat(dbRecord).isNotNull();
        assertThat(dbRecord.getUpdatedAt()).isAfter(dbRecord.getCreatedAt());
    }

    @Test
    void testPostgreSQLEnumHandling() {
        // When - Create application with various enum values
        CorporateOnboardingApplicationResponse response = corporateOnboardingService
                .createApplication(validRequest, "test-user");

        // Then - Verify PostgreSQL properly stores and retrieves enums
        assertThat(response.businessType()).isEqualTo(BusinessType.CORPORATION);
        assertThat(response.industrySector()).isEqualTo(IndustrySector.TECHNOLOGY);
        assertThat(response.companySize()).isEqualTo(CompanySize.MEDIUM);
        assertThat(response.applicationStatus()).isEqualTo(CorporateOnboardingStatus.DRAFT);
        
        // Verify enum storage in database
        CorporateOnboardingApplication savedApplication = applicationRepository
                .findById(response.id()).orElse(null);
        assertThat(savedApplication).isNotNull();
        assertThat(savedApplication.getBusinessType()).isEqualTo(BusinessType.CORPORATION);
        assertThat(savedApplication.getIndustrySector()).isEqualTo(IndustrySector.TECHNOLOGY);
    }

    @Test
    void testPostgreSQLBigDecimalPrecision() {
        // When - Create application with specific decimal precision
        BigDecimal preciseCreditLimit = new BigDecimal("123456.78");
        CreateCorporateOnboardingApplicationRequest precisionRequest = 
                new CreateCorporateOnboardingApplicationRequest(
                        validRequest.companyName(), "REG999888777", validRequest.taxIdentificationNumber(),
                        validRequest.businessLicenseNumber(), "precision@test.com", validRequest.companyPhone(),
                        validRequest.companyWebsite(), validRequest.businessType(), validRequest.industrySector(),
                        validRequest.companySize(), validRequest.annualShippingVolume(),
                        validRequest.businessAddressLine1(), validRequest.businessAddressLine2(),
                        validRequest.businessCity(), validRequest.businessStateProvince(),
                        validRequest.businessPostalCode(), validRequest.businessCountry(),
                        validRequest.primaryContactFirstName(), validRequest.primaryContactLastName(),
                        "precision@contact.com", validRequest.primaryContactPhone(),
                        validRequest.primaryContactPosition(), validRequest.billingContactFirstName(),
                        validRequest.billingContactLastName(), validRequest.billingContactEmail(),
                        validRequest.billingContactPhone(), preciseCreditLimit,
                        validRequest.preferredPaymentTerms(), validRequest.slaRequirements(),
                        validRequest.preferredCommunicationMethod(), validRequest.marketingConsent(),
                        validRequest.termsAccepted(), validRequest.privacyPolicyAccepted(),
                        validRequest.dataProcessingAgreementAccepted()
                );

        CorporateOnboardingApplicationResponse response = corporateOnboardingService
                .createApplication(precisionRequest, "test-user");

        // Then - Verify PostgreSQL maintains decimal precision
        assertThat(response.requestedCreditLimit()).isEqualTo(preciseCreditLimit);
        assertThat(response.requestedCreditLimit().scale()).isEqualTo(2);
        
        // Verify in database
        CorporateOnboardingApplication savedApplication = applicationRepository
                .findById(response.id()).orElse(null);
        assertThat(savedApplication).isNotNull();
        assertThat(savedApplication.getRequestedCreditLimit()).isEqualTo(preciseCreditLimit);
    }

    @Test
    void testPostgreSQLIndexPerformance() {
        // Given - Create multiple applications for index testing
        for (int i = 0; i < 5; i++) {
            CreateCorporateOnboardingApplicationRequest indexTestRequest = 
                    new CreateCorporateOnboardingApplicationRequest(
                            "Company " + i, "REG" + i, "TAX" + i, "BIZ" + i,
                            "company" + i + "@test.com", "+1-555-000" + i, "https://company" + i + ".com",
                            BusinessType.CORPORATION, IndustrySector.TECHNOLOGY, CompanySize.MEDIUM,
                            ShippingVolume.HIGH, "Address " + i, "Suite " + i, "City " + i,
                            "State " + i, "1000" + i, "Country " + i, "First" + i, "Last" + i,
                            "contact" + i + "@test.com", "+1-555-100" + i, "Position " + i,
                            null, null, null, null, new BigDecimal("10000.00"),
                            PaymentTerms.NET_30, "SLA " + i, CommunicationMethod.EMAIL, true,
                            true, true, true
                    );
            corporateOnboardingService.createApplication(indexTestRequest, "test-user");
        }

        // When - Query using indexed fields
        long startTime = System.currentTimeMillis();
        
        // Test company email index
        CorporateOnboardingApplicationResponse foundByEmail = corporateOnboardingService
                .getApplicationByReferenceId(
                        applicationRepository.findByCompanyEmail("company2@test.com")
                                .orElseThrow().getApplicationReferenceId()
                );
        
        // Test status index
        Page<CorporateOnboardingApplicationResponse> draftApplications = corporateOnboardingService
                .getApplications(CorporateOnboardingStatus.DRAFT, PageRequest.of(0, 10));
        
        long endTime = System.currentTimeMillis();
        
        // Then - Verify queries work efficiently with PostgreSQL indexes
        assertThat(foundByEmail).isNotNull();
        assertThat(foundByEmail.companyEmail()).isEqualTo("company2@test.com");
        
        assertThat(draftApplications.getContent()).hasSize(5);
        
        // Performance should be reasonable (adjust threshold as needed)
        long queryTime = endTime - startTime;
        assertThat(queryTime).isLessThan(1000); // Should complete within 1 second
    }

    @Test
    void testPostgreSQLTransactionRollback() {
        // Given - Valid application
        CorporateOnboardingApplicationResponse created = corporateOnboardingService
                .createApplication(validRequest, "test-user");
        
        // When - Attempt invalid update that should trigger rollback
        assertThatThrownBy(() -> {
            // This should fail validation and rollback the transaction
            UpdateCorporateOnboardingApplicationRequest invalidUpdate = 
                    new UpdateCorporateOnboardingApplicationRequest(
                            null, null, null, "invalid-email-format", null, null,
                            null, null, null, null, null, null, null, null, null, null,
                            null, null, null, null, null, null, null, null, null,
                            null, null, null, null, null
                    );
            corporateOnboardingService.updateApplication(created.id(), invalidUpdate, "test-user");
        }).isInstanceOf(Exception.class);
        
        // Then - Verify original data is preserved (transaction rolled back)
        CorporateOnboardingApplicationResponse preserved = corporateOnboardingService
                .getApplication(created.id());
        assertThat(preserved.companyName()).isEqualTo(validRequest.companyName());
        assertThat(preserved.companyEmail()).isEqualTo(validRequest.companyEmail());
    }

    @Test
    void testCompleteWorkflowWithPostgreSQLFeatures() {
        // Given - Create application leveraging PostgreSQL features
        CorporateOnboardingApplicationResponse created = corporateOnboardingService
                .createApplication(validRequest, "test-user");
        
        // Verify PostgreSQL UUID and timestamps
        assertThat(created.id()).isNotNull();
        assertThat(created.createdAt()).isNotNull();
        assertThat(created.applicationReferenceId()).startsWith("CORP-");

        // When - Complete full workflow using PostgreSQL transaction handling
        CorporateOnboardingApplicationResponse submitted = corporateOnboardingService
                .submitApplication(created.id(), "customer-user");
        assertThat(submitted.applicationStatus()).isEqualTo(CorporateOnboardingStatus.SUBMITTED);

        CorporateOnboardingApplicationResponse kybInitiated = corporateOnboardingService
                .initiateKybVerification(created.id(), "kyc-processor");
        assertThat(kybInitiated.applicationStatus()).isEqualTo(CorporateOnboardingStatus.KYB_IN_PROGRESS);
        assertThat(kybInitiated.kybVerificationId()).startsWith("KYB-");

        CorporateOnboardingApplicationResponse kybApproved = corporateOnboardingService
                .processKybResult(created.id(), kybInitiated.kybVerificationId(), true, "kyc-reviewer");
        assertThat(kybApproved.applicationStatus()).isEqualTo(CorporateOnboardingStatus.KYB_APPROVED);

        // Then - Verify PostgreSQL stored all state transitions correctly
        CorporateOnboardingApplication finalState = applicationRepository
                .findById(created.id()).orElse(null);
        assertThat(finalState).isNotNull();
        assertThat(finalState.getApplicationStatus()).isEqualTo(CorporateOnboardingStatus.KYB_APPROVED);
        assertThat(finalState.getKybVerificationId()).isNotNull();
        assertThat(finalState.getSubmittedAt()).isNotNull();
        assertThat(finalState.getUpdatedAt()).isAfter(finalState.getCreatedAt());
        
        // Verify PostgreSQL audit trail
        assertThat(finalState.getCreatedBy()).isEqualTo("test-user");
        assertThat(finalState.getUpdatedBy()).isEqualTo("kyc-reviewer");
    }
}