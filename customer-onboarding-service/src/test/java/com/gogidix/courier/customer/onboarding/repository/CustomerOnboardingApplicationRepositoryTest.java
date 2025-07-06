package com.gogidix.courier.customer.onboarding.repository;

import com.gogidix.courier.customer.onboarding.enums.CustomerOnboardingStatus;
import com.gogidix.courier.customer.onboarding.enums.CustomerType;
import com.gogidix.courier.customer.onboarding.model.CustomerOnboardingApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository tests for CustomerOnboardingApplicationRepository.
 * 
 * Tests custom query methods and data access operations including:
 * - Finding applications by various criteria
 * - Complex search queries
 * - Date range filtering
 * - Status-based queries
 * - Aggregation queries
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Customer Onboarding Application Repository Tests")
class CustomerOnboardingApplicationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CustomerOnboardingApplicationRepository repository;

    private CustomerOnboardingApplication application1;
    private CustomerOnboardingApplication application2;
    private CustomerOnboardingApplication application3;

    @BeforeEach
    void setUp() {
        // Create test applications
        application1 = createApplication("john.doe@gmail.com", "John", "Doe", 
            CustomerOnboardingStatus.DRAFT, CustomerType.INDIVIDUAL);
        application1.setEmailVerified(false);
        entityManager.persist(application1);

        application2 = createApplication("jane.smith@yahoo.com", "Jane", "Smith", 
            CustomerOnboardingStatus.SUBMITTED, CustomerType.INDIVIDUAL);
        application2.setEmailVerified(true);
        entityManager.persist(application2);

        application3 = createApplication("business@company.com", "Business", "Corp", 
            CustomerOnboardingStatus.APPROVED, CustomerType.BUSINESS);
        application3.setEmailVerified(true);
        application3.setApprovedAt(LocalDateTime.now());
        application3.setApprovedBy("admin");
        entityManager.persist(application3);

        entityManager.flush();
    }

    @Test
    @DisplayName("Should find application by reference ID")
    void shouldFindByApplicationReferenceId() {
        // When
        Optional<CustomerOnboardingApplication> found = repository
            .findByApplicationReferenceId(application1.getApplicationReferenceId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getCustomerEmail()).isEqualTo("john.doe@gmail.com");
    }

    @Test
    @DisplayName("Should find application by customer email")
    void shouldFindByCustomerEmail() {
        // When
        Optional<CustomerOnboardingApplication> found = repository
            .findByCustomerEmail("jane.smith@yahoo.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getCustomerFirstName()).isEqualTo("Jane");
    }

    @Test
    @DisplayName("Should check existence by email and status")
    void shouldCheckExistenceByEmailAndStatus() {
        // When
        boolean exists = repository.existsByCustomerEmailAndApplicationStatusNotIn(
            "john.doe@gmail.com", 
            List.of(CustomerOnboardingStatus.CANCELLED, CustomerOnboardingStatus.REJECTED)
        );

        // Then
        assertThat(exists).isTrue();

        // Check non-existent
        boolean notExists = repository.existsByCustomerEmailAndApplicationStatusNotIn(
            "nonexistent@gmail.com",
            List.of(CustomerOnboardingStatus.CANCELLED)
        );
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Should find applications by status")
    void shouldFindByApplicationStatus() {
        // When
        Page<CustomerOnboardingApplication> draftApps = repository
            .findByApplicationStatus(CustomerOnboardingStatus.DRAFT, PageRequest.of(0, 10));

        // Then
        assertThat(draftApps.getContent()).hasSize(1);
        assertThat(draftApps.getContent().get(0).getCustomerEmail())
            .isEqualTo("john.doe@gmail.com");
    }

    @Test
    @DisplayName("Should find applications by customer type")
    void shouldFindByCustomerType() {
        // When
        Page<CustomerOnboardingApplication> businessApps = repository
            .findByCustomerType(CustomerType.BUSINESS, PageRequest.of(0, 10));

        // Then
        assertThat(businessApps.getContent()).hasSize(1);
        assertThat(businessApps.getContent().get(0).getCustomerEmail())
            .isEqualTo("business@company.com");
    }

    @Test
    @DisplayName("Should find unverified email applications")
    void shouldFindUnverifiedEmailApplications() {
        // When
        LocalDateTime cutoffTime = LocalDateTime.now().plusDays(1);
        Page<CustomerOnboardingApplication> unverified = repository
            .findByEmailVerifiedFalseAndCreatedAtBefore(cutoffTime, PageRequest.of(0, 10));

        // Then
        assertThat(unverified.getContent()).hasSize(1);
        assertThat(unverified.getContent().get(0).getCustomerEmail())
            .isEqualTo("john.doe@gmail.com");
    }

    @Test
    @DisplayName("Should find applications pending review")
    void shouldFindApplicationsPendingReview() {
        // When
        List<CustomerOnboardingApplication> pending = repository
            .findByApplicationStatusInOrderByCreatedAtAsc(
                List.of(CustomerOnboardingStatus.SUBMITTED, CustomerOnboardingStatus.KYC_IN_PROGRESS)
            );

        // Then
        assertThat(pending).hasSize(1);
        assertThat(pending.get(0).getCustomerEmail()).isEqualTo("jane.smith@yahoo.com");
    }

    @Test
    @DisplayName("Should search applications with multiple criteria")
    void shouldSearchApplicationsWithCriteria() {
        // When
        Page<CustomerOnboardingApplication> results = repository.searchApplications(
            "jane",
            CustomerType.INDIVIDUAL,
            CustomerOnboardingStatus.SUBMITTED,
            true,
            null,
            null,
            PageRequest.of(0, 10)
        );

        // Then
        assertThat(results.getContent()).hasSize(1);
        assertThat(results.getContent().get(0).getCustomerEmail())
            .isEqualTo("jane.smith@yahoo.com");
    }

    @Test
    @DisplayName("Should search with partial name match")
    void shouldSearchWithPartialNameMatch() {
        // When
        Page<CustomerOnboardingApplication> results = repository.searchApplications(
            "doe", // Should match "John Doe"
            null,
            null,
            null,
            null,
            null,
            PageRequest.of(0, 10)
        );

        // Then
        assertThat(results.getContent()).hasSize(1);
        assertThat(results.getContent().get(0).getCustomerFirstName()).isEqualTo("John");
    }

    @Test
    @DisplayName("Should find applications created between dates")
    void shouldFindApplicationsCreatedBetweenDates() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);

        // When
        Page<CustomerOnboardingApplication> results = repository.searchApplications(
            null,
            null,
            null,
            null,
            startDate,
            endDate,
            PageRequest.of(0, 10)
        );

        // Then
        assertThat(results.getContent()).hasSize(3);
    }

    @Test
    @DisplayName("Should count applications by status")
    void shouldCountByApplicationStatus() {
        // When
        long draftCount = repository.countByApplicationStatus(CustomerOnboardingStatus.DRAFT);
        long submittedCount = repository.countByApplicationStatus(CustomerOnboardingStatus.SUBMITTED);
        long approvedCount = repository.countByApplicationStatus(CustomerOnboardingStatus.APPROVED);

        // Then
        assertThat(draftCount).isEqualTo(1);
        assertThat(submittedCount).isEqualTo(1);
        assertThat(approvedCount).isEqualTo(1);
    }

    @Test
    @DisplayName("Should find by email verification token")
    void shouldFindByEmailVerificationToken() {
        // Given
        String token = UUID.randomUUID().toString();
        application1.setEmailVerificationToken(token);
        entityManager.persist(application1);
        entityManager.flush();

        // When
        Optional<CustomerOnboardingApplication> found = repository
            .findByEmailVerificationToken(token);

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getCustomerEmail()).isEqualTo("john.doe@gmail.com");
    }

    @Test
    @DisplayName("Should find applications expiring soon")
    void shouldFindApplicationsExpiringSoon() {
        // Given
        LocalDate expiryDate = LocalDate.now().plusDays(7);

        // When
        List<CustomerOnboardingApplication> expiring = repository
            .findByApplicationStatusAndKycExpiryDateBefore(
                CustomerOnboardingStatus.APPROVED, expiryDate
            );

        // Then
        // Should be empty as we haven't set KYC expiry dates
        assertThat(expiring).isEmpty();
    }

    @Test
    @DisplayName("Should get application statistics by date range")
    void shouldGetApplicationStatisticsByDateRange() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);

        // When
        List<Object[]> stats = repository.getApplicationStatisticsByDateRange(startDate, endDate);

        // Then
        assertThat(stats).isNotEmpty();
        // Stats should contain status counts
    }

    @Test
    @DisplayName("Should find overdue applications")
    void shouldFindOverdueApplications() {
        // Given
        application2.setCreatedAt(LocalDateTime.now().minusDays(10));
        entityManager.persist(application2);
        entityManager.flush();

        // When
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(5);
        List<CustomerOnboardingApplication> overdue = repository
            .findOverdueApplications(
                List.of(CustomerOnboardingStatus.SUBMITTED),
                cutoffDate
            );

        // Then
        assertThat(overdue).hasSize(1);
        assertThat(overdue.get(0).getCustomerEmail()).isEqualTo("jane.smith@yahoo.com");
    }

    // Helper method
    private CustomerOnboardingApplication createApplication(String email, String firstName, 
                                                           String lastName, 
                                                           CustomerOnboardingStatus status,
                                                           CustomerType type) {
        return CustomerOnboardingApplication.builder()
            .applicationReferenceId("APP-" + UUID.randomUUID().toString().substring(0, 8))
            .customerEmail(email)
            .customerFirstName(firstName)
            .customerLastName(lastName)
            .customerPhoneNumber("+1234567890")
            .customerType(type)
            .applicationStatus(status)
            .acceptedTerms(true)
            .createdAt(LocalDateTime.now())
            .build();
    }
}