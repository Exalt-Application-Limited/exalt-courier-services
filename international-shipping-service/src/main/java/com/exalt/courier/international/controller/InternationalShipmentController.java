package com.exalt.courierservices.international-shipping.$1;

import com.exalt.courier.international.model.CustomsDeclaration;
import com.exalt.courier.international.model.InternationalShipment;
import com.exalt.courier.international.service.InternationalShipmentService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * REST controller for managing international shipments.
 */
@RestController
@RequestMapping("/api/international/shipments")
@Slf4j
public class InternationalShipmentController {

    private final InternationalShipmentService shipmentService;

    @Autowired
    public InternationalShipmentController(InternationalShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    /**
     * Create a new international shipment
     * 
     * @param shipment The shipment to create
     * @return The created shipment
     */
    @PostMapping
    public ResponseEntity<InternationalShipment> createShipment(@Valid @RequestBody InternationalShipment shipment) {
        log.info("REST request to create international shipment: {}", shipment);
        InternationalShipment result = shipmentService.createShipment(shipment);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * Update an existing international shipment
     * 
     * @param referenceId The reference ID of the shipment to update
     * @param shipment The updated shipment
     * @return The updated shipment
     */
    @PutMapping("/{referenceId}")
    public ResponseEntity<InternationalShipment> updateShipment(
            @PathVariable String referenceId,
            @Valid @RequestBody InternationalShipment shipment) {
        log.info("REST request to update international shipment: {}", referenceId);
        InternationalShipment result = shipmentService.updateShipment(referenceId, shipment);
        return ResponseEntity.ok(result);
    }

    /**
     * Get a shipment by reference ID
     * 
     * @param referenceId The reference ID of the shipment
     * @return The shipment
     */
    @GetMapping("/{referenceId}")
    public ResponseEntity<InternationalShipment> getShipment(@PathVariable String referenceId) {
        log.info("REST request to get international shipment: {}", referenceId);
        return shipmentService.getShipmentByReferenceId(referenceId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shipment not found"));
    }

    /**
     * Get a shipment by tracking number
     * 
     * @param trackingNumber The tracking number
     * @return The shipment
     */
    @GetMapping("/tracking/{trackingNumber}")
    public ResponseEntity<InternationalShipment> getShipmentByTrackingNumber(@PathVariable String trackingNumber) {
        log.info("REST request to get international shipment by tracking number: {}", trackingNumber);
        return shipmentService.getShipmentByTrackingNumber(trackingNumber)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shipment not found"));
    }

    /**
     * Get shipments by status
     * 
     * @param status The status to filter by
     * @return List of shipments with the specified status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<InternationalShipment>> getShipmentsByStatus(
            @PathVariable InternationalShipment.ShipmentStatus status) {
        log.info("REST request to get international shipments by status: {}", status);
        List<InternationalShipment> shipments = shipmentService.getShipmentsByStatus(status);
        return ResponseEntity.ok(shipments);
    }

    /**
     * Get shipments by destination country
     * 
     * @param countryCode The destination country code
     * @return List of shipments to the specified country
     */
    @GetMapping("/destination/{countryCode}")
    public ResponseEntity<List<InternationalShipment>> getShipmentsByDestinationCountry(
            @PathVariable String countryCode) {
        log.info("REST request to get international shipments by destination country: {}", countryCode);
        List<InternationalShipment> shipments = shipmentService.getShipmentsByDestinationCountry(countryCode);
        return ResponseEntity.ok(shipments);
    }

    /**
     * Get shipments by date range
     * 
     * @param startDate Start of the date range
     * @param endDate End of the date range
     * @return List of shipments created within the date range
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<InternationalShipment>> getShipmentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("REST request to get international shipments between {} and {}", startDate, endDate);
        List<InternationalShipment> shipments = shipmentService.getShipmentsByDateRange(startDate, endDate);
        return ResponseEntity.ok(shipments);
    }

    /**
     * Submit a shipment to the carrier
     * 
     * @param referenceId The reference ID of the shipment to submit
     * @return The updated shipment with carrier tracking information
     */
    @PostMapping("/{referenceId}/submit")
    public ResponseEntity<InternationalShipment> submitShipmentToCarrier(@PathVariable String referenceId) {
        log.info("REST request to submit international shipment to carrier: {}", referenceId);
        InternationalShipment result = shipmentService.submitShipmentToCarrier(referenceId);
        return ResponseEntity.ok(result);
    }

    /**
     * Cancel a shipment
     * 
     * @param referenceId The reference ID of the shipment to cancel
     * @return The updated shipment with cancelled status
     */
    @PostMapping("/{referenceId}/cancel")
    public ResponseEntity<InternationalShipment> cancelShipment(@PathVariable String referenceId) {
        log.info("REST request to cancel international shipment: {}", referenceId);
        InternationalShipment result = shipmentService.cancelShipment(referenceId);
        return ResponseEntity.ok(result);
    }

    /**
     * Update the customs declaration for a shipment
     * 
     * @param referenceId The reference ID of the shipment
     * @param customsDeclaration The updated customs declaration
     * @return The updated shipment with the new customs declaration
     */
    @PutMapping("/{referenceId}/customs-declaration")
    public ResponseEntity<InternationalShipment> updateCustomsDeclaration(
            @PathVariable String referenceId,
            @Valid @RequestBody CustomsDeclaration customsDeclaration) {
        log.info("REST request to update customs declaration for shipment: {}", referenceId);
        InternationalShipment result = shipmentService.updateCustomsDeclaration(referenceId, customsDeclaration);
        return ResponseEntity.ok(result);
    }

    /**
     * Submit a shipment for compliance approval
     * 
     * @param referenceId The reference ID of the shipment
     * @return The updated shipment with submitted status
     */
    @PostMapping("/{referenceId}/submit-for-compliance")
    public ResponseEntity<InternationalShipment> submitForComplianceApproval(@PathVariable String referenceId) {
        log.info("REST request to submit international shipment for compliance approval: {}", referenceId);
        InternationalShipment result = shipmentService.submitForComplianceApproval(referenceId);
        return ResponseEntity.ok(result);
    }

    /**
     * Approve a shipment from a compliance perspective
     * 
     * @param referenceId The reference ID of the shipment
     * @param approvalInfo Map containing approval information
     * @return The updated shipment with approved status
     */
    @PostMapping("/{referenceId}/approve-compliance")
    public ResponseEntity<InternationalShipment> approveShipmentCompliance(
            @PathVariable String referenceId,
            @RequestBody Map<String, String> approvalInfo) {
        log.info("REST request to approve compliance for international shipment: {}", referenceId);
        String approvedBy = approvalInfo.getOrDefault("approvedBy", "System");
        InternationalShipment result = shipmentService.approveShipmentCompliance(referenceId, approvedBy);
        return ResponseEntity.ok(result);
    }

    /**
     * Reject a shipment from a compliance perspective
     * 
     * @param referenceId The reference ID of the shipment
     * @param rejectionInfo Map containing rejection information
     * @return The updated shipment with rejected status
     */
    @PostMapping("/{referenceId}/reject-compliance")
    public ResponseEntity<InternationalShipment> rejectShipmentCompliance(
            @PathVariable String referenceId,
            @RequestBody Map<String, String> rejectionInfo) {
        log.info("REST request to reject compliance for international shipment: {}", referenceId);
        String rejectionReason = rejectionInfo.getOrDefault("rejectionReason", "");
        InternationalShipment result = shipmentService.rejectShipmentCompliance(referenceId, rejectionReason);
        return ResponseEntity.ok(result);
    }

    /**
     * Generate shipping documents for a shipment
     * 
     * @param referenceId The reference ID of the shipment
     * @return The updated shipment with document URLs
     */
    @PostMapping("/{referenceId}/generate-documents")
    public ResponseEntity<InternationalShipment> generateShippingDocuments(@PathVariable String referenceId) {
        log.info("REST request to generate documents for international shipment: {}", referenceId);
        InternationalShipment result = shipmentService.generateShippingDocuments(referenceId);
        return ResponseEntity.ok(result);
    }

    /**
     * Get shipments that need compliance attention
     * 
     * @return List of shipments needing attention
     */
    @GetMapping("/needs-attention")
    public ResponseEntity<List<InternationalShipment>> getShipmentsNeedingAttention() {
        log.info("REST request to get international shipments needing attention");
        List<InternationalShipment> shipments = shipmentService.getShipmentsNeedingAttention();
        return ResponseEntity.ok(shipments);
    }

    /**
     * Get shipments that are held by customs
     * 
     * @return List of shipments held by customs
     */
    @GetMapping("/held-by-customs")
    public ResponseEntity<List<InternationalShipment>> getShipmentsHeldByCustoms() {
        log.info("REST request to get international shipments held by customs");
        List<InternationalShipment> shipments = shipmentService.getShipmentsHeldByCustoms();
        return ResponseEntity.ok(shipments);
    }

    /**
     * Update the tracking information for a shipment
     * 
     * @param referenceId The reference ID of the shipment
     * @return The updated shipment with the latest tracking information
     */
    @PostMapping("/{referenceId}/update-tracking")
    public ResponseEntity<InternationalShipment> updateTrackingInformation(@PathVariable String referenceId) {
        log.info("REST request to update tracking information for international shipment: {}", referenceId);
        InternationalShipment result = shipmentService.updateTrackingInformation(referenceId);
        return ResponseEntity.ok(result);
    }

    /**
     * Check if a shipment is eligible for international shipping
     * 
     * @param originCountry The origin country code
     * @param destinationCountry The destination country code
     * @param categories The list of product categories
     * @return true if eligible, false otherwise
     */
    @GetMapping("/check-eligibility")
    public ResponseEntity<Boolean> checkEligibility(
            @RequestParam String originCountry,
            @RequestParam String destinationCountry,
            @RequestParam List<String> categories) {
        log.info("REST request to check eligibility for international shipment from {} to {}", originCountry, destinationCountry);
        boolean isEligible = shipmentService.isEligibleForInternationalShipping(originCountry, destinationCountry, categories);
        return ResponseEntity.ok(isEligible);
    }

    /**
     * Estimate duties and taxes for a shipment
     * 
     * @param destinationCountry The destination country code
     * @param hsCodeList The list of HS codes
     * @param declaredValueList The list of declared values
     * @param currencyCode The currency code
     * @return Map of HS codes to estimated duties and taxes
     */
    @GetMapping("/estimate-duties")
    public ResponseEntity<Map<String, Double>> estimateDutiesAndTaxes(
            @RequestParam String destinationCountry,
            @RequestParam List<String> hsCodeList,
            @RequestParam List<Double> declaredValueList,
            @RequestParam String currencyCode) {
        log.info("REST request to estimate duties and taxes for shipment to {}", destinationCountry);
        Map<String, Double> estimates = shipmentService.estimateDutiesAndTaxes(
                destinationCountry, hsCodeList, declaredValueList, currencyCode);
        return ResponseEntity.ok(estimates);
    }

    /**
     * Delete a shipment
     * 
     * @param referenceId The reference ID of the shipment to delete
     * @return Status message
     */
    @DeleteMapping("/{referenceId}")
    public ResponseEntity<Void> deleteShipment(@PathVariable String referenceId) {
        log.info("REST request to delete international shipment: {}", referenceId);
        boolean result = shipmentService.deleteShipment(referenceId);
        return result ? ResponseEntity.noContent().build() : 
                ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
