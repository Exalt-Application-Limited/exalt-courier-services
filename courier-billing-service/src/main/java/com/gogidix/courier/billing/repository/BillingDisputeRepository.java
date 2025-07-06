package com.gogidix.courier.billing.repository;

import com.gogidix.courier.billing.model.BillingDispute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Billing Dispute operations.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Repository
public interface BillingDisputeRepository extends JpaRepository<BillingDispute, UUID> {

    /**
     * Find dispute by dispute number.
     */
    Optional<BillingDispute> findByDisputeNumber(String disputeNumber);

    /**
     * Find all disputes for a customer.
     */
    List<BillingDispute> findByCustomerIdOrderByCreatedAtDesc(String customerId);

    /**
     * Find disputes by invoice ID.
     */
    List<BillingDispute> findByInvoiceIdOrderByCreatedAtDesc(String invoiceId);

    /**
     * Find open disputes by customer.
     */
    @Query("SELECT d FROM BillingDispute d WHERE d.customerId = :customerId AND d.status NOT IN ('RESOLVED_CUSTOMER_FAVOR', 'RESOLVED_MERCHANT_FAVOR', 'CLOSED_NO_RESOLUTION') ORDER BY d.createdAt DESC")
    List<BillingDispute> findOpenDisputesByCustomer(@Param("customerId") String customerId);

    /**
     * Find disputes due for review.
     */
    @Query("SELECT d FROM BillingDispute d WHERE d.dueDate <= :currentDate AND d.status IN ('UNDER_REVIEW', 'AWAITING_CUSTOMER_RESPONSE', 'AWAITING_INTERNAL_RESPONSE') ORDER BY d.dueDate ASC")
    List<BillingDispute> findDisputesDueForReview(@Param("currentDate") LocalDateTime currentDate);
}