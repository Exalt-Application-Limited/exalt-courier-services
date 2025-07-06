package com.gogidix.courier.courier.drivermobileapp.controller;

import com.socialecommerceecosystem.drivermobileapp.dto.tracking.DeliveryConfirmationDTO;
import com.socialecommerceecosystem.drivermobileapp.dto.tracking.TrackingEventDTO;
import com.socialecommerceecosystem.drivermobileapp.dto.tracking.TrackingInfoDTO;
import com.socialecommerceecosystem.drivermobileapp.service.TrackingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * REST controller for tracking operations in the driver mobile app.
 */
@RestController
@RequestMapping("/api/v1/tracking")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Tracking", description = "Package tracking operations for mobile app")
public class TrackingController {
    
    private final TrackingService trackingService;
    
    /**
     * Get tracking information for a tracking number.
     *
     * @param trackingNumber the tracking number
     * @return the tracking information
     */
    @GetMapping("/{trackingNumber}")
    @Operation(
        summary = "Get tracking information",
        description = "Retrieves tracking information for a specific tracking number",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "Tracking information retrieved successfully",
                content = @Content(schema = @Schema(implementation = TrackingInfoDTO.class))
            ),
            @ApiResponse(responseCode = "404", description = "Tracking information not found")
        }
    )
    @PreAuthorize("hasRole('DRIVER') or hasRole('ADMIN')")
    public ResponseEntity<TrackingInfoDTO> getTrackingInfo(@PathVariable String trackingNumber) {
        log.info("REST request to get tracking info for tracking number: {}", trackingNumber);
        return ResponseEntity.ok(trackingService.getTrackingInfo(trackingNumber));
    }
    
    /**
     * Update the status of a package.
     *
     * @param packageId the package ID
     * @param statusUpdate the status update data
     * @return the updated tracking information
     */
    @PutMapping("/packages/{packageId}/status")
    @Operation(
        summary = "Update package status",
        description = "Updates the status of a package with current location and description",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "Package status updated successfully",
                content = @Content(schema = @Schema(implementation = TrackingInfoDTO.class))
            ),
            @ApiResponse(responseCode = "404", description = "Package not found")
        }
    )
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<TrackingInfoDTO> updatePackageStatus(
            @PathVariable String packageId,
            @Valid @RequestBody Map<String, String> statusUpdate) {
        
        log.info("REST request to update status for package: {}", packageId);
        
        String status = statusUpdate.get("status");
        String location = statusUpdate.get("location");
        String description = statusUpdate.get("description");
        
        return ResponseEntity.ok(trackingService.updatePackageStatus(packageId, status, location, description));
    }
    
    /**
     * Confirm delivery of a package.
     *
     * @param packageId the package ID
     * @param confirmation the delivery confirmation data
     * @return the updated tracking information
     */
    @PostMapping("/packages/{packageId}/confirm-delivery")
    @Operation(
        summary = "Confirm delivery",
        description = "Confirms delivery of a package with proof of delivery",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "Delivery confirmed successfully",
                content = @Content(schema = @Schema(implementation = TrackingInfoDTO.class))
            ),
            @ApiResponse(responseCode = "404", description = "Package not found")
        }
    )
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<TrackingInfoDTO> confirmDelivery(
            @PathVariable String packageId,
            @Valid @RequestBody DeliveryConfirmationDTO confirmation) {
        
        log.info("REST request to confirm delivery for package: {}", packageId);
        
        // Ensure package ID is set
        confirmation.setPackageId(packageId);
        
        return ResponseEntity.ok(trackingService.confirmDelivery(confirmation));
    }
    
    /**
     * Add a tracking event.
     *
     * @param packageId the package ID
     * @param event the tracking event data
     * @return the updated tracking information
     */
    @PostMapping("/packages/{packageId}/events")
    @Operation(
        summary = "Add tracking event",
        description = "Adds a new tracking event for a package",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "Tracking event added successfully",
                content = @Content(schema = @Schema(implementation = TrackingInfoDTO.class))
            ),
            @ApiResponse(responseCode = "404", description = "Package not found")
        }
    )
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<TrackingInfoDTO> addTrackingEvent(
            @PathVariable String packageId,
            @Valid @RequestBody TrackingEventDTO event) {
        
        log.info("REST request to add tracking event for package: {}", packageId);
        
        // Ensure package ID is set
        event.setPackageId(packageId);
        
        return ResponseEntity.ok(trackingService.addTrackingEvent(event));
    }
    
    /**
     * Get all tracking events for a package.
     *
     * @param packageId the package ID
     * @return list of tracking events
     */
    @GetMapping("/packages/{packageId}/events")
    @Operation(
        summary = "Get tracking events",
        description = "Retrieves all tracking events for a specific package",
        responses = {
            @ApiResponse(responseCode = "200", description = "Tracking events retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Package not found")
        }
    )
    @PreAuthorize("hasRole('DRIVER') or hasRole('ADMIN')")
    public ResponseEntity<List<TrackingEventDTO>> getTrackingEvents(@PathVariable String packageId) {
        log.info("REST request to get tracking events for package: {}", packageId);
        return ResponseEntity.ok(trackingService.getTrackingEvents(packageId));
    }
    
    /**
     * Store tracking event locally when offline.
     *
     * @param packageId the package ID
     * @param event the tracking event
     * @return success status
     */
    @PostMapping("/packages/{packageId}/offline-events")
    @Operation(
        summary = "Store offline tracking event",
        description = "Stores a tracking event locally when the device is offline",
        responses = {
            @ApiResponse(responseCode = "200", description = "Tracking event stored successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid event data")
        }
    )
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<Map<String, Boolean>> storeOfflineTrackingEvent(
            @PathVariable String packageId,
            @Valid @RequestBody TrackingEventDTO event) {
        
        log.info("REST request to store offline tracking event for package: {}", packageId);
        
        // Ensure package ID is set
        event.setPackageId(packageId);
        
        boolean success = trackingService.storeOfflineTrackingEvent(event);
        return ResponseEntity.ok(Map.of("success", success));
    }
    
    /**
     * Store delivery confirmation locally when offline.
     *
     * @param packageId the package ID
     * @param confirmation the delivery confirmation
     * @return success status
     */
    @PostMapping("/packages/{packageId}/offline-confirmations")
    @Operation(
        summary = "Store offline delivery confirmation",
        description = "Stores a delivery confirmation locally when the device is offline",
        responses = {
            @ApiResponse(responseCode = "200", description = "Delivery confirmation stored successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid confirmation data")
        }
    )
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<Map<String, Boolean>> storeOfflineDeliveryConfirmation(
            @PathVariable String packageId,
            @Valid @RequestBody DeliveryConfirmationDTO confirmation) {
        
        log.info("REST request to store offline delivery confirmation for package: {}", packageId);
        
        // Ensure package ID is set
        confirmation.setPackageId(packageId);
        
        boolean success = trackingService.storeOfflineDeliveryConfirmation(confirmation);
        return ResponseEntity.ok(Map.of("success", success));
    }
    
    /**
     * Synchronize offline tracking data with the server.
     *
     * @param courierId the courier ID
     * @return map of sync results
     */
    @PostMapping("/offline-sync/{courierId}")
    @Operation(
        summary = "Synchronize offline tracking data",
        description = "Synchronizes locally stored tracking data with the server after offline operations",
        responses = {
            @ApiResponse(responseCode = "200", description = "Data synchronized successfully")
        }
    )
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<Map<String, String>> synchronizeOfflineTrackingData(@PathVariable String courierId) {
        log.info("REST request to synchronize offline tracking data for courier: {}", courierId);
        return ResponseEntity.ok(trackingService.synchronizeOfflineTrackingData(courierId));
    }
    
    /**
     * Get packages assigned to a courier.
     *
     * @param courierId the courier ID
     * @return list of tracking info
     */
    @GetMapping("/packages/by-courier/{courierId}")
    @Operation(
        summary = "Get packages by courier",
        description = "Retrieves all packages assigned to a specific courier",
        responses = {
            @ApiResponse(responseCode = "200", description = "Packages retrieved successfully")
        }
    )
    @PreAuthorize("hasRole('DRIVER') or hasRole('ADMIN')")
    public ResponseEntity<List<TrackingInfoDTO>> getPackagesByCourier(@PathVariable String courierId) {
        log.info("REST request to get packages for courier: {}", courierId);
        return ResponseEntity.ok(trackingService.getPackagesByCourier(courierId));
    }
}
