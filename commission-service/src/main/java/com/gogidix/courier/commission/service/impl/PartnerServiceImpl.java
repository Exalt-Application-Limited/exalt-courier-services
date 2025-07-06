package com.gogidix.courier.commission.service;

import com.gogidix.courier.commission.model.Partner;
import com.gogidix.courier.commission.model.PartnerStatus;
import com.gogidix.courier.commission.model.PartnerType;
import com.gogidix.courier.commission.repository.PartnerRepository;
import com.gogidix.courier.commission.service.PartnerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@Service
@Slf4j
public class PartnerServiceImpl implements PartnerService {

    
    
    @Autowired
    private PartnerRepository partnerRepository;
    
    @Override
    public Partner createPartner(Partner partner) {
        log.info("Creating new partner: {}", partner.getName());
        return partnerRepository.save(partner);
    }
    
    @Override
    public Partner getPartner(String partnerId) {
        log.debug("Getting partner with ID: {}", partnerId);
        return partnerRepository.findById(partnerId)
                .orElseThrow(() -> new EntityNotFoundException("Partner not found with ID: " + partnerId));
    }
    
    @Override
    public Partner updatePartner(Partner partner) {
        log.info("Updating partner with ID: {}", partner.getId());
        
        // Verify partner exists
        if (!partnerRepository.existsById(partner.getId())) {
            throw new EntityNotFoundException("Partner not found with ID: " + partner.getId());
        }
        
        return partnerRepository.save(partner);
    }
    
    @Override
    public Partner updatePartnerStatus(String partnerId, PartnerStatus status) {
        log.info("Updating partner status to {} for partner ID: {}", status, partnerId);
        
        Partner partner = getPartner(partnerId);
        partner.setStatus(status);
        
        return partnerRepository.save(partner);
    }
    
    @Override
    public void deletePartner(String partnerId) {
        log.info("Deleting partner with ID: {}", partnerId);
        
        // Verify partner exists
        if (!partnerRepository.existsById(partnerId)) {
            throw new EntityNotFoundException("Partner not found with ID: " + partnerId);
        }
        
        partnerRepository.deleteById(partnerId);
    }
    
    @Override
    public List<Partner> findPartnersByType(PartnerType partnerType) {
        log.debug("Finding partners by type: {}", partnerType);
        return partnerRepository.findByPartnerType(partnerType);
    }
    
    @Override
    public List<Partner> findPartnersByStatus(PartnerStatus status) {
        log.debug("Finding partners by status: {}", status);
        return partnerRepository.findByStatus(status);
    }
    
    @Override
    public List<Partner> findPartnersByTypeAndStatus(PartnerType partnerType, PartnerStatus status) {
        log.debug("Finding partners by type: {} and status: {}", partnerType, status);
        return partnerRepository.findByPartnerTypeAndStatus(partnerType, status);
    }
    
    @Override
    public List<Partner> searchPartnersByName(String namePattern) {
        log.debug("Searching partners by name pattern: {}", namePattern);
        return partnerRepository.findByNameContainingIgnoreCase(namePattern);
    }
}

