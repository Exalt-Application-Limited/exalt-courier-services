package com.gogidix.courier.billing.controller;

import com.gogidix.courier.billing.dto.*;
import com.gogidix.courier.billing.model.InvoiceStatus;
import com.gogidix.courier.billing.service.CourierBillingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST Controller for Courier Billing operations.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/billing")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Courier Billing", description = "Courier billing and invoice management APIs")
public class CourierBillingController {

    private final CourierBillingService billingService;

    // Invoice Management APIs

    @PostMapping("/invoices/shipment")
    @Operation(summary = "Create shipment invoice", description = "Create a new invoice for a shipment")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Invoice created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<InvoiceResponse> createShipmentInvoice(
            @Valid @RequestBody CreateShipmentInvoiceRequest request) {
        log.info("Creating shipment invoice for customer: {}", request.customerId());
        InvoiceResponse response = billingService.createShipmentInvoice(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/invoices/bulk")
    @Operation(summary = "Create bulk invoice", description = "Create a single invoice for multiple shipments")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Bulk invoice created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<InvoiceResponse> createBulkInvoice(
            @Valid @RequestBody CreateBulkInvoiceRequest request) {
        log.info("Creating bulk invoice for customer: {} with {} shipments", 
                request.customerId(), request.shipmentIds().size());
        InvoiceResponse response = billingService.createBulkInvoice(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/invoices/subscription")
    @Operation(summary = "Create subscription invoice", description = "Create a new subscription invoice")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Subscription invoice created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "Subscription not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<InvoiceResponse> createSubscriptionInvoice(
            @Valid @RequestBody CreateSubscriptionInvoiceRequest request) {
        log.info("Creating subscription invoice for subscription: {}", request.subscriptionId());
        InvoiceResponse response = billingService.createSubscriptionInvoice(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/invoices/{invoiceId}")
    @Operation(summary = "Get invoice", description = "Retrieve invoice by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Invoice retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Invoice not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<InvoiceResponse> getInvoice(
            @Parameter(description = "Invoice ID") @PathVariable String invoiceId) {
        log.info("Retrieving invoice: {}", invoiceId);
        InvoiceResponse response = billingService.getInvoice(invoiceId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/invoices/{invoiceId}")
    @Operation(summary = "Update invoice", description = "Update invoice details")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Invoice updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "Invoice not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<InvoiceResponse> updateInvoice(
            @Parameter(description = "Invoice ID") @PathVariable String invoiceId,
            @Valid @RequestBody UpdateInvoiceRequest request) {
        log.info("Updating invoice: {}", invoiceId);
        InvoiceResponse response = billingService.updateInvoice(invoiceId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/invoices/{invoiceId}/finalize")
    @Operation(summary = "Finalize invoice", description = "Finalize and send invoice to customer")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Invoice finalized successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid invoice status"),
        @ApiResponse(responseCode = "404", description = "Invoice not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> finalizeInvoice(
            @Parameter(description = "Invoice ID") @PathVariable String invoiceId) {
        log.info("Finalizing invoice: {}", invoiceId);
        billingService.finalizeInvoice(invoiceId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/invoices/{invoiceId}/send")
    @Operation(summary = "Send invoice", description = "Send invoice to specified recipients")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Invoice sent successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "Invoice not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> sendInvoice(
            @Parameter(description = "Invoice ID") @PathVariable String invoiceId,
            @Valid @RequestBody SendInvoiceRequest request) {
        log.info("Sending invoice: {} to {} recipients", invoiceId, request.recipients().size());
        billingService.sendInvoice(invoiceId, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/invoices/{invoiceId}/cancel")
    @Operation(summary = "Cancel invoice", description = "Cancel an invoice")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Invoice cancelled successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid invoice status"),
        @ApiResponse(responseCode = "404", description = "Invoice not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> cancelInvoice(
            @Parameter(description = "Invoice ID") @PathVariable String invoiceId,
            @Parameter(description = "Cancellation reason") @RequestParam String reason) {
        log.info("Cancelling invoice: {} with reason: {}", invoiceId, reason);
        billingService.cancelInvoice(invoiceId, reason);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/invoices/customer/{customerId}")
    @Operation(summary = "Get customer invoices", description = "Get all invoices for a customer")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Invoices retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<InvoiceResponse>> getCustomerInvoices(
            @Parameter(description = "Customer ID") @PathVariable String customerId,
            @Parameter(description = "Invoice filter") @ModelAttribute InvoiceFilterRequest filter) {
        log.info("Getting invoices for customer: {}", customerId);
        List<InvoiceResponse> response = billingService.getCustomerInvoices(customerId, filter);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/invoices")
    @Operation(summary = "Get invoices by status and date range", description = "Get invoices filtered by status and date range")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Invoices retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<InvoiceResponse>> getInvoicesByStatusAndDateRange(
            @Parameter(description = "Invoice status") @RequestParam InvoiceStatus status,
            @Parameter(description = "From date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @Parameter(description = "To date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate) {
        log.info("Getting invoices by status: {} and date range: {} to {}", status, fromDate, toDate);
        List<InvoiceResponse> response = billingService.getInvoicesByStatusAndDateRange(status, fromDate, toDate);
        return ResponseEntity.ok(response);
    }

    // Payment Management APIs

    @PostMapping("/invoices/{invoiceId}/payments/process")
    @Operation(summary = "Process payment", description = "Process payment for an invoice")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Payment processed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "Invoice not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PaymentResponse> processPayment(
            @Parameter(description = "Invoice ID") @PathVariable String invoiceId,
            @Valid @RequestBody ProcessPaymentRequest request) {
        log.info("Processing payment for invoice: {}", invoiceId);
        PaymentResponse response = billingService.processPayment(invoiceId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/invoices/{invoiceId}/payments/manual")
    @Operation(summary = "Record manual payment", description = "Record a manual payment for an invoice")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Manual payment recorded successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "Invoice not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PaymentResponse> recordManualPayment(
            @Parameter(description = "Invoice ID") @PathVariable String invoiceId,
            @Valid @RequestBody RecordManualPaymentRequest request) {
        log.info("Recording manual payment for invoice: {}", invoiceId);
        PaymentResponse response = billingService.recordManualPayment(invoiceId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/invoices/{invoiceId}/payments/automatic")
    @Operation(summary = "Initiate automatic payment", description = "Initiate automatic payment for an invoice")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Automatic payment initiated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid invoice status or no default payment method"),
        @ApiResponse(responseCode = "404", description = "Invoice not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PaymentResponse> initiateAutomaticPayment(
            @Parameter(description = "Invoice ID") @PathVariable String invoiceId) {
        log.info("Initiating automatic payment for invoice: {}", invoiceId);
        PaymentResponse response = billingService.initiateAutomaticPayment(invoiceId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/invoices/{invoiceId}/payments")
    @Operation(summary = "Get invoice payments", description = "Get all payments for an invoice")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Payments retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Invoice not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<PaymentResponse>> getInvoicePayments(
            @Parameter(description = "Invoice ID") @PathVariable String invoiceId) {
        log.info("Getting payments for invoice: {}", invoiceId);
        List<PaymentResponse> response = billingService.getInvoicePayments(invoiceId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/payments/customer/{customerId}")
    @Operation(summary = "Get customer payments", description = "Get all payments for a customer")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Payments retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<PaymentResponse>> getCustomerPayments(
            @Parameter(description = "Customer ID") @PathVariable String customerId,
            @Parameter(description = "Payment filter") @ModelAttribute PaymentFilterRequest filter) {
        log.info("Getting payments for customer: {}", customerId);
        List<PaymentResponse> response = billingService.getCustomerPayments(customerId, filter);
        return ResponseEntity.ok(response);
    }

    // Refund Management APIs

    @PostMapping("/payments/{paymentId}/refund")
    @Operation(summary = "Process refund", description = "Process a refund for a payment")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Refund processed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "Payment not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<RefundResponse> processRefund(
            @Parameter(description = "Payment ID") @PathVariable String paymentId,
            @Valid @RequestBody ProcessRefundRequest request) {
        log.info("Processing refund for payment: {}", paymentId);
        RefundResponse response = billingService.processRefund(paymentId, request);
        return ResponseEntity.ok(response);
    }

    // Pricing and Calculation APIs

    @PostMapping("/pricing/calculate")
    @Operation(summary = "Calculate shipping charges", description = "Calculate shipping charges for a shipment")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Charges calculated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PricingCalculationResponse> calculateShippingCharges(
            @Valid @RequestBody PricingCalculationRequest request) {
        log.info("Calculating shipping charges for service: {}", request.serviceType());
        PricingCalculationResponse response = billingService.calculateShippingCharges(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/taxes/calculate")
    @Operation(summary = "Calculate taxes", description = "Calculate taxes for an amount")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Taxes calculated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<TaxCalculationResponse> calculateTaxes(
            @Valid @RequestBody TaxCalculationRequest request) {
        log.info("Calculating taxes for amount: {}", request.amount());
        TaxCalculationResponse response = billingService.calculateTaxes(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pricing/customer/{customerId}/tier")
    @Operation(summary = "Get customer pricing tier", description = "Get pricing tier for a customer")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pricing tier retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Customer not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PricingTierResponse> getCustomerPricingTier(
            @Parameter(description = "Customer ID") @PathVariable String customerId) {
        log.info("Getting pricing tier for customer: {}", customerId);
        PricingTierResponse response = billingService.getCustomerPricingTier(customerId);
        return ResponseEntity.ok(response);
    }

    // Utility APIs

    @GetMapping("/invoices/{invoiceId}/overdue")
    @Operation(summary = "Check if invoice is overdue", description = "Check if an invoice is overdue")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Overdue status retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Invoice not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Boolean> isInvoiceOverdue(
            @Parameter(description = "Invoice ID") @PathVariable String invoiceId) {
        boolean isOverdue = billingService.isInvoiceOverdue(invoiceId);
        return ResponseEntity.ok(isOverdue);
    }
}