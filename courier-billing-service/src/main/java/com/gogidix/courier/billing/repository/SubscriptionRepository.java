package com.gogidix.courier.billing.repository;

import com.gogidix.courier.billing.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for Subscription operations.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

    /**
     * Find all subscriptions for a specific customer.
     */
    List<Subscription> findByCustomerIdOrderByCreatedAtDesc(String customerId);

    /**
     * Find subscriptions by status.
     */
    List<Subscription> findByStatusOrderByCreatedAtDesc(String status);

    /**
     * Find active subscriptions for a customer.
     */
    List<Subscription> findByCustomerIdAndStatus(String customerId, String status);

    /**
     * Find subscriptions due for billing.
     */
    @Query("SELECT s FROM Subscription s WHERE s.status = 'ACTIVE' AND s.nextBillingDate <= :currentDate ORDER BY s.nextBillingDate ASC")
    List<Subscription> findSubscriptionsDueForBilling(@Param("currentDate") LocalDateTime currentDate);

    /**
     * Find subscriptions expiring soon.
     */
    @Query("SELECT s FROM Subscription s WHERE s.status = 'ACTIVE' AND s.endDate IS NOT NULL AND s.endDate <= :cutoffDate ORDER BY s.endDate ASC")
    List<Subscription> findExpiringSoon(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Count active subscriptions for a customer.
     */
    Long countByCustomerIdAndStatus(String customerId, String status);
}