package com.exalt.courierservices.tracking.service;

import lombok.extern.slf4j.Slf4j;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import com.exalt.courierservices.tracking.exception.ShipmentNotFoundException;
import com.exalt.courierservices.tracking.model.Shipment;
import com.exalt.courierservices.tracking.model.ShipmentStatus;
import com.exalt.courierservices.tracking.model.TrackingEvent;
import com.exalt.courierservices.tracking.repository.ShipmentRepository;
import com.exalt.courierservices.tracking.repository.TrackingEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class ShipmentTrackingServiceTest {

    @Mock
    private ShipmentRepository shipmentRepository;

    @Mock
    private TrackingEventRepository trackingEventRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ShipmentTrackingServiceImpl shipmentTrackingService;

    private Shipment testShipment;
    private final String trackingNumber = "TRK-12345678";
    private final String orderId = "ORD-12345";
    private final String customerId = "CUST-12345";
    private final String originAddress = "123 Sender St, City, Country";
    private final String destinationAddress = "456 Receiver St, City, Country";
    
    @BeforeEach
    void setUp() {
        testShipment = Shipment.builder()
                .trackingNumber(trackingNumber)
                .orderId(orderId)
                .customerId(customerId)
                .originAddress(originAddress)
                .destinationAddress(destinationAddress)
                .createdAt(LocalDateTime.now())
                .currentStatus(ShipmentStatus.CREATED)
                .packageWeight(2.5)
                .trackingEvents(new ArrayList<>())
                .build();
    }

    @Test
    void createShipment_success() {
        // Arrange
        when(shipmentRepository.save(any(Shipment.class))).thenReturn(testShipment);
        when(notificationService.sendShipmentCreatedNotification(any(Shipment.class))).thenReturn(true);
        
        // Act
        Shipment result = shipmentTrackingService.createShipment(
                orderId, customerId, originAddress, destinationAddress, 2.5, "10x10x10", "EMAIL"
        );
        
        // Assert
        assertNotNull(result);
        assertEquals(trackingNumber, result.getTrackingNumber());
        assertEquals(orderId, result.getOrderId());
        assertEquals(customerId, result.getCustomerId());
        verify(shipmentRepository).save(any(Shipment.class));
        verify(notificationService).sendShipmentCreatedNotification(any(Shipment.class));
    }

    @Test
    void getShipmentByTrackingNumber_returnShipment() {
        // Arrange
        when(shipmentRepository.findById(trackingNumber)).thenReturn(Optional.of(testShipment));
        
        // Act
        Optional<Shipment> result = shipmentTrackingService.getShipmentByTrackingNumber(trackingNumber);
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals(trackingNumber, result.get().getTrackingNumber());
    }

    @Test
    void getShipmentsByCustomerId_returnsShipments() {
        // Arrange
        List<Shipment> shipments = List.of(testShipment);
        when(shipmentRepository.findByCustomerId(customerId)).thenReturn(shipments);
        
        // Act
        List<Shipment> result = shipmentTrackingService.getShipmentsByCustomerId(customerId);
        
        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(trackingNumber, result.get(0).getTrackingNumber());
    }

    @Test
    void updateShipmentStatus_success() {
        // Arrange
        when(shipmentRepository.findById(trackingNumber)).thenReturn(Optional.of(testShipment));
        when(shipmentRepository.save(any(Shipment.class))).thenReturn(testShipment);
        when(notificationService.sendStatusUpdateNotification(any(Shipment.class), any(TrackingEvent.class))).thenReturn(true);
        
        // Act
        TrackingEvent result = shipmentTrackingService.updateShipmentStatus(
                trackingNumber, ShipmentStatus.IN_TRANSIT, "Distribution Center", 
                "Package in transit", "COUR-12345", 40.7128, -74.0060
        );
        
        // Assert
        assertNotNull(result);
        assertEquals(ShipmentStatus.IN_TRANSIT, result.getStatus());
        assertEquals("Distribution Center", result.getLocation());
        verify(shipmentRepository).save(any(Shipment.class));
        verify(notificationService).sendStatusUpdateNotification(any(Shipment.class), any(TrackingEvent.class));
    }

    @Test
    void updateShipmentStatus_shipmentNotFound() {
        // Arrange
        when(shipmentRepository.findById(trackingNumber)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ShipmentNotFoundException.class, () -> shipmentTrackingService.updateShipmentStatus(
                trackingNumber, ShipmentStatus.IN_TRANSIT, "Distribution Center", 
                "Package in transit", "COUR-12345", 40.7128, -74.0060
        ));
    }

    @Test
    void assignCourier_success() {
        // Arrange
        String courierId = "COUR-12345";
        when(shipmentRepository.findById(trackingNumber)).thenReturn(Optional.of(testShipment));
        when(shipmentRepository.save(any(Shipment.class))).thenReturn(testShipment);
        
        // Act
        Shipment result = shipmentTrackingService.assignCourier(trackingNumber, courierId);
        
        // Assert
        assertNotNull(result);
        assertEquals(courierId, result.getCourierId());
        verify(shipmentRepository).save(any(Shipment.class));
    }

    @Test
    void updateEstimatedDeliveryDate_success() {
        // Arrange
        LocalDateTime deliveryDate = LocalDateTime.now().plusDays(2);
        when(shipmentRepository.findById(trackingNumber)).thenReturn(Optional.of(testShipment));
        when(shipmentRepository.save(any(Shipment.class))).thenReturn(testShipment);
        when(notificationService.sendDeliveryDateUpdateNotification(any(Shipment.class))).thenReturn(true);
        
        // Act
        Shipment result = shipmentTrackingService.updateEstimatedDeliveryDate(trackingNumber, deliveryDate);
        
        // Assert
        assertNotNull(result);
        assertEquals(deliveryDate, testShipment.getEstimatedDeliveryDate());
        verify(shipmentRepository).save(any(Shipment.class));
        verify(notificationService).sendDeliveryDateUpdateNotification(any(Shipment.class));
    }

    @Test
    void getTrackingEventsByTrackingNumber_returnEvents() {
        // Arrange
        TrackingEvent event = new TrackingEvent();
        List<TrackingEvent> events = List.of(event);
        when(trackingEventRepository.findByShipmentTrackingNumberOrderByTimestampDesc(trackingNumber)).thenReturn(events);
        
        // Act
        List<TrackingEvent> result = shipmentTrackingService.getTrackingEventsByTrackingNumber(trackingNumber);
        
        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }
} 
