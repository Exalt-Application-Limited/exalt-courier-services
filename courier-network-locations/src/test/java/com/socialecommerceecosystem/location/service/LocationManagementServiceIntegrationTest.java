package com.exalt.courier.location.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.socialecommerceecosystem.location.model.LocationOperatingHours;
import com.socialecommerceecosystem.location.model.LocationType;
import com.socialecommerceecosystem.location.model.PhysicalLocation;
import com.socialecommerceecosystem.location.repository.LocationOperatingHoursRepository;
import com.socialecommerceecosystem.location.repository.PhysicalLocationRepository;
import com.socialecommerceecosystem.location.service.impl.LocationManagementServiceImpl;

/**
 * Integration tests for LocationManagementService.
 * These tests verify that the service layer integrates correctly with repositories
 * and other dependent services.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class LocationManagementServiceIntegrationTest {

    @Autowired
    private LocationManagementService locationService;

    @MockBean
    private PhysicalLocationRepository locationRepository;

    @MockBean
    private LocationOperatingHoursRepository operatingHoursRepository;

    private PhysicalLocation testLocation;
    private LocationOperatingHours testOperatingHours;

    @BeforeEach
    void setUp() {
        // Create a test location
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
        testLocation.setLatitude(40.7128);
        testLocation.setLongitude(-74.0060);

        // Create test operating hours
        testOperatingHours = new LocationOperatingHours();
        testOperatingHours.setId(1L);
        testOperatingHours.setDayOfWeek(DayOfWeek.MONDAY);
        testOperatingHours.setOpenTime(LocalTime.of(9, 0));
        testOperatingHours.setCloseTime(LocalTime.of(17, 0));
        testOperatingHours.setPhysicalLocation(testLocation);
    }

    @Test
    @DisplayName("Should create location with operating hours")
    void testCreateLocationWithOperatingHours() {
        // Arrange
        PhysicalLocation locationToSave = new PhysicalLocation();
        locationToSave.setName("New Location");
        locationToSave.setLocationType(LocationType.BRANCH_OFFICE);
        locationToSave.setAddress("456 New Street");
        locationToSave.setCity("New City");
        locationToSave.setState("New State");
        locationToSave.setCountry("New Country");
        locationToSave.setZipCode("54321");
        locationToSave.setContactPhone("987-654-3210");
        locationToSave.setActive(true);
        
        List<LocationOperatingHours> operatingHours = Arrays.asList(
            createOperatingHours(DayOfWeek.MONDAY, "09:00", "17:00"),
            createOperatingHours(DayOfWeek.TUESDAY, "09:00", "17:00"),
            createOperatingHours(DayOfWeek.WEDNESDAY, "09:00", "17:00")
        );
        
        PhysicalLocation savedLocation = new PhysicalLocation();
        savedLocation.setId(2L);
        savedLocation.setName("New Location");
        // Copy other properties...
        
        when(locationRepository.save(any(PhysicalLocation.class))).thenReturn(savedLocation);
        when(operatingHoursRepository.saveAll(anyList())).thenReturn(operatingHours);
        
        // Act
        PhysicalLocation result = locationService.createLocation(locationToSave);
        
        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("New Location", result.getName());
        
        verify(locationRepository, times(1)).save(any(PhysicalLocation.class));
        // Verify operating hours are saved if they were present in the service implementation
        if (locationService instanceof LocationManagementServiceImpl) {
            // This would need to be adapted based on how your service handles operating hours
            verify(operatingHoursRepository, times(operatingHours.size())).save(any(LocationOperatingHours.class));
        }
    }

    @Test
    @DisplayName("Should retrieve all locations")
    void testGetAllLocations() {
        // Arrange
        List<PhysicalLocation> expectedLocations = Arrays.asList(testLocation);
        when(locationRepository.findAll()).thenReturn(expectedLocations);

        // Act
        List<PhysicalLocation> results = locationService.getAllLocations();

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Test Location", results.get(0).getName());
        
        verify(locationRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should retrieve paginated locations")
    void testGetPaginatedLocations() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<PhysicalLocation> locationList = Arrays.asList(testLocation);
        Page<PhysicalLocation> expectedPage = new PageImpl<>(locationList, pageable, locationList.size());
        
        when(locationRepository.findAll(pageable)).thenReturn(expectedPage);

        // Act
        Page<PhysicalLocation> resultPage = locationService.getAllLocations(pageable);

        // Assert
        assertNotNull(resultPage);
        assertEquals(1, resultPage.getTotalElements());
        assertEquals("Test Location", resultPage.getContent().get(0).getName());
        
        verify(locationRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Should retrieve location by ID when exists")
    void testGetLocationByIdExists() {
        // Arrange
        when(locationRepository.findById(1L)).thenReturn(Optional.of(testLocation));

        // Act
        Optional<PhysicalLocation> result = locationService.getLocationById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Test Location", result.get().getName());
        
        verify(locationRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return empty when location ID doesn't exist")
    void testGetLocationByIdNotExists() {
        // Arrange
        when(locationRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<PhysicalLocation> result = locationService.getLocationById(999L);

        // Assert
        assertFalse(result.isPresent());
        
        verify(locationRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should update location successfully")
    void testUpdateLocation() {
        // Arrange
        PhysicalLocation updatedLocation = new PhysicalLocation();
        updatedLocation.setName("Updated Location");
        updatedLocation.setLocationType(LocationType.BRANCH_OFFICE);
        updatedLocation.setAddress("Updated Address");
        updatedLocation.setActive(true);
        
        when(locationRepository.findById(1L)).thenReturn(Optional.of(testLocation));
        when(locationRepository.save(any(PhysicalLocation.class))).thenReturn(updatedLocation);

        // Act
        PhysicalLocation result = locationService.updateLocation(1L, updatedLocation);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Location", result.getName());
        assertEquals("Updated Address", result.getAddress());
        
        verify(locationRepository, times(1)).findById(1L);
        verify(locationRepository, times(1)).save(any(PhysicalLocation.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent location")
    void testUpdateLocationNotFound() {
        // Arrange
        PhysicalLocation updatedLocation = new PhysicalLocation();
        updatedLocation.setName("Updated Location");
        
        when(locationRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            locationService.updateLocation(999L, updatedLocation);
        });
        
        assertTrue(exception.getMessage().contains("not found"));
        verify(locationRepository, times(1)).findById(999L);
        verify(locationRepository, never()).save(any(PhysicalLocation.class));
    }

    @Test
    @DisplayName("Should delete location")
    void testDeleteLocation() {
        // Arrange
        doNothing().when(locationRepository).deleteById(1L);

        // Act
        locationService.deleteLocation(1L);

        // Assert
        verify(locationRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should find locations by city")
    void testFindLocationsByCity() {
        // Arrange
        List<PhysicalLocation> expectedLocations = Arrays.asList(testLocation);
        when(locationRepository.findByCity("Test City")).thenReturn(expectedLocations);

        // Act
        List<PhysicalLocation> results = locationService.findLocationsByCity("Test City");

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Test City", results.get(0).getCity());
        
        verify(locationRepository, times(1)).findByCity("Test City");
    }

    @Test
    @DisplayName("Should find active locations")
    void testFindActiveLocations() {
        // Arrange
        List<PhysicalLocation> expectedLocations = Arrays.asList(testLocation);
        when(locationRepository.findByActiveTrue()).thenReturn(expectedLocations);

        // Act
        List<PhysicalLocation> results = locationService.findActiveLocations();

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertTrue(results.get(0).isActive());
        
        verify(locationRepository, times(1)).findByActiveTrue();
    }

    @Test
    @DisplayName("Should find locations by type")
    void testFindLocationsByType() {
        // Arrange
        List<PhysicalLocation> expectedLocations = Arrays.asList(testLocation);
        when(locationRepository.findByLocationType(LocationType.BRANCH_OFFICE)).thenReturn(expectedLocations);

        // Act
        List<PhysicalLocation> results = locationService.findLocationsByType(LocationType.BRANCH_OFFICE);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(LocationType.BRANCH_OFFICE, results.get(0).getLocationType());
        
        verify(locationRepository, times(1)).findByLocationType(LocationType.BRANCH_OFFICE);
    }

    // Helper method to create operating hours
    private LocationOperatingHours createOperatingHours(DayOfWeek day, String open, String close) {
        LocationOperatingHours hours = new LocationOperatingHours();
        hours.setDayOfWeek(day);
        hours.setOpenTime(LocalTime.parse(open));
        hours.setCloseTime(LocalTime.parse(close));
        return hours;
    }
}
