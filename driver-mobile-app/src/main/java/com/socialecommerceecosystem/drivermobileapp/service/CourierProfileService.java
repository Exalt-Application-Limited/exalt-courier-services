package com.gogidix.courier.drivermobileapp.service;

import com.socialecommerceecosystem.drivermobileapp.model.AccountStatus;
import com.socialecommerceecosystem.drivermobileapp.model.CourierProfile;
import com.socialecommerceecosystem.drivermobileapp.model.LocationData;
import com.socialecommerceecosystem.drivermobileapp.model.OnboardingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service interface for managing courier profiles.
 */
public interface CourierProfileService {
    
    /**
     * Create a new courier profile.
     *
     * @param courierProfile the courier profile to create
     * @return the created courier profile
     */
    CourierProfile createCourierProfile(CourierProfile courierProfile);
    
    /**
     * Get a courier profile by ID.
     *
     * @param id the profile ID
     * @return the courier profile
     */
    Optional<CourierProfile> getCourierProfileById(String id);
    
    /**
     * Get a courier profile by courier ID.
     *
     * @param courierId the courier ID
     * @return the courier profile
     */
    Optional<CourierProfile> getCourierProfileByCourierId(String courierId);
    
    /**
     * Get a courier profile by username.
     *
     * @param username the username
     * @return the courier profile
     */
    Optional<CourierProfile> getCourierProfileByUsername(String username);
    
    /**
     * Update a courier profile.
     *
     * @param id the profile ID
     * @param courierProfile the updated profile details
     * @return the updated courier profile
     */
    CourierProfile updateCourierProfile(String id, CourierProfile courierProfile);
    
    /**
     * Delete a courier profile.
     *
     * @param id the profile ID
     * @return true if deleted successfully
     */
    boolean deleteCourierProfile(String id);
    
    /**
     * Update courier's online status.
     *
     * @param courierId the courier ID
     * @param isOnline the online status
     * @return the updated courier profile
     */
    CourierProfile updateOnlineStatus(String courierId, boolean isOnline);
    
    /**
     * Update courier's current location.
     *
     * @param courierId the courier ID
     * @param locationData the location data
     * @return the updated courier profile
     */
    CourierProfile updateLocation(String courierId, LocationData locationData);
    
    /**
     * Update courier's Firebase Cloud Messaging token.
     *
     * @param courierId the courier ID
     * @param fcmToken the FCM token
     * @return the updated courier profile
     */
    CourierProfile updateFcmToken(String courierId, String fcmToken);
    
    /**
     * Update courier's account status.
     *
     * @param courierId the courier ID
     * @param accountStatus the account status
     * @return the updated courier profile
     */
    CourierProfile updateAccountStatus(String courierId, AccountStatus accountStatus);
    
    /**
     * Update courier's onboarding status.
     *
     * @param courierId the courier ID
     * @param onboardingStatus the onboarding status
     * @return the updated courier profile
     */
    CourierProfile updateOnboardingStatus(String courierId, OnboardingStatus onboardingStatus);
    
    /**
     * Add a preferred delivery zone for a courier.
     *
     * @param courierId the courier ID
     * @param zoneId the delivery zone ID
     * @return the updated courier profile
     */
    CourierProfile addPreferredDeliveryZone(String courierId, String zoneId);
    
    /**
     * Remove a preferred delivery zone for a courier.
     *
     * @param courierId the courier ID
     * @param zoneId the delivery zone ID
     * @return the updated courier profile
     */
    CourierProfile removePreferredDeliveryZone(String courierId, String zoneId);
    
    /**
     * Add a delivery skill for a courier.
     *
     * @param courierId the courier ID
     * @param skill the delivery skill
     * @return the updated courier profile
     */
    CourierProfile addDeliverySkill(String courierId, String skill);
    
    /**
     * Remove a delivery skill for a courier.
     *
     * @param courierId the courier ID
     * @param skill the delivery skill
     * @return the updated courier profile
     */
    CourierProfile removeDeliverySkill(String courierId, String skill);
    
    /**
     * Update courier's last login time.
     *
     * @param courierId the courier ID
     * @return the updated courier profile
     */
    CourierProfile updateLastLoginTime(String courierId);
    
    /**
     * Update courier's device info and app version.
     *
     * @param courierId the courier ID
     * @param deviceInfo the device info
     * @param appVersion the app version
     * @return the updated courier profile
     */
    CourierProfile updateDeviceInfo(String courierId, String deviceInfo, String appVersion);
    
    /**
     * Get all courier profiles with a specific account status.
     *
     * @param accountStatus the account status
     * @param pageable the pagination information
     * @return page of courier profiles
     */
    Page<CourierProfile> getCourierProfilesByAccountStatus(AccountStatus accountStatus, Pageable pageable);
    
    /**
     * Get all courier profiles with a specific onboarding status.
     *
     * @param onboardingStatus the onboarding status
     * @param pageable the pagination information
     * @return page of courier profiles
     */
    Page<CourierProfile> getCourierProfilesByOnboardingStatus(OnboardingStatus onboardingStatus, Pageable pageable);
    
    /**
     * Get all online couriers.
     *
     * @return list of online courier profiles
     */
    List<CourierProfile> getOnlineCouriers();
    
    /**
     * Get all couriers within a specific delivery zone.
     *
     * @param zoneId the delivery zone ID
     * @return list of courier profiles
     */
    List<CourierProfile> getCouriersByDeliveryZone(String zoneId);
    
    /**
     * Get all couriers with a specific delivery skill.
     *
     * @param skill the delivery skill
     * @return list of courier profiles
     */
    List<CourierProfile> getCouriersByDeliverySkill(String skill);
    
    /**
     * Get all couriers with a rating above the specified threshold.
     *
     * @param minRating the minimum rating threshold
     * @return list of courier profiles
     */
    List<CourierProfile> getCouriersByMinimumRating(Double minRating);
    
    /**
     * Get all active couriers by vehicle type.
     *
     * @param vehicleType the vehicle type
     * @return list of courier profiles
     */
    List<CourierProfile> getActiveCouriersByVehicleType(String vehicleType);
    
    /**
     * Update courier's performance metrics.
     *
     * @param courierId the courier ID
     * @param totalDeliveries the total deliveries
     * @param averageRating the average rating
     * @return the updated courier profile
     */
    CourierProfile updatePerformanceMetrics(String courierId, Integer totalDeliveries, Double averageRating);
    
    /**
     * Get couriers statistics grouped by status.
     *
     * @return map of status to count
     */
    Map<String, Long> getCourierStatistics();
} 