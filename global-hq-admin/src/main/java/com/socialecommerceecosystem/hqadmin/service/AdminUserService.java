package com.gogidix.courier.courier.hqadmin.service;

import com.socialecommerceecosystem.hqadmin.model.AdminUser;
import com.socialecommerceecosystem.hqadmin.model.GlobalRegion;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service interface defining operations for managing admin users at the global level.
 */
public interface AdminUserService {
    
    /**
     * Get all admin users
     * 
     * @return List of all admin users
     */
    List<AdminUser> getAllAdminUsers();
    
    /**
     * Get an admin user by ID
     * 
     * @param id The admin user ID
     * @return The admin user if found
     */
    Optional<AdminUser> getAdminUserById(Long id);
    
    /**
     * Get an admin user by username
     * 
     * @param username The username
     * @return The admin user if found
     */
    Optional<AdminUser> getAdminUserByUsername(String username);
    
    /**
     * Get an admin user by email
     * 
     * @param email The email
     * @return The admin user if found
     */
    Optional<AdminUser> getAdminUserByEmail(String email);
    
    /**
     * Create a new admin user
     * 
     * @param adminUser The admin user to create
     * @return The created admin user
     */
    AdminUser createAdminUser(AdminUser adminUser);
    
    /**
     * Update an existing admin user
     * 
     * @param id The admin user ID to update
     * @param adminUserDetails The updated admin user details
     * @return The updated admin user
     * @throws RuntimeException if admin user not found
     */
    AdminUser updateAdminUser(Long id, AdminUser adminUserDetails);
    
    /**
     * Delete an admin user
     * 
     * @param id The admin user ID to delete
     * @throws RuntimeException if admin user not found
     */
    void deleteAdminUser(Long id);
    
    /**
     * Get all active admin users
     * 
     * @return List of active admin users
     */
    List<AdminUser> getAllActiveAdminUsers();
    
    /**
     * Find admin users by role
     * 
     * @param role The role to search for
     * @return List of admin users with the specified role
     */
    List<AdminUser> findAdminUsersByRole(String role);
    
    /**
     * Find admin users by name (first name or last name)
     * 
     * @param name The name to search for
     * @return List of admin users matching the name search
     */
    List<AdminUser> findAdminUsersByName(String name);
    
    /**
     * Add roles to an admin user
     * 
     * @param id The admin user ID
     * @param roles The roles to add
     * @return The updated admin user
     * @throws RuntimeException if admin user not found
     */
    AdminUser addRolesToAdminUser(Long id, Set<String> roles);
    
    /**
     * Remove roles from an admin user
     * 
     * @param id The admin user ID
     * @param roles The roles to remove
     * @return The updated admin user
     * @throws RuntimeException if admin user not found
     */
    AdminUser removeRolesFromAdminUser(Long id, Set<String> roles);
    
    /**
     * Add permissions to an admin user
     * 
     * @param id The admin user ID
     * @param permissions The permissions to add
     * @return The updated admin user
     * @throws RuntimeException if admin user not found
     */
    AdminUser addPermissionsToAdminUser(Long id, Set<String> permissions);
    
    /**
     * Remove permissions from an admin user
     * 
     * @param id The admin user ID
     * @param permissions The permissions to remove
     * @return The updated admin user
     * @throws RuntimeException if admin user not found
     */
    AdminUser removePermissionsFromAdminUser(Long id, Set<String> permissions);
    
    /**
     * Grant region access to an admin user
     * 
     * @param adminUserId The admin user ID
     * @param regionId The global region ID
     * @return The updated admin user
     * @throws RuntimeException if admin user or region not found
     */
    AdminUser grantRegionAccess(Long adminUserId, Long regionId);
    
    /**
     * Revoke region access from an admin user
     * 
     * @param adminUserId The admin user ID
     * @param regionId The global region ID
     * @return The updated admin user
     * @throws RuntimeException if admin user or region not found
     */
    AdminUser revokeRegionAccess(Long adminUserId, Long regionId);
    
    /**
     * Find admin users who have access to a specific region
     * 
     * @param region The global region
     * @return List of admin users with access to the region
     */
    List<AdminUser> findAdminUsersWithRegionAccess(GlobalRegion region);
    
    /**
     * Change an admin user's password
     * 
     * @param id The admin user ID
     * @param newPasswordHash The new password hash
     * @return The updated admin user
     * @throws RuntimeException if admin user not found
     */
    AdminUser changePassword(Long id, String newPasswordHash);
    
    /**
     * Update an admin user's last login time
     * 
     * @param id The admin user ID
     * @return The updated admin user
     * @throws RuntimeException if admin user not found
     */
    AdminUser updateLastLoginTime(Long id);
}
