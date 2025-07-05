package com.gogidix.courier.international.repository;

import com.gogidix.courier.international.model.CustomsDeclaration;
import com.gogidix.courier.international.model.CustomsDeclaration.CustomsStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing {@link CustomsDeclaration} entities.
 */
@Repository
public interface CustomsDeclarationRepository extends JpaRepository<CustomsDeclaration, String> {

    /**
     * Find a customs declaration by its reference ID
     * @param referenceId The unique reference ID
     * @return The customs declaration if found
     */
    Optional<CustomsDeclaration> findByReferenceId(String referenceId);
    
    /**
     * Find a customs declaration by its shipment ID
     * @param shipmentId The shipment ID
     * @return The customs declaration if found
     */
    Optional<CustomsDeclaration> findByShipmentId(String shipmentId);
    
    /**
     * Find customs declarations by their status
     * @param status The status to search for
     * @return List of matching customs declarations
     */
    List<CustomsDeclaration> findByStatus(CustomsStatus status);
    
    /**
     * Find customs declarations by origin and destination countries
     * @param originCountryCode The origin country code
     * @param destinationCountryCode The destination country code
     * @return List of matching customs declarations
     */
    List<CustomsDeclaration> findByOriginCountryCodeAndDestinationCountryCode(
            String originCountryCode, String destinationCountryCode);
    
    /**
     * Find customs declarations by destination country
     * @param destinationCountryCode The destination country code
     * @return List of matching customs declarations
     */
    List<CustomsDeclaration> findByDestinationCountryCode(String destinationCountryCode);
    
    /**
     * Find customs declarations by shipment purpose
     * @param shipmentPurpose The purpose of the shipment
     * @return List of matching customs declarations
     */
    List<CustomsDeclaration> findByShipmentPurpose(CustomsDeclaration.ShipmentPurpose shipmentPurpose);
    
    /**
     * Find customs declarations by declaration type
     * @param declarationType The type of declaration
     * @return List of matching customs declarations
     */
    List<CustomsDeclaration> findByDeclarationType(CustomsDeclaration.DeclarationType declarationType);
    
    /**
     * Find customs declarations for commercial shipments
     * @return List of customs declarations for commercial shipments
     */
    List<CustomsDeclaration> findByCommercialTrue();
    
    /**
     * Find customs declarations created within a date range
     * @param startDate Start of the date range
     * @param endDate End of the date range
     * @return List of matching customs declarations
     */
    List<CustomsDeclaration> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find customs declarations with a value greater than a threshold
     * @param value The threshold value
     * @return List of matching customs declarations
     */
    @Query("SELECT c FROM CustomsDeclaration c WHERE c.declaredValue > :value")
    List<CustomsDeclaration> findByDeclaredValueGreaterThan(@Param("value") Double value);
    
    /**
     * Find customs declarations with a value less than a threshold
     * @param value The threshold value
     * @return List of matching customs declarations
     */
    @Query("SELECT c FROM CustomsDeclaration c WHERE c.declaredValue < :value")
    List<CustomsDeclaration> findByDeclaredValueLessThan(@Param("value") Double value);
    
    /**
     * Count customs declarations by status
     * @param status The status to count
     * @return Number of customs declarations with the given status
     */
    long countByStatus(CustomsStatus status);
    
    /**
     * Find pending customs declarations (draft or submitted but not yet approved)
     * @return List of pending customs declarations
     */
    @Query("SELECT c FROM CustomsDeclaration c WHERE c.status = 'DRAFT' OR c.status = 'SUBMITTED' OR c.status = 'PENDING_INFORMATION'")
    List<CustomsDeclaration> findPendingDeclarations();
    
    /**
     * Find rejected customs declarations
     * @return List of rejected customs declarations
     */
    List<CustomsDeclaration> findByStatusOrderByUpdatedAtDesc(CustomsStatus status);
}
