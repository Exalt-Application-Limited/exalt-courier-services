package com.gogidix.courier.location.controller;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.socialecommerceecosystem.location.model.LocationOperatingHours;
import com.socialecommerceecosystem.location.model.LocationType;
import com.socialecommerceecosystem.location.model.PhysicalLocation;
import com.socialecommerceecosystem.location.service.LocationManagementService;

/**
 * Unit tests for the LocationController class.
 */
@WebMvcTest(LocationController.class)
public class LocationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LocationManagementService locationService;

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
        testOperatingHours.setDayOfWeek(java.time.DayOfWeek.MONDAY);
        testOperatingHours.setOpenTime(java.time.LocalTime.of(9, 0));
        testOperatingHours.setCloseTime(java.time.LocalTime.of(17, 0));
        testOperatingHours.setPhysicalLocation(testLocation);
    }

    @Test
    @DisplayName("GET /api/locations - Should return all locations")
    void testGetAllLocations() throws Exception {
        // Arrange
        List<PhysicalLocation> locations = Arrays.asList(testLocation);
        when(locationService.getAllLocations()).thenReturn(locations);

        // Act & Assert
        mockMvc.perform(get("/api/locations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Location")));

        verify(locationService, times(1)).getAllLocations();
    }

    @Test
    @DisplayName("GET /api/locations/paginated - Should return paginated locations")
    void testGetPaginatedLocations() throws Exception {
        // Arrange
        List<PhysicalLocation> locations = Arrays.asList(testLocation);
        when(locationService.getAllLocations(any(Pageable.class)))
                .thenReturn(new PageImpl<>(locations));

        // Act & Assert
        mockMvc.perform(get("/api/locations/paginated"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[0].name", is("Test Location")));

        verify(locationService, times(1)).getAllLocations(any(Pageable.class));
    }

    @Test
    @DisplayName("GET /api/locations/{id} - Should return location by id")
    void testGetLocationById() throws Exception {
        // Arrange
        when(locationService.getLocationById(1L)).thenReturn(Optional.of(testLocation));

        // Act & Assert
        mockMvc.perform(get("/api/locations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Location")));

        verify(locationService, times(1)).getLocationById(1L);
    }

    @Test
    @DisplayName("GET /api/locations/{id} - Should return 404 when location not found")
    void testGetLocationByIdNotFound() throws Exception {
        // Arrange
        when(locationService.getLocationById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/locations/999"))
                .andExpect(status().isNotFound());

        verify(locationService, times(1)).getLocationById(999L);
    }

    @Test
    @DisplayName("POST /api/locations - Should create a new location")
    void testCreateLocation() throws Exception {
        // Arrange
        when(locationService.createLocation(any(PhysicalLocation.class))).thenReturn(testLocation);

        // Act & Assert
        mockMvc.perform(post("/api/locations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testLocation)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Location")));

        verify(locationService, times(1)).createLocation(any(PhysicalLocation.class));
    }

    @Test
    @DisplayName("PUT /api/locations/{id} - Should update an existing location")
    void testUpdateLocation() throws Exception {
        // Arrange
        when(locationService.updateLocation(eq(1L), any(PhysicalLocation.class))).thenReturn(testLocation);

        // Act & Assert
        mockMvc.perform(put("/api/locations/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testLocation)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Location")));

        verify(locationService, times(1)).updateLocation(eq(1L), any(PhysicalLocation.class));
    }

    @Test
    @DisplayName("PUT /api/locations/{id} - Should return 404 when updating non-existent location")
    void testUpdateLocationNotFound() throws Exception {
        // Arrange
        when(locationService.updateLocation(eq(999L), any(PhysicalLocation.class)))
                .thenThrow(new IllegalArgumentException("Location not found with ID: 999"));

        // Act & Assert
        mockMvc.perform(put("/api/locations/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testLocation)))
                .andExpect(status().isNotFound());

        verify(locationService, times(1)).updateLocation(eq(999L), any(PhysicalLocation.class));
    }

    @Test
    @DisplayName("DELETE /api/locations/{id} - Should delete a location")
    void testDeleteLocation() throws Exception {
        // Arrange
        doNothing().when(locationService).deleteLocation(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/locations/1"))
                .andExpect(status().isNoContent());

        verify(locationService, times(1)).deleteLocation(1L);
    }

    @Test
    @DisplayName("DELETE /api/locations/{id} - Should return 404 when deleting non-existent location")
    void testDeleteLocationNotFound() throws Exception {
        // Arrange
        doThrow(new IllegalArgumentException("Location not found with ID: 999"))
                .when(locationService).deleteLocation(999L);

        // Act & Assert
        mockMvc.perform(delete("/api/locations/999"))
                .andExpect(status().isNotFound());

        verify(locationService, times(1)).deleteLocation(999L);
    }

    @Test
    @DisplayName("GET /api/locations/type/{type} - Should get locations by type")
    void testGetLocationsByType() throws Exception {
        // Arrange
        List<PhysicalLocation> locations = Arrays.asList(testLocation);
        when(locationService.getLocationsByType(LocationType.BRANCH_OFFICE)).thenReturn(locations);

        // Act & Assert
        mockMvc.perform(get("/api/locations/type/BRANCH_OFFICE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Location")));

        verify(locationService, times(1)).getLocationsByType(LocationType.BRANCH_OFFICE);
    }

    @Test
    @DisplayName("GET /api/locations/country/{country}/state/{state} - Should get locations by country and state")
    void testGetLocationsByCountryAndState() throws Exception {
        // Arrange
        List<PhysicalLocation> locations = Arrays.asList(testLocation);
        when(locationService.getLocationsByCountryAndState("Test Country", "Test State")).thenReturn(locations);

        // Act & Assert
        mockMvc.perform(get("/api/locations/country/Test Country/state/Test State"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Location")));

        verify(locationService, times(1)).getLocationsByCountryAndState("Test Country", "Test State");
    }

    @Test
    @DisplayName("GET /api/locations/city/{city} - Should get locations by city")
    void testGetLocationsByCity() throws Exception {
        // Arrange
        List<PhysicalLocation> locations = Arrays.asList(testLocation);
        when(locationService.getLocationsByCity("Test City")).thenReturn(locations);

        // Act & Assert
        mockMvc.perform(get("/api/locations/city/Test City"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Location")));

        verify(locationService, times(1)).getLocationsByCity("Test City");
    }

    @Test
    @DisplayName("GET /api/locations/service/{serviceType} - Should get locations by service type")
    void testFindLocationsByServiceType() throws Exception {
        // Arrange
        List<PhysicalLocation> locations = Arrays.asList(testLocation);
        when(locationService.findLocationsByServiceType("express")).thenReturn(locations);

        // Act & Assert
        mockMvc.perform(get("/api/locations/service/express"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Location")));

        verify(locationService, times(1)).findLocationsByServiceType("express");
    }

    @Test
    @DisplayName("GET /api/locations/nearby - Should find nearby locations")
    void testFindNearbyLocations() throws Exception {
        // Arrange
        List<PhysicalLocation> locations = Arrays.asList(testLocation);
        when(locationService.findNearbyLocations(40.7128, -74.0060, 10.0)).thenReturn(locations);

        // Act & Assert
        mockMvc.perform(get("/api/locations/nearby")
                .param("latitude", "40.7128")
                .param("longitude", "-74.0060")
                .param("radiusKm", "10.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Location")));

        verify(locationService, times(1)).findNearbyLocations(40.7128, -74.0060, 10.0);
    }

    @Test
    @DisplayName("GET /api/locations/active - Should get active locations")
    void testGetActiveLocations() throws Exception {
        // Arrange
        List<PhysicalLocation> locations = Arrays.asList(testLocation);
        when(locationService.getActiveLocations()).thenReturn(locations);

        // Act & Assert
        mockMvc.perform(get("/api/locations/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Location")));

        verify(locationService, times(1)).getActiveLocations();
    }

    @Test
    @DisplayName("PUT /api/locations/{id}/status - Should update location status")
    void testUpdateLocationStatus() throws Exception {
        // Arrange
        when(locationService.updateLocationStatus(1L, true)).thenReturn(testLocation);

        // Act & Assert
        mockMvc.perform(put("/api/locations/1/status")
                .param("active", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Location")));

        verify(locationService, times(1)).updateLocationStatus(1L, true);
    }

    @Test
    @DisplayName("POST /api/locations/{id}/operating-hours - Should add operating hours")
    void testAddOperatingHours() throws Exception {
        // Arrange
        when(locationService.addOperatingHours(eq(1L), any(LocationOperatingHours.class)))
                .thenReturn(testOperatingHours);

        // Act & Assert
        mockMvc.perform(post("/api/locations/1/operating-hours")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testOperatingHours)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)));

        verify(locationService, times(1)).addOperatingHours(eq(1L), any(LocationOperatingHours.class));
    }

    @Test
    @DisplayName("GET /api/locations/{id}/operating-hours - Should get operating hours by location")
    void testGetOperatingHoursByLocation() throws Exception {
        // Arrange
        List<LocationOperatingHours> operatingHours = Arrays.asList(testOperatingHours);
        when(locationService.getOperatingHoursByLocation(1L)).thenReturn(operatingHours);

        // Act & Assert
        mockMvc.perform(get("/api/locations/1/operating-hours"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));

        verify(locationService, times(1)).getOperatingHoursByLocation(1L);
    }

    @Test
    @DisplayName("PUT /api/locations/{id}/capacity-utilization - Should update capacity utilization")
    void testUpdateCapacityUtilization() throws Exception {
        // Arrange
        doNothing().when(locationService).updateCapacityUtilization(1L, 75.0);

        // Act & Assert
        mockMvc.perform(put("/api/locations/1/capacity-utilization")
                .param("capacityUtilization", "75.0"))
                .andExpect(status().isNoContent());

        verify(locationService, times(1)).updateCapacityUtilization(1L, 75.0);
    }

    @Test
    @DisplayName("GET /api/locations/{id}/is-open - Should check if location is open")
    void testIsLocationOpen() throws Exception {
        // Arrange
        when(locationService.isLocationOpen(eq(1L), any(java.time.LocalDateTime.class))).thenReturn(true);

        // Act & Assert
        mockMvc.perform(get("/api/locations/1/is-open"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(locationService, times(1)).isLocationOpen(eq(1L), any(java.time.LocalDateTime.class));
    }

    @Test
    @DisplayName("GET /api/locations/counts/by-type - Should get location counts by type")
    void testGetLocationCountsByType() throws Exception {
        // Arrange
        Map<String, Long> counts = new HashMap<>();
        counts.put("Branch Office", 3L);
        counts.put("Sorting Center", 1L);
        
        when(locationService.getLocationCountsByType()).thenReturn(counts);

        // Act & Assert
        mockMvc.perform(get("/api/locations/counts/by-type"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.['Branch Office']", is(3)))
                .andExpect(jsonPath("$.['Sorting Center']", is(1)));

        verify(locationService, times(1)).getLocationCountsByType();
    }

    @Test
    @DisplayName("GET /api/locations/capacity-utilization - Should get locations by capacity utilization range")
    void testGetLocationsByCapacityUtilizationRange() throws Exception {
        // Arrange
        List<PhysicalLocation> locations = Arrays.asList(testLocation);
        when(locationService.getLocationsByCapacityUtilizationRange(50.0, 75.0)).thenReturn(locations);

        // Act & Assert
        mockMvc.perform(get("/api/locations/capacity-utilization")
                .param("minUtilization", "50.0")
                .param("maxUtilization", "75.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Location")));

        verify(locationService, times(1)).getLocationsByCapacityUtilizationRange(50.0, 75.0);
    }
}
