package com.exalt.courierservices.commission.$1;

import com.exalt.courier.commission.model.Partner;
import com.exalt.courier.commission.model.PartnerStatus;
import com.exalt.courier.commission.model.PartnerType;
import com.exalt.courier.commission.repository.PartnerRepository;
import com.exalt.courier.commission.service.PartnerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@Service
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class PartnerServiceImpl implements PartnerService {

    
    
    @Autowired
    private PartnerRepository partnerRepository;
    
    @Override
    public Partner createPartner(Partner partner) {
        logger.info("Creating new partner: {}", partner.getName());
        return partnerRepository.save(partner);
    }
    
    @Override
    public Partner getPartner(String partnerId) {
        logger.debug("Getting partner with ID: {}", partnerId);
        return partnerRepository.findById(partnerId)
                .orElseThrow(() -> new EntityNotFoundException("Partner not found with ID: " + partnerId));
    }
    
    @Override
    public Partner updatePartner(Partner partner) {
        logger.info("Updating partner with ID: {}", partner.getId());
        
        // Verify partner exists
        if (!partnerRepository.existsById(partner.getId())) {
            throw new EntityNotFoundException("Partner not found with ID: " + partner.getId());
        }
        
        return partnerRepository.save(partner);
    }
    
    @Override
    public Partner updatePartnerStatus(String partnerId, PartnerStatus status) {
        logger.info("Updating partner status to {} for partner ID: {}", status, partnerId);
        
        Partner partner = getPartner(partnerId);
        partner.setStatus(status);
        
        return partnerRepository.save(partner);
    }
    
    @Override
    public void deletePartner(String partnerId) {
        logger.info("Deleting partner with ID: {}", partnerId);
        
        // Verify partner exists
        if (!partnerRepository.existsById(partnerId)) {
            throw new EntityNotFoundException("Partner not found with ID: " + partnerId);
        }
        
        partnerRepository.deleteById(partnerId);
    }
    
    @Override
    public List<Partner> findPartnersByType(PartnerType partnerType) {
        logger.debug("Finding partners by type: {}", partnerType);
        return partnerRepository.findByPartnerType(partnerType);
    }
    
    @Override
    public List<Partner> findPartnersByStatus(PartnerStatus status) {
        logger.debug("Finding partners by status: {}", status);
        return partnerRepository.findByStatus(status);
    }
    
    @Override
    public List<Partner> findPartnersByTypeAndStatus(PartnerType partnerType, PartnerStatus status) {
        logger.debug("Finding partners by type: {} and status: {}", partnerType, status);
        return partnerRepository.findByPartnerTypeAndStatus(partnerType, status);
    }
    
    @Override
    public List<Partner> searchPartnersByName(String namePattern) {
        logger.debug("Searching partners by name pattern: {}", namePattern);
        return partnerRepository.findByNameContainingIgnoreCase(namePattern);
    }
}

