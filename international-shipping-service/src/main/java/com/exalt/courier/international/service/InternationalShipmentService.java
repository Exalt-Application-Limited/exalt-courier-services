package com.exalt.courierservices.international-shipping.$1;

import com.exalt.courier.international.model.CustomsDeclaration;
import com.exalt.courier.international.model.InternationalShipment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing international shipments.
 */
public interface InternationalShipmentService {

    /**
     * Create a new international shipment
     * @param shipment The shipment details
     * @return The created shipment with ID assigned
     */
    InternationalShipment createShipment(InternationalShipment shipment);
    
    /**
     * Update an existing international shipment
     * @param referenceId The reference ID of the shipment to update
     * @param shipment The updated shipment details
     * @return The updated shipment
     */
    InternationalShipment updateShipment(String referenceId, InternationalShipment shipment);
    
    /**
     * Get a shipment by its reference ID
     * @param referenceId The reference ID
     * @return The shipment if found
     */
    Optional<InternationalShipment> getShipmentByReferenceId(String referenceId);
    
    /**
     * Get a shipment by its tracking number
     * @param trackingNumber The tracking number
     * @return The shipment if found
     */
    Optional<InternationalShipment> getShipmentByTrackingNumber(String trackingNumber);
    
    /**
     * Get all shipments with a particular status
     * @param status The status to filter by
     * @return List of matching shipments
     */
    List<InternationalShipment> getShipmentsByStatus(InternationalShipment.ShipmentStatus status);
    
    /**
     * Get shipments created within a date range
     * @param startDate Start of the date range
     * @param endDate End of the date range
     * @return List of matching shipments
     */
    List<InternationalShipment> getShipmentsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Get shipments by destination country
     * @param countryCode The destination country code
     * @return List of matching shipments
     */
    List<InternationalShipment> getShipmentsByDestinationCountry(String countryCode);
    
    /**
     * Submit a shipment to the carrier through the Third-Party Integration Service
     * @param referenceId The reference ID of the shipment to submit
     * @return The updated shipment with carrier tracking information
     */
    InternationalShipment submitShipmentToCarrier(String referenceId);
    
    /**
     * Cancel a shipment with the carrier
     * @param referenceId The reference ID of the shipment to cancel
     * @return The updated shipment with cancelled status
     */
    InternationalShipment cancelShipment(String referenceId);
    
    /**
     * Update the customs declaration for a shipment
     * @param referenceId The reference ID of the shipment
     * @param customsDeclaration The updated customs declaration
     * @return The updated shipment with the new customs declaration
     */
    InternationalShipment updateCustomsDeclaration(String referenceId, CustomsDeclaration customsDeclaration);
    
    /**
     * Submit a shipment for compliance approval
     * @param referenceId The reference ID of the shipment
     * @return The updated shipment with submitted status
     */
    InternationalShipment submitForComplianceApproval(String referenceId);
    
    /**
     * Approve a shipment from a compliance perspective
     * @param referenceId The reference ID of the shipment
     * @param approvedBy The name of the person approving
     * @return The updated shipment with approved status
     */
    InternationalShipment approveShipmentCompliance(String referenceId, String approvedBy);
    
    /**
     * Reject a shipment from a compliance perspective
     * @param referenceId The reference ID of the shipment
     * @param rejectionReason The reason for rejection
     * @return The updated shipment with rejected status
     */
    InternationalShipment rejectShipmentCompliance(String referenceId, String rejectionReason);
    
    /**
     * Generate shipping documents for a shipment
     * @param referenceId The reference ID of the shipment
     * @return The updated shipment with document URLs
     */
    InternationalShipment generateShippingDocuments(String referenceId);
    
    /**
     * Get shipments that need compliance attention
     * @return List of shipments needing attention
     */
    List<InternationalShipment> getShipmentsNeedingAttention();
    
    /**
     * Get shipments that are held by customs
     * @return List of shipments held by customs
     */
    List<InternationalShipment> getShipmentsHeldByCustoms();
    
    /**
     * Update the tracking information for a shipment
     * @param referenceId The reference ID of the shipment
     * @return The updated shipment with the latest tracking information
     */
    InternationalShipment updateTrackingInformation(String referenceId);
    
    /**
     * Check if a shipment is eligible for international shipping
     * @param originCountry The origin country code
     * @param destinationCountry The destination country code
     * @param categoryList The list of product categories
     * @return true if the shipment is eligible, false otherwise
     */
    boolean isEligibleForInternationalShipping(String originCountry, String destinationCountry, List<String> categoryList);
    
    /**
     * Estimate duties and taxes for a shipment
     * @param destinationCountry The destination country code
     * @param hsCodeList The list of HS codes
     * @param declaredValueList The list of declared values
     * @param currencyCode The currency code
     * @return Map of HS codes to estimated duties and taxes
     */
    java.util.Map<String, Double> estimateDutiesAndTaxes(
            String destinationCountry, 
            List<String> hsCodeList, 
            List<Double> declaredValueList,
            String currencyCode);
    
    /**
     * Delete a shipment (soft delete, marks as cancelled)
     * @param referenceId The reference ID of the shipment to delete
     * @return true if successfully deleted, false otherwise
     */
    boolean deleteShipment(String referenceId);
}
