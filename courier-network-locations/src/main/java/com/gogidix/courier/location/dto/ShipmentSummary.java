package com.gogidix.courier.location.dto;

import java.time.LocalDateTime;

import com.socialecommerceecosystem.location.model.ShipmentStatus;

/**
 * Projection interface for shipment summary data.
 * Used to retrieve only the necessary fields for shipment list displays,
 * reducing query overhead and improving performance.
 */
public interface ShipmentSummary {
    
    /**
     * Get the shipment ID.
     * 
     * @return the shipment ID
     */
    Long getId();
    
    /**
     * Get the tracking number.
     * 
     * @return the tracking number
     */
    String getTrackingNumber();
    
    /**
     * Get the shipment status.
     * 
     * @return the shipment status
     */
    ShipmentStatus getStatus();
    
    /**
     * Get the creation date.
     * 
     * @return the creation date
     */
    LocalDateTime getCreationDate();
    
    /**
     * Get the recipient name.
     * 
     * @return the recipient name
     */
    String getRecipientName();
    
    /**
     * Get the destination city.
     * 
     * @return the destination city
     */
    String getDestinationCity();
    
    /**
     * Get the destination country.
     * 
     * @return the destination country
     */
    String getDestinationCountry();
    
    /**
     * Get the service type.
     * 
     * @return the service type
     */
    String getServiceType();
}
