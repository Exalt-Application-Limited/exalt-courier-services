package com.microecosystem.courier.driver.app.security;

import com.microecosystem.courier.driver.app.model.Driver;
import com.microecosystem.courier.driver.app.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Service for security-related operations and authorization checks.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityService {

    private final DriverRepository driverRepository;

    /**
     * Checks if the current authenticated user is the driver with the given ID.
     *
     * @param driverId driver ID to check
     * @return true if the current user is the driver
     */
    public boolean isCurrentDriver(Long driverId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getId();

        return driverRepository.findById(driverId)
                .map(driver -> driver.getUser() != null && driver.getUser().getId().equals(userId))
                .orElse(false);
    }

    /**
     * Checks if the current authenticated user has the given user ID.
     *
     * @param userId user ID to check
     * @return true if the current user has the given ID
     */
    public boolean isCurrentUser(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId().equals(userId);
    }

    /**
     * Gets the current authenticated user's ID.
     *
     * @return current user ID or null if not authenticated
     */
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId();
    }

    /**
     * Gets the current authenticated driver's ID.
     *
     * @return current driver ID or null if not authenticated or not a driver
     */
    public Long getCurrentDriverId() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return null;
        }

        return driverRepository.findByUserId(userId)
                .map(Driver::getId)
                .orElse(null);
    }

    /**
     * Get the currently authenticated username.
     *
     * @return the username
     */
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getUsername();
    }
} 