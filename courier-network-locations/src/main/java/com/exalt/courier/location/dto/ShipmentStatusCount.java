package com.exalt.courier.location.dto;

import com.socialecommerceecosystem.location.model.ShipmentStatus;

/**
 * Projection interface for shipment status counts.
 * Used for dashboard status distribution widgets.
 */
public interface ShipmentStatusCount {
    
    /**
     * Get the shipment status.
     * 
     * @return the shipment status
     */
    ShipmentStatus getStatus();
    
    /**
     * Get the count of shipments with this status.
     * 
     * @return the count
     */
    Long getCount();
}
