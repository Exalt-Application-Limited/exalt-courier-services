package com.gogidix.courier.commission.controller;

import com.gogidix.courier.commission.model.CommissionEntry;
import com.gogidix.courier.commission.model.CommissionStatus;
import com.gogidix.courier.commission.service.CommissionService;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/commissions")
@Slf4j
public class CommissionController {

    
    
    @Autowired
    private CommissionService commissionService;
    
    @PostMapping("/calculate")
    public ResponseEntity<CommissionEntry> calculateCommission(
            @RequestParam String partnerId, 
            @RequestParam String orderId, 
            @RequestParam double amount,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime transactionDate) {
        
        log.info("REST request to calculate commission for partner ID: {}, order ID: {}, amount: {}", 
                partnerId, orderId, amount);
        
        // Use current time if not provided
        if (transactionDate == null) {
            transactionDate = LocalDateTime.now();
        }
        
        CommissionEntry entry = commissionService.calculateCommission(partnerId, orderId, amount, transactionDate);
        return new ResponseEntity<>(entry, HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CommissionEntry> getCommissionEntry(@PathVariable String id) {
        log.info("REST request to get commission entry with ID: {}", id);
        CommissionEntry entry = commissionService.getCommissionEntry(id);
        return ResponseEntity.ok(entry);
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<CommissionEntry> updateCommissionStatus(
            @PathVariable String id, 
            @RequestParam CommissionStatus status) {
        log.info("REST request to update status to {} for commission entry with ID: {}", status, id);
        CommissionEntry updatedEntry = commissionService.updateCommissionStatus(id, status);
        return ResponseEntity.ok(updatedEntry);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommissionEntry(@PathVariable String id) {
        log.info("REST request to delete commission entry with ID: {}", id);
        commissionService.deleteCommissionEntry(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/partner/{partnerId}")
    public ResponseEntity<List<CommissionEntry>> getCommissionsByPartner(@PathVariable String partnerId) {
        log.info("REST request to get commissions for partner ID: {}", partnerId);
        List<CommissionEntry> entries = commissionService.findCommissionsByPartner(partnerId);
        return ResponseEntity.ok(entries);
    }
    
    @GetMapping("/partner/{partnerId}/date-range")
    public ResponseEntity<List<CommissionEntry>> getCommissionsByPartnerAndDateRange(
            @PathVariable String partnerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("REST request to get commissions for partner ID: {} between {} and {}", 
                partnerId, startDate, endDate);
        
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        
        List<CommissionEntry> entries = commissionService.findCommissionsByPartnerAndDateRange(
                partnerId, startDateTime, endDateTime);
        
        return ResponseEntity.ok(entries);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<CommissionEntry>> getCommissionsByStatus(@PathVariable CommissionStatus status) {
        log.info("REST request to get commissions with status: {}", status);
        List<CommissionEntry> entries = commissionService.findCommissionsByStatus(status);
        return ResponseEntity.ok(entries);
    }
    
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<CommissionEntry>> getCommissionsByOrderId(@PathVariable String orderId) {
        log.info("REST request to get commissions for order ID: {}", orderId);
        List<CommissionEntry> entries = commissionService.findCommissionsByOrderId(orderId);
        return ResponseEntity.ok(entries);
    }
    
    @GetMapping("/unpaid")
    public ResponseEntity<List<CommissionEntry>> getUnpaidCommissions() {
        log.info("REST request to get all unpaid commissions");
        List<CommissionEntry> entries = commissionService.findUnpaidCommissions();
        return ResponseEntity.ok(entries);
    }
    
    @GetMapping("/partner/{partnerId}/unpaid")
    public ResponseEntity<List<CommissionEntry>> getUnpaidCommissionsByPartner(@PathVariable String partnerId) {
        log.info("REST request to get unpaid commissions for partner ID: {}", partnerId);
        List<CommissionEntry> entries = commissionService.findUnpaidCommissionsByPartner(partnerId);
        return ResponseEntity.ok(entries);
    }
    
    @PostMapping("/recalculate")
    public ResponseEntity<Void> recalculateCommissions(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("REST request to recalculate commissions between {} and {}", startDate, endDate);
        commissionService.recalculateCommissions(startDate, endDate);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/bulk-calculate")
    public ResponseEntity<List<CommissionEntry>> bulkCalculateCommissions(
            @RequestBody List<CommissionService.OrderData> orderDataList) {
        
        log.info("REST request to bulk calculate commissions for {} orders", orderDataList.size());
        List<CommissionEntry> entries = commissionService.bulkCalculateCommissions(orderDataList);
        return new ResponseEntity<>(entries, HttpStatus.CREATED);
    }
}

