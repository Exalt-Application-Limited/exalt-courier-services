package com.gogidix.courier.corporate.customer.onboarding.integration;

import com.gogidix.courier.corporate.customer.onboarding.dto.*;
import com.gogidix.courier.corporate.customer.onboarding.enums.*;
import com.gogidix.courier.corporate.customer.onboarding.model.CorporateOnboardingApplication;
import com.gogidix.courier.corporate.customer.onboarding.repository.CorporateOnboardingApplicationRepository;
import com.gogidix.courier.corporate.customer.onboarding.service.CorporateOnboardingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for Corporate Customer Onboarding Service.
 * 
 * Tests the complete corporate onboarding workflow from registration to account activation.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Corporate Customer Onboarding Integration Tests")
@Transactional
class CorporateOnboardingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CorporateOnboardingService corporateOnboardingService;

    @Autowired
    private CorporateOnboardingApplicationRepository applicationRepository;

    private static String applicationReferenceId;

    @BeforeEach
    void setUp() {
        // Clear test data
        applicationRepository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("Should complete corporate customer onboarding workflow successfully")
    void testCompleteCorporateOnboardingWorkflow() throws Exception {
        // Step 1: Check business availability
        mockMvc.perform(get("/api/v1/corporate/check-availability")
                .param("businessEmail", "contact@acmecorp.com")
                .param("businessRegistrationNumber", "BRN-123456789"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        // Step 2: Register new corporate customer
        CreateCorporateOnboardingApplicationRequest registerRequest = new CreateCorporateOnboardingApplicationRequest(
            "Acme Corporation Ltd.",
            BusinessType.CORPORATION,
            IndustrySector.TECHNOLOGY,
            "contact@acmecorp.com",
            "+1-555-0123",
            "BRN-123456789",
            "TAX-987654321",
            "123 Business Avenue",
            "456 Billing Street",
            "John",
            "Smith",
            "john.smith@acmecorp.com",
            "+1-555-0124",
            "CEO",
            5000,
            "Technology consulting and software development",
            "https://www.acmecorp.com",
            250,
            50000000.0,
            CommunicationMethod.EMAIL,
            "Same-day delivery for critical components",
            false,
            true,
            true,
            true
        );

        MvcResult registerResult = mockMvc.perform(post("/api/v1/corporate/onboarding/applications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.businessName").value("Acme Corporation Ltd."))
                .andExpect(jsonPath("$.businessEmail").value("contact@acmecorp.com"))
                .andExpect(jsonPath("$.applicationStatus").value("DRAFT"))
                .andExpected(jsonPath("$.applicationReferenceId").exists())
                .andReturn();

        String responseJson = registerResult.getResponse().getContentAsString();
        CorporateOnboardingApplicationResponse application = objectMapper.readValue(
            responseJson, CorporateOnboardingApplicationResponse.class);

        applicationReferenceId = application.applicationReferenceId();
        assertNotNull(applicationReferenceId);
        assertTrue(applicationReferenceId.startsWith("CORP-ONB"));

        // Step 3: Check that business is no longer available
        mockMvc.perform(get("/api/v1/corporate/check-availability")
                .param("businessEmail", "contact@acmecorp.com")
                .param("businessRegistrationNumber", "BRN-123456789"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));

        // Step 4: Submit application for review
        mockMvc.perform(post("/api/v1/corporate/onboarding/applications/{referenceId}/submit", 
                        applicationReferenceId))
                .andExpect(status().isOk());

        // Step 5: Verify application is now SUBMITTED
        mockMvc.perform(get("/api/v1/corporate/onboarding/applications/{referenceId}", 
                        applicationReferenceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.applicationStatus").value("SUBMITTED"))
                .andExpect(jsonPath("$.submittedAt").exists());

        // Step 6: Initiate corporate KYC verification
        mockMvc.perform(post("/api/v1/corporate/onboarding/applications/{referenceId}/kyc/initiate", 
                        applicationReferenceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verificationId").exists())
                .andExpect(jsonPath("$.status").value("INITIATED"));
    }

    @Test
    @Order(2)
    @DisplayName("Should validate corporate registration data correctly")
    void testCorporateRegistrationValidation() throws Exception {
        // Test invalid business email format
        CreateCorporateOnboardingApplicationRequest invalidEmailRequest = new CreateCorporateOnboardingApplicationRequest(
            "Invalid Corp",
            BusinessType.LLC,
            IndustrySector.RETAIL,
            "invalid-email",  // Invalid email
            "+1-555-0123",
            "BRN-123456789",
            "TAX-987654321",
            "123 Business Avenue",
            "456 Billing Street",
            "John",
            "Smith",
            "john.smith@invalid.com",
            "+1-555-0124",
            "CEO",
            1000,
            "Retail business",
            "https://www.invalid.com",
            50,
            1000000.0,
            CommunicationMethod.EMAIL,
            null,
            false,
            true,
            true,
            true
        );

        mockMvc.perform(post("/api/v1/corporate/onboarding/applications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidEmailRequest)))
                .andExpect(status().isBadRequest());

        // Test terms not accepted
        CreateCorporateOnboardingApplicationRequest termsNotAcceptedRequest = new CreateCorporateOnboardingApplicationRequest(
            "Terms Not Accepted Corp",
            BusinessType.CORPORATION,
            IndustrySector.MANUFACTURING,
            "contact@termsnotaccepted.com",
            "+1-555-0123",
            "BRN-987654321",
            "TAX-123456789",
            "789 Terms Street",
            "789 Terms Street",
            "Jane",
            "Doe",
            "jane.doe@termsnotaccepted.com",
            "+1-555-0125",
            "President",
            2000,
            "Manufacturing business",
            "https://www.termsnotaccepted.com",
            100,
            5000000.0,
            CommunicationMethod.EMAIL,
            null,
            false,
            false,  // Terms not accepted
            false,  // Privacy policy not accepted
            false   // Data processing not accepted
        );

        mockMvc.perform(post("/api/v1/corporate/onboarding/applications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(termsNotAcceptedRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(3)
    @DisplayName("Should handle duplicate corporate registration attempts")
    void testDuplicateCorporateRegistration() throws Exception {
        CreateCorporateOnboardingApplicationRequest request = new CreateCorporateOnboardingApplicationRequest(
            "Duplicate Corp Ltd.",
            BusinessType.CORPORATION,
            IndustrySector.FINANCE,
            "duplicate@business.com",
            "+1-555-0200",
            "BRN-DUPLICATE-123",
            "TAX-DUPLICATE-456",
            "123 Duplicate Street",
            "123 Duplicate Street",
            "Robert",
            "Wilson",
            "robert.wilson@business.com",
            "+1-555-0201",
            "CFO",
            3000,
            "Financial services",
            "https://www.duplicatecorp.com",
            150,
            10000000.0,
            CommunicationMethod.EMAIL,
            "High security requirements",
            false,
            true,
            true,
            true
        );

        // First registration should succeed
        mockMvc.perform(post("/api/v1/corporate/onboarding/applications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Second registration with same business email should fail
        mockMvc.perform(post("/api/v1/corporate/onboarding/applications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @Order(4)
    @DisplayName("Should handle corporate application updates correctly")
    void testCorporateApplicationUpdate() throws Exception {
        // Create application first
        CreateCorporateOnboardingApplicationRequest createRequest = new CreateCorporateOnboardingApplicationRequest(
            "Update Test Corp",
            BusinessType.LLC,
            IndustrySector.HEALTHCARE,
            "update@testcorp.com",
            "+1-555-0300",
            "BRN-UPDATE-789",
            "TAX-UPDATE-012",
            "100 Update Avenue",
            "100 Update Avenue",
            "Sarah",
            "Johnson",
            "sarah.johnson@testcorp.com",
            "+1-555-0301",
            "COO",
            1500,
            "Healthcare technology",
            "https://www.updatetestcorp.com",
            75,
            3000000.0,
            CommunicationMethod.EMAIL,
            null,
            false,
            true,
            true,
            true
        );

        MvcResult createResult = mockMvc.perform(post("/api/v1/corporate/onboarding/applications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        CorporateOnboardingApplicationResponse application = objectMapper.readValue(
            createResult.getResponse().getContentAsString(), CorporateOnboardingApplicationResponse.class);

        // Update application (should work in DRAFT status)
        UpdateCorporateOnboardingApplicationRequest updateRequest = new UpdateCorporateOnboardingApplicationRequest(
            "Updated Test Corp Ltd.",
            "TAX-UPDATED-345",
            "BIZ-UPDATED-678",
            "updated@testcorp.com",
            "+1-555-0302",
            "https://www.updatedtestcorp.com",
            BusinessType.CORPORATION,  // Changed from LLC
            IndustrySector.TECHNOLOGY,  // Changed from HEALTHCARE
            CompanySize.LARGE,
            ShippingVolume.VERY_HIGH,
            "200 Updated Boulevard",
            "Suite 500",
            "Updated City",
            "Texas",
            "75001",
            "United States",
            "Sarah",
            "Updated",  // Updated last name
            "sarah.updated@testcorp.com",
            "+1-555-0303",
            "CEO",  // Updated title
            "Michael",
            "Finance",
            "michael.finance@testcorp.com",
            "+1-555-0304",
            10000000.0,
            PaymentTerms.NET_45,
            "Premium SLA requirements",
            CommunicationMethod.PHONE,  // Changed to PHONE
            true  // Updated marketing consent
        );

        mockMvc.perform(put("/api/v1/corporate/onboarding/applications/{referenceId}", 
                        application.applicationReferenceId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.businessName").value("Updated Test Corp Ltd."))
                .andExpect(jsonPath("$.businessType").value("CORPORATION"))
                .andExpect(jsonPath("$.primaryContactLastName").value("Updated"));

        // Submit application
        mockMvc.perform(post("/api/v1/corporate/onboarding/applications/{referenceId}/submit", 
                        application.applicationReferenceId()))
                .andExpect(status().isOk());

        // Try to update after submission (should fail)
        mockMvc.perform(put("/api/v1/corporate/onboarding/applications/{referenceId}", 
                        application.applicationReferenceId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(5)
    @DisplayName("Should handle application not found scenarios")
    void testApplicationNotFound() throws Exception {
        String nonExistentId = "CORP-ONB-20251201-9999";

        mockMvc.perform(get("/api/v1/corporate/onboarding/applications/{referenceId}", nonExistentId))
                .andExpected(status().isNotFound());

        mockMvc.perform(post("/api/v1/corporate/onboarding/applications/{referenceId}/submit", nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(6)
    @DisplayName("Should validate business registration correctly")
    void testBusinessRegistrationValidation() throws Exception {
        // Create an application first for business validation
        CreateCorporateOnboardingApplicationRequest createRequest = new CreateCorporateOnboardingApplicationRequest(
            "Validation Test Corp",
            BusinessType.CORPORATION,
            IndustrySector.LOGISTICS,
            "validation@testcorp.com",
            "+1-555-0400",
            "BRN-VALIDATION-123",
            "TAX-VALIDATION-456",
            "400 Validation Street",
            "400 Validation Street",
            "David",
            "Brown",
            "david.brown@testcorp.com",
            "+1-555-0401",
            "General Manager",
            2500,
            "Logistics and transportation",
            "https://www.validationtestcorp.com",
            200,
            8000000.0,
            CommunicationMethod.EMAIL,
            "Express delivery capabilities required",
            false,
            true,
            true,
            true
        );

        MvcResult createResult = mockMvc.perform(post("/api/v1/corporate/onboarding/applications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        CorporateOnboardingApplicationResponse application = objectMapper.readValue(
            createResult.getResponse().getContentAsString(), CorporateOnboardingApplicationResponse.class);

        // Test business validation endpoint
        BusinessValidationRequest validationRequest = new BusinessValidationRequest(
            "BRN-VALIDATION-123",
            "Validation Test Corp",
            "US",
            "California", 
            "CORPORATION",
            "TAX-VALIDATION-456"
        );

        mockMvc.perform(post("/api/v1/corporate/onboarding/applications/{referenceId}/validate-business", 
                        application.applicationReferenceId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("VALID"))
                .andExpect(jsonPath("$.isValid").value(true));
    }

    @Test
    @Order(7)
    @DisplayName("Should handle contract negotiation workflow")
    void testContractNegotiationWorkflow() throws Exception {
        // Create and submit application
        CreateCorporateOnboardingApplicationRequest createRequest = new CreateCorporateOnboardingApplicationRequest(
            "Contract Test Corp",
            BusinessType.CORPORATION,
            IndustrySector.RETAIL,
            "contract@testcorp.com",
            "+1-555-0500",
            "BRN-CONTRACT-123",
            "TAX-CONTRACT-456",
            "500 Contract Avenue",
            "500 Contract Avenue",
            "Linda",
            "Garcia",
            "linda.garcia@testcorp.com",
            "+1-555-0501",
            "VP Operations",
            8000,
            "Retail and e-commerce",
            "https://www.contracttestcorp.com",
            500,
            25000000.0,
            CommunicationMethod.EMAIL,
            "Multi-location delivery requirements",
            false,
            true,
            true,
            true
        );

        MvcResult createResult = mockMvc.perform(post("/api/v1/corporate/onboarding/applications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        CorporateOnboardingApplicationResponse application = objectMapper.readValue(
            createResult.getResponse().getContentAsString(), CorporateOnboardingApplicationResponse.class);

        // Submit application
        mockMvc.perform(post("/api/v1/corporate/onboarding/applications/{referenceId}/submit", 
                        application.applicationReferenceId()))
                .andExpect(status().isOk());

        // Initiate contract negotiation
        ContractNegotiationRequest contractRequest = new ContractNegotiationRequest(
            8000,
            "Multi-location delivery with real-time tracking",
            "NET_30",
            "MONTHLY",
            List.of("SAME_DAY", "NEXT_DAY", "STANDARD"),
            List.of("California", "Nevada", "Arizona"),
            12000,
            new java.math.BigDecimal("2000000.00"),
            new ServiceLevelRequirements(24, 99.5, 15, 2, "REAL_TIME", new java.math.BigDecimal("100000.00")),
            List.of("API_INTEGRATION", "EDI", "PORTAL_ACCESS"),
            "VOLUME_BASED",
            "Require dedicated account manager and premium support"
        );

        mockMvc.perform(post("/api/v1/corporate/onboarding/applications/{referenceId}/contract/negotiate", 
                        application.applicationReferenceId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(contractRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.negotiationId").exists())
                .andExpect(jsonPath("$.status").value("INITIATED"))
                .andExpect(jsonPath("$.volumeDiscount").exists());
    }
}