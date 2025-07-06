package com.gogidix.courier.corporate.customer.onboarding.repository;

import com.gogidix.courier.corporate.customer.onboarding.model.CorporateApplicationStatusHistory;
import com.gogidix.courier.corporate.customer.onboarding.model.CorporateOnboardingApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for Corporate Application Status History operations.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Repository
public interface CorporateApplicationStatusHistoryRepository extends JpaRepository<CorporateApplicationStatusHistory, UUID> {

    /**
     * Find all status history entries for a specific corporate application ordered by change date descending.
     * 
     * @param application the corporate onboarding application
     * @return list of status history entries
     */
    List<CorporateApplicationStatusHistory> findByApplicationOrderByChangedAtDesc(CorporateOnboardingApplication application);

    /**
     * Find all status history entries for a specific corporate application ordered by change date ascending.
     * 
     * @param application the corporate onboarding application
     * @return list of status history entries
     */
    List<CorporateApplicationStatusHistory> findByApplicationOrderByChangedAtAsc(CorporateOnboardingApplication application);

    /**
     * Find the most recent status change for a specific corporate application.
     * 
     * @param application the corporate onboarding application
     * @return the most recent status history entry
     */
    @Query("SELECT h FROM CorporateApplicationStatusHistory h WHERE h.application = :application ORDER BY h.changedAt DESC LIMIT 1")
    CorporateApplicationStatusHistory findMostRecentByApplication(@Param("application") CorporateOnboardingApplication application);

    /**
     * Count the number of status changes for a specific corporate application.
     * 
     * @param application the corporate onboarding application
     * @return count of status changes
     */
    long countByApplication(CorporateOnboardingApplication application);
}