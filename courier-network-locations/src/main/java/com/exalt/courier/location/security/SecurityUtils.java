package com.exalt.courier.location.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Utility methods for working with security context and JWT tokens.
 */
@Component
public class SecurityUtils {

    /**
     * Get the username from the JWT token in the security context.
     * 
     * @return Optional containing the username, or empty if not found
     */
    public Optional<String> getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        
        Object principal = authentication.getPrincipal();
        if (principal instanceof Jwt) {
            Jwt jwt = (Jwt) principal;
            return Optional.ofNullable(jwt.getClaimAsString("sub"));
        }
        
        return Optional.empty();
    }

    /**
     * Get the user ID from the JWT token in the security context.
     * 
     * @return Optional containing the user ID, or empty if not found
     */
    public Optional<String> getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        
        Object principal = authentication.getPrincipal();
        if (principal instanceof Jwt) {
            Jwt jwt = (Jwt) principal;
            return Optional.ofNullable(jwt.getClaimAsString("user_id"));
        }
        
        return Optional.empty();
    }

    /**
     * Get the roles from the JWT token in the security context.
     * 
     * @return List of roles, or empty list if none found
     */
    public List<String> getCurrentUserRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return List.of();
        }
        
        Object principal = authentication.getPrincipal();
        if (principal instanceof Jwt) {
            Jwt jwt = (Jwt) principal;
            List<String> roles = jwt.getClaimAsStringList("roles");
            return roles != null ? roles : List.of();
        }
        
        return List.of();
    }

    /**
     * Check if the current user has the specified role.
     * 
     * @param role the role to check
     * @return true if the user has the role, false otherwise
     */
    public boolean hasRole(String role) {
        return getCurrentUserRoles().contains(role);
    }

    /**
     * Check if the current user has admin role.
     * 
     * @return true if the user is an admin, false otherwise
     */
    public boolean isAdmin() {
        return hasRole("ADMIN");
    }

    /**
     * Check if the current user owns the resource.
     * 
     * @param resourceOwnerId the ID of the resource owner
     * @return true if the current user owns the resource, false otherwise
     */
    public boolean isResourceOwner(String resourceOwnerId) {
        return getCurrentUserId()
                .map(userId -> userId.equals(resourceOwnerId))
                .orElse(false);
    }
}
