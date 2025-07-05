package com.gogidix.courier.corporate.customer.onboarding.repository;

import com.gogidix.courier.corporate.customer.onboarding.enums.CorporateOnboardingStatus;
import com.gogidix.courier.corporate.customer.onboarding.enums.BusinessType;
import com.gogidix.courier.corporate.customer.onboarding.enums.IndustrySector;
import com.gogidix.courier.corporate.customer.onboarding.enums.CompanySize;
import com.gogidix.courier.corporate.customer.onboarding.model.CorporateOnboardingApplication;
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
 * Repository interface for Corporate Onboarding Application entities.
 * 
 * Provides comprehensive data access methods for corporate customer onboarding
 * including complex queries for business analytics and reporting.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Repository
public interface CorporateOnboardingApplicationRepository extends JpaRepository<CorporateOnboardingApplication, UUID> {

    // ========== BASIC FINDERS ==========

    /**
     * Find application by unique reference ID.
     */
    Optional<CorporateOnboardingApplication> findByApplicationReferenceId(String applicationReferenceId);

    /**
     * Find application by company email.
     */
    Optional<CorporateOnboardingApplication> findByCompanyEmail(String companyEmail);

    /**
     * Find application by company registration number.
     */
    Optional<CorporateOnboardingApplication> findByCompanyRegistrationNumber(String registrationNumber);

    /**
     * Check if application exists by company email and excludes specific statuses.
     */
    boolean existsByCompanyEmailAndApplicationStatusNotIn(String companyEmail, List<CorporateOnboardingStatus> excludedStatuses);

    /**
     * Check if application exists by registration number and excludes specific statuses.
     */
    boolean existsByCompanyRegistrationNumberAndApplicationStatusNotIn(String registrationNumber, List<CorporateOnboardingStatus> excludedStatuses);

    // ========== STATUS-BASED QUERIES ==========

    /**
     * Find applications by status.
     */
    Page<CorporateOnboardingApplication> findByApplicationStatus(CorporateOnboardingStatus status, Pageable pageable);

    /**
     * Find applications by multiple statuses.
     */
    Page<CorporateOnboardingApplication> findByApplicationStatusIn(List<CorporateOnboardingStatus> statuses, Pageable pageable);

    /**
     * Find applications excluding specific statuses.
     */
    Page<CorporateOnboardingApplication> findByApplicationStatusNotIn(List<CorporateOnboardingStatus> excludedStatuses, Pageable pageable);

    /**
     * Count applications by status.
     */
    long countByApplicationStatus(CorporateOnboardingStatus status);

    /**
     * Find applications pending admin action.
     */
    @Query("SELECT a FROM CorporateOnboardingApplication a WHERE a.applicationStatus IN :statuses ORDER BY a.createdAt ASC")
    List<CorporateOnboardingApplication> findApplicationsPendingAdminAction(@Param("statuses") List<CorporateOnboardingStatus> statuses);

    /**
     * Find applications pending customer action.
     */
    @Query("SELECT a FROM CorporateOnboardingApplication a WHERE a.applicationStatus IN :statuses ORDER BY a.createdAt ASC")
    List<CorporateOnboardingApplication> findApplicationsPendingCustomerAction(@Param("statuses") List<CorporateOnboardingStatus> statuses);

    // ========== BUSINESS CLASSIFICATION QUERIES ==========

    /**
     * Find applications by business type.
     */
    Page<CorporateOnboardingApplication> findByBusinessType(BusinessType businessType, Pageable pageable);

    /**
     * Find applications by industry sector.
     */
    Page<CorporateOnboardingApplication> findByIndustrySector(IndustrySector industrySector, Pageable pageable);

    /**
     * Find applications by company size.
     */
    Page<CorporateOnboardingApplication> findByCompanySize(CompanySize companySize, Pageable pageable);

    /**
     * Count applications by business type.
     */
    long countByBusinessType(BusinessType businessType);

    /**
     * Count applications by industry sector.
     */
    long countByIndustrySector(IndustrySector industrySector);

    // ========== SEARCH AND FILTERING ==========

    /**
     * Search applications with multiple criteria.
     */
    @Query("""
        SELECT a FROM CorporateOnboardingApplication a 
        WHERE (:searchTerm IS NULL OR 
               LOWER(a.companyName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
               LOWER(a.companyEmail) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
               LOWER(a.primaryContactFirstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
               LOWER(a.primaryContactLastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
               LOWER(a.companyRegistrationNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
        AND (:businessType IS NULL OR a.businessType = :businessType)
        AND (:industrySector IS NULL OR a.industrySector = :industrySector)
        AND (:companySize IS NULL OR a.companySize = :companySize)
        AND (:applicationStatus IS NULL OR a.applicationStatus = :applicationStatus)
        AND (:fromDate IS NULL OR a.createdAt >= :fromDate)
        AND (:toDate IS NULL OR a.createdAt <= :toDate)
        ORDER BY a.createdAt DESC
    """)
    Page<CorporateOnboardingApplication> searchApplications(
        @Param("searchTerm") String searchTerm,
        @Param("businessType") BusinessType businessType,
        @Param("industrySector") IndustrySector industrySector,
        @Param("companySize") CompanySize companySize,
        @Param("applicationStatus") CorporateOnboardingStatus applicationStatus,
        @Param("fromDate") LocalDateTime fromDate,
        @Param("toDate") LocalDateTime toDate,
        Pageable pageable
    );

    // ========== TIME-BASED QUERIES ==========

    /**
     * Find applications created between dates.
     */
    List<CorporateOnboardingApplication> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find applications submitted after specific date.
     */
    List<CorporateOnboardingApplication> findBySubmittedAtAfter(LocalDateTime date);

    /**
     * Find applications approved in date range.
     */
    List<CorporateOnboardingApplication> findByApprovedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find overdue applications (in progress status for too long).
     */
    @Query("""
        SELECT a FROM CorporateOnboardingApplication a 
        WHERE a.applicationStatus IN :inProgressStatuses 
        AND a.createdAt < :cutoffDate
        ORDER BY a.createdAt ASC
    """)
    List<CorporateOnboardingApplication> findOverdueApplications(
        @Param("inProgressStatuses") List<CorporateOnboardingStatus> inProgressStatuses,
        @Param("cutoffDate") LocalDateTime cutoffDate
    );

    // ========== BUSINESS ANALYTICS QUERIES ==========

    /**
     * Get application statistics by date range.
     */
    @Query("""
        SELECT a.applicationStatus, COUNT(a) 
        FROM CorporateOnboardingApplication a 
        WHERE a.createdAt BETWEEN :startDate AND :endDate 
        GROUP BY a.applicationStatus
    """)
    List<Object[]> getApplicationStatisticsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Get industry distribution.
     */
    @Query("SELECT a.industrySector, COUNT(a) FROM CorporateOnboardingApplication a GROUP BY a.industrySector")
    List<Object[]> getIndustryDistribution();

    /**
     * Get company size distribution.
     */
    @Query("SELECT a.companySize, COUNT(a) FROM CorporateOnboardingApplication a GROUP BY a.companySize")
    List<Object[]> getCompanySizeDistribution();

    /**
     * Get business type distribution.
     */
    @Query("SELECT a.businessType, COUNT(a) FROM CorporateOnboardingApplication a GROUP BY a.businessType")
    List<Object[]> getBusinessTypeDistribution();

    /**
     * Get approval rate by business type.
     */
    @Query("""
        SELECT a.businessType, 
               COUNT(CASE WHEN a.applicationStatus = 'APPROVED' THEN 1 END) as approved,
               COUNT(a) as total
        FROM CorporateOnboardingApplication a 
        WHERE a.applicationStatus IN ('APPROVED', 'REJECTED')
        GROUP BY a.businessType
    """)
    List<Object[]> getApprovalRateByBusinessType();

    /**
     * Get average processing time by status.
     */
    @Query("""
        SELECT a.applicationStatus, 
               AVG(EXTRACT(EPOCH FROM (a.updatedAt - a.createdAt))/3600) as avgHours
        FROM CorporateOnboardingApplication a 
        WHERE a.applicationStatus IN ('APPROVED', 'REJECTED')
        GROUP BY a.applicationStatus
    """)
    List<Object[]> getAverageProcessingTimeByStatus();

    // ========== INTEGRATION QUERIES ==========

    /**
     * Find applications by auth service user ID.
     */
    Optional<CorporateOnboardingApplication> findByAuthServiceUserId(String authServiceUserId);

    /**
     * Find applications by KYB verification ID.
     */
    Optional<CorporateOnboardingApplication> findByKybVerificationId(String kybVerificationId);

    /**
     * Find applications by billing customer ID.
     */
    Optional<CorporateOnboardingApplication> findByBillingCustomerId(String billingCustomerId);

    /**
     * Find applications requiring KYB verification.
     */
    @Query("""
        SELECT a FROM CorporateOnboardingApplication a 
        WHERE a.applicationStatus IN ('SUBMITTED', 'DOCUMENTS_UPLOADED') 
        AND a.kybVerificationId IS NULL
        ORDER BY a.createdAt ASC
    """)
    List<CorporateOnboardingApplication> findApplicationsRequiringKybVerification();

    // ========== COMPLIANCE AND AUDIT QUERIES ==========

    /**
     * Find applications with missing required consents.
     */
    @Query("""
        SELECT a FROM CorporateOnboardingApplication a 
        WHERE a.termsAccepted = false 
        OR a.privacyPolicyAccepted = false 
        OR a.dataProcessingAgreementAccepted = false
    """)
    List<CorporateOnboardingApplication> findApplicationsWithMissingConsents();

    /**
     * Find applications by country for compliance reporting.
     */
    List<CorporateOnboardingApplication> findByBusinessCountry(String country);

    /**
     * Count applications by country.
     */
    @Query("SELECT a.businessCountry, COUNT(a) FROM CorporateOnboardingApplication a GROUP BY a.businessCountry")
    List<Object[]> getApplicationCountByCountry();

    // ========== PERFORMANCE MONITORING ==========

    /**
     * Find applications created today.
     */
    @Query("SELECT a FROM CorporateOnboardingApplication a WHERE DATE(a.createdAt) = CURRENT_DATE")
    List<CorporateOnboardingApplication> findApplicationsCreatedToday();

    /**
     * Count applications created in last N days.
     */
    @Query("""
        SELECT COUNT(a) FROM CorporateOnboardingApplication a 
        WHERE a.createdAt >= :fromDate
    """)
    long countApplicationsCreatedSince(@Param("fromDate") LocalDateTime fromDate);

    /**
     * Find top companies by application volume (for enterprise customers).
     */
    @Query("""
        SELECT a.companyName, COUNT(a) as applicationCount
        FROM CorporateOnboardingApplication a 
        GROUP BY a.companyName 
        HAVING COUNT(a) > 1
        ORDER BY applicationCount DESC
    """)
    List<Object[]> findTopCompaniesByApplicationVolume();
}