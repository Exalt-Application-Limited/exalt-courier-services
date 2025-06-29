package com.microecosystem.courier.driver.app.controller.api;

import com.microecosystem.courier.driver.app.service.sync.OfflineSyncService;
import com.microecosystem.courier.driver.app.service.sync.OfflineSyncService.SyncOperation;
import com.microecosystem.courier.driver.app.service.sync.OfflineSyncService.SyncResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST API controller for handling offline synchronization operations.
 * This controller provides endpoints for syncing data between mobile devices and the server.
 */
@RestController
@RequestMapping("/api/v1/sync")
@Tag(name = "Offline Synchronization", description = "APIs for offline data synchronization between mobile devices and server")
public class OfflineSyncController {

    private static final Logger logger = LoggerFactory.getLogger(OfflineSyncController.class);
    
    private final OfflineSyncService offlineSyncService;
    
    public OfflineSyncController(OfflineSyncService offlineSyncService) {
        this.offlineSyncService = offlineSyncService;
    }
    
    /**
     * Sync a batch of operations from a mobile device.
     * 
     * @param deviceId The ID of the mobile device
     * @param operations List of operations to sync
     * @return Result of the sync operation
     */
    @PostMapping("/batch/{deviceId}")
    @Operation(summary = "Sync operations batch", description = "Synchronizes a batch of operations from a mobile device")
    @PreAuthorize("hasRole('DRIVER') and @securityService.isAuthorizedDevice(#deviceId)")
    public ResponseEntity<SyncResult> syncBatch(
            @Parameter(description = "Device ID", required = true)
            @PathVariable String deviceId,
            @Parameter(description = "Operations to sync", required = true)
            @RequestBody List<SyncOperation> operations) {
        
        logger.info("Received sync request from device {}: {} operations", deviceId, operations.size());
        
        SyncResult result = offlineSyncService.processSyncBatch(deviceId, operations);
        
        if (result.isSuccess()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(207).body(result); // 207 Multi-Status
        }
    }
    
    /**
     * Get the sync status for a device.
     * 
     * @param deviceId The ID of the mobile device
     * @return Sync status information
     */
    @GetMapping("/status/{deviceId}")
    @Operation(summary = "Get sync status", description = "Retrieves the synchronization status for a device")
    @PreAuthorize("hasRole('DRIVER') and @securityService.isAuthorizedDevice(#deviceId)")
    public ResponseEntity<Object> getSyncStatus(
            @Parameter(description = "Device ID", required = true)
            @PathVariable String deviceId) {
            
        logger.info("Getting sync status for device {}", deviceId);
        
        // In a real implementation, this would fetch actual status
        // For now, returning a simple success response
        return ResponseEntity.ok(
            Map.of(
                "deviceId", deviceId,
                "lastSyncTime", System.currentTimeMillis(),
                "pendingOperations", 0,
                "syncStatus", "SYNCHRONIZED"
            )
        );
    }
    
    /**
     * Clear all pending sync operations for a device.
     * 
     * @param deviceId The ID of the mobile device
     * @return Result of the clear operation
     */
    @DeleteMapping("/pending/{deviceId}")
    @Operation(summary = "Clear pending operations", description = "Clears all pending synchronization operations for a device")
    @PreAuthorize("hasRole('DRIVER') and @securityService.isAuthorizedDevice(#deviceId)")
    public ResponseEntity<Object> clearPendingOperations(
            @Parameter(description = "Device ID", required = true)
            @PathVariable String deviceId) {
            
        logger.info("Clearing pending operations for device {}", deviceId);
        
        boolean success = offlineSyncService.clearPendingOperations(deviceId);
        
        if (success) {
            return ResponseEntity.ok(
                Map.of(
                    "success", true,
                    "message", "All pending operations cleared",
                    "deviceId", deviceId
                )
            );
        } else {
            return ResponseEntity.badRequest().body(
                Map.of(
                    "success", false,
                    "message", "Failed to clear pending operations",
                    "deviceId", deviceId
                )
            );
        }
    }
    
    /**
     * Get synchronization statistics for a device.
     * 
     * @param deviceId The ID of the mobile device
     * @return Synchronization statistics
     */
    @GetMapping("/stats/{deviceId}")
    @Operation(summary = "Get sync statistics", description = "Retrieves synchronization statistics for a device")
    @PreAuthorize("hasRole('DRIVER') and @securityService.isAuthorizedDevice(#deviceId)")
    public ResponseEntity<Map<String, Object>> getSyncStats(
            @Parameter(description = "Device ID", required = true)
            @PathVariable String deviceId) {
            
        logger.info("Getting sync stats for device {}", deviceId);
        
        Map<String, Object> stats = offlineSyncService.getSyncStats(deviceId);
        
        return ResponseEntity.ok(stats);
    }
}
