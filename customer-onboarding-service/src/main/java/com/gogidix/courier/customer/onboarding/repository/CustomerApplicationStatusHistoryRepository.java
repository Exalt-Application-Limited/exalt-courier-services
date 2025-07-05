package com.gogidix.courier.customer.onboarding.repository;

import com.gogidix.courier.customer.onboarding.model.CustomerApplicationStatusHistory;
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
import java.util.UUID;

/**
 * Repository interface for CustomerApplicationStatusHistory entity.
 * 
 * Provides data access operations for tracking status changes in customer
 * onboarding applications using UUID-based primary keys.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Repository
public interface CustomerApplicationStatusHistoryRepository extends JpaRepository<CustomerApplicationStatusHistory, UUID> {

    /**
     * Finds all status history records for a specific application.
     * 
     * @param application The customer onboarding application
     * @return List of status history records ordered by changed date
     */
    List<CustomerApplicationStatusHistory> findByApplicationOrderByChangedAtDesc(CustomerOnboardingApplication application);

    /**
     * Finds all status history records for a specific application ID.
     * 
     * @param applicationId The application UUID
     * @return List of status history records ordered by changed date
     */
    List<CustomerApplicationStatusHistory> findByApplicationIdOrderByChangedAtDesc(UUID applicationId);

    /**
     * Finds status history by application and target status.
     * 
     * @param application The customer onboarding application
     * @param toStatus The target status to filter by
     * @return List of status history records
     */
    List<CustomerApplicationStatusHistory> findByApplicationAndToStatus(
            CustomerOnboardingApplication application, 
            CustomerOnboardingStatus toStatus);

    /**
     * Finds the most recent status change for an application.
     * 
     * @param application The customer onboarding application
     * @return The most recent status history record if exists
     */
    CustomerApplicationStatusHistory findTopByApplicationOrderByChangedAtDesc(CustomerOnboardingApplication application);

    /**
     * Finds status changes within a date range for an application.
     * 
     * @param application The customer onboarding application
     * @param startDate The start date
     * @param endDate The end date
     * @return List of status history records within the date range
     */
    List<CustomerApplicationStatusHistory> findByApplicationAndChangedAtBetweenOrderByChangedAtDesc(
            CustomerOnboardingApplication application,
            LocalDateTime startDate,
            LocalDateTime endDate);

    /**
     * Finds all status changes by a specific user.
     * 
     * @param changedBy The user who made the status changes
     * @param pageable Pagination information
     * @return Page of status history records
     */
    Page<CustomerApplicationStatusHistory> findByChangedByOrderByChangedAtDesc(String changedBy, Pageable pageable);

    /**
     * Finds status changes for a specific transition (from one status to another).
     * 
     * @param fromStatus The original status
     * @param toStatus The new status
     * @param pageable Pagination information
     * @return Page of status history records
     */
    Page<CustomerApplicationStatusHistory> findByFromStatusAndToStatusOrderByChangedAtDesc(
            CustomerOnboardingStatus fromStatus,
            CustomerOnboardingStatus toStatus,
            Pageable pageable);

    /**
     * Counts status changes for a specific application.
     * 
     * @param application The customer onboarding application
     * @return Number of status changes for the application
     */
    long countByApplication(CustomerOnboardingApplication application);

    /**
     * Finds status changes made by admin users within a date range.
     * 
     * @param startDate The start date
     * @param endDate The end date
     * @param adminUsers List of admin usernames
     * @param pageable Pagination information
     * @return Page of admin status changes
     */
    @Query("SELECT h FROM CustomerApplicationStatusHistory h WHERE " +
           "h.changedAt BETWEEN :startDate AND :endDate AND " +
           "h.changedBy IN :adminUsers " +
           "ORDER BY h.changedAt DESC")
    Page<CustomerApplicationStatusHistory> findAdminStatusChanges(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("adminUsers") List<String> adminUsers,
            Pageable pageable);

    /**
     * Gets status transition statistics.
     * 
     * @return List of transition counts [fromStatus, toStatus, count]
     */
    @Query("SELECT h.fromStatus, h.toStatus, COUNT(h) FROM CustomerApplicationStatusHistory h " +
           "WHERE h.fromStatus IS NOT NULL " +
           "GROUP BY h.fromStatus, h.toStatus " +
           "ORDER BY COUNT(h) DESC")
    List<Object[]> getStatusTransitionStats();

    /**
     * Finds applications that have been in a specific status for too long.
     * 
     * @param status The status to check
     * @param cutoffDate Applications in this status since before this date
     * @param pageable Pagination information
     * @return Page of applications stuck in status
     */
    @Query("SELECT h FROM CustomerApplicationStatusHistory h WHERE " +
           "h.toStatus = :status AND " +
           "h.changedAt < :cutoffDate AND " +
           "h.application.applicationStatus = :status " +
           "ORDER BY h.changedAt ASC")
    Page<CustomerApplicationStatusHistory> findStuckInStatus(
            @Param("status") CustomerOnboardingStatus status,
            @Param("cutoffDate") LocalDateTime cutoffDate,
            Pageable pageable);

    /**
     * Finds the average time spent in each status.
     * 
     * @return List of [status, average_hours]
     */
    @Query("SELECT h1.toStatus, " +
           "AVG(EXTRACT(EPOCH FROM (COALESCE(h2.changedAt, CURRENT_TIMESTAMP) - h1.changedAt)) / 3600.0) " +
           "FROM CustomerApplicationStatusHistory h1 " +
           "LEFT JOIN CustomerApplicationStatusHistory h2 ON " +
           "h1.application = h2.application AND h2.changedAt > h1.changedAt AND " +
           "h2.id = (SELECT MIN(h3.id) FROM CustomerApplicationStatusHistory h3 " +
           "         WHERE h3.application = h1.application AND h3.changedAt > h1.changedAt) " +
           "GROUP BY h1.toStatus")
    List<Object[]> getAverageTimeInStatus();

    /**
     * Deletes old status history records (for data cleanup).
     * 
     * @param cutoffDate Delete records older than this date
     * @return Number of deleted records
     */
    @Query("DELETE FROM CustomerApplicationStatusHistory h WHERE h.changedAt < :cutoffDate")
    int deleteOldRecords(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Finds all status changes for applications created within a date range.
     * 
     * @param startDate Application created after this date
     * @param endDate Application created before this date
     * @param pageable Pagination information
     * @return Page of status history for applications in date range
     */
    @Query("SELECT h FROM CustomerApplicationStatusHistory h " +
           "WHERE h.application.createdAt BETWEEN :startDate AND :endDate " +
           "ORDER BY h.changedAt DESC")
    Page<CustomerApplicationStatusHistory> findByApplicationCreatedAtBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);
}