package com.exalt.courier.location.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.socialecommerceecosystem.location.model.PaymentMethod;
import com.socialecommerceecosystem.location.model.PaymentStatus;
import com.socialecommerceecosystem.location.model.WalkInPayment;

/**
 * Repository interface for WalkInPayment entity.
 * Provides methods for accessing and querying payment data.
 */
@Repository
public interface WalkInPaymentRepository extends JpaRepository<WalkInPayment, Long> {
    
    /**
     * Find payment by transaction ID.
     * 
     * @param transactionId the transaction ID to search for
     * @return optional payment with the specified transaction ID
     */
    Optional<WalkInPayment> findByTransactionId(String transactionId);
    
    /**
     * Find payment by receipt number.
     * 
     * @param receiptNumber the receipt number to search for
     * @return optional payment with the specified receipt number
     */
    Optional<WalkInPayment> findByReceiptNumber(String receiptNumber);
    
    /**
     * Find payment by shipment ID.
     * 
     * @param shipmentId the shipment ID to search for
     * @return optional payment for the specified shipment
     */
    Optional<WalkInPayment> findByShipmentId(Long shipmentId);
    
    /**
     * Find payments by status.
     * 
     * @param status the payment status to search for
     * @return list of payments with the specified status
     */
    List<WalkInPayment> findByStatus(PaymentStatus status);
    
    /**
     * Find payments by payment method.
     * 
     * @param paymentMethod the payment method to search for
     * @return list of payments with the specified payment method
     */
    List<WalkInPayment> findByPaymentMethod(PaymentMethod paymentMethod);
    
    /**
     * Find payments processed by a specific staff member.
     * 
     * @param staffId the ID of the staff member
     * @return list of payments processed by the specified staff member
     */
    List<WalkInPayment> findByProcessedByStaffId(Long staffId);
    
    /**
     * Find payments by payment date range.
     * 
     * @param startDate the start of the date range
     * @param endDate the end of the date range
     * @return list of payments within the specified date range
     */
    List<WalkInPayment> findByPaymentDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find payments by amount range.
     * 
     * @param minAmount the minimum amount
     * @param maxAmount the maximum amount
     * @return list of payments within the specified amount range
     */
    List<WalkInPayment> findByAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);
    
    /**
     * Find payments with refunds.
     * 
     * @return list of payments with refunds
     */
    List<WalkInPayment> findByRefundAmountIsNotNull();
    
    /**
     * Find payments by refund date range.
     * 
     * @param startDate the start of the date range
     * @param endDate the end of the date range
     * @return list of payments with refunds within the specified date range
     */
    List<WalkInPayment> findByRefundDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find payments by card type.
     * 
     * @param cardType the card type to search for
     * @return list of payments with the specified card type
     */
    List<WalkInPayment> findByCardType(String cardType);
    
    /**
     * Find payments by authorization code.
     * 
     * @param authorizationCode the authorization code to search for
     * @return list of payments with the specified authorization code
     */
    List<WalkInPayment> findByAuthorizationCode(String authorizationCode);
    
    /**
     * Find payments containing specific notes.
     * 
     * @param notesText the text to search for in notes
     * @return list of payments with notes containing the specified text
     */
    List<WalkInPayment> findByNotesContainingIgnoreCase(String notesText);
    
    /**
     * Calculate total revenue by payment method within a date range.
     * 
     * @param paymentMethod the payment method
     * @param startDate the start of the date range
     * @param endDate the end of the date range
     * @return the total revenue for the specified payment method and date range
     */
    @Query("SELECT SUM(p.amount) FROM WalkInPayment p WHERE p.paymentMethod = :paymentMethod " +
           "AND p.status = 'COMPLETED' AND p.paymentDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalRevenueByPaymentMethodAndDateRange(
            @Param("paymentMethod") PaymentMethod paymentMethod,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    /**
     * Calculate total refunds within a date range.
     * 
     * @param startDate the start of the date range
     * @param endDate the end of the date range
     * @return the total refunds for the specified date range
     */
    @Query("SELECT SUM(p.refundAmount) FROM WalkInPayment p WHERE p.refundAmount IS NOT NULL " +
           "AND p.refundDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalRefundsByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find payments requiring follow-up (pending or on hold).
     * 
     * @return list of payments requiring follow-up
     */
    @Query("SELECT p FROM WalkInPayment p WHERE p.status IN ('PENDING', 'PROCESSING', 'ON_HOLD', 'AWAITING_CONFIRMATION')")
    List<WalkInPayment> findPaymentsRequiringFollowUp();
    
    /**
     * Find payments with issues (failed or disputed).
     * 
     * @return list of payments with issues
     */
    @Query("SELECT p FROM WalkInPayment p WHERE p.status IN ('FAILED', 'DECLINED', 'DISPUTED')")
    List<WalkInPayment> findPaymentsWithIssues();
    
    /**
     * Count payments by payment method and status.
     * 
     * @param paymentMethod the payment method to count
     * @param status the payment status to count
     * @return the count of payments with the specified method and status
     */
    long countByPaymentMethodAndStatus(PaymentMethod paymentMethod, PaymentStatus status);
    
    /**
     * Get failed payment percentage for a specific time period.
     * 
     * @param startDate the start of the time period
     * @param endDate the end of the time period
     * @return the percentage of failed payments
     */
    @Query("SELECT (COUNT(CASE WHEN p.status IN ('FAILED', 'DECLINED') THEN 1 ELSE NULL END) * 100.0 / COUNT(*)) " +
           "FROM WalkInPayment p WHERE p.paymentDate BETWEEN :startDate AND :endDate")
    Double getFailedPaymentPercentage(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
