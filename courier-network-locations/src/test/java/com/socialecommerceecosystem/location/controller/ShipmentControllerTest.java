package com.gogidix.courier.location.controller;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.socialecommerceecosystem.location.model.PhysicalLocation;
import com.socialecommerceecosystem.location.model.ShipmentStatus;
import com.socialecommerceecosystem.location.model.WalkInCustomer;
import com.socialecommerceecosystem.location.model.WalkInShipment;
import com.socialecommerceecosystem.location.service.NotificationService;
import com.socialecommerceecosystem.location.service.ShipmentProcessingService;

/**
 * Unit tests for the ShipmentController class.
 */
@WebMvcTest(ShipmentController.class)
public class ShipmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ShipmentProcessingService shipmentService;
    
    @MockBean
    private NotificationService notificationService;

    private WalkInShipment testShipment;
    private WalkInCustomer testCustomer;
    private PhysicalLocation testLocation;

    @BeforeEach
    void setUp() {
        // Create test location
        testLocation = new PhysicalLocation();
        testLocation.setId(1L);
        testLocation.setName("Test Location");
        testLocation.setAddress("123 Test Street");
        
        // Create test customer
        testCustomer = new WalkInCustomer();
        testCustomer.setId(1L);
        testCustomer.setName("Test Customer");
        testCustomer.setEmail("customer@example.com");
        testCustomer.setPhone("+15551234567");
        
        // Create test shipment
        testShipment = new WalkInShipment();
        testShipment.setId(1L);
        testShipment.setTrackingNumber("TEST123456789");
        testShipment.setStatus(ShipmentStatus.RECEIVED);
        testShipment.setCustomer(testCustomer);
        testShipment.setLocationId(1L);
        testShipment.setOriginAddress("123 Sender St, Sender City, Sender State, 12345");
        testShipment.setDestinationAddress("456 Recipient St, Recipient City, Recipient State, 67890");
        testShipment.setWeight(2.5);
        testShipment.setDimensions("30x20x15");
        testShipment.setEstimatedCost(BigDecimal.valueOf(25.99));
        testShipment.setActualCost(BigDecimal.valueOf(25.99));
        testShipment.setServiceLevel("STANDARD");
        testShipment.setCreatedAt(LocalDateTime.now());
        testShipment.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("GET /api/shipments - Should return all shipments")
    void testGetAllShipments() throws Exception {
        // Arrange
        List<WalkInShipment> shipments = Arrays.asList(testShipment);
        when(shipmentService.getAllShipments()).thenReturn(shipments);

        // Act & Assert
        mockMvc.perform(get("/api/shipments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].trackingNumber", is("TEST123456789")));

        verify(shipmentService, times(1)).getAllShipments();
    }

    @Test
    @DisplayName("GET /api/shipments/{id} - Should return shipment by ID")
    void testGetShipmentById() throws Exception {
        // Arrange
        when(shipmentService.getShipmentById(1L)).thenReturn(Optional.of(testShipment));

        // Act & Assert
        mockMvc.perform(get("/api/shipments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.trackingNumber", is("TEST123456789")))
                .andExpect(jsonPath("$.status", is("RECEIVED")));

        verify(shipmentService, times(1)).getShipmentById(1L);
    }

    @Test
    @DisplayName("GET /api/shipments/{id} - Should return 404 if shipment not found")
    void testGetShipmentByIdNotFound() throws Exception {
        // Arrange
        when(shipmentService.getShipmentById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/shipments/999"))
                .andExpect(status().isNotFound());

        verify(shipmentService, times(1)).getShipmentById(999L);
    }

    @Test
    @DisplayName("GET /api/shipments/tracking/{trackingNumber} - Should return shipment by tracking number")
    void testGetShipmentByTrackingNumber() throws Exception {
        // Arrange
        when(shipmentService.getShipmentByTrackingNumber("TEST123456789")).thenReturn(Optional.of(testShipment));

        // Act & Assert
        mockMvc.perform(get("/api/shipments/tracking/TEST123456789"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.trackingNumber", is("TEST123456789")));

        verify(shipmentService, times(1)).getShipmentByTrackingNumber("TEST123456789");
    }

    @Test
    @DisplayName("POST /api/shipments - Should create a new shipment")
    void testCreateShipment() throws Exception {
        // Arrange
        when(shipmentService.createShipment(any(WalkInShipment.class))).thenReturn(testShipment);
        when(notificationService.sendShipmentCreationConfirmation(any(WalkInShipment.class))).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/api/shipments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testShipment)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.trackingNumber", is("TEST123456789")))
                .andExpect(jsonPath("$.status", is("RECEIVED")));

        verify(shipmentService, times(1)).createShipment(any(WalkInShipment.class));
        verify(notificationService, times(1)).sendShipmentCreationConfirmation(any(WalkInShipment.class));
    }

    @Test
    @DisplayName("PUT /api/shipments/{id} - Should update an existing shipment")
    void testUpdateShipment() throws Exception {
        // Arrange
        WalkInShipment updatedShipment = new WalkInShipment();
        updatedShipment.setId(1L);
        updatedShipment.setTrackingNumber("TEST123456789");
        updatedShipment.setStatus(ShipmentStatus.IN_TRANSIT);
        updatedShipment.setCustomer(testCustomer);
        updatedShipment.setLocationId(1L);
        updatedShipment.setServiceLevel("EXPRESS");
        updatedShipment.setUpdatedAt(LocalDateTime.now());
        
        when(shipmentService.updateShipment(eq(1L), any(WalkInShipment.class))).thenReturn(updatedShipment);
        when(shipmentService.getShipmentById(1L)).thenReturn(Optional.of(testShipment));

        // Act & Assert
        mockMvc.perform(put("/api/shipments/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedShipment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("IN_TRANSIT")))
                .andExpect(jsonPath("$.serviceLevel", is("EXPRESS")));

        verify(shipmentService, times(1)).updateShipment(eq(1L), any(WalkInShipment.class));
    }

    @Test
    @DisplayName("PUT /api/shipments/{id}/status - Should update shipment status")
    void testUpdateShipmentStatus() throws Exception {
        // Arrange
        Map<String, Object> statusUpdate = new HashMap<>();
        statusUpdate.put("status", "DELIVERED");
        statusUpdate.put("notes", "Left at front door");
        
        WalkInShipment updatedShipment = new WalkInShipment();
        updatedShipment.setId(1L);
        updatedShipment.setTrackingNumber("TEST123456789");
        updatedShipment.setStatus(ShipmentStatus.DELIVERED);
        updatedShipment.setNotes("Left at front door");
        updatedShipment.setCustomer(testCustomer);
        
        when(shipmentService.updateShipmentStatus(eq(1L), eq(ShipmentStatus.DELIVERED), eq("Left at front door")))
                .thenReturn(updatedShipment);
        when(shipmentService.getShipmentById(1L)).thenReturn(Optional.of(testShipment));
        when(notificationService.sendShipmentStatusUpdateNotification(
                any(WalkInShipment.class), any(ShipmentStatus.class), any(ShipmentStatus.class)))
                .thenReturn(true);

        // Act & Assert
        mockMvc.perform(put("/api/shipments/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("DELIVERED")))
                .andExpect(jsonPath("$.notes", is("Left at front door")));

        verify(shipmentService, times(1)).updateShipmentStatus(1L, ShipmentStatus.DELIVERED, "Left at front door");
        verify(notificationService, times(1)).sendShipmentStatusUpdateNotification(
                any(WalkInShipment.class), any(ShipmentStatus.class), eq(ShipmentStatus.DELIVERED));
    }

    @Test
    @DisplayName("GET /api/shipments/status/{status} - Should return shipments by status")
    void testGetShipmentsByStatus() throws Exception {
        // Arrange
        List<WalkInShipment> shipments = Arrays.asList(testShipment);
        when(shipmentService.getShipmentsByStatus(ShipmentStatus.RECEIVED)).thenReturn(shipments);

        // Act & Assert
        mockMvc.perform(get("/api/shipments/status/RECEIVED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].status", is("RECEIVED")));

        verify(shipmentService, times(1)).getShipmentsByStatus(ShipmentStatus.RECEIVED);
    }

    @Test
    @DisplayName("GET /api/shipments/location/{locationId} - Should return shipments by location")
    void testGetShipmentsByLocation() throws Exception {
        // Arrange
        List<WalkInShipment> shipments = Arrays.asList(testShipment);
        when(shipmentService.getShipmentsByLocationId(1L)).thenReturn(shipments);

        // Act & Assert
        mockMvc.perform(get("/api/shipments/location/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].locationId", is(1)));

        verify(shipmentService, times(1)).getShipmentsByLocationId(1L);
    }

    @Test
    @DisplayName("GET /api/shipments/customer/{customerId} - Should return shipments by customer")
    void testGetShipmentsByCustomer() throws Exception {
        // Arrange
        List<WalkInShipment> shipments = Arrays.asList(testShipment);
        when(shipmentService.getShipmentsByCustomerId(1L)).thenReturn(shipments);

        // Act & Assert
        mockMvc.perform(get("/api/shipments/customer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].customer.id", is(1)));

        verify(shipmentService, times(1)).getShipmentsByCustomerId(1L);
    }
}
