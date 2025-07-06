package com.gogidix.courier.courier.hqadmin.service.impl;

import com.socialecommerceecosystem.hqadmin.model.GlobalRegion;
import com.socialecommerceecosystem.hqadmin.repository.GlobalRegionRepository;
import com.socialecommerceecosystem.hqadmin.service.GlobalRegionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the GlobalRegionService interface.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class GlobalRegionServiceImpl implements GlobalRegionService {

    private final GlobalRegionRepository globalRegionRepository;

    @Override
    public List<GlobalRegion> getAllRegions() {
        return globalRegionRepository.findAll();
    }

    @Override
    public Optional<GlobalRegion> getRegionById(Long id) {
        return globalRegionRepository.findById(id);
    }

    @Override
    public Optional<GlobalRegion> getRegionByCode(String regionCode) {
        return globalRegionRepository.findByRegionCode(regionCode);
    }

    @Override
    @Transactional
    public GlobalRegion createRegion(GlobalRegion region) {
        log.info("Creating new global region with code: {}", region.getRegionCode());
        
        // Check if region code already exists
        if (globalRegionRepository.findByRegionCode(region.getRegionCode()).isPresent()) {
            throw new IllegalArgumentException("Region with code " + region.getRegionCode() + " already exists");
        }
        
        return globalRegionRepository.save(region);
    }

    @Override
    @Transactional
    public GlobalRegion updateRegion(Long id, GlobalRegion regionDetails) {
        log.info("Updating global region with id: {}", id);
        
        return globalRegionRepository.findById(id)
            .map(existingRegion -> {
                // Check if region code is being changed and if it already exists
                if (!existingRegion.getRegionCode().equals(regionDetails.getRegionCode()) && 
                    globalRegionRepository.findByRegionCode(regionDetails.getRegionCode()).isPresent()) {
                    throw new IllegalArgumentException("Region with code " + regionDetails.getRegionCode() + " already exists");
                }
                
                // Update fields
                existingRegion.setRegionCode(regionDetails.getRegionCode());
                existingRegion.setName(regionDetails.getName());
                existingRegion.setDescription(regionDetails.getDescription());
                existingRegion.setIsActive(regionDetails.getIsActive());
                existingRegion.setTimezone(regionDetails.getTimezone());
                existingRegion.setLocale(regionDetails.getLocale());
                existingRegion.setCurrencyCode(regionDetails.getCurrencyCode());
                existingRegion.setLatitude(regionDetails.getLatitude());
                existingRegion.setLongitude(regionDetails.getLongitude());
                
                // Don't update parent region here, handled separately
                
                return globalRegionRepository.save(existingRegion);
            })
            .orElseThrow(() -> new IllegalArgumentException("Global region not found with id: " + id));
    }

    @Override
    @Transactional
    public void deleteRegion(Long id) {
        log.info("Deleting global region with id: {}", id);
        
        GlobalRegion region = globalRegionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Global region not found with id: " + id));
        
        // Check if region has child regions
        if (!region.getChildRegions().isEmpty()) {
            throw new IllegalStateException("Cannot delete region with child regions");
        }
        
        // Check if region has regional admin systems
        if (!region.getRegionalAdminSystems().isEmpty()) {
            throw new IllegalStateException("Cannot delete region with associated regional admin systems");
        }
        
        globalRegionRepository.delete(region);
    }

    @Override
    public List<GlobalRegion> getAllActiveRegions() {
        return globalRegionRepository.findByIsActiveTrue();
    }

    @Override
    public List<GlobalRegion> getTopLevelRegions() {
        return globalRegionRepository.findByParentRegionIsNull();
    }

    @Override
    public List<GlobalRegion> getChildRegions(Long parentRegionId) {
        log.debug("Getting child regions for parent region id: {}", parentRegionId);
        
        GlobalRegion parentRegion = globalRegionRepository.findById(parentRegionId)
            .orElseThrow(() -> new IllegalArgumentException("Parent region not found with id: " + parentRegionId));
        
        return globalRegionRepository.findByParentRegion(parentRegion);
    }

    @Override
    public List<GlobalRegion> searchRegionsByName(String searchText) {
        return globalRegionRepository.findByNameContainingIgnoreCase(searchText);
    }

    @Override
    @Transactional
    public GlobalRegion addChildToParentRegion(Long parentId, Long childId) {
        log.info("Adding region {} as child of parent region {}", childId, parentId);
        
        GlobalRegion parentRegion = globalRegionRepository.findById(parentId)
            .orElseThrow(() -> new IllegalArgumentException("Parent region not found with id: " + parentId));
        
        GlobalRegion childRegion = globalRegionRepository.findById(childId)
            .orElseThrow(() -> new IllegalArgumentException("Child region not found with id: " + childId));
        
        // Check for circular reference
        GlobalRegion currentParent = parentRegion.getParentRegion();
        while (currentParent != null) {
            if (currentParent.getId().equals(childId)) {
                throw new IllegalArgumentException("Circular reference detected: cannot add a parent/ancestor as a child");
            }
            currentParent = currentParent.getParentRegion();
        }
        
        // Remove child from its current parent if it has one
        if (childRegion.getParentRegion() != null) {
            childRegion.getParentRegion().removeChildRegion(childRegion);
        }
        
        // Add child to new parent
        parentRegion.addChildRegion(childRegion);
        
        return globalRegionRepository.save(parentRegion);
    }

    @Override
    @Transactional
    public GlobalRegion removeChildFromParentRegion(Long parentId, Long childId) {
        log.info("Removing region {} from parent region {}", childId, parentId);
        
        GlobalRegion parentRegion = globalRegionRepository.findById(parentId)
            .orElseThrow(() -> new IllegalArgumentException("Parent region not found with id: " + parentId));
        
        GlobalRegion childRegion = globalRegionRepository.findById(childId)
            .orElseThrow(() -> new IllegalArgumentException("Child region not found with id: " + childId));
        
        // Check if child is actually a child of this parent
        if (!childRegion.getParentRegion().getId().equals(parentId)) {
            throw new IllegalArgumentException("Region " + childId + " is not a child of region " + parentId);
        }
        
        parentRegion.removeChildRegion(childRegion);
        
        return globalRegionRepository.save(parentRegion);
    }

    @Override
    public List<GlobalRegion> findRegionsByCurrencyCode(String currencyCode) {
        return globalRegionRepository.findByCurrencyCode(currencyCode);
    }

    @Override
    public List<GlobalRegion> findRegionsInBoundingBox(Double minLat, Double maxLat, Double minLong, Double maxLong) {
        return globalRegionRepository.findRegionsInBoundingBox(minLat, maxLat, minLong, maxLong);
    }

    @Override
    public Long countChildRegions(Long parentId) {
        // Verify parent exists
        globalRegionRepository.findById(parentId)
            .orElseThrow(() -> new IllegalArgumentException("Parent region not found with id: " + parentId));
        
        return globalRegionRepository.countChildRegions(parentId);
    }
}
