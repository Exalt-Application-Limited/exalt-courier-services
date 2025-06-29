package com.exalt.courier.onboarding.controller;

import com.exalt.courier.onboarding.dto.OnboardingApplicationRequest;
import com.exalt.courier.onboarding.dto.OnboardingApplicationResponse;
import com.exalt.courier.onboarding.model.ApplicationStatus;
import com.exalt.courier.onboarding.model.OnboardingApplication;
import com.exalt.courier.onboarding.service.OnboardingApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

/**
 * REST controller for managing courier onboarding applications.
 */
@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
@Slf4j
public class OnboardingApplicationController {

    private final OnboardingApplicationService applicationService;

    /**
     * Create a new onboarding application
     *
     * @param request Application creation request
     * @return The created application
     */
    @PostMapping
    public ResponseEntity<OnboardingApplicationResponse> createApplication(
            @Valid @RequestBody OnboardingApplicationRequest request) {
        
        log.info("Creating new onboarding application for: {}", request.getEmail());
        
        // Convert request to domain model
        OnboardingApplication application = convertToEntity(request);
        
        // Save the application
        OnboardingApplication savedApplication = applicationService.createApplication(application);
        
        // Convert to response DTO
        OnboardingApplicationResponse response = convertToDto(savedApplication);
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get an onboarding application by reference ID
     *
     * @param referenceId Application reference ID
     * @return The application if found
     */
    @GetMapping("/{referenceId}")
    public ResponseEntity<OnboardingApplicationResponse> getApplication(
            @PathVariable String referenceId) {
        
        log.info("Fetching onboarding application with reference ID: {}", referenceId);
        
        return applicationService.getApplicationByReferenceId(referenceId)
                .map(application -> ResponseEntity.ok(convertToDto(application)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update an onboarding application
     *
     * @param referenceId Application reference ID
     * @param request Updated application data
     * @return The updated application
     */
    @PutMapping("/{referenceId}")
    public ResponseEntity<OnboardingApplicationResponse> updateApplication(
            @PathVariable String referenceId,
            @Valid @RequestBody OnboardingApplicationRequest request) {
        
        log.info("Updating onboarding application with reference ID: {}", referenceId);
        
        // Convert request to domain model
        OnboardingApplication application = convertToEntity(request);
        
        // Update the application
        OnboardingApplication updatedApplication = applicationService.updateApplication(referenceId, application);
        
        // Convert to response DTO
        OnboardingApplicationResponse response = convertToDto(updatedApplication);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Submit an onboarding application for review
     *
     * @param referenceId Application reference ID
     * @return The submitted application
     */
    @PostMapping("/{referenceId}/submit")
    public ResponseEntity<OnboardingApplicationResponse> submitApplication(
            @PathVariable String referenceId) {
        
        log.info("Submitting onboarding application with reference ID: {}", referenceId);
        
        OnboardingApplication submittedApplication = applicationService.submitApplication(referenceId);
        OnboardingApplicationResponse response = convertToDto(submittedApplication);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Start the review process for an application
     *
     * @param referenceId Application reference ID
     * @param reviewerId ID of the reviewer
     * @return The application with updated status
     */
    @PostMapping("/{referenceId}/review/start")
    public ResponseEntity<OnboardingApplicationResponse> startReview(
            @PathVariable String referenceId,
            @RequestParam String reviewerId) {
        
        log.info("Starting review for application with reference ID: {} by reviewer: {}", 
                referenceId, reviewerId);
        
        OnboardingApplication application = applicationService.startApplicationReview(referenceId, reviewerId);
        OnboardingApplicationResponse response = convertToDto(application);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Approve an onboarding application
     *
     * @param referenceId Application reference ID
     * @param reviewerId ID of the reviewer
     * @param notes Additional notes for the approval
     * @return The approved application
     */
    @PostMapping("/{referenceId}/approve")
    public ResponseEntity<OnboardingApplicationResponse> approveApplication(
            @PathVariable String referenceId,
            @RequestParam String reviewerId,
            @RequestParam(required = false) String notes) {
        
        log.info("Approving application with reference ID: {} by reviewer: {}", 
                referenceId, reviewerId);
        
        OnboardingApplication application = applicationService.approveApplication(referenceId, reviewerId, notes);
        OnboardingApplicationResponse response = convertToDto(application);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Reject an onboarding application
     *
     * @param referenceId Application reference ID
     * @param reviewerId ID of the reviewer
     * @param rejectionReason Reason for rejection
     * @param notes Additional notes for the rejection
     * @return The rejected application
     */
    @PostMapping("/{referenceId}/reject")
    public ResponseEntity<OnboardingApplicationResponse> rejectApplication(
            @PathVariable String referenceId,
            @RequestParam String reviewerId,
            @RequestParam String rejectionReason,
            @RequestParam(required = false) String notes) {
        
        log.info("Rejecting application with reference ID: {} by reviewer: {} for reason: {}", 
                referenceId, reviewerId, rejectionReason);
        
        OnboardingApplication application = applicationService.rejectApplication(
                referenceId, reviewerId, rejectionReason, notes);
        OnboardingApplicationResponse response = convertToDto(application);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Request additional information for an application
     *
     * @param referenceId Application reference ID
     * @param reviewerId ID of the reviewer
     * @param requestDetails Details of the requested information
     * @return The updated application
     */
    @PostMapping("/{referenceId}/request-info")
    public ResponseEntity<OnboardingApplicationResponse> requestAdditionalInformation(
            @PathVariable String referenceId,
            @RequestParam String reviewerId,
            @RequestParam String requestDetails) {
        
        log.info("Requesting additional information for application with reference ID: {} by reviewer: {}", 
                referenceId, reviewerId);
        
        OnboardingApplication application = applicationService.requestAdditionalInformation(
                referenceId, reviewerId, requestDetails);
        OnboardingApplicationResponse response = convertToDto(application);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get all applications with a specific status
     *
     * @param status Application status
     * @param pageable Pagination information
     * @return Page of applications with the specified status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<OnboardingApplicationResponse>> getApplicationsByStatus(
            @PathVariable ApplicationStatus status,
            Pageable pageable) {
        
        log.info("Fetching applications with status: {}, page: {}, size: {}", 
                status, pageable.getPageNumber(), pageable.getPageSize());
        
        Page<OnboardingApplication> applications = applicationService.getApplicationsByStatus(status, pageable);
        Page<OnboardingApplicationResponse> responsePage = applications.map(this::convertToDto);
        
        return ResponseEntity.ok(responsePage);
    }

    /**
     * Search for applications
     *
     * @param query Search query
     * @param pageable Pagination information
     * @return Page of applications matching the search query
     */
    @GetMapping("/search")
    public ResponseEntity<Page<OnboardingApplicationResponse>> searchApplications(
            @RequestParam String query,
            Pageable pageable) {
        
        log.info("Searching for applications with query: {}, page: {}, size: {}", 
                query, pageable.getPageNumber(), pageable.getPageSize());
        
        Page<OnboardingApplication> applications = applicationService.searchApplications(query, pageable);
        Page<OnboardingApplicationResponse> responsePage = applications.map(this::convertToDto);
        
        return ResponseEntity.ok(responsePage);
    }

    /**
     * Get application status statistics
     *
     * @return Map of status to count
     */
    @GetMapping("/stats/status")
    public ResponseEntity<Map<ApplicationStatus, Long>> getApplicationStatusStats() {
        log.info("Fetching application status statistics");
        
        Map<ApplicationStatus, Long> stats = applicationService.getApplicationStatusCounts();
        return ResponseEntity.ok(stats);
    }

    /**
     * Convert DTO to entity
     *
     * @param request Request DTO
     * @return Entity
     */
    private OnboardingApplication convertToEntity(OnboardingApplicationRequest request) {
        return OnboardingApplication.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .dateOfBirth(request.getDateOfBirth())
                .streetAddress(request.getStreetAddress())
                .city(request.getCity())
                .stateProvince(request.getStateProvince())
                .postalCode(request.getPostalCode())
                .country(request.getCountry())
                .transportationType(request.getTransportationType())
                .vehicleMake(request.getVehicleMake())
                .vehicleModel(request.getVehicleModel())
                .vehicleYear(request.getVehicleYear())
                .vehicleColor(request.getVehicleColor())
                .vehicleLicensePlate(request.getVehicleLicensePlate())
                .hasValidDriversLicense(request.getHasValidDriversLicense())
                .hasVehicleInsurance(request.getHasVehicleInsurance())
                .canProvideBackgroundCheck(request.getCanProvideBackgroundCheck())
                .emergencyContactName(request.getEmergencyContactName())
                .emergencyContactPhone(request.getEmergencyContactPhone())
                .emergencyContactRelationship(request.getEmergencyContactRelationship())
                .agreeToTerms(request.getAgreeToTerms())
                .additionalNotes(request.getAdditionalNotes())
                .build();
    }

    /**
     * Convert entity to DTO
     *
     * @param application Entity
     * @return Response DTO
     */
    private OnboardingApplicationResponse convertToDto(OnboardingApplication application) {
        // This is a simplified conversion - in a real implementation, you'd map all fields
        // and potentially use a mapping library like MapStruct
        
        return OnboardingApplicationResponse.builder()
                .referenceId(application.getReferenceId())
                .firstName(application.getFirstName())
                .lastName(application.getLastName())
                .email(application.getEmail())
                .phoneNumber(application.getPhoneNumber())
                .dateOfBirth(application.getDateOfBirth())
                .streetAddress(application.getStreetAddress())
                .city(application.getCity())
                .stateProvince(application.getStateProvince())
                .postalCode(application.getPostalCode())
                .country(application.getCountry())
                .transportationType(application.getTransportationType())
                .vehicleMake(application.getVehicleMake())
                .vehicleModel(application.getVehicleModel())
                .vehicleYear(application.getVehicleYear())
                .vehicleColor(application.getVehicleColor())
                .vehicleLicensePlate(application.getVehicleLicensePlate())
                .hasValidDriversLicense(application.getHasValidDriversLicense())
                .hasVehicleInsurance(application.getHasVehicleInsurance())
                .canProvideBackgroundCheck(application.getCanProvideBackgroundCheck())
                .emergencyContactName(application.getEmergencyContactName())
                .emergencyContactPhone(application.getEmergencyContactPhone())
                .emergencyContactRelationship(application.getEmergencyContactRelationship())
                .status(application.getStatus())
                .createdAt(application.getCreatedAt())
                .updatedAt(application.getUpdatedAt())
                .submittedAt(application.getSubmittedAt())
                .backgroundCheckStatus(application.getBackgroundCheckStatus())
                .backgroundCheckReferenceId(application.getBackgroundCheckReferenceId())
                .reviewerId(application.getReviewerId())
                .reviewedAt(application.getReviewedAt())
                .rejectionReason(application.getRejectionReason())
                .rejectionNotes(application.getRejectionNotes())
                .additionalNotes(application.getAdditionalNotes())
                // TODO: Map documents and status history
                .build();
    }
}
