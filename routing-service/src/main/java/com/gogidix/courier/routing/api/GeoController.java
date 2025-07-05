package com.gogidix.courier.routing.api;

import com.gogidix.courier.routing.model.Location;
import com.gogidix.courier.routing.service.geo.GeoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * REST controller for geospatial operations.
 */
@RestController
@RequestMapping("/api/routing/geo")
@RequiredArgsConstructor
@Slf4j
public class GeoController {

    private final GeoService geoService;

    @GetMapping("/locations/radius")
    public ResponseEntity<List<Location>> findLocationsWithinRadius(
            @RequestParam @NotNull Double latitude,
            @RequestParam @NotNull Double longitude,
            @RequestParam @NotNull Double radiusKm) {
        
        log.info("Finding locations within {}km of [{}, {}]", radiusKm, latitude, longitude);
        
        Location centerLocation = Location.builder()
                .latitude(latitude)
                .longitude(longitude)
                .build();
        
        List<Location> locations = geoService.findLocationsWithinRadius(centerLocation, radiusKm);
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/locations/nearest")
    public ResponseEntity<List<Location>> findNearestLocations(
            @RequestParam @NotNull Double latitude,
            @RequestParam @NotNull Double longitude,
            @RequestParam(defaultValue = "10") Integer limit) {
        
        log.info("Finding {} nearest locations to [{}, {}]", limit, latitude, longitude);
        
        Location referenceLocation = Location.builder()
                .latitude(latitude)
                .longitude(longitude)
                .build();
        
        List<Location> locations = geoService.findNearestLocations(referenceLocation, limit);
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/locations/boundary")
    public ResponseEntity<List<Location>> findLocationsWithinBoundary(
            @RequestParam @NotNull Double swLat,
            @RequestParam @NotNull Double swLng,
            @RequestParam @NotNull Double neLat,
            @RequestParam @NotNull Double neLng) {
        
        log.info("Finding locations within boundary: SW=[{}, {}], NE=[{}, {}]", 
                swLat, swLng, neLat, neLng);
        
        Location southWest = Location.builder()
                .latitude(swLat)
                .longitude(swLng)
                .build();
        
        Location northEast = Location.builder()
                .latitude(neLat)
                .longitude(neLng)
                .build();
        
        List<Location> locations = geoService.findLocationsWithinBoundary(southWest, northEast);
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/distance")
    public ResponseEntity<Double> calculateDistance(
            @RequestParam @NotNull Double lat1,
            @RequestParam @NotNull Double lng1,
            @RequestParam @NotNull Double lat2,
            @RequestParam @NotNull Double lng2) {
        
        log.info("Calculating distance between [{}, {}] and [{}, {}]", lat1, lng1, lat2, lng2);
        
        Location location1 = Location.builder()
                .latitude(lat1)
                .longitude(lng1)
                .build();
        
        Location location2 = Location.builder()
                .latitude(lat2)
                .longitude(lng2)
                .build();
        
        double distance = geoService.calculateDistance(location1, location2);
        return ResponseEntity.ok(distance);
    }

    @GetMapping("/couriers/nearest")
    public ResponseEntity<List<String>> findNearestCouriers(
            @RequestParam @NotNull Double latitude,
            @RequestParam @NotNull Double longitude,
            @RequestParam(defaultValue = "10.0") Double radiusKm,
            @RequestParam(defaultValue = "5") Integer limit) {
        
        log.info("Finding {} nearest couriers within {}km of [{}, {}]", 
                limit, radiusKm, latitude, longitude);
        
        Location deliveryLocation = Location.builder()
                .latitude(latitude)
                .longitude(longitude)
                .build();
        
        List<String> courierIds = geoService.findNearestCouriers(deliveryLocation, radiusKm, limit);
        return ResponseEntity.ok(courierIds);
    }

    @PostMapping("/zones/create")
    public ResponseEntity<List<String>> createDeliveryZones(
            @RequestBody @Valid CreateDeliveryZonesRequest request) {
        
        log.info("Creating {} delivery zones with max radius {}km around [{}, {}]", 
                request.getNumberOfZones(), request.getMaxRadiusKm(), 
                request.getLatitude(), request.getLongitude());
        
        Location centerLocation = Location.builder()
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .build();
        
        List<String> zones = geoService.createDeliveryZones(
                centerLocation, 
                request.getMaxRadiusKm(), 
                request.getNumberOfZones()
        );
        
        return ResponseEntity.ok(zones);
    }
}
