package com.gogidix.courier.location.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.socialecommerceecosystem.location.model.LocationStaff;
import com.socialecommerceecosystem.location.model.StaffRole;

/**
 * Service interface for managing staff members at physical courier locations.
 * Provides high-level business functions for staff management.
 */
public interface StaffManagementService {
    
    /**
     * Get all staff members.
     * 
     * @return list of all staff members
     */
    List<LocationStaff> getAllStaff();
    
    /**
     * Get all staff members with pagination.
     * 
     * @param pageable pagination parameters
     * @return page of staff members
     */
    Page<LocationStaff> getAllStaff(Pageable pageable);
    
    /**
     * Get a staff member by ID.
     * 
     * @param staffId the ID of the staff member
     * @return optional containing the staff member if found
     */
    Optional<LocationStaff> getStaffById(Long staffId);
    
    /**
     * Get a staff member by employee ID.
     * 
     * @param employeeId the employee ID of the staff member
     * @return optional containing the staff member if found
     */
    Optional<LocationStaff> getStaffByEmployeeId(String employeeId);
    
    /**
     * Create a new staff member.
     * 
     * @param staff the staff member to create
     * @return the created staff member
     */
    LocationStaff createStaff(LocationStaff staff);
    
    /**
     * Update an existing staff member.
     * 
     * @param staffId the ID of the staff member to update
     * @param staff the updated staff member details
     * @return the updated staff member
     */
    LocationStaff updateStaff(Long staffId, LocationStaff staff);
    
    /**
     * Deactivate a staff member.
     * 
     * @param staffId the ID of the staff member to deactivate
     * @return the deactivated staff member
     */
    LocationStaff deactivateStaff(Long staffId);
    
    /**
     * Activate a staff member.
     * 
     * @param staffId the ID of the staff member to activate
     * @return the activated staff member
     */
    LocationStaff activateStaff(Long staffId);
    
    /**
     * Delete a staff member.
     * 
     * @param staffId the ID of the staff member to delete
     */
    void deleteStaff(Long staffId);
    
    /**
     * Get all active staff members.
     * 
     * @return list of active staff members
     */
    List<LocationStaff> getActiveStaff();
    
    /**
     * Get staff members by location.
     * 
     * @param locationId the ID of the location
     * @return list of staff members at the specified location
     */
    List<LocationStaff> getStaffByLocation(Long locationId);
    
    /**
     * Get active staff members by location.
     * 
     * @param locationId the ID of the location
     * @return list of active staff members at the specified location
     */
    List<LocationStaff> getActiveStaffByLocation(Long locationId);
    
    /**
     * Get staff members by role.
     * 
     * @param role the role of staff members to find
     * @return list of staff members with the specified role
     */
    List<LocationStaff> getStaffByRole(StaffRole role);
    
    /**
     * Get active staff members by role.
     * 
     * @param role the role of staff members to find
     * @return list of active staff members with the specified role
     */
    List<LocationStaff> getActiveStaffByRole(StaffRole role);
    
    /**
     * Get staff members by location and role.
     * 
     * @param locationId the ID of the location
     * @param role the role of staff members to find
     * @return list of staff members with the specified location and role
     */
    List<LocationStaff> getStaffByLocationAndRole(Long locationId, StaffRole role);
    
    /**
     * Search staff members by name.
     * 
     * @param nameQuery the name query to search for
     * @return list of staff members with names containing the query
     */
    List<LocationStaff> searchStaffByName(String nameQuery);
    
    /**
     * Find staff members assigned after a given date.
     * 
     * @param date the date after which staff were assigned
     * @return list of staff members assigned after the specified date
     */
    List<LocationStaff> findStaffAssignedAfter(LocalDateTime date);
    
    /**
     * Update the role of a staff member.
     * 
     * @param staffId the ID of the staff member
     * @param newRole the new role to assign
     * @return the updated staff member
     */
    LocationStaff updateStaffRole(Long staffId, StaffRole newRole);
    
    /**
     * End the assignment of a staff member.
     * 
     * @param staffId the ID of the staff member
     * @param endDateTime the date and time when the assignment ends
     * @return the updated staff member
     */
    LocationStaff endStaffAssignment(Long staffId, LocalDateTime endDateTime);
    
    /**
     * Reassign a staff member to a different location.
     * 
     * @param staffId the ID of the staff member
     * @param newLocationId the ID of the new location
     * @return the updated staff member
     */
    LocationStaff reassignStaff(Long staffId, Long newLocationId);
    
    /**
     * Get management staff by location.
     * 
     * @param locationId the ID of the location
     * @return list of management staff at the specified location
     */
    List<LocationStaff> getManagementStaffByLocation(Long locationId);
    
    /**
     * Get staff members who can handle payments at a location.
     * 
     * @param locationId the ID of the location
     * @return list of staff members who can handle payments
     */
    List<LocationStaff> getStaffWhoCanHandlePaymentsByLocation(Long locationId);
    
    /**
     * Get staff members who can access inventory at a location.
     * 
     * @param locationId the ID of the location
     * @return list of staff members who can access inventory
     */
    List<LocationStaff> getStaffWhoCanAccessInventoryByLocation(Long locationId);
    
    /**
     * Count staff members by location.
     * 
     * @return map of staff counts by location
     */
    Map<Long, Long> countStaffByLocation();
    
    /**
     * Count staff members by role.
     * 
     * @return map of staff counts by role
     */
    Map<StaffRole, Long> countStaffByRole();
    
    /**
     * Count active staff members by location.
     * 
     * @param locationId the ID of the location
     * @return the count of active staff members at the specified location
     */
    long countActiveStaffByLocation(Long locationId);
    
    /**
     * Check if a staff member exists by employee ID.
     * 
     * @param employeeId the employee ID to check
     * @return true if a staff member with the specified employee ID exists, false otherwise
     */
    boolean existsByEmployeeId(String employeeId);
    
    /**
     * Check if a staff member exists by email.
     * 
     * @param email the email to check
     * @return true if a staff member with the specified email exists, false otherwise
     */
    boolean existsByEmail(String email);
}
