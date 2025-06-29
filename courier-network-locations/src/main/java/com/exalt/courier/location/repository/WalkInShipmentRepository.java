package com.exalt.courier.location.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.socialecommerceecosystem.location.dto.DailyRevenue;
import com.socialecommerceecosystem.location.dto.ShipmentStatusCount;
import com.socialecommerceecosystem.location.dto.ShipmentSummary;
import com.socialecommerceecosystem.location.model.PaymentMethod;
import com.socialecommerceecosystem.location.model.ShipmentStatus;
import com.socialecommerceecosystem.location.model.WalkInShipment;

/**
 * Repository interface for WalkInShipment entity.
 * Provides methods for accessing and querying walk-in shipment data.
 * Optimized for performance with indexed queries and projections.
 */
@Repository
public interface WalkInShipmentRepository extends JpaRepository<WalkInShipment, Long> {
    
    /**
     * Find shipment by tracking number.
     * 
     * @param trackingNumber the tracking number to search for
     * @return optional shipment with the specified tracking number
     */
    Optional<WalkInShipment> findByTrackingNumber(String trackingNumber);
    
    /**
     * Get shipment summary by tracking number.
     * Uses projection to optimize data retrieval.
     * 
     * @param trackingNumber the tracking number to search for
     * @return optional shipment summary with the specified tracking number
     */
    @Query("SELECT s.id as id, s.trackingNumber as trackingNumber, s.status as status, " +
           "s.creationDate as creationDate, s.recipientName as recipientName, " +
           "s.destinationCity as destinationCity, s.destinationCountry as destinationCountry, " +
           "s.serviceType as serviceType FROM WalkInShipment s WHERE s.trackingNumber = :trackingNumber")
    Optional<ShipmentSummary> findShipmentSummaryByTrackingNumber(@Param("trackingNumber") String trackingNumber);
    
    /**
     * Find shipments by customer ID.
     * 
     * @param customerId the ID of the customer
     * @return list of shipments for the specified customer
     */
    List<WalkInShipment> findByCustomerId(Long customerId);
    
    /**
     * Find most recent shipments by customer ID.
     * Optimized to return only a limited number of most recent shipments.
     * 
     * @param customerId the ID of the customer
     * @return list of the 10 most recent shipments for the specified customer
     */
    List<WalkInShipment> findTop10ByCustomerIdOrderByCreationDateDesc(Long customerId);
    
    /**
     * Find shipment summaries by customer ID.
     * Uses projection to optimize data retrieval.
     * 
     * @param customerId the ID of the customer
     * @return list of shipment summaries for the specified customer
     */
    @Query("SELECT s.id as id, s.trackingNumber as trackingNumber, s.status as status, " +
           "s.creationDate as creationDate, s.recipientName as recipientName, " +
           "s.destinationCity as destinationCity, s.destinationCountry as destinationCountry, " +
           "s.serviceType as serviceType FROM WalkInShipment s WHERE s.customer.id = :customerId " +
           "ORDER BY s.creationDate DESC")
    List<ShipmentSummary> findShipmentSummariesByCustomerId(@Param("customerId") Long customerId);
    
    /**
     * Find shipments by customer ID with pagination.
     * 
     * @param customerId the ID of the customer
     * @param pageable pagination parameters
     * @return page of shipments for the specified customer
     */
    Page<WalkInShipment> findByCustomerId(Long customerId, Pageable pageable);
    
    /**
     * Find shipments by origin location ID.
     * 
     * @param originId the ID of the origin location
     * @return list of shipments from the specified origin
     */
    List<WalkInShipment> findByOriginId(Long originId);
    
    /**
     * Find shipments by status.
     * 
     * @param status the status to search for
     * @return list of shipments with the specified status
     */
    List<WalkInShipment> findByStatus(ShipmentStatus status);
    
    /**
     * Find shipments by status with pagination.
     * 
     * @param status the status to search for
     * @param pageable pagination parameters
     * @return page of shipments with the specified status
     */
    Page<WalkInShipment> findByStatus(ShipmentStatus status, Pageable pageable);
    
    /**
     * Find shipments by recipient phone number.
     * 
     * @param recipientPhone the recipient phone number to search for
     * @return list of shipments with the specified recipient phone
     */
    List<WalkInShipment> findByRecipientPhone(String recipientPhone);
    
    /**
     * Find shipments by recipient email.
     * 
     * @param recipientEmail the recipient email to search for
     * @return list of shipments with the specified recipient email
     */
    List<WalkInShipment> findByRecipientEmail(String recipientEmail);
    
    /**
     * Find shipments by destination country.
     * 
     * @param recipientCountry the destination country to search for
     * @return list of shipments to the specified destination country
     */
    List<WalkInShipment> findByRecipientCountry(String recipientCountry);
    
    /**
     * Find shipments by service type.
     * 
     * @param serviceType the service type to search for
     * @return list of shipments with the specified service type
     */
    List<WalkInShipment> findByServiceType(String serviceType);
    
    /**
     * Find international shipments.
     * 
     * @param international whether to find international shipments
     * @return list of international or domestic shipments
     */
    List<WalkInShipment> findByInternational(boolean international);
    
    /**
     * Find shipments by payment method.
     * 
     * @param paymentMethod the payment method to search for
     * @return list of shipments with the specified payment method
     */
    List<WalkInShipment> findByPaymentMethod(PaymentMethod paymentMethod);
    
    /**
     * Find shipments by creation date range.
     * 
     * @param startDate the start of the date range
     * @param endDate the end of the date range
     * @return list of shipments created within the specified date range
     */
    List<WalkInShipment> findByCreationDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find shipments by creation date range with pagination.
     * 
     * @param startDate the start of the date range
     * @param endDate the end of the date range
     * @param pageable pagination parameters
     * @return page of shipments created within the specified date range
     */
    Page<WalkInShipment> findByCreationDateBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    /**
     * Find shipments by estimated delivery date range.
     * 
     * @param startDate the start of the date range
     * @param endDate the end of the date range
     * @return list of shipments with estimated delivery within the specified date range
     */
    List<WalkInShipment> findByEstimatedDeliveryDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find shipments with weight above a threshold.
     * 
     * @param weightThreshold the weight threshold
     * @return list of shipments with weight above the specified threshold
     */
    List<WalkInShipment> findByWeightGreaterThan(Double weightThreshold);
    
    /**
     * Find shipments with declared value above a threshold.
     * 
     * @param valueThreshold the value threshold
     * @return list of shipments with declared value above the specified threshold
     */
    List<WalkInShipment> findByDeclaredValueGreaterThan(BigDecimal valueThreshold);
    
    /**
     * Find shipments handled by a specific staff member.
     * 
     * @param staffId the ID of the staff member
     * @return list of shipments handled by the specified staff member
     */
    List<WalkInShipment> findByHandledByStaffId(Long staffId);
    
    /**
     * Find shipments requiring action.
     * Optimized to use parameterized query for better index usage.
     * 
     * @return list of shipments requiring action
     */
    @Query("SELECT s FROM WalkInShipment s WHERE s.status IN :actionStatuses")
    List<WalkInShipment> findShipmentsRequiringAction(
            @Param("actionStatuses") List<ShipmentStatus> actionStatuses);
    
    /**
     * Find shipments with pending delivery.
     * 
     * @return list of shipments with pending delivery
     */
    @Query("SELECT s FROM WalkInShipment s WHERE s.status IN ('OUT_FOR_DELIVERY', 'DELIVERY_ATTEMPTED')")
    List<WalkInShipment> findShipmentsWithPendingDelivery();
    
    /**
     * Find high-value shipments.
     * 
     * @param threshold the value threshold
     * @return list of high-value shipments
     */
    @Query("SELECT s FROM WalkInShipment s WHERE s.declaredValue >= :threshold")
    List<WalkInShipment> findHighValueShipments(@Param("threshold") BigDecimal threshold);
    
    /**
     * Count shipments by status.
     * 
     * @param status the status to count
     * @return the count of shipments with the specified status
     */
    long countByStatus(ShipmentStatus status);
    
    /**
     * Count shipments by origin location.
     * 
     * @param originId the ID of the origin location
     * @return the count of shipments from the specified origin
     */
    long countByOriginId(Long originId);
    
    /**
     * Calculate total revenue by origin location within a date range.
     * 
     * @param originId the ID of the origin location
     * @param startDate the start of the date range
     * @param endDate the end of the date range
     * @return the total revenue for the specified origin and date range
     */
    @Query("SELECT SUM(s.totalCost) FROM WalkInShipment s WHERE s.origin.id = :originId " +
           "AND s.creationDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalRevenueByOriginAndDateRange(
            @Param("originId") Long originId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    /**
     * Calculate total revenue by service type within a date range.
     * 
     * @param serviceType the service type
     * @param startDate the start of the date range
     * @param endDate the end of the date range
     * @return the total revenue for the specified service type and date range
     */
    @Query("SELECT SUM(s.totalCost) FROM WalkInShipment s WHERE s.serviceType = :serviceType " +
           "AND s.creationDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalRevenueByServiceTypeAndDateRange(
            @Param("serviceType") String serviceType,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
            
    /**
     * Get shipment status counts for dashboard.
     * 
     * @param cutoffDate the cutoff date for including shipments
     * @return list of status counts
     */
    @Query("SELECT s.status as status, COUNT(s) as count FROM WalkInShipment s " +
           "WHERE s.creationDate >= :cutoffDate GROUP BY s.status")
    List<ShipmentStatusCount> getShipmentStatusCounts(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    /**
     * Get daily revenue for reporting.
     * 
     * @param startDate the start of the date range
     * @param endDate the end of the date range
     * @return list of daily revenue
     */
    @Query("SELECT CAST(s.creationDate as date) as day, SUM(s.totalCost) as revenue " +
           "FROM WalkInShipment s WHERE s.creationDate BETWEEN :startDate AND :endDate " +
           "GROUP BY CAST(s.creationDate as date) ORDER BY day")
    List<DailyRevenue> getDailyRevenueBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
            
    /**
     * Search shipments by destination.
     * Optimized to use a more specific query for better performance.
     * 
     * @param destination the destination to search for
     * @return list of shipments to the specified destination
     */
    @Query("SELECT s FROM WalkInShipment s WHERE " +
           "LOWER(s.destinationCity) LIKE LOWER(CONCAT('%', :destination, '%')) OR " +
           "LOWER(s.destinationState) LIKE LOWER(CONCAT('%', :destination, '%')) OR " +
           "LOWER(s.destinationCountry) LIKE LOWER(CONCAT('%', :destination, '%'))")
    List<WalkInShipment> searchShipmentsByDestination(@Param("destination") String destination);
}
