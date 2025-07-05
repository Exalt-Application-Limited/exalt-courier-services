package com.gogidix.courier.location.controller;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.socialecommerceecosystem.location.model.LocationStaff;
import com.socialecommerceecosystem.location.model.PhysicalLocation;
import com.socialecommerceecosystem.location.model.StaffRole;
import com.socialecommerceecosystem.location.service.StaffManagementService;

/**
 * Unit tests for the StaffController class.
 */
@WebMvcTest(StaffController.class)
public class StaffControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StaffManagementService staffService;

    private LocationStaff testStaff;
    private PhysicalLocation testLocation;

    @BeforeEach
    void setUp() {
        // Create test location
        testLocation = new PhysicalLocation();
        testLocation.setId(1L);
        testLocation.setName("Test Location");
        testLocation.setAddress("123 Test Street");
        testLocation.setCity("Test City");
        testLocation.setState("Test State");
        testLocation.setCountry("Test Country");
        testLocation.setZipCode("12345");
        
        // Create test staff
        testStaff = new LocationStaff();
        testStaff.setId(1L);
        testStaff.setName("Test Staff");
        testStaff.setEmail("staff@example.com");
        testStaff.setPhone("+15551234567");
        testStaff.setRole(StaffRole.MANAGER);
        testStaff.setLocation(testLocation);
        testStaff.setActive(true);
    }

    @Test
    @DisplayName("GET /api/staff - Should return all staff")
    void testGetAllStaff() throws Exception {
        // Arrange
        List<LocationStaff> staffList = Arrays.asList(testStaff);
        when(staffService.getAllStaff()).thenReturn(staffList);

        // Act & Assert
        mockMvc.perform(get("/api/staff"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Staff")))
                .andExpect(jsonPath("$[0].role", is("MANAGER")));

        verify(staffService, times(1)).getAllStaff();
    }

    @Test
    @DisplayName("GET /api/staff/{id} - Should return staff by ID")
    void testGetStaffById() throws Exception {
        // Arrange
        when(staffService.getStaffById(1L)).thenReturn(Optional.of(testStaff));

        // Act & Assert
        mockMvc.perform(get("/api/staff/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Staff")))
                .andExpect(jsonPath("$.email", is("staff@example.com")));

        verify(staffService, times(1)).getStaffById(1L);
    }

    @Test
    @DisplayName("GET /api/staff/{id} - Should return 404 if staff not found")
    void testGetStaffByIdNotFound() throws Exception {
        // Arrange
        when(staffService.getStaffById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/staff/999"))
                .andExpect(status().isNotFound());

        verify(staffService, times(1)).getStaffById(999L);
    }

    @Test
    @DisplayName("POST /api/staff - Should create a new staff")
    void testCreateStaff() throws Exception {
        // Arrange
        when(staffService.createStaff(any(LocationStaff.class))).thenReturn(testStaff);

        // Act & Assert
        mockMvc.perform(post("/api/staff")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testStaff)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Staff")))
                .andExpect(jsonPath("$.role", is("MANAGER")));

        verify(staffService, times(1)).createStaff(any(LocationStaff.class));
    }

    @Test
    @DisplayName("PUT /api/staff/{id} - Should update an existing staff")
    void testUpdateStaff() throws Exception {
        // Arrange
        LocationStaff updatedStaff = new LocationStaff();
        updatedStaff.setId(1L);
        updatedStaff.setName("Updated Staff Name");
        updatedStaff.setEmail("updated.staff@example.com");
        updatedStaff.setPhone("+15559876543");
        updatedStaff.setRole(StaffRole.SUPERVISOR);
        updatedStaff.setLocation(testLocation);
        updatedStaff.setActive(true);
        
        when(staffService.updateStaff(eq(1L), any(LocationStaff.class))).thenReturn(updatedStaff);

        // Act & Assert
        mockMvc.perform(put("/api/staff/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedStaff)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Updated Staff Name")))
                .andExpect(jsonPath("$.email", is("updated.staff@example.com")))
                .andExpect(jsonPath("$.role", is("SUPERVISOR")));

        verify(staffService, times(1)).updateStaff(eq(1L), any(LocationStaff.class));
    }

    @Test
    @DisplayName("PUT /api/staff/{id} - Should return 404 if staff not found")
    void testUpdateStaffNotFound() throws Exception {
        // Arrange
        when(staffService.updateStaff(eq(999L), any(LocationStaff.class))).thenThrow(new IllegalArgumentException("Staff not found"));

        // Act & Assert
        mockMvc.perform(put("/api/staff/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testStaff)))
                .andExpect(status().isNotFound());

        verify(staffService, times(1)).updateStaff(eq(999L), any(LocationStaff.class));
    }

    @Test
    @DisplayName("DELETE /api/staff/{id} - Should delete a staff")
    void testDeleteStaff() throws Exception {
        // Arrange
        doNothing().when(staffService).deleteStaff(1L);
        when(staffService.getStaffById(1L)).thenReturn(Optional.of(testStaff));

        // Act & Assert
        mockMvc.perform(delete("/api/staff/1"))
                .andExpect(status().isOk());

        verify(staffService, times(1)).deleteStaff(1L);
    }

    @Test
    @DisplayName("GET /api/staff/by-location/{locationId} - Should return staff by location")
    void testGetStaffByLocation() throws Exception {
        // Arrange
        List<LocationStaff> staffList = Arrays.asList(testStaff);
        when(staffService.getStaffByLocationId(1L)).thenReturn(staffList);

        // Act & Assert
        mockMvc.perform(get("/api/staff/by-location/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Staff")));

        verify(staffService, times(1)).getStaffByLocationId(1L);
    }

    @Test
    @DisplayName("GET /api/staff/by-role/{role} - Should return staff by role")
    void testGetStaffByRole() throws Exception {
        // Arrange
        List<LocationStaff> staffList = Arrays.asList(testStaff);
        when(staffService.getStaffByRole(StaffRole.MANAGER)).thenReturn(staffList);

        // Act & Assert
        mockMvc.perform(get("/api/staff/by-role/MANAGER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Staff")))
                .andExpect(jsonPath("$[0].role", is("MANAGER")));

        verify(staffService, times(1)).getStaffByRole(StaffRole.MANAGER);
    }
}
