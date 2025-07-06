import java.util.Map;
package com.gogidix.courier.courier.tracking.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.socialecommerceecosystem.tracking.model.Shipment;
import com.socialecommerceecosystem.tracking.model.ShipmentStatus;
import com.socialecommerceecosystem.tracking.model.TrackingEvent;
import com.socialecommerceecosystem.tracking.service.ShipmentTrackingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ShipmentTrackingController.class)
public class ShipmentTrackingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ShipmentTrackingService trackingService;

    private Shipment testShipment;
    private final String trackingNumber = "TRK-12345678";
    private final String orderId = "ORD-12345";
    private final String customerId = "CUST-12345";
    private TrackingEvent testEvent;

    @BeforeEach
    void setUp() {
        testShipment = Shipment.builder()
                .trackingNumber(trackingNumber)
                .orderId(orderId)
                .customerId(customerId)
                .originAddress("123 Sender St, City, Country")
                .destinationAddress("456 Receiver St, City, Country")
                .createdAt(LocalDateTime.now())
                .currentStatus(ShipmentStatus.CREATED)
                .packageWeight(2.5)
                .build();
        
        testEvent = TrackingEvent.builder()
                .id(1L)
                .shipment(testShipment)
                .status(ShipmentStatus.IN_TRANSIT)
                .location("Distribution Center")
                .description("Package in transit")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Test
    void createShipment_success() throws Exception {
        // Arrange
        CreateShipmentRequest request = CreateShipmentRequest.builder()
                .orderId(orderId)
                .customerId(customerId)
                .originAddress("123 Sender St, City, Country")
                .destinationAddress("456 Receiver St, City, Country")
                .packageWeight(2.5)
                .packageDimensions("10x10x10")
                .notificationPreferences("EMAIL")
                .build();
        
        when(trackingService.createShipment(
                anyString(), anyString(), anyString(), anyString(), 
                any(Double.class), anyString(), anyString()
        )).thenReturn(testShipment);
        
        // Act & Assert
        mockMvc.perform(post("/api/tracking/shipments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.trackingNumber", is(trackingNumber)))
                .andExpect(jsonPath("$.orderId", is(orderId)))
                .andExpect(jsonPath("$.customerId", is(customerId)));
    }

    @Test
    void getShipment_success() throws Exception {
        // Arrange
        when(trackingService.getShipmentByTrackingNumber(trackingNumber)).thenReturn(Optional.of(testShipment));
        
        // Act & Assert
        mockMvc.perform(get("/api/tracking/shipments/{trackingNumber}", trackingNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trackingNumber", is(trackingNumber)))
                .andExpect(jsonPath("$.orderId", is(orderId)));
    }

    @Test
    void getShipment_notFound() throws Exception {
        // Arrange
        when(trackingService.getShipmentByTrackingNumber(anyString())).thenReturn(Optional.empty());
        
        // Act & Assert
        mockMvc.perform(get("/api/tracking/shipments/{trackingNumber}", "INVALID-TRK"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getShipmentsByCustomer_success() throws Exception {
        // Arrange
        List<Shipment> shipments = Arrays.asList(testShipment);
        when(trackingService.getShipmentsByCustomerId(customerId)).thenReturn(shipments);
        
        // Act & Assert
        mockMvc.perform(get("/api/tracking/shipments/customer/{customerId}", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].trackingNumber", is(trackingNumber)));
    }

    @Test
    void updateShipmentStatus_success() throws Exception {
        // Arrange
        UpdateStatusRequest request = UpdateStatusRequest.builder()
                .status(ShipmentStatus.IN_TRANSIT)
                .location("Distribution Center")
                .description("Package in transit")
                .courierId("COUR-12345")
                .latitude(40.7128)
                .longitude(-74.0060)
                .build();
        
        when(trackingService.updateShipmentStatus(
                eq(trackingNumber), any(ShipmentStatus.class), anyString(), 
                anyString(), anyString(), any(Double.class), any(Double.class)
        )).thenReturn(testEvent);
        
        // Act & Assert
        mockMvc.perform(put("/api/tracking/shipments/{trackingNumber}/status", trackingNumber)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(ShipmentStatus.IN_TRANSIT.toString())))
                .andExpect(jsonPath("$.location", is("Distribution Center")));
    }

    @Test
    void assignCourier_success() throws Exception {
        // Arrange
        String courierId = "COUR-12345";
        testShipment.setCourierId(courierId);
        
        when(trackingService.assignCourier(trackingNumber, courierId)).thenReturn(testShipment);
        
        // Act & Assert
        mockMvc.perform(put("/api/tracking/shipments/{trackingNumber}/courier/{courierId}", 
                trackingNumber, courierId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trackingNumber", is(trackingNumber)))
                .andExpect(jsonPath("$.courierId", is(courierId)));
    }

    @Test
    void getTrackingEvents_success() throws Exception {
        // Arrange
        List<TrackingEvent> events = Arrays.asList(testEvent);
        when(trackingService.getTrackingEventsByTrackingNumber(trackingNumber)).thenReturn(events);
        
        // Act & Assert
        mockMvc.perform(get("/api/tracking/shipments/{trackingNumber}/events", trackingNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status", is(ShipmentStatus.IN_TRANSIT.toString())))
                .andExpect(jsonPath("$[0].location", is("Distribution Center")));
    }
} 
