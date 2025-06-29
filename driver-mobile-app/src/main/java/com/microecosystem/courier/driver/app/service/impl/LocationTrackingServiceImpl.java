package com.microecosystem.courier.driver.app.service.impl;

import com.microecosystem.courier.driver.app.dto.LocationUpdateRequest;
import com.microecosystem.courier.driver.app.exception.ResourceNotFoundException;
import com.microecosystem.courier.driver.app.model.Driver;
import com.microecosystem.courier.driver.app.model.DriverStatus;
import com.microecosystem.courier.driver.app.repository.DriverRepository;
import com.microecosystem.courier.driver.app.service.LocationTrackingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of the LocationTrackingService interface.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LocationTrackingServiceImpl implements LocationTrackingService {

    private final DriverRepository driverRepository;
    
    @Value("${app.location.accuracy-threshold:50}")
    private int accuracyThreshold;
    
    private static final double EARTH_RADIUS_KM = 6371.0;

    @Override
    @Transactional
    public Driver updateDriverLocation(Long driverId, LocationUpdateRequest request) {
        log.info("Updating location for driver with ID: {}", driverId);
        
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + driverId));
        
        // Skip update if accuracy is worse than threshold (if provided)
        if (request.getAccuracyInMeters() != null && request.getAccuracyInMeters() > accuracyThreshold) {
            log.info("Skipping location update for driver ID: {} due to poor accuracy: {} meters", 
                    driverId, request.getAccuracyInMeters());
            return driver;
        }
        
        driver.setCurrentLatitude(request.getLatitude());
        driver.setCurrentLongitude(request.getLongitude());
        driver.setLastLocationUpdate(LocalDateTime.now());
        
        return driverRepository.save(driver);
    }

    @Override
    public List<Driver> findNearbyDrivers(BigDecimal latitude, BigDecimal longitude, Double radiusInKm, String status) {
        log.info("Finding drivers near lat: {}, lon: {} within {}km with status: {}", 
                latitude, longitude, radiusInKm, status);
        
        DriverStatus driverStatus = null;
        if (status != null && !status.isEmpty()) {
            try {
                driverStatus = DriverStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid driver status: {}", status);
            }
        }
        
        return driverRepository.findNearbyDrivers(latitude, longitude, radiusInKm, driverStatus);
    }

    @Override
    public double calculateDistance(BigDecimal lat1, BigDecimal lon1, BigDecimal lat2, BigDecimal lon2) {
        double lat1Rad = Math.toRadians(lat1.doubleValue());
        double lon1Rad = Math.toRadians(lon1.doubleValue());
        double lat2Rad = Math.toRadians(lat2.doubleValue());
        double lon2Rad = Math.toRadians(lon2.doubleValue());
        
        double dLat = lat2Rad - lat1Rad;
        double dLon = lon2Rad - lon1Rad;
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS_KM * c;
    }

    @Override
    public double getEstimatedTimeOfArrival(BigDecimal lat1, BigDecimal lon1, BigDecimal lat2, BigDecimal lon2, double averageSpeedKmh) {
        // Calculate distance in kilometers
        double distanceKm = calculateDistance(lat1, lon1, lat2, lon2);
        
        // Calculate time in hours
        double timeHours = distanceKm / averageSpeedKmh;
        
        // Convert to minutes
        return timeHours * 60;
    }
} 