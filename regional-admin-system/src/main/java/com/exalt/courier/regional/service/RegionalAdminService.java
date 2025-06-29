package com.exalt.courier.regional.service;

import com.socialecommerceecosystem.regional.model.RegionalAdmin;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service interface for managing regional admin operations.
 */
public interface RegionalAdminService {

    /**
     * Get all regional admins.
     *
     * @return List of all regional admins
     */
    List<RegionalAdmin> getAllRegionalAdmins();

    /**
     * Get a regional admin by ID.
     *
     * @param id The ID of the regional admin
     * @return Optional containing the regional admin if found, or empty otherwise
     */
    Optional<RegionalAdmin> getRegionalAdminById(Long id);

    /**
     * Get a regional admin by username.
     *
     * @param username The username of the admin
     * @return Optional containing the regional admin if found, or empty otherwise
     */
    Optional<RegionalAdmin> getRegionalAdminByUsername(String username);

    /**
     * Get a regional admin by email.
     *
     * @param email The email of the admin
     * @return Optional containing the regional admin if found, or empty otherwise
     */
    Optional<RegionalAdmin> getRegionalAdminByEmail(String email);

    /**
     * Get all active regional admins.
     *
     * @return List of active regional admins
     */
    List<RegionalAdmin> getActiveRegionalAdmins();

    /**
     * Get all regional admins for a specific regional settings ID.
     *
     * @param regionalSettingsId The ID of the regional settings
     * @return List of regional admins associated with the regional settings
     */
    List<RegionalAdmin> getRegionalAdminsByRegionalSettingsId(Long regionalSettingsId);

    /**
     * Create a new regional admin.
     *
     * @param regionalAdmin The regional admin to create
     * @return The created regional admin with its ID
     * @throws IllegalArgumentException if the username or email already exists
     */
    RegionalAdmin createRegionalAdmin(RegionalAdmin regionalAdmin);

    /**
     * Update an existing regional admin.
     *
     * @param id The ID of the regional admin to update
     * @param regionalAdmin The updated regional admin data
     * @return The updated regional admin
     * @throws IllegalArgumentException if the regional admin is not found
     */
    RegionalAdmin updateRegionalAdmin(Long id, RegionalAdmin regionalAdmin);

    /**
     * Delete a regional admin by its ID.
     *
     * @param id The ID of the regional admin to delete
     * @throws IllegalArgumentException if the regional admin is not found
     */
    void deleteRegionalAdmin(Long id);

    /**
     * Get all regional admins with a specific role.
     *
     * @param role The role to filter by
     * @return List of regional admins with the specified role
     */
    List<RegionalAdmin> getRegionalAdminsByRole(String role);

    /**
     * Get all regional admins with any of the specified roles.
     *
     * @param roles The set of roles to filter by
     * @return List of regional admins having any of the specified roles
     */
    List<RegionalAdmin> getRegionalAdminsByRoles(Set<String> roles);

    /**
     * Get all regional admins who have logged in since the specified date.
     *
     * @param date The date to compare against
     * @return List of regional admins who have logged in since the date
     */
    List<RegionalAdmin> getRegionalAdminsLoggedInSince(LocalDateTime date);

    /**
     * Get all regional admins who have not logged in since the specified date.
     *
     * @param date The date to compare against
     * @return List of regional admins who have not logged in since the date
     */
    List<RegionalAdmin> getRegionalAdminsNotLoggedInSince(LocalDateTime date);

    /**
     * Get all regional admins with accounts expiring before the specified date.
     *
     * @param date The date to compare against
     * @return List of regional admins with accounts expiring before the date
     */
    List<RegionalAdmin> getRegionalAdminsWithExpiringAccounts(LocalDateTime date);

    /**
     * Get all regional admins by department.
     *
     * @param department The department to filter by
     * @return List of regional admins in the specified department
     */
    List<RegionalAdmin> getRegionalAdminsByDepartment(String department);

    /**
     * Get all regional admins by position.
     *
     * @param position The position to filter by
     * @return List of regional admins with the specified position
     */
    List<RegionalAdmin> getRegionalAdminsByPosition(String position);

    /**
     * Get all regional admins with a specific access level.
     *
     * @param accessLevel The access level to filter by
     * @return List of regional admins with the specified access level
     */
    List<RegionalAdmin> getRegionalAdminsByAccessLevel(Integer accessLevel);

    /**
     * Get all regional admins reporting to a specific manager.
     *
     * @param managerId The ID of the manager
     * @return List of regional admins reporting to the specified manager
     */
    List<RegionalAdmin> getRegionalAdminsByManager(Long managerId);

    /**
     * Get all regional admins with two-factor authentication enabled.
     *
     * @return List of regional admins with two-factor authentication enabled
     */
    List<RegionalAdmin> getRegionalAdminsWithTwoFactorEnabled();

    /**
     * Get all regional admins with locked accounts.
     *
     * @return List of regional admins with locked accounts
     */
    List<RegionalAdmin> getRegionalAdminsWithLockedAccounts();

    /**
     * Search for regional admins by name (first name or last name, case-insensitive, partial match).
     *
     * @param name The name to search for
     * @return List of matching regional admins
     */
    List<RegionalAdmin> searchRegionalAdminsByName(String name);

    /**
     * Add roles to a regional admin.
     *
     * @param id The ID of the regional admin
     * @param roles The roles to add
     * @return The updated regional admin
     * @throws IllegalArgumentException if the regional admin is not found
     */
    RegionalAdmin addRolesToRegionalAdmin(Long id, Set<String> roles);

    /**
     * Remove roles from a regional admin.
     *
     * @param id The ID of the regional admin
     * @param roles The roles to remove
     * @return The updated regional admin
     * @throws IllegalArgumentException if the regional admin is not found
     */
    RegionalAdmin removeRolesFromRegionalAdmin(Long id, Set<String> roles);

    /**
     * Update the last login date for a regional admin.
     *
     * @param id The ID of the regional admin
     * @return The updated regional admin
     * @throws IllegalArgumentException if the regional admin is not found
     */
    RegionalAdmin updateLastLoginDate(Long id);

    /**
     * Lock a regional admin account.
     *
     * @param id The ID of the regional admin
     * @return The updated regional admin
     * @throws IllegalArgumentException if the regional admin is not found
     */
    RegionalAdmin lockAccount(Long id);

    /**
     * Unlock a regional admin account.
     *
     * @param id The ID of the regional admin
     * @return The updated regional admin
     * @throws IllegalArgumentException if the regional admin is not found
     */
    RegionalAdmin unlockAccount(Long id);

    /**
     * Enable two-factor authentication for a regional admin.
     *
     * @param id The ID of the regional admin
     * @return The updated regional admin
     * @throws IllegalArgumentException if the regional admin is not found
     */
    RegionalAdmin enableTwoFactorAuthentication(Long id);

    /**
     * Disable two-factor authentication for a regional admin.
     *
     * @param id The ID of the regional admin
     * @return The updated regional admin
     * @throws IllegalArgumentException if the regional admin is not found
     */
    RegionalAdmin disableTwoFactorAuthentication(Long id);

    /**
     * Check if a regional admin with the given username exists.
     *
     * @param username The username to check
     * @return true if a regional admin with the username exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Check if a regional admin with the given email exists.
     *
     * @param email The email to check
     * @return true if a regional admin with the email exists, false otherwise
     */
    boolean existsByEmail(String email);
}
