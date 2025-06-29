package com.exalt.courier.hqadmin.service.impl;

import com.socialecommerceecosystem.hqadmin.model.GlobalPricing;
import com.socialecommerceecosystem.hqadmin.model.GlobalRegion;
import com.socialecommerceecosystem.hqadmin.repository.GlobalPricingRepository;
import com.socialecommerceecosystem.hqadmin.repository.GlobalRegionRepository;
import com.socialecommerceecosystem.hqadmin.service.GlobalPricingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the GlobalPricingService interface.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class GlobalPricingServiceImpl implements GlobalPricingService {

    private final GlobalPricingRepository globalPricingRepository;
    private final GlobalRegionRepository globalRegionRepository;

    @Override
    public List<GlobalPricing> getAllPricing() {
        return globalPricingRepository.findAll();
    }

    @Override
    public Optional<GlobalPricing> getPricingById(Long id) {
        return globalPricingRepository.findById(id);
    }

    @Override
    public Optional<GlobalPricing> getPricingByCode(String pricingCode) {
        return globalPricingRepository.findByPricingCode(pricingCode);
    }

    @Override
    @Transactional
    public GlobalPricing createPricing(GlobalPricing pricing) {
        log.info("Creating new global pricing strategy with code: {}", pricing.getPricingCode());
        
        // Check if pricing code already exists
        if (globalPricingRepository.findByPricingCode(pricing.getPricingCode()).isPresent()) {
            throw new IllegalArgumentException("Global pricing with code " + pricing.getPricingCode() + " already exists");
        }
        
        // Verify that the global region exists if provided
        if (pricing.getGlobalRegion() != null && pricing.getGlobalRegion().getId() != null) {
            GlobalRegion region = globalRegionRepository.findById(pricing.getGlobalRegion().getId())
                .orElseThrow(() -> new IllegalArgumentException("Global region not found with id: " + pricing.getGlobalRegion().getId()));
            pricing.setGlobalRegion(region);
        }
        
        return globalPricingRepository.save(pricing);
    }

    @Override
    @Transactional
    public GlobalPricing updatePricing(Long id, GlobalPricing pricingDetails) {
        log.info("Updating global pricing strategy with id: {}", id);
        
        GlobalPricing existingPricing = globalPricingRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Global pricing not found with id: " + id));
        
        // Check if pricing code is being changed and if it already exists
        if (!existingPricing.getPricingCode().equals(pricingDetails.getPricingCode()) && 
            globalPricingRepository.findByPricingCode(pricingDetails.getPricingCode()).isPresent()) {
            throw new IllegalArgumentException("Global pricing with code " + pricingDetails.getPricingCode() + " already exists");
        }
        
        // Update fields
        existingPricing.setPricingCode(pricingDetails.getPricingCode());
        existingPricing.setName(pricingDetails.getName());
        existingPricing.setDescription(pricingDetails.getDescription());
        existingPricing.setServiceType(pricingDetails.getServiceType());
        existingPricing.setBasePrice(pricingDetails.getBasePrice());
        existingPricing.setPricePerKm(pricingDetails.getPricePerKm());
        existingPricing.setPricePerKg(pricingDetails.getPricePerKg());
        existingPricing.setMinimumPrice(pricingDetails.getMinimumPrice());
        existingPricing.setCurrencyCode(pricingDetails.getCurrencyCode());
        existingPricing.setAllowRegionalOverride(pricingDetails.getAllowRegionalOverride());
        existingPricing.setIsActive(pricingDetails.getIsActive());
        existingPricing.setEffectiveFrom(pricingDetails.getEffectiveFrom());
        existingPricing.setEffectiveUntil(pricingDetails.getEffectiveUntil());
        existingPricing.setLastUpdatedBy(pricingDetails.getLastUpdatedBy());
        
        // Update global region if provided
        if (pricingDetails.getGlobalRegion() != null && pricingDetails.getGlobalRegion().getId() != null) {
            GlobalRegion newRegion = globalRegionRepository.findById(pricingDetails.getGlobalRegion().getId())
                .orElseThrow(() -> new IllegalArgumentException("Global region not found with id: " + pricingDetails.getGlobalRegion().getId()));
            existingPricing.setGlobalRegion(newRegion);
        } else {
            existingPricing.setGlobalRegion(null);
        }
        
        return globalPricingRepository.save(existingPricing);
    }

    @Override
    @Transactional
    public void deletePricing(Long id) {
        log.info("Deleting global pricing strategy with id: {}", id);
        
        GlobalPricing pricing = globalPricingRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Global pricing not found with id: " + id));
        
        globalPricingRepository.delete(pricing);
    }

    @Override
    public List<GlobalPricing> getAllActivePricing() {
        return globalPricingRepository.findByIsActiveTrue();
    }

    @Override
    public List<GlobalPricing> getPricingByServiceType(String serviceType) {
        return globalPricingRepository.findByServiceType(serviceType);
    }

    @Override
    public List<GlobalPricing> getPricingByRegion(Long regionId) {
        log.debug("Getting pricing strategies for region id: {}", regionId);
        
        GlobalRegion region = globalRegionRepository.findById(regionId)
            .orElseThrow(() -> new IllegalArgumentException("Global region not found with id: " + regionId));
        
        return globalPricingRepository.findByGlobalRegion(region);
    }

    @Override
    public List<GlobalPricing> getPricingWithRegionalOverrides() {
        return globalPricingRepository.findByAllowRegionalOverrideTrue();
    }

    @Override
    public List<GlobalPricing> getGlobalDefaultPricing() {
        return globalPricingRepository.findByGlobalRegionIsNull();
    }

    @Override
    public List<GlobalPricing> searchPricingByName(String searchText) {
        return globalPricingRepository.findByNameContainingIgnoreCase(searchText);
    }

    @Override
    public List<GlobalPricing> getPricingByCurrencyCode(String currencyCode) {
        return globalPricingRepository.findByCurrencyCode(currencyCode);
    }

    @Override
    public List<GlobalPricing> getPricingBelowBasePrice(BigDecimal maxPrice) {
        return globalPricingRepository.findByBasePriceLessThanEqual(maxPrice);
    }

    @Override
    public List<GlobalPricing> getPricingEffectiveOn(LocalDate date) {
        return globalPricingRepository.findPricingEffectiveOn(date);
    }

    @Override
    public List<GlobalPricing> getPricingByServiceTypeAndRegion(String serviceType, Long regionId) {
        log.debug("Getting pricing strategies for service type: {} and region id: {}", serviceType, regionId);
        
        GlobalRegion region = globalRegionRepository.findById(regionId)
            .orElseThrow(() -> new IllegalArgumentException("Global region not found with id: " + regionId));
        
        return globalPricingRepository.findByServiceTypeAndGlobalRegion(serviceType, region);
    }

    @Override
    public List<Object[]> countPricingByServiceType() {
        return globalPricingRepository.countPricingByServiceType();
    }

    @Override
    @Transactional
    public GlobalPricing applyGlobalPricingToRegion(Long pricingId, Long regionId) {
        log.info("Applying global pricing {} to region {}", pricingId, regionId);
        
        GlobalPricing globalPricing = globalPricingRepository.findById(pricingId)
            .orElseThrow(() -> new IllegalArgumentException("Global pricing not found with id: " + pricingId));
        
        // Ensure this is a global pricing strategy
        if (globalPricing.getGlobalRegion() != null) {
            throw new IllegalArgumentException("The pricing with id " + pricingId + " is not a global pricing strategy");
        }
        
        // Ensure regional overrides are allowed
        if (!globalPricing.getAllowRegionalOverride()) {
            throw new IllegalArgumentException("The pricing with id " + pricingId + " does not allow regional overrides");
        }
        
        GlobalRegion region = globalRegionRepository.findById(regionId)
            .orElseThrow(() -> new IllegalArgumentException("Global region not found with id: " + regionId));
        
        // Check if this pricing already exists for this region
        Optional<GlobalPricing> existingRegionalPricing = globalPricingRepository.findAll().stream()
            .filter(p -> p.getPricingCode().equals(globalPricing.getPricingCode()) && 
                     region.equals(p.getGlobalRegion()))
            .findFirst();
        
        if (existingRegionalPricing.isPresent()) {
            throw new IllegalArgumentException("A regional pricing for code " + globalPricing.getPricingCode() + 
                                             " already exists for region " + region.getName());
        }
        
        // Create a new regional pricing based on the global one
        GlobalPricing regionalPricing = GlobalPricing.builder()
            .pricingCode(globalPricing.getPricingCode() + "." + region.getRegionCode())
            .name(globalPricing.getName() + " - " + region.getName())
            .description(globalPricing.getDescription())
            .serviceType(globalPricing.getServiceType())
            .basePrice(globalPricing.getBasePrice())
            .pricePerKm(globalPricing.getPricePerKm())
            .pricePerKg(globalPricing.getPricePerKg())
            .minimumPrice(globalPricing.getMinimumPrice())
            .currencyCode(region.getCurrencyCode() != null ? region.getCurrencyCode() : globalPricing.getCurrencyCode())
            .allowRegionalOverride(false) // Regional pricing cannot be overridden further
            .globalRegion(region)
            .isActive(globalPricing.getIsActive())
            .effectiveFrom(globalPricing.getEffectiveFrom())
            .effectiveUntil(globalPricing.getEffectiveUntil())
            .lastUpdatedBy(globalPricing.getLastUpdatedBy())
            .build();
            
        return globalPricingRepository.save(regionalPricing);
    }
}
