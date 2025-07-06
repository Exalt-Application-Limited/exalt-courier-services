package com.gogidix.courier.location.service.impl;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.socialecommerceecosystem.location.model.LocationOperatingHours;
import com.socialecommerceecosystem.location.model.LocationType;
import com.socialecommerceecosystem.location.model.PhysicalLocation;
import com.socialecommerceecosystem.location.repository.LocationOperatingHoursRepository;
import com.socialecommerceecosystem.location.repository.PhysicalLocationRepository;
import com.socialecommerceecosystem.location.service.LocationManagementService;
import com.socialecommerceecosystem.location.service.NotificationService;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of the LocationManagementService interface.
 * Provides business logic for managing physical locations in the courier network.
 */
@Service
@Slf4j
public class LocationManagementServiceImpl implements LocationManagementService {

    private final PhysicalLocationRepository locationRepository;
    private final LocationOperatingHoursRepository operatingHoursRepository;
    private final NotificationService notificationService;

    @Autowired
    public LocationManagementServiceImpl(
            PhysicalLocationRepository locationRepository,
            LocationOperatingHoursRepository operatingHoursRepository,
            NotificationService notificationService) {
        this.locationRepository = locationRepository;
        this.operatingHoursRepository = operatingHoursRepository;
        this.notificationService = notificationService;
    }

    @Override
    public List<PhysicalLocation> getAllLocations() {
        log.debug("Getting all physical locations");
        return locationRepository.findAll();
    }

    @Override
    public Page<PhysicalLocation> getAllLocations(Pageable pageable) {
        log.debug("Getting physical locations with pagination: {}", pageable);
        return locationRepository.findAll(pageable);
    }

    @Override
    public Optional<PhysicalLocation> getLocationById(Long locationId) {
        log.debug("Getting physical location with ID: {}", locationId);
        return locationRepository.findById(locationId);
    }

    @Override
    @Transactional
    public PhysicalLocation createLocation(PhysicalLocation location) {
        log.info("Creating new physical location: {}", location.getName());
        
        // Set default values if not provided
        if (location.getCreatedAt() == null) {
            location.setCreatedAt(LocalDateTime.now());
        }
        if (location.getUpdatedAt() == null) {
            location.setUpdatedAt(LocalDateTime.now());
        }
        if (location.getCapacityUtilization() == null) {
            location.setCapacityUtilization(0.0);
        }
        
        PhysicalLocation savedLocation = locationRepository.save(location);
        log.info("Successfully created physical location with ID: {}", savedLocation.getId());
        
        return savedLocation;
    }

    @Override
    @Transactional
    public PhysicalLocation updateLocation(Long locationId, PhysicalLocation location) {
        log.info("Updating physical location with ID: {}", locationId);
        
        return locationRepository.findById(locationId)
                .map(existingLocation -> {
                    // Update basic information
                    existingLocation.setName(location.getName());
                    existingLocation.setDescription(location.getDescription());
                    existingLocation.setLocationType(location.getLocationType());
                    existingLocation.setAddress(location.getAddress());
                    existingLocation.setCity(location.getCity());
                    existingLocation.setState(location.getState());
                    existingLocation.setCountry(location.getCountry());
                    existingLocation.setPostalCode(location.getPostalCode());
                    existingLocation.setLatitude(location.getLatitude());
                    existingLocation.setLongitude(location.getLongitude());
                    existingLocation.setContactPhone(location.getContactPhone());
                    existingLocation.setContactEmail(location.getContactEmail());
                    existingLocation.setCapacity(location.getCapacity());
                    existingLocation.setCapacityUnit(location.getCapacityUnit());
                    existingLocation.setCapacityUtilization(location.getCapacityUtilization());
                    existingLocation.setServiceTypes(location.getServiceTypes());
                    existingLocation.setNotes(location.getNotes());
                    existingLocation.setActive(location.isActive());
                    existingLocation.setUpdatedAt(LocalDateTime.now());
                    
                    PhysicalLocation updatedLocation = locationRepository.save(existingLocation);
                    log.info("Successfully updated physical location with ID: {}", updatedLocation.getId());
                    
                    return updatedLocation;
                })
                .orElseThrow(() -> {
                    log.error("Location with ID: {} not found", locationId);
                    return new IllegalArgumentException("Location not found with ID: " + locationId);
                });
    }

    @Override
    @Transactional
    public void deleteLocation(Long locationId) {
        log.info("Deleting physical location with ID: {}", locationId);
        
        if (locationRepository.existsById(locationId)) {
            locationRepository.deleteById(locationId);
            log.info("Successfully deleted physical location with ID: {}", locationId);
        } else {
            log.error("Location with ID: {} not found", locationId);
            throw new IllegalArgumentException("Location not found with ID: " + locationId);
        }
    }

    @Override
    public List<PhysicalLocation> getLocationsByType(LocationType type) {
        log.debug("Getting physical locations with type: {}", type);
        return locationRepository.findByLocationType(type);
    }

    @Override
    public List<PhysicalLocation> getLocationsByCountryAndState(String country, String state) {
        log.debug("Getting physical locations in country: {} and state: {}", country, state);
        return locationRepository.findByCountryAndState(country, state);
    }

    @Override
    public List<PhysicalLocation> getLocationsByCity(String city) {
        log.debug("Getting physical locations in city: {}", city);
        return locationRepository.findByCity(city);
    }

    @Override
    public List<PhysicalLocation> findLocationsByServiceType(String serviceType) {
        log.debug("Finding physical locations offering service type: {}", serviceType);
        return locationRepository.findByServiceTypesContaining(serviceType);
    }

    @Override
    public List<PhysicalLocation> findNearbyLocations(double latitude, double longitude, double radiusKm) {
        log.debug("Finding physical locations near coordinates: [{}, {}] within {}km", 
                latitude, longitude, radiusKm);
        
        // Calculate the approximate degree distance for the radius
        // 1 degree of latitude is approximately 111 kilometers
        double latitudeDelta = radiusKm / 111.0;
        
        // 1 degree of longitude varies with latitude, approximate using cosine
        double longitudeDelta = radiusKm / (111.0 * Math.cos(Math.toRadians(latitude)));
        
        double minLatitude = latitude - latitudeDelta;
        double maxLatitude = latitude + latitudeDelta;
        double minLongitude = longitude - longitudeDelta;
        double maxLongitude = longitude + longitudeDelta;
        
        List<PhysicalLocation> locationsInBox = locationRepository
                .findByLatitudeBetweenAndLongitudeBetween(
                        minLatitude, maxLatitude, minLongitude, maxLongitude);
        
        // Filter by actual distance (Haversine formula)
        return locationsInBox.stream()
                .filter(location -> calculateDistanceKm(
                        latitude, longitude, 
                        location.getLatitude(), location.getLongitude()) <= radiusKm)
                .collect(Collectors.toList());
    }
    
    /**
     * Calculate distance between two coordinates using the Haversine formula.
     * 
     * @param lat1 latitude of first point
     * @param lon1 longitude of first point
     * @param lat2 latitude of second point
     * @param lon2 longitude of second point
     * @return distance in kilometers
     */
    private double calculateDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS_KM = 6371;
        
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                   Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        
        return EARTH_RADIUS_KM * c;
    }

    @Override
    public List<PhysicalLocation> getActiveLocations() {
        log.debug("Getting active physical locations");
        return locationRepository.findByActiveTrue();
    }

    @Override
    @Transactional
    public PhysicalLocation updateLocationStatus(Long locationId, boolean active) {
        log.info("Updating status of physical location with ID: {} to active: {}", locationId, active);
        
        return locationRepository.findById(locationId)
                .map(existingLocation -> {
                    existingLocation.setActive(active);
                    existingLocation.setUpdatedAt(LocalDateTime.now());
                    PhysicalLocation updatedLocation = locationRepository.save(existingLocation);
                    log.info("Successfully updated status of physical location with ID: {}", updatedLocation.getId());
                    return updatedLocation;
                })
                .orElseThrow(() -> {
                    log.error("Location with ID: {} not found", locationId);
                    return new IllegalArgumentException("Location not found with ID: " + locationId);
                });
    }

    @Override
    @Transactional
    public LocationOperatingHours addOperatingHours(Long locationId, LocationOperatingHours operatingHours) {
        log.info("Adding operating hours for physical location with ID: {}", locationId);
        
        return locationRepository.findById(locationId)
                .map(location -> {
                    operatingHours.setPhysicalLocation(location);
                    if (operatingHours.getCreatedAt() == null) {
                        operatingHours.setCreatedAt(LocalDateTime.now());
                    }
                    if (operatingHours.getUpdatedAt() == null) {
                        operatingHours.setUpdatedAt(LocalDateTime.now());
                    }
                    
                    LocationOperatingHours savedHours = operatingHoursRepository.save(operatingHours);
                    log.info("Successfully added operating hours with ID: {} for location ID: {}", 
                            savedHours.getId(), locationId);
                    return savedHours;
                })
                .orElseThrow(() -> {
                    log.error("Location with ID: {} not found", locationId);
                    return new IllegalArgumentException("Location not found with ID: " + locationId);
                });
    }

    @Override
    public List<LocationOperatingHours> getOperatingHoursByLocation(Long locationId) {
        log.debug("Getting operating hours for physical location with ID: {}", locationId);
        return operatingHoursRepository.findByPhysicalLocationId(locationId);
    }

    @Override
    @Transactional
    public void updateCapacityUtilization(Long locationId, Double capacityUtilization) {
        log.info("Updating capacity utilization for physical location with ID: {} to: {}", 
                locationId, capacityUtilization);
        
        locationRepository.findById(locationId)
                .map(location -> {
                    location.setCapacityUtilization(capacityUtilization);
                    location.setUpdatedAt(LocalDateTime.now());
                    
                    PhysicalLocation updatedLocation = locationRepository.save(location);
                    log.info("Successfully updated capacity utilization for location ID: {}", locationId);
                    
                    // Check if capacity utilization is above warning threshold (85%) and send notification
                    if (capacityUtilization >= 85.0) {
                        notificationService.notifyStaffAboutHighCapacityUtilization(
                                locationId, capacityUtilization);
                    }
                    
                    return updatedLocation;
                })
                .orElseThrow(() -> {
                    log.error("Location with ID: {} not found", locationId);
                    return new IllegalArgumentException("Location not found with ID: " + locationId);
                });
    }

    @Override
    public boolean isLocationOpen(Long locationId, LocalDateTime dateTime) {
        log.debug("Checking if physical location with ID: {} is open at: {}", locationId, dateTime);
        
        // Get day of week
        DayOfWeek dayOfWeek = dateTime.getDayOfWeek();
        LocalTime time = dateTime.toLocalTime();
        
        // Find operating hours for this location and day of week
        return operatingHoursRepository.findByPhysicalLocationIdAndDayOfWeek(locationId, dayOfWeek)
                .map(hours -> hours.isOpenAt(time))
                .orElse(false);
    }

    @Override
    public Map<String, Long> getLocationCountsByType() {
        log.debug("Getting location counts by type");
        
        Map<String, Long> countsByType = new HashMap<>();
        for (LocationType type : LocationType.values()) {
            long count = locationRepository.countByLocationType(type);
            countsByType.put(type.getDisplayName(), count);
        }
        
        return countsByType;
    }

    @Override
    public Map<String, Long> getLocationCountsByCountry() {
        log.debug("Getting location counts by country");
        return locationRepository.countByCountry();
    }

    @Override
    public List<PhysicalLocation> getLocationsByCapacityUtilizationRange(
            Double minUtilization, Double maxUtilization) {
        log.debug("Getting locations with capacity utilization between {} and {}", 
                minUtilization, maxUtilization);
        return locationRepository.findByCapacityUtilizationBetween(minUtilization, maxUtilization);
    }

    @Override
    @Transactional
    public PhysicalLocation updateLocationServiceTypes(Long locationId, List<String> serviceTypes) {
        log.info("Updating service types for physical location with ID: {}", locationId);
        
        return locationRepository.findById(locationId)
                .map(location -> {
                    location.setServiceTypes(serviceTypes);
                    location.setUpdatedAt(LocalDateTime.now());
                    
                    PhysicalLocation updatedLocation = locationRepository.save(location);
                    log.info("Successfully updated service types for location ID: {}", locationId);
                    return updatedLocation;
                })
                .orElseThrow(() -> {
                    log.error("Location with ID: {} not found", locationId);
                    return new IllegalArgumentException("Location not found with ID: " + locationId);
                });
    }

    @Override
    @Transactional
    public void setAllLocationsInactive() {
        log.warn("Setting all physical locations to inactive");
        locationRepository.findAll().forEach(location -> {
            location.setActive(false);
            location.setUpdatedAt(LocalDateTime.now());
            locationRepository.save(location);
        });
        log.info("Successfully set all physical locations to inactive");
    }
}
