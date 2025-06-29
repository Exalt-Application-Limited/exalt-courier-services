package com.exalt.courierservices.commission.$1;

import com.exalt.courier.commission.model.PartnerPayment;
import com.exalt.courier.commission.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface PaymentService {
    
    /**
     * Create a new payment for a partner
     */
    PartnerPayment createPayment(PartnerPayment payment);
    
    /**
     * Get payment by ID
     */
    PartnerPayment getPayment(String paymentId);
    
    /**
     * Update payment
     */
    PartnerPayment updatePayment(PartnerPayment payment);
    
    /**
     * Update payment status
     */
    PartnerPayment updatePaymentStatus(String paymentId, PaymentStatus status);
    
    /**
     * Delete payment
     */
    void deletePayment(String paymentId);
    
    /**
     * Process payment - update status, set payment date, generate reference
     */
    PartnerPayment processPayment(String paymentId, String paymentMethod);
    
    /**
     * Find payments by partner ID
     */
    List<PartnerPayment> findPaymentsByPartner(String partnerId);
    
    /**
     * Find payments by status
     */
    List<PartnerPayment> findPaymentsByStatus(PaymentStatus status);
    
    /**
     * Find payments by date range
     */
    List<PartnerPayment> findPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find payments by period
     */
    List<PartnerPayment> findPaymentsByPeriod(LocalDate startDate, LocalDate endDate);
    
    /**
     * Find pending payments
     */
    List<PartnerPayment> findPendingPayments();
    
    /**
     * Generate payment for a partner's unpaid commissions within a date range
     */
    PartnerPayment generatePayment(String partnerId, LocalDate periodStart, LocalDate periodEnd);
    
    /**
     * Generate payments for all partners with unpaid commissions in a date range
     */
    List<PartnerPayment> generatePaymentsForAllPartners(LocalDate periodStart, LocalDate periodEnd);
    
    /**
     * Schedule payments processing for pending payments
     */
    void schedulePaymentsProcessing();
    
    /**
     * Get payment details with commission entries
     */
    List<PaymentDetail> getPaymentDetails(String paymentId);
    
    /**
     * Process all pending payments (called by scheduler)
     */
    void processPendingPayments();
    
    /**
     * Generate payments for all partners (called by scheduler)
     */
    void generateAllPayments();
    
    /**
     * Inner class to represent payment detail information
     * Converted to use Lombok annotations for reduced boilerplate.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    class PaymentDetail {
        private String commissionEntryId;
        private String orderId;
        private double baseAmount;
        private double commissionAmount;
        private LocalDateTime transactionDate;
    }
}
