package com.microecosystem.courier.driver.app.security;

import com.microecosystem.courier.driver.app.model.Driver;
import com.microecosystem.courier.driver.app.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Implementation of the SecurityService interface.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityServiceImpl implements SecurityService {

    private final DriverRepository driverRepository;

    @Override
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || 
                authentication.getPrincipal().equals("anonymousUser")) {
            return null;
        }
        
        return authentication.getName();
    }

    @Override
    public Long getCurrentDriverId() {
        String username = getCurrentUsername();
        if (username == null) {
            return null;
        }
        
        return driverRepository.findByUserUsername(username)
                .map(Driver::getId)
                .orElse(null);
    }
} 