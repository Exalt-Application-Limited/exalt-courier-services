package com.exalt.courier.location.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.socialecommerceecosystem.location.model.LocationStaff;
import com.socialecommerceecosystem.location.model.LocationType;
import com.socialecommerceecosystem.location.model.PhysicalLocation;
import com.socialecommerceecosystem.location.model.StaffRole;
import com.socialecommerceecosystem.location.repository.LocationStaffRepository;
import com.socialecommerceecosystem.location.repository.PhysicalLocationRepository;

/**
 * Integration tests for StaffManagementService.
 * These tests verify that the service layer integrates correctly with repositories
 * and other dependent services.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class StaffManagementServiceIntegrationTest {

    @Autowired
    private StaffManagementService staffService;

    @MockBean
    private LocationStaffRepository staffRepository;

    @MockBean
    private PhysicalLocationRepository locationRepository;

    @MockBean
    private NotificationService notificationService;

    private PhysicalLocation testLocation;
    private LocationStaff testStaff;

    @BeforeEach
    void setUp() {
        // Create test location
        testLocation = new PhysicalLocation();
        testLocation.setId(1L);
        testLocation.setName("Test Location");
        testLocation.setLocationType(LocationType.BRANCH_OFFICE);
        testLocation.setAddress("123 Test Street");
        testLocation.setCity("Test City");
        testLocation.setState("Test State");
        testLocation.setCountry("Test Country");
        testLocation.setZipCode("12345");
        testLocation.setContactPhone("123-456-7890");
        testLocation.setActive(true);

        // Create test staff
        testStaff = new LocationStaff();
        testStaff.setId(1L);
        testStaff.setName("Test Staff");
        testStaff.setEmail("staff@example.com");
        testStaff.setPhone("555-4321");
        testStaff.setRole(StaffRole.MANAGER);
        testStaff.setLocation(testLocation);
        testStaff.setActive(true);
    }

    @Test
    @DisplayName("Should retrieve all staff")
    void testGetAllStaff() {
        // Arrange
        List<LocationStaff> expectedStaff = Arrays.asList(testStaff);
        when(staffRepository.findAll()).thenReturn(expectedStaff);

        // Act
        List<LocationStaff> results = staffService.getAllStaff();

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Test Staff", results.get(0).getName());
        
        verify(staffRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should retrieve staff by ID")
    void testGetStaffById() {
        // Arrange
        when(staffRepository.findById(1L)).thenReturn(Optional.of(testStaff));

        // Act
        Optional<LocationStaff> result = staffService.getStaffById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Test Staff", result.get().getName());
        
        verify(staffRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should create new staff")
    void testCreateStaff() {
        // Arrange
        LocationStaff staffToCreate = new LocationStaff();
        staffToCreate.setName("New Staff");
        staffToCreate.setEmail("newstaff@example.com");
        staffToCreate.setPhone("555-8765");
        staffToCreate.setRole(StaffRole.CLERK);
        staffToCreate.setLocation(testLocation);
        staffToCreate.setActive(true);
        
        when(locationRepository.findById(1L)).thenReturn(Optional.of(testLocation));
        when(staffRepository.save(any(LocationStaff.class))).thenReturn(staffToCreate);

        // Act
        LocationStaff result = staffService.createStaff(staffToCreate);

        // Assert
        assertNotNull(result);
        assertEquals("New Staff", result.getName());
        assertEquals(StaffRole.CLERK, result.getRole());
        
        verify(staffRepository, times(1)).save(any(LocationStaff.class));
    }

    @Test
    @DisplayName("Should update staff")
    void testUpdateStaff() {
        // Arrange
        LocationStaff updatedStaff = new LocationStaff();
        updatedStaff.setId(1L);
        updatedStaff.setName("Updated Staff");
        updatedStaff.setEmail("updatedstaff@example.com");
        updatedStaff.setPhone("555-9876");
        updatedStaff.setRole(StaffRole.SUPERVISOR);
        updatedStaff.setLocation(testLocation);
        updatedStaff.setActive(true);
        
        when(staffRepository.findById(1L)).thenReturn(Optional.of(testStaff));
        when(staffRepository.save(any(LocationStaff.class))).thenReturn(updatedStaff);

        // Act
        LocationStaff result = staffService.updateStaff(1L, updatedStaff);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Staff", result.getName());
        assertEquals(StaffRole.SUPERVISOR, result.getRole());
        
        verify(staffRepository, times(1)).findById(1L);
        verify(staffRepository, times(1)).save(any(LocationStaff.class));
    }

    @Test
    @DisplayName("Should delete staff")
    void testDeleteStaff() {
        // Arrange
        doNothing().when(staffRepository).deleteById(1L);

        // Act
        staffService.deleteStaff(1L);

        // Assert
        verify(staffRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should find staff by location")
    void testFindStaffByLocation() {
        // Arrange
        List<LocationStaff> expectedStaff = Arrays.asList(testStaff);
        when(staffRepository.findByLocationId(1L)).thenReturn(expectedStaff);

        // Act
        List<LocationStaff> results = staffService.findStaffByLocation(1L);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(1L, results.get(0).getLocation().getId());
        
        verify(staffRepository, times(1)).findByLocationId(1L);
    }

    @Test
    @DisplayName("Should find staff by role")
    void testFindStaffByRole() {
        // Arrange
        List<LocationStaff> expectedStaff = Arrays.asList(testStaff);
        when(staffRepository.findByRole(StaffRole.MANAGER)).thenReturn(expectedStaff);

        // Act
        List<LocationStaff> results = staffService.findStaffByRole(StaffRole.MANAGER);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(StaffRole.MANAGER, results.get(0).getRole());
        
        verify(staffRepository, times(1)).findByRole(StaffRole.MANAGER);
    }

    @Test
    @DisplayName("Should find active staff")
    void testFindActiveStaff() {
        // Arrange
        List<LocationStaff> expectedStaff = Arrays.asList(testStaff);
        when(staffRepository.findByActiveTrue()).thenReturn(expectedStaff);

        // Act
        List<LocationStaff> results = staffService.findActiveStaff();

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertTrue(results.get(0).isActive());
        
        verify(staffRepository, times(1)).findByActiveTrue();
    }

    @Test
    @DisplayName("Should find staff by email")
    void testFindStaffByEmail() {
        // Arrange
        when(staffRepository.findByEmail("staff@example.com")).thenReturn(Optional.of(testStaff));

        // Act
        Optional<LocationStaff> result = staffService.findStaffByEmail("staff@example.com");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("staff@example.com", result.get().getEmail());
        
        verify(staffRepository, times(1)).findByEmail("staff@example.com");
    }

    @Test
    @DisplayName("Should update staff status")
    void testUpdateStaffStatus() {
        // Arrange
        boolean newStatus = false;
        
        LocationStaff inactiveStaff = new LocationStaff();
        inactiveStaff.setId(1L);
        inactiveStaff.setName("Test Staff");
        inactiveStaff.setActive(false);
        
        when(staffRepository.findById(1L)).thenReturn(Optional.of(testStaff));
        when(staffRepository.save(any(LocationStaff.class))).thenReturn(inactiveStaff);

        // Act
        LocationStaff result = staffService.updateStaffStatus(1L, newStatus);

        // Assert
        assertNotNull(result);
        assertFalse(result.isActive());
        
        verify(staffRepository, times(1)).findById(1L);
        verify(staffRepository, times(1)).save(any(LocationStaff.class));
    }

    @Test
    @DisplayName("Should reassign staff to new location")
    void testReassignStaffLocation() {
        // Arrange
        PhysicalLocation newLocation = new PhysicalLocation();
        newLocation.setId(2L);
        newLocation.setName("New Location");
        
        LocationStaff reassignedStaff = new LocationStaff();
        reassignedStaff.setId(1L);
        reassignedStaff.setName("Test Staff");
        reassignedStaff.setLocation(newLocation);
        
        when(staffRepository.findById(1L)).thenReturn(Optional.of(testStaff));
        when(locationRepository.findById(2L)).thenReturn(Optional.of(newLocation));
        when(staffRepository.save(any(LocationStaff.class))).thenReturn(reassignedStaff);

        // Act
        LocationStaff result = staffService.reassignStaffLocation(1L, 2L);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getLocation().getId());
        assertEquals("New Location", result.getLocation().getName());
        
        verify(staffRepository, times(1)).findById(1L);
        verify(locationRepository, times(1)).findById(2L);
        verify(staffRepository, times(1)).save(any(LocationStaff.class));
    }
}
