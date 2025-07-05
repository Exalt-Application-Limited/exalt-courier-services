package com.gogidix.courier.regional.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.socialecommerceecosystem.regional.model.CourierLocation;
import com.socialecommerceecosystem.regional.repository.CourierLocationRepository;
import com.socialecommerceecosystem.regional.repository.RegionalSettingsRepository;
import com.socialecommerceecosystem.regional.service.CourierLocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of the CourierLocationService interface.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CourierLocationServiceImpl implements CourierLocationService {

    private final CourierLocationRepository courierLocationRepository;
    private final RegionalSettingsRepository regionalSettingsRepository;
    private final ObjectMapper objectMapper;

    @Override
    public List<CourierLocation> getAllCourierLocations() {
        log.debug("Retrieving all courier locations");
        List<CourierLocation> locations = courierLocationRepository.findAll();
        locations.forEach(this::processAdditionalMetadata);
        return locations;
    }

    @Override
    public Page<CourierLocation> getAllCourierLocations(Pageable pageable) {
        log.debug("Retrieving all courier locations with pagination");
        Page<CourierLocation> locations = courierLocationRepository.findAll(pageable);
        locations.forEach(this::processAdditionalMetadata);
        return locations;
    }

    @Override
    public Optional<CourierLocation> getCourierLocationById(Long id) {
        log.debug("Retrieving courier location with ID: {}", id);
        Optional<CourierLocation> location = courierLocationRepository.findById(id);
        location.ifPresent(this::processAdditionalMetadata);
        return location;
    }

    @Override
    public Optional<CourierLocation> getCourierLocationByCode(String locationCode) {
        log.debug("Retrieving courier location with code: {}", locationCode);
        Optional<CourierLocation> location = courierLocationRepository.findByLocationCode(locationCode);
        location.ifPresent(this::processAdditionalMetadata);
        return location;
    }

    @Override
    public List<CourierLocation> getActiveCourierLocations() {
        log.debug("Retrieving all active courier locations");
        List<CourierLocation> locations = courierLocationRepository.findByIsActiveTrue();
        locations.forEach(this::processAdditionalMetadata);
        return locations;
    }

    @Override
    public List<CourierLocation> getCourierLocationsByRegionalSettingsId(Long regionalSettingsId) {
        log.debug("Retrieving courier locations for regional settings ID: {}", regionalSettingsId);
        
        // Verify that the regional settings exists
        if (!regionalSettingsRepository.existsById(regionalSettingsId)) {
            throw new IllegalArgumentException("Regional settings not found with ID: " + regionalSettingsId);
        }
        
        List<CourierLocation> locations = courierLocationRepository.findByRegionalSettingsId(regionalSettingsId);
        locations.forEach(this::processAdditionalMetadata);
        return locations;
    }

    @Override
    public Page<CourierLocation> getCourierLocationsByRegionalSettingsId(Long regionalSettingsId, Pageable pageable) {
        log.debug("Retrieving courier locations for regional settings ID: {} with pagination", regionalSettingsId);
        
        // Verify that the regional settings exists
        if (!regionalSettingsRepository.existsById(regionalSettingsId)) {
            throw new IllegalArgumentException("Regional settings not found with ID: " + regionalSettingsId);
        }
        
        Page<CourierLocation> locations = courierLocationRepository.findByRegionalSettingsId(regionalSettingsId, pageable);
        locations.forEach(this::processAdditionalMetadata);
        return locations;
    }

    @Override
    public CourierLocation createCourierLocation(CourierLocation courierLocation) {
        log.debug("Creating new courier location: {}", courierLocation);
        
        if (courierLocation.getLocationCode() != null && 
            courierLocationRepository.existsByLocationCode(courierLocation.getLocationCode())) {
            throw new IllegalArgumentException("Location code already exists: " + courierLocation.getLocationCode());
        }
        
        // Verify that the regional settings exists
        if (!regionalSettingsRepository.existsById(courierLocation.getRegionalSettingsId())) {
            throw new IllegalArgumentException("Regional settings not found with ID: " + courierLocation.getRegionalSettingsId());
        }
        
        // Process additional metadata before saving
        if (courierLocation.getAdditionalMetadata() != null) {
            try {
                courierLocation.setMetadataJson(objectMapper.writeValueAsString(courierLocation.getAdditionalMetadata()));
            } catch (IOException e) {
                log.error("Error converting additional metadata to JSON", e);
                throw new IllegalArgumentException("Invalid additional metadata format");
            }
        }
        
        return courierLocationRepository.save(courierLocation);
    }

    @Override
    public CourierLocation updateCourierLocation(Long id, CourierLocation courierLocation) {
        log.debug("Updating courier location with ID: {}", id);
        
        CourierLocation existingLocation = courierLocationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Courier location not found with ID: " + id));
        
        // Check if the location code is being changed and if it already exists
        if (courierLocation.getLocationCode() != null && 
            !courierLocation.getLocationCode().equals(existingLocation.getLocationCode()) &&
            courierLocationRepository.existsByLocationCode(courierLocation.getLocationCode())) {
            throw new IllegalArgumentException("Location code already exists: " + courierLocation.getLocationCode());
        }
        
        // Verify that the regional settings exists
        if (!regionalSettingsRepository.existsById(courierLocation.getRegionalSettingsId())) {
            throw new IllegalArgumentException("Regional settings not found with ID: " + courierLocation.getRegionalSettingsId());
        }
        
        // Update fields
        existingLocation.setLocationName(courierLocation.getLocationName());
        existingLocation.setLocationCode(courierLocation.getLocationCode());
        existingLocation.setRegionalSettingsId(courierLocation.getRegionalSettingsId());
        existingLocation.setIsActive(courierLocation.getIsActive());
        existingLocation.setLocationType(courierLocation.getLocationType());
        existingLocation.setAddressLine1(courierLocation.getAddressLine1());
        existingLocation.setAddressLine2(courierLocation.getAddressLine2());
        existingLocation.setCity(courierLocation.getCity());
        existingLocation.setStateProvince(courierLocation.getStateProvince());
        existingLocation.setPostalCode(courierLocation.getPostalCode());
        existingLocation.setCountry(courierLocation.getCountry());
        existingLocation.setLatitude(courierLocation.getLatitude());
        existingLocation.setLongitude(courierLocation.getLongitude());
        existingLocation.setContactPhone(courierLocation.getContactPhone());
        existingLocation.setContactEmail(courierLocation.getContactEmail());
        existingLocation.setManagerId(courierLocation.getManagerId());
        existingLocation.setCapacity(courierLocation.getCapacity());
        existingLocation.setOperatingHours(courierLocation.getOperatingHours());
        existingLocation.setServicesOffered(courierLocation.getServicesOffered());
        existingLocation.setImageUrl(courierLocation.getImageUrl());
        existingLocation.setNotes(courierLocation.getNotes());
        existingLocation.setLocationRating(courierLocation.getLocationRating());
        existingLocation.setIsHub(courierLocation.getIsHub());
        existingLocation.setParentLocationId(courierLocation.getParentLocationId());
        existingLocation.setServiceAreaRadius(courierLocation.getServiceAreaRadius());
        existingLocation.setMaxDailyPackages(courierLocation.getMaxDailyPackages());
        existingLocation.setStorageCapacity(courierLocation.getStorageCapacity());
        existingLocation.setHasRefrigeration(courierLocation.getHasRefrigeration());
        existingLocation.setHasSecurity(courierLocation.getHasSecurity());
        existingLocation.setPropertySizeSqft(courierLocation.getPropertySizeSqft());
        
        // Process additional metadata before saving
        if (courierLocation.getAdditionalMetadata() != null) {
            try {
                existingLocation.setMetadataJson(objectMapper.writeValueAsString(courierLocation.getAdditionalMetadata()));
            } catch (IOException e) {
                log.error("Error converting additional metadata to JSON", e);
                throw new IllegalArgumentException("Invalid additional metadata format");
            }
        }
        
        CourierLocation updatedLocation = courierLocationRepository.save(existingLocation);
        processAdditionalMetadata(updatedLocation);
        return updatedLocation;
    }

    @Override
    public void deleteCourierLocation(Long id) {
        log.debug("Deleting courier location with ID: {}", id);
        
        if (!courierLocationRepository.existsById(id)) {
            throw new IllegalArgumentException("Courier location not found with ID: " + id);
        }
        
        courierLocationRepository.deleteById(id);
    }

    @Override
    public List<CourierLocation> getCourierLocationsByType(String locationType) {
        log.debug("Retrieving courier locations with type: {}", locationType);
        List<CourierLocation> locations = courierLocationRepository.findByLocationType(locationType);
        locations.forEach(this::processAdditionalMetadata);
        return locations;
    }

    @Override
    public List<CourierLocation> getCourierLocationsByCity(String city) {
        log.debug("Retrieving courier locations in city: {}", city);
        List<CourierLocation> locations = courierLocationRepository.findByCity(city);
        locations.forEach(this::processAdditionalMetadata);
        return locations;
    }

    @Override
    public List<CourierLocation> getCourierLocationsByStateProvince(String stateProvince) {
        log.debug("Retrieving courier locations in state/province: {}", stateProvince);
        List<CourierLocation> locations = courierLocationRepository.findByStateProvince(stateProvince);
        locations.forEach(this::processAdditionalMetadata);
        return locations;
    }

    @Override
    public List<CourierLocation> getCourierLocationsByCountry(String country) {
        log.debug("Retrieving courier locations in country: {}", country);
        List<CourierLocation> locations = courierLocationRepository.findByCountry(country);
        locations.forEach(this::processAdditionalMetadata);
        return locations;
    }

    @Override
    public List<CourierLocation> getCourierLocationsByManager(Long managerId) {
        log.debug("Retrieving courier locations managed by ID: {}", managerId);
        List<CourierLocation> locations = courierLocationRepository.findByManagerId(managerId);
        locations.forEach(this::processAdditionalMetadata);
        return locations;
    }

    @Override
    public List<CourierLocation> getHubLocations() {
        log.debug("Retrieving hub courier locations");
        List<CourierLocation> locations = courierLocationRepository.findByIsHubTrue();
        locations.forEach(this::processAdditionalMetadata);
        return locations;
    }

    @Override
    public List<CourierLocation> getCourierLocationsByParentLocation(Long parentLocationId) {
        log.debug("Retrieving courier locations with parent location ID: {}", parentLocationId);
        List<CourierLocation> locations = courierLocationRepository.findByParentLocationId(parentLocationId);
        locations.forEach(this::processAdditionalMetadata);
        return locations;
    }

    @Override
    public List<CourierLocation> searchCourierLocationsByName(String searchText) {
        log.debug("Searching for courier locations with name containing: {}", searchText);
        List<CourierLocation> locations = courierLocationRepository.findByLocationNameContainingIgnoreCase(searchText);
        locations.forEach(this::processAdditionalMetadata);
        return locations;
    }

    @Override
    public List<CourierLocation> getCourierLocationsByPostalCode(String postalCode) {
        log.debug("Retrieving courier locations with postal code: {}", postalCode);
        List<CourierLocation> locations = courierLocationRepository.findByPostalCode(postalCode);
        locations.forEach(this::processAdditionalMetadata);
        return locations;
    }

    @Override
    public List<CourierLocation> getCourierLocationsWithMinimumRating(BigDecimal rating) {
        log.debug("Retrieving courier locations with minimum rating: {}", rating);
        List<CourierLocation> locations = courierLocationRepository.findByLocationRatingGreaterThanEqual(rating);
        locations.forEach(this::processAdditionalMetadata);
        return locations;
    }

    @Override
    public List<CourierLocation> getCourierLocationsWithRefrigeration() {
        log.debug("Retrieving courier locations with refrigeration capabilities");
        List<CourierLocation> locations = courierLocationRepository.findByHasRefrigerationTrue();
        locations.forEach(this::processAdditionalMetadata);
        return locations;
    }

    @Override
    public List<CourierLocation> getCourierLocationsWithSecurity() {
        log.debug("Retrieving courier locations with security systems");
        List<CourierLocation> locations = courierLocationRepository.findByHasSecurityTrue();
        locations.forEach(this::processAdditionalMetadata);
        return locations;
    }

    @Override
    public List<CourierLocation> getCourierLocationsWithMinimumDailyCapacity(Integer capacity) {
        log.debug("Retrieving courier locations with minimum daily capacity: {}", capacity);
        List<CourierLocation> locations = courierLocationRepository.findByMaxDailyPackagesGreaterThanEqual(capacity);
        locations.forEach(this::processAdditionalMetadata);
        return locations;
    }

    @Override
    public List<CourierLocation> getCourierLocationsWithMinimumStorageCapacity(Integer capacity) {
        log.debug("Retrieving courier locations with minimum storage capacity: {}", capacity);
        List<CourierLocation> locations = courierLocationRepository.findByStorageCapacityGreaterThanEqual(capacity);
        locations.forEach(this::processAdditionalMetadata);
        return locations;
    }

    @Override
    public List<CourierLocation> getCourierLocationsByPropertySizeRange(Integer minSize, Integer maxSize) {
        log.debug("Retrieving courier locations with property size between {} and {} sq ft", minSize, maxSize);
        List<CourierLocation> locations = courierLocationRepository.findByPropertySizeRange(minSize, maxSize);
        locations.forEach(this::processAdditionalMetadata);
        return locations;
    }

    @Override
    public List<CourierLocation> getCourierLocationsByServiceOffered(String service) {
        log.debug("Retrieving courier locations offering service: {}", service);
        List<CourierLocation> locations = courierLocationRepository.findByServiceOffered(service);
        locations.forEach(this::processAdditionalMetadata);
        return locations;
    }

    @Override
    public List<CourierLocation> getCourierLocationsWithinRadius(BigDecimal latitude, BigDecimal longitude, Double radiusKm) {
        log.debug("Retrieving courier locations within {} km of coordinates ({}, {})", radiusKm, latitude, longitude);
        List<CourierLocation> locations = courierLocationRepository.findLocationsWithinRadius(latitude, longitude, radiusKm);
        locations.forEach(this::processAdditionalMetadata);
        return locations;
    }

    @Override
    public boolean existsByLocationCode(String locationCode) {
        log.debug("Checking if courier location exists with code: {}", locationCode);
        return courierLocationRepository.existsByLocationCode(locationCode);
    }

    @Override
    public long countByLocationType(String locationType) {
        log.debug("Counting courier locations with type: {}", locationType);
        return courierLocationRepository.countByLocationType(locationType);
    }

    @Override
    public long countByRegionalSettingsId(Long regionalSettingsId) {
        log.debug("Counting courier locations for regional settings ID: {}", regionalSettingsId);
        return courierLocationRepository.countByRegionalSettingsId(regionalSettingsId);
    }

    @Override
    public CourierLocation updateSpecificAttributes(Long id, Map<String, Object> attributes) {
        log.debug("Updating specific attributes for courier location with ID: {}", id);
        
        CourierLocation location = courierLocationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Courier location not found with ID: " + id));
        
        // Update fields based on attribute map
        attributes.forEach((key, value) -> {
            switch (key) {
                case "locationName":
                    location.setLocationName((String) value);
                    break;
                case "isActive":
                    location.setIsActive((Boolean) value);
                    break;
                case "contactPhone":
                    location.setContactPhone((String) value);
                    break;
                case "contactEmail":
                    location.setContactEmail((String) value);
                    break;
                case "managerId":
                    location.setManagerId(Long.valueOf(value.toString()));
                    break;
                case "capacity":
                    location.setCapacity((Integer) value);
                    break;
                case "operatingHours":
                    location.setOperatingHours((String) value);
                    break;
                case "servicesOffered":
                    location.setServicesOffered((String) value);
                    break;
                case "imageUrl":
                    location.setImageUrl((String) value);
                    break;
                case "notes":
                    location.setNotes((String) value);
                    break;
                case "locationRating":
                    location.setLocationRating(new BigDecimal(value.toString()));
                    break;
                case "isHub":
                    location.setIsHub((Boolean) value);
                    break;
                case "serviceAreaRadius":
                    location.setServiceAreaRadius((Integer) value);
                    break;
                case "maxDailyPackages":
                    location.setMaxDailyPackages((Integer) value);
                    break;
                case "storageCapacity":
                    location.setStorageCapacity((Integer) value);
                    break;
                case "hasRefrigeration":
                    location.setHasRefrigeration((Boolean) value);
                    break;
                case "hasSecurity":
                    location.setHasSecurity((Boolean) value);
                    break;
                case "metadata":
                    try {
                        location.setMetadataJson(objectMapper.writeValueAsString(value));
                    } catch (IOException e) {
                        log.error("Error converting metadata to JSON", e);
                        throw new IllegalArgumentException("Invalid metadata format");
                    }
                    break;
                default:
                    log.warn("Unknown attribute: {}", key);
            }
        });
        
        CourierLocation updatedLocation = courierLocationRepository.save(location);
        processAdditionalMetadata(updatedLocation);
        return updatedLocation;
    }

    @Override
    public CourierLocation activateCourierLocation(Long id) {
        log.debug("Activating courier location with ID: {}", id);
        
        CourierLocation location = courierLocationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Courier location not found with ID: " + id));
        
        location.setIsActive(true);
        
        CourierLocation activatedLocation = courierLocationRepository.save(location);
        processAdditionalMetadata(activatedLocation);
        return activatedLocation;
    }

    @Override
    public CourierLocation deactivateCourierLocation(Long id) {
        log.debug("Deactivating courier location with ID: {}", id);
        
        CourierLocation location = courierLocationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Courier location not found with ID: " + id));
        
        location.setIsActive(false);
        
        CourierLocation deactivatedLocation = courierLocationRepository.save(location);
        processAdditionalMetadata(deactivatedLocation);
        return deactivatedLocation;
    }

    @Override
    public CourierLocation reassignCourierLocationManager(Long id, Long managerId) {
        log.debug("Reassigning courier location with ID: {} to manager with ID: {}", id, managerId);
        
        CourierLocation location = courierLocationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Courier location not found with ID: " + id));
        
        location.setManagerId(managerId);
        
        CourierLocation updatedLocation = courierLocationRepository.save(location);
        processAdditionalMetadata(updatedLocation);
        return updatedLocation;
    }

    @Override
    public CourierLocation updateCourierLocationServices(Long id, String services) {
        log.debug("Updating services for courier location with ID: {} to: {}", id, services);
        
        CourierLocation location = courierLocationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Courier location not found with ID: " + id));
        
        location.setServicesOffered(services);
        
        CourierLocation updatedLocation = courierLocationRepository.save(location);
        processAdditionalMetadata(updatedLocation);
        return updatedLocation;
    }
    
    /**
     * Helper method to process additional metadata JSON into a map.
     * 
     * @param location The courier location to process
     */
    private void processAdditionalMetadata(CourierLocation location) {
        if (location.getMetadataJson() != null && !location.getMetadataJson().isEmpty()) {
            try {
                location.setAdditionalMetadata(objectMapper.readValue(location.getMetadataJson(), Map.class));
            } catch (IOException e) {
                log.error("Error parsing metadata JSON", e);
                location.setAdditionalMetadata(new HashMap<>());
            }
        } else {
            location.setAdditionalMetadata(new HashMap<>());
        }
    }
}
