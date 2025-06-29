package com.exalt.courier.location.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.socialecommerceecosystem.location.model.PaymentMethod;
import com.socialecommerceecosystem.location.model.PaymentStatus;
import com.socialecommerceecosystem.location.model.WalkInPayment;
import com.socialecommerceecosystem.location.model.WalkInShipment;

/**
 * Service interface for processing payments for walk-in shipments at physical courier locations.
 * Provides high-level business functions for payment management.
 */
public interface PaymentProcessingService {
    
    /**
     * Get all payments.
     * 
     * @return list of all payments
     */
    List<WalkInPayment> getAllPayments();
    
    /**
     * Get a payment by ID.
     * 
     * @param paymentId the ID of the payment
     * @return optional containing the payment if found
     */
    Optional<WalkInPayment> getPaymentById(Long paymentId);
    
    /**
     * Get a payment by transaction ID.
     * 
     * @param transactionId the transaction ID of the payment
     * @return optional containing the payment if found
     */
    Optional<WalkInPayment> getPaymentByTransactionId(String transactionId);
    
    /**
     * Get a payment by receipt number.
     * 
     * @param receiptNumber the receipt number of the payment
     * @return optional containing the payment if found
     */
    Optional<WalkInPayment> getPaymentByReceiptNumber(String receiptNumber);
    
    /**
     * Get a payment by shipment ID.
     * 
     * @param shipmentId the ID of the associated shipment
     * @return optional containing the payment if found
     */
    Optional<WalkInPayment> getPaymentByShipmentId(Long shipmentId);
    
    /**
     * Create a new payment.
     * 
     * @param payment the payment to create
     * @return the created payment
     */
    WalkInPayment createPayment(WalkInPayment payment);
    
    /**
     * Process a payment for a shipment.
     * 
     * @param shipment the shipment to process payment for
     * @param paymentMethod the payment method
     * @param staffId the ID of the staff member processing the payment
     * @param paymentDetails additional payment details (card info, etc.)
     * @return the created payment
     */
    WalkInPayment processPayment(WalkInShipment shipment, PaymentMethod paymentMethod, 
            Long staffId, Map<String, Object> paymentDetails);
    
    /**
     * Update an existing payment.
     * 
     * @param paymentId the ID of the payment to update
     * @param payment the updated payment details
     * @return the updated payment
     */
    WalkInPayment updatePayment(Long paymentId, WalkInPayment payment);
    
    /**
     * Delete a payment.
     * 
     * @param paymentId the ID of the payment to delete
     */
    void deletePayment(Long paymentId);
    
    /**
     * Update the status of a payment.
     * 
     * @param paymentId the ID of the payment
     * @param newStatus the new status
     * @return the updated payment
     */
    WalkInPayment updatePaymentStatus(Long paymentId, PaymentStatus newStatus);
    
    /**
     * Get payments by status.
     * 
     * @param status the status of payments to find
     * @return list of payments with the specified status
     */
    List<WalkInPayment> getPaymentsByStatus(PaymentStatus status);
    
    /**
     * Get payments by payment method.
     * 
     * @param paymentMethod the payment method
     * @return list of payments with the specified payment method
     */
    List<WalkInPayment> getPaymentsByPaymentMethod(PaymentMethod paymentMethod);
    
    /**
     * Get payments by date range.
     * 
     * @param startDate the start of the date range
     * @param endDate the end of the date range
     * @return list of payments within the specified date range
     */
    List<WalkInPayment> getPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Get payments by amount range.
     * 
     * @param minAmount the minimum amount
     * @param maxAmount the maximum amount
     * @return list of payments within the specified amount range
     */
    List<WalkInPayment> getPaymentsByAmountRange(BigDecimal minAmount, BigDecimal maxAmount);
    
    /**
     * Get payments processed by a specific staff member.
     * 
     * @param staffId the ID of the staff member
     * @return list of payments processed by the specified staff member
     */
    List<WalkInPayment> getPaymentsByStaff(Long staffId);
    
    /**
     * Process a refund for a payment.
     * 
     * @param paymentId the ID of the payment to refund
     * @param refundAmount the amount to refund
     * @param reason the reason for the refund
     * @return the updated payment
     */
    WalkInPayment processRefund(Long paymentId, BigDecimal refundAmount, String reason);
    
    /**
     * Mark a payment as failed.
     * 
     * @param paymentId the ID of the payment
     * @param failureReason the reason for the failure
     * @return the updated payment
     */
    WalkInPayment markPaymentAsFailed(Long paymentId, String failureReason);
    
    /**
     * Generate a receipt number for a payment.
     * 
     * @param payment the payment to generate a receipt number for
     * @return the generated receipt number
     */
    String generateReceiptNumber(WalkInPayment payment);
    
    /**
     * Generate a transaction ID for a new payment.
     * 
     * @return the generated transaction ID
     */
    String generateTransactionId();
    
    /**
     * Find payments with refunds.
     * 
     * @return list of payments with refunds
     */
    List<WalkInPayment> findPaymentsWithRefunds();
    
    /**
     * Find payments by refund date range.
     * 
     * @param startDate the start of the date range
     * @param endDate the end of the date range
     * @return list of payments with refunds within the specified date range
     */
    List<WalkInPayment> findPaymentsByRefundDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find payments by card type.
     * 
     * @param cardType the card type
     * @return list of payments with the specified card type
     */
    List<WalkInPayment> findPaymentsByCardType(String cardType);
    
    /**
     * Calculate total revenue by payment method within a date range.
     * 
     * @param paymentMethod the payment method
     * @param startDate the start of the date range
     * @param endDate the end of the date range
     * @return the total revenue
     */
    BigDecimal calculateRevenueByPaymentMethodAndDateRange(
            PaymentMethod paymentMethod, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Calculate total refunds within a date range.
     * 
     * @param startDate the start of the date range
     * @param endDate the end of the date range
     * @return the total refunds
     */
    BigDecimal calculateTotalRefundsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find payments requiring follow-up (pending or on hold).
     * 
     * @return list of payments requiring follow-up
     */
    List<WalkInPayment> findPaymentsRequiringFollowUp();
    
    /**
     * Find payments with issues (failed or disputed).
     * 
     * @return list of payments with issues
     */
    List<WalkInPayment> findPaymentsWithIssues();
    
    /**
     * Get payment counts by payment method.
     * 
     * @return map of payment counts by payment method
     */
    Map<PaymentMethod, Long> getPaymentCountsByPaymentMethod();
    
    /**
     * Get payment counts by status.
     * 
     * @return map of payment counts by status
     */
    Map<PaymentStatus, Long> getPaymentCountsByStatus();
    
    /**
     * Get payment counts by card type.
     * 
     * @return map of payment counts by card type
     */
    Map<String, Long> getPaymentCountsByCardType();
    
    /**
     * Calculate failed payment percentage for a specific time period.
     * 
     * @param startDate the start of the time period
     * @param endDate the end of the time period
     * @return the percentage of failed payments
     */
    Double calculateFailedPaymentPercentage(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Check if a transaction ID already exists.
     * 
     * @param transactionId the transaction ID to check
     * @return true if the transaction ID exists, false otherwise
     */
    boolean existsByTransactionId(String transactionId);
    
    /**
     * Check if a receipt number already exists.
     * 
     * @param receiptNumber the receipt number to check
     * @return true if the receipt number exists, false otherwise
     */
    boolean existsByReceiptNumber(String receiptNumber);
}
