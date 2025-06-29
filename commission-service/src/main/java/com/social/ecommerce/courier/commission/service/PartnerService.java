package com.exalt.courierservices.commission.$1;

import com.exalt.courier.commission.model.Partner;
import com.exalt.courier.commission.model.PartnerStatus;
import com.exalt.courier.commission.model.PartnerType;

import java.util.List;

public interface PartnerService {
    
    /**
     * Create a new partner
     */
    Partner createPartner(Partner partner);
    
    /**
     * Get partner by ID
     */
    Partner getPartner(String partnerId);
    
    /**
     * Update existing partner
     */
    Partner updatePartner(Partner partner);
    
    /**
     * Update partner status
     */
    Partner updatePartnerStatus(String partnerId, PartnerStatus status);
    
    /**
     * Delete partner
     */
    void deletePartner(String partnerId);
    
    /**
     * Find partners by type
     */
    List<Partner> findPartnersByType(PartnerType partnerType);
    
    /**
     * Find partners by status
     */
    List<Partner> findPartnersByStatus(PartnerStatus status);
    
    /**
     * Find partners by type and status
     */
    List<Partner> findPartnersByTypeAndStatus(PartnerType partnerType, PartnerStatus status);
    
    /**
     * Search partners by name pattern
     */
    List<Partner> searchPartnersByName(String namePattern);
}
