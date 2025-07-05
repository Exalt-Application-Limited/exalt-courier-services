package com.gogidix.courier.location.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.socialecommerceecosystem.location.model.PaymentMethod;
import com.socialecommerceecosystem.location.model.PaymentStatus;
import com.socialecommerceecosystem.location.model.WalkInPayment;
import com.socialecommerceecosystem.location.model.WalkInShipment;
import com.socialecommerceecosystem.location.service.NotificationService;
import com.socialecommerceecosystem.location.service.PaymentProcessingService;
import com.socialecommerceecosystem.location.service.ShipmentProcessingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for payment operations in the courier network.
 * Provides endpoints for managing walk-in payments and refunds.
 */
@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payment Management", description = "API for managing payments in the courier network")
@Slf4j
public class PaymentController {

    private final PaymentProcessingService paymentService;
    private final ShipmentProcessingService shipmentService;
    private final NotificationService notificationService;

    @Autowired
    public PaymentController(
            PaymentProcessingService paymentService,
            ShipmentProcessingService shipmentService,
            NotificationService notificationService) {
        this.paymentService = paymentService;
        this.shipmentService = shipmentService;
        this.notificationService = notificationService;
    }

    @GetMapping
    @Operation(summary = "Get all payments", description = "Retrieves all payments")
    @ApiResponse(responseCode = "200", description = "Payments retrieved successfully", 
            content = @Content(schema = @Schema(implementation = WalkInPayment.class)))
    public ResponseEntity<List<WalkInPayment>> getAllPayments() {
        log.debug("REST request to get all payments");
        List<WalkInPayment> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment by ID", description = "Retrieves a specific payment by ID")
    @ApiResponse(responseCode = "200", description = "Payment found", 
            content = @Content(schema = @Schema(implementation = WalkInPayment.class)))
    @ApiResponse(responseCode = "404", description = "Payment not found")
    public ResponseEntity<WalkInPayment> getPaymentById(
            @Parameter(description = "ID of the payment", required = true) @PathVariable Long id) {
        log.debug("REST request to get payment with ID: {}", id);
        return paymentService.getPaymentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/transaction/{transactionId}")
    @Operation(summary = "Get payment by transaction ID", 
            description = "Retrieves a payment by its transaction ID")
    @ApiResponse(responseCode = "200", description = "Payment found", 
            content = @Content(schema = @Schema(implementation = WalkInPayment.class)))
    @ApiResponse(responseCode = "404", description = "Payment not found")
    public ResponseEntity<WalkInPayment> getPaymentByTransactionId(
            @Parameter(description = "Transaction ID", required = true) @PathVariable String transactionId) {
        log.debug("REST request to get payment with transaction ID: {}", transactionId);
        return paymentService.getPaymentByTransactionId(transactionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/receipt/{receiptNumber}")
    @Operation(summary = "Get payment by receipt number", 
            description = "Retrieves a payment by its receipt number")
    @ApiResponse(responseCode = "200", description = "Payment found", 
            content = @Content(schema = @Schema(implementation = WalkInPayment.class)))
    @ApiResponse(responseCode = "404", description = "Payment not found")
    public ResponseEntity<WalkInPayment> getPaymentByReceiptNumber(
            @Parameter(description = "Receipt number", required = true) @PathVariable String receiptNumber) {
        log.debug("REST request to get payment with receipt number: {}", receiptNumber);
        return paymentService.getPaymentByReceiptNumber(receiptNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/shipment/{shipmentId}")
    @Operation(summary = "Get payment by shipment ID", 
            description = "Retrieves a payment for a specific shipment")
    @ApiResponse(responseCode = "200", description = "Payment found", 
            content = @Content(schema = @Schema(implementation = WalkInPayment.class)))
    @ApiResponse(responseCode = "404", description = "Payment not found")
    public ResponseEntity<WalkInPayment> getPaymentByShipmentId(
            @Parameter(description = "ID of the shipment", required = true) @PathVariable Long shipmentId) {
        log.debug("REST request to get payment for shipment with ID: {}", shipmentId);
        return paymentService.getPaymentByShipmentId(shipmentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a new payment", description = "Creates a new payment")
    @ApiResponse(responseCode = "201", description = "Payment created successfully", 
            content = @Content(schema = @Schema(implementation = WalkInPayment.class)))
    public ResponseEntity<WalkInPayment> createPayment(
            @Parameter(description = "Payment details", required = true) 
            @Valid @RequestBody WalkInPayment payment) {
        log.debug("REST request to create a new payment");
        WalkInPayment createdPayment = paymentService.createPayment(payment);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPayment);
    }

    @PostMapping("/process")
    @Operation(summary = "Process a payment", 
            description = "Processes a payment for a shipment")
    @ApiResponse(responseCode = "200", description = "Payment processed successfully", 
            content = @Content(schema = @Schema(implementation = WalkInPayment.class)))
    @ApiResponse(responseCode = "404", description = "Shipment not found")
    @ApiResponse(responseCode = "400", description = "Invalid payment data")
    public ResponseEntity<WalkInPayment> processPayment(
            @Parameter(description = "ID of the shipment", required = true) 
            @RequestParam Long shipmentId,
            @Parameter(description = "Payment method", required = true) 
            @RequestParam PaymentMethod paymentMethod,
            @Parameter(description = "ID of the staff member processing the payment") 
            @RequestParam(required = false) Long staffId,
            @Parameter(description = "Payment details", required = true) 
            @RequestBody Map<String, Object> paymentDetails) {
        log.debug("REST request to process payment for shipment with ID: {} using method: {}", 
                shipmentId, paymentMethod);
        
        try {
            // Get the shipment
            WalkInShipment shipment = shipmentService.getShipmentById(shipmentId)
                    .orElseThrow(() -> new IllegalArgumentException("Shipment not found with ID: " + shipmentId));
            
            // Process the payment
            WalkInPayment payment = paymentService.processPayment(shipment, paymentMethod, staffId, paymentDetails);
            
            // If payment was successful, update shipment status
            if (payment.getStatus() == PaymentStatus.COMPLETED) {
                shipment.setStatus(ShipmentStatus.PAID);
                shipmentService.updateShipment(shipmentId, shipment);
                
                // Send digital receipt
                Map<String, Object> receiptDetails = new HashMap<>();
                receiptDetails.put("amount", payment.getAmount().toString());
                receiptDetails.put("paymentMethod", payment.getPaymentMethod().name());
                receiptDetails.put("date", payment.getPaymentDate().toString());
                
                notificationService.sendDigitalReceipt(
                        shipment, payment.getReceiptNumber(), receiptDetails);
            }
            
            return ResponseEntity.ok(payment);
        } catch (IllegalArgumentException e) {
            log.error("Error processing payment: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error processing payment", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a payment", description = "Updates an existing payment")
    @ApiResponse(responseCode = "200", description = "Payment updated successfully", 
            content = @Content(schema = @Schema(implementation = WalkInPayment.class)))
    @ApiResponse(responseCode = "404", description = "Payment not found")
    public ResponseEntity<WalkInPayment> updatePayment(
            @Parameter(description = "ID of the payment", required = true) @PathVariable Long id,
            @Parameter(description = "Updated payment details", required = true) 
            @Valid @RequestBody WalkInPayment payment) {
        log.debug("REST request to update payment with ID: {}", id);
        try {
            WalkInPayment updatedPayment = paymentService.updatePayment(id, payment);
            return ResponseEntity.ok(updatedPayment);
        } catch (IllegalArgumentException e) {
            log.error("Payment not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a payment", description = "Deletes a payment")
    @ApiResponse(responseCode = "204", description = "Payment deleted successfully")
    @ApiResponse(responseCode = "404", description = "Payment not found")
    public ResponseEntity<Void> deletePayment(
            @Parameter(description = "ID of the payment", required = true) @PathVariable Long id) {
        log.debug("REST request to delete payment with ID: {}", id);
        try {
            paymentService.deletePayment(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("Payment not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update payment status", description = "Updates the status of a payment")
    @ApiResponse(responseCode = "200", description = "Status updated successfully", 
            content = @Content(schema = @Schema(implementation = WalkInPayment.class)))
    @ApiResponse(responseCode = "404", description = "Payment not found")
    public ResponseEntity<WalkInPayment> updatePaymentStatus(
            @Parameter(description = "ID of the payment", required = true) @PathVariable Long id,
            @Parameter(description = "New status", required = true) @RequestParam PaymentStatus status) {
        log.debug("REST request to update status of payment with ID: {} to: {}", id, status);
        try {
            WalkInPayment updatedPayment = paymentService.updatePaymentStatus(id, status);
            return ResponseEntity.ok(updatedPayment);
        } catch (IllegalArgumentException e) {
            log.error("Payment not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get payments by status", 
            description = "Retrieves all payments with a specific status")
    public ResponseEntity<List<WalkInPayment>> getPaymentsByStatus(
            @Parameter(description = "Payment status", required = true) @PathVariable PaymentStatus status) {
        log.debug("REST request to get payments with status: {}", status);
        List<WalkInPayment> payments = paymentService.getPaymentsByStatus(status);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/method/{method}")
    @Operation(summary = "Get payments by method", 
            description = "Retrieves all payments with a specific payment method")
    public ResponseEntity<List<WalkInPayment>> getPaymentsByMethod(
            @Parameter(description = "Payment method", required = true) @PathVariable PaymentMethod method) {
        log.debug("REST request to get payments with method: {}", method);
        List<WalkInPayment> payments = paymentService.getPaymentsByPaymentMethod(method);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get payments by date range", 
            description = "Retrieves all payments created within a date range")
    public ResponseEntity<List<WalkInPayment>> getPaymentsByDateRange(
            @Parameter(description = "Start date (ISO format)", required = true) 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date (ISO format)", required = true) 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.debug("REST request to get payments between dates: {} and {}", startDate, endDate);
        List<WalkInPayment> payments = paymentService.getPaymentsByDateRange(startDate, endDate);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/amount-range")
    @Operation(summary = "Get payments by amount range", 
            description = "Retrieves all payments with amount within a range")
    public ResponseEntity<List<WalkInPayment>> getPaymentsByAmountRange(
            @Parameter(description = "Minimum amount", required = true) 
            @RequestParam BigDecimal minAmount,
            @Parameter(description = "Maximum amount", required = true) 
            @RequestParam BigDecimal maxAmount) {
        log.debug("REST request to get payments with amount between: {} and {}", minAmount, maxAmount);
        List<WalkInPayment> payments = paymentService.getPaymentsByAmountRange(minAmount, maxAmount);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/staff/{staffId}")
    @Operation(summary = "Get payments by staff", 
            description = "Retrieves all payments processed by a specific staff member")
    public ResponseEntity<List<WalkInPayment>> getPaymentsByStaff(
            @Parameter(description = "ID of the staff member", required = true) @PathVariable Long staffId) {
        log.debug("REST request to get payments processed by staff with ID: {}", staffId);
        List<WalkInPayment> payments = paymentService.getPaymentsByStaff(staffId);
        return ResponseEntity.ok(payments);
    }

    @PostMapping("/{id}/refund")
    @Operation(summary = "Process a refund", description = "Processes a refund for a payment")
    @ApiResponse(responseCode = "200", description = "Refund processed successfully", 
            content = @Content(schema = @Schema(implementation = WalkInPayment.class)))
    @ApiResponse(responseCode = "404", description = "Payment not found")
    @ApiResponse(responseCode = "400", description = "Invalid refund amount")
    public ResponseEntity<WalkInPayment> processRefund(
            @Parameter(description = "ID of the payment", required = true) @PathVariable Long id,
            @Parameter(description = "Refund amount", required = true) @RequestParam BigDecimal refundAmount,
            @Parameter(description = "Reason for refund", required = true) @RequestParam String reason) {
        log.debug("REST request to process refund of amount: {} for payment with ID: {}", refundAmount, id);
        try {
            WalkInPayment refundedPayment = paymentService.processRefund(id, refundAmount, reason);
            
            // Update shipment status if payment is fully refunded
            if (refundedPayment.getStatus() == PaymentStatus.REFUNDED) {
                WalkInShipment shipment = refundedPayment.getShipment();
                shipment.setStatus(ShipmentStatus.CANCELLED);
                shipmentService.updateShipment(shipment.getId(), shipment);
            }
            
            return ResponseEntity.ok(refundedPayment);
        } catch (IllegalArgumentException e) {
            log.error("Error processing refund: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            log.error("Error processing refund", e);
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/mark-failed")
    @Operation(summary = "Mark payment as failed", 
            description = "Marks a payment as failed with a reason")
    @ApiResponse(responseCode = "200", description = "Payment marked as failed successfully", 
            content = @Content(schema = @Schema(implementation = WalkInPayment.class)))
    @ApiResponse(responseCode = "404", description = "Payment not found")
    public ResponseEntity<WalkInPayment> markPaymentAsFailed(
            @Parameter(description = "ID of the payment", required = true) @PathVariable Long id,
            @Parameter(description = "Failure reason", required = true) @RequestParam String failureReason) {
        log.debug("REST request to mark payment with ID: {} as failed", id);
        try {
            WalkInPayment failedPayment = paymentService.markPaymentAsFailed(id, failureReason);
            return ResponseEntity.ok(failedPayment);
        } catch (IllegalArgumentException e) {
            log.error("Payment not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/with-refunds")
    @Operation(summary = "Get payments with refunds", 
            description = "Retrieves all payments that have been refunded")
    public ResponseEntity<List<WalkInPayment>> getPaymentsWithRefunds() {
        log.debug("REST request to get payments with refunds");
        List<WalkInPayment> payments = paymentService.findPaymentsWithRefunds();
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/refund-date-range")
    @Operation(summary = "Get payments by refund date range", 
            description = "Retrieves all payments refunded within a date range")
    public ResponseEntity<List<WalkInPayment>> getPaymentsByRefundDateRange(
            @Parameter(description = "Start date (ISO format)", required = true) 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date (ISO format)", required = true) 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.debug("REST request to get payments refunded between dates: {} and {}", startDate, endDate);
        List<WalkInPayment> payments = paymentService.findPaymentsByRefundDateRange(startDate, endDate);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/card-type/{cardType}")
    @Operation(summary = "Get payments by card type", 
            description = "Retrieves all payments with a specific card type")
    public ResponseEntity<List<WalkInPayment>> getPaymentsByCardType(
            @Parameter(description = "Card type", required = true) @PathVariable String cardType) {
        log.debug("REST request to get payments with card type: {}", cardType);
        List<WalkInPayment> payments = paymentService.findPaymentsByCardType(cardType);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/revenue")
    @Operation(summary = "Calculate revenue", 
            description = "Calculates total revenue by payment method within a date range")
    public ResponseEntity<BigDecimal> calculateRevenue(
            @Parameter(description = "Payment method", required = true) 
            @RequestParam PaymentMethod paymentMethod,
            @Parameter(description = "Start date (ISO format)", required = true) 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date (ISO format)", required = true) 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.debug("REST request to calculate revenue for payment method: {} between dates: {} and {}", 
                paymentMethod, startDate, endDate);
        BigDecimal revenue = paymentService.calculateRevenueByPaymentMethodAndDateRange(
                paymentMethod, startDate, endDate);
        return ResponseEntity.ok(revenue);
    }

    @GetMapping("/refunds")
    @Operation(summary = "Calculate total refunds", 
            description = "Calculates total refunds within a date range")
    public ResponseEntity<BigDecimal> calculateTotalRefunds(
            @Parameter(description = "Start date (ISO format)", required = true) 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date (ISO format)", required = true) 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.debug("REST request to calculate refunds between dates: {} and {}", startDate, endDate);
        BigDecimal refunds = paymentService.calculateTotalRefundsByDateRange(startDate, endDate);
        return ResponseEntity.ok(refunds);
    }

    @GetMapping("/requiring-follow-up")
    @Operation(summary = "Get payments requiring follow-up", 
            description = "Retrieves all payments requiring follow-up (pending or on hold)")
    public ResponseEntity<List<WalkInPayment>> getPaymentsRequiringFollowUp() {
        log.debug("REST request to get payments requiring follow-up");
        List<WalkInPayment> payments = paymentService.findPaymentsRequiringFollowUp();
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/with-issues")
    @Operation(summary = "Get payments with issues", 
            description = "Retrieves all payments with issues (failed or disputed)")
    public ResponseEntity<List<WalkInPayment>> getPaymentsWithIssues() {
        log.debug("REST request to get payments with issues");
        List<WalkInPayment> payments = paymentService.findPaymentsWithIssues();
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/counts/by-method")
    @Operation(summary = "Get payment counts by method", 
            description = "Retrieves the count of payments by payment method")
    public ResponseEntity<Map<PaymentMethod, Long>> getPaymentCountsByMethod() {
        log.debug("REST request to get payment counts by method");
        Map<PaymentMethod, Long> counts = paymentService.getPaymentCountsByPaymentMethod();
        return ResponseEntity.ok(counts);
    }

    @GetMapping("/counts/by-status")
    @Operation(summary = "Get payment counts by status", 
            description = "Retrieves the count of payments by status")
    public ResponseEntity<Map<PaymentStatus, Long>> getPaymentCountsByStatus() {
        log.debug("REST request to get payment counts by status");
        Map<PaymentStatus, Long> counts = paymentService.getPaymentCountsByStatus();
        return ResponseEntity.ok(counts);
    }

    @GetMapping("/counts/by-card-type")
    @Operation(summary = "Get payment counts by card type", 
            description = "Retrieves the count of payments by card type")
    public ResponseEntity<Map<String, Long>> getPaymentCountsByCardType() {
        log.debug("REST request to get payment counts by card type");
        Map<String, Long> counts = paymentService.getPaymentCountsByCardType();
        return ResponseEntity.ok(counts);
    }

    @GetMapping("/failed-percentage")
    @Operation(summary = "Calculate failed payment percentage", 
            description = "Calculates the percentage of failed payments within a time period")
    public ResponseEntity<Double> calculateFailedPaymentPercentage(
            @Parameter(description = "Start date (ISO format)", required = true) 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date (ISO format)", required = true) 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.debug("REST request to calculate failed payment percentage between dates: {} and {}", 
                startDate, endDate);
        Double percentage = paymentService.calculateFailedPaymentPercentage(startDate, endDate);
        return ResponseEntity.ok(percentage);
    }

    @PostMapping("/generate-receipt-number")
    @Operation(summary = "Generate receipt number", 
            description = "Generates a receipt number for a payment")
    @ApiResponse(responseCode = "200", description = "Receipt number generated successfully")
    @ApiResponse(responseCode = "404", description = "Payment not found")
    public ResponseEntity<String> generateReceiptNumber(
            @Parameter(description = "ID of the payment", required = true) @RequestParam Long paymentId) {
        log.debug("REST request to generate receipt number for payment with ID: {}", paymentId);
        try {
            WalkInPayment payment = paymentService.getPaymentById(paymentId)
                    .orElseThrow(() -> new IllegalArgumentException("Payment not found with ID: " + paymentId));
            
            String receiptNumber = paymentService.generateReceiptNumber(payment);
            return ResponseEntity.ok(receiptNumber);
        } catch (IllegalArgumentException e) {
            log.error("Payment not found with ID: {}", paymentId);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/generate-transaction-id")
    @Operation(summary = "Generate transaction ID", 
            description = "Generates a unique transaction ID for a new payment")
    public ResponseEntity<String> generateTransactionId() {
        log.debug("REST request to generate transaction ID");
        String transactionId = paymentService.generateTransactionId();
        return ResponseEntity.ok(transactionId);
    }
}
