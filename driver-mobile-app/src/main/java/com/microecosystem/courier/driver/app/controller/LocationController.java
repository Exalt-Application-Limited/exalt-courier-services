package com.microecosystem.courier.driver.app.controller;

import com.microecosystem.courier.driver.app.dto.LocationUpdateRequest;
import com.microecosystem.courier.driver.app.model.Driver;
import com.microecosystem.courier.driver.app.security.SecurityService;
import com.microecosystem.courier.driver.app.service.LocationTrackingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * REST controller for location tracking operations.
 */
@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Location Tracking", description = "APIs for location tracking")
public class LocationController {

    private final LocationTrackingService locationTrackingService;
    private final SecurityService securityService;

    @PatchMapping("/update")
    @Operation(summary = "Update driver location", description = "Updates the current driver's location")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<Driver> updateLocation(@Valid @RequestBody LocationUpdateRequest request) {
        log.info("REST request to update driver location");
        
        Long driverId = securityService.getCurrentDriverId();
        if (driverId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        Driver driver = locationTrackingService.updateDriverLocation(driverId, request);
        return ResponseEntity.ok(driver);
    }

    @GetMapping("/nearby")
    @Operation(summary = "Find nearby drivers", description = "Finds drivers near a specific location")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<Driver>> findNearbyDrivers(
            @Parameter(description = "Latitude", required = true)
            @RequestParam BigDecimal latitude,
            @Parameter(description = "Longitude", required = true)
            @RequestParam BigDecimal longitude,
            @Parameter(description = "Radius in kilometers", required = true)
            @RequestParam Double radiusInKm,
            @Parameter(description = "Driver status (optional)")
            @RequestParam(required = false) String status) {
        log.info("REST request to find drivers near lat: {}, lon: {} within {}km", latitude, longitude, radiusInKm);
        
        List<Driver> drivers = locationTrackingService.findNearbyDrivers(latitude, longitude, radiusInKm, status);
        return ResponseEntity.ok(drivers);
    }

    @GetMapping("/distance")
    @Operation(summary = "Calculate distance", description = "Calculates distance between two points")
    @PreAuthorize("hasRole('DRIVER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Map<String, Object>> calculateDistance(
            @Parameter(description = "First latitude", required = true)
            @RequestParam BigDecimal lat1,
            @Parameter(description = "First longitude", required = true)
            @RequestParam BigDecimal lon1,
            @Parameter(description = "Second latitude", required = true)
            @RequestParam BigDecimal lat2,
            @Parameter(description = "Second longitude", required = true)
            @RequestParam BigDecimal lon2,
            @Parameter(description = "Average speed in km/h (optional)")
            @RequestParam(required = false) Double averageSpeed) {
        log.info("REST request to calculate distance between ({}, {}) and ({}, {})", lat1, lon1, lat2, lon2);
        
        double distanceKm = locationTrackingService.calculateDistance(lat1, lon1, lat2, lon2);
        
        if (averageSpeed != null && averageSpeed > 0) {
            double etaMinutes = locationTrackingService.getEstimatedTimeOfArrival(lat1, lon1, lat2, lon2, averageSpeed);
            return ResponseEntity.ok(Map.of(
                    "distanceKm", distanceKm,
                    "etaMinutes", etaMinutes
            ));
        } else {
            return ResponseEntity.ok(Map.of("distanceKm", distanceKm));
        }
    }
} 