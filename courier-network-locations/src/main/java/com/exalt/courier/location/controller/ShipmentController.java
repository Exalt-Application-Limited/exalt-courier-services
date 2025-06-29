package com.exalt.courier.location.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.socialecommerceecosystem.location.model.ShipmentStatus;
import com.socialecommerceecosystem.location.model.WalkInShipment;
import com.socialecommerceecosystem.location.service.NotificationService;
import com.socialecommerceecosystem.location.service.ShipmentProcessingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for shipment operations in the courier network.
 * Provides endpoints for managing walk-in shipments and their status updates.
 */
@RestController
@RequestMapping("/api/shipments")
@Tag(name = "Shipment Management", description = "API for managing walk-in shipments in the courier network")
@Slf4j
public class ShipmentController {

    private final ShipmentProcessingService shipmentService;
    private final NotificationService notificationService;

    @Autowired
    public ShipmentController(
            ShipmentProcessingService shipmentService,
            NotificationService notificationService) {
        this.shipmentService = shipmentService;
        this.notificationService = notificationService;
    }

    @GetMapping
    @Operation(summary = "Get all shipments", description = "Retrieves all walk-in shipments")
    @ApiResponse(responseCode = "200", description = "Shipments retrieved successfully", 
            content = @Content(schema = @Schema(implementation = WalkInShipment.class)))
    public ResponseEntity<List<WalkInShipment>> getAllShipments() {
        log.debug("REST request to get all shipments");
        List<WalkInShipment> shipments = shipmentService.getAllShipments();
        return ResponseEntity.ok(shipments);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get shipment by ID", description = "Retrieves a specific shipment by ID")
    @ApiResponse(responseCode = "200", description = "Shipment found", 
            content = @Content(schema = @Schema(implementation = WalkInShipment.class)))
    @ApiResponse(responseCode = "404", description = "Shipment not found")
    public ResponseEntity<WalkInShipment> getShipmentById(
            @Parameter(description = "ID of the shipment", required = true) @PathVariable Long id) {
        log.debug("REST request to get shipment with ID: {}", id);
        return shipmentService.getShipmentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/tracking/{trackingNumber}")
    @Operation(summary = "Get shipment by tracking number", 
            description = "Retrieves a shipment by its tracking number")
    @ApiResponse(responseCode = "200", description = "Shipment found", 
            content = @Content(schema = @Schema(implementation = WalkInShipment.class)))
    @ApiResponse(responseCode = "404", description = "Shipment not found")
    public ResponseEntity<WalkInShipment> getShipmentByTrackingNumber(
            @Parameter(description = "Tracking number", required = true) @PathVariable String trackingNumber) {
        log.debug("REST request to get shipment with tracking number: {}", trackingNumber);
        return shipmentService.getShipmentByTrackingNumber(trackingNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a new shipment", description = "Creates a new walk-in shipment")
    @ApiResponse(responseCode = "201", description = "Shipment created successfully", 
            content = @Content(schema = @Schema(implementation = WalkInShipment.class)))
    public ResponseEntity<WalkInShipment> createShipment(
            @Parameter(description = "Shipment details", required = true) 
            @Valid @RequestBody WalkInShipment shipment) {
        log.debug("REST request to create a new shipment");
        WalkInShipment createdShipment = shipmentService.createShipment(shipment);
        
        // Send notification for shipment creation
        notificationService.sendShipmentCreationConfirmation(createdShipment);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(createdShipment);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a shipment", description = "Updates an existing shipment")
    @ApiResponse(responseCode = "200", description = "Shipment updated successfully", 
            content = @Content(schema = @Schema(implementation = WalkInShipment.class)))
    @ApiResponse(responseCode = "404", description = "Shipment not found")
    public ResponseEntity<WalkInShipment> updateShipment(
            @Parameter(description = "ID of the shipment", required = true) @PathVariable Long id,
            @Parameter(description = "Updated shipment details", required = true) 
            @Valid @RequestBody WalkInShipment shipment) {
        log.debug("REST request to update shipment with ID: {}", id);
        try {
            WalkInShipment updatedShipment = shipmentService.updateShipment(id, shipment);
            return ResponseEntity.ok(updatedShipment);
        } catch (IllegalArgumentException e) {
            log.error("Shipment not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a shipment", description = "Deletes a shipment")
    @ApiResponse(responseCode = "204", description = "Shipment deleted successfully")
    @ApiResponse(responseCode = "404", description = "Shipment not found")
    public ResponseEntity<Void> deleteShipment(
            @Parameter(description = "ID of the shipment", required = true) @PathVariable Long id) {
        log.debug("REST request to delete shipment with ID: {}", id);
        try {
            shipmentService.deleteShipment(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("Shipment not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update shipment status", description = "Updates the status of a shipment")
    @ApiResponse(responseCode = "200", description = "Status updated successfully", 
            content = @Content(schema = @Schema(implementation = WalkInShipment.class)))
    @ApiResponse(responseCode = "404", description = "Shipment not found")
    public ResponseEntity<WalkInShipment> updateShipmentStatus(
            @Parameter(description = "ID of the shipment", required = true) @PathVariable Long id,
            @Parameter(description = "New status", required = true) @RequestParam ShipmentStatus status) {
        log.debug("REST request to update status of shipment with ID: {} to: {}", id, status);
        try {
            // Get the current status before updating
            ShipmentStatus oldStatus = shipmentService.getShipmentById(id)
                    .map(WalkInShipment::getStatus)
                    .orElseThrow(() -> new IllegalArgumentException("Shipment not found with ID: " + id));
            
            WalkInShipment updatedShipment = shipmentService.updateShipmentStatus(id, status);
            
            // Send notification for status update
            notificationService.sendShipmentStatusUpdateNotification(updatedShipment, oldStatus, status);
            
            return ResponseEntity.ok(updatedShipment);
        } catch (IllegalArgumentException e) {
            log.error("Shipment not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/location")
    @Operation(summary = "Update shipment location", 
            description = "Updates the current location of a shipment")
    @ApiResponse(responseCode = "200", description = "Location updated successfully", 
            content = @Content(schema = @Schema(implementation = WalkInShipment.class)))
    @ApiResponse(responseCode = "404", description = "Shipment not found")
    public ResponseEntity<WalkInShipment> updateShipmentLocation(
            @Parameter(description = "ID of the shipment", required = true) @PathVariable Long id,
            @Parameter(description = "ID of the new location", required = true) @RequestParam Long locationId) {
        log.debug("REST request to update location of shipment with ID: {} to location ID: {}", 
                id, locationId);
        try {
            WalkInShipment updatedShipment = shipmentService.updateShipmentLocation(id, locationId);
            return ResponseEntity.ok(updatedShipment);
        } catch (IllegalArgumentException e) {
            log.error("Error updating shipment location: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get shipments by customer", 
            description = "Retrieves all shipments for a specific customer")
    public ResponseEntity<List<WalkInShipment>> getShipmentsByCustomer(
            @Parameter(description = "ID of the customer", required = true) @PathVariable Long customerId) {
        log.debug("REST request to get shipments for customer with ID: {}", customerId);
        List<WalkInShipment> shipments = shipmentService.getShipmentsByCustomer(customerId);
        return ResponseEntity.ok(shipments);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get shipments by status", 
            description = "Retrieves all shipments with a specific status")
    public ResponseEntity<List<WalkInShipment>> getShipmentsByStatus(
            @Parameter(description = "Shipment status", required = true) @PathVariable ShipmentStatus status) {
        log.debug("REST request to get shipments with status: {}", status);
        List<WalkInShipment> shipments = shipmentService.getShipmentsByStatus(status);
        return ResponseEntity.ok(shipments);
    }

    @GetMapping("/location/{locationId}")
    @Operation(summary = "Get shipments by location", 
            description = "Retrieves all shipments at a specific location")
    public ResponseEntity<List<WalkInShipment>> getShipmentsByLocation(
            @Parameter(description = "ID of the location", required = true) @PathVariable Long locationId) {
        log.debug("REST request to get shipments at location with ID: {}", locationId);
        List<WalkInShipment> shipments = shipmentService.getShipmentsByLocation(locationId);
        return ResponseEntity.ok(shipments);
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get shipments by date range", 
            description = "Retrieves all shipments created within a date range")
    public ResponseEntity<List<WalkInShipment>> getShipmentsByDateRange(
            @Parameter(description = "Start date (ISO format)", required = true) 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date (ISO format)", required = true) 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.debug("REST request to get shipments between dates: {} and {}", startDate, endDate);
        List<WalkInShipment> shipments = shipmentService.getShipmentsByDateRange(startDate, endDate);
        return ResponseEntity.ok(shipments);
    }

    @PostMapping("/{id}/pickup-reminder")
    @Operation(summary = "Send pickup reminder", 
            description = "Sends a pickup reminder notification for a shipment")
    @ApiResponse(responseCode = "200", description = "Reminder sent successfully")
    @ApiResponse(responseCode = "404", description = "Shipment not found")
    public ResponseEntity<Map<String, Object>> sendPickupReminder(
            @Parameter(description = "ID of the shipment", required = true) @PathVariable Long id) {
        log.debug("REST request to send pickup reminder for shipment with ID: {}", id);
        try {
            WalkInShipment shipment = shipmentService.getShipmentById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Shipment not found with ID: " + id));
            
            boolean sent = notificationService.sendPickupReminder(shipment);
            
            Map<String, Object> response = Map.of(
                    "success", sent,
                    "message", sent ? "Pickup reminder sent successfully" : "Failed to send pickup reminder"
            );
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Shipment not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/delivery-attempt")
    @Operation(summary = "Record delivery attempt", 
            description = "Records a delivery attempt for a shipment and sends notification")
    @ApiResponse(responseCode = "200", description = "Delivery attempt recorded successfully")
    @ApiResponse(responseCode = "404", description = "Shipment not found")
    public ResponseEntity<Map<String, Object>> recordDeliveryAttempt(
            @Parameter(description = "ID of the shipment", required = true) @PathVariable Long id,
            @Parameter(description = "Delivery attempt details", required = true) 
            @RequestBody Map<String, Object> attemptDetails) {
        log.debug("REST request to record delivery attempt for shipment with ID: {}", id);
        try {
            WalkInShipment shipment = shipmentService.getShipmentById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Shipment not found with ID: " + id));
            
            // Update shipment with delivery attempt info
            shipment.setLastDeliveryAttempt(LocalDateTime.now());
            shipment.setDeliveryAttemptCount(shipment.getDeliveryAttemptCount() + 1);
            shipment.setStatus(ShipmentStatus.DELIVERY_ATTEMPTED);
            
            WalkInShipment updatedShipment = shipmentService.updateShipment(id, shipment);
            
            // Send notification about delivery attempt
            boolean sent = notificationService.sendDeliveryAttemptNotification(updatedShipment, attemptDetails);
            
            Map<String, Object> response = Map.of(
                    "success", true,
                    "shipment", updatedShipment,
                    "notificationSent", sent
            );
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Shipment not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/counts/by-status")
    @Operation(summary = "Get shipment counts by status", 
            description = "Retrieves the count of shipments by status")
    public ResponseEntity<Map<ShipmentStatus, Long>> getShipmentCountsByStatus() {
        log.debug("REST request to get shipment counts by status");
        Map<ShipmentStatus, Long> counts = shipmentService.getShipmentCountsByStatus();
        return ResponseEntity.ok(counts);
    }

    @GetMapping("/counts/by-location")
    @Operation(summary = "Get shipment counts by location", 
            description = "Retrieves the count of shipments by location")
    public ResponseEntity<Map<Long, Long>> getShipmentCountsByLocation() {
        log.debug("REST request to get shipment counts by location");
        Map<Long, Long> counts = shipmentService.getShipmentCountsByLocation();
        return ResponseEntity.ok(counts);
    }

    @GetMapping("/delayed")
    @Operation(summary = "Get delayed shipments", 
            description = "Retrieves all shipments that are delayed")
    public ResponseEntity<List<WalkInShipment>> getDelayedShipments() {
        log.debug("REST request to get delayed shipments");
        List<WalkInShipment> shipments = shipmentService.findDelayedShipments();
        return ResponseEntity.ok(shipments);
    }

    @GetMapping("/high-value")
    @Operation(summary = "Get high-value shipments", 
            description = "Retrieves all high-value shipments")
    public ResponseEntity<List<WalkInShipment>> getHighValueShipments(
            @Parameter(description = "Minimum value threshold") 
            @RequestParam(required = false, defaultValue = "1000") double minValue) {
        log.debug("REST request to get high-value shipments with minimum value: {}", minValue);
        List<WalkInShipment> shipments = shipmentService.findHighValueShipments(minValue);
        return ResponseEntity.ok(shipments);
    }

    @GetMapping("/ready-for-pickup")
    @Operation(summary = "Get shipments ready for pickup", 
            description = "Retrieves all shipments that are ready for pickup")
    public ResponseEntity<List<WalkInShipment>> getShipmentsReadyForPickup() {
        log.debug("REST request to get shipments ready for pickup");
        List<WalkInShipment> shipments = shipmentService.findShipmentsReadyForPickup();
        return ResponseEntity.ok(shipments);
    }
}
