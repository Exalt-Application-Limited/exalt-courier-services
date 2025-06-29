package com.exalt.courier.regional.repository;

import com.socialecommerceecosystem.regional.model.RegionalAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Repository interface for managing RegionalAdmin entities.
 */
@Repository
public interface RegionalAdminRepository extends JpaRepository<RegionalAdmin, Long> {

    /**
     * Find a regional admin by username.
     * 
     * @param username The username of the admin
     * @return An Optional containing the RegionalAdmin if found, or empty otherwise
     */
    Optional<RegionalAdmin> findByUsername(String username);

    /**
     * Find a regional admin by email.
     * 
     * @param email The email of the admin
     * @return An Optional containing the RegionalAdmin if found, or empty otherwise
     */
    Optional<RegionalAdmin> findByEmail(String email);

    /**
     * Find all active regional admins.
     * 
     * @return List of active regional admins
     */
    List<RegionalAdmin> findByIsActiveTrue();

    /**
     * Find all regional admins for a specific regional settings ID.
     * 
     * @param regionalSettingsId The ID of the regional settings
     * @return List of regional admins associated with the regional settings
     */
    List<RegionalAdmin> findByRegionalSettingsId(Long regionalSettingsId);

    /**
     * Find all regional admins with a specific role.
     * 
     * @param role The role to filter by
     * @return List of regional admins with the specified role
     */
    @Query("SELECT ra FROM RegionalAdmin ra JOIN ra.roles role WHERE role = :role")
    List<RegionalAdmin> findByRole(@Param("role") String role);

    /**
     * Find all regional admins who have logged in since the specified date.
     * 
     * @param date The date to compare against
     * @return List of regional admins who have logged in since the date
     */
    List<RegionalAdmin> findByLastLoginDateAfter(LocalDateTime date);

    /**
     * Find all regional admins who have not logged in since the specified date.
     * 
     * @param date The date to compare against
     * @return List of regional admins who have not logged in since the date
     */
    List<RegionalAdmin> findByLastLoginDateBefore(LocalDateTime date);

    /**
     * Find all regional admins with accounts expiring before the specified date.
     * 
     * @param date The date to compare against
     * @return List of regional admins with accounts expiring before the date
     */
    List<RegionalAdmin> findByAccountExpiresAtBefore(LocalDateTime date);

    /**
     * Find all regional admins by department.
     * 
     * @param department The department to filter by
     * @return List of regional admins in the specified department
     */
    List<RegionalAdmin> findByDepartment(String department);

    /**
     * Find all regional admins by position.
     * 
     * @param position The position to filter by
     * @return List of regional admins with the specified position
     */
    List<RegionalAdmin> findByPosition(String position);

    /**
     * Find all regional admins with a specific access level.
     * 
     * @param accessLevel The access level to filter by
     * @return List of regional admins with the specified access level
     */
    List<RegionalAdmin> findByAccessLevel(Integer accessLevel);

    /**
     * Find all regional admins reporting to a specific manager.
     * 
     * @param managerId The ID of the manager
     * @return List of regional admins reporting to the specified manager
     */
    List<RegionalAdmin> findByReportsTo(Long managerId);

    /**
     * Find all regional admins with two-factor authentication enabled.
     * 
     * @return List of regional admins with two-factor authentication enabled
     */
    List<RegionalAdmin> findByTwoFactorEnabledTrue();

    /**
     * Find all regional admins with accounts that are locked.
     * 
     * @return List of regional admins with locked accounts
     */
    List<RegionalAdmin> findByAccountLockedTrue();

    /**
     * Find regional admins by name (first name or last name containing the search text, case-insensitive).
     * 
     * @param name The name to search for
     * @return List of regional admins matching the search criteria
     */
    @Query("SELECT ra FROM RegionalAdmin ra WHERE " +
           "LOWER(ra.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
           "LOWER(ra.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<RegionalAdmin> findByNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Find regional admins by multiple roles.
     * 
     * @param roles The set of roles to filter by
     * @return List of regional admins having any of the specified roles
     */
    @Query("SELECT DISTINCT ra FROM RegionalAdmin ra JOIN ra.roles role WHERE role IN :roles")
    List<RegionalAdmin> findByRoles(@Param("roles") Set<String> roles);

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
