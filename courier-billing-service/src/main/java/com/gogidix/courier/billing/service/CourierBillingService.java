package com.gogidix.courier.billing.service;

import com.gogidix.courier.billing.dto.*;
import com.gogidix.courier.billing.model.Invoice;
import com.gogidix.courier.billing.model.InvoiceStatus;
import com.gogidix.courier.billing.model.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for Courier Billing operations.
 * 
 * This service provides comprehensive billing functionality including:
 * - Invoice generation and management
 * - Payment processing and tracking
 * - Subscription and recurring billing
 * - Volume-based pricing and discounts
 * - Credit management and payment terms
 * - Tax calculation and compliance
 * - Financial reporting and analytics
 * - Integration with payment gateways
 * - Corporate billing and contracts
 * - Multi-currency support
 */
public interface CourierBillingService {

    // Invoice Management
    
    /**
     * Creates a new invoice for shipment charges
     */
    InvoiceResponse createShipmentInvoice(CreateShipmentInvoiceRequest request);

    /**
     * Creates a bulk invoice for multiple shipments
     */
    InvoiceResponse createBulkInvoice(CreateBulkInvoiceRequest request);

    /**
     * Creates a recurring subscription invoice
     */
    InvoiceResponse createSubscriptionInvoice(CreateSubscriptionInvoiceRequest request);

    /**
     * Retrieves an invoice by ID
     */
    InvoiceResponse getInvoice(String invoiceId);

    /**
     * Updates an existing invoice
     */
    InvoiceResponse updateInvoice(String invoiceId, UpdateInvoiceRequest request);

    /**
     * Finalizes a draft invoice
     */
    void finalizeInvoice(String invoiceId);

    /**
     * Cancels an invoice
     */
    void cancelInvoice(String invoiceId, String reason);

    /**
     * Sends invoice to customer via email
     */
    void sendInvoice(String invoiceId, SendInvoiceRequest request);

    /**
     * Gets all invoices for a customer with filtering
     */
    List<InvoiceResponse> getCustomerInvoices(String customerId, InvoiceFilterRequest filter);

    /**
     * Gets invoices by status and date range
     */
    List<InvoiceResponse> getInvoicesByStatusAndDateRange(InvoiceStatus status, 
                                                         LocalDateTime fromDate, 
                                                         LocalDateTime toDate);

    // Payment Processing
    
    /**
     * Processes a payment for an invoice
     */
    PaymentResponse processPayment(String invoiceId, ProcessPaymentRequest request);

    /**
     * Records a manual payment
     */
    PaymentResponse recordManualPayment(String invoiceId, RecordManualPaymentRequest request);

    /**
     * Initiates automatic payment collection
     */
    PaymentResponse initiateAutomaticPayment(String invoiceId);

    /**
     * Processes a refund for a payment
     */
    RefundResponse processRefund(String paymentId, ProcessRefundRequest request);

    /**
     * Gets payment history for an invoice
     */
    List<PaymentResponse> getInvoicePayments(String invoiceId);

    /**
     * Gets payment history for a customer
     */
    List<PaymentResponse> getCustomerPayments(String customerId, PaymentFilterRequest filter);

    // Pricing and Billing Calculations
    
    /**
     * Calculates shipping charges for a quote
     */
    PricingCalculationResponse calculateShippingCharges(PricingCalculationRequest request);

    /**
     * Applies volume discounts to pricing
     */
    DiscountApplicationResponse applyVolumeDiscounts(String customerId, 
                                                    BigDecimal baseAmount, 
                                                    int shipmentCount);

    /**
     * Calculates taxes for billing address and service type
     */
    TaxCalculationResponse calculateTaxes(TaxCalculationRequest request);

    /**
     * Gets pricing tiers for a customer
     */
    PricingTierResponse getCustomerPricingTier(String customerId);

    /**
     * Updates customer pricing tier
     */
    void updateCustomerPricingTier(String customerId, UpdatePricingTierRequest request);

    // Subscription and Recurring Billing
    
    /**
     * Creates a billing subscription for a customer
     */
    SubscriptionResponse createSubscription(CreateSubscriptionRequest request);

    /**
     * Updates an existing subscription
     */
    SubscriptionResponse updateSubscription(String subscriptionId, UpdateSubscriptionRequest request);

    /**
     * Cancels a subscription
     */
    void cancelSubscription(String subscriptionId, String reason);

    /**
     * Processes recurring billing for all active subscriptions
     */
    RecurringBillingResponse processRecurringBilling();

    /**
     * Gets subscription details
     */
    SubscriptionResponse getSubscription(String subscriptionId);

    /**
     * Gets all subscriptions for a customer
     */
    List<SubscriptionResponse> getCustomerSubscriptions(String customerId);

    // Credit and Payment Terms Management
    
    /**
     * Sets up credit terms for a customer
     */
    CreditTermsResponse setupCreditTerms(String customerId, SetupCreditTermsRequest request);

    /**
     * Updates customer credit limit
     */
    void updateCreditLimit(String customerId, BigDecimal newCreditLimit);

    /**
     * Gets customer credit information
     */
    CustomerCreditResponse getCustomerCredit(String customerId);

    /**
     * Processes credit application
     */
    CreditApplicationResponse processCreditApplication(String customerId, CreditApplicationRequest request);

    /**
     * Gets overdue invoices for collection
     */
    List<InvoiceResponse> getOverdueInvoices(OverdueInvoicesRequest request);

    /**
     * Sends payment reminder to customer
     */
    void sendPaymentReminder(String invoiceId, PaymentReminderRequest request);

    // Financial Reporting
    
    /**
     * Generates revenue report for date range
     */
    RevenueReportResponse generateRevenueReport(RevenueReportRequest request);

    /**
     * Generates customer billing summary
     */
    CustomerBillingSummaryResponse generateCustomerBillingSummary(String customerId, 
                                                                 BillingSummaryRequest request);

    /**
     * Generates accounts receivable report
     */
    AccountsReceivableReportResponse generateAccountsReceivableReport(AccountsReceivableRequest request);

    /**
     * Gets billing analytics and metrics
     */
    BillingAnalyticsResponse getBillingAnalytics(BillingAnalyticsRequest request);

    /**
     * Exports billing data for accounting integration
     */
    BillingExportResponse exportBillingData(BillingExportRequest request);

    // Multi-Currency Support
    
    /**
     * Converts amount between currencies
     */
    CurrencyConversionResponse convertCurrency(String fromCurrency, 
                                              String toCurrency, 
                                              BigDecimal amount);

    /**
     * Gets supported currencies
     */
    List<CurrencyResponse> getSupportedCurrencies();

    /**
     * Updates exchange rates
     */
    void updateExchangeRates(List<ExchangeRateUpdate> rates);

    // Integration Services
    
    /**
     * Syncs billing data with external accounting system
     */
    void syncWithAccountingSystem(String customerId);

    /**
     * Integrates with payment gateway
     */
    PaymentGatewayResponse integratePaymentGateway(PaymentGatewayIntegrationRequest request);

    /**
     * Validates payment method
     */
    PaymentMethodValidationResponse validatePaymentMethod(ValidatePaymentMethodRequest request);

    /**
     * Sets up automatic payment for customer
     */
    AutoPaymentSetupResponse setupAutomaticPayment(String customerId, AutoPaymentSetupRequest request);

    // Dispute and Chargeback Management
    
    /**
     * Creates a billing dispute
     */
    DisputeResponse createBillingDispute(String invoiceId, CreateDisputeRequest request);

    /**
     * Resolves a billing dispute
     */
    void resolveDispute(String disputeId, ResolveDisputeRequest request);

    /**
     * Handles chargeback notification
     */
    void handleChargeback(String paymentId, ChargebackNotificationRequest request);

    /**
     * Gets disputes for a customer
     */
    List<DisputeResponse> getCustomerDisputes(String customerId);

    // Audit and Compliance
    
    /**
     * Gets billing audit trail
     */
    List<BillingAuditEntry> getBillingAuditTrail(String entityId, AuditTrailRequest request);

    /**
     * Validates tax compliance
     */
    TaxComplianceResponse validateTaxCompliance(String customerId, TaxComplianceRequest request);

    /**
     * Generates tax reports for compliance
     */
    TaxReportResponse generateTaxReport(TaxReportRequest request);

    // Utility Methods
    
    /**
     * Updates invoice status with validation
     */
    void updateInvoiceStatus(Invoice invoice, InvoiceStatus newStatus, String reason, String updatedBy);

    /**
     * Validates status transition
     */
    boolean validateInvoiceStatusTransition(InvoiceStatus currentStatus, InvoiceStatus newStatus);

    /**
     * Calculates due date based on payment terms
     */
    LocalDateTime calculateDueDate(LocalDateTime invoiceDate, String paymentTerms);

    /**
     * Checks if invoice is overdue
     */
    boolean isInvoiceOverdue(String invoiceId);

    /**
     * Applies late fees to overdue invoices
     */
    void applyLateFees(String invoiceId, LateFeeConfiguration config);
}