package com.gogidix.courier.customer.onboarding.controller;

import com.gogidix.courier.customer.onboarding.dto.*;
import com.gogidix.courier.customer.onboarding.service.CustomerOnboardingService;
import com.gogidix.ecosystem.shared.exceptions.ResourceNotFoundException;
import com.gogidix.ecosystem.shared.exceptions.ValidationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for Customer Onboarding operations.
 * 
 * This controller handles all customer onboarding API endpoints for individual customers
 * registering for courier services via www.exaltcourier.com
 */
@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Customer Onboarding", description = "APIs for managing customer onboarding process")
@SecurityRequirement(name = "bearerAuth")
public class CustomerOnboardingController {

    private final CustomerOnboardingService customerOnboardingService;

    // ========== PUBLIC ENDPOINTS (No Authentication Required) ==========
    
    @Operation(summary = "Register new customer (Public endpoint)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Customer registration initiated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid registration data"),
        @ApiResponse(responseCode = "409", description = "Customer already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<CustomerOnboardingApplicationResponse> registerCustomer(
            @Valid @RequestBody CreateCustomerOnboardingApplicationRequest request) {
        log.info("New customer registration initiated for email: {}", request.customerEmail());
        CustomerOnboardingApplicationResponse response = customerOnboardingService.createApplication(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @Operation(summary = "Check email availability (Public endpoint)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Email availability checked"),
        @ApiResponse(responseCode = "400", description = "Invalid email format")
    })
    @GetMapping("/check-availability")
    public ResponseEntity<Boolean> checkEmailAvailability(
            @Parameter(description = "Email to check availability")
            @RequestParam @Email @NotBlank String email) {
        log.info("Checking email availability for: {}", email);
        boolean isAvailable = customerOnboardingService.isEmailAvailable(email);
        return ResponseEntity.ok(isAvailable);
    }
    
    @Operation(summary = "Verify customer email (Public endpoint)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Email verified successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid verification token"),
        @ApiResponse(responseCode = "404", description = "Application not found")
    })
    @PostMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(
            @Parameter(description = "Email verification token")
            @RequestParam @NotBlank String token) {
        log.info("Verifying email with token: {}", token);
        customerOnboardingService.verifyEmail(token);
        return ResponseEntity.ok("Email verified successfully");
    }
    
    // ========== AUTHENTICATED ENDPOINTS ==========
    
    @Operation(summary = "Create new customer onboarding application")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Application created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid application data"),
        @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @PostMapping("/onboarding/applications")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'SUPPORT_AGENT')")
    public ResponseEntity<CustomerOnboardingApplicationResponse> createApplication(
            @Valid @RequestBody CreateCustomerOnboardingApplicationRequest request) {
        log.info("Creating customer onboarding application for email: {}", request.customerEmail());
        CustomerOnboardingApplicationResponse response = customerOnboardingService.createApplication(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get customer onboarding application by reference ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Application retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Application not found"),
        @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @GetMapping("/onboarding/applications/{referenceId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'SUPPORT_AGENT')")
    public ResponseEntity<CustomerOnboardingApplicationResponse> getApplication(
            @Parameter(description = "Application reference ID")
            @PathVariable String referenceId) {
        log.info("Retrieving customer onboarding application: {}", referenceId);
        CustomerOnboardingApplicationResponse response = customerOnboardingService.getApplicationByReferenceId(referenceId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update customer onboarding application")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Application updated successfully"),
        @ApiResponse(responseCode = "404", description = "Application not found"),
        @ApiResponse(responseCode = "400", description = "Invalid update data"),
        @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @PutMapping("/onboarding/applications/{referenceId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'SUPPORT_AGENT')")
    public ResponseEntity<CustomerOnboardingApplicationResponse> updateApplication(
            @Parameter(description = "Application reference ID")
            @PathVariable String referenceId,
            @Valid @RequestBody UpdateCustomerOnboardingApplicationRequest request) {
        log.info("Updating customer onboarding application: {}", referenceId);
        CustomerOnboardingApplicationResponse response = customerOnboardingService.updateApplication(referenceId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Submit customer onboarding application for review")
    @PostMapping("/applications/{referenceId}/submit")
    public ResponseEntity<Void> submitApplication(@PathVariable String referenceId) {
        log.info("Submitting customer onboarding application: {}", referenceId);
        customerOnboardingService.submitApplication(referenceId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Start KYC verification process")
    @PostMapping("/applications/{referenceId}/kyc/initiate")
    public ResponseEntity<KycInitiationResponse> initiateKycVerification(@PathVariable String referenceId) {
        log.info("Initiating KYC verification for application: {}", referenceId);
        KycInitiationResponse response = customerOnboardingService.initiateKycVerification(referenceId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get KYC verification status")
    @GetMapping("/applications/{referenceId}/kyc/status")
    public ResponseEntity<KycStatusResponse> getKycStatus(@PathVariable String referenceId) {
        log.info("Getting KYC status for application: {}", referenceId);
        KycStatusResponse response = customerOnboardingService.getKycStatus(referenceId);
        return ResponseEntity.ok(response);
    }

    // ========== ADMIN ENDPOINTS ==========
    
    @Operation(summary = "Approve customer onboarding application (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Application approved successfully"),
        @ApiResponse(responseCode = "404", description = "Application not found"),
        @ApiResponse(responseCode = "403", description = "Admin access required")
    })
    @PostMapping("/onboarding/applications/{referenceId}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPORT_AGENT')")
    public ResponseEntity<Void> approveApplication(
            @Parameter(description = "Application reference ID")
            @PathVariable String referenceId,
            @Valid @RequestBody ApplicationDecisionRequest request) {
        log.info("Approving customer onboarding application: {}", referenceId);
        customerOnboardingService.approveApplication(referenceId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Reject customer onboarding application (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Application rejected successfully"),
        @ApiResponse(responseCode = "404", description = "Application not found"),
        @ApiResponse(responseCode = "403", description = "Admin access required")
    })
    @PostMapping("/onboarding/applications/{referenceId}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPORT_AGENT')")
    public ResponseEntity<Void> rejectApplication(
            @Parameter(description = "Application reference ID")
            @PathVariable String referenceId,
            @Valid @RequestBody ApplicationDecisionRequest request) {
        log.info("Rejecting customer onboarding application: {}", referenceId);
        customerOnboardingService.rejectApplication(referenceId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get application status history")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status history retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Application not found")
    })
    @GetMapping("/onboarding/applications/{referenceId}/status-history")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'SUPPORT_AGENT')")
    public ResponseEntity<List<ApplicationStatusHistoryResponse>> getApplicationStatusHistory(
            @Parameter(description = "Application reference ID")
            @PathVariable String referenceId) {
        log.info("Getting status history for application: {}", referenceId);
        List<ApplicationStatusHistoryResponse> response = customerOnboardingService.getApplicationStatusHistory(referenceId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all applications with pagination (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Applications retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Admin access required")
    })
    @GetMapping("/onboarding/applications")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPORT_AGENT')")
    public ResponseEntity<Page<CustomerOnboardingApplicationResponse>> getAllApplications(
            Pageable pageable,
            @Parameter(description = "Filter by application status")
            @RequestParam(required = false) String status) {
        log.info("Getting all customer onboarding applications - page: {}, size: {}, status: {}", 
                 pageable.getPageNumber(), pageable.getPageSize(), status);
        Page<CustomerOnboardingApplicationResponse> response = customerOnboardingService.getAllApplications(pageable, status);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Activate customer account (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customer account activated successfully"),
        @ApiResponse(responseCode = "404", description = "Application not found"),
        @ApiResponse(responseCode = "403", description = "Admin access required")
    })
    @PostMapping("/onboarding/applications/{referenceId}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activateCustomerAccount(
            @Parameter(description = "Application reference ID")
            @PathVariable String referenceId) {
        log.info("Activating customer account for application: {}", referenceId);
        customerOnboardingService.activateCustomerAccount(referenceId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Suspend customer account (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customer account suspended successfully"),
        @ApiResponse(responseCode = "404", description = "Application not found"),
        @ApiResponse(responseCode = "403", description = "Admin access required")
    })
    @PostMapping("/onboarding/applications/{referenceId}/suspend")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> suspendCustomerAccount(
            @Parameter(description = "Application reference ID")
            @PathVariable String referenceId,
            @Valid @RequestBody CustomerAccountActionRequest request) {
        log.info("Suspending customer account for application: {}", referenceId);
        customerOnboardingService.suspendCustomerAccount(referenceId, request);
        return ResponseEntity.ok().build();
    }
    
    // ========== KYC ENDPOINTS ==========
    
    @Operation(summary = "Start KYC verification process")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "KYC verification initiated successfully"),
        @ApiResponse(responseCode = "404", description = "Application not found"),
        @ApiResponse(responseCode = "400", description = "KYC already in progress")
    })
    @PostMapping("/kyc/{referenceId}/initiate")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'SUPPORT_AGENT')")
    public ResponseEntity<KycInitiationResponse> initiateKycVerification(
            @Parameter(description = "Application reference ID")
            @PathVariable String referenceId) {
        log.info("Initiating KYC verification for application: {}", referenceId);
        KycInitiationResponse response = customerOnboardingService.initiateKycVerification(referenceId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get KYC verification status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "KYC status retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Application not found")
    })
    @GetMapping("/kyc/{referenceId}/status")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'SUPPORT_AGENT')")
    public ResponseEntity<KycStatusResponse> getKycStatus(
            @Parameter(description = "Application reference ID")
            @PathVariable String referenceId) {
        log.info("Getting KYC status for application: {}", referenceId);
        KycStatusResponse response = customerOnboardingService.getKycStatus(referenceId);
        return ResponseEntity.ok(response);
    }
    
    // ========== CUSTOMER WORKFLOW ENDPOINTS ==========
    
    @Operation(summary = "Submit customer onboarding application for review")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Application submitted successfully"),
        @ApiResponse(responseCode = "404", description = "Application not found"),
        @ApiResponse(responseCode = "400", description = "Application not ready for submission")
    })
    @PostMapping("/onboarding/applications/{referenceId}/submit")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'SUPPORT_AGENT')")
    public ResponseEntity<Void> submitApplication(
            @Parameter(description = "Application reference ID")
            @PathVariable String referenceId) {
        log.info("Submitting customer onboarding application: {}", referenceId);
        customerOnboardingService.submitApplication(referenceId);
        return ResponseEntity.ok().build();
    }
}