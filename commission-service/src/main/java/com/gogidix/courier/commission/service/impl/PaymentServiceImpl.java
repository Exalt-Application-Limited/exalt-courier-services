package com.gogidix.courier.commission.service;

import com.gogidix.courier.commission.model.*;
import com.gogidix.courier.commission.repository.CommissionEntryRepository;
import com.gogidix.courier.commission.repository.PartnerPaymentRepository;
import com.gogidix.courier.commission.repository.PaymentDetailsRepository;
import com.gogidix.courier.commission.service.CommissionService;
import com.gogidix.courier.commission.service.PartnerService;
import com.gogidix.courier.commission.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    
    
    @Autowired
    private PartnerPaymentRepository paymentRepository;
    
    @Autowired
    private CommissionEntryRepository commissionEntryRepository;
    
    @Autowired
    private PaymentDetailsRepository paymentDetailsRepository;
    
    @Autowired
    private PartnerService partnerService;
    
    @Autowired
    private CommissionService commissionService;
    
    @Override
    @Transactional
    public PartnerPayment createPayment(PartnerPayment payment) {
        log.info("Creating new payment for partner ID: {}", payment.getPartner().getId());
        return paymentRepository.save(payment);
    }
    
    @Override
    public PartnerPayment getPayment(String paymentId) {
        log.debug("Getting payment with ID: {}", paymentId);
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found with ID: " + paymentId));
    }
    
    @Override
    @Transactional
    public PartnerPayment updatePayment(PartnerPayment payment) {
        log.info("Updating payment with ID: {}", payment.getId());
        
        // Verify payment exists
        if (!paymentRepository.existsById(payment.getId())) {
            throw new EntityNotFoundException("Payment not found with ID: " + payment.getId());
        }
        
        return paymentRepository.save(payment);
    }
    
    @Override
    @Transactional
    public PartnerPayment updatePaymentStatus(String paymentId, PaymentStatus status) {
        log.info("Updating payment status to {} for payment ID: {}", status, paymentId);
        
        PartnerPayment payment = getPayment(paymentId);
        payment.setStatus(status);
        
        return paymentRepository.save(payment);
    }
    
    @Override
    @Transactional
    public void deletePayment(String paymentId) {
        log.info("Deleting payment with ID: {}", paymentId);
        
        // Verify payment exists
        if (!paymentRepository.existsById(paymentId)) {
            throw new EntityNotFoundException("Payment not found with ID: " + paymentId);
        }
        
        // Delete payment details (junction table entries) first
        List<PaymentDetails> details = paymentDetailsRepository.findByPaymentId(paymentId);
        paymentDetailsRepository.deleteAll(details);
        
        // Reset commission entries payment reference
        details.forEach(detail -> {
            try {
                CommissionEntry entry = commissionEntryRepository.findById(detail.getCommissionEntryId())
                        .orElse(null);
                if (entry != null) {
                    entry.setPaymentId(null);
                    entry.setStatus(CommissionStatus.APPROVED); // Reset to approved status
                    commissionEntryRepository.save(entry);
                }
            } catch (Exception e) {
                log.error("Error resetting commission entry for payment ID: {}: {}", 
                        paymentId, e.getMessage());
            }
        });
        
        // Delete the payment
        paymentRepository.deleteById(paymentId);
    }
    
    @Override
    @Transactional
    public PartnerPayment processPayment(String paymentId, String paymentMethod) {
        log.info("Processing payment ID: {} using method: {}", paymentId, paymentMethod);
        
        PartnerPayment payment = getPayment(paymentId);
        
        // Only process if in PENDING status
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException("Cannot process payment that is not in PENDING status");
        }
        
        // Update payment details
        payment.setStatus(PaymentStatus.PROCESSING);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentMethod(paymentMethod);
        payment.setReferenceNumber("PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        
        // Save updated payment
        PartnerPayment updatedPayment = paymentRepository.save(payment);
        
        // Simulate payment processing
        simulatePaymentProcessing(updatedPayment);
        
        return updatedPayment;
    }
    
    private void simulatePaymentProcessing(PartnerPayment payment) {
        // In a real implementation, this would integrate with a payment gateway
        
        try {
            // Simulate processing delay
            Thread.sleep((long) (Math.random() * 2000));
            
            // Simulate successful payment
            if (Math.random() < 0.95) { // 95% success rate
                payment.setStatus(PaymentStatus.COMPLETED);
                
                // Update commission entries status
                payment.getCommissionEntries().forEach(entry -> {
                    entry.setStatus(CommissionStatus.PAID);
                    commissionEntryRepository.save(entry);
                });
            } else {
                // Simulate failed payment
                payment.setStatus(PaymentStatus.FAILED);
                payment.setNotes("Payment processing failed: Technical error with payment gateway");
            }
            
            paymentRepository.save(payment);
            
            log.info("Payment processing completed with status: {} for payment ID: {}", 
                    payment.getStatus(), payment.getId());
        } catch (Exception e) {
            log.error("Error in payment processing for payment ID: {}: {}", 
                    payment.getId(), e.getMessage());
            
            payment.setStatus(PaymentStatus.FAILED);
            payment.setNotes("Payment processing failed: " + e.getMessage());
            paymentRepository.save(payment);
        }
    }
    
    @Override
    public List<PartnerPayment> findPaymentsByPartner(String partnerId) {
        log.debug("Finding payments for partner ID: {}", partnerId);
        return paymentRepository.findByPartnerId(partnerId);
    }
    
    @Override
    public List<PartnerPayment> findPaymentsByStatus(PaymentStatus status) {
        log.debug("Finding payments with status: {}", status);
        return paymentRepository.findByStatus(status);
    }
    
    @Override
    public List<PartnerPayment> findPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Finding payments between {} and {}", startDate, endDate);
        return paymentRepository.findByPaymentDateBetween(startDate, endDate);
    }
    
    @Override
    public List<PartnerPayment> findPaymentsByPeriod(LocalDate startDate, LocalDate endDate) {
        log.debug("Finding payments for period between {} and {}", startDate, endDate);
        return paymentRepository.findByPeriodBetween(startDate, endDate);
    }
    
    @Override
    public List<PartnerPayment> findPendingPayments() {
        log.debug("Finding pending payments");
        return paymentRepository.findPendingPayments(PaymentStatus.PENDING);
    }
    
    @Override
    @Transactional
    public PartnerPayment generatePayment(String partnerId, LocalDate periodStart, LocalDate periodEnd) {
        log.info("Generating payment for partner ID: {} for period {} to {}", 
                partnerId, periodStart, periodEnd);
        
        Partner partner = partnerService.getPartner(partnerId);
        
        // Find all unpaid commissions for the partner in the given period
        LocalDateTime startDateTime = periodStart.atStartOfDay();
        LocalDateTime endDateTime = periodEnd.atTime(LocalTime.MAX);
        
        List<CommissionEntry> unpaidCommissions = commissionService.findUnpaidCommissionsByPartner(partnerId);
        List<CommissionEntry> commissionsInPeriod = unpaidCommissions.stream()
                .filter(entry -> {
                    LocalDateTime date = entry.getTransactionDate();
                    return !date.isBefore(startDateTime) && !date.isAfter(endDateTime);
                })
                .collect(Collectors.toList());
        
        if (commissionsInPeriod.isEmpty()) {
            throw new IllegalStateException("No unpaid commissions found for partner ID: " + 
                    partnerId + " in period " + periodStart + " to " + periodEnd);
        }
        
        // Calculate total payment amount
        BigDecimal totalAmount = commissionsInPeriod.stream()
                .map(CommissionEntry::getCommissionAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Create payment
        PartnerPayment payment = new PartnerPayment();
        payment.setPartner(partner);
        payment.setAmount(totalAmount);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setPeriodStart(periodStart);
        payment.setPeriodEnd(periodEnd);
        
        // Save payment
        PartnerPayment savedPayment = paymentRepository.save(payment);
        
        // Create payment details and link commission entries
        for (CommissionEntry entry : commissionsInPeriod) {
            PaymentDetails detail = new PaymentDetails();
            detail.setPaymentId(savedPayment.getId());
            detail.setCommissionEntryId(entry.getId());
            detail.setAmount(entry.getCommissionAmount());
            paymentDetailsRepository.save(detail);
            
            // Update commission entry with payment ID
            entry.setPaymentId(savedPayment.getId());
            commissionEntryRepository.save(entry);
        }
        
        // Update payment with commission entries
        savedPayment.setCommissionEntries(new java.util.HashSet<>(commissionsInPeriod));
        
        return savedPayment;
    }
    
    @Override
    @Transactional
    public List<PartnerPayment> generatePaymentsForAllPartners(LocalDate periodStart, LocalDate periodEnd) {
        log.info("Generating payments for all partners for period {} to {}", periodStart, periodEnd);
        
        List<PartnerPayment> results = new ArrayList<>();
        List<Partner> activePartners = partnerService.findPartnersByStatus(PartnerStatus.ACTIVE);
        
        for (Partner partner : activePartners) {
            try {
                PartnerPayment payment = generatePayment(partner.getId(), periodStart, periodEnd);
                results.add(payment);
            } catch (Exception e) {
                log.warn("Failed to generate payment for partner ID: {}: {}", 
                        partner.getId(), e.getMessage());
                // Continue with next partner
            }
        }
        
        return results;
    }
    
    @Override
    @Scheduled(cron = "${commission.payment.schedule:0 0 2 * * ?}") // Default: 2 AM every day
    @Transactional
    public void schedulePaymentsProcessing() {
        log.info("Running scheduled payments processing");
        
        List<PartnerPayment> pendingPayments = findPendingPayments();
        
        log.info("Found {} pending payments to process", pendingPayments.size());
        
        for (PartnerPayment payment : pendingPayments) {
            try {
                processPayment(payment.getId(), "AUTO_TRANSFER");
                log.info("Successfully processed payment ID: {}", payment.getId());
            } catch (Exception e) {
                log.error("Failed to process payment ID: {}: {}", payment.getId(), e.getMessage());
                // Continue with next payment
            }
        }
    }
    
    @Override
    public List<PaymentDetail> getPaymentDetails(String paymentId) {
        log.debug("Getting payment details for payment ID: {}", paymentId);
        
        List<PaymentDetails> details = paymentDetailsRepository.findByPaymentId(paymentId);
        List<PaymentDetail> result = new ArrayList<>();
        
        for (PaymentDetails detail : details) {
            try {
                CommissionEntry entry = commissionEntryRepository.findById(detail.getCommissionEntryId())
                        .orElse(null);
                
                if (entry != null) {
                    PaymentDetail paymentDetail = new PaymentDetail();
                    paymentDetail.setCommissionEntryId(entry.getId());
                    paymentDetail.setOrderId(entry.getOrderId());
                    paymentDetail.setBaseAmount(entry.getBaseAmount().doubleValue());
                    paymentDetail.setCommissionAmount(entry.getCommissionAmount().doubleValue());
                    paymentDetail.setTransactionDate(entry.getTransactionDate());
                    
                    result.add(paymentDetail);
                }
            } catch (Exception e) {
                log.error("Error getting commission entry for payment detail: {}", e.getMessage());
            }
        }
        
        return result;
    }
    
    @Override
    @Transactional
    public void processPendingPayments() {
        log.info("Processing all pending payments");
        List<PartnerPayment> pendingPayments = findPendingPayments();
        for (PartnerPayment payment : pendingPayments) {
            try {
                processPayment(payment.getId(), "SCHEDULED_PROCESSING");
                log.debug("Processed payment: {}", payment.getId());
            } catch (Exception e) {
                log.error("Failed to process payment {}: {}", payment.getId(), e.getMessage());
            }
        }
    }
    
    @Override
    @Transactional  
    public void generateAllPayments() {
        log.info("Generating payments for all partners");
        LocalDate lastMonth = LocalDate.now().minusMonths(1);
        LocalDate periodStart = lastMonth.withDayOfMonth(1);
        LocalDate periodEnd = lastMonth.withDayOfMonth(lastMonth.lengthOfMonth());
        
        List<PartnerPayment> generatedPayments = generatePaymentsForAllPartners(periodStart, periodEnd);
        log.info("Generated {} payments for all partners", generatedPayments.size());
    }
}

