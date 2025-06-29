package com.exalt.courier.regional.service.impl;

import com.socialecommerceecosystem.regional.model.RegionalAdmin;
import com.socialecommerceecosystem.regional.repository.RegionalAdminRepository;
import com.socialecommerceecosystem.regional.repository.RegionalSettingsRepository;
import com.socialecommerceecosystem.regional.service.RegionalAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Implementation of the RegionalAdminService interface.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RegionalAdminServiceImpl implements RegionalAdminService {

    private final RegionalAdminRepository regionalAdminRepository;
    private final RegionalSettingsRepository regionalSettingsRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<RegionalAdmin> getAllRegionalAdmins() {
        log.debug("Retrieving all regional admins");
        return regionalAdminRepository.findAll();
    }

    @Override
    public Optional<RegionalAdmin> getRegionalAdminById(Long id) {
        log.debug("Retrieving regional admin with ID: {}", id);
        return regionalAdminRepository.findById(id);
    }

    @Override
    public Optional<RegionalAdmin> getRegionalAdminByUsername(String username) {
        log.debug("Retrieving regional admin with username: {}", username);
        return regionalAdminRepository.findByUsername(username);
    }

    @Override
    public Optional<RegionalAdmin> getRegionalAdminByEmail(String email) {
        log.debug("Retrieving regional admin with email: {}", email);
        return regionalAdminRepository.findByEmail(email);
    }

    @Override
    public List<RegionalAdmin> getActiveRegionalAdmins() {
        log.debug("Retrieving all active regional admins");
        return regionalAdminRepository.findByIsActiveTrue();
    }

    @Override
    public List<RegionalAdmin> getRegionalAdminsByRegionalSettingsId(Long regionalSettingsId) {
        log.debug("Retrieving regional admins for regional settings ID: {}", regionalSettingsId);
        
        // Verify that the regional settings exists
        if (!regionalSettingsRepository.existsById(regionalSettingsId)) {
            throw new IllegalArgumentException("Regional settings not found with ID: " + regionalSettingsId);
        }
        
        return regionalAdminRepository.findByRegionalSettingsId(regionalSettingsId);
    }

    @Override
    public RegionalAdmin createRegionalAdmin(RegionalAdmin regionalAdmin) {
        log.debug("Creating new regional admin: {}", regionalAdmin);
        
        // Validate username and email uniqueness
        if (regionalAdminRepository.existsByUsername(regionalAdmin.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + regionalAdmin.getUsername());
        }
        
        if (regionalAdminRepository.existsByEmail(regionalAdmin.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + regionalAdmin.getEmail());
        }
        
        // Verify that the regional settings exists
        if (!regionalSettingsRepository.existsById(regionalAdmin.getRegionalSettingsId())) {
            throw new IllegalArgumentException("Regional settings not found with ID: " + regionalAdmin.getRegionalSettingsId());
        }
        
        // Encode password
        regionalAdmin.setPassword(passwordEncoder.encode(regionalAdmin.getPassword()));
        
        // Set default values if not provided
        if (regionalAdmin.getIsActive() == null) {
            regionalAdmin.setIsActive(true);
        }
        
        if (regionalAdmin.getRoles() == null) {
            regionalAdmin.setRoles(new HashSet<>());
            regionalAdmin.getRoles().add("ROLE_USER");
        }
        
        if (regionalAdmin.getTwoFactorEnabled() == null) {
            regionalAdmin.setTwoFactorEnabled(false);
        }
        
        if (regionalAdmin.getAccountLocked() == null) {
            regionalAdmin.setAccountLocked(false);
        }
        
        return regionalAdminRepository.save(regionalAdmin);
    }

    @Override
    public RegionalAdmin updateRegionalAdmin(Long id, RegionalAdmin regionalAdmin) {
        log.debug("Updating regional admin with ID: {}", id);
        
        RegionalAdmin existingAdmin = regionalAdminRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Regional admin not found with ID: " + id));
        
        // Check if username is being changed and if it already exists
        if (!regionalAdmin.getUsername().equals(existingAdmin.getUsername()) &&
            regionalAdminRepository.existsByUsername(regionalAdmin.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + regionalAdmin.getUsername());
        }
        
        // Check if email is being changed and if it already exists
        if (!regionalAdmin.getEmail().equals(existingAdmin.getEmail()) &&
            regionalAdminRepository.existsByEmail(regionalAdmin.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + regionalAdmin.getEmail());
        }
        
        // Verify that the regional settings exists
        if (!regionalSettingsRepository.existsById(regionalAdmin.getRegionalSettingsId())) {
            throw new IllegalArgumentException("Regional settings not found with ID: " + regionalAdmin.getRegionalSettingsId());
        }
        
        // Update fields
        existingAdmin.setUsername(regionalAdmin.getUsername());
        existingAdmin.setFirstName(regionalAdmin.getFirstName());
        existingAdmin.setLastName(regionalAdmin.getLastName());
        existingAdmin.setEmail(regionalAdmin.getEmail());
        existingAdmin.setPhoneNumber(regionalAdmin.getPhoneNumber());
        existingAdmin.setIsActive(regionalAdmin.getIsActive());
        existingAdmin.setRegionalSettingsId(regionalAdmin.getRegionalSettingsId());
        existingAdmin.setPosition(regionalAdmin.getPosition());
        existingAdmin.setDepartment(regionalAdmin.getDepartment());
        existingAdmin.setRoles(regionalAdmin.getRoles());
        existingAdmin.setAccessLevel(regionalAdmin.getAccessLevel());
        existingAdmin.setProfileImageUrl(regionalAdmin.getProfileImageUrl());
        existingAdmin.setEmergencyContact(regionalAdmin.getEmergencyContact());
        existingAdmin.setNotes(regionalAdmin.getNotes());
        existingAdmin.setManagedLocations(regionalAdmin.getManagedLocations());
        existingAdmin.setReportsTo(regionalAdmin.getReportsTo());
        existingAdmin.setTwoFactorEnabled(regionalAdmin.getTwoFactorEnabled());
        existingAdmin.setAccountLocked(regionalAdmin.getAccountLocked());
        existingAdmin.setAccountExpiresAt(regionalAdmin.getAccountExpiresAt());
        
        // Only update password if a new one is provided (not empty)
        if (regionalAdmin.getPassword() != null && !regionalAdmin.getPassword().isEmpty()) {
            existingAdmin.setPassword(passwordEncoder.encode(regionalAdmin.getPassword()));
        }
        
        return regionalAdminRepository.save(existingAdmin);
    }

    @Override
    public void deleteRegionalAdmin(Long id) {
        log.debug("Deleting regional admin with ID: {}", id);
        
        if (!regionalAdminRepository.existsById(id)) {
            throw new IllegalArgumentException("Regional admin not found with ID: " + id);
        }
        
        regionalAdminRepository.deleteById(id);
    }

    @Override
    public List<RegionalAdmin> getRegionalAdminsByRole(String role) {
        log.debug("Retrieving regional admins with role: {}", role);
        return regionalAdminRepository.findByRole(role);
    }

    @Override
    public List<RegionalAdmin> getRegionalAdminsByRoles(Set<String> roles) {
        log.debug("Retrieving regional admins with roles: {}", roles);
        return regionalAdminRepository.findByRoles(roles);
    }

    @Override
    public List<RegionalAdmin> getRegionalAdminsLoggedInSince(LocalDateTime date) {
        log.debug("Retrieving regional admins logged in since: {}", date);
        return regionalAdminRepository.findByLastLoginDateAfter(date);
    }

    @Override
    public List<RegionalAdmin> getRegionalAdminsNotLoggedInSince(LocalDateTime date) {
        log.debug("Retrieving regional admins not logged in since: {}", date);
        return regionalAdminRepository.findByLastLoginDateBefore(date);
    }

    @Override
    public List<RegionalAdmin> getRegionalAdminsWithExpiringAccounts(LocalDateTime date) {
        log.debug("Retrieving regional admins with accounts expiring before: {}", date);
        return regionalAdminRepository.findByAccountExpiresAtBefore(date);
    }

    @Override
    public List<RegionalAdmin> getRegionalAdminsByDepartment(String department) {
        log.debug("Retrieving regional admins in department: {}", department);
        return regionalAdminRepository.findByDepartment(department);
    }

    @Override
    public List<RegionalAdmin> getRegionalAdminsByPosition(String position) {
        log.debug("Retrieving regional admins with position: {}", position);
        return regionalAdminRepository.findByPosition(position);
    }

    @Override
    public List<RegionalAdmin> getRegionalAdminsByAccessLevel(Integer accessLevel) {
        log.debug("Retrieving regional admins with access level: {}", accessLevel);
        return regionalAdminRepository.findByAccessLevel(accessLevel);
    }

    @Override
    public List<RegionalAdmin> getRegionalAdminsByManager(Long managerId) {
        log.debug("Retrieving regional admins reporting to manager with ID: {}", managerId);
        return regionalAdminRepository.findByReportsTo(managerId);
    }

    @Override
    public List<RegionalAdmin> getRegionalAdminsWithTwoFactorEnabled() {
        log.debug("Retrieving regional admins with two-factor authentication enabled");
        return regionalAdminRepository.findByTwoFactorEnabledTrue();
    }

    @Override
    public List<RegionalAdmin> getRegionalAdminsWithLockedAccounts() {
        log.debug("Retrieving regional admins with locked accounts");
        return regionalAdminRepository.findByAccountLockedTrue();
    }

    @Override
    public List<RegionalAdmin> searchRegionalAdminsByName(String name) {
        log.debug("Searching for regional admins with name containing: {}", name);
        return regionalAdminRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public RegionalAdmin addRolesToRegionalAdmin(Long id, Set<String> roles) {
        log.debug("Adding roles {} to regional admin with ID: {}", roles, id);
        
        RegionalAdmin admin = regionalAdminRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Regional admin not found with ID: " + id));
        
        Set<String> currentRoles = admin.getRoles();
        currentRoles.addAll(roles);
        admin.setRoles(currentRoles);
        
        return regionalAdminRepository.save(admin);
    }

    @Override
    public RegionalAdmin removeRolesFromRegionalAdmin(Long id, Set<String> roles) {
        log.debug("Removing roles {} from regional admin with ID: {}", roles, id);
        
        RegionalAdmin admin = regionalAdminRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Regional admin not found with ID: " + id));
        
        Set<String> currentRoles = admin.getRoles();
        currentRoles.removeAll(roles);
        admin.setRoles(currentRoles);
        
        return regionalAdminRepository.save(admin);
    }

    @Override
    public RegionalAdmin updateLastLoginDate(Long id) {
        log.debug("Updating last login date for regional admin with ID: {}", id);
        
        RegionalAdmin admin = regionalAdminRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Regional admin not found with ID: " + id));
        
        admin.setLastLoginDate(LocalDateTime.now());
        
        return regionalAdminRepository.save(admin);
    }

    @Override
    public RegionalAdmin lockAccount(Long id) {
        log.debug("Locking account for regional admin with ID: {}", id);
        
        RegionalAdmin admin = regionalAdminRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Regional admin not found with ID: " + id));
        
        admin.setAccountLocked(true);
        
        return regionalAdminRepository.save(admin);
    }

    @Override
    public RegionalAdmin unlockAccount(Long id) {
        log.debug("Unlocking account for regional admin with ID: {}", id);
        
        RegionalAdmin admin = regionalAdminRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Regional admin not found with ID: " + id));
        
        admin.setAccountLocked(false);
        
        return regionalAdminRepository.save(admin);
    }

    @Override
    public RegionalAdmin enableTwoFactorAuthentication(Long id) {
        log.debug("Enabling two-factor authentication for regional admin with ID: {}", id);
        
        RegionalAdmin admin = regionalAdminRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Regional admin not found with ID: " + id));
        
        admin.setTwoFactorEnabled(true);
        
        return regionalAdminRepository.save(admin);
    }

    @Override
    public RegionalAdmin disableTwoFactorAuthentication(Long id) {
        log.debug("Disabling two-factor authentication for regional admin with ID: {}", id);
        
        RegionalAdmin admin = regionalAdminRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Regional admin not found with ID: " + id));
        
        admin.setTwoFactorEnabled(false);
        
        return regionalAdminRepository.save(admin);
    }

    @Override
    public boolean existsByUsername(String username) {
        log.debug("Checking if regional admin exists with username: {}", username);
        return regionalAdminRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        log.debug("Checking if regional admin exists with email: {}", email);
        return regionalAdminRepository.existsByEmail(email);
    }
}
