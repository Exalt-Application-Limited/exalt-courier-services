package com.gogidix.courier.customer.onboarding.controller;

import com.gogidix.courier.customer.onboarding.dto.CreateCustomerOnboardingApplicationRequest;
import com.gogidix.courier.customer.onboarding.dto.CustomerOnboardingApplicationResponse;
import com.gogidix.courier.customer.onboarding.enums.CustomerOnboardingStatus;
import com.gogidix.courier.customer.onboarding.enums.CustomerType;
import com.gogidix.courier.customer.onboarding.service.CustomerOnboardingService;
import com.gogidix.shared.exceptions.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller tests for CustomerOnboardingController.
 * 
 * Tests REST API endpoints including:
 * - Request/response serialization
 * - Input validation
 * - Security and authorization
 * - Error handling
 * - HTTP status codes
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@WebMvcTest(CustomerOnboardingController.class)
@ActiveProfiles("test")
@DisplayName("Customer Onboarding Controller Tests")
class CustomerOnboardingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerOnboardingService customerOnboardingService;

    private CreateCustomerOnboardingApplicationRequest validRequest;
    private CustomerOnboardingApplicationResponse mockResponse;
    private String applicationReferenceId;

    @BeforeEach
    void setUp() {
        applicationReferenceId = "APP-2025-001234";

        validRequest = new CreateCustomerOnboardingApplicationRequest(
            "John",
            "Doe",
            "john.doe@gmail.com",
            "+1234567890",
            CustomerType.INDIVIDUAL,
            true
        );

        mockResponse = new CustomerOnboardingApplicationResponse(
            UUID.randomUUID(),
            applicationReferenceId,
            "John",
            "Doe",
            "john.doe@gmail.com",
            "+1234567890",
            CustomerType.INDIVIDUAL,
            CustomerOnboardingStatus.DRAFT,
            false,
            null,
            null,
            true,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            LocalDateTime.now(),
            LocalDateTime.now(),
            "system",
            "system",
            1L,
            50
        );
    }

    @Test
    @DisplayName("Should register customer successfully")
    void shouldRegisterCustomerSuccessfully() throws Exception {
        // Given
        when(customerOnboardingService.createApplication(any(CreateCustomerOnboardingApplicationRequest.class)))
            .thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(post("/api/customer/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest))
                .with(csrf()))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.applicationReferenceId").value(applicationReferenceId))
            .andExpect(jsonPath("$.customerEmail").value("john.doe@gmail.com"))
            .andExpect(jsonPath("$.applicationStatus").value("DRAFT"));

        verify(customerOnboardingService).createApplication(any(CreateCustomerOnboardingApplicationRequest.class));
    }

    @Test
    @DisplayName("Should validate required fields on registration")
    void shouldValidateRequiredFieldsOnRegistration() throws Exception {
        // Given - Invalid request with null values
        CreateCustomerOnboardingApplicationRequest invalidRequest = new CreateCustomerOnboardingApplicationRequest(
            null, // Missing first name
            "Doe",
            "invalid-email", // Invalid email
            "+123", // Invalid phone
            CustomerType.INDIVIDUAL,
            false // Terms not accepted
        );

        // When & Then
        mockMvc.perform(post("/api/customer/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))
                .with(csrf()))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should verify email successfully")
    void shouldVerifyEmailSuccessfully() throws Exception {
        // Given
        String token = UUID.randomUUID().toString();
        doNothing().when(customerOnboardingService).verifyEmail(token);

        // When & Then
        mockMvc.perform(get("/api/customer/verify-email")
                .param("token", token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Email verified successfully"));

        verify(customerOnboardingService).verifyEmail(token);
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    @DisplayName("Should get application by reference ID")
    void shouldGetApplicationByReferenceId() throws Exception {
        // Given
        when(customerOnboardingService.getApplicationByReferenceId(applicationReferenceId))
            .thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(get("/api/customer/application/{referenceId}", applicationReferenceId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.applicationReferenceId").value(applicationReferenceId))
            .andExpect(jsonPath("$.customerEmail").value("john.doe@gmail.com"));

        verify(customerOnboardingService).getApplicationByReferenceId(applicationReferenceId);
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    @DisplayName("Should handle not found exception")
    void shouldHandleNotFoundException() throws Exception {
        // Given
        when(customerOnboardingService.getApplicationByReferenceId(applicationReferenceId))
            .thenThrow(new ResourceNotFoundException("Application not found"));

        // When & Then
        mockMvc.perform(get("/api/customer/application/{referenceId}", applicationReferenceId))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    @DisplayName("Should submit application for review")
    void shouldSubmitApplicationForReview() throws Exception {
        // Given
        doNothing().when(customerOnboardingService)
            .updateApplicationStatus(applicationReferenceId, CustomerOnboardingStatus.SUBMITTED);

        // When & Then
        mockMvc.perform(post("/api/customer/application/{referenceId}/submit", applicationReferenceId)
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Application submitted for review"));

        verify(customerOnboardingService).updateApplicationStatus(
            applicationReferenceId, CustomerOnboardingStatus.SUBMITTED);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should search applications as admin")
    void shouldSearchApplicationsAsAdmin() throws Exception {
        // Given
        Page<CustomerOnboardingApplicationResponse> mockPage = new PageImpl<>(
            List.of(mockResponse), PageRequest.of(0, 10), 1);
        
        when(customerOnboardingService.searchApplications(
            eq("john"), eq(CustomerType.INDIVIDUAL), eq(CustomerOnboardingStatus.DRAFT),
            any(), any(), any(), any()))
            .thenReturn(mockPage);

        // When & Then
        mockMvc.perform(get("/api/customer/admin/applications/search")
                .param("searchTerm", "john")
                .param("customerType", "INDIVIDUAL")
                .param("status", "DRAFT"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)))
            .andExpect(jsonPath("$.content[0].customerEmail").value("john.doe@gmail.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should approve application as admin")
    void shouldApproveApplicationAsAdmin() throws Exception {
        // Given
        Map<String, String> approvalRequest = Map.of("approvalNotes", "All checks passed");
        doNothing().when(customerOnboardingService)
            .approveApplication(applicationReferenceId, "All checks passed");

        // When & Then
        mockMvc.perform(post("/api/customer/admin/applications/{referenceId}/approve", applicationReferenceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(approvalRequest))
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Application approved successfully"));

        verify(customerOnboardingService).approveApplication(applicationReferenceId, "All checks passed");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should reject application as admin")
    void shouldRejectApplicationAsAdmin() throws Exception {
        // Given
        Map<String, String> rejectionRequest = Map.of("rejectionReason", "Failed verification");
        doNothing().when(customerOnboardingService)
            .rejectApplication(applicationReferenceId, "Failed verification");

        // When & Then
        mockMvc.perform(post("/api/customer/admin/applications/{referenceId}/reject", applicationReferenceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rejectionRequest))
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Application rejected"));

        verify(customerOnboardingService).rejectApplication(applicationReferenceId, "Failed verification");
    }

    @Test
    @DisplayName("Should reject unauthorized access to customer endpoints")
    void shouldRejectUnauthorizedAccessToCustomerEndpoints() throws Exception {
        // When & Then - No authentication
        mockMvc.perform(get("/api/customer/application/{referenceId}", applicationReferenceId))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    @DisplayName("Should reject customer access to admin endpoints")
    void shouldRejectCustomerAccessToAdminEndpoints() throws Exception {
        // When & Then - Customer trying to access admin endpoint
        mockMvc.perform(get("/api/customer/admin/applications"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get application statistics as admin")
    void shouldGetApplicationStatisticsAsAdmin() throws Exception {
        // Given
        Map<String, Object> mockStats = Map.of(
            "total", 100,
            "pending", 20,
            "approved", 70,
            "rejected", 10
        );
        when(customerOnboardingService.getApplicationStatistics()).thenReturn(mockStats);

        // When & Then
        mockMvc.perform(get("/api/customer/admin/statistics"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.total").value(100))
            .andExpect(jsonPath("$.pending").value(20));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    @DisplayName("Should resend verification email")
    void shouldResendVerificationEmail() throws Exception {
        // Given
        doNothing().when(customerOnboardingService).resendVerificationEmail("john.doe@gmail.com");

        // When & Then
        mockMvc.perform(post("/api/customer/resend-verification")
                .param("email", "john.doe@gmail.com")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Verification email sent"));

        verify(customerOnboardingService).resendVerificationEmail("john.doe@gmail.com");
    }

    @Test
    @WithMockUser(roles = "SUPPORT_AGENT")
    @DisplayName("Should allow support agent to access admin endpoints")
    void shouldAllowSupportAgentToAccessAdminEndpoints() throws Exception {
        // Given
        Page<CustomerOnboardingApplicationResponse> mockPage = new PageImpl<>(
            List.of(mockResponse), PageRequest.of(0, 10), 1);
        
        when(customerOnboardingService.findApplicationsByStatus(
            eq(CustomerOnboardingStatus.DRAFT), any()))
            .thenReturn(mockPage);

        // When & Then
        mockMvc.perform(get("/api/customer/admin/applications")
                .param("status", "DRAFT"))
            .andExpect(status().isOk());
    }
}