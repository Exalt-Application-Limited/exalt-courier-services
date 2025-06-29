package com.exalt.courier.drivermobileapp.controller.sync;

import com.socialecommerceecosystem.drivermobileapp.dto.assignment.OfflineSyncRequestDTO;
import com.socialecommerceecosystem.drivermobileapp.dto.assignment.OfflineSyncResponseDTO;
import com.socialecommerceecosystem.drivermobileapp.dto.tracking.DeliveryConfirmationDTO;
import com.socialecommerceecosystem.drivermobileapp.dto.tracking.TrackingEventDTO;
import com.socialecommerceecosystem.drivermobileapp.service.sync.SynchronizationService;
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
 * REST controller for data synchronization operations in the driver mobile app.
 * Handles the synchronization of offline data with the server when connectivity is restored.
 */
@RestController
@RequestMapping("/api/v1/sync")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Synchronization", description = "Data synchronization operations for mobile app")
public class SynchronizationController {
    
    private final SynchronizationService synchronizationService;
    
    /**
     * Synchronize all offline data with the server.
     *
     * @param courierId the courier ID
     * @param deviceId the device ID
     * @return synchronization response
     */
    @PostMapping("/all/{courierId}")
    @Operation(
        summary = "Synchronize all data",
        description = "Performs a comprehensive synchronization of all offline data with the server",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "Data synchronized successfully",
                content = @Content(schema = @Schema(implementation = OfflineSyncResponseDTO.class))
            )
        }
    )
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<OfflineSyncResponseDTO> synchronizeAll(
            @PathVariable String courierId,
            @RequestHeader(value = "X-Device-Id", required = false, defaultValue = "unknown") String deviceId) {
        
        log.info("REST request to synchronize all data for courier: {}, device: {}", courierId, deviceId);
        return ResponseEntity.ok(synchronizationService.synchronizeAll(courierId, deviceId));
    }
    
    /**
     * Synchronize assignments with the server.
     *
     * @param syncRequest the synchronization request
     * @return synchronization response
     */
    @PostMapping("/assignments")
    @Operation(
        summary = "Synchronize assignments",
        description = "Synchronizes locally stored assignments with the server after offline operations",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "Assignments synchronized successfully",
                content = @Content(schema = @Schema(implementation = OfflineSyncResponseDTO.class))
            )
        }
    )
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<OfflineSyncResponseDTO> synchronizeAssignments(
            @Valid @RequestBody OfflineSyncRequestDTO syncRequest) {
        
        log.info("REST request to synchronize assignments for courier: {}", syncRequest.getCourierId());
        return ResponseEntity.ok(synchronizationService.synchronizeAssignments(syncRequest));
    }
    
    /**
     * Synchronize tracking events with the server.
     *
     * @param courierId the courier ID
     * @param events the list of tracking events to synchronize
     * @return synchronization results
     */
    @PostMapping("/tracking-events/{courierId}")
    @Operation(
        summary = "Synchronize tracking events",
        description = "Synchronizes locally stored tracking events with the server after offline operations",
        responses = {
            @ApiResponse(responseCode = "200", description = "Tracking events synchronized successfully")
        }
    )
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<Map<String, String>> synchronizeTrackingEvents(
            @PathVariable String courierId,
            @Valid @RequestBody List<TrackingEventDTO> events) {
        
        log.info("REST request to synchronize {} tracking events for courier: {}", events.size(), courierId);
        return ResponseEntity.ok(synchronizationService.synchronizeTrackingEvents(courierId, events));
    }
    
    /**
     * Synchronize delivery confirmations with the server.
     *
     * @param courierId the courier ID
     * @param confirmations the list of delivery confirmations to synchronize
     * @return synchronization results
     */
    @PostMapping("/delivery-confirmations/{courierId}")
    @Operation(
        summary = "Synchronize delivery confirmations",
        description = "Synchronizes locally stored delivery confirmations with the server after offline operations",
        responses = {
            @ApiResponse(responseCode = "200", description = "Delivery confirmations synchronized successfully")
        }
    )
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<Map<String, String>> synchronizeDeliveryConfirmations(
            @PathVariable String courierId,
            @Valid @RequestBody List<DeliveryConfirmationDTO> confirmations) {
        
        log.info("REST request to synchronize {} delivery confirmations for courier: {}", 
                confirmations.size(), courierId);
        
        return ResponseEntity.ok(
                synchronizationService.synchronizeDeliveryConfirmations(courierId, confirmations));
    }
    
    /**
     * Fetch updates from the server.
     *
     * @param courierId the courier ID
     * @return list of new or updated assignments
     */
    @GetMapping("/updates/{courierId}")
    @Operation(
        summary = "Fetch server updates",
        description = "Retrieves new assignments and updates from the server since last synchronization",
        responses = {
            @ApiResponse(responseCode = "200", description = "Updates retrieved successfully")
        }
    )
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<Map<String, Object>> fetchServerUpdates(
            @PathVariable String courierId) {
        
        log.info("REST request to fetch updates for courier: {}", courierId);
        
        Map<String, String> lastSyncState = synchronizationService.getLastSyncState(courierId);
        List<?> updates = synchronizationService.fetchServerUpdates(courierId, lastSyncState.get("timestamp"));
        
        return ResponseEntity.ok(Map.of(
                "assignments", updates,
                "lastSyncTimestamp", lastSyncState.get("timestamp"),
                "syncToken", lastSyncState.get("token")
        ));
    }
    
    /**
     * Get synchronization status.
     *
     * @param courierId the courier ID
     * @return synchronization status
     */
    @GetMapping("/status/{courierId}")
    @Operation(
        summary = "Get synchronization status",
        description = "Retrieves the current synchronization status for a courier",
        responses = {
            @ApiResponse(responseCode = "200", description = "Status retrieved successfully")
        }
    )
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<Map<String, String>> getSyncStatus(
            @PathVariable String courierId) {
        
        log.info("REST request to get sync status for courier: {}", courierId);
        return ResponseEntity.ok(synchronizationService.getLastSyncState(courierId));
    }
}
