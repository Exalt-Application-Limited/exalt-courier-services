package com.exalt.courier.drivermobileapp.repository;

import com.socialecommerceecosystem.drivermobileapp.model.AccountStatus;
import com.socialecommerceecosystem.drivermobileapp.model.CourierProfile;
import com.socialecommerceecosystem.drivermobileapp.model.OnboardingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for CourierProfile entity.
 */
@Repository
public interface CourierProfileRepository extends MongoRepository<CourierProfile, String> {
    
    /**
     * Find courier profile by courier ID.
     *
     * @param courierId the courier ID
     * @return the courier profile
     */
    Optional<CourierProfile> findByCourierId(String courierId);
    
    /**
     * Find courier profile by username.
     *
     * @param username the username
     * @return the courier profile
     */
    Optional<CourierProfile> findByUsername(String username);
    
    /**
     * Find courier profile by phone number.
     *
     * @param phoneNumber the phone number
     * @return the courier profile
     */
    Optional<CourierProfile> findByPhoneNumber(String phoneNumber);
    
    /**
     * Find courier profile by email.
     *
     * @param email the email address
     * @return the courier profile
     */
    Optional<CourierProfile> findByEmail(String email);
    
    /**
     * Find all courier profiles with the specified account status.
     *
     * @param accountStatus the account status
     * @param pageable pagination information
     * @return page of courier profiles
     */
    Page<CourierProfile> findByAccountStatus(AccountStatus accountStatus, Pageable pageable);
    
    /**
     * Find all courier profiles with the specified onboarding status.
     *
     * @param onboardingStatus the onboarding status
     * @param pageable pagination information
     * @return page of courier profiles
     */
    Page<CourierProfile> findByOnboardingStatus(OnboardingStatus onboardingStatus, Pageable pageable);
    
    /**
     * Find all online couriers.
     *
     * @return list of online courier profiles
     */
    List<CourierProfile> findByIsOnlineTrue();
    
    /**
     * Find all couriers within a specific delivery zone.
     *
     * @param zoneId the delivery zone ID
     * @return list of courier profiles
     */
    @Query("{'preferredDeliveryZones': ?0}")
    List<CourierProfile> findByPreferredDeliveryZone(String zoneId);
    
    /**
     * Find all couriers with a specific delivery skill.
     *
     * @param skill the delivery skill
     * @return list of courier profiles
     */
    @Query("{'deliverySkills': ?0}")
    List<CourierProfile> findByDeliverySkill(String skill);
    
    /**
     * Find all couriers with a rating above the specified threshold.
     *
     * @param minRating the minimum rating threshold
     * @return list of courier profiles
     */
    List<CourierProfile> findByAverageRatingGreaterThanEqual(Double minRating);
    
    /**
     * Find all active couriers by vehicle type.
     *
     * @param vehicleType the vehicle type
     * @return list of courier profiles
     */
    @Query("{'vehicleInfo.type': ?0, 'accountStatus': 'ACTIVE'}")
    List<CourierProfile> findActiveByVehicleType(String vehicleType);
    
    /**
     * Find all couriers whose FCM token needs updating.
     *
     * @param oldVersion the old app version
     * @return list of courier profiles
     */
    List<CourierProfile> findByAppVersionNot(String oldVersion);
} 