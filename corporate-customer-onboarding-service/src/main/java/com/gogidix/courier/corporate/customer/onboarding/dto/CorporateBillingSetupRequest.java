package com.gogidix.courier.corporate.customer.onboarding.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;

/**
 * Request DTO for setting up corporate billing configuration.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Schema(description = "Request to setup corporate billing configuration")
public record CorporateBillingSetupRequest(
    
    @NotBlank(message = "Billing cycle is required")
    @Schema(description = "Billing cycle frequency", example = "MONTHLY", allowableValues = {"WEEKLY", "MONTHLY", "QUARTERLY", "ANNUALLY"})
    String billingCycle,
    
    @NotBlank(message = "Payment terms are required")
    @Schema(description = "Payment terms", example = "NET_30", allowableValues = {"NET_15", "NET_30", "NET_45", "NET_60", "IMMEDIATE"})
    String paymentTerms,
    
    @DecimalMin(value = "0.0", message = "Volume discount cannot be negative")
    @DecimalMax(value = "0.5", message = "Volume discount cannot exceed 50%")
    @Schema(description = "Volume discount percentage (0.0 to 0.5)", example = "0.15")
    Double volumeDiscount,
    
    @NotBlank(message = "Billing contact email is required")
    @Email(message = "Valid billing contact email is required")
    @Schema(description = "Primary billing contact email", example = "billing@company.com")
    String billingContactEmail,
    
    @Schema(description = "Secondary billing contact email", example = "finance@company.com")
    @Email(message = "Valid secondary billing contact email required")
    String secondaryBillingContactEmail,
    
    @Schema(description = "Whether purchase orders are required", example = "true")
    Boolean requiresPurchaseOrder,
    
    @Schema(description = "Default purchase order number format", example = "PO-{YYYY}-{MM}-{XXXX}")
    String poNumberFormat,
    
    @Schema(description = "Credit limit for the corporate account")
    BigDecimal creditLimit,
    
    @Schema(description = "Auto-payment setup")
    AutoPaymentConfiguration autoPayment,
    
    @Schema(description = "Invoice delivery preferences")
    InvoiceDeliveryPreferences invoicePreferences,
    
    @Schema(description = "Tax configuration")
    TaxConfiguration taxConfiguration,
    
    @Schema(description = "Cost center mappings for departmental billing")
    List<CostCenterMapping> costCenters,
    
    @Schema(description = "Billing notifications setup")
    BillingNotificationPreferences notificationPreferences
) {}

/**
 * Auto-payment configuration for corporate billing.
 */
@Schema(description = "Auto-payment configuration")
record AutoPaymentConfiguration(
    
    @Schema(description = "Whether auto-payment is enabled")
    Boolean enabled,
    
    @Schema(description = "Payment method", example = "ACH", allowableValues = {"ACH", "WIRE", "CREDIT_CARD", "CHECK"})
    String paymentMethod,
    
    @Schema(description = "Bank account information for ACH")
    BankAccountInfo bankAccount,
    
    @Schema(description = "Credit card information")
    CreditCardInfo creditCard,
    
    @Schema(description = "Auto-payment trigger amount")
    BigDecimal triggerAmount,
    
    @Schema(description = "Days before due date to trigger auto-payment", example = "5")
    Integer daysBefore
) {}

/**
 * Bank account information for ACH payments.
 */
@Schema(description = "Bank account information")
record BankAccountInfo(
    
    @Schema(description = "Bank routing number")
    String routingNumber,
    
    @Schema(description = "Account number (encrypted)")
    String accountNumber,
    
    @Schema(description = "Account type", allowableValues = {"CHECKING", "SAVINGS"})
    String accountType,
    
    @Schema(description = "Bank name")
    String bankName
) {}

/**
 * Credit card information for payments.
 */
@Schema(description = "Credit card information")
record CreditCardInfo(
    
    @Schema(description = "Card number (encrypted)")
    String cardNumber,
    
    @Schema(description = "Expiration month")
    Integer expirationMonth,
    
    @Schema(description = "Expiration year")
    Integer expirationYear,
    
    @Schema(description = "Cardholder name")
    String cardholderName,
    
    @Schema(description = "Billing address")
    String billingAddress
) {}

/**
 * Invoice delivery preferences.
 */
@Schema(description = "Invoice delivery preferences")
record InvoiceDeliveryPreferences(
    
    @Schema(description = "Delivery method", allowableValues = {"EMAIL", "POSTAL_MAIL", "ELECTRONIC_PORTAL", "API"})
    String deliveryMethod,
    
    @Schema(description = "Invoice format", allowableValues = {"PDF", "XML", "CSV", "EDI"})
    String format,
    
    @Schema(description = "Email addresses for invoice delivery")
    List<String> emailAddresses,
    
    @Schema(description = "Postal address for physical delivery")
    String postalAddress,
    
    @Schema(description = "API webhook URL for electronic delivery")
    String webhookUrl
) {}

/**
 * Tax configuration for corporate billing.
 */
@Schema(description = "Tax configuration")
record TaxConfiguration(
    
    @Schema(description = "Tax exemption status")
    Boolean taxExempt,
    
    @Schema(description = "Tax exemption certificate number")
    String exemptionCertificateNumber,
    
    @Schema(description = "Tax ID number")
    String taxIdNumber,
    
    @Schema(description = "Tax jurisdiction")
    String taxJurisdiction,
    
    @Schema(description = "Special tax rates or configurations")
    List<TaxRate> specialTaxRates
) {}

/**
 * Tax rate configuration.
 */
@Schema(description = "Tax rate configuration")
record TaxRate(
    
    @Schema(description = "Tax type", example = "SALES_TAX")
    String taxType,
    
    @Schema(description = "Tax rate percentage")
    Double rate,
    
    @Schema(description = "Geographic scope")
    String geoScope
) {}

/**
 * Cost center mapping for departmental billing.
 */
@Schema(description = "Cost center mapping")
record CostCenterMapping(
    
    @Schema(description = "Cost center code", example = "CC-SALES-001")
    String costCenterCode,
    
    @Schema(description = "Cost center name", example = "Sales Department")
    String costCenterName,
    
    @Schema(description = "Department or division")
    String department,
    
    @Schema(description = "Budget allocation percentage")
    Double allocationPercentage,
    
    @Schema(description = "Monthly budget limit")
    BigDecimal budgetLimit
) {}

/**
 * Billing notification preferences.
 */
@Schema(description = "Billing notification preferences")
record BillingNotificationPreferences(
    
    @Schema(description = "Email addresses for billing notifications")
    List<String> notificationEmails,
    
    @Schema(description = "SMS numbers for urgent billing notifications")
    List<String> smsNumbers,
    
    @Schema(description = "Types of notifications to send")
    List<String> notificationTypes,
    
    @Schema(description = "Notification frequency", allowableValues = {"IMMEDIATE", "DAILY", "WEEKLY"})
    String frequency,
    
    @Schema(description = "Days before due date to send reminders", example = "7")
    Integer reminderDays
) {}