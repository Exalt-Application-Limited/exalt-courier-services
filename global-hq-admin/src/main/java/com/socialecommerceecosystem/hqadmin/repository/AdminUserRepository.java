package com.gogidix.courier.courier.hqadmin.repository;

import com.socialecommerceecosystem.hqadmin.model.AdminUser;
import com.socialecommerceecosystem.hqadmin.model.GlobalRegion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for {@link AdminUser} entity that provides
 * data access operations for global admin users.
 */
@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {
    
    /**
     * Find an admin user by username
     * 
     * @param username The username
     * @return The admin user if found
     */
    Optional<AdminUser> findByUsername(String username);
    
    /**
     * Find an admin user by email
     * 
     * @param email The email
     * @return The admin user if found
     */
    Optional<AdminUser> findByEmail(String email);
    
    /**
     * Find all active admin users
     * 
     * @return List of active admin users
     */
    List<AdminUser> findByIsActiveTrue();
    
    /**
     * Find admin users by role
     * 
     * @param role The role to search for
     * @return List of admin users with the specified role
     */
    @Query("SELECT a FROM AdminUser a JOIN a.roles r WHERE r = :role")
    List<AdminUser> findByRole(@Param("role") String role);
    
    /**
     * Find admin users who have access to a specific region
     * 
     * @param region The global region
     * @return List of admin users with access to the region
     */
    List<AdminUser> findByAccessibleRegionsContaining(GlobalRegion region);
    
    /**
     * Find admin users by name (first name or last name)
     * 
     * @param name The name to search for
     * @return List of admin users matching the name search
     */
    List<AdminUser> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String name, String sameName);
}
