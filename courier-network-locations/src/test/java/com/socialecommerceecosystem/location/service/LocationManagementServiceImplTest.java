package com.gogidix.courier.location.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.socialecommerceecosystem.location.model.LocationOperatingHours;
import com.socialecommerceecosystem.location.model.LocationType;
import com.socialecommerceecosystem.location.model.PhysicalLocation;
import com.socialecommerceecosystem.location.repository.LocationOperatingHoursRepository;
import com.socialecommerceecosystem.location.repository.PhysicalLocationRepository;
import com.socialecommerceecosystem.location.service.impl.LocationManagementServiceImpl;

/**
 * Unit tests for the LocationManagementServiceImpl class.
 */
public class LocationManagementServiceImplTest {

    @Mock
    private PhysicalLocationRepository locationRepository;

    @Mock
    private LocationOperatingHoursRepository operatingHoursRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private LocationManagementServiceImpl locationService;

    private PhysicalLocation testLocation;
    private LocationOperatingHours testOperatingHours;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

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
    @DisplayName("Should get all locations")
    void testGetAllLocations() {
        // Arrange
        List<PhysicalLocation> expectedLocations = Arrays.asList(testLocation);
        when(locationRepository.findAll()).thenReturn(expectedLocations);

        // Act
        List<PhysicalLocation> actualLocations = locationService.getAllLocations();

        // Assert
        assertEquals(expectedLocations, actualLocations);
        verify(locationRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should get all locations with pagination")
    void testGetAllLocationsWithPagination() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<PhysicalLocation> expectedLocationsList = Arrays.asList(testLocation);
        Page<PhysicalLocation> expectedLocations = new PageImpl<>(expectedLocationsList, pageable, 1);
        when(locationRepository.findAll(pageable)).thenReturn(expectedLocations);

        // Act
        Page<PhysicalLocation> actualLocations = locationService.getAllLocations(pageable);

        // Assert
        assertEquals(expectedLocations, actualLocations);
        verify(locationRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Should get location by ID when exists")
    void testGetLocationByIdWhenExists() {
        // Arrange
        when(locationRepository.findById(1L)).thenReturn(Optional.of(testLocation));

        // Act
        Optional<PhysicalLocation> actualLocation = locationService.getLocationById(1L);

        // Assert
        assertTrue(actualLocation.isPresent());
        assertEquals(testLocation, actualLocation.get());
        verify(locationRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return empty optional when location doesn't exist")
    void testGetLocationByIdWhenNotExists() {
        // Arrange
        when(locationRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<PhysicalLocation> actualLocation = locationService.getLocationById(999L);

        // Assert
        assertFalse(actualLocation.isPresent());
        verify(locationRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should create new location")
    void testCreateLocation() {
        // Arrange
        when(locationRepository.save(any(PhysicalLocation.class))).thenReturn(testLocation);

        // Act
        PhysicalLocation createdLocation = locationService.createLocation(testLocation);

        // Assert
        assertEquals(testLocation, createdLocation);
        verify(locationRepository, times(1)).save(testLocation);
    }

    @Test
    @DisplayName("Should update existing location")
    void testUpdateLocation() {
        // Arrange
        PhysicalLocation updatedLocation = new PhysicalLocation();
        updatedLocation.setName("Updated Test Location");
        
        when(locationRepository.findById(1L)).thenReturn(Optional.of(testLocation));
        when(locationRepository.save(any(PhysicalLocation.class))).thenReturn(updatedLocation);

        // Act
        PhysicalLocation result = locationService.updateLocation(1L, updatedLocation);

        // Assert
        assertEquals(updatedLocation, result);
        verify(locationRepository, times(1)).findById(1L);
        verify(locationRepository, times(1)).save(any(PhysicalLocation.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent location")
    void testUpdateLocationNotFound() {
        // Arrange
        when(locationRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            locationService.updateLocation(999L, new PhysicalLocation());
        });
        verify(locationRepository, times(1)).findById(999L);
        verify(locationRepository, never()).save(any(PhysicalLocation.class));
    }

    @Test
    @DisplayName("Should delete location when exists")
    void testDeleteLocationWhenExists() {
        // Arrange
        when(locationRepository.existsById(1L)).thenReturn(true);
        doNothing().when(locationRepository).deleteById(1L);

        // Act
        assertDoesNotThrow(() -> locationService.deleteLocation(1L));

        // Assert
        verify(locationRepository, times(1)).existsById(1L);
        verify(locationRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent location")
    void testDeleteLocationNotFound() {
        // Arrange
        when(locationRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            locationService.deleteLocation(999L);
        });
        verify(locationRepository, times(1)).existsById(999L);
        verify(locationRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should get locations by type")
    void testGetLocationsByType() {
        // Arrange
        List<PhysicalLocation> expectedLocations = Arrays.asList(testLocation);
        when(locationRepository.findByLocationType(LocationType.BRANCH_OFFICE))
                .thenReturn(expectedLocations);

        // Act
        List<PhysicalLocation> actualLocations = locationService.getLocationsByType(LocationType.BRANCH_OFFICE);

        // Assert
        assertEquals(expectedLocations, actualLocations);
        verify(locationRepository, times(1)).findByLocationType(LocationType.BRANCH_OFFICE);
    }

    @Test
    @DisplayName("Should check if location is open")
    void testIsLocationOpen() {
        // Arrange
        LocalDateTime dateTime = LocalDateTime.of(2025, 5, 12, 10, 0); // Monday at 10:00 AM
        DayOfWeek dayOfWeek = dateTime.getDayOfWeek(); // MONDAY
        
        when(operatingHoursRepository.findByPhysicalLocationIdAndDayOfWeek(1L, dayOfWeek))
                .thenReturn(Optional.of(testOperatingHours));

        // Act
        boolean isOpen = locationService.isLocationOpen(1L, dateTime);

        // Assert
        assertTrue(isOpen);
        verify(operatingHoursRepository, times(1))
                .findByPhysicalLocationIdAndDayOfWeek(1L, dayOfWeek);
    }

    @Test
    @DisplayName("Should find nearby locations")
    void testFindNearbyLocations() {
        // Arrange
        double testLat = 40.7128;
        double testLon = -74.0060;
        double radiusKm = 10.0;
        
        // Calculate approximate degree distances for the test radius
        double latDelta = radiusKm / 111.0;
        double lonDelta = radiusKm / (111.0 * Math.cos(Math.toRadians(testLat)));
        
        List<PhysicalLocation> expectedLocations = Arrays.asList(testLocation);
        
        when(locationRepository.findByLatitudeBetweenAndLongitudeBetween(
                anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(expectedLocations);

        // Act
        List<PhysicalLocation> actualLocations = locationService.findNearbyLocations(
                testLat, testLon, radiusKm);

        // Assert
        assertEquals(expectedLocations, actualLocations);
        verify(locationRepository, times(1)).findByLatitudeBetweenAndLongitudeBetween(
                anyDouble(), anyDouble(), anyDouble(), anyDouble());
    }

    @Test
    @DisplayName("Should add operating hours to location")
    void testAddOperatingHours() {
        // Arrange
        when(locationRepository.findById(1L)).thenReturn(Optional.of(testLocation));
        when(operatingHoursRepository.save(any(LocationOperatingHours.class))).thenReturn(testOperatingHours);

        // Act
        LocationOperatingHours result = locationService.addOperatingHours(1L, testOperatingHours);

        // Assert
        assertEquals(testOperatingHours, result);
        verify(locationRepository, times(1)).findById(1L);
        verify(operatingHoursRepository, times(1)).save(testOperatingHours);
    }

    @Test
    @DisplayName("Should throw exception when adding operating hours to non-existent location")
    void testAddOperatingHoursLocationNotFound() {
        // Arrange
        when(locationRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            locationService.addOperatingHours(999L, testOperatingHours);
        });
        verify(locationRepository, times(1)).findById(999L);
        verify(operatingHoursRepository, never()).save(any(LocationOperatingHours.class));
    }

    @Test
    @DisplayName("Should update capacity utilization and notify when threshold exceeded")
    void testUpdateCapacityUtilization() {
        // Arrange
        double capacityUtilization = 90.0; // Above warning threshold
        
        when(locationRepository.findById(1L)).thenReturn(Optional.of(testLocation));
        when(locationRepository.save(any(PhysicalLocation.class))).thenReturn(testLocation);
        doNothing().when(notificationService).notifyStaffAboutHighCapacityUtilization(1L, capacityUtilization);

        // Act
        locationService.updateCapacityUtilization(1L, capacityUtilization);

        // Assert
        verify(locationRepository, times(1)).findById(1L);
        verify(locationRepository, times(1)).save(any(PhysicalLocation.class));
        verify(notificationService, times(1)).notifyStaffAboutHighCapacityUtilization(1L, capacityUtilization);
    }

    @Test
    @DisplayName("Should not notify when capacity utilization below threshold")
    void testUpdateCapacityUtilizationBelowThreshold() {
        // Arrange
        double capacityUtilization = 70.0; // Below warning threshold
        
        when(locationRepository.findById(1L)).thenReturn(Optional.of(testLocation));
        when(locationRepository.save(any(PhysicalLocation.class))).thenReturn(testLocation);

        // Act
        locationService.updateCapacityUtilization(1L, capacityUtilization);

        // Assert
        verify(locationRepository, times(1)).findById(1L);
        verify(locationRepository, times(1)).save(any(PhysicalLocation.class));
        verify(notificationService, never()).notifyStaffAboutHighCapacityUtilization(anyLong(), anyDouble());
    }
}
