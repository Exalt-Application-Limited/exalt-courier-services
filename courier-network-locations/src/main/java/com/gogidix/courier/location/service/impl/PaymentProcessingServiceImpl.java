package com.gogidix.courier.location.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.socialecommerceecosystem.location.model.PaymentMethod;
import com.socialecommerceecosystem.location.model.PaymentStatus;
import com.socialecommerceecosystem.location.model.WalkInPayment;
import com.socialecommerceecosystem.location.model.WalkInShipment;
import com.socialecommerceecosystem.location.repository.LocationStaffRepository;
import com.socialecommerceecosystem.location.repository.WalkInPaymentRepository;
import com.socialecommerceecosystem.location.repository.WalkInShipmentRepository;
import com.socialecommerceecosystem.location.service.NotificationService;
import com.socialecommerceecosystem.location.service.PaymentProcessingService;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of the PaymentProcessingService interface.
 * Provides business logic for processing payments for walk-in shipments at courier locations.
 */
@Service
@Slf4j
public class PaymentProcessingServiceImpl implements PaymentProcessingService {

    private final WalkInPaymentRepository paymentRepository;
    private final WalkInShipmentRepository shipmentRepository;
    private final LocationStaffRepository staffRepository;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    @Autowired
    public PaymentProcessingServiceImpl(
            WalkInPaymentRepository paymentRepository,
            WalkInShipmentRepository shipmentRepository,
            LocationStaffRepository staffRepository,
            NotificationService notificationService,
            ObjectMapper objectMapper) {
        this.paymentRepository = paymentRepository;
        this.shipmentRepository = shipmentRepository;
        this.staffRepository = staffRepository;
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
        log.info("PaymentProcessingServiceImpl initialized");
    }

    @Override
    public List<WalkInPayment> getAllPayments() {
        log.debug("Getting all payments");
        return paymentRepository.findAll();
    }
    
    @Override
    public Optional<WalkInPayment> getPaymentById(Long paymentId) {
        log.debug("Getting payment by ID: {}", paymentId);
        return paymentRepository.findById(paymentId);
    }
    
    @Override
    public Optional<WalkInPayment> getPaymentByTransactionId(String transactionId) {
        log.debug("Getting payment by transaction ID: {}", transactionId);
        return paymentRepository.findByTransactionId(transactionId);
    }
    
    @Override
    public Optional<WalkInPayment> getPaymentByReceiptNumber(String receiptNumber) {
        log.debug("Getting payment by receipt number: {}", receiptNumber);
        return paymentRepository.findByReceiptNumber(receiptNumber);
    }
    
    @Override
    public Optional<WalkInPayment> getPaymentByShipmentId(Long shipmentId) {
        log.debug("Getting payment by shipment ID: {}", shipmentId);
        return paymentRepository.findByShipmentId(shipmentId);
    }
    
    @Override
    @Transactional
    public WalkInPayment createPayment(WalkInPayment payment) {
        log.debug("Creating new payment");
        
        // Validate payment data
        if (payment.getAmount() == null || payment.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero");
        }
        
        if (payment.getPaymentMethod() == null) {
            throw new IllegalArgumentException("Payment method must be specified");
        }
        
        // Generate transaction ID if not provided
        if (payment.getTransactionId() == null || payment.getTransactionId().isEmpty()) {
            payment.setTransactionId(generateTransactionId());
        } else if (existsByTransactionId(payment.getTransactionId())) {
            throw new IllegalArgumentException("Transaction ID already exists: " + payment.getTransactionId());
        }
        
        // Generate receipt number if not provided
        if (payment.getReceiptNumber() == null || payment.getReceiptNumber().isEmpty()) {
            payment.setReceiptNumber(generateReceiptNumber());
        } else if (existsByReceiptNumber(payment.getReceiptNumber())) {
            throw new IllegalArgumentException("Receipt number already exists: " + payment.getReceiptNumber());
        }
        
        // Set payment date if not provided
        if (payment.getPaymentDate() == null) {
            payment.setPaymentDate(LocalDateTime.now());
        }
        
        // Set default status if not provided
        if (payment.getStatus() == null) {
            payment.setStatus(PaymentStatus.PENDING);
        }
        
        // Save payment
        WalkInPayment savedPayment = paymentRepository.save(payment);
        log.info("Payment created with ID: {}, transaction ID: {}", savedPayment.getId(), savedPayment.getTransactionId());
        
        // Notify relevant parties
        try {
            notificationService.sendPaymentNotification(savedPayment);
        } catch (Exception e) {
            log.error("Failed to send payment notification for payment ID: {}", savedPayment.getId(), e);
        }
        
        return savedPayment;
    }
    
    @Override
    @Transactional
    public WalkInPayment processPayment(Long shipmentId, BigDecimal amount, PaymentMethod paymentMethod, Map<String, Object> paymentDetails) {
        log.debug("Processing payment for shipment ID: {} with amount: {} and method: {}", shipmentId, amount, paymentMethod);
        
        // Validate input
        if (shipmentId == null) {
            throw new IllegalArgumentException("Shipment ID cannot be null");
        }
        
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero");
        }
        
        if (paymentMethod == null) {
            throw new IllegalArgumentException("Payment method must be specified");
        }
        
        // Check if shipment exists
        WalkInShipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new IllegalArgumentException("Shipment not found with ID: " + shipmentId));
        
        // Create new payment
        WalkInPayment payment = new WalkInPayment();
        payment.setShipment(shipment);
        payment.setShipmentId(shipmentId);
        payment.setAmount(amount);
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus(PaymentStatus.PROCESSING);
        payment.setTransactionId(generateTransactionId());
        payment.setReceiptNumber(generateReceiptNumber());
        
        // Process payment details based on payment method
        if (paymentDetails != null) {
            try {
                String detailsJson = objectMapper.writeValueAsString(paymentDetails);
                payment.setPaymentDetails(detailsJson);
                
                // Extract and set card information for credit card payments
                if (paymentMethod == PaymentMethod.CREDIT_CARD) {
                    if (paymentDetails.containsKey("cardType")) {
                        payment.setCardType((String) paymentDetails.get("cardType"));
                    }
                    if (paymentDetails.containsKey("lastFourDigits")) {
                        payment.setLastFourDigits((String) paymentDetails.get("lastFourDigits"));
                    }
                }
            } catch (Exception e) {
                log.error("Failed to process payment details", e);
                throw new IllegalArgumentException("Invalid payment details: " + e.getMessage());
            }
        }
        
        // Save the payment
        WalkInPayment savedPayment = paymentRepository.save(payment);
        log.info("Payment processed successfully with ID: {}, transaction ID: {}", savedPayment.getId(), savedPayment.getTransactionId());
        
        // Update shipment status based on payment status
        updateShipmentStatusBasedOnPayment(shipment, savedPayment);
        
        // Notify relevant parties
        try {
            notificationService.sendPaymentNotification(savedPayment);
        } catch (Exception e) {
            log.error("Failed to send payment notification for payment ID: {}", savedPayment.getId(), e);
        }
        
        return savedPayment;
    }
    
    @Override
    @Transactional
    public WalkInPayment updatePaymentStatus(Long paymentId, PaymentStatus newStatus, String notes) {
        log.debug("Updating payment status for payment ID: {} to status: {}", paymentId, newStatus);
        
        // Validate input
        if (paymentId == null) {
            throw new IllegalArgumentException("Payment ID cannot be null");
        }
        
        if (newStatus == null) {
            throw new IllegalArgumentException("New status cannot be null");
        }
        
        // Retrieve payment
        WalkInPayment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found with ID: " + paymentId));
        
        // Update status
        PaymentStatus oldStatus = payment.getStatus();
        payment.setStatus(newStatus);
        
        // Update notes if provided
        if (notes != null && !notes.isEmpty()) {
            payment.setNotes(notes);
        }
        
        // Update last modified date
        payment.setLastModifiedDate(LocalDateTime.now());
        
        // Save the updated payment
        WalkInPayment updatedPayment = paymentRepository.save(payment);
        log.info("Payment status updated from {} to {} for payment ID: {}", oldStatus, newStatus, paymentId);
        
        // Update shipment status based on payment status if payment is linked to a shipment
        if (updatedPayment.getShipment() != null) {
            updateShipmentStatusBasedOnPayment(updatedPayment.getShipment(), updatedPayment);
        }
        
        // Notify relevant parties about status change
        try {
            notificationService.sendPaymentStatusUpdateNotification(updatedPayment, oldStatus);
        } catch (Exception e) {
            log.error("Failed to send payment status update notification for payment ID: {}", updatedPayment.getId(), e);
        }
        
        return updatedPayment;
    }
    
    @Override
    @Transactional
    public WalkInPayment processRefund(Long paymentId, BigDecimal refundAmount, String reason) {
        log.debug("Processing refund for payment ID: {} with amount: {} and reason: {}", paymentId, refundAmount, reason);
        
        // Validate input
        if (paymentId == null) {
            throw new IllegalArgumentException("Payment ID cannot be null");
        }
        
        if (refundAmount == null || refundAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Refund amount must be greater than zero");
        }
        
        // Retrieve payment
        WalkInPayment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found with ID: " + paymentId));
        
        // Validate refund eligibility
        if (payment.getStatus() != PaymentStatus.COMPLETED && payment.getStatus() != PaymentStatus.PARTIALLY_REFUNDED) {
            throw new IllegalArgumentException("Payment must be in COMPLETED or PARTIALLY_REFUNDED status to process a refund");
        }
        
        // Check if refund amount is valid
        BigDecimal maxRefundAmount = payment.getAmount().subtract(payment.getRefundAmount() != null ? payment.getRefundAmount() : BigDecimal.ZERO);
        if (refundAmount.compareTo(maxRefundAmount) > 0) {
            throw new IllegalArgumentException("Refund amount exceeds available amount");
        }
        
        // Update payment with refund information
        BigDecimal previousRefundAmount = payment.getRefundAmount() != null ? payment.getRefundAmount() : BigDecimal.ZERO;
        BigDecimal newRefundAmount = previousRefundAmount.add(refundAmount);
        payment.setRefundAmount(newRefundAmount);
        payment.setRefundDate(LocalDateTime.now());
        payment.setRefundReason(reason);
        
        // Update status based on refund amount
        if (newRefundAmount.compareTo(payment.getAmount()) >= 0) {
            payment.setStatus(PaymentStatus.REFUNDED);
        } else {
            payment.setStatus(PaymentStatus.PARTIALLY_REFUNDED);
        }
        
        // Generate refund transaction ID
        String refundTransactionId = "REF-" + payment.getTransactionId() + "-" + System.currentTimeMillis();
        payment.setRefundTransactionId(refundTransactionId);
        
        // Save the updated payment
        WalkInPayment updatedPayment = paymentRepository.save(payment);
        log.info("Refund processed for payment ID: {} with refund amount: {} and transaction ID: {}", 
                 paymentId, refundAmount, refundTransactionId);
        
        // Notify relevant parties
        try {
            notificationService.sendRefundNotification(updatedPayment, refundAmount);
        } catch (Exception e) {
            log.error("Failed to send refund notification for payment ID: {}", updatedPayment.getId(), e);
        }
        
        return updatedPayment;
    }
    
    @Override
    public List<WalkInPayment> findPaymentsByStatus(PaymentStatus status) {
        log.debug("Finding payments by status: {}", status);
        return paymentRepository.findByStatus(status);
    }
    
    @Override
    public List<WalkInPayment> findPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Finding payments between dates: {} and {}", startDate, endDate);
        return paymentRepository.findByPaymentDateBetween(startDate, endDate);
    }
    
    @Override
    public List<WalkInPayment> findPaymentsByMethodAndStatus(PaymentMethod paymentMethod, PaymentStatus status) {
        log.debug("Finding payments by method: {} and status: {}", paymentMethod, status);
        return paymentRepository.findByPaymentMethodAndStatus(paymentMethod, status);
    }
    
    @Override
    public BigDecimal calculateTotalRevenue() {
        log.debug("Calculating total revenue");
        return paymentRepository.calculateTotalRevenue();
    }
    
    @Override
    public BigDecimal calculateTotalRevenueByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Calculating total revenue between dates: {} and {}", startDate, endDate);
        return paymentRepository.calculateTotalRevenueByDateRange(startDate, endDate);
    }
    
    @Override
    public BigDecimal calculateTotalRefunds() {
        log.debug("Calculating total refunds");
        return paymentRepository.calculateTotalRefunds();
    }
    
    @Override
    public BigDecimal calculateTotalRefundsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Calculating total refunds between dates: {} and {}", startDate, endDate);
        return paymentRepository.calculateTotalRefundsByDateRange(startDate, endDate);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<WalkInPayment> findPaymentsRequiringFollowUp() {
        log.debug("Finding payments requiring follow-up");
        return paymentRepository.findPaymentsRequiringFollowUp();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<WalkInPayment> findPaymentsWithIssues() {
        log.debug("Finding payments with issues");
        return paymentRepository.findPaymentsWithIssues();
    }
    
    @Override
    public Map<PaymentMethod, Long> getPaymentCountsByPaymentMethod() {
        log.debug("Getting payment counts by payment method");
        
        Map<PaymentMethod, Long> countsByMethod = new HashMap<>();
        for (PaymentMethod method : PaymentMethod.values()) {
            long count = paymentRepository.countByPaymentMethodAndStatus(method, null);
            countsByMethod.put(method, count);
        }
        
        return countsByMethod;
    }
    
    @Override
    public Map<PaymentStatus, Long> getPaymentCountsByStatus() {
        log.debug("Getting payment counts by status");
        
        Map<PaymentStatus, Long> countsByStatus = new HashMap<>();
        for (PaymentStatus status : PaymentStatus.values()) {
            long count = paymentRepository.countByPaymentMethodAndStatus(null, status);
            countsByStatus.put(status, count);
        }
        
        return countsByStatus;
    }
    
    @Override
    public Map<String, Long> getPaymentCountsByCardType() {
        log.debug("Getting payment counts by card type");
        
        // In a real implementation, this would use a custom repository query
        // For now, we'll handle it in the service layer
        Map<String, Long> countsByCardType = new HashMap<>();
        List<WalkInPayment> cardPayments = paymentRepository.findByPaymentMethod(PaymentMethod.CREDIT_CARD);
        
        for (WalkInPayment payment : cardPayments) {
            String cardType = payment.getCardType();
            if (cardType != null && !cardType.isEmpty()) {
                countsByCardType.put(cardType, countsByCardType.getOrDefault(cardType, 0L) + 1);
            }
        }
        
        return countsByCardType;
    }
    
    @Override
    public Double calculateFailedPaymentPercentage(LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Calculating failed payment percentage between dates: {} and {}", 
                startDate, endDate);
        
        Double percentage = paymentRepository.getFailedPaymentPercentage(startDate, endDate);
        
        return percentage != null ? percentage : 0.0;
    }
    
    @Override
    public boolean existsByTransactionId(String transactionId) {
        log.debug("Checking if transaction ID exists: {}", transactionId);
        return paymentRepository.findByTransactionId(transactionId).isPresent();
    }
    
    @Override
    public boolean existsByReceiptNumber(String receiptNumber) {
        log.debug("Checking if receipt number exists: {}", receiptNumber);
        return paymentRepository.findByReceiptNumber(receiptNumber).isPresent();
    }
    
    /**
     * Generate a unique transaction ID.
     * 
     * @return a unique transaction ID
     */
    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString();
    }
    
    /**
     * Generate a unique receipt number.
     * 
     * @return a unique receipt number
     */
    private String generateReceiptNumber() {
        return "RCP-" + System.currentTimeMillis() + "-" + (int) (Math.random() * 10000);
    }
    
    /**
     * Update shipment status based on payment status.
     * 
     * @param shipment the shipment to update
     * @param payment the payment with the new status
     */
    private void updateShipmentStatusBasedOnPayment(WalkInShipment shipment, WalkInPayment payment) {
        // Implementation would depend on the business rules for your application
        // This is a simplified example
        switch (payment.getStatus()) {
            case COMPLETED:
                // Only update shipment status if it's in a state that depends on payment
                if (shipment.getStatus() == com.socialecommerceecosystem.location.model.ShipmentStatus.PAYMENT_PENDING
                        || shipment.getStatus() == com.socialecommerceecosystem.location.model.ShipmentStatus.PROCESSING) {
                    shipment.setStatus(com.socialecommerceecosystem.location.model.ShipmentStatus.READY_FOR_PICKUP);
                    shipmentRepository.save(shipment);
                    log.info("Shipment status updated to READY_FOR_PICKUP for shipment ID: {}", shipment.getId());
                }
                break;
            case FAILED:
            case DECLINED:
                if (shipment.getStatus() == com.socialecommerceecosystem.location.model.ShipmentStatus.PAYMENT_PENDING
                        || shipment.getStatus() == com.socialecommerceecosystem.location.model.ShipmentStatus.PROCESSING) {
                    shipment.setStatus(com.socialecommerceecosystem.location.model.ShipmentStatus.PAYMENT_FAILED);
                    shipmentRepository.save(shipment);
                    log.info("Shipment status updated to PAYMENT_FAILED for shipment ID: {}", shipment.getId());
                }
                break;
            case REFUNDED:
                shipment.setStatus(com.socialecommerceecosystem.location.model.ShipmentStatus.CANCELLED);
                shipmentRepository.save(shipment);
                log.info("Shipment status updated to CANCELLED for shipment ID: {}", shipment.getId());
                break;
            default:
                // No action needed for other statuses
                break;
        }
    }
}