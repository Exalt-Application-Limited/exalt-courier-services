package com.exalt.courier.routing.api;

import com.exalt.courier.routing.exception.ResourceNotFoundException;
import com.exalt.courier.routing.model.Location;
import com.exalt.courier.routing.repository.LocationRepository;
import com.exalt.courier.routing.service.geo.GeoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * REST controller for Location resource operations.
 */
@RestController
@RequestMapping("/api/routing/locations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Locations", description = "API for location management operations")
public class LocationController {

    private final LocationRepository locationRepository;
    private final GeoService geoService;

    @GetMapping
    @Operation(summary = "Get all locations", description = "Retrieves all locations in the system")
    public ResponseEntity<List<Location>> getAllLocations() {
        log.info("Retrieving all locations");
        List<Location> locations = locationRepository.findAll();
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get location by ID", description = "Retrieves a location by its ID")
    public ResponseEntity<Location> getLocationById(
            @Parameter(description = "Location ID") @PathVariable Long id) {
        log.info("Retrieving location with ID: {}", id);
        
        return locationRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + id));
    }

    @PostMapping
    @Operation(summary = "Create location", description = "Creates a new location")
    public ResponseEntity<Location> createLocation(
            @Parameter(description = "Location details") @Valid @RequestBody Location location) {
        log.info("Creating new location: {}", location.getAddress());
        
        // Set timestamps
        LocalDateTime now = LocalDateTime.now();
        location.setCreatedAt(now);
        location.setUpdatedAt(now);
        
        Location savedLocation = locationRepository.save(location);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedLocation);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update location", description = "Updates an existing location")
    public ResponseEntity<Location> updateLocation(
            @Parameter(description = "Location ID") @PathVariable Long id,
            @Parameter(description = "Updated location details") @Valid @RequestBody Location locationDetails) {
        log.info("Updating location with ID: {}", id);
        
        return locationRepository.findById(id)
                .map(existingLocation -> {
                    // Update fields
                    existingLocation.setLatitude(locationDetails.getLatitude());
                    existingLocation.setLongitude(locationDetails.getLongitude());
                    existingLocation.setAddress(locationDetails.getAddress());
                    existingLocation.setPostalCode(locationDetails.getPostalCode());
                    existingLocation.setCity(locationDetails.getCity());
                    existingLocation.setState(locationDetails.getState());
                    existingLocation.setCountry(locationDetails.getCountry());
                    existingLocation.setName(locationDetails.getName());
                    existingLocation.setDescription(locationDetails.getDescription());
                    existingLocation.setUpdatedAt(LocalDateTime.now());
                    
                    Location updatedLocation = locationRepository.save(existingLocation);
                    return ResponseEntity.ok(updatedLocation);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete location", description = "Deletes a location")
    public ResponseEntity<Map<String, Boolean>> deleteLocation(
            @Parameter(description = "Location ID") @PathVariable Long id) {
        log.info("Deleting location with ID: {}", id);
        
        return locationRepository.findById(id)
                .map(location -> {
                    locationRepository.delete(location);
                    return ResponseEntity.ok(Map.of("deleted", true));
                })
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + id));
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search locations", description = "Search locations by various criteria")
    public ResponseEntity<List<Location>> searchLocations(
            @Parameter(description = "City name") @RequestParam(required = false) String city,
            @Parameter(description = "State/province") @RequestParam(required = false) String state,
            @Parameter(description = "Country") @RequestParam(required = false) String country) {
        log.info("Searching locations by criteria: city={}, state={}, country={}", city, state, country);
        
        List<Location> locations;
        
        // In a real implementation, this would use more sophisticated search methods
        // For now, we're just doing a simple search based on available repository methods
        if (city != null && !city.isEmpty()) {
            locations = locationRepository.findByCity(city);
        } else if (state != null && !state.isEmpty()) {
            locations = locationRepository.findByState(state);
        } else if (country != null && !country.isEmpty()) {
            locations = locationRepository.findByCountry(country);
        } else {
            locations = locationRepository.findAll();
        }
        
        return ResponseEntity.ok(locations);
    }
    
    @GetMapping("/nearest")
    @Operation(summary = "Find nearest locations", description = "Find the nearest locations to a specific point")
    public ResponseEntity<List<Location>> findNearestLocations(
            @Parameter(description = "Latitude") @RequestParam Double latitude,
            @Parameter(description = "Longitude") @RequestParam Double longitude,
            @Parameter(description = "Maximum number of results") @RequestParam(defaultValue = "10") Integer limit) {
        log.info("Finding {} nearest locations to coordinates: [{}, {}]", limit, latitude, longitude);
        
        Location referenceLocation = Location.builder()
                .latitude(latitude)
                .longitude(longitude)
                .build();
        
        List<Location> nearestLocations = geoService.findNearestLocations(referenceLocation, limit);
        return ResponseEntity.ok(nearestLocations);
    }
    
    @GetMapping("/within-radius")
    @Operation(summary = "Find locations within radius", description = "Find locations within a specified radius")
    public ResponseEntity<List<Location>> findLocationsWithinRadius(
            @Parameter(description = "Latitude") @RequestParam Double latitude,
            @Parameter(description = "Longitude") @RequestParam Double longitude,
            @Parameter(description = "Radius in kilometers") @RequestParam Double radiusKm) {
        log.info("Finding locations within {}km of coordinates: [{}, {}]", radiusKm, latitude, longitude);
        
        Location centerLocation = Location.builder()
                .latitude(latitude)
                .longitude(longitude)
                .build();
        
        List<Location> locationsWithinRadius = geoService.findLocationsWithinRadius(centerLocation, radiusKm);
        return ResponseEntity.ok(locationsWithinRadius);
    }
    
    @GetMapping("/calculate-distance")
    @Operation(summary = "Calculate distance", description = "Calculate distance between two locations")
    public ResponseEntity<Map<String, Object>> calculateDistance(
            @Parameter(description = "First location ID") @RequestParam Long location1Id,
            @Parameter(description = "Second location ID") @RequestParam Long location2Id) {
        log.info("Calculating distance between locations: {} and {}", location1Id, location2Id);
        
        Location location1 = locationRepository.findById(location1Id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + location1Id));
        
        Location location2 = locationRepository.findById(location2Id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + location2Id));
        
        double distance = geoService.calculateDistance(location1, location2);
        
        Map<String, Object> response = Map.of(
                "location1", location1,
                "location2", location2,
                "distanceMeters", distance,
                "distanceKilometers", distance / 1000.0
        );
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/in-zone")
    @Operation(summary = "Find locations in zone", description = "Find locations within a geographical zone (polygon)")
    public ResponseEntity<List<Location>> findLocationsInZone(
            @Parameter(description = "WKT representation of the zone") @RequestParam String zoneWkt) {
        log.info("Finding locations in zone: {}", zoneWkt);
        
        List<Location> locationsInZone = geoService.findLocationsInZone(zoneWkt);
        return ResponseEntity.ok(locationsInZone);
    }
} 