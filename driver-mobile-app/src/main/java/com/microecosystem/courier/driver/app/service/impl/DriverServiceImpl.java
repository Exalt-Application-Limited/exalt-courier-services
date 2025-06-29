package com.microecosystem.courier.driver.app.service.impl;

import com.microecosystem.courier.driver.app.dto.DriverDto;
import com.microecosystem.courier.driver.app.dto.LocationUpdateRequest;
import com.microecosystem.courier.driver.app.exception.ResourceNotFoundException;
import com.microecosystem.courier.driver.app.model.Driver;
import com.microecosystem.courier.driver.app.model.DriverStatus;
import com.microecosystem.courier.driver.app.repository.DriverRepository;
import com.microecosystem.courier.driver.app.service.DriverService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the DriverService interface.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;

    @Override
    @Transactional
    public Driver createDriver(DriverDto driverDto) {
        log.info("Creating new driver with email: {}", driverDto.getEmail());
        
        Driver driver = new Driver();
        BeanUtils.copyProperties(driverDto, driver);
        
        // Set default values
        if (driver.getStatus() == null) {
            driver.setStatus(DriverStatus.OFFLINE);
        }
        if (driver.getIsActive() == null) {
            driver.setIsActive(true);
        }
        if (driver.getIsVerified() == null) {
            driver.setIsVerified(false);
        }
        if (driver.getTotalDeliveries() == null) {
            driver.setTotalDeliveries(0);
        }
        if (driver.getCompletedDeliveries() == null) {
            driver.setCompletedDeliveries(0);
        }
        if (driver.getCanceledDeliveries() == null) {
            driver.setCanceledDeliveries(0);
        }
        if (driver.getAverageRating() == null) {
            driver.setAverageRating(BigDecimal.ZERO);
        }
        if (driver.getTotalRatings() == null) {
            driver.setTotalRatings(0);
        }
        
        return driverRepository.save(driver);
    }

    @Override
    @Transactional
    public Driver updateDriver(Long id, DriverDto driverDto) {
        log.info("Updating driver with ID: {}", id);
        
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + id));
        
        // Update fields but preserve some that shouldn't be directly updated
        String email = driver.getEmail();
        String phoneNumber = driver.getPhoneNumber();
        LocalDateTime createdAt = driver.getCreatedAt();
        Integer totalDeliveries = driver.getTotalDeliveries();
        Integer completedDeliveries = driver.getCompletedDeliveries();
        Integer canceledDeliveries = driver.getCanceledDeliveries();
        BigDecimal averageRating = driver.getAverageRating();
        Integer totalRatings = driver.getTotalRatings();
        
        BeanUtils.copyProperties(driverDto, driver);
        
        // Restore preserved fields
        driver.setId(id);
        driver.setEmail(email);
        driver.setPhoneNumber(phoneNumber);
        driver.setCreatedAt(createdAt);
        driver.setTotalDeliveries(totalDeliveries);
        driver.setCompletedDeliveries(completedDeliveries);
        driver.setCanceledDeliveries(canceledDeliveries);
        driver.setAverageRating(averageRating);
        driver.setTotalRatings(totalRatings);
        
        return driverRepository.save(driver);
    }

    @Override
    public Optional<Driver> getDriverById(Long id) {
        log.info("Fetching driver with ID: {}", id);
        return driverRepository.findById(id);
    }

    @Override
    public Optional<Driver> getDriverByUserId(Long userId) {
        log.info("Fetching driver with user ID: {}", userId);
        return driverRepository.findByUserId(userId);
    }

    @Override
    public Optional<Driver> getDriverByEmail(String email) {
        log.info("Fetching driver with email: {}", email);
        return driverRepository.findByEmail(email);
    }

    @Override
    public Page<Driver> getAllDrivers(Pageable pageable) {
        log.info("Fetching all drivers with pagination");
        return driverRepository.findAll(pageable);
    }

    @Override
    public Page<Driver> getDriversByStatus(DriverStatus status, Pageable pageable) {
        log.info("Fetching drivers with status: {}", status);
        return driverRepository.findByStatus(status, pageable);
    }

    @Override
    @Transactional
    public Driver updateDriverStatus(Long id, DriverStatus status) {
        log.info("Updating status to {} for driver with ID: {}", status, id);
        
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + id));
        
        driver.setStatus(status);
        return driverRepository.save(driver);
    }

    @Override
    @Transactional
    public Driver updateDriverLocation(Long id, LocationUpdateRequest locationRequest) {
        log.info("Updating location for driver with ID: {}", id);
        
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + id));
        
        driver.setCurrentLatitude(locationRequest.getLatitude());
        driver.setCurrentLongitude(locationRequest.getLongitude());
        driver.setLastLocationUpdate(LocalDateTime.now());
        
        return driverRepository.save(driver);
    }

    @Override
    public List<Driver> findNearbyDrivers(BigDecimal latitude, BigDecimal longitude, 
                                         Double radiusInKm, DriverStatus status) {
        log.info("Finding drivers near lat: {}, lon: {} within {}km with status: {}", 
                latitude, longitude, radiusInKm, status);
        
        return driverRepository.findNearbyDrivers(latitude, longitude, radiusInKm, status);
    }

    @Override
    public Page<Driver> searchDriversByName(String query, Pageable pageable) {
        log.info("Searching drivers by name: {}", query);
        return driverRepository.searchByName(query, query, pageable);
    }

    @Override
    @Transactional
    public void deleteDriver(Long id) {
        log.info("Deleting driver with ID: {}", id);
        
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + id));
        
        driverRepository.delete(driver);
    }

    @Override
    @Transactional
    public Driver updateDeviceToken(Long id, String deviceToken) {
        log.info("Updating device token for driver with ID: {}", id);
        
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + id));
        
        driver.setDeviceToken(deviceToken);
        return driverRepository.save(driver);
    }
} 