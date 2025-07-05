package com.gogidix.courier.customer.onboarding.integration;

import com.gogidix.courier.customer.onboarding.dto.*;
import com.gogidix.courier.customer.onboarding.model.CustomerOnboardingStatus;
import com.gogidix.courier.customer.onboarding.model.CustomerOnboardingApplication;
import com.gogidix.courier.customer.onboarding.repository.CustomerOnboardingApplicationRepository;
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
 * Integration tests for Customer Onboarding Service.
 * 
 * Tests the complete onboarding workflow from registration to account activation.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Customer Onboarding Integration Tests")
@Transactional
class CustomerOnboardingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerOnboardingApplicationRepository applicationRepository;

    private static String applicationReferenceId;

    @BeforeEach
    void setUp() {
        // Clear test data
        applicationRepository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("Should complete customer registration workflow successfully")
    void testCompleteCustomerOnboardingWorkflow() throws Exception {
        // Step 1: Check email availability
        mockMvc.perform(get("/api/v1/customer/check-availability")
                .param("email", "john.doe@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        // Step 2: Register new customer
        CreateCustomerOnboardingApplicationRequest registerRequest = new CreateCustomerOnboardingApplicationRequest(
            "john.doe@example.com",
            "+1234567890",
            "John",
            "Doe",
            "1990-01-15",
            "123456789",
            "123 Main Street",
            "Apt 4B",
            "New York",
            "NY",
            "10001",
            "United States",
            "EMAIL",
            false,
            true,
            true
        );

        MvcResult registerResult = mockMvc.perform(post("/api/v1/customer/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerEmail").value("john.doe@example.com"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.applicationStatus").value("DRAFT"))
                .andExpect(jsonPath("$.applicationReferenceId").exists())
                .andReturn();

        String responseJson = registerResult.getResponse().getContentAsString();
        CustomerOnboardingApplicationResponse application = objectMapper.readValue(
            responseJson, CustomerOnboardingApplicationResponse.class);

        applicationReferenceId = application.applicationReferenceId();
        assertNotNull(applicationReferenceId);
        assertTrue(applicationReferenceId.startsWith("CUST-ONB"));

        // Step 3: Check that email is no longer available
        mockMvc.perform(get("/api/v1/customer/check-availability")
                .param("email", "john.doe@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));

        // Step 4: Submit application for review
        mockMvc.perform(post("/api/v1/customer/onboarding/applications/{referenceId}/submit", 
                        applicationReferenceId))
                .andExpect(status().isOk());

        // Step 5: Verify application is now SUBMITTED
        mockMvc.perform(get("/api/v1/customer/onboarding/applications/{referenceId}", 
                        applicationReferenceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.applicationStatus").value("SUBMITTED"))
                .andExpect(jsonPath("$.submittedAt").exists());
    }

    @Test
    @Order(2)
    @DisplayName("Should validate registration data correctly")
    void testCustomerRegistrationValidation() throws Exception {
        // Test invalid email format
        CreateCustomerOnboardingApplicationRequest invalidEmailRequest = new CreateCustomerOnboardingApplicationRequest(
            "invalid-email",  // Invalid email
            "+1234567890",
            "John",
            "Doe",
            "1990-01-15",
            "123456789",
            "123 Main Street",
            null,
            "New York",
            "NY",
            "10001",
            "United States",
            "EMAIL",
            false,
            true,
            true
        );

        mockMvc.perform(post("/api/v1/customer/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidEmailRequest)))
                .andExpect(status().isBadRequest());

        // Test terms not accepted
        CreateCustomerOnboardingApplicationRequest termsNotAcceptedRequest = new CreateCustomerOnboardingApplicationRequest(
            "john.doe@example.com",
            "+1234567890",
            "John",
            "Doe",
            "1990-01-15",
            "123456789",
            "123 Main Street",
            null,
            "New York",
            "NY",
            "10001",
            "United States",
            "EMAIL",
            false,
            false,  // Terms not accepted
            true
        );

        mockMvc.perform(post("/api/v1/customer/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(termsNotAcceptedRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(3)
    @DisplayName("Should handle duplicate registration attempts")
    void testDuplicateRegistration() throws Exception {
        CreateCustomerOnboardingApplicationRequest request = new CreateCustomerOnboardingApplicationRequest(
            "duplicate@example.com",
            "+1555666777",
            "Duplicate",
            "User",
            "1988-12-01",
            "555666777",
            "123 Duplicate Street",
            null,
            "Duplicate City",
            "DC",
            "20001",
            "United States",
            "EMAIL",
            false,
            true,
            true
        );

        // First registration should succeed
        mockMvc.perform(post("/api/v1/customer/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Second registration with same email should fail
        mockMvc.perform(post("/api/v1/customer/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @Order(4)
    @DisplayName("Should handle application updates correctly")
    void testApplicationUpdate() throws Exception {
        // Create application first
        CreateCustomerOnboardingApplicationRequest createRequest = new CreateCustomerOnboardingApplicationRequest(
            "update.test@example.com",
            "+1111222333",
            "Update",
            "Test",
            "1992-03-10",
            "111222333",
            "100 Update Street",
            null,
            "Update City",
            "UC",
            "12345",
            "United States",
            "EMAIL",
            false,
            true,
            true
        );

        MvcResult createResult = mockMvc.perform(post("/api/v1/customer/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        CustomerOnboardingApplicationResponse application = objectMapper.readValue(
            createResult.getResponse().getContentAsString(), CustomerOnboardingApplicationResponse.class);

        // Update application (should work in DRAFT status)
        UpdateCustomerOnboardingApplicationRequest updateRequest = new UpdateCustomerOnboardingApplicationRequest(
            "update.test@example.com",
            "+1111222333",
            "Update",
            "Updated",  // Updated last name
            "1992-03-10",
            "111222333",
            "100 Update Street",
            null,
            "Update City",
            "UC",
            "12345",
            "United States",
            "EMAIL",
            true  // Updated marketing consent
        );

        mockMvc.perform(put("/api/v1/customer/onboarding/applications/{referenceId}", 
                        application.applicationReferenceId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Updated"))
                .andExpect(jsonPath("$.marketingConsent").value(true));

        // Submit application
        mockMvc.perform(post("/api/v1/customer/onboarding/applications/{referenceId}/submit", 
                        application.applicationReferenceId()))
                .andExpect(status().isOk());

        // Try to update after submission (should fail)
        mockMvc.perform(put("/api/v1/customer/onboarding/applications/{referenceId}", 
                        application.applicationReferenceId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(5)
    @DisplayName("Should handle application not found scenarios")
    void testApplicationNotFound() throws Exception {
        String nonExistentId = "CUST-ONB-20251201-9999";

        mockMvc.perform(get("/api/v1/customer/onboarding/applications/{referenceId}", nonExistentId))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/api/v1/customer/onboarding/applications/{referenceId}/submit", nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(6)
    @DisplayName("Should validate email availability correctly")
    void testEmailAvailabilityValidation() throws Exception {
        // Test invalid email format
        mockMvc.perform(get("/api/v1/customer/check-availability")
                .param("email", "invalid-email"))
                .andExpect(status().isBadRequest());

        // Test valid email format
        mockMvc.perform(get("/api/v1/customer/check-availability")
                .param("email", "available@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}