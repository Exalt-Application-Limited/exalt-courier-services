package com.exalt.courier.international.repository;

import com.exalt.courier.international.model.InternationalShipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing {@link InternationalShipment} entities.
 */
@Repository
public interface InternationalShipmentRepository extends JpaRepository<InternationalShipment, String> {

    /**
     * Find a shipment by its reference ID
     * @param referenceId The unique reference ID
     * @return The shipment if found
     */
    Optional<InternationalShipment> findByReferenceId(String referenceId);
    
    /**
     * Find a shipment by its tracking number
     * @param trackingNumber The tracking number
     * @return The shipment if found
     */
    Optional<InternationalShipment> findByTrackingNumber(String trackingNumber);
    
    /**
     * Find a shipment by its external shipment ID
     * @param externalShipmentId The external ID from the carrier
     * @return The shipment if found
     */
    Optional<InternationalShipment> findByExternalShipmentId(String externalShipmentId);
    
    /**
     * Find shipments by their current status
     * @param status The status to search for
     * @return List of matching shipments
     */
    List<InternationalShipment> findByStatus(InternationalShipment.ShipmentStatus status);
    
    /**
     * Find shipments by destination country
     * @param destinationCountryCode The destination country code
     * @return List of matching shipments
     */
    List<InternationalShipment> findByDestinationCountryCode(String destinationCountryCode);
    
    /**
     * Find shipments by origin country
     * @param originCountryCode The origin country code
     * @return List of matching shipments
     */
    List<InternationalShipment> findByOriginCountryCode(String originCountryCode);
    
    /**
     * Find shipments by carrier
     * @param carrierCode The carrier code
     * @return List of matching shipments
     */
    List<InternationalShipment> findByCarrierCode(String carrierCode);
    
    /**
     * Find shipments that were created within a date range
     * @param startDate Start of the date range
     * @param endDate End of the date range
     * @return List of matching shipments
     */
    List<InternationalShipment> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find shipments that are pending compliance approval
     * @return List of shipments awaiting compliance approval
     */
    List<InternationalShipment> findByStatusAndComplianceApproved(
            InternationalShipment.ShipmentStatus status, boolean complianceApproved);
    
    /**
     * Find shipments that contain controlled items
     * @return List of shipments with controlled items
     */
    List<InternationalShipment> findByContainsControlledItemsTrue();
    
    /**
     * Find shipments with special handling requirements
     * @return List of shipments requiring special handling
     */
    List<InternationalShipment> findBySpecialHandlingRequiredTrue();
    
    /**
     * Count shipments by status
     * @param status The status to count
     * @return Number of shipments with the given status
     */
    long countByStatus(InternationalShipment.ShipmentStatus status);
    
    /**
     * Search for shipments by reference ID or tracking number
     * @param searchTerm The search term
     * @return List of matching shipments
     */
    @Query("SELECT s FROM InternationalShipment s WHERE s.referenceId LIKE %:searchTerm% OR s.trackingNumber LIKE %:searchTerm%")
    List<InternationalShipment> searchByReferenceIdOrTrackingNumber(@Param("searchTerm") String searchTerm);
    
    /**
     * Find shipments that need attention (held by customs or compliance rejected)
     * @return List of shipments that need attention
     */
    @Query("SELECT s FROM InternationalShipment s WHERE s.status = 'HELD_BY_CUSTOMS' OR s.status = 'COMPLIANCE_REJECTED'")
    List<InternationalShipment> findShipmentsNeedingAttention();
    
    /**
     * Find shipments with customs declarations in a particular status
     * @param customsStatus The customs status to search for
     * @return List of matching shipments
     */
    @Query("SELECT s FROM InternationalShipment s JOIN s.customsDeclaration c WHERE c.status = :customsStatus")
    List<InternationalShipment> findByCustomsDeclarationStatus(@Param("customsStatus") String customsStatus);
}
