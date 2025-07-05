package com.gogidix.courier.billing.repository;

import com.gogidix.courier.billing.model.Payment;
import com.gogidix.courier.billing.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Payment operations.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    /**
     * Find payment by payment ID.
     */
    Optional<Payment> findByPaymentId(String paymentId);

    /**
     * Find all payments for a specific invoice.
     */
    @Query("SELECT p FROM Payment p WHERE p.invoice.invoiceNumber = :invoiceNumber ORDER BY p.processedAt DESC")
    List<Payment> findByInvoiceNumber(@Param("invoiceNumber") String invoiceNumber);

    /**
     * Find all payments for a specific customer.
     */
    List<Payment> findByCustomerIdOrderByProcessedAtDesc(String customerId);

    /**
     * Find payments by status.
     */
    List<Payment> findByStatusOrderByProcessedAtDesc(PaymentStatus status);

    /**
     * Find payments by customer and status.
     */
    List<Payment> findByCustomerIdAndStatusOrderByProcessedAtDesc(String customerId, PaymentStatus status);

    /**
     * Find payments by date range.
     */
    @Query("SELECT p FROM Payment p WHERE p.processedAt BETWEEN :fromDate AND :toDate ORDER BY p.processedAt DESC")
    List<Payment> findByDateRange(@Param("fromDate") LocalDateTime fromDate, 
                                @Param("toDate") LocalDateTime toDate);

    /**
     * Find payment by gateway transaction ID.
     */
    Optional<Payment> findByGatewayTransactionId(String gatewayTransactionId);

    /**
     * Find failed payments for retry.
     */
    @Query("SELECT p FROM Payment p WHERE p.status = 'FAILED' AND p.processedAt > :cutoffDate ORDER BY p.processedAt DESC")
    List<Payment> findFailedPaymentsForRetry(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Count payments by customer and status.
     */
    Long countByCustomerIdAndStatus(String customerId, PaymentStatus status);

    /**
     * Check if payment ID exists.
     */
    boolean existsByPaymentId(String paymentId);

    /**
     * Find refunds by original payment ID.
     */
    List<Payment> findByOriginalPaymentId(String originalPaymentId);
}