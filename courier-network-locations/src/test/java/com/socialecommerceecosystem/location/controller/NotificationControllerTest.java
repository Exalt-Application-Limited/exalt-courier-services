package com.gogidix.courier.location.controller;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
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
import com.socialecommerceecosystem.location.service.WalkInCustomerService;

/**
 * Unit tests for the NotificationController class.
 */
@WebMvcTest(NotificationController.class)
public class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NotificationService notificationService;
    
    @MockBean
    private ShipmentProcessingService shipmentService;
    
    @MockBean
    private WalkInCustomerService customerService;

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
        testCustomer.setEmailNotificationsEnabled(true);
        testCustomer.setSmsNotificationsEnabled(true);
        
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
        testShipment.setCreatedAt(LocalDateTime.now());
        testShipment.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("POST /api/notifications/shipment-status - Should send shipment status notification")
    void testSendShipmentStatusUpdateNotification() throws Exception {
        // Arrange
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("shipmentId", 1L);
        requestParams.put("oldStatus", "RECEIVED");
        requestParams.put("newStatus", "IN_TRANSIT");
        
        when(shipmentService.getShipmentById(1L)).thenReturn(Optional.of(testShipment));
        when(notificationService.sendShipmentStatusUpdateNotification(
                any(WalkInShipment.class), eq(ShipmentStatus.RECEIVED), eq(ShipmentStatus.IN_TRANSIT)))
                .thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/api/notifications/shipment-status")
                .param("shipmentId", "1")
                .param("oldStatus", "RECEIVED")
                .param("newStatus", "IN_TRANSIT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", containsString("Status update notification sent successfully")));

        verify(shipmentService, times(1)).getShipmentById(1L);
        verify(notificationService, times(1)).sendShipmentStatusUpdateNotification(
                any(WalkInShipment.class), eq(ShipmentStatus.RECEIVED), eq(ShipmentStatus.IN_TRANSIT));
    }

    @Test
    @DisplayName("POST /api/notifications/shipment-creation - Should send shipment creation confirmation")
    void testSendShipmentCreationConfirmation() throws Exception {
        // Arrange
        when(shipmentService.getShipmentById(1L)).thenReturn(Optional.of(testShipment));
        when(notificationService.sendShipmentCreationConfirmation(any(WalkInShipment.class))).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/api/notifications/shipment-creation")
                .param("shipmentId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", containsString("Creation confirmation sent successfully")));

        verify(shipmentService, times(1)).getShipmentById(1L);
        verify(notificationService, times(1)).sendShipmentCreationConfirmation(any(WalkInShipment.class));
    }

    @Test
    @DisplayName("POST /api/notifications/shipment-creation - Should return 404 if shipment not found")
    void testSendShipmentCreationConfirmationNotFound() throws Exception {
        // Arrange
        when(shipmentService.getShipmentById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(post("/api/notifications/shipment-creation")
                .param("shipmentId", "999"))
                .andExpect(status().isNotFound());

        verify(shipmentService, times(1)).getShipmentById(999L);
        verify(notificationService, never()).sendShipmentCreationConfirmation(any(WalkInShipment.class));
    }

    @Test
    @DisplayName("POST /api/notifications/pickup-ready - Should send pickup ready notification")
    void testSendPickupReadyNotification() throws Exception {
        // Arrange
        when(shipmentService.getShipmentById(1L)).thenReturn(Optional.of(testShipment));
        when(notificationService.sendPickupReadyNotification(any(WalkInShipment.class))).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/api/notifications/pickup-ready")
                .param("shipmentId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", containsString("Pickup ready notification sent successfully")));

        verify(shipmentService, times(1)).getShipmentById(1L);
        verify(notificationService, times(1)).sendPickupReadyNotification(any(WalkInShipment.class));
    }

    @Test
    @DisplayName("POST /api/notifications/delivery-completed - Should send delivery completed notification")
    void testSendDeliveryCompletedNotification() throws Exception {
        // Arrange
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("deliveryDetails", "Package left at front door");
        
        when(shipmentService.getShipmentById(1L)).thenReturn(Optional.of(testShipment));
        when(notificationService.sendDeliveryCompletedNotification(any(WalkInShipment.class), anyString()))
                .thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/api/notifications/delivery-completed")
                .param("shipmentId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", containsString("Delivery completed notification sent successfully")));

        verify(shipmentService, times(1)).getShipmentById(1L);
        verify(notificationService, times(1)).sendDeliveryCompletedNotification(
                any(WalkInShipment.class), eq("Package left at front door"));
    }

    @Test
    @DisplayName("POST /api/notifications/urgent-shipment - Should notify staff about urgent shipment")
    void testNotifyStaffAboutUrgentShipment() throws Exception {
        // Arrange
        when(shipmentService.getShipmentById(1L)).thenReturn(Optional.of(testShipment));
        when(notificationService.notifyStaffAboutUrgentShipment(any(WalkInShipment.class), eq(1L)))
                .thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/api/notifications/urgent-shipment")
                .param("shipmentId", "1")
                .param("locationId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", containsString("Staff notification sent successfully")));

        verify(shipmentService, times(1)).getShipmentById(1L);
        verify(notificationService, times(1)).notifyStaffAboutUrgentShipment(any(WalkInShipment.class), eq(1L));
    }

    @Test
    @DisplayName("POST /api/notifications/high-capacity - Should notify staff about high capacity")
    void testNotifyStaffAboutHighCapacityUtilization() throws Exception {
        // Arrange
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("utilizationPercentage", 85.5);
        
        when(notificationService.notifyStaffAboutHighCapacityUtilization(eq(1L), eq(85.5)))
                .thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/api/notifications/high-capacity")
                .param("locationId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", containsString("Capacity utilization notification sent successfully")));

        verify(notificationService, times(1)).notifyStaffAboutHighCapacityUtilization(eq(1L), eq(85.5));
    }

    @Test
    @DisplayName("GET /api/notifications/test-sms - Should test SMS notification capability")
    void testCheckSmsNotificationCapability() throws Exception {
        // Arrange
        when(customerService.getCustomerById(1L)).thenReturn(Optional.of(testCustomer));
        when(notificationService.canReceiveSmsNotifications(any(WalkInCustomer.class))).thenReturn(true);

        // Act & Assert
        mockMvc.perform(get("/api/notifications/test-sms")
                .param("customerId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.canReceive", is(true)))
                .andExpect(jsonPath("$.channel", is("SMS")));

        verify(customerService, times(1)).getCustomerById(1L);
        verify(notificationService, times(1)).canReceiveSmsNotifications(any(WalkInCustomer.class));
    }

    @Test
    @DisplayName("GET /api/notifications/test-email - Should test email notification capability")
    void testCheckEmailNotificationCapability() throws Exception {
        // Arrange
        when(customerService.getCustomerById(1L)).thenReturn(Optional.of(testCustomer));
        when(notificationService.canReceiveEmailNotifications(any(WalkInCustomer.class))).thenReturn(true);

        // Act & Assert
        mockMvc.perform(get("/api/notifications/test-email")
                .param("customerId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.canReceive", is(true)))
                .andExpect(jsonPath("$.channel", is("Email")));

        verify(customerService, times(1)).getCustomerById(1L);
        verify(notificationService, times(1)).canReceiveEmailNotifications(any(WalkInCustomer.class));
    }

    @Test
    @DisplayName("GET /api/notifications/statistics - Should return notification statistics")
    void testGetNotificationStatistics() throws Exception {
        // Arrange
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("email_count", 250);
        statistics.put("sms_count", 120);
        statistics.put("shipment_status_count", 180);
        statistics.put("payment_count", 100);
        statistics.put("staff_count", 50);
        statistics.put("email_enabled", true);
        statistics.put("sms_enabled", true);
        
        when(notificationService.getNotificationStatistics()).thenReturn(statistics);

        // Act & Assert
        mockMvc.perform(get("/api/notifications/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email_count", is(250)))
                .andExpect(jsonPath("$.sms_count", is(120)))
                .andExpect(jsonPath("$.shipment_status_count", is(180)))
                .andExpect(jsonPath("$.payment_count", is(100)))
                .andExpect(jsonPath("$.staff_count", is(50)))
                .andExpect(jsonPath("$.email_enabled", is(true)))
                .andExpect(jsonPath("$.sms_enabled", is(true)));

        verify(notificationService, times(1)).getNotificationStatistics();
    }
}
