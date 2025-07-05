package com.gogidix.courier.corporate.customer.onboarding.controller;

import com.gogidix.courier.corporate.customer.onboarding.dto.*;
import com.gogidix.courier.corporate.customer.onboarding.enums.CorporateOnboardingStatus;
import com.gogidix.courier.corporate.customer.onboarding.service.impl.CorporateOnboardingServiceImplNew;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

/**
 * REST Controller for Corporate Customer Onboarding operations.
 * 
 * Provides comprehensive API endpoints for corporate customer registration,
 * verification, and account management processes.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/corporate-onboarding")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Corporate Onboarding", description = "Corporate customer onboarding management")
public class CorporateOnboardingController {

    private final CorporateOnboardingServiceImplNew corporateOnboardingService;

    @Operation(summary = "Create new corporate onboarding application")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Application created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "409", description = "Company already registered")
    })
    @PostMapping("/applications")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<CorporateOnboardingApplicationResponse> createApplication(
            @Valid @RequestBody CreateCorporateOnboardingApplicationRequest request,
            Principal principal) {
        
        log.info("Creating corporate onboarding application for company: {}", request.companyName());
        
        CorporateOnboardingApplicationResponse response = corporateOnboardingService
                .createApplication(request, principal.getName());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get corporate application by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Application retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Application not found")
    })
    @GetMapping("/applications/{applicationId}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN') or hasRole('SUPPORT_AGENT')")
    public ResponseEntity<CorporateOnboardingApplicationResponse> getApplication(
            @Parameter(description = "Application ID") @PathVariable UUID applicationId) {
        
        log.info("Retrieving corporate application: {}", applicationId);
        
        CorporateOnboardingApplicationResponse response = corporateOnboardingService
                .getApplication(applicationId);
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get corporate application by reference ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Application retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Application not found")
    })
    @GetMapping("/applications/reference/{referenceId}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN') or hasRole('SUPPORT_AGENT')")
    public ResponseEntity<CorporateOnboardingApplicationResponse> getApplicationByReferenceId(
            @Parameter(description = "Application reference ID") @PathVariable String referenceId) {
        
        log.info("Retrieving corporate application by reference: {}", referenceId);
        
        CorporateOnboardingApplicationResponse response = corporateOnboardingService
                .getApplicationByReferenceId(referenceId);
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all corporate applications with pagination and filtering")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Applications retrieved successfully")
    })
    @GetMapping("/applications")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPORT_AGENT')")
    public ResponseEntity<Page<CorporateOnboardingApplicationResponse>> getApplications(
            @Parameter(description = "Filter by application status") 
            @RequestParam(required = false) CorporateOnboardingStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.info("Retrieving corporate applications with status filter: {}", status);
        
        Page<CorporateOnboardingApplicationResponse> applications = corporateOnboardingService
                .getApplications(status, pageable);
        
        return ResponseEntity.ok(applications);
    }

    @Operation(summary = "Update corporate application")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Application updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "Application not found"),
        @ApiResponse(responseCode = "409", description = "Application cannot be updated in current status")
    })
    @PutMapping("/applications/{applicationId}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<CorporateOnboardingApplicationResponse> updateApplication(
            @Parameter(description = "Application ID") @PathVariable UUID applicationId,
            @Valid @RequestBody UpdateCorporateOnboardingApplicationRequest request,
            Principal principal) {
        
        log.info("Updating corporate application: {}", applicationId);
        
        CorporateOnboardingApplicationResponse response = corporateOnboardingService
                .updateApplication(applicationId, request, principal.getName());
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Submit application for review")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Application submitted successfully"),
        @ApiResponse(responseCode = "400", description = "Application cannot be submitted"),
        @ApiResponse(responseCode = "404", description = "Application not found")
    })
    @PostMapping("/applications/{applicationId}/submit")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<CorporateOnboardingApplicationResponse> submitApplication(
            @Parameter(description = "Application ID") @PathVariable UUID applicationId,
            Principal principal) {
        
        log.info("Submitting corporate application: {}", applicationId);
        
        CorporateOnboardingApplicationResponse response = corporateOnboardingService
                .submitApplication(applicationId, principal.getName());
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update application status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status transition"),
        @ApiResponse(responseCode = "404", description = "Application not found")
    })
    @PostMapping("/applications/{applicationId}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPORT_AGENT')")
    public ResponseEntity<CorporateOnboardingApplicationResponse> updateStatus(
            @Parameter(description = "Application ID") @PathVariable UUID applicationId,
            @Valid @RequestBody StatusUpdateRequest request,
            Principal principal) {
        
        log.info("Updating status for corporate application: {} to {}", applicationId, request.newStatus());
        
        CorporateOnboardingApplicationResponse response = corporateOnboardingService
                .updateStatus(applicationId, request, principal.getName());
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Approve corporate application")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Application approved successfully"),
        @ApiResponse(responseCode = "400", description = "Application cannot be approved"),
        @ApiResponse(responseCode = "404", description = "Application not found")
    })
    @PostMapping("/applications/{applicationId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CorporateOnboardingApplicationResponse> approveApplication(
            @Parameter(description = "Application ID") @PathVariable UUID applicationId,
            @Parameter(description = "Approval notes") @RequestParam(required = false) String notes,
            Principal principal) {
        
        log.info("Approving corporate application: {}", applicationId);
        
        CorporateOnboardingApplicationResponse response = corporateOnboardingService
                .approveApplication(applicationId, principal.getName(), notes);
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Reject corporate application")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Application rejected successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid rejection reason"),
        @ApiResponse(responseCode = "404", description = "Application not found")
    })
    @PostMapping("/applications/{applicationId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CorporateOnboardingApplicationResponse> rejectApplication(
            @Parameter(description = "Application ID") @PathVariable UUID applicationId,
            @Parameter(description = "Rejection reason") @RequestParam String reason,
            Principal principal) {
        
        log.info("Rejecting corporate application: {}", applicationId);
        
        CorporateOnboardingApplicationResponse response = corporateOnboardingService
                .rejectApplication(applicationId, principal.getName(), reason);
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Cancel corporate application")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Application cancelled successfully"),
        @ApiResponse(responseCode = "400", description = "Application cannot be cancelled"),
        @ApiResponse(responseCode = "404", description = "Application not found")
    })
    @PostMapping("/applications/{applicationId}/cancel")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<CorporateOnboardingApplicationResponse> cancelApplication(
            @Parameter(description = "Application ID") @PathVariable UUID applicationId,
            Principal principal) {
        
        log.info("Cancelling corporate application: {}", applicationId);
        
        CorporateOnboardingApplicationResponse response = corporateOnboardingService
                .cancelApplication(applicationId, principal.getName());
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Initiate KYB verification")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "KYB verification initiated successfully"),
        @ApiResponse(responseCode = "400", description = "Application not ready for KYB verification"),
        @ApiResponse(responseCode = "404", description = "Application not found")
    })
    @PostMapping("/applications/{applicationId}/kyb/initiate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('KYC_AGENT')")
    public ResponseEntity<CorporateOnboardingApplicationResponse> initiateKybVerification(
            @Parameter(description = "Application ID") @PathVariable UUID applicationId,
            Principal principal) {
        
        log.info("Initiating KYB verification for application: {}", applicationId);
        
        CorporateOnboardingApplicationResponse response = corporateOnboardingService
                .initiateKybVerification(applicationId, principal.getName());
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Process KYB verification result")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "KYB result processed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid verification data"),
        @ApiResponse(responseCode = "404", description = "Application not found")
    })
    @PostMapping("/applications/{applicationId}/kyb/result")
    @PreAuthorize("hasRole('ADMIN') or hasRole('KYC_AGENT')")
    public ResponseEntity<CorporateOnboardingApplicationResponse> processKybResult(
            @Parameter(description = "Application ID") @PathVariable UUID applicationId,
            @Parameter(description = "KYB verification ID") @RequestParam String kybVerificationId,
            @Parameter(description = "Verification approved") @RequestParam boolean approved,
            Principal principal) {
        
        log.info("Processing KYB result for application: {} - Approved: {}", applicationId, approved);
        
        CorporateOnboardingApplicationResponse response = corporateOnboardingService
                .processKybResult(applicationId, kybVerificationId, approved, principal.getName());
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Initiate credit assessment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Credit assessment initiated successfully"),
        @ApiResponse(responseCode = "400", description = "Application not ready for credit assessment"),
        @ApiResponse(responseCode = "404", description = "Application not found")
    })
    @PostMapping("/applications/{applicationId}/credit-assessment/initiate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CREDIT_AGENT')")
    public ResponseEntity<CorporateOnboardingApplicationResponse> initiateCreditAssessment(
            @Parameter(description = "Application ID") @PathVariable UUID applicationId,
            Principal principal) {
        
        log.info("Initiating credit assessment for application: {}", applicationId);
        
        CorporateOnboardingApplicationResponse response = corporateOnboardingService
                .initiateCreditAssessment(applicationId, principal.getName());
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Check if company is already registered")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Check completed successfully")
    })
    @GetMapping("/companies/check-registration")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<Boolean> checkCompanyRegistration(
            @Parameter(description = "Company email") @RequestParam String companyEmail,
            @Parameter(description = "Company registration number") @RequestParam String registrationNumber) {
        
        log.info("Checking company registration for email: {} and registration: {}", companyEmail, registrationNumber);
        
        boolean isRegistered = corporateOnboardingService
                .isCompanyAlreadyRegistered(companyEmail, registrationNumber);
        
        return ResponseEntity.ok(isRegistered);
    }
}