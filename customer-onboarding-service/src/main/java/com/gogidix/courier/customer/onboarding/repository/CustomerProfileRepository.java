package com.gogidix.courier.customer.onboarding.repository;

import com.gogidix.courier.customer.onboarding.model.CustomerProfile;
import com.gogidix.courier.customer.onboarding.model.CustomerOnboardingApplication;
import com.gogidix.ecosystem.shared.model.user.User;
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
 * Repository interface for CustomerProfile entity.
 * 
 * Provides data access operations for customer profiles that extend
 * the shared User entity with courier-specific information.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Repository
public interface CustomerProfileRepository extends JpaRepository<CustomerProfile, UUID> {

    /**
     * Finds a customer profile by the associated User entity.
     * 
     * @param user The shared User entity
     * @return Optional containing the customer profile if found
     */
    Optional<CustomerProfile> findByUser(User user);

    /**
     * Finds a customer profile by user ID.
     * 
     * @param userId The UUID of the associated user
     * @return Optional containing the customer profile if found
     */
    Optional<CustomerProfile> findByUserId(UUID userId);

    /**
     * Finds a customer profile by customer reference ID.
     * 
     * @param customerReferenceId The unique customer reference ID
     * @return Optional containing the customer profile if found
     */
    Optional<CustomerProfile> findByCustomerReferenceId(String customerReferenceId);

    /**
     * Finds a customer profile by the associated onboarding application.
     * 
     * @param onboardingApplication The customer onboarding application
     * @return Optional containing the customer profile if found
     */
    Optional<CustomerProfile> findByOnboardingApplication(CustomerOnboardingApplication onboardingApplication);

    /**
     * Finds customer profiles by customer segment.
     * 
     * @param customerSegment The customer segment (INDIVIDUAL, SMALL_BUSINESS, ENTERPRISE)
     * @param pageable Pagination information
     * @return Page of customer profiles in the specified segment
     */
    Page<CustomerProfile> findByCustomerSegment(String customerSegment, Pageable pageable);

    /**
     * Finds customer profiles by customer tier.
     * 
     * @param customerTier The customer tier (BRONZE, SILVER, GOLD, PLATINUM)
     * @param pageable Pagination information
     * @return Page of customer profiles in the specified tier
     */
    Page<CustomerProfile> findByCustomerTier(String customerTier, Pageable pageable);

    /**
     * Finds customer profiles by KYC verification status.
     * 
     * @param kycVerified KYC verification status
     * @param pageable Pagination information
     * @return Page of customer profiles with specified KYC status
     */
    Page<CustomerProfile> findByKycVerified(Boolean kycVerified, Pageable pageable);

    /**
     * Finds customer profiles by account activation status.
     * 
     * @param accountActivated Account activation status
     * @param pageable Pagination information
     * @return Page of customer profiles with specified activation status
     */
    Page<CustomerProfile> findByAccountActivated(Boolean accountActivated, Pageable pageable);

    /**
     * Finds customer profiles by billing customer ID.
     * 
     * @param billingCustomerId The billing customer ID
     * @return Optional containing the customer profile if found
     */
    Optional<CustomerProfile> findByBillingCustomerId(String billingCustomerId);

    /**
     * Checks if a customer reference ID already exists.
     * 
     * @param customerReferenceId The customer reference ID to check
     * @return true if exists, false otherwise
     */
    boolean existsByCustomerReferenceId(String customerReferenceId);

    /**
     * Finds customer profiles created within a date range.
     * 
     * @param startDate The start date
     * @param endDate The end date
     * @param pageable Pagination information
     * @return Page of customer profiles created within the date range
     */
    Page<CustomerProfile> findByCreatedAtBetween(
            LocalDateTime startDate, 
            LocalDateTime endDate, 
            Pageable pageable);

    /**
     * Finds customer profiles with recent activity (last order within days).
     * 
     * @param cutoffDate Profiles with last order after this date
     * @param pageable Pagination information
     * @return Page of recently active customer profiles
     */
    Page<CustomerProfile> findByLastOrderDateAfter(LocalDateTime cutoffDate, Pageable pageable);

    /**
     * Finds inactive customer profiles (no orders within specified days).
     * 
     * @param cutoffDate Profiles with last order before this date (or null)
     * @param pageable Pagination information
     * @return Page of inactive customer profiles
     */
    @Query("SELECT p FROM CustomerProfile p WHERE " +
           "p.lastOrderDate IS NULL OR p.lastOrderDate < :cutoffDate")
    Page<CustomerProfile> findInactiveCustomers(@Param("cutoffDate") LocalDateTime cutoffDate, Pageable pageable);

    /**
     * Finds top customers by total amount spent.
     * 
     * @param pageable Pagination information (use for top N)
     * @return Page of customer profiles ordered by total amount spent
     */
    Page<CustomerProfile> findByTotalAmountSpentIsNotNullOrderByTotalAmountSpentDesc(Pageable pageable);

    /**
     * Finds customers with high order volume.
     * 
     * @param minOrderCount Minimum number of orders
     * @param pageable Pagination information
     * @return Page of customer profiles with high order volume
     */
    Page<CustomerProfile> findByTotalOrdersCountGreaterThanEqualOrderByTotalOrdersCountDesc(
            Integer minOrderCount, 
            Pageable pageable);

    /**
     * Gets customer statistics by segment.
     * 
     * @return List of [segment, count] statistics
     */
    @Query("SELECT p.customerSegment, COUNT(p) FROM CustomerProfile p " +
           "WHERE p.customerSegment IS NOT NULL " +
           "GROUP BY p.customerSegment " +
           "ORDER BY COUNT(p) DESC")
    List<Object[]> getCustomerStatsBySegment();

    /**
     * Gets customer statistics by tier.
     * 
     * @return List of [tier, count] statistics
     */
    @Query("SELECT p.customerTier, COUNT(p) FROM CustomerProfile p " +
           "WHERE p.customerTier IS NOT NULL " +
           "GROUP BY p.customerTier " +
           "ORDER BY COUNT(p) DESC")
    List<Object[]> getCustomerStatsByTier();

    /**
     * Finds customers eligible for tier upgrade based on spending/orders.
     * 
     * @param minAmount Minimum total amount spent
     * @param minOrders Minimum total orders
     * @param currentTier Current customer tier
     * @param pageable Pagination information
     * @return Page of customers eligible for upgrade
     */
    @Query("SELECT p FROM CustomerProfile p WHERE " +
           "p.totalAmountSpent >= :minAmount AND " +
           "p.totalOrdersCount >= :minOrders AND " +
           "p.customerTier = :currentTier AND " +
           "p.accountActivated = true")
    Page<CustomerProfile> findTierUpgradeEligible(
            @Param("minAmount") Double minAmount,
            @Param("minOrders") Integer minOrders,
            @Param("currentTier") String currentTier,
            Pageable pageable);

    /**
     * Finds customers requiring KYC verification.
     * 
     * @param pageable Pagination information
     * @return Page of customer profiles requiring KYC
     */
    @Query("SELECT p FROM CustomerProfile p WHERE " +
           "p.kycVerified = false AND " +
           "p.accountActivated = true " +
           "ORDER BY p.createdAt ASC")
    Page<CustomerProfile> findRequiringKycVerification(Pageable pageable);

    /**
     * Finds customers by multiple search criteria.
     * 
     * @param customerReferenceId Customer reference ID (partial match)
     * @param customerSegment Customer segment
     * @param customerTier Customer tier
     * @param kycVerified KYC verification status
     * @param accountActivated Account activation status
     * @param pageable Pagination information
     * @return Page of matching customer profiles
     */
    @Query("SELECT p FROM CustomerProfile p WHERE " +
           "(:customerReferenceId IS NULL OR p.customerReferenceId LIKE %:customerReferenceId%) AND " +
           "(:customerSegment IS NULL OR p.customerSegment = :customerSegment) AND " +
           "(:customerTier IS NULL OR p.customerTier = :customerTier) AND " +
           "(:kycVerified IS NULL OR p.kycVerified = :kycVerified) AND " +
           "(:accountActivated IS NULL OR p.accountActivated = :accountActivated) " +
           "ORDER BY p.createdAt DESC")
    Page<CustomerProfile> findByMultipleCriteria(
            @Param("customerReferenceId") String customerReferenceId,
            @Param("customerSegment") String customerSegment,
            @Param("customerTier") String customerTier,
            @Param("kycVerified") Boolean kycVerified,
            @Param("accountActivated") Boolean accountActivated,
            Pageable pageable);

    /**
     * Counts total number of active customers.
     * 
     * @return Number of active customers
     */
    long countByAccountActivatedTrue();

    /**
     * Counts total number of KYC verified customers.
     * 
     * @return Number of KYC verified customers
     */
    long countByKycVerifiedTrue();
}