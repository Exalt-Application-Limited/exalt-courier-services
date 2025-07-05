package com.gogidix.courier.billing.service.impl;

import com.gogidix.courier.billing.client.PaymentProcessingServiceClient;
import com.gogidix.courier.billing.client.NotificationServiceClient;
import com.gogidix.courier.billing.client.TaxCalculationServiceClient;
import com.gogidix.courier.billing.client.CurrencyExchangeServiceClient;
import com.gogidix.courier.billing.dto.*;
import com.gogidix.courier.billing.exception.BillingException;
import com.gogidix.courier.billing.exception.ResourceNotFoundException;
import com.gogidix.courier.billing.model.*;
import com.gogidix.courier.billing.repository.*;
import com.gogidix.courier.billing.service.CourierBillingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Async;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.concurrent.CompletableFuture;

/**
 * Implementation of Courier Billing Service.
 * 
 * This service provides comprehensive billing functionality with
 * invoice management, payment processing, subscription billing,
 * multi-currency support, and financial reporting.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CourierBillingServiceImpl implements CourierBillingService {

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final CustomerCreditRepository customerCreditRepository;
    private final BillingDisputeRepository disputeRepository;
    private final BillingAuditRepository auditRepository;
    private final PricingTierRepository pricingTierRepository;
    
    private final PaymentProcessingServiceClient paymentProcessingClient;
    private final NotificationServiceClient notificationServiceClient;
    private final TaxCalculationServiceClient taxCalculationClient;
    private final CurrencyExchangeServiceClient currencyExchangeClient;
    
    private static final String INVOICE_PREFIX = "INV";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MMdd");
    private static final BigDecimal DEFAULT_LATE_FEE_RATE = new BigDecimal("0.015"); // 1.5% monthly

    @Override
    public InvoiceResponse createShipmentInvoice(CreateShipmentInvoiceRequest request) {
        log.info("Creating shipment invoice for customer: {} and shipment: {}", 
                request.customerId(), request.shipmentId());
        
        // Calculate pricing with discounts
        PricingCalculationResponse pricing = calculateShippingCharges(new PricingCalculationRequest(
                request.serviceType(),
                request.weight(),
                request.dimensions(),
                request.origin(),
                request.destination(),
                request.declaredValue(),
                request.customerId()
        ));
        
        // Apply volume discounts if applicable
        DiscountApplicationResponse discounts = applyVolumeDiscounts(
                request.customerId(), 
                pricing.baseAmount(), 
                getCustomerMonthlyShipmentCount(request.customerId())
        );
        
        // Calculate taxes
        TaxCalculationResponse taxes = calculateTaxes(new TaxCalculationRequest(
                request.billingAddress(),
                discounts.finalAmount(),
                request.serviceType(),
                "SHIPMENT"
        ));
        
        // Create invoice
        Invoice invoice = Invoice.builder()
                .invoiceNumber(generateInvoiceNumber())
                .customerId(request.customerId())
                .customerName(request.customerName())
                .customerEmail(request.customerEmail())
                .billingAddress(request.billingAddress())
                .shipmentId(request.shipmentId())
                .serviceType(request.serviceType())
                .description(request.description())
                .subtotal(pricing.baseAmount())
                .discountAmount(discounts.discountAmount())
                .taxAmount(taxes.totalTax())
                .totalAmount(discounts.finalAmount().add(taxes.totalTax()))
                .currency(request.currency())
                .status(InvoiceStatus.DRAFT)
                .dueDate(calculateDueDate(LocalDateTime.now(), getCustomerPaymentTerms(request.customerId())))
                .createdBy("SYSTEM")
                .build();
        
        Invoice savedInvoice = invoiceRepository.save(invoice);
        
        // Create invoice line items
        createInvoiceLineItems(savedInvoice, pricing, discounts, taxes);
        
        // Create audit entry
        createBillingAuditEntry(savedInvoice.getId(), "INVOICE_CREATED", 
                              "Invoice created for shipment", "SYSTEM");
        
        log.info("Shipment invoice created with number: {}", savedInvoice.getInvoiceNumber());
        
        return mapInvoiceToResponse(savedInvoice);
    }

    @Override
    public InvoiceResponse createBulkInvoice(CreateBulkInvoiceRequest request) {
        log.info("Creating bulk invoice for customer: {} with {} shipments", 
                request.customerId(), request.shipmentIds().size());
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        
        // Calculate totals for all shipments
        for (String shipmentId : request.shipmentIds()) {
            // Get shipment details and calculate charges
            // This would integrate with shipment service to get details
            BigDecimal shipmentAmount = calculateShipmentCharges(shipmentId, request.customerId());
            totalAmount = totalAmount.add(shipmentAmount);
        }
        
        // Apply bulk discounts
        DiscountApplicationResponse bulkDiscounts = applyVolumeDiscounts(
                request.customerId(), totalAmount, request.shipmentIds().size());
        totalDiscount = bulkDiscounts.discountAmount();
        
        // Calculate taxes on discounted amount
        TaxCalculationResponse taxes = calculateTaxes(new TaxCalculationRequest(
                request.billingAddress(),
                bulkDiscounts.finalAmount(),
                "BULK_SHIPMENT",
                "BULK"
        ));
        totalTax = taxes.totalTax();
        
        // Create bulk invoice
        Invoice invoice = Invoice.builder()
                .invoiceNumber(generateInvoiceNumber())
                .customerId(request.customerId())
                .customerName(request.customerName())
                .customerEmail(request.customerEmail())
                .billingAddress(request.billingAddress())
                .description("Bulk invoice for " + request.shipmentIds().size() + " shipments")
                .subtotal(totalAmount)
                .discountAmount(totalDiscount)
                .taxAmount(totalTax)
                .totalAmount(bulkDiscounts.finalAmount().add(totalTax))
                .currency(request.currency())
                .status(InvoiceStatus.DRAFT)
                .dueDate(calculateDueDate(LocalDateTime.now(), getCustomerPaymentTerms(request.customerId())))
                .invoiceType("BULK")
                .createdBy("SYSTEM")
                .build();
        
        Invoice savedInvoice = invoiceRepository.save(invoice);
        
        // Create line items for each shipment
        createBulkInvoiceLineItems(savedInvoice, request.shipmentIds(), bulkDiscounts, taxes);
        
        log.info("Bulk invoice created with number: {}", savedInvoice.getInvoiceNumber());
        
        return mapInvoiceToResponse(savedInvoice);
    }

    @Override
    public InvoiceResponse createSubscriptionInvoice(CreateSubscriptionInvoiceRequest request) {
        log.info("Creating subscription invoice for subscription: {}", request.subscriptionId());
        
        Subscription subscription = subscriptionRepository.findById(request.subscriptionId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Subscription not found: " + request.subscriptionId()));
        
        // Calculate subscription charges
        BigDecimal subscriptionAmount = subscription.getMonthlyAmount();
        
        // Apply subscription discounts if any
        if (subscription.getDiscountPercentage() != null) {
            BigDecimal discountAmount = subscriptionAmount
                    .multiply(subscription.getDiscountPercentage())
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            subscriptionAmount = subscriptionAmount.subtract(discountAmount);
        }
        
        // Calculate taxes
        TaxCalculationResponse taxes = calculateTaxes(new TaxCalculationRequest(
                subscription.getBillingAddress(),
                subscriptionAmount,
                subscription.getServicePlan(),
                "SUBSCRIPTION"
        ));
        
        // Create subscription invoice
        Invoice invoice = Invoice.builder()
                .invoiceNumber(generateInvoiceNumber())
                .customerId(subscription.getCustomerId())
                .customerName(subscription.getCustomerName())
                .customerEmail(subscription.getCustomerEmail())
                .billingAddress(subscription.getBillingAddress())
                .subscriptionId(subscription.getId())
                .description("Subscription: " + subscription.getServicePlan())
                .subtotal(subscription.getMonthlyAmount())
                .discountAmount(subscription.getMonthlyAmount().subtract(subscriptionAmount))
                .taxAmount(taxes.totalTax())
                .totalAmount(subscriptionAmount.add(taxes.totalTax()))
                .currency(subscription.getCurrency())
                .status(InvoiceStatus.DRAFT)
                .invoiceType("SUBSCRIPTION")
                .dueDate(calculateDueDate(LocalDateTime.now(), "NET_30"))
                .createdBy("SYSTEM")
                .build();
        
        Invoice savedInvoice = invoiceRepository.save(invoice);
        
        // Update subscription billing date
        subscription.setLastBilledAt(LocalDateTime.now());
        subscription.setNextBillingDate(subscription.getNextBillingDate().plusMonths(1));
        subscriptionRepository.save(subscription);
        
        // Auto-finalize subscription invoices
        finalizeInvoice(savedInvoice.getInvoiceNumber());
        
        log.info("Subscription invoice created with number: {}", savedInvoice.getInvoiceNumber());
        
        return mapInvoiceToResponse(savedInvoice);
    }

    @Override
    public InvoiceResponse getInvoice(String invoiceId) {
        log.info("Retrieving invoice: {}", invoiceId);
        
        Invoice invoice = invoiceRepository.findByInvoiceNumber(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found: " + invoiceId));
        
        return mapInvoiceToResponse(invoice);
    }

    @Override
    public void finalizeInvoice(String invoiceId) {
        log.info("Finalizing invoice: {}", invoiceId);
        
        Invoice invoice = invoiceRepository.findByInvoiceNumber(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found: " + invoiceId));
        
        if (invoice.getStatus() != InvoiceStatus.DRAFT) {
            throw new BillingException("Only draft invoices can be finalized");
        }
        
        updateInvoiceStatus(invoice, InvoiceStatus.SENT, "Invoice finalized and sent to customer", "SYSTEM");
        
        invoice.setSentAt(LocalDateTime.now());
        invoiceRepository.save(invoice);
        
        // Send invoice to customer
        sendInvoiceToCustomer(invoice);
        
        // Check for automatic payment if enabled
        if (isAutomaticPaymentEnabled(invoice.getCustomerId())) {
            CompletableFuture.runAsync(() -> {
                try {
                    Thread.sleep(30000); // Wait 30 seconds before attempting auto-payment
                    initiateAutomaticPayment(invoiceId);
                } catch (Exception e) {
                    log.error("Failed to initiate automatic payment for invoice: {}", invoiceId, e);
                }
            });
        }
        
        log.info("Invoice finalized: {}", invoiceId);
    }

    @Override
    public PaymentResponse recordManualPayment(String invoiceId, RecordManualPaymentRequest request) {
        log.info("Recording manual payment for invoice: {}", invoiceId);
        
        Invoice invoice = invoiceRepository.findByInvoiceNumber(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found: " + invoiceId));
        
        if (invoice.getStatus() == InvoiceStatus.PAID || invoice.getStatus() == InvoiceStatus.CANCELLED) {
            throw new BillingException("Cannot record payment for invoice in status: " + invoice.getStatus());
        }
        
        // Create manual payment record
        Payment payment = Payment.builder()
                .paymentId(UUID.randomUUID().toString())
                .invoice(invoice)
                .customerId(invoice.getCustomerId())
                .amount(request.amount())
                .currency(request.currency())
                .paymentMethodType("MANUAL")
                .status(PaymentStatus.COMPLETED)
                .processedAt(LocalDateTime.now())
                .notes(request.notes())
                .createdBy(request.recordedBy())
                .build();
        
        Payment savedPayment = paymentRepository.save(payment);
        
        // Update invoice status based on payment amount
        BigDecimal totalPaid = getTotalPaidAmount(invoiceId).add(request.amount());
        
        if (totalPaid.compareTo(invoice.getTotalAmount()) >= 0) {
            updateInvoiceStatus(invoice, InvoiceStatus.PAID, "Manual payment - fully paid", request.recordedBy());
            invoice.setPaidAt(LocalDateTime.now());
        } else {
            updateInvoiceStatus(invoice, InvoiceStatus.PARTIALLY_PAID, "Manual payment - partial", request.recordedBy());
        }
        
        invoiceRepository.save(invoice);
        
        // Create audit entry
        createBillingAuditEntry(invoice.getId(), "MANUAL_PAYMENT", 
                              "Manual payment recorded: " + request.amount() + " " + request.currency(), 
                              request.recordedBy());
        
        // Send payment confirmation if customer email provided
        if (invoice.getCustomerEmail() != null) {
            sendPaymentConfirmation(invoice, savedPayment);
        }
        
        log.info("Manual payment recorded for invoice: {} with amount: {}", 
                invoiceId, request.amount());
        
        return mapPaymentToResponse(savedPayment);
    }
    
    @Override
    public PaymentResponse initiateAutomaticPayment(String invoiceId) {
        log.info("Initiating automatic payment for invoice: {}", invoiceId);
        
        Invoice invoice = invoiceRepository.findByInvoiceNumber(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found: " + invoiceId));
        
        if (invoice.getStatus() == InvoiceStatus.PAID || invoice.getStatus() == InvoiceStatus.CANCELLED) {
            throw new BillingException("Cannot process automatic payment for invoice in status: " + invoice.getStatus());
        }
        
        // Get customer's default payment method
        String defaultPaymentMethodId = getCustomerDefaultPaymentMethod(invoice.getCustomerId());
        if (defaultPaymentMethodId == null) {
            throw new BillingException("No default payment method found for customer: " + invoice.getCustomerId());
        }
        
        // Process automatic payment
        PaymentProcessingServiceClient.ProcessPaymentRequest paymentRequest = 
                new PaymentProcessingServiceClient.ProcessPaymentRequest(
                        invoice.getTotalAmount(),
                        invoice.getCurrency(),
                        defaultPaymentMethodId,
                        invoice.getCustomerId(),
                        invoice.getInvoiceNumber(),
                        "AUTOMATIC_PAYMENT"
                );
        
        try {
            PaymentProcessingServiceClient.PaymentResult paymentResult = 
                    paymentProcessingClient.processPayment(paymentRequest);
            
            // Create payment record
            Payment payment = Payment.builder()
                    .paymentId(paymentResult.paymentId())
                    .invoice(invoice)
                    .customerId(invoice.getCustomerId())
                    .amount(invoice.getTotalAmount())
                    .currency(invoice.getCurrency())
                    .paymentMethodId(defaultPaymentMethodId)
                    .paymentMethodType("AUTOMATIC")
                    .status(PaymentStatus.valueOf(paymentResult.status()))
                    .gatewayTransactionId(paymentResult.transactionId())
                    .gatewayResponse(paymentResult.gatewayResponse())
                    .processedAt(LocalDateTime.now())
                    .createdBy("SYSTEM_AUTO")
                    .build();
            
            Payment savedPayment = paymentRepository.save(payment);
            
            // Update invoice status if payment successful
            if (PaymentStatus.COMPLETED.equals(savedPayment.getStatus())) {
                updateInvoiceStatus(invoice, InvoiceStatus.PAID, "Automatic payment completed", "SYSTEM_AUTO");
                invoice.setPaidAt(LocalDateTime.now());
                invoiceRepository.save(invoice);
                
                // Send payment confirmation
                sendPaymentConfirmation(invoice, savedPayment);
                
                log.info("Automatic payment successful for invoice: {}", invoiceId);
            } else {
                // Payment failed, update invoice to overdue if past due date
                if (invoice.getDueDate() != null && invoice.getDueDate().isBefore(LocalDateTime.now())) {
                    updateInvoiceStatus(invoice, InvoiceStatus.OVERDUE, "Automatic payment failed", "SYSTEM_AUTO");
                    invoiceRepository.save(invoice);
                }
                
                // Send payment failure notification
                sendPaymentFailureNotification(invoice, savedPayment);
                
                log.warn("Automatic payment failed for invoice: {} - Reason: {}", 
                        invoiceId, paymentResult.failureReason());
            }
            
            return mapPaymentToResponse(savedPayment);
            
        } catch (Exception e) {
            log.error("Failed to process automatic payment for invoice: {}", invoiceId, e);
            
            // Create failed payment record
            Payment failedPayment = Payment.builder()
                    .paymentId(UUID.randomUUID().toString())
                    .invoice(invoice)
                    .customerId(invoice.getCustomerId())
                    .amount(invoice.getTotalAmount())
                    .currency(invoice.getCurrency())
                    .paymentMethodId(defaultPaymentMethodId)
                    .paymentMethodType("AUTOMATIC")
                    .status(PaymentStatus.FAILED)
                    .failureReason("Payment processing error: " + e.getMessage())
                    .processedAt(LocalDateTime.now())
                    .createdBy("SYSTEM_AUTO")
                    .build();
            
            Payment savedFailedPayment = paymentRepository.save(failedPayment);
            
            // Send failure notification
            sendPaymentFailureNotification(invoice, savedFailedPayment);
            
            throw new BillingException("Automatic payment failed: " + e.getMessage());
        }
    }
    
    @Override
    public RefundResponse processRefund(String paymentId, ProcessRefundRequest request) {
        log.info("Processing refund for payment: {} with amount: {}", paymentId, request.refundAmount());
        
        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + paymentId));
        
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new BillingException("Can only refund completed payments");
        }
        
        // Validate refund amount
        if (request.refundAmount().compareTo(payment.getAmount()) > 0) {
            throw new BillingException("Refund amount cannot exceed original payment amount");
        }
        
        // Check if already refunded
        BigDecimal totalRefunded = getTotalRefundedAmount(paymentId);
        BigDecimal newTotalRefunded = totalRefunded.add(request.refundAmount());
        
        if (newTotalRefunded.compareTo(payment.getAmount()) > 0) {
            throw new BillingException("Total refund amount would exceed original payment");
        }
        
        try {
            // Process refund through payment gateway
            PaymentProcessingServiceClient.ProcessPaymentRequest refundRequest = 
                    new PaymentProcessingServiceClient.ProcessPaymentRequest(
                            request.refundAmount().negate(), // Negative amount for refund
                            payment.getCurrency(),
                            payment.getPaymentMethodId(),
                            payment.getCustomerId(),
                            "REFUND-" + payment.getGatewayTransactionId(),
                            "REFUND"
                    );
            
            PaymentProcessingServiceClient.PaymentResult refundResult = 
                    paymentProcessingClient.processPayment(refundRequest);
            
            // Create refund record
            Payment refund = Payment.builder()
                    .paymentId(refundResult.paymentId())
                    .invoice(payment.getInvoice())
                    .customerId(payment.getCustomerId())
                    .amount(request.refundAmount().negate()) // Store as negative amount
                    .currency(payment.getCurrency())
                    .paymentMethodId(payment.getPaymentMethodId())
                    .paymentMethodType("REFUND")
                    .status(PaymentStatus.valueOf(refundResult.status()))
                    .gatewayTransactionId(refundResult.transactionId())
                    .gatewayResponse(refundResult.gatewayResponse())
                    .originalPaymentId(paymentId)
                    .processedAt(LocalDateTime.now())
                    .notes("Refund: " + request.reason())
                    .createdBy(request.processedBy())
                    .build();
            
            Payment savedRefund = paymentRepository.save(refund);
            
            // Update invoice status if fully refunded
            Invoice invoice = payment.getInvoice();
            BigDecimal updatedTotalRefunded = getTotalRefundedAmount(paymentId);
            
            if (updatedTotalRefunded.compareTo(payment.getAmount()) >= 0) {
                updateInvoiceStatus(invoice, InvoiceStatus.REFUNDED, 
                                  "Invoice fully refunded: " + request.reason(), request.processedBy());
            } else {
                updateInvoiceStatus(invoice, InvoiceStatus.PARTIALLY_REFUNDED, 
                                  "Partial refund processed: " + request.reason(), request.processedBy());
            }
            
            invoiceRepository.save(invoice);
            
            // Create audit entry
            createBillingAuditEntry(invoice.getId(), "REFUND_PROCESSED", 
                                  "Refund processed: " + request.refundAmount() + " " + payment.getCurrency() + 
                                  " - Reason: " + request.reason(), request.processedBy());
            
            // Send refund confirmation
            sendRefundConfirmation(invoice, savedRefund);
            
            // Update customer credit
            updateCustomerCreditAfterRefund(payment.getCustomerId(), request.refundAmount());
            
            log.info("Refund processed successfully for payment: {} with refund ID: {}", 
                    paymentId, savedRefund.getPaymentId());
            
            return new RefundResponse(
                    savedRefund.getPaymentId(),
                    paymentId,
                    invoice.getInvoiceNumber(),
                    request.refundAmount(),
                    payment.getCurrency(),
                    savedRefund.getStatus().toString(),
                    savedRefund.getGatewayTransactionId(),
                    savedRefund.getProcessedAt(),
                    request.reason(),
                    request.processedBy(),
                    updatedTotalRefunded.compareTo(payment.getAmount()) >= 0
            );
            
        } catch (Exception e) {
            log.error("Failed to process refund for payment: {}", paymentId, e);
            
            // Create failed refund record
            Payment failedRefund = Payment.builder()
                    .paymentId(UUID.randomUUID().toString())
                    .invoice(payment.getInvoice())
                    .customerId(payment.getCustomerId())
                    .amount(request.refundAmount().negate())
                    .currency(payment.getCurrency())
                    .paymentMethodId(payment.getPaymentMethodId())
                    .paymentMethodType("REFUND")
                    .status(PaymentStatus.FAILED)
                    .failureReason("Refund processing error: " + e.getMessage())
                    .originalPaymentId(paymentId)
                    .processedAt(LocalDateTime.now())
                    .notes("Failed refund: " + request.reason())
                    .createdBy(request.processedBy())
                    .build();
            
            paymentRepository.save(failedRefund);
            
            throw new BillingException("Refund processing failed: " + e.getMessage());
        }
    }
    
    @Override
    public java.util.List<PaymentResponse> getInvoicePayments(String invoiceId) {
        log.info("Getting payments for invoice: {}", invoiceId);
        
        java.util.List<Payment> payments = paymentRepository.findByInvoiceNumber(invoiceId);
        return payments.stream()
                .map(this::mapPaymentToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public java.util.List<PaymentResponse> getCustomerPayments(String customerId, PaymentFilterRequest filter) {
        log.info("Getting payments for customer: {}", customerId);
        
        java.util.List<Payment> payments = paymentRepository.findByCustomerIdOrderByProcessedAtDesc(customerId);
        return payments.stream()
                .map(this::mapPaymentToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public PaymentResponse processPayment(String invoiceId, ProcessPaymentRequest request) {
        log.info("Processing payment for invoice: {}", invoiceId);
        
        Invoice invoice = invoiceRepository.findByInvoiceNumber(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found: " + invoiceId));
        
        if (invoice.getStatus() == InvoiceStatus.PAID || invoice.getStatus() == InvoiceStatus.CANCELLED) {
            throw new BillingException("Cannot process payment for invoice in status: " + invoice.getStatus());
        }
        
        // Process payment through payment gateway
        PaymentProcessingServiceClient.ProcessPaymentRequest paymentRequest = 
                new PaymentProcessingServiceClient.ProcessPaymentRequest(
                        invoice.getTotalAmount(),
                        invoice.getCurrency(),
                        request.paymentMethodId(),
                        invoice.getCustomerId(),
                        invoice.getInvoiceNumber(),
                        "INVOICE_PAYMENT"
                );
        
        PaymentProcessingServiceClient.PaymentResult paymentResult = 
                paymentProcessingClient.processPayment(paymentRequest);
        
        // Create payment record
        Payment payment = Payment.builder()
                .paymentId(paymentResult.paymentId())
                .invoice(invoice)
                .customerId(invoice.getCustomerId())
                .amount(request.amount())
                .currency(invoice.getCurrency())
                .paymentMethodId(request.paymentMethodId())
                .paymentMethodType(request.paymentMethodType())
                .status(PaymentStatus.valueOf(paymentResult.status()))
                .gatewayTransactionId(paymentResult.transactionId())
                .gatewayResponse(paymentResult.gatewayResponse())
                .processedAt(LocalDateTime.now())
                .createdBy("CUSTOMER")
                .build();
        
        Payment savedPayment = paymentRepository.save(payment);
        
        // Update invoice status if fully paid
        if (request.amount().compareTo(invoice.getTotalAmount()) >= 0) {
            updateInvoiceStatus(invoice, InvoiceStatus.PAID, "Invoice fully paid", "SYSTEM");
            invoice.setPaidAt(LocalDateTime.now());
        } else {
            updateInvoiceStatus(invoice, InvoiceStatus.PARTIALLY_PAID, "Partial payment received", "SYSTEM");
        }
        
        invoiceRepository.save(invoice);
        
        // Send payment confirmation
        sendPaymentConfirmation(invoice, savedPayment);
        
        // Update customer credit if applicable
        updateCustomerCreditAfterPayment(invoice.getCustomerId(), request.amount());
        
        log.info("Payment processed for invoice: {} with payment ID: {}", 
                invoiceId, savedPayment.getPaymentId());
        
        return mapPaymentToResponse(savedPayment);
    }

    @Override
    public PricingCalculationResponse calculateShippingCharges(PricingCalculationRequest request) {
        log.info("Calculating shipping charges for service: {} from {} to {}", 
                request.serviceType(), request.origin(), request.destination());
        
        // Get customer pricing tier
        PricingTierResponse pricingTier = getCustomerPricingTier(request.customerId());
        
        // Base rate calculation
        BigDecimal baseRate = getBaseRateForService(request.serviceType());
        BigDecimal weightCharge = calculateWeightCharge(request.weight(), baseRate);
        BigDecimal dimensionCharge = calculateDimensionCharge(request.dimensions());
        BigDecimal distanceCharge = calculateDistanceCharge(request.origin(), request.destination());
        
        // Apply pricing tier modifiers
        BigDecimal tierMultiplier = pricingTier.discountPercentage() != null ? 
                BigDecimal.ONE.subtract(pricingTier.discountPercentage().divide(new BigDecimal("100"))) : 
                BigDecimal.ONE;
        
        BigDecimal subtotal = weightCharge.add(dimensionCharge).add(distanceCharge);
        BigDecimal baseAmount = subtotal.multiply(tierMultiplier).setScale(2, RoundingMode.HALF_UP);
        
        // Add service-specific fees
        BigDecimal serviceFees = calculateServiceFees(request.serviceType(), request.declaredValue());
        BigDecimal totalAmount = baseAmount.add(serviceFees);
        
        return new PricingCalculationResponse(
                baseAmount,
                serviceFees,
                totalAmount,
                request.serviceType(),
                pricingTier.tierName(),
                generatePricingBreakdown(weightCharge, dimensionCharge, distanceCharge, serviceFees)
        );
    }

    // Implementation of remaining interface methods
    
    
    @Override
    public InvoiceResponse updateInvoice(String invoiceId, UpdateInvoiceRequest request) {
        log.info("Updating invoice: {}", invoiceId);
        
        Invoice invoice = invoiceRepository.findByInvoiceNumber(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found: " + invoiceId));
        
        if (invoice.getStatus() == InvoiceStatus.PAID || invoice.getStatus() == InvoiceStatus.CANCELLED) {
            throw new BillingException("Cannot update invoice in status: " + invoice.getStatus());
        }
        
        // Update allowed fields
        boolean updated = false;
        StringBuilder updateLog = new StringBuilder("Invoice updated: ");
        
        if (request.customerName() != null && !request.customerName().equals(invoice.getCustomerName())) {
            invoice.setCustomerName(request.customerName());
            updateLog.append("customerName, ");
            updated = true;
        }
        
        if (request.customerEmail() != null && !request.customerEmail().equals(invoice.getCustomerEmail())) {
            invoice.setCustomerEmail(request.customerEmail());
            updateLog.append("customerEmail, ");
            updated = true;
        }
        
        if (request.billingAddress() != null && !request.billingAddress().equals(invoice.getBillingAddress())) {
            invoice.setBillingAddress(request.billingAddress());
            updateLog.append("billingAddress, ");
            updated = true;
        }
        
        if (request.description() != null && !request.description().equals(invoice.getDescription())) {
            invoice.setDescription(request.description());
            updateLog.append("description, ");
            updated = true;
        }
        
        if (request.dueDate() != null && !request.dueDate().equals(invoice.getDueDate())) {
            invoice.setDueDate(request.dueDate());
            updateLog.append("dueDate, ");
            updated = true;
        }
        
        if (request.currency() != null && !request.currency().equals(invoice.getCurrency())) {
            // Recalculate amounts if currency changed
            if (invoice.getStatus() == InvoiceStatus.DRAFT) {
                invoice.setCurrency(request.currency());
                // TODO: Implement currency conversion logic if needed
                updateLog.append("currency, ");
                updated = true;
            } else {
                throw new BillingException("Cannot change currency for non-draft invoices");
            }
        }
        
        // Update metadata
        if (request.metadata() != null) {
            invoice.setMetadata(request.metadata());
            updateLog.append("metadata, ");
            updated = true;
        }
        
        if (!updated) {
            log.info("No changes detected for invoice: {}", invoiceId);
            return mapInvoiceToResponse(invoice);
        }
        
        invoice.setUpdatedBy(request.updatedBy());
        Invoice savedInvoice = invoiceRepository.save(invoice);
        
        // Create audit entry
        createBillingAuditEntry(savedInvoice.getId(), "INVOICE_UPDATED", 
                              updateLog.toString(), request.updatedBy());
        
        log.info("Invoice updated successfully: {}", invoiceId);
        
        return mapInvoiceToResponse(savedInvoice);
    }
    
    @Override
    public void cancelInvoice(String invoiceId, String reason) {
        log.info("Cancelling invoice: {} with reason: {}", invoiceId, reason);
        
        Invoice invoice = invoiceRepository.findByInvoiceNumber(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found: " + invoiceId));
        
        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new BillingException("Cannot cancel paid invoice");
        }
        
        updateInvoiceStatus(invoice, InvoiceStatus.CANCELLED, reason, "SYSTEM");
        invoiceRepository.save(invoice);
    }
    
    @Override
    public void sendInvoice(String invoiceId, SendInvoiceRequest request) {
        log.info("Sending invoice: {} to recipients: {}", invoiceId, request.recipients());
        
        Invoice invoice = invoiceRepository.findByInvoiceNumber(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found: " + invoiceId));
        
        if (invoice.getStatus() == InvoiceStatus.CANCELLED) {
            throw new BillingException("Cannot send cancelled invoice");
        }
        
        // Update invoice status to SENT if it's DRAFT
        if (invoice.getStatus() == InvoiceStatus.DRAFT) {
            updateInvoiceStatus(invoice, InvoiceStatus.SENT, "Invoice sent manually", request.sentBy());
            invoice.setSentAt(LocalDateTime.now());
        }
        
        // Update last sent timestamp
        invoice.setLastSentAt(LocalDateTime.now());
        invoiceRepository.save(invoice);
        
        // Send to all recipients
        for (String recipient : request.recipients()) {
            try {
                sendInvoiceToRecipient(invoice, recipient, request.customMessage(), request.attachPdf());
                log.info("Invoice {} sent successfully to: {}", invoiceId, recipient);
            } catch (Exception e) {
                log.error("Failed to send invoice {} to recipient: {}", invoiceId, recipient, e);
                // Continue sending to other recipients even if one fails
            }
        }
        
        // Create audit entry
        createBillingAuditEntry(invoice.getId(), "INVOICE_SENT", 
                              "Invoice sent to " + request.recipients().size() + " recipients", 
                              request.sentBy());
        
        // Send notification to internal team if requested
        if (request.notifyInternalTeam()) {
            sendInternalInvoiceNotification(invoice, request.sentBy());
        }
        
        log.info("Invoice sending completed for: {}", invoiceId);
    }
    
    @Override
    public java.util.List<InvoiceResponse> getCustomerInvoices(String customerId, InvoiceFilterRequest filter) {
        log.info("Getting invoices for customer: {}", customerId);
        
        java.util.List<Invoice> invoices = invoiceRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
        return invoices.stream()
                .map(this::mapInvoiceToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public java.util.List<InvoiceResponse> getInvoicesByStatusAndDateRange(InvoiceStatus status, 
                                                                          LocalDateTime fromDate, 
                                                                          LocalDateTime toDate) {
        log.info("Getting invoices by status: {} and date range: {} to {}", status, fromDate, toDate);
        
        java.util.List<Invoice> invoices = invoiceRepository.findByStatusAndDateRange(status, fromDate, toDate);
        return invoices.stream()
                .map(this::mapInvoiceToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public TaxCalculationResponse calculateTaxes(TaxCalculationRequest request) {
        log.info("Calculating taxes for amount: {} at address: {}", request.amount(), request.billingAddress());
        
        // Mock tax calculation - in production would integrate with tax service
        BigDecimal taxRate = new BigDecimal("8.5"); // 8.5% tax rate
        BigDecimal taxAmount = request.amount()
                .multiply(taxRate)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        
        java.util.Map<String, BigDecimal> taxBreakdown = new java.util.HashMap<>();
        taxBreakdown.put("SALES_TAX", taxAmount);
        
        return new TaxCalculationResponse(
                taxAmount,
                taxRate,
                "California, USA",
                taxBreakdown,
                false,
                "DESTINATION_BASED"
        );
    }
    
    @Override
    public PricingTierResponse getCustomerPricingTier(String customerId) {
        log.info("Getting pricing tier for customer: {}", customerId);
        
        // Mock implementation - would query actual customer tier data
        return new PricingTierResponse(
                "STANDARD",
                new BigDecimal("5.0"),
                "Standard customer pricing",
                20,
                false
        );
    }
    
    @Override
    public boolean isInvoiceOverdue(String invoiceId) {
        Invoice invoice = invoiceRepository.findByInvoiceNumber(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found: " + invoiceId));
        
        return invoice.getDueDate() != null && 
               invoice.getDueDate().isBefore(LocalDateTime.now()) &&
               (invoice.getStatus() == InvoiceStatus.SENT || invoice.getStatus() == InvoiceStatus.PARTIALLY_PAID);
    }
    
    @Override
    public DiscountApplicationResponse applyVolumeDiscounts(String customerId, BigDecimal baseAmount, int shipmentCount) {
        log.info("Applying volume discounts for customer: {} with {} shipments", customerId, shipmentCount);
        
        BigDecimal discountPercentage = BigDecimal.ZERO;
        
        // Volume discount tiers
        if (shipmentCount >= 100) {
            discountPercentage = new BigDecimal("15.0"); // 15% for 100+ shipments
        } else if (shipmentCount >= 50) {
            discountPercentage = new BigDecimal("10.0"); // 10% for 50+ shipments
        } else if (shipmentCount >= 20) {
            discountPercentage = new BigDecimal("5.0");  // 5% for 20+ shipments
        }
        
        BigDecimal discountAmount = baseAmount
                .multiply(discountPercentage)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        
        BigDecimal finalAmount = baseAmount.subtract(discountAmount);
        
        return new DiscountApplicationResponse(
                discountPercentage,
                discountAmount,
                finalAmount,
                "VOLUME_DISCOUNT",
                "Volume discount applied for " + shipmentCount + " shipments"
        );
    }

    @Override
    public void updateInvoiceStatus(Invoice invoice, InvoiceStatus newStatus, String reason, String updatedBy) {
        InvoiceStatus oldStatus = invoice.getStatus();
        
        if (!validateInvoiceStatusTransition(oldStatus, newStatus)) {
            throw new BillingException(
                    String.format("Invalid status transition from %s to %s", oldStatus, newStatus));
        }
        
        invoice.setStatus(newStatus);
        invoice.setUpdatedBy(updatedBy);
        
        // Create audit entry
        createBillingAuditEntry(invoice.getId(), "STATUS_CHANGE", 
                              String.format("Status changed from %s to %s: %s", oldStatus, newStatus, reason), 
                              updatedBy);
        
        log.info("Invoice {} status updated from {} to {}", 
                invoice.getInvoiceNumber(), oldStatus, newStatus);
    }

    @Override
    public boolean validateInvoiceStatusTransition(InvoiceStatus currentStatus, InvoiceStatus newStatus) {
        switch (currentStatus) {
            case DRAFT:
                return newStatus == InvoiceStatus.SENT || 
                       newStatus == InvoiceStatus.CANCELLED;
            case SENT:
                return newStatus == InvoiceStatus.PAID ||
                       newStatus == InvoiceStatus.PARTIALLY_PAID ||
                       newStatus == InvoiceStatus.OVERDUE ||
                       newStatus == InvoiceStatus.CANCELLED;
            case PARTIALLY_PAID:
                return newStatus == InvoiceStatus.PAID ||
                       newStatus == InvoiceStatus.OVERDUE;
            case OVERDUE:
                return newStatus == InvoiceStatus.PAID ||
                       newStatus == InvoiceStatus.PARTIALLY_PAID ||
                       newStatus == InvoiceStatus.CANCELLED;
            case PAID:
                return newStatus == InvoiceStatus.REFUNDED;
            case CANCELLED:
            case REFUNDED:
                return false; // No transitions allowed
            default:
                return false;
        }
    }

    @Override
    public LocalDateTime calculateDueDate(LocalDateTime invoiceDate, String paymentTerms) {
        switch (paymentTerms) {
            case "NET_15":
                return invoiceDate.plusDays(15);
            case "NET_30":
                return invoiceDate.plusDays(30);
            case "NET_45":
                return invoiceDate.plusDays(45);
            case "NET_60":
                return invoiceDate.plusDays(60);
            case "COD":
                return invoiceDate; // Cash on delivery
            case "IMMEDIATE":
                return invoiceDate.plusHours(24);
            default:
                return invoiceDate.plusDays(30); // Default to NET_30
        }
    }

    // Helper methods
    
    private String generateInvoiceNumber() {
        String date = LocalDateTime.now().format(DATE_FORMATTER);
        String uniqueId = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return String.format("%s-%s-%s", INVOICE_PREFIX, date, uniqueId);
    }
    
    private InvoiceResponse mapInvoiceToResponse(Invoice invoice) {
        return new InvoiceResponse(
                invoice.getInvoiceNumber(),
                invoice.getCustomerId(),
                invoice.getCustomerName(),
                invoice.getCustomerEmail(),
                invoice.getBillingAddress(),
                invoice.getDescription(),
                invoice.getSubtotal(),
                invoice.getDiscountAmount(),
                invoice.getTaxAmount(),
                invoice.getTotalAmount(),
                invoice.getCurrency(),
                invoice.getStatus(),
                invoice.getDueDate(),
                invoice.getCreatedAt(),
                invoice.getSentAt(),
                invoice.getPaidAt(),
                invoice.getShipmentId(),
                invoice.getSubscriptionId(),
                invoice.getInvoiceType()
        );
    }
    
    private PaymentResponse mapPaymentToResponse(Payment payment) {
        return new PaymentResponse(
                payment.getPaymentId(),
                payment.getInvoice().getInvoiceNumber(),
                payment.getCustomerId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getPaymentMethodType(),
                payment.getStatus(),
                payment.getGatewayTransactionId(),
                payment.getProcessedAt(),
                payment.getFailureReason()
        );
    }
    
    // Helper methods implementation
    
    private void createInvoiceLineItems(Invoice invoice, PricingCalculationResponse pricing, 
                                       DiscountApplicationResponse discounts, TaxCalculationResponse taxes) {
        // Implementation for creating invoice line items
        log.info("Creating line items for invoice: {}", invoice.getInvoiceNumber());
        // This would create detailed line items based on pricing breakdown
    }
    
    private void createBulkInvoiceLineItems(Invoice invoice, java.util.List<String> shipmentIds, 
                                          DiscountApplicationResponse discounts, TaxCalculationResponse taxes) {
        log.info("Creating bulk line items for invoice: {} with {} shipments", 
                invoice.getInvoiceNumber(), shipmentIds.size());
        // Implementation for bulk invoice line items
    }
    
    private void createBillingAuditEntry(java.util.UUID entityId, String action, String description, String performedBy) {
        log.info("Creating audit entry for entity: {} - action: {}", entityId, action);
        // Implementation for creating audit trail entries
    }
    
    private BigDecimal calculateShipmentCharges(String shipmentId, String customerId) {
        // Mock implementation - would integrate with shipment service
        return new BigDecimal("25.00");
    }
    
    private int getCustomerMonthlyShipmentCount(String customerId) {
        // Mock implementation - would query actual shipment data
        return 25;
    }
    
    private String getCustomerPaymentTerms(String customerId) {
        // Mock implementation - would query customer settings
        return "NET_30";
    }
    
    private void sendInvoiceToCustomer(Invoice invoice) {
        log.info("Sending invoice {} to customer: {}", invoice.getInvoiceNumber(), invoice.getCustomerEmail());
        // Implementation would use notification service
    }
    
    private boolean isAutomaticPaymentEnabled(String customerId) {
        // Mock implementation - would check customer payment preferences
        return false;
    }
    
    private void sendPaymentConfirmation(Invoice invoice, Payment payment) {
        log.info("Sending payment confirmation for payment: {} on invoice: {}", 
                payment.getPaymentId(), invoice.getInvoiceNumber());
        // Implementation would use notification service
    }
    
    private void updateCustomerCreditAfterPayment(String customerId, BigDecimal amount) {
        log.info("Updating customer credit for: {} with payment amount: {}", customerId, amount);
        // Implementation would update customer credit records
    }
    
    private BigDecimal getBaseRateForService(String serviceType) {
        // Mock implementation - would use actual rate tables
        switch (serviceType) {
            case "SAME_DAY": return new BigDecimal("50.00");
            case "NEXT_DAY": return new BigDecimal("25.00");
            case "STANDARD": return new BigDecimal("15.00");
            default: return new BigDecimal("20.00");
        }
    }
    
    private BigDecimal calculateWeightCharge(BigDecimal weight, BigDecimal baseRate) {
        return baseRate.multiply(weight).setScale(2, RoundingMode.HALF_UP);
    }
    
    private BigDecimal calculateDimensionCharge(String dimensions) {
        // Mock implementation - would calculate based on dimensional weight
        return new BigDecimal("5.00");
    }
    
    private BigDecimal calculateDistanceCharge(String origin, String destination) {
        // Mock implementation - would calculate based on actual distance
        return new BigDecimal("10.00");
    }
    
    private BigDecimal calculateServiceFees(String serviceType, BigDecimal declaredValue) {
        BigDecimal serviceFee = BigDecimal.ZERO;
        
        // Add insurance fee if declared value exists
        if (declaredValue != null && declaredValue.compareTo(BigDecimal.ZERO) > 0) {
            serviceFee = serviceFee.add(declaredValue.multiply(new BigDecimal("0.01"))); // 1% insurance
        }
        
        // Add service-specific fees
        switch (serviceType) {
            case "SAME_DAY":
                serviceFee = serviceFee.add(new BigDecimal("15.00")); // Rush fee
                break;
            case "SIGNATURE_REQUIRED":
                serviceFee = serviceFee.add(new BigDecimal("5.00")); // Signature fee
                break;
        }
        
        return serviceFee;
    }
    
    private java.util.Map<String, BigDecimal> generatePricingBreakdown(BigDecimal weightCharge, 
                                                                      BigDecimal dimensionCharge, 
                                                                      BigDecimal distanceCharge, 
                                                                      BigDecimal serviceFees) {
        java.util.Map<String, BigDecimal> breakdown = new java.util.HashMap<>();
        breakdown.put("weightCharge", weightCharge);
        breakdown.put("dimensionCharge", dimensionCharge);
        breakdown.put("distanceCharge", distanceCharge);
        breakdown.put("serviceFees", serviceFees);
        return breakdown;
    }
    
    // Additional helper methods for new functionality
    
    private BigDecimal getTotalPaidAmount(String invoiceId) {
        List<Payment> payments = paymentRepository.findByInvoiceNumber(invoiceId);
        return payments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.COMPLETED)
                .filter(p -> !"REFUND".equals(p.getPaymentMethodType()))
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private BigDecimal getTotalRefundedAmount(String paymentId) {
        List<Payment> refunds = paymentRepository.findByOriginalPaymentId(paymentId);
        return refunds.stream()
                .filter(p -> p.getStatus() == PaymentStatus.COMPLETED)
                .filter(p -> "REFUND".equals(p.getPaymentMethodType()))
                .map(p -> p.getAmount().abs()) // Convert negative refund amounts to positive
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private String getCustomerDefaultPaymentMethod(String customerId) {
        // Mock implementation - would integrate with customer service
        return "pm_default_" + customerId;
    }
    
    private void sendPaymentFailureNotification(Invoice invoice, Payment payment) {
        log.info("Sending payment failure notification for invoice: {} and payment: {}", 
                invoice.getInvoiceNumber(), payment.getPaymentId());
        // Implementation would use notification service
    }
    
    private void sendRefundConfirmation(Invoice invoice, Payment refund) {
        log.info("Sending refund confirmation for invoice: {} and refund: {}", 
                invoice.getInvoiceNumber(), refund.getPaymentId());
        // Implementation would use notification service
    }
    
    private void updateCustomerCreditAfterRefund(String customerId, BigDecimal refundAmount) {
        log.info("Updating customer credit for: {} with refund amount: {}", customerId, refundAmount);
        // Implementation would update customer credit records
    }
    
    private void sendInvoiceToRecipient(Invoice invoice, String recipient, String customMessage, boolean attachPdf) {
        log.info("Sending invoice {} to recipient: {} with PDF: {}", 
                invoice.getInvoiceNumber(), recipient, attachPdf);
        // Implementation would use notification service with PDF generation
    }
    
    private void sendInternalInvoiceNotification(Invoice invoice, String sentBy) {
        log.info("Sending internal notification for invoice: {} sent by: {}", 
                invoice.getInvoiceNumber(), sentBy);
        // Implementation would notify internal team
    }
}