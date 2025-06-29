package com.exalt.courier.location.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.socialecommerceecosystem.location.model.LocationStaff;
import com.socialecommerceecosystem.location.model.PhysicalLocation;
import com.socialecommerceecosystem.location.model.StaffRole;
import com.socialecommerceecosystem.location.repository.LocationStaffRepository;
import com.socialecommerceecosystem.location.repository.PhysicalLocationRepository;
import com.socialecommerceecosystem.location.service.StaffManagementService;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of the StaffManagementService interface.
 * Provides business logic for managing staff members at physical courier locations.
 */
@Service
@Slf4j
public class StaffManagementServiceImpl implements StaffManagementService {

    private final LocationStaffRepository staffRepository;
    private final PhysicalLocationRepository locationRepository;

    @Autowired
    public StaffManagementServiceImpl(
            LocationStaffRepository staffRepository,
            PhysicalLocationRepository locationRepository) {
        this.staffRepository = staffRepository;
        this.locationRepository = locationRepository;
    }

    @Override
    public List<LocationStaff> getAllStaff() {
        log.debug("Getting all staff members");
        return staffRepository.findAll();
    }

    @Override
    public Page<LocationStaff> getAllStaff(Pageable pageable) {
        log.debug("Getting staff members with pagination: {}", pageable);
        return staffRepository.findAll(pageable);
    }

    @Override
    public Optional<LocationStaff> getStaffById(Long staffId) {
        log.debug("Getting staff member with ID: {}", staffId);
        return staffRepository.findById(staffId);
    }

    @Override
    public List<LocationStaff> getStaffByLocation(Long locationId) {
        log.debug("Getting staff members for location with ID: {}", locationId);
        return staffRepository.findByPhysicalLocationId(locationId);
    }

    @Override
    public List<LocationStaff> getStaffByRole(StaffRole role) {
        log.debug("Getting staff members with role: {}", role);
        return staffRepository.findByRole(role);
    }

    @Override
    @Transactional
    public LocationStaff createStaff(LocationStaff staff) {
        log.info("Creating new staff member: {} {}", staff.getFirstName(), staff.getLastName());
        
        // Set default values if not provided
        if (staff.getCreatedAt() == null) {
            staff.setCreatedAt(LocalDateTime.now());
        }
        if (staff.getUpdatedAt() == null) {
            staff.setUpdatedAt(LocalDateTime.now());
        }
        
        // Verify that the location exists
        if (staff.getPhysicalLocation() != null && staff.getPhysicalLocation().getId() != null) {
            Long locationId = staff.getPhysicalLocation().getId();
            PhysicalLocation location = locationRepository.findById(locationId)
                    .orElseThrow(() -> {
                        log.error("Location with ID: {} not found", locationId);
                        return new IllegalArgumentException("Location not found with ID: " + locationId);
                    });
            staff.setPhysicalLocation(location);
        }
        
        LocationStaff savedStaff = staffRepository.save(staff);
        log.info("Successfully created staff member with ID: {}", savedStaff.getId());
        
        return savedStaff;
    }

    @Override
    @Transactional
    public LocationStaff updateStaff(Long staffId, LocationStaff staff) {
        log.info("Updating staff member with ID: {}", staffId);
        
        return staffRepository.findById(staffId)
                .map(existingStaff -> {
                    // Update basic information
                    existingStaff.setFirstName(staff.getFirstName());
                    existingStaff.setLastName(staff.getLastName());
                    existingStaff.setEmail(staff.getEmail());
                    existingStaff.setPhone(staff.getPhone());
                    existingStaff.setRole(staff.getRole());
                    existingStaff.setActive(staff.isActive());
                    existingStaff.setNotes(staff.getNotes());
                    existingStaff.setUpdatedAt(LocalDateTime.now());
                    
                    // Only update location if provided
                    if (staff.getPhysicalLocation() != null && staff.getPhysicalLocation().getId() != null) {
                        Long locationId = staff.getPhysicalLocation().getId();
                        PhysicalLocation location = locationRepository.findById(locationId)
                                .orElseThrow(() -> {
                                    log.error("Location with ID: {} not found", locationId);
                                    return new IllegalArgumentException("Location not found with ID: " + locationId);
                                });
                        existingStaff.setPhysicalLocation(location);
                    }
                    
                    LocationStaff updatedStaff = staffRepository.save(existingStaff);
                    log.info("Successfully updated staff member with ID: {}", updatedStaff.getId());
                    
                    return updatedStaff;
                })
                .orElseThrow(() -> {
                    log.error("Staff member with ID: {} not found", staffId);
                    return new IllegalArgumentException("Staff member not found with ID: " + staffId);
                });
    }

    @Override
    @Transactional
    public void deleteStaff(Long staffId) {
        log.info("Deleting staff member with ID: {}", staffId);
        
        if (staffRepository.existsById(staffId)) {
            staffRepository.deleteById(staffId);
            log.info("Successfully deleted staff member with ID: {}", staffId);
        } else {
            log.error("Staff member with ID: {} not found", staffId);
            throw new IllegalArgumentException("Staff member not found with ID: " + staffId);
        }
    }

    @Override
    @Transactional
    public LocationStaff assignStaffToLocation(Long staffId, Long locationId) {
        log.info("Assigning staff member with ID: {} to location with ID: {}", staffId, locationId);
        
        LocationStaff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> {
                    log.error("Staff member with ID: {} not found", staffId);
                    return new IllegalArgumentException("Staff member not found with ID: " + staffId);
                });
        
        PhysicalLocation location = locationRepository.findById(locationId)
                .orElseThrow(() -> {
                    log.error("Location with ID: {} not found", locationId);
                    return new IllegalArgumentException("Location not found with ID: " + locationId);
                });
        
        staff.setPhysicalLocation(location);
        staff.setUpdatedAt(LocalDateTime.now());
        
        LocationStaff updatedStaff = staffRepository.save(staff);
        log.info("Successfully assigned staff member with ID: {} to location with ID: {}", 
                staffId, locationId);
        
        return updatedStaff;
    }

    @Override
    @Transactional
    public LocationStaff changeStaffRole(Long staffId, StaffRole newRole) {
        log.info("Changing role of staff member with ID: {} to: {}", staffId, newRole);
        
        return staffRepository.findById(staffId)
                .map(staff -> {
                    staff.setRole(newRole);
                    staff.setUpdatedAt(LocalDateTime.now());
                    
                    LocationStaff updatedStaff = staffRepository.save(staff);
                    log.info("Successfully changed role of staff member with ID: {} to: {}", 
                            staffId, newRole);
                    
                    return updatedStaff;
                })
                .orElseThrow(() -> {
                    log.error("Staff member with ID: {} not found", staffId);
                    return new IllegalArgumentException("Staff member not found with ID: " + staffId);
                });
    }

    @Override
    @Transactional
    public LocationStaff updateStaffStatus(Long staffId, boolean active) {
        log.info("Updating status of staff member with ID: {} to active: {}", staffId, active);
        
        return staffRepository.findById(staffId)
                .map(staff -> {
                    staff.setActive(active);
                    staff.setUpdatedAt(LocalDateTime.now());
                    
                    LocationStaff updatedStaff = staffRepository.save(staff);
                    log.info("Successfully updated status of staff member with ID: {} to active: {}", 
                            staffId, active);
                    
                    return updatedStaff;
                })
                .orElseThrow(() -> {
                    log.error("Staff member with ID: {} not found", staffId);
                    return new IllegalArgumentException("Staff member not found with ID: " + staffId);
                });
    }

    @Override
    public List<LocationStaff> getActiveStaff() {
        log.debug("Getting all active staff members");
        return staffRepository.findByActiveTrue();
    }

    @Override
    public List<LocationStaff> getInactiveStaff() {
        log.debug("Getting all inactive staff members");
        return staffRepository.findByActiveFalse();
    }

    @Override
    public List<LocationStaff> findStaffByName(String nameQuery) {
        log.debug("Finding staff members by name query: {}", nameQuery);
        return staffRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(nameQuery, nameQuery);
    }

    @Override
    public Optional<LocationStaff> findStaffByEmail(String email) {
        log.debug("Finding staff member by email: {}", email);
        return staffRepository.findByEmail(email);
    }

    @Override
    public Optional<LocationStaff> findStaffByPhone(String phone) {
        log.debug("Finding staff member by phone: {}", phone);
        return staffRepository.findByPhone(phone);
    }

    @Override
    public List<LocationStaff> getStaffByLocationAndRole(Long locationId, StaffRole role) {
        log.debug("Getting staff members for location with ID: {} and role: {}", locationId, role);
        return staffRepository.findByPhysicalLocationIdAndRole(locationId, role);
    }

    @Override
    public Map<StaffRole, Long> getStaffCountsByRole() {
        log.debug("Getting staff counts by role");
        
        Map<StaffRole, Long> countsByRole = new HashMap<>();
        for (StaffRole role : StaffRole.values()) {
            long count = staffRepository.countByRole(role);
            countsByRole.put(role, count);
        }
        
        return countsByRole;
    }

    @Override
    public Map<Long, Long> getStaffCountsByLocation() {
        log.debug("Getting staff counts by location");
        return staffRepository.findAll().stream()
                .filter(staff -> staff.getPhysicalLocation() != null)
                .collect(Collectors.groupingBy(
                        staff -> staff.getPhysicalLocation().getId(),
                        Collectors.counting()));
    }

    @Override
    @Transactional
    public void deactivateAllStaffAtLocation(Long locationId) {
        log.warn("Deactivating all staff members at location with ID: {}", locationId);
        
        List<LocationStaff> staffAtLocation = staffRepository.findByPhysicalLocationId(locationId);
        for (LocationStaff staff : staffAtLocation) {
            staff.setActive(false);
            staff.setUpdatedAt(LocalDateTime.now());
            staffRepository.save(staff);
        }
        
        log.info("Successfully deactivated {} staff members at location with ID: {}", 
                staffAtLocation.size(), locationId);
    }

    @Override
    public List<LocationStaff> getStaffCreatedAfter(LocalDateTime date) {
        log.debug("Getting staff members created after: {}", date);
        return staffRepository.findByCreatedAtAfter(date);
    }

    @Override
    public boolean existsByEmail(String email) {
        log.debug("Checking if staff member exists with email: {}", email);
        return staffRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByPhone(String phone) {
        log.debug("Checking if staff member exists with phone: {}", phone);
        return staffRepository.existsByPhone(phone);
    }
}
