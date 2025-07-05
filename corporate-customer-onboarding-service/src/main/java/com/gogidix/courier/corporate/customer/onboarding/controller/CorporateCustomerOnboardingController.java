package com.gogidix.courier.corporate.customer.onboarding.controller;

import com.gogidix.courier.corporate.customer.onboarding.dto.*;
import com.gogidix.courier.corporate.customer.onboarding.service.CorporateOnboardingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Corporate Customer Onboarding operations.
 * 
 * This controller handles all corporate customer onboarding API endpoints for businesses
 * registering for courier services via www.exaltcourier.com
 */
@RestController
@RequestMapping("/api/v1/corporate-customer-onboarding")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Corporate Customer Onboarding", description = "APIs for managing corporate customer onboarding process")
public class CorporateCustomerOnboardingController {

    private final CorporateOnboardingService corporateOnboardingService;

    @Operation(summary = "Create new corporate customer onboarding application")
    @PostMapping("/applications")
    public ResponseEntity<CorporateOnboardingApplicationResponse> createApplication(
            @Valid @RequestBody CreateCorporateOnboardingApplicationRequest request) {
        log.info("Creating corporate customer onboarding application for company: {}", request.companyName());
        CorporateOnboardingApplicationResponse response = corporateOnboardingService.createApplication(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get corporate customer onboarding application by reference ID")
    @GetMapping("/applications/{referenceId}")
    public ResponseEntity<CorporateOnboardingApplicationResponse> getApplication(
            @PathVariable String referenceId) {
        log.info("Retrieving corporate customer onboarding application: {}", referenceId);
        CorporateOnboardingApplicationResponse response = corporateOnboardingService.getApplicationByReferenceId(referenceId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update corporate customer onboarding application")
    @PutMapping("/applications/{referenceId}")
    public ResponseEntity<CorporateOnboardingApplicationResponse> updateApplication(
            @PathVariable String referenceId,
            @Valid @RequestBody UpdateCorporateOnboardingApplicationRequest request) {
        log.info("Updating corporate customer onboarding application: {}", referenceId);
        CorporateOnboardingApplicationResponse response = corporateOnboardingService.updateApplication(referenceId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Submit corporate customer onboarding application for review")
    @PostMapping("/applications/{referenceId}/submit")
    public ResponseEntity<Void> submitApplication(@PathVariable String referenceId) {
        log.info("Submitting corporate customer onboarding application: {}", referenceId);
        corporateOnboardingService.submitApplication(referenceId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Start KYB verification process")
    @PostMapping("/applications/{referenceId}/kyb/initiate")
    public ResponseEntity<KybInitiationResponse> initiateKybVerification(@PathVariable String referenceId) {
        log.info("Initiating KYB verification for corporate application: {}", referenceId);
        KybInitiationResponse response = corporateOnboardingService.initiateKybVerification(referenceId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get KYB verification status")
    @GetMapping("/applications/{referenceId}/kyb/status")
    public ResponseEntity<KybStatusResponse> getKybStatus(@PathVariable String referenceId) {
        log.info("Getting KYB status for corporate application: {}", referenceId);
        KybStatusResponse response = corporateOnboardingService.getKybStatus(referenceId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Initiate credit assessment")
    @PostMapping("/applications/{referenceId}/credit-assessment/initiate")
    public ResponseEntity<CreditAssessmentResponse> initiateCreditAssessment(
            @PathVariable String referenceId,
            @Valid @RequestBody CreditAssessmentRequest request) {
        log.info("Initiating credit assessment for corporate application: {}", referenceId);
        CreditAssessmentResponse response = corporateOnboardingService.initiateCreditAssessment(referenceId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Set commercial terms")
    @PostMapping("/applications/{referenceId}/commercial-terms")
    public ResponseEntity<Void> setCommercialTerms(
            @PathVariable String referenceId,
            @Valid @RequestBody CommercialTermsRequest request) {
        log.info("Setting commercial terms for corporate application: {}", referenceId);
        corporateOnboardingService.setCommercialTerms(referenceId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Generate service contract")
    @PostMapping("/applications/{referenceId}/contract/generate")
    public ResponseEntity<ContractGenerationResponse> generateServiceContract(@PathVariable String referenceId) {
        log.info("Generating service contract for corporate application: {}", referenceId);
        ContractGenerationResponse response = corporateOnboardingService.generateServiceContract(referenceId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Record contract signature")
    @PostMapping("/applications/{referenceId}/contract/signed")
    public ResponseEntity<Void> recordContractSignature(
            @PathVariable String referenceId,
            @Valid @RequestBody ContractSignatureRequest request) {
        log.info("Recording contract signature for corporate application: {}", referenceId);
        corporateOnboardingService.recordContractSignature(referenceId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Approve corporate customer onboarding application")
    @PostMapping("/applications/{referenceId}/approve")
    public ResponseEntity<Void> approveApplication(
            @PathVariable String referenceId,
            @Valid @RequestBody ApplicationDecisionRequest request) {
        log.info("Approving corporate customer onboarding application: {}", referenceId);
        corporateOnboardingService.approveApplication(referenceId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Reject corporate customer onboarding application")
    @PostMapping("/applications/{referenceId}/reject")
    public ResponseEntity<Void> rejectApplication(
            @PathVariable String referenceId,
            @Valid @RequestBody ApplicationDecisionRequest request) {
        log.info("Rejecting corporate customer onboarding application: {}", referenceId);
        corporateOnboardingService.rejectApplication(referenceId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get application status history")
    @GetMapping("/applications/{referenceId}/status-history")
    public ResponseEntity<List<CorporateApplicationStatusHistoryResponse>> getApplicationStatusHistory(
            @PathVariable String referenceId) {
        log.info("Getting status history for corporate application: {}", referenceId);
        List<CorporateApplicationStatusHistoryResponse> response = corporateOnboardingService.getApplicationStatusHistory(referenceId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all corporate applications (admin endpoint)")
    @GetMapping("/applications")
    public ResponseEntity<List<CorporateOnboardingApplicationResponse>> getAllApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String companyName) {
        log.info("Getting all corporate customer onboarding applications - page: {}, size: {}, status: {}, company: {}", page, size, status, companyName);
        List<CorporateOnboardingApplicationResponse> response = corporateOnboardingService.getAllApplications(page, size, status, companyName);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Activate corporate customer account")
    @PostMapping("/applications/{referenceId}/activate")
    public ResponseEntity<Void> activateCorporateAccount(@PathVariable String referenceId) {
        log.info("Activating corporate customer account for application: {}", referenceId);
        corporateOnboardingService.activateCorporateAccount(referenceId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Suspend corporate customer account")
    @PostMapping("/applications/{referenceId}/suspend")
    public ResponseEntity<Void> suspendCorporateAccount(
            @PathVariable String referenceId,
            @Valid @RequestBody CorporateAccountActionRequest request) {
        log.info("Suspending corporate customer account for application: {}", referenceId);
        corporateOnboardingService.suspendCorporateAccount(referenceId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get pricing proposal for corporate customer")
    @PostMapping("/applications/{referenceId}/pricing-proposal")
    public ResponseEntity<PricingProposalResponse> generatePricingProposal(
            @PathVariable String referenceId,
            @Valid @RequestBody PricingProposalRequest request) {
        log.info("Generating pricing proposal for corporate application: {}", referenceId);
        PricingProposalResponse response = corporateOnboardingService.generatePricingProposal(referenceId, request);
        return ResponseEntity.ok(response);
    }
}