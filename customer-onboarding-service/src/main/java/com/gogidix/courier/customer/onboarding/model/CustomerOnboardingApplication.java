package com.gogidix.courier.customer.onboarding.model;

import com.gogidix.ecosystem.shared.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Entity representing a customer onboarding application.
 * 
 * This entity tracks the complete customer onboarding journey for individual customers
 * registering for courier services via www.exaltcourier.com
 */
@Entity
@Table(name = "customer_onboarding_applications", indexes = {
    @Index(name = "idx_application_reference_id", columnList = "application_reference_id"),
    @Index(name = "idx_customer_email", columnList = "customer_email"),
    @Index(name = "idx_application_status", columnList = "application_status"),
    @Index(name = "idx_auth_service_user_id", columnList = "auth_service_user_id")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerOnboardingApplication extends BaseEntity {

    @Column(name = "application_reference_id", unique = true, nullable = false, length = 50)
    @NotBlank(message = "Application reference ID is required")
    @Size(max = 50, message = "Application reference ID must not exceed 50 characters")
    private String applicationReferenceId;

    @Column(name = "customer_email", nullable = false, length = 100)
    @NotBlank(message = "Customer email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String customerEmail;

    @Column(name = "customer_phone", nullable = false, length = 20)
    @NotBlank(message = "Customer phone is required")
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String customerPhone;

    @Column(name = "first_name", nullable = false, length = 50)
    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    @Column(name = "date_of_birth")
    private String dateOfBirth;

    @Column(name = "national_id")
    private String nationalId;

    @Column(name = "address_line1")
    private String addressLine1;

    @Column(name = "address_line2")
    private String addressLine2;

    @Column(name = "city")
    private String city;

    @Column(name = "state_province")
    private String stateProvince;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "country")
    private String country;

    @Enumerated(EnumType.STRING)
    @Column(name = "application_status", nullable = false, length = 30)
    @NotNull(message = "Application status is required")
    private CustomerOnboardingStatus applicationStatus;

    @Column(name = "kyc_verification_id", length = 100)
    @Size(max = 100, message = "KYC verification ID must not exceed 100 characters")
    private String kycVerificationId;

    @Column(name = "auth_service_user_id", length = 100)
    @Size(max = 100, message = "Auth service user ID must not exceed 100 characters")
    private String authServiceUserId;

    @Column(name = "billing_customer_id", length = 100)
    @Size(max = 100, message = "Billing customer ID must not exceed 100 characters")
    private String billingCustomerId;

    @Column(name = "preferred_communication_method")
    private String preferredCommunicationMethod;

    @Column(name = "marketing_consent")
    private Boolean marketingConsent;

    @Column(name = "terms_accepted")
    private Boolean termsAccepted;

    @Column(name = "privacy_policy_accepted")
    private Boolean privacyPolicyAccepted;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CustomerApplicationStatusHistory> statusHistory;

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CustomerVerificationDocument> verificationDocuments;
}