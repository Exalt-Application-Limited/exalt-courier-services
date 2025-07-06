package com.gogidix.courier.location.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.socialecommerceecosystem.location.model.LocationStaff;
import com.socialecommerceecosystem.location.model.StaffRole;

/**
 * Repository interface for LocationStaff entity.
 * Provides methods for accessing and querying staff data.
 */
@Repository
public interface LocationStaffRepository extends JpaRepository<LocationStaff, Long> {
    
    /**
     * Find staff members by physical location ID.
     * 
     * @param physicalLocationId the ID of the physical location
     * @return list of staff members at the specified location
     */
    List<LocationStaff> findByPhysicalLocationId(Long physicalLocationId);
    
    /**
     * Find active staff members by physical location ID.
     * 
     * @param physicalLocationId the ID of the physical location
     * @return list of active staff members at the specified location
     */
    List<LocationStaff> findByPhysicalLocationIdAndActiveTrue(Long physicalLocationId);
    
    /**
     * Find staff members by physical location ID with pagination.
     * 
     * @param physicalLocationId the ID of the physical location
     * @param pageable pagination parameters
     * @return page of staff members at the specified location
     */
    Page<LocationStaff> findByPhysicalLocationId(Long physicalLocationId, Pageable pageable);
    
    /**
     * Find staff members by role.
     * 
     * @param role the staff role to search for
     * @return list of staff members with the specified role
     */
    List<LocationStaff> findByRole(StaffRole role);
    
    /**
     * Find active staff members by role.
     * 
     * @param role the staff role to search for
     * @param active the active status to match
     * @return list of active staff members with the specified role
     */
    List<LocationStaff> findByRoleAndActive(StaffRole role, boolean active);
    
    /**
     * Find staff member by employee ID.
     * 
     * @param employeeId the employee ID to search for
     * @return optional staff member with the specified employee ID
     */
    Optional<LocationStaff> findByEmployeeId(String employeeId);
    
    /**
     * Find staff members by email.
     * 
     * @param email the email to search for
     * @return optional staff member with the specified email
     */
    Optional<LocationStaff> findByEmail(String email);
    
    /**
     * Search staff members by name.
     * 
     * @param firstName the first name to search for (partial match)
     * @param lastName the last name to search for (partial match)
     * @return list of staff members matching the name criteria
     */
    List<LocationStaff> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String firstName, String lastName);
    
    /**
     * Find staff members assigned to any location after a given date.
     * 
     * @param date the date after which staff were assigned
     * @return list of staff members assigned after the specified date
     */
    List<LocationStaff> findByAssignmentDateAfter(LocalDateTime date);
    
    /**
     * Find staff members whose assignments ended in a given period.
     * 
     * @param startDate the start of the period
     * @param endDate the end of the period
     * @return list of staff members whose assignments ended in the specified period
     */
    List<LocationStaff> findByEndDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Count active staff members by physical location ID.
     * 
     * @param physicalLocationId the ID of the physical location
     * @return the count of active staff members at the specified location
     */
    long countByPhysicalLocationIdAndActiveTrue(Long physicalLocationId);
    
    /**
     * Count staff members by role and physical location.
     * 
     * @param role the staff role to count
     * @param physicalLocationId the ID of the physical location
     * @return the count of staff members with the specified role at the specified location
     */
    long countByRoleAndPhysicalLocationId(StaffRole role, Long physicalLocationId);
    
    /**
     * Find management staff by physical location ID.
     * 
     * @param physicalLocationId the ID of the physical location
     * @return list of management staff at the specified location
     */
    @Query("SELECT s FROM LocationStaff s WHERE s.physicalLocation.id = :locationId " +
           "AND (s.role = 'LOCATION_MANAGER' OR s.role = 'ASSISTANT_MANAGER') AND s.active = true")
    List<LocationStaff> findManagementStaffByLocationId(@Param("locationId") Long physicalLocationId);
    
    /**
     * Find staff members who can handle payments at a specific location.
     * 
     * @param physicalLocationId the ID of the physical location
     * @return list of staff members who can handle payments
     */
    @Query("SELECT s FROM LocationStaff s WHERE s.physicalLocation.id = :locationId " +
           "AND s.role IN ('LOCATION_MANAGER', 'ASSISTANT_MANAGER', 'CUSTOMER_SERVICE_REPRESENTATIVE', 'ADMINISTRATIVE_STAFF') " +
           "AND s.active = true")
    List<LocationStaff> findStaffWhoCanHandlePaymentsByLocationId(@Param("locationId") Long physicalLocationId);
    
    /**
     * Find staff members who can access inventory at a specific location.
     * 
     * @param physicalLocationId the ID of the physical location
     * @return list of staff members who can access inventory
     */
    @Query("SELECT s FROM LocationStaff s WHERE s.physicalLocation.id = :locationId " +
           "AND s.role IN ('LOCATION_MANAGER', 'ASSISTANT_MANAGER', 'LOGISTICS_OFFICER', 'TECHNICAL_SUPPORT') " +
           "AND s.active = true")
    List<LocationStaff> findStaffWhoCanAccessInventoryByLocationId(@Param("locationId") Long physicalLocationId);
    
    /**
     * Delete all staff by physical location ID.
     * 
     * @param physicalLocationId the ID of the physical location
     */
    void deleteByPhysicalLocationId(Long physicalLocationId);
}
