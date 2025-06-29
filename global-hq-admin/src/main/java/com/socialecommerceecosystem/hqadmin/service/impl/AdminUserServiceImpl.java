package com.exalt.courier.hqadmin.service.impl;

import com.socialecommerceecosystem.hqadmin.model.AdminUser;
import com.socialecommerceecosystem.hqadmin.model.GlobalRegion;
import com.socialecommerceecosystem.hqadmin.repository.AdminUserRepository;
import com.socialecommerceecosystem.hqadmin.repository.GlobalRegionRepository;
import com.socialecommerceecosystem.hqadmin.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of the AdminUserService interface.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final AdminUserRepository adminUserRepository;
    private final GlobalRegionRepository globalRegionRepository;

    @Override
    public List<AdminUser> getAllAdminUsers() {
        return adminUserRepository.findAll();
    }

    @Override
    public Optional<AdminUser> getAdminUserById(Long id) {
        return adminUserRepository.findById(id);
    }

    @Override
    public Optional<AdminUser> getAdminUserByUsername(String username) {
        return adminUserRepository.findByUsername(username);
    }

    @Override
    public Optional<AdminUser> getAdminUserByEmail(String email) {
        return adminUserRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public AdminUser createAdminUser(AdminUser adminUser) {
        log.info("Creating new admin user with username: {}", adminUser.getUsername());
        
        // Check if username already exists
        if (adminUserRepository.findByUsername(adminUser.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists: " + adminUser.getUsername());
        }
        
        // Check if email already exists
        if (adminUserRepository.findByEmail(adminUser.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + adminUser.getEmail());
        }
        
        return adminUserRepository.save(adminUser);
    }

    @Override
    @Transactional
    public AdminUser updateAdminUser(Long id, AdminUser adminUserDetails) {
        log.info("Updating admin user with id: {}", id);
        
        return adminUserRepository.findById(id)
            .map(existingUser -> {
                // Check if username is being changed and if it already exists
                if (!existingUser.getUsername().equals(adminUserDetails.getUsername()) && 
                    adminUserRepository.findByUsername(adminUserDetails.getUsername()).isPresent()) {
                    throw new IllegalArgumentException("Username already exists: " + adminUserDetails.getUsername());
                }
                
                // Check if email is being changed and if it already exists
                if (!existingUser.getEmail().equals(adminUserDetails.getEmail()) && 
                    adminUserRepository.findByEmail(adminUserDetails.getEmail()).isPresent()) {
                    throw new IllegalArgumentException("Email already exists: " + adminUserDetails.getEmail());
                }
                
                // Update fields
                existingUser.setFirstName(adminUserDetails.getFirstName());
                existingUser.setLastName(adminUserDetails.getLastName());
                existingUser.setEmail(adminUserDetails.getEmail());
                existingUser.setPhoneNumber(adminUserDetails.getPhoneNumber());
                existingUser.setIsActive(adminUserDetails.getIsActive());
                
                // Only update username if it's different
                if (!existingUser.getUsername().equals(adminUserDetails.getUsername())) {
                    existingUser.setUsername(adminUserDetails.getUsername());
                }
                
                // Update password only if provided
                if (adminUserDetails.getPasswordHash() != null && !adminUserDetails.getPasswordHash().isEmpty()) {
                    existingUser.setPasswordHash(adminUserDetails.getPasswordHash());
                }
                
                return adminUserRepository.save(existingUser);
            })
            .orElseThrow(() -> new IllegalArgumentException("Admin user not found with id: " + id));
    }

    @Override
    @Transactional
    public void deleteAdminUser(Long id) {
        log.info("Deleting admin user with id: {}", id);
        
        AdminUser adminUser = adminUserRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Admin user not found with id: " + id));
        
        adminUserRepository.delete(adminUser);
    }

    @Override
    public List<AdminUser> getAllActiveAdminUsers() {
        return adminUserRepository.findByIsActiveTrue();
    }

    @Override
    public List<AdminUser> findAdminUsersByRole(String role) {
        return adminUserRepository.findByRole(role);
    }

    @Override
    public List<AdminUser> findAdminUsersByName(String name) {
        return adminUserRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name, name);
    }

    @Override
    @Transactional
    public AdminUser addRolesToAdminUser(Long id, Set<String> roles) {
        log.info("Adding roles {} to admin user with id: {}", roles, id);
        
        AdminUser adminUser = adminUserRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Admin user not found with id: " + id));
        
        adminUser.getRoles().addAll(roles);
        
        return adminUserRepository.save(adminUser);
    }

    @Override
    @Transactional
    public AdminUser removeRolesFromAdminUser(Long id, Set<String> roles) {
        log.info("Removing roles {} from admin user with id: {}", roles, id);
        
        AdminUser adminUser = adminUserRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Admin user not found with id: " + id));
        
        adminUser.setRoles(adminUser.getRoles().stream()
            .filter(role -> !roles.contains(role))
            .collect(Collectors.toSet()));
        
        return adminUserRepository.save(adminUser);
    }

    @Override
    @Transactional
    public AdminUser addPermissionsToAdminUser(Long id, Set<String> permissions) {
        log.info("Adding permissions {} to admin user with id: {}", permissions, id);
        
        AdminUser adminUser = adminUserRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Admin user not found with id: " + id));
        
        adminUser.getPermissions().addAll(permissions);
        
        return adminUserRepository.save(adminUser);
    }

    @Override
    @Transactional
    public AdminUser removePermissionsFromAdminUser(Long id, Set<String> permissions) {
        log.info("Removing permissions {} from admin user with id: {}", permissions, id);
        
        AdminUser adminUser = adminUserRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Admin user not found with id: " + id));
        
        adminUser.setPermissions(adminUser.getPermissions().stream()
            .filter(permission -> !permissions.contains(permission))
            .collect(Collectors.toSet()));
        
        return adminUserRepository.save(adminUser);
    }

    @Override
    @Transactional
    public AdminUser grantRegionAccess(Long adminUserId, Long regionId) {
        log.info("Granting region access for region id {} to admin user with id: {}", regionId, adminUserId);
        
        AdminUser adminUser = adminUserRepository.findById(adminUserId)
            .orElseThrow(() -> new IllegalArgumentException("Admin user not found with id: " + adminUserId));
        
        GlobalRegion region = globalRegionRepository.findById(regionId)
            .orElseThrow(() -> new IllegalArgumentException("Global region not found with id: " + regionId));
        
        adminUser.getAccessibleRegions().add(region);
        
        return adminUserRepository.save(adminUser);
    }

    @Override
    @Transactional
    public AdminUser revokeRegionAccess(Long adminUserId, Long regionId) {
        log.info("Revoking region access for region id {} from admin user with id: {}", regionId, adminUserId);
        
        AdminUser adminUser = adminUserRepository.findById(adminUserId)
            .orElseThrow(() -> new IllegalArgumentException("Admin user not found with id: " + adminUserId));
        
        GlobalRegion region = globalRegionRepository.findById(regionId)
            .orElseThrow(() -> new IllegalArgumentException("Global region not found with id: " + regionId));
        
        adminUser.getAccessibleRegions().remove(region);
        
        return adminUserRepository.save(adminUser);
    }

    @Override
    public List<AdminUser> findAdminUsersWithRegionAccess(GlobalRegion region) {
        return adminUserRepository.findByAccessibleRegionsContaining(region);
    }

    @Override
    @Transactional
    public AdminUser changePassword(Long id, String newPasswordHash) {
        log.info("Changing password for admin user with id: {}", id);
        
        AdminUser adminUser = adminUserRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Admin user not found with id: " + id));
        
        adminUser.setPasswordHash(newPasswordHash);
        
        return adminUserRepository.save(adminUser);
    }

    @Override
    @Transactional
    public AdminUser updateLastLoginTime(Long id) {
        log.info("Updating last login time for admin user with id: {}", id);
        
        AdminUser adminUser = adminUserRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Admin user not found with id: " + id));
        
        adminUser.setLastLoginAt(LocalDateTime.now());
        
        return adminUserRepository.save(adminUser);
    }
}
