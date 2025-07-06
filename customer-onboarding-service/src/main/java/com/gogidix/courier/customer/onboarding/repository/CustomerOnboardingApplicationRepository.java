package com.gogidix.courier.customer.onboarding.repository;

import com.gogidix.courier.customer.onboarding.model.CustomerOnboardingApplication;
import com.gogidix.courier.customer.onboarding.model.CustomerOnboardingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for CustomerOnboardingApplication entity.
 * 
 * Provides data access operations for customer onboarding applications
 * using Spring Data JPA with UUID-based primary keys.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Repository
public interface CustomerOnboardingApplicationRepository extends JpaRepository<CustomerOnboardingApplication, UUID> {

    /**
     * Finds an application by its unique reference ID.
     * 
     * @param applicationReferenceId The unique reference ID
     * @return Optional containing the application if found
     */
    Optional<CustomerOnboardingApplication> findByApplicationReferenceId(String applicationReferenceId);

    /**
     * Finds an application by customer email.
     * 
     * @param customerEmail The customer's email address
     * @return Optional containing the application if found
     */
    Optional<CustomerOnboardingApplication> findByCustomerEmail(String customerEmail);

    /**
     * Checks if an application exists for the given email.
     * 
     * @param customerEmail The customer's email address
     * @return true if application exists, false otherwise
     */
    boolean existsByCustomerEmail(String customerEmail);

    /**
     * Checks if an application exists for the given phone number.
     * 
     * @param customerPhone The customer's phone number
     * @return true if application exists, false otherwise
     */
    boolean existsByCustomerPhone(String customerPhone);

    /**
     * Finds applications by current status.
     * 
     * @param status The application status to filter by
     * @param pageable Pagination information
     * @return Page of applications with the specified status
     */
    Page<CustomerOnboardingApplication> findByApplicationStatus(CustomerOnboardingStatus status, Pageable pageable);

    /**
     * Finds applications by auth service user ID.
     * 
     * @param authServiceUserId The auth service user ID
     * @return Optional containing the application if found
     */
    Optional<CustomerOnboardingApplication> findByAuthServiceUserId(String authServiceUserId);

    /**
     * Finds applications by KYC verification ID.
     * 
     * @param kycVerificationId The KYC verification ID
     * @return Optional containing the application if found
     */
    Optional<CustomerOnboardingApplication> findByKycVerificationId(String kycVerificationId);

    /**
     * Finds applications created within a date range.
     * 
     * @param startDate The start date
     * @param endDate The end date
     * @param pageable Pagination information
     * @return Page of applications created within the date range
     */
    Page<CustomerOnboardingApplication> findByCreatedAtBetween(
            LocalDateTime startDate, 
            LocalDateTime endDate, 
            Pageable pageable);

    /**
     * Finds applications by status and created date range.
     * 
     * @param status The application status
     * @param startDate The start date
     * @param endDate The end date
     * @param pageable Pagination information
     * @return Page of filtered applications
     */
    Page<CustomerOnboardingApplication> findByApplicationStatusAndCreatedAtBetween(
            CustomerOnboardingStatus status,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable);

    /**
     * Counts applications by status.
     * 
     * @param status The application status
     * @return Number of applications with the specified status
     */
    long countByApplicationStatus(CustomerOnboardingStatus status);

    /**
     * Finds applications submitted but not yet processed.
     * 
     * @param submittedAfter Applications submitted after this date
     * @param pageable Pagination information
     * @return Page of pending applications
     */
    @Query("SELECT a FROM CustomerOnboardingApplication a WHERE " +
           "a.applicationStatus = 'SUBMITTED' AND " +
           "a.submittedAt > :submittedAfter " +
           "ORDER BY a.submittedAt ASC")
    Page<CustomerOnboardingApplication> findPendingApplications(
            @Param("submittedAfter") LocalDateTime submittedAfter,
            Pageable pageable);

    /**
     * Finds applications requiring KYC verification.
     * 
     * @param pageable Pagination information
     * @return Page of applications requiring KYC
     */
    @Query("SELECT a FROM CustomerOnboardingApplication a WHERE " +
           "a.applicationStatus IN ('SUBMITTED', 'KYC_IN_PROGRESS') AND " +
           "a.kycVerificationId IS NULL " +
           "ORDER BY a.submittedAt ASC")
    Page<CustomerOnboardingApplication> findApplicationsRequiringKyc(Pageable pageable);

    /**
     * Finds applications by customer name (first and last name).
     * 
     * @param firstName The customer's first name (case-insensitive)
     * @param lastName The customer's last name (case-insensitive)
     * @param pageable Pagination information
     * @return Page of matching applications
     */
    @Query("SELECT a FROM CustomerOnboardingApplication a WHERE " +
           "LOWER(a.firstName) LIKE LOWER(CONCAT('%', :firstName, '%')) AND " +
           "LOWER(a.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))")
    Page<CustomerOnboardingApplication> findByCustomerName(
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            Pageable pageable);

    /**
     * Finds applications with expired KYC verification (older than specified days).
     * 
     * @param daysOld Number of days to consider as expired
     * @param pageable Pagination information
     * @return Page of applications with expired KYC
     */
    @Query("SELECT a FROM CustomerOnboardingApplication a WHERE " +
           "a.applicationStatus = 'KYC_IN_PROGRESS' AND " +
           "a.submittedAt < :cutoffDate")
    Page<CustomerOnboardingApplication> findExpiredKycApplications(
            @Param("cutoffDate") LocalDateTime cutoffDate,
            Pageable pageable);

    /**
     * Gets application statistics by status.
     * 
     * @return List of status counts
     */
    @Query("SELECT a.applicationStatus, COUNT(a) FROM CustomerOnboardingApplication a " +
           "GROUP BY a.applicationStatus")
    List<Object[]> getApplicationStatsByStatus();

    /**
     * Finds applications by multiple criteria (used for admin search).
     * 
     * @param email Customer email (optional)
     * @param status Application status (optional)
     * @param startDate Created after this date (optional)
     * @param endDate Created before this date (optional)
     * @param pageable Pagination information
     * @return Page of matching applications
     */
    @Query("SELECT a FROM CustomerOnboardingApplication a WHERE " +
           "(:email IS NULL OR LOWER(a.customerEmail) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
           "(:status IS NULL OR a.applicationStatus = :status) AND " +
           "(:startDate IS NULL OR a.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR a.createdAt <= :endDate) " +
           "ORDER BY a.createdAt DESC")
    Page<CustomerOnboardingApplication> findByMultipleCriteria(
            @Param("email") String email,
            @Param("status") CustomerOnboardingStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    /**
     * Finds all applications ordered by creation date (most recent first).
     * 
     * @param pageable Pagination information
     * @return Page of all applications
     */
    Page<CustomerOnboardingApplication> findAllByOrderByCreatedAtDesc(Pageable pageable);
}