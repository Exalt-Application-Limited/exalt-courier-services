package com.gogidix.courier.customer.onboarding.model;

import com.gogidix.ecosystem.shared.model.BaseEntity;
import com.gogidix.ecosystem.shared.model.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing customer-specific profile information extending the shared User entity.
 * 
 * This entity contains courier service specific customer data while leveraging the 
 * shared User entity for common user attributes (email, phone, name, etc.).
 */
@Entity
@Table(name = "customer_profiles", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_customer_reference_id", columnList = "customer_reference_id"),
    @Index(name = "idx_onboarding_application_id", columnList = "onboarding_application_id")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerProfile extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @NotNull(message = "User is required")
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "onboarding_application_id", unique = true)
    private CustomerOnboardingApplication onboardingApplication;

    @Column(name = "customer_reference_id", unique = true, nullable = false, length = 50)
    @NotNull(message = "Customer reference ID is required")
    @Size(max = 50, message = "Customer reference ID must not exceed 50 characters")
    private String customerReferenceId;

    @Column(name = "preferred_delivery_address_line1", length = 200)
    @Size(max = 200, message = "Address line 1 must not exceed 200 characters")
    private String preferredDeliveryAddressLine1;

    @Column(name = "preferred_delivery_address_line2", length = 200)
    @Size(max = 200, message = "Address line 2 must not exceed 200 characters")
    private String preferredDeliveryAddressLine2;

    @Column(name = "preferred_delivery_city", length = 100)
    @Size(max = 100, message = "City must not exceed 100 characters")
    private String preferredDeliveryCity;

    @Column(name = "preferred_delivery_state_province", length = 100)
    @Size(max = 100, message = "State/Province must not exceed 100 characters")
    private String preferredDeliveryStateProvince;

    @Column(name = "preferred_delivery_postal_code", length = 20)
    @Size(max = 20, message = "Postal code must not exceed 20 characters")
    private String preferredDeliveryPostalCode;

    @Column(name = "preferred_delivery_country", length = 100)
    @Size(max = 100, message = "Country must not exceed 100 characters")
    private String preferredDeliveryCountry;

    @Column(name = "billing_address_line1", length = 200)
    @Size(max = 200, message = "Billing address line 1 must not exceed 200 characters")
    private String billingAddressLine1;

    @Column(name = "billing_address_line2", length = 200)
    @Size(max = 200, message = "Billing address line 2 must not exceed 200 characters")
    private String billingAddressLine2;

    @Column(name = "billing_city", length = 100)
    @Size(max = 100, message = "Billing city must not exceed 100 characters")
    private String billingCity;

    @Column(name = "billing_state_province", length = 100)
    @Size(max = 100, message = "Billing state/province must not exceed 100 characters")
    private String billingStateProvince;

    @Column(name = "billing_postal_code", length = 20)
    @Size(max = 20, message = "Billing postal code must not exceed 20 characters")
    private String billingPostalCode;

    @Column(name = "billing_country", length = 100)
    @Size(max = 100, message = "Billing country must not exceed 100 characters")
    private String billingCountry;

    @Column(name = "customer_segment", length = 50)
    @Size(max = 50, message = "Customer segment must not exceed 50 characters")
    private String customerSegment; // INDIVIDUAL, SMALL_BUSINESS, ENTERPRISE

    @Column(name = "preferred_communication_method", length = 20)
    @Size(max = 20, message = "Communication method must not exceed 20 characters")
    private String preferredCommunicationMethod; // EMAIL, SMS, PHONE, APP

    @Column(name = "marketing_consent")
    private Boolean marketingConsent;

    @Column(name = "sms_notifications_enabled")
    private Boolean smsNotificationsEnabled;

    @Column(name = "email_notifications_enabled")
    private Boolean emailNotificationsEnabled;

    @Column(name = "push_notifications_enabled")
    private Boolean pushNotificationsEnabled;

    @Column(name = "delivery_instructions", length = 500)
    @Size(max = 500, message = "Delivery instructions must not exceed 500 characters")
    private String deliveryInstructions;

    @Column(name = "billing_customer_id", length = 100)
    @Size(max = 100, message = "Billing customer ID must not exceed 100 characters")
    private String billingCustomerId;

    @Column(name = "kyc_verified")
    private Boolean kycVerified;

    @Column(name = "kyc_verification_date")
    private LocalDateTime kycVerificationDate;

    @Column(name = "customer_tier", length = 20)
    @Size(max = 20, message = "Customer tier must not exceed 20 characters")
    private String customerTier; // BRONZE, SILVER, GOLD, PLATINUM

    @Column(name = "account_activated")
    private Boolean accountActivated;

    @Column(name = "account_activation_date")
    private LocalDateTime accountActivationDate;

    @Column(name = "last_order_date")
    private LocalDateTime lastOrderDate;

    @Column(name = "total_orders_count")
    private Integer totalOrdersCount;

    @Column(name = "total_amount_spent")
    private Double totalAmountSpent;

    @Column(name = "credit_limit")
    private Double creditLimit;

    @Column(name = "payment_terms_days")
    private Integer paymentTermsDays;

    @Column(name = "profile_notes", length = 1000)
    @Size(max = 1000, message = "Profile notes must not exceed 1000 characters")
    private String profileNotes;
}