package com.gogidix.courier.commission.controller;

import com.gogidix.courier.commission.model.PartnerPayment;
import com.gogidix.courier.commission.model.PaymentStatus;
import com.gogidix.courier.commission.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@Slf4j
public class PaymentController {

    
    
    @Autowired
    private PaymentService paymentService;
    
    @PostMapping
    public ResponseEntity<PartnerPayment> createPayment(@Valid @RequestBody PartnerPayment payment) {
        log.info("REST request to create a payment");
        PartnerPayment createdPayment = paymentService.createPayment(payment);
        return new ResponseEntity<>(createdPayment, HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PartnerPayment> getPayment(@PathVariable String id) {
        log.info("REST request to get payment with ID: {}", id);
        PartnerPayment payment = paymentService.getPayment(id);
        return ResponseEntity.ok(payment);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PartnerPayment> updatePayment(
            @PathVariable String id, 
            @Valid @RequestBody PartnerPayment payment) {
        log.info("REST request to update payment with ID: {}", id);
        
        // Ensure the ID in the path matches the ID in the body
        if (!id.equals(payment.getId())) {
            return ResponseEntity.badRequest().build();
        }
        
        PartnerPayment updatedPayment = paymentService.updatePayment(payment);
        return ResponseEntity.ok(updatedPayment);
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<PartnerPayment> updatePaymentStatus(
            @PathVariable String id, 
            @RequestParam PaymentStatus status) {
        log.info("REST request to update status to {} for payment with ID: {}", status, id);
        PartnerPayment updatedPayment = paymentService.updatePaymentStatus(id, status);
        return ResponseEntity.ok(updatedPayment);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable String id) {
        log.info("REST request to delete payment with ID: {}", id);
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{id}/process")
    public ResponseEntity<PartnerPayment> processPayment(
            @PathVariable String id,
            @RequestParam String paymentMethod) {
        log.info("REST request to process payment with ID: {} using method: {}", id, paymentMethod);
        PartnerPayment processedPayment = paymentService.processPayment(id, paymentMethod);
        return ResponseEntity.ok(processedPayment);
    }
    
    @GetMapping("/partner/{partnerId}")
    public ResponseEntity<List<PartnerPayment>> getPaymentsByPartner(@PathVariable String partnerId) {
        log.info("REST request to get payments for partner ID: {}", partnerId);
        List<PartnerPayment> payments = paymentService.findPaymentsByPartner(partnerId);
        return ResponseEntity.ok(payments);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<PartnerPayment>> getPaymentsByStatus(@PathVariable PaymentStatus status) {
        log.info("REST request to get payments with status: {}", status);
        List<PartnerPayment> payments = paymentService.findPaymentsByStatus(status);
        return ResponseEntity.ok(payments);
    }
    
    @GetMapping("/date-range")
    public ResponseEntity<List<PartnerPayment>> getPaymentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        log.info("REST request to get payments between {} and {}", startDate, endDate);
        List<PartnerPayment> payments = paymentService.findPaymentsByDateRange(startDate, endDate);
        return ResponseEntity.ok(payments);
    }
    
    @GetMapping("/period")
    public ResponseEntity<List<PartnerPayment>> getPaymentsByPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("REST request to get payments for period between {} and {}", startDate, endDate);
        List<PartnerPayment> payments = paymentService.findPaymentsByPeriod(startDate, endDate);
        return ResponseEntity.ok(payments);
    }
    
    @GetMapping("/pending")
    public ResponseEntity<List<PartnerPayment>> getPendingPayments() {
        log.info("REST request to get pending payments");
        List<PartnerPayment> payments = paymentService.findPendingPayments();
        return ResponseEntity.ok(payments);
    }
    
    @PostMapping("/generate/{partnerId}")
    public ResponseEntity<PartnerPayment> generatePayment(
            @PathVariable String partnerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodStart,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodEnd) {
        
        log.info("REST request to generate payment for partner ID: {} for period {} to {}", 
                partnerId, periodStart, periodEnd);
        
        PartnerPayment payment = paymentService.generatePayment(partnerId, periodStart, periodEnd);
        return new ResponseEntity<>(payment, HttpStatus.CREATED);
    }
    
    @PostMapping("/generate-all")
    public ResponseEntity<List<PartnerPayment>> generatePaymentsForAllPartners(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodStart,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodEnd) {
        
        log.info("REST request to generate payments for all partners for period {} to {}", 
                periodStart, periodEnd);
        
        List<PartnerPayment> payments = paymentService.generatePaymentsForAllPartners(periodStart, periodEnd);
        return new ResponseEntity<>(payments, HttpStatus.CREATED);
    }
    
    @PostMapping("/process-pending")
    public ResponseEntity<Void> processPendingPayments() {
        log.info("REST request to process all pending payments");
        paymentService.schedulePaymentsProcessing();
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{id}/details")
    public ResponseEntity<List<PaymentService.PaymentDetail>> getPaymentDetails(@PathVariable String id) {
        log.info("REST request to get payment details for payment ID: {}", id);
        List<PaymentService.PaymentDetail> details = paymentService.getPaymentDetails(id);
        return ResponseEntity.ok(details);
    }
}

