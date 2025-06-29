package com.exalt.courier.drivermobileapp.service;

import java.util.Optional;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import com.socialecommerceecosystem.drivermobileapp.exception.ResourceNotFoundException;
import com.socialecommerceecosystem.drivermobileapp.model.AccountStatus;
import com.socialecommerceecosystem.drivermobileapp.model.CourierProfile;
import com.socialecommerceecosystem.drivermobileapp.model.LocationData;
import com.socialecommerceecosystem.drivermobileapp.model.OnboardingStatus;
import com.socialecommerceecosystem.drivermobileapp.repository.CourierProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the CourierProfileService interface.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CourierProfileServiceImpl implements CourierProfileService {
    
    private final CourierProfileRepository courierProfileRepository;
    
    @Override
    public CourierProfile createCourierProfile(CourierProfile courierProfile) {
        log.info("Creating new courier profile for courierId: {}", courierProfile.getCourierId());
        
        // Set default values
        if (courierProfile.getIsOnline() == null) {
            courierProfile.setIsOnline(false);
        }
        
        if (courierProfile.getOnboardingStatus() == null) {
            courierProfile.setOnboardingStatus(OnboardingStatus.REGISTERED);
        }
        
        if (courierProfile.getAccountStatus() == null) {
            courierProfile.setAccountStatus(AccountStatus.INACTIVE);
        }
        
        if (courierProfile.getTotalDeliveries() == null) {
            courierProfile.setTotalDeliveries(0);
        }
        
        if (courierProfile.getAverageRating() == null) {
            courierProfile.setAverageRating(0.0);
        }
        
        courierProfile.setCreatedAt(LocalDateTime.now());
        courierProfile.setUpdatedAt(LocalDateTime.now());
        
        return courierProfileRepository.save(courierProfile);
    }
    
    @Override
    public Optional<CourierProfile> getCourierProfileById(String id) {
        log.info("Fetching courier profile by ID: {}", id);
        return courierProfileRepository.findById(id);
    }
    
    @Override
    public Optional<CourierProfile> getCourierProfileByCourierId(String courierId) {
        log.info("Fetching courier profile by courier ID: {}", courierId);
        return courierProfileRepository.findByCourierId(courierId);
    }
    
    @Override
    public Optional<CourierProfile> getCourierProfileByUsername(String username) {
        log.info("Fetching courier profile by username: {}", username);
        return courierProfileRepository.findByUsername(username);
    }
    
    @Override
    public CourierProfile updateCourierProfile(String id, CourierProfile courierProfile) {
        log.info("Updating courier profile with ID: {}", id);
        
        return courierProfileRepository.findById(id)
                .map(existingProfile -> {
                    // Update basic profile information
                    existingProfile.setFirstName(courierProfile.getFirstName());
                    existingProfile.setLastName(courierProfile.getLastName());
                    existingProfile.setPhoneNumber(courierProfile.getPhoneNumber());
                    existingProfile.setEmail(courierProfile.getEmail());
                    existingProfile.setProfileImageUrl(courierProfile.getProfileImageUrl());
                    
                    // Update vehicle information if provided
                    if (courierProfile.getVehicleInfo() != null) {
                        existingProfile.setVehicleInfo(courierProfile.getVehicleInfo());
                    }
                    
                    // Update location if provided
                    if (courierProfile.getCurrentLocation() != null) {
                        existingProfile.setCurrentLocation(courierProfile.getCurrentLocation());
                    }
                    
                    // Update preferred delivery zones if provided
                    if (courierProfile.getPreferredDeliveryZones() != null && !courierProfile.getPreferredDeliveryZones().isEmpty()) {
                        existingProfile.setPreferredDeliveryZones(courierProfile.getPreferredDeliveryZones());
                    }
                    
                    // Update delivery skills if provided
                    if (courierProfile.getDeliverySkills() != null && !courierProfile.getDeliverySkills().isEmpty()) {
                        existingProfile.setDeliverySkills(courierProfile.getDeliverySkills());
                    }
                    
                    // Update language preference if provided
                    if (courierProfile.getLanguagePreference() != null) {
                        existingProfile.setLanguagePreference(courierProfile.getLanguagePreference());
                    }
                    
                    // Update timestamps
                    existingProfile.setUpdatedAt(LocalDateTime.now());
                    
                    return courierProfileRepository.save(existingProfile);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Courier profile not found with id " + id));
    }
    
    @Override
    public boolean deleteCourierProfile(String id) {
        log.info("Deleting courier profile with ID: {}", id);
        
        return courierProfileRepository.findById(id)
                .map(profile -> {
                    courierProfileRepository.delete(profile);
                    return true;
                })
                .orElse(false);
    }
    
    @Override
    public CourierProfile updateOnlineStatus(String courierId, boolean isOnline) {
        log.info("Updating online status to {} for courier ID: {}", isOnline, courierId);
        
        return getCourierProfileByCourierId(courierId)
                .map(profile -> {
                    profile.setIsOnline(isOnline);
                    profile.setUpdatedAt(LocalDateTime.now());
                    return courierProfileRepository.save(profile);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Courier profile not found with courier ID " + courierId));
    }
    
    @Override
    public CourierProfile updateLocation(String courierId, LocationData locationData) {
        log.info("Updating location for courier ID: {}", courierId);
        
        return getCourierProfileByCourierId(courierId)
                .map(profile -> {
                    locationData.setTimestamp(LocalDateTime.now());
                    profile.setCurrentLocation(locationData);
                    profile.setUpdatedAt(LocalDateTime.now());
                    return courierProfileRepository.save(profile);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Courier profile not found with courier ID " + courierId));
    }
    
    @Override
    public CourierProfile updateFcmToken(String courierId, String fcmToken) {
        log.info("Updating FCM token for courier ID: {}", courierId);
        
        return getCourierProfileByCourierId(courierId)
                .map(profile -> {
                    profile.setFcmToken(fcmToken);
                    profile.setUpdatedAt(LocalDateTime.now());
                    return courierProfileRepository.save(profile);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Courier profile not found with courier ID " + courierId));
    }
    
    @Override
    public CourierProfile updateAccountStatus(String courierId, AccountStatus accountStatus) {
        log.info("Updating account status to {} for courier ID: {}", accountStatus, courierId);
        
        return getCourierProfileByCourierId(courierId)
                .map(profile -> {
                    profile.setAccountStatus(accountStatus);
                    profile.setUpdatedAt(LocalDateTime.now());
                    return courierProfileRepository.save(profile);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Courier profile not found with courier ID " + courierId));
    }
    
    @Override
    public CourierProfile updateOnboardingStatus(String courierId, OnboardingStatus onboardingStatus) {
        log.info("Updating onboarding status to {} for courier ID: {}", onboardingStatus, courierId);
        
        return getCourierProfileByCourierId(courierId)
                .map(profile -> {
                    profile.setOnboardingStatus(onboardingStatus);
                    profile.setUpdatedAt(LocalDateTime.now());
                    
                    // If onboarding is completed, update account status to ACTIVE
                    if (onboardingStatus == OnboardingStatus.ACTIVATED) {
                        profile.setAccountStatus(AccountStatus.ACTIVE);
                    }
                    
                    return courierProfileRepository.save(profile);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Courier profile not found with courier ID " + courierId));
    }
    
    @Override
    public CourierProfile addPreferredDeliveryZone(String courierId, String zoneId) {
        log.info("Adding preferred delivery zone {} for courier ID: {}", zoneId, courierId);
        
        return getCourierProfileByCourierId(courierId)
                .map(profile -> {
                    profile.getPreferredDeliveryZones().add(zoneId);
                    profile.setUpdatedAt(LocalDateTime.now());
                    return courierProfileRepository.save(profile);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Courier profile not found with courier ID " + courierId));
    }
    
    @Override
    public CourierProfile removePreferredDeliveryZone(String courierId, String zoneId) {
        log.info("Removing preferred delivery zone {} for courier ID: {}", zoneId, courierId);
        
        return getCourierProfileByCourierId(courierId)
                .map(profile -> {
                    profile.getPreferredDeliveryZones().remove(zoneId);
                    profile.setUpdatedAt(LocalDateTime.now());
                    return courierProfileRepository.save(profile);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Courier profile not found with courier ID " + courierId));
    }
    
    @Override
    public CourierProfile addDeliverySkill(String courierId, String skill) {
        log.info("Adding delivery skill {} for courier ID: {}", skill, courierId);
        
        return getCourierProfileByCourierId(courierId)
                .map(profile -> {
                    profile.getDeliverySkills().add(skill);
                    profile.setUpdatedAt(LocalDateTime.now());
                    return courierProfileRepository.save(profile);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Courier profile not found with courier ID " + courierId));
    }
    
    @Override
    public CourierProfile removeDeliverySkill(String courierId, String skill) {
        log.info("Removing delivery skill {} for courier ID: {}", skill, courierId);
        
        return getCourierProfileByCourierId(courierId)
                .map(profile -> {
                    profile.getDeliverySkills().remove(skill);
                    profile.setUpdatedAt(LocalDateTime.now());
                    return courierProfileRepository.save(profile);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Courier profile not found with courier ID " + courierId));
    }
    
    @Override
    public CourierProfile updateLastLoginTime(String courierId) {
        log.info("Updating last login time for courier ID: {}", courierId);
        
        return getCourierProfileByCourierId(courierId)
                .map(profile -> {
                    profile.setLastLoginAt(LocalDateTime.now());
                    profile.setUpdatedAt(LocalDateTime.now());
                    return courierProfileRepository.save(profile);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Courier profile not found with courier ID " + courierId));
    }
    
    @Override
    public CourierProfile updateDeviceInfo(String courierId, String deviceInfo, String appVersion) {
        log.info("Updating device info and app version for courier ID: {}", courierId);
        
        return getCourierProfileByCourierId(courierId)
                .map(profile -> {
                    profile.setDeviceInfo(deviceInfo);
                    profile.setAppVersion(appVersion);
                    profile.setUpdatedAt(LocalDateTime.now());
                    return courierProfileRepository.save(profile);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Courier profile not found with courier ID " + courierId));
    }
    
    @Override
    public Page<CourierProfile> getCourierProfilesByAccountStatus(AccountStatus accountStatus, Pageable pageable) {
        log.info("Fetching courier profiles with account status: {}", accountStatus);
        return courierProfileRepository.findByAccountStatus(accountStatus, pageable);
    }
    
    @Override
    public Page<CourierProfile> getCourierProfilesByOnboardingStatus(OnboardingStatus onboardingStatus, Pageable pageable) {
        log.info("Fetching courier profiles with onboarding status: {}", onboardingStatus);
        return courierProfileRepository.findByOnboardingStatus(onboardingStatus, pageable);
    }
    
    @Override
    public List<CourierProfile> getOnlineCouriers() {
        log.info("Fetching all online couriers");
        return courierProfileRepository.findByIsOnlineTrue();
    }
    
    @Override
    public List<CourierProfile> getCouriersByDeliveryZone(String zoneId) {
        log.info("Fetching couriers by delivery zone: {}", zoneId);
        return courierProfileRepository.findByPreferredDeliveryZone(zoneId);
    }
    
    @Override
    public List<CourierProfile> getCouriersByDeliverySkill(String skill) {
        log.info("Fetching couriers by delivery skill: {}", skill);
        return courierProfileRepository.findByDeliverySkill(skill);
    }
    
    @Override
    public List<CourierProfile> getCouriersByMinimumRating(Double minRating) {
        log.info("Fetching couriers with minimum rating: {}", minRating);
        return courierProfileRepository.findByAverageRatingGreaterThanEqual(minRating);
    }
    
    @Override
    public List<CourierProfile> getActiveCouriersByVehicleType(String vehicleType) {
        log.info("Fetching active couriers by vehicle type: {}", vehicleType);
        return courierProfileRepository.findActiveByVehicleType(vehicleType);
    }
    
    @Override
    public CourierProfile updatePerformanceMetrics(String courierId, Integer totalDeliveries, Double averageRating) {
        log.info("Updating performance metrics for courier ID: {}", courierId);
        
        return getCourierProfileByCourierId(courierId)
                .map(profile -> {
                    profile.setTotalDeliveries(totalDeliveries);
                    profile.setAverageRating(averageRating);
                    profile.setUpdatedAt(LocalDateTime.now());
                    return courierProfileRepository.save(profile);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Courier profile not found with courier ID " + courierId));
    }
    
    @Override
    public Map<String, Long> getCourierStatistics() {
        log.info("Generating courier statistics");
        
        Map<String, Long> statistics = new HashMap<>();
        
        // Get count by account status
        for (AccountStatus status : AccountStatus.values()) {
            long count = courierProfileRepository.findByAccountStatus(status, Pageable.unpaged()).getTotalElements();
            statistics.put("ACCOUNT_" + status.name(), count);
        }
        
        // Get count by onboarding status
        for (OnboardingStatus status : OnboardingStatus.values()) {
            long count = courierProfileRepository.findByOnboardingStatus(status, Pageable.unpaged()).getTotalElements();
            statistics.put("ONBOARDING_" + status.name(), count);
        }
        
        // Get online couriers count
        long onlineCount = courierProfileRepository.findByIsOnlineTrue().size();
        statistics.put("ONLINE", onlineCount);
        
        // Get total count
        long totalCount = courierProfileRepository.count();
        statistics.put("TOTAL", totalCount);
        
        return statistics;
    }
} 
