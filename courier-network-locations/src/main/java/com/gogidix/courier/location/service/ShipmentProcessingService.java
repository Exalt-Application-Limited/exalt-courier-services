package com.gogidix.courier.location.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.socialecommerceecosystem.location.model.PaymentMethod;
import com.socialecommerceecosystem.location.model.ShipmentStatus;
import com.socialecommerceecosystem.location.model.WalkInShipment;

/**
 * Service interface for processing walk-in shipments at physical courier locations.
 * Provides high-level business functions for shipment management.
 */
public interface ShipmentProcessingService {
    
    /**
     * Get all shipments.
     * 
     * @return list of all shipments
     */
    List<WalkInShipment> getAllShipments();
    
    /**
     * Get all shipments with pagination.
     * 
     * @param pageable pagination parameters
     * @return page of shipments
     */
    Page<WalkInShipment> getAllShipments(Pageable pageable);
    
    /**
     * Get a shipment by ID.
     * 
     * @param shipmentId the ID of the shipment
     * @return optional containing the shipment if found
     */
    Optional<WalkInShipment> getShipmentById(Long shipmentId);
    
    /**
     * Get a shipment by tracking number.
     * 
     * @param trackingNumber the tracking number of the shipment
     * @return optional containing the shipment if found
     */
    Optional<WalkInShipment> getShipmentByTrackingNumber(String trackingNumber);
    
    /**
     * Create a new shipment.
     * 
     * @param shipment the shipment to create
     * @return the created shipment
     */
    WalkInShipment createShipment(WalkInShipment shipment);
    
    /**
     * Update an existing shipment.
     * 
     * @param shipmentId the ID of the shipment to update
     * @param shipment the updated shipment details
     * @return the updated shipment
     */
    WalkInShipment updateShipment(Long shipmentId, WalkInShipment shipment);
    
    /**
     * Delete a shipment.
     * 
     * @param shipmentId the ID of the shipment to delete
     */
    void deleteShipment(Long shipmentId);
    
    /**
     * Get shipments by customer.
     * 
     * @param customerId the ID of the customer
     * @return list of shipments for the specified customer
     */
    List<WalkInShipment> getShipmentsByCustomer(Long customerId);
    
    /**
     * Get shipments by origin location.
     * 
     * @param locationId the ID of the origin location
     * @return list of shipments from the specified origin
     */
    List<WalkInShipment> getShipmentsByOrigin(Long locationId);
    
    /**
     * Get shipments by status.
     * 
     * @param status the status of shipments to find
     * @return list of shipments with the specified status
     */
    List<WalkInShipment> getShipmentsByStatus(ShipmentStatus status);
    
    /**
     * Get shipments by creation date range.
     * 
     * @param startDate the start of the date range
     * @param endDate the end of the date range
     * @return list of shipments created within the specified date range
     */
    List<WalkInShipment> getShipmentsByCreationDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Get shipments by estimated delivery date range.
     * 
     * @param startDate the start of the date range
     * @param endDate the end of the date range
     * @return list of shipments with estimated delivery within the specified date range
     */
    List<WalkInShipment> getShipmentsByEstimatedDeliveryDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Update the status of a shipment.
     * 
     * @param shipmentId the ID of the shipment
     * @param newStatus the new status
     * @return the updated shipment
     */
    WalkInShipment updateShipmentStatus(Long shipmentId, ShipmentStatus newStatus);
    
    /**
     * Find shipments by recipient contact information.
     * 
     * @param contactInfo the recipient phone or email
     * @return list of shipments with matching recipient contact info
     */
    List<WalkInShipment> findShipmentsByRecipientContact(String contactInfo);
    
    /**
     * Find shipments to a specific destination country.
     * 
     * @param country the destination country
     * @return list of shipments to the specified country
     */
    List<WalkInShipment> findShipmentsByDestinationCountry(String country);
    
    /**
     * Find shipments by service type.
     * 
     * @param serviceType the service type
     * @return list of shipments with the specified service type
     */
    List<WalkInShipment> findShipmentsByServiceType(String serviceType);
    
    /**
     * Find international shipments.
     * 
     * @return list of international shipments
     */
    List<WalkInShipment> findInternationalShipments();
    
    /**
     * Find shipments by payment method.
     * 
     * @param paymentMethod the payment method
     * @return list of shipments with the specified payment method
     */
    List<WalkInShipment> findShipmentsByPaymentMethod(PaymentMethod paymentMethod);
    
    /**
     * Generate a tracking number for a new shipment.
     * 
     * @param locationId the ID of the origin location
     * @return the generated tracking number
     */
    String generateTrackingNumber(Long locationId);
    
    /**
     * Calculate shipping cost for a shipment.
     * 
     * @param weight the weight of the package
     * @param dimensions the dimensions [length, width, height]
     * @param serviceType the service type
     * @param international whether the shipment is international
     * @param destinationCountry the destination country
     * @return the calculated shipping cost
     */
    BigDecimal calculateShippingCost(
            Double weight, List<Double> dimensions, String serviceType,
            boolean international, String destinationCountry);
    
    /**
     * Calculate insurance cost for a shipment.
     * 
     * @param declaredValue the declared value of the shipment
     * @param international whether the shipment is international
     * @return the calculated insurance cost
     */
    BigDecimal calculateInsuranceCost(BigDecimal declaredValue, boolean international);
    
    /**
     * Calculate tax amount for a shipment.
     * 
     * @param shippingCost the shipping cost
     * @param insuranceCost the insurance cost
     * @param destinationCountry the destination country
     * @return the calculated tax amount
     */
    BigDecimal calculateTaxAmount(BigDecimal shippingCost, BigDecimal insuranceCost, String destinationCountry);
    
    /**
     * Find shipments requiring action (exception, delivery attempted, etc.).
     * 
     * @return list of shipments requiring action
     */
    List<WalkInShipment> findShipmentsRequiringAction();
    
    /**
     * Find shipments with pending delivery.
     * 
     * @return list of shipments with pending delivery
     */
    List<WalkInShipment> findShipmentsWithPendingDelivery();
    
    /**
     * Find high-value shipments.
     * 
     * @param threshold the value threshold
     * @return list of high-value shipments
     */
    List<WalkInShipment> findHighValueShipments(BigDecimal threshold);
    
    /**
     * Calculate total revenue by origin location within a date range.
     * 
     * @param locationId the ID of the location
     * @param startDate the start of the date range
     * @param endDate the end of the date range
     * @return the total revenue
     */
    BigDecimal calculateRevenueByLocationAndDateRange(Long locationId, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Calculate total revenue by service type within a date range.
     * 
     * @param serviceType the service type
     * @param startDate the start of the date range
     * @param endDate the end of the date range
     * @return the total revenue
     */
    BigDecimal calculateRevenueByServiceTypeAndDateRange(String serviceType, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Get shipment counts by status.
     * 
     * @return map of shipment counts by status
     */
    Map<ShipmentStatus, Long> getShipmentCountsByStatus();
    
    /**
     * Get shipment counts by origin location.
     * 
     * @return map of shipment counts by location
     */
    Map<Long, Long> getShipmentCountsByLocation();
    
    /**
     * Get shipment counts by service type.
     * 
     * @return map of shipment counts by service type
     */
    Map<String, Long> getShipmentCountsByServiceType();
    
    /**
     * Get shipment counts by destination country.
     * 
     * @return map of shipment counts by country
     */
    Map<String, Long> getShipmentCountsByDestinationCountry();
    
    /**
     * Check if a tracking number already exists.
     * 
     * @param trackingNumber the tracking number to check
     * @return true if the tracking number exists, false otherwise
     */
    boolean existsByTrackingNumber(String trackingNumber);
}
