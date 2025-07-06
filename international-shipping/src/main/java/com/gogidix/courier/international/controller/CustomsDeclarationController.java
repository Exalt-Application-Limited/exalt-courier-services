package com.gogidix.courier.international.controller;

import com.gogidix.courier.international.model.CustomsDeclaration;
import com.gogidix.courier.international.model.CustomsItem;
import com.gogidix.courier.international.service.CustomsDeclarationService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * REST controller for managing customs declarations.
 */
@RestController
@RequestMapping("/api/international/customs")
@Slf4j
public class CustomsDeclarationController {

    private final CustomsDeclarationService customsService;

    @Autowired
    public CustomsDeclarationController(CustomsDeclarationService customsService) {
        this.customsService = customsService;
    }

    /**
     * Create a new customs declaration
     * 
     * @param declaration The customs declaration to create
     * @return The created customs declaration
     */
    @PostMapping
    public ResponseEntity<CustomsDeclaration> createCustomsDeclaration(@Valid @RequestBody CustomsDeclaration declaration) {
        log.info("REST request to create customs declaration: {}", declaration);
        CustomsDeclaration result = customsService.createCustomsDeclaration(declaration);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * Update an existing customs declaration
     * 
     * @param referenceId The reference ID of the customs declaration to update
     * @param declaration The updated customs declaration
     * @return The updated customs declaration
     */
    @PutMapping("/{referenceId}")
    public ResponseEntity<CustomsDeclaration> updateCustomsDeclaration(
            @PathVariable String referenceId,
            @Valid @RequestBody CustomsDeclaration declaration) {
        log.info("REST request to update customs declaration: {}", referenceId);
        CustomsDeclaration result = customsService.updateCustomsDeclaration(referenceId, declaration);
        return ResponseEntity.ok(result);
    }

    /**
     * Get a customs declaration by reference ID
     * 
     * @param referenceId The reference ID of the customs declaration
     * @return The customs declaration
     */
    @GetMapping("/{referenceId}")
    public ResponseEntity<CustomsDeclaration> getCustomsDeclaration(@PathVariable String referenceId) {
        log.info("REST request to get customs declaration: {}", referenceId);
        return customsService.getCustomsDeclarationByReferenceId(referenceId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customs declaration not found"));
    }

    /**
     * Get a customs declaration by shipment ID
     * 
     * @param shipmentId The shipment ID
     * @return The customs declaration
     */
    @GetMapping("/shipment/{shipmentId}")
    public ResponseEntity<CustomsDeclaration> getCustomsDeclarationByShipmentId(@PathVariable String shipmentId) {
        log.info("REST request to get customs declaration by shipment ID: {}", shipmentId);
        return customsService.getCustomsDeclarationByShipmentId(shipmentId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customs declaration not found"));
    }

    /**
     * Get customs declarations by status
     * 
     * @param status The status to filter by
     * @return List of customs declarations with the specified status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<CustomsDeclaration>> getCustomsDeclarationsByStatus(
            @PathVariable CustomsDeclaration.CustomsStatus status) {
        log.info("REST request to get customs declarations by status: {}", status);
        List<CustomsDeclaration> declarations = customsService.getCustomsDeclarationsByStatus(status);
        return ResponseEntity.ok(declarations);
    }

    /**
     * Add an item to a customs declaration
     * 
     * @param referenceId The reference ID of the customs declaration
     * @param item The item to add
     * @return The updated customs declaration with the new item
     */
    @PostMapping("/{referenceId}/items")
    public ResponseEntity<CustomsDeclaration> addItemToDeclaration(
            @PathVariable String referenceId,
            @Valid @RequestBody CustomsItem item) {
        log.info("REST request to add item to customs declaration: {}", referenceId);
        CustomsDeclaration result = customsService.addItemToDeclaration(referenceId, item);
        return ResponseEntity.ok(result);
    }

    /**
     * Remove an item from a customs declaration
     * 
     * @param referenceId The reference ID of the customs declaration
     * @param itemId The ID of the item to remove
     * @return The updated customs declaration without the removed item
     */
    @DeleteMapping("/{referenceId}/items/{itemId}")
    public ResponseEntity<CustomsDeclaration> removeItemFromDeclaration(
            @PathVariable String referenceId,
            @PathVariable Long itemId) {
        log.info("REST request to remove item {} from customs declaration: {}", itemId, referenceId);
        CustomsDeclaration result = customsService.removeItemFromDeclaration(referenceId, itemId);
        return ResponseEntity.ok(result);
    }

    /**
     * Update an item in a customs declaration
     * 
     * @param referenceId The reference ID of the customs declaration
     * @param item The updated item
     * @return The updated customs declaration with the modified item
     */
    @PutMapping("/{referenceId}/items")
    public ResponseEntity<CustomsDeclaration> updateItemInDeclaration(
            @PathVariable String referenceId,
            @Valid @RequestBody CustomsItem item) {
        log.info("REST request to update item in customs declaration: {}", referenceId);
        CustomsDeclaration result = customsService.updateItemInDeclaration(referenceId, item);
        return ResponseEntity.ok(result);
    }

    /**
     * Submit a customs declaration for approval
     * 
     * @param referenceId The reference ID of the customs declaration
     * @return The updated customs declaration with submitted status
     */
    @PostMapping("/{referenceId}/submit")
    public ResponseEntity<CustomsDeclaration> submitCustomsDeclaration(@PathVariable String referenceId) {
        log.info("REST request to submit customs declaration: {}", referenceId);
        CustomsDeclaration result = customsService.submitCustomsDeclaration(referenceId);
        return ResponseEntity.ok(result);
    }

    /**
     * Validate a customs declaration
     * 
     * @param declaration The customs declaration to validate
     * @return List of validation errors, empty if valid
     */
    @PostMapping("/validate")
    public ResponseEntity<List<String>> validateCustomsDeclaration(@RequestBody CustomsDeclaration declaration) {
        log.info("REST request to validate customs declaration");
        List<String> validationErrors = customsService.validateCustomsDeclaration(declaration);
        return ResponseEntity.ok(validationErrors);
    }

    /**
     * Generate customs documentation
     * 
     * @param referenceId The reference ID of the customs declaration
     * @param documentType The type of document to generate
     * @return URL to the generated document
     */
    @GetMapping("/{referenceId}/documents/{documentType}")
    public ResponseEntity<String> generateCustomsDocument(
            @PathVariable String referenceId,
            @PathVariable CustomsDeclaration.DeclarationType documentType) {
        log.info("REST request to generate {} document for customs declaration: {}", documentType, referenceId);
        String documentUrl = customsService.generateCustomsDocument(referenceId, documentType);
        return ResponseEntity.ok(documentUrl);
    }

    /**
     * Check if a customs declaration is ready for submission
     * 
     * @param referenceId The reference ID of the customs declaration
     * @return true if ready for submission, false otherwise
     */
    @GetMapping("/{referenceId}/check-readiness")
    public ResponseEntity<Boolean> isReadyForSubmission(@PathVariable String referenceId) {
        log.info("REST request to check readiness of customs declaration: {}", referenceId);
        boolean isReady = customsService.isReadyForSubmission(referenceId);
        return ResponseEntity.ok(isReady);
    }

    /**
     * Get pending customs declarations that need attention
     * 
     * @return List of pending customs declarations
     */
    @GetMapping("/pending")
    public ResponseEntity<List<CustomsDeclaration>> getPendingDeclarations() {
        log.info("REST request to get pending customs declarations");
        List<CustomsDeclaration> pendingDeclarations = customsService.getPendingDeclarations();
        return ResponseEntity.ok(pendingDeclarations);
    }

    /**
     * Set the status of a customs declaration
     * 
     * @param referenceId The reference ID of the customs declaration
     * @param status The new status
     * @return The updated customs declaration with the new status
     */
    @PutMapping("/{referenceId}/status/{status}")
    public ResponseEntity<CustomsDeclaration> setCustomsDeclarationStatus(
            @PathVariable String referenceId,
            @PathVariable CustomsDeclaration.CustomsStatus status) {
        log.info("REST request to set status of customs declaration {} to {}", referenceId, status);
        CustomsDeclaration result = customsService.setCustomsDeclarationStatus(referenceId, status);
        return ResponseEntity.ok(result);
    }

    /**
     * Calculate the total declared value of a customs declaration
     * 
     * @param referenceId The reference ID of the customs declaration
     * @return The total declared value
     */
    @GetMapping("/{referenceId}/total-value")
    public ResponseEntity<Double> calculateTotalDeclaredValue(@PathVariable String referenceId) {
        log.info("REST request to calculate total declared value for customs declaration: {}", referenceId);
        Double totalValue = customsService.calculateTotalDeclaredValue(referenceId);
        return ResponseEntity.ok(totalValue);
    }

    /**
     * Check if a customs declaration requires any export licenses
     * 
     * @param referenceId The reference ID of the customs declaration
     * @return true if export licenses are required, false otherwise
     */
    @GetMapping("/{referenceId}/requires-export-license")
    public ResponseEntity<Boolean> requiresExportLicense(@PathVariable String referenceId) {
        log.info("REST request to check if customs declaration requires export license: {}", referenceId);
        boolean requiresExportLicense = customsService.requiresExportLicense(referenceId);
        return ResponseEntity.ok(requiresExportLicense);
    }

    /**
     * Delete a customs declaration
     * 
     * @param referenceId The reference ID of the customs declaration to delete
     * @return Status message
     */
    @DeleteMapping("/{referenceId}")
    public ResponseEntity<Void> deleteCustomsDeclaration(@PathVariable String referenceId) {
        log.info("REST request to delete customs declaration: {}", referenceId);
        boolean result = customsService.deleteCustomsDeclaration(referenceId);
        return result ? ResponseEntity.noContent().build() : 
                ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
