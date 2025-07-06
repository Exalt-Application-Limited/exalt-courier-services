package com.gogidix.courier.courier.drivermobileapp.controller;

import com.socialecommerceecosystem.drivermobileapp.exception.ResourceNotFoundException;
import com.socialecommerceecosystem.drivermobileapp.model.AccountStatus;
import com.socialecommerceecosystem.drivermobileapp.model.CourierProfile;
import com.socialecommerceecosystem.drivermobileapp.model.LocationData;
import com.socialecommerceecosystem.drivermobileapp.model.OnboardingStatus;
import com.socialecommerceecosystem.drivermobileapp.service.CourierProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * REST controller for CourierProfile operations.
 */
@RestController
@RequestMapping("/api/v1/courier-profiles")
@RequiredArgsConstructor
@Slf4j
public class CourierProfileController {
    
    private final CourierProfileService courierProfileService;
    
    /**
     * Create a new courier profile.
     *
     * @param courierProfile the courier profile to create
     * @return the created courier profile
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CourierProfile createCourierProfile(@Valid @RequestBody CourierProfile courierProfile) {
        log.info("REST request to create a new courier profile");
        return courierProfileService.createCourierProfile(courierProfile);
    }
    
    /**
     * Get a courier profile by ID.
     *
     * @param id the profile ID
     * @return the courier profile
     */
    @GetMapping("/{id}")
    public ResponseEntity<CourierProfile> getCourierProfile(@PathVariable String id) {
        log.info("REST request to get courier profile by ID: {}", id);
        return courierProfileService.getCourierProfileById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get a courier profile by courier ID.
     *
     * @param courierId the courier ID
     * @return the courier profile
     */
    @GetMapping("/by-courier-id/{courierId}")
    public ResponseEntity<CourierProfile> getCourierProfileByCourierId(@PathVariable String courierId) {
        log.info("REST request to get courier profile by courier ID: {}", courierId);
        return courierProfileService.getCourierProfileByCourierId(courierId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get a courier profile by username.
     *
     * @param username the username
     * @return the courier profile
     */
    @GetMapping("/by-username/{username}")
    public ResponseEntity<CourierProfile> getCourierProfileByUsername(@PathVariable String username) {
        log.info("REST request to get courier profile by username: {}", username);
        return courierProfileService.getCourierProfileByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Update a courier profile.
     *
     * @param id the profile ID
     * @param courierProfile the updated profile details
     * @return the updated courier profile
     */
    @PutMapping("/{id}")
    public ResponseEntity<CourierProfile> updateCourierProfile(
            @PathVariable String id,
            @Valid @RequestBody CourierProfile courierProfile) {
        
        log.info("REST request to update courier profile with ID: {}", id);
        
        try {
            CourierProfile updatedProfile = courierProfileService.updateCourierProfile(id, courierProfile);
            return ResponseEntity.ok(updatedProfile);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Delete a courier profile.
     *
     * @param id the profile ID
     * @return no content if successful
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourierProfile(@PathVariable String id) {
        log.info("REST request to delete courier profile with ID: {}", id);
        
        if (courierProfileService.deleteCourierProfile(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    /**
     * Update courier's online status.
     *
     * @param courierId the courier ID
     * @param status the status update request containing the online status
     * @return the updated courier profile
     */
    @PatchMapping("/by-courier-id/{courierId}/online-status")
    public ResponseEntity<CourierProfile> updateOnlineStatus(
            @PathVariable String courierId,
            @RequestBody Map<String, Boolean> status) {
        
        log.info("REST request to update online status for courier ID: {}", courierId);
        
        Boolean isOnline = status.get("isOnline");
        if (isOnline == null) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            CourierProfile updatedProfile = courierProfileService.updateOnlineStatus(courierId, isOnline);
            return ResponseEntity.ok(updatedProfile);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Update courier's current location.
     *
     * @param courierId the courier ID
     * @param locationData the location data
     * @return the updated courier profile
     */
    @PatchMapping("/by-courier-id/{courierId}/location")
    public ResponseEntity<CourierProfile> updateLocation(
            @PathVariable String courierId,
            @Valid @RequestBody LocationData locationData) {
        
        log.info("REST request to update location for courier ID: {}", courierId);
        
        try {
            CourierProfile updatedProfile = courierProfileService.updateLocation(courierId, locationData);
            return ResponseEntity.ok(updatedProfile);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Update courier's FCM token.
     *
     * @param courierId the courier ID
     * @param tokenRequest the token request containing the FCM token
     * @return the updated courier profile
     */
    @PatchMapping("/by-courier-id/{courierId}/fcm-token")
    public ResponseEntity<CourierProfile> updateFcmToken(
            @PathVariable String courierId,
            @RequestBody Map<String, String> tokenRequest) {
        
        log.info("REST request to update FCM token for courier ID: {}", courierId);
        
        String fcmToken = tokenRequest.get("fcmToken");
        if (fcmToken == null || fcmToken.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            CourierProfile updatedProfile = courierProfileService.updateFcmToken(courierId, fcmToken);
            return ResponseEntity.ok(updatedProfile);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Update courier's account status.
     *
     * @param courierId the courier ID
     * @param statusRequest the status request containing the account status
     * @return the updated courier profile
     */
    @PatchMapping("/by-courier-id/{courierId}/account-status")
    public ResponseEntity<CourierProfile> updateAccountStatus(
            @PathVariable String courierId,
            @RequestBody Map<String, String> statusRequest) {
        
        log.info("REST request to update account status for courier ID: {}", courierId);
        
        String statusStr = statusRequest.get("accountStatus");
        if (statusStr == null) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            AccountStatus accountStatus = AccountStatus.valueOf(statusStr);
            CourierProfile updatedProfile = courierProfileService.updateAccountStatus(courierId, accountStatus);
            return ResponseEntity.ok(updatedProfile);
        } catch (IllegalArgumentException e) {
            log.error("Invalid account status value: {}", statusStr);
            return ResponseEntity.badRequest().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Update courier's onboarding status.
     *
     * @param courierId the courier ID
     * @param statusRequest the status request containing the onboarding status
     * @return the updated courier profile
     */
    @PatchMapping("/by-courier-id/{courierId}/onboarding-status")
    public ResponseEntity<CourierProfile> updateOnboardingStatus(
            @PathVariable String courierId,
            @RequestBody Map<String, String> statusRequest) {
        
        log.info("REST request to update onboarding status for courier ID: {}", courierId);
        
        String statusStr = statusRequest.get("onboardingStatus");
        if (statusStr == null) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            OnboardingStatus onboardingStatus = OnboardingStatus.valueOf(statusStr);
            CourierProfile updatedProfile = courierProfileService.updateOnboardingStatus(courierId, onboardingStatus);
            return ResponseEntity.ok(updatedProfile);
        } catch (IllegalArgumentException e) {
            log.error("Invalid onboarding status value: {}", statusStr);
            return ResponseEntity.badRequest().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get all courier profiles with a specific account status.
     *
     * @param status the account status
     * @param pageable the pagination information
     * @return page of courier profiles
     */
    @GetMapping("/by-account-status/{status}")
    public Page<CourierProfile> getCourierProfilesByAccountStatus(
            @PathVariable String status,
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.info("REST request to get courier profiles with account status: {}", status);
        
        try {
            AccountStatus accountStatus = AccountStatus.valueOf(status);
            return courierProfileService.getCourierProfilesByAccountStatus(accountStatus, pageable);
        } catch (IllegalArgumentException e) {
            log.error("Invalid account status value: {}", status);
            throw new IllegalArgumentException("Invalid account status value: " + status);
        }
    }
    
    /**
     * Get all courier profiles with a specific onboarding status.
     *
     * @param status the onboarding status
     * @param pageable the pagination information
     * @return page of courier profiles
     */
    @GetMapping("/by-onboarding-status/{status}")
    public Page<CourierProfile> getCourierProfilesByOnboardingStatus(
            @PathVariable String status,
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.info("REST request to get courier profiles with onboarding status: {}", status);
        
        try {
            OnboardingStatus onboardingStatus = OnboardingStatus.valueOf(status);
            return courierProfileService.getCourierProfilesByOnboardingStatus(onboardingStatus, pageable);
        } catch (IllegalArgumentException e) {
            log.error("Invalid onboarding status value: {}", status);
            throw new IllegalArgumentException("Invalid onboarding status value: " + status);
        }
    }
    
    /**
     * Get all online couriers.
     *
     * @return list of online courier profiles
     */
    @GetMapping("/online")
    public List<CourierProfile> getOnlineCouriers() {
        log.info("REST request to get all online couriers");
        return courierProfileService.getOnlineCouriers();
    }
    
    /**
     * Get couriers statistics.
     *
     * @return map of status to count
     */
    @GetMapping("/statistics")
    public Map<String, Long> getCourierStatistics() {
        log.info("REST request to get courier statistics");
        return courierProfileService.getCourierStatistics();
    }
} 
