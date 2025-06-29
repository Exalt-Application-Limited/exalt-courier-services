package com.exalt.courier.location.controller;

import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.socialecommerceecosystem.location.model.LocationStaff;
import com.socialecommerceecosystem.location.model.StaffRole;
import com.socialecommerceecosystem.location.service.StaffManagementService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for staff management operations in the courier network.
 * Provides endpoints for CRUD operations on staff members and role assignments.
 */
@RestController
@RequestMapping("/api/staff")
@Tag(name = "Staff Management", description = "API for managing staff members in the courier network locations")
@Slf4j
public class StaffController {

    private final StaffManagementService staffService;

    @Autowired
    public StaffController(StaffManagementService staffService) {
        this.staffService = staffService;
    }

    @GetMapping
    @Operation(summary = "Get all staff members", description = "Retrieves all staff members in the courier network")
    @ApiResponse(responseCode = "200", description = "Staff members retrieved successfully", 
            content = @Content(schema = @Schema(implementation = LocationStaff.class)))
    public ResponseEntity<List<LocationStaff>> getAllStaff() {
        log.debug("REST request to get all staff members");
        List<LocationStaff> staff = staffService.getAllStaff();
        return ResponseEntity.ok(staff);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get staff member by ID", description = "Retrieves a specific staff member by ID")
    @ApiResponse(responseCode = "200", description = "Staff member found", 
            content = @Content(schema = @Schema(implementation = LocationStaff.class)))
    @ApiResponse(responseCode = "404", description = "Staff member not found")
    public ResponseEntity<LocationStaff> getStaffById(
            @Parameter(description = "ID of the staff member", required = true) @PathVariable Long id) {
        log.debug("REST request to get staff member with ID: {}", id);
        return staffService.getStaffById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a new staff member", description = "Creates a new staff member")
    @ApiResponse(responseCode = "201", description = "Staff member created successfully", 
            content = @Content(schema = @Schema(implementation = LocationStaff.class)))
    public ResponseEntity<LocationStaff> createStaff(
            @Parameter(description = "Staff member details", required = true) 
            @Valid @RequestBody LocationStaff staff) {
        log.debug("REST request to create a new staff member: {}", staff.getName());
        LocationStaff createdStaff = staffService.createStaff(staff);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStaff);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a staff member", description = "Updates an existing staff member")
    @ApiResponse(responseCode = "200", description = "Staff member updated successfully", 
            content = @Content(schema = @Schema(implementation = LocationStaff.class)))
    @ApiResponse(responseCode = "404", description = "Staff member not found")
    public ResponseEntity<LocationStaff> updateStaff(
            @Parameter(description = "ID of the staff member", required = true) @PathVariable Long id,
            @Parameter(description = "Updated staff member details", required = true) 
            @Valid @RequestBody LocationStaff staff) {
        log.debug("REST request to update staff member with ID: {}", id);
        try {
            LocationStaff updatedStaff = staffService.updateStaff(id, staff);
            return ResponseEntity.ok(updatedStaff);
        } catch (IllegalArgumentException e) {
            log.error("Staff member not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a staff member", description = "Deletes a staff member")
    @ApiResponse(responseCode = "204", description = "Staff member deleted successfully")
    @ApiResponse(responseCode = "404", description = "Staff member not found")
    public ResponseEntity<Void> deleteStaff(
            @Parameter(description = "ID of the staff member", required = true) @PathVariable Long id) {
        log.debug("REST request to delete staff member with ID: {}", id);
        try {
            staffService.deleteStaff(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("Staff member not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/location/{locationId}")
    @Operation(summary = "Get staff by location", 
            description = "Retrieves all staff members assigned to a specific location")
    public ResponseEntity<List<LocationStaff>> getStaffByLocation(
            @Parameter(description = "ID of the location", required = true) @PathVariable Long locationId) {
        log.debug("REST request to get staff members assigned to location with ID: {}", locationId);
        List<LocationStaff> staff = staffService.getStaffByLocation(locationId);
        return ResponseEntity.ok(staff);
    }

    @GetMapping("/role/{role}")
    @Operation(summary = "Get staff by role", 
            description = "Retrieves all staff members with a specific role")
    public ResponseEntity<List<LocationStaff>> getStaffByRole(
            @Parameter(description = "Staff role", required = true) @PathVariable StaffRole role) {
        log.debug("REST request to get staff members with role: {}", role);
        List<LocationStaff> staff = staffService.getStaffByRole(role);
        return ResponseEntity.ok(staff);
    }

    @PutMapping("/{id}/assign")
    @Operation(summary = "Assign staff to location", 
            description = "Assigns a staff member to a specific location")
    @ApiResponse(responseCode = "200", description = "Staff member assigned successfully", 
            content = @Content(schema = @Schema(implementation = LocationStaff.class)))
    @ApiResponse(responseCode = "404", description = "Staff member or location not found")
    public ResponseEntity<LocationStaff> assignStaffToLocation(
            @Parameter(description = "ID of the staff member", required = true) @PathVariable Long id,
            @Parameter(description = "ID of the location", required = true) @RequestParam Long locationId) {
        log.debug("REST request to assign staff member with ID: {} to location with ID: {}", 
                id, locationId);
        try {
            LocationStaff updatedStaff = staffService.assignStaffToLocation(id, locationId);
            return ResponseEntity.ok(updatedStaff);
        } catch (IllegalArgumentException e) {
            log.error("Failed to assign staff: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/role")
    @Operation(summary = "Update staff role", description = "Updates the role of a staff member")
    @ApiResponse(responseCode = "200", description = "Staff role updated successfully", 
            content = @Content(schema = @Schema(implementation = LocationStaff.class)))
    @ApiResponse(responseCode = "404", description = "Staff member not found")
    public ResponseEntity<LocationStaff> updateStaffRole(
            @Parameter(description = "ID of the staff member", required = true) @PathVariable Long id,
            @Parameter(description = "New role", required = true) @RequestParam StaffRole role) {
        log.debug("REST request to update role of staff member with ID: {} to: {}", id, role);
        try {
            LocationStaff updatedStaff = staffService.updateStaffRole(id, role);
            return ResponseEntity.ok(updatedStaff);
        } catch (IllegalArgumentException e) {
            log.error("Staff member not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update staff status", description = "Updates the active status of a staff member")
    @ApiResponse(responseCode = "200", description = "Staff status updated successfully", 
            content = @Content(schema = @Schema(implementation = LocationStaff.class)))
    @ApiResponse(responseCode = "404", description = "Staff member not found")
    public ResponseEntity<LocationStaff> updateStaffStatus(
            @Parameter(description = "ID of the staff member", required = true) @PathVariable Long id,
            @Parameter(description = "Active status", required = true) @RequestParam boolean active) {
        log.debug("REST request to update status of staff member with ID: {} to active: {}", id, active);
        try {
            LocationStaff updatedStaff = staffService.updateStaffStatus(id, active);
            return ResponseEntity.ok(updatedStaff);
        } catch (IllegalArgumentException e) {
            log.error("Staff member not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Search staff members", 
            description = "Searches for staff members by name or employee ID")
    public ResponseEntity<List<LocationStaff>> searchStaff(
            @Parameter(description = "Search query") @RequestParam String query) {
        log.debug("REST request to search for staff members with query: {}", query);
        List<LocationStaff> staff = staffService.searchStaff(query);
        return ResponseEntity.ok(staff);
    }

    @GetMapping("/counts/by-role")
    @Operation(summary = "Get staff counts by role", 
            description = "Retrieves the count of staff members by role")
    public ResponseEntity<Map<StaffRole, Long>> getStaffCountsByRole() {
        log.debug("REST request to get staff counts by role");
        Map<StaffRole, Long> counts = staffService.getStaffCountsByRole();
        return ResponseEntity.ok(counts);
    }

    @GetMapping("/counts/by-location")
    @Operation(summary = "Get staff counts by location", 
            description = "Retrieves the count of staff members by location")
    public ResponseEntity<Map<Long, Long>> getStaffCountsByLocation() {
        log.debug("REST request to get staff counts by location");
        Map<Long, Long> counts = staffService.getStaffCountsByLocation();
        return ResponseEntity.ok(counts);
    }

    @GetMapping("/active")
    @Operation(summary = "Get active staff members", 
            description = "Retrieves all active staff members")
    public ResponseEntity<List<LocationStaff>> getActiveStaff() {
        log.debug("REST request to get active staff members");
        List<LocationStaff> staff = staffService.getActiveStaff();
        return ResponseEntity.ok(staff);
    }

    @GetMapping("/schedule-conflicts")
    @Operation(summary = "Check for schedule conflicts", 
            description = "Checks for staff scheduling conflicts at a location")
    public ResponseEntity<List<Map<String, Object>>> checkForScheduleConflicts(
            @Parameter(description = "ID of the location", required = true) @RequestParam Long locationId,
            @Parameter(description = "Start date (ISO format)", required = true) @RequestParam String startDate,
            @Parameter(description = "End date (ISO format)", required = true) @RequestParam String endDate) {
        log.debug("REST request to check for schedule conflicts at location ID: {} between {} and {}", 
                locationId, startDate, endDate);
        
        java.time.LocalDate start = java.time.LocalDate.parse(startDate);
        java.time.LocalDate end = java.time.LocalDate.parse(endDate);
        
        List<Map<String, Object>> conflicts = staffService.checkForScheduleConflicts(locationId, start, end);
        return ResponseEntity.ok(conflicts);
    }
}
