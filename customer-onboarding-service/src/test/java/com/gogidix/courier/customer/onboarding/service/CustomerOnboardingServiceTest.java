package com.gogidix.courier.customer.onboarding.service;

import com.gogidix.courier.customer.onboarding.dto.CreateCustomerOnboardingApplicationRequest;
import com.gogidix.courier.customer.onboarding.dto.CustomerOnboardingApplicationResponse;
import com.gogidix.courier.customer.onboarding.dto.UpdateCustomerOnboardingApplicationRequest;
import com.gogidix.courier.customer.onboarding.enums.CustomerOnboardingStatus;
import com.gogidix.courier.customer.onboarding.enums.CustomerType;
import com.gogidix.courier.customer.onboarding.exception.CustomerOnboardingException;
import com.gogidix.courier.customer.onboarding.model.CustomerOnboardingApplication;
import com.gogidix.courier.customer.onboarding.repository.CustomerApplicationStatusHistoryRepository;
import com.gogidix.courier.customer.onboarding.repository.CustomerOnboardingApplicationRepository;
import com.gogidix.courier.customer.onboarding.repository.CustomerProfileRepository;
import com.gogidix.courier.customer.onboarding.service.impl.CustomerOnboardingServiceImpl;
import com.gogidix.shared.exceptions.ResourceNotFoundException;
import com.gogidix.shared.messaging.MessageProducer;
import com.gogidix.shared.utilities.SecurityUtil;
import com.gogidix.shared.validation.ValidationResult;
import com.gogidix.shared.validation.ValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CustomerOnboardingService.
 * 
 * Tests business logic for customer onboarding operations including:
 * - Application creation and updates
 * - Status transitions and validations
 * - Email verification workflow
 * - Search and filtering operations
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Customer Onboarding Service Tests")
class CustomerOnboardingServiceTest {

    @Mock
    private CustomerOnboardingApplicationRepository applicationRepository;

    @Mock
    private CustomerApplicationStatusHistoryRepository statusHistoryRepository;

    @Mock
    private CustomerProfileRepository customerProfileRepository;

    @Mock
    private ValidationService validationService;

    @Mock
    private MessageProducer messageProducer;

    @InjectMocks
    private CustomerOnboardingServiceImpl customerOnboardingService;

    private CreateCustomerOnboardingApplicationRequest createRequest;
    private CustomerOnboardingApplication application;
    private UUID applicationId;
    private String applicationReferenceId;

    @BeforeEach
    void setUp() {
        // Set allowed email domains
        ReflectionTestUtils.setField(customerOnboardingService, "allowedEmailDomains", 
            "gmail.com,yahoo.com,outlook.com,company.com");

        applicationId = UUID.randomUUID();
        applicationReferenceId = "APP-2025-001234";

        // Create test request
        createRequest = new CreateCustomerOnboardingApplicationRequest(
            "John",
            "Doe",
            "john.doe@gmail.com",
            "+1234567890",
            CustomerType.INDIVIDUAL,
            true
        );

        // Create test application
        application = CustomerOnboardingApplication.builder()
            .id(applicationId)
            .applicationReferenceId(applicationReferenceId)
            .customerFirstName("John")
            .customerLastName("Doe")
            .customerEmail("john.doe@gmail.com")
            .customerPhoneNumber("+1234567890")
            .customerType(CustomerType.INDIVIDUAL)
            .applicationStatus(CustomerOnboardingStatus.DRAFT)
            .emailVerified(false)
            .acceptedTerms(true)
            .createdAt(LocalDateTime.now())
            .build();
    }

    @Test
    @DisplayName("Should create customer onboarding application successfully")
    void shouldCreateApplicationSuccessfully() {
        // Given
        when(applicationRepository.existsByCustomerEmailAndApplicationStatusNotIn(
            eq("john.doe@gmail.com"), anyList())).thenReturn(false);
        when(applicationRepository.save(any(CustomerOnboardingApplication.class)))
            .thenReturn(application);

        // When
        CustomerOnboardingApplicationResponse response = customerOnboardingService
            .createApplication(createRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.customerEmail()).isEqualTo("john.doe@gmail.com");
        assertThat(response.applicationStatus()).isEqualTo(CustomerOnboardingStatus.DRAFT);

        verify(applicationRepository).save(any(CustomerOnboardingApplication.class));
        verify(messageProducer).sendMessage(eq("customer.onboarding.created"), any());
    }

    @Test
    @DisplayName("Should reject duplicate application creation")
    void shouldRejectDuplicateApplication() {
        // Given
        when(applicationRepository.existsByCustomerEmailAndApplicationStatusNotIn(
            eq("john.doe@gmail.com"), anyList())).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> customerOnboardingService.createApplication(createRequest))
            .isInstanceOf(CustomerOnboardingException.class)
            .hasMessageContaining("active application already exists");

        verify(applicationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should reject invalid email domain")
    void shouldRejectInvalidEmailDomain() {
        // Given
        createRequest = new CreateCustomerOnboardingApplicationRequest(
            "John", "Doe", "john@invaliddomain.com", "+1234567890",
            CustomerType.INDIVIDUAL, true
        );

        // When/Then
        assertThatThrownBy(() -> customerOnboardingService.createApplication(createRequest))
            .isInstanceOf(CustomerOnboardingException.class)
            .hasMessageContaining("Email domain not allowed");
    }

    @Test
    @DisplayName("Should update application status successfully")
    void shouldUpdateApplicationStatusSuccessfully() {
        // Given
        when(applicationRepository.findByApplicationReferenceId(applicationReferenceId))
            .thenReturn(Optional.of(application));
        when(applicationRepository.save(any(CustomerOnboardingApplication.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        customerOnboardingService.updateApplicationStatus(applicationReferenceId, 
            CustomerOnboardingStatus.SUBMITTED);

        // Then
        ArgumentCaptor<CustomerOnboardingApplication> captor = 
            ArgumentCaptor.forClass(CustomerOnboardingApplication.class);
        verify(applicationRepository).save(captor.capture());
        
        assertThat(captor.getValue().getApplicationStatus())
            .isEqualTo(CustomerOnboardingStatus.SUBMITTED);
        verify(statusHistoryRepository).save(any());
    }

    @Test
    @DisplayName("Should reject invalid status transition")
    void shouldRejectInvalidStatusTransition() {
        // Given
        application.setApplicationStatus(CustomerOnboardingStatus.APPROVED);
        when(applicationRepository.findByApplicationReferenceId(applicationReferenceId))
            .thenReturn(Optional.of(application));

        // When/Then
        assertThatThrownBy(() -> customerOnboardingService.updateApplicationStatus(
            applicationReferenceId, CustomerOnboardingStatus.DRAFT))
            .isInstanceOf(CustomerOnboardingException.class)
            .hasMessageContaining("Invalid status transition");
    }

    @Test
    @DisplayName("Should verify email successfully")
    void shouldVerifyEmailSuccessfully() {
        // Given
        String verificationToken = UUID.randomUUID().toString();
        application.setEmailVerificationToken(verificationToken);
        application.setEmailVerificationTokenExpiry(LocalDateTime.now().plusMinutes(30));
        
        when(applicationRepository.findByEmailVerificationToken(verificationToken))
            .thenReturn(Optional.of(application));
        when(applicationRepository.save(any(CustomerOnboardingApplication.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        try (MockedStatic<SecurityUtil> mockedSecurity = mockStatic(SecurityUtil.class)) {
            mockedSecurity.when(SecurityUtil::getCurrentUsername).thenReturn("system");
            
            customerOnboardingService.verifyEmail(verificationToken);
        }

        // Then
        ArgumentCaptor<CustomerOnboardingApplication> captor = 
            ArgumentCaptor.forClass(CustomerOnboardingApplication.class);
        verify(applicationRepository).save(captor.capture());
        
        assertThat(captor.getValue().getEmailVerified()).isTrue();
        assertThat(captor.getValue().getEmailVerifiedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should reject expired email verification token")
    void shouldRejectExpiredVerificationToken() {
        // Given
        String verificationToken = UUID.randomUUID().toString();
        application.setEmailVerificationToken(verificationToken);
        application.setEmailVerificationTokenExpiry(LocalDateTime.now().minusMinutes(1));
        
        when(applicationRepository.findByEmailVerificationToken(verificationToken))
            .thenReturn(Optional.of(application));

        // When/Then
        assertThatThrownBy(() -> customerOnboardingService.verifyEmail(verificationToken))
            .isInstanceOf(CustomerOnboardingException.class)
            .hasMessageContaining("verification token has expired");
    }

    @Test
    @DisplayName("Should find applications by status")
    void shouldFindApplicationsByStatus() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<CustomerOnboardingApplication> applications = List.of(application);
        Page<CustomerOnboardingApplication> page = new PageImpl<>(applications, pageable, 1);
        
        when(applicationRepository.findByApplicationStatus(CustomerOnboardingStatus.DRAFT, pageable))
            .thenReturn(page);

        // When
        Page<CustomerOnboardingApplicationResponse> result = customerOnboardingService
            .findApplicationsByStatus(CustomerOnboardingStatus.DRAFT, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).applicationStatus())
            .isEqualTo(CustomerOnboardingStatus.DRAFT);
    }

    @Test
    @DisplayName("Should search applications with criteria")
    void shouldSearchApplicationsWithCriteria() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<CustomerOnboardingApplication> applications = List.of(application);
        Page<CustomerOnboardingApplication> page = new PageImpl<>(applications, pageable, 1);
        
        when(applicationRepository.searchApplications(
            eq("john"), eq(CustomerType.INDIVIDUAL), eq(CustomerOnboardingStatus.DRAFT), 
            anyBoolean(), any(), any(), eq(pageable)))
            .thenReturn(page);

        // When
        Page<CustomerOnboardingApplicationResponse> result = customerOnboardingService
            .searchApplications("john", CustomerType.INDIVIDUAL, CustomerOnboardingStatus.DRAFT, 
                null, null, null, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(applicationRepository).searchApplications(any(), any(), any(), anyBoolean(), 
            any(), any(), any());
    }

    @Test
    @DisplayName("Should approve application successfully")
    void shouldApproveApplicationSuccessfully() {
        // Given
        application.setApplicationStatus(CustomerOnboardingStatus.KYC_COMPLETED);
        when(applicationRepository.findByApplicationReferenceId(applicationReferenceId))
            .thenReturn(Optional.of(application));
        when(applicationRepository.save(any(CustomerOnboardingApplication.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        try (MockedStatic<SecurityUtil> mockedSecurity = mockStatic(SecurityUtil.class)) {
            mockedSecurity.when(SecurityUtil::getCurrentUsername).thenReturn("admin");
            
            customerOnboardingService.approveApplication(applicationReferenceId, "All checks passed");
        }

        // Then
        ArgumentCaptor<CustomerOnboardingApplication> captor = 
            ArgumentCaptor.forClass(CustomerOnboardingApplication.class);
        verify(applicationRepository).save(captor.capture());
        
        assertThat(captor.getValue().getApplicationStatus())
            .isEqualTo(CustomerOnboardingStatus.APPROVED);
        assertThat(captor.getValue().getApprovedBy()).isEqualTo("admin");
        verify(messageProducer).sendMessage(eq("customer.onboarding.approved"), any());
    }

    @Test
    @DisplayName("Should reject application successfully")
    void shouldRejectApplicationSuccessfully() {
        // Given
        application.setApplicationStatus(CustomerOnboardingStatus.KYC_IN_PROGRESS);
        when(applicationRepository.findByApplicationReferenceId(applicationReferenceId))
            .thenReturn(Optional.of(application));
        when(applicationRepository.save(any(CustomerOnboardingApplication.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        try (MockedStatic<SecurityUtil> mockedSecurity = mockStatic(SecurityUtil.class)) {
            mockedSecurity.when(SecurityUtil::getCurrentUsername).thenReturn("admin");
            
            customerOnboardingService.rejectApplication(applicationReferenceId, "Failed KYC check");
        }

        // Then
        ArgumentCaptor<CustomerOnboardingApplication> captor = 
            ArgumentCaptor.forClass(CustomerOnboardingApplication.class);
        verify(applicationRepository).save(captor.capture());
        
        assertThat(captor.getValue().getApplicationStatus())
            .isEqualTo(CustomerOnboardingStatus.REJECTED);
        assertThat(captor.getValue().getRejectionReason()).isEqualTo("Failed KYC check");
        verify(messageProducer).sendMessage(eq("customer.onboarding.rejected"), any());
    }

    @Test
    @DisplayName("Should throw exception when application not found")
    void shouldThrowExceptionWhenApplicationNotFound() {
        // Given
        when(applicationRepository.findByApplicationReferenceId(applicationReferenceId))
            .thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> customerOnboardingService.getApplicationByReferenceId(applicationReferenceId))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Customer onboarding application not found");
    }

    @Test
    @DisplayName("Should calculate completion percentage correctly")
    void shouldCalculateCompletionPercentage() {
        // Given
        application.setEmailVerified(true);
        application.setKycStatus("COMPLETED");
        application.setPhoneVerified(true);
        
        when(applicationRepository.findByApplicationReferenceId(applicationReferenceId))
            .thenReturn(Optional.of(application));

        // When
        CustomerOnboardingApplicationResponse response = customerOnboardingService
            .getApplicationByReferenceId(applicationReferenceId);

        // Then
        assertThat(response.completionPercentage()).isGreaterThan(0);
    }
}