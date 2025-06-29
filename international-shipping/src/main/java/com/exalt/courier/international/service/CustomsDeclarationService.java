package com.exalt.courier.international.service;

import com.exalt.courier.international.model.CustomsDeclaration;
import com.exalt.courier.international.model.CustomsItem;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing customs declarations.
 */
public interface CustomsDeclarationService {

    /**
     * Create a new customs declaration
     * @param customsDeclaration The customs declaration to create
     * @return The created customs declaration with ID assigned
     */
    CustomsDeclaration createCustomsDeclaration(CustomsDeclaration customsDeclaration);
    
    /**
     * Update an existing customs declaration
     * @param referenceId The reference ID of the customs declaration to update
     * @param customsDeclaration The updated customs declaration
     * @return The updated customs declaration
     */
    CustomsDeclaration updateCustomsDeclaration(String referenceId, CustomsDeclaration customsDeclaration);
    
    /**
     * Get a customs declaration by its reference ID
     * @param referenceId The reference ID
     * @return The customs declaration if found
     */
    Optional<CustomsDeclaration> getCustomsDeclarationByReferenceId(String referenceId);
    
    /**
     * Get a customs declaration by its shipment ID
     * @param shipmentId The shipment ID
     * @return The customs declaration if found
     */
    Optional<CustomsDeclaration> getCustomsDeclarationByShipmentId(String shipmentId);
    
    /**
     * Get all customs declarations with a specific status
     * @param status The status to filter by
     * @return List of matching customs declarations
     */
    List<CustomsDeclaration> getCustomsDeclarationsByStatus(CustomsDeclaration.CustomsStatus status);
    
    /**
     * Add an item to a customs declaration
     * @param referenceId The reference ID of the customs declaration
     * @param item The item to add
     * @return The updated customs declaration with the new item
     */
    CustomsDeclaration addItemToDeclaration(String referenceId, CustomsItem item);
    
    /**
     * Remove an item from a customs declaration
     * @param referenceId The reference ID of the customs declaration
     * @param itemId The ID of the item to remove
     * @return The updated customs declaration without the removed item
     */
    CustomsDeclaration removeItemFromDeclaration(String referenceId, Long itemId);
    
    /**
     * Update an item in a customs declaration
     * @param referenceId The reference ID of the customs declaration
     * @param item The updated item
     * @return The updated customs declaration with the modified item
     */
    CustomsDeclaration updateItemInDeclaration(String referenceId, CustomsItem item);
    
    /**
     * Submit a customs declaration for approval
     * @param referenceId The reference ID of the customs declaration
     * @return The updated customs declaration with submitted status
     */
    CustomsDeclaration submitCustomsDeclaration(String referenceId);
    
    /**
     * Validate a customs declaration
     * @param customsDeclaration The customs declaration to validate
     * @return List of validation errors, empty if valid
     */
    List<String> validateCustomsDeclaration(CustomsDeclaration customsDeclaration);
    
    /**
     * Generate customs documentation (e.g., CN22, CN23, commercial invoice)
     * @param referenceId The reference ID of the customs declaration
     * @param documentType The type of document to generate
     * @return URL to the generated document
     */
    String generateCustomsDocument(String referenceId, CustomsDeclaration.DeclarationType documentType);
    
    /**
     * Check if a customs declaration is complete and ready for submission
     * @param referenceId The reference ID of the customs declaration
     * @return true if ready for submission, false otherwise
     */
    boolean isReadyForSubmission(String referenceId);
    
    /**
     * Get pending customs declarations that need attention
     * @return List of pending customs declarations
     */
    List<CustomsDeclaration> getPendingDeclarations();
    
    /**
     * Delete a customs declaration
     * @param referenceId The reference ID of the customs declaration to delete
     * @return true if successfully deleted, false otherwise
     */
    boolean deleteCustomsDeclaration(String referenceId);
    
    /**
     * Set the status of a customs declaration
     * @param referenceId The reference ID of the customs declaration
     * @param status The new status
     * @return The updated customs declaration with the new status
     */
    CustomsDeclaration setCustomsDeclarationStatus(String referenceId, CustomsDeclaration.CustomsStatus status);
    
    /**
     * Calculate the total declared value of a customs declaration
     * @param referenceId The reference ID of the customs declaration
     * @return The total declared value
     */
    Double calculateTotalDeclaredValue(String referenceId);
    
    /**
     * Check if a customs declaration requires any export licenses
     * @param referenceId The reference ID of the customs declaration
     * @return true if export licenses are required, false otherwise
     */
    boolean requiresExportLicense(String referenceId);
}
