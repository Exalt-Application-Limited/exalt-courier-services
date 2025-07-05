package com.gogidix.courier.location.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

import com.socialecommerceecosystem.location.model.LocationType;
import com.socialecommerceecosystem.location.model.PhysicalLocation;
import com.socialecommerceecosystem.location.model.ShipmentStatus;
import com.socialecommerceecosystem.location.model.WalkInCustomer;
import com.socialecommerceecosystem.location.model.WalkInShipment;
import com.socialecommerceecosystem.location.repository.PhysicalLocationRepository;
import com.socialecommerceecosystem.location.repository.WalkInCustomerRepository;
import com.socialecommerceecosystem.location.repository.WalkInShipmentRepository;

/**
 * Integration tests for ShipmentProcessingService.
 * These tests verify that the service layer integrates correctly with repositories
 * and other dependent services.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ShipmentProcessingServiceIntegrationTest {

    @Autowired
    private ShipmentProcessingService shipmentService;

    @MockBean
    private WalkInShipmentRepository shipmentRepository;

    @MockBean
    private WalkInCustomerRepository customerRepository;

    @MockBean
    private PhysicalLocationRepository locationRepository;

    @MockBean
    private NotificationService notificationService;

    private PhysicalLocation testLocation;
    private WalkInCustomer testCustomer;
    private WalkInShipment testShipment;

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

        // Create test customer
        testCustomer = new WalkInCustomer();
        testCustomer.setId(1L);
        testCustomer.setName("Test Customer");
        testCustomer.setEmail("test@example.com");
        testCustomer.setPhone("555-1234");
        testCustomer.setLocation(testLocation);

        // Create test shipment
        testShipment = new WalkInShipment();
        testShipment.setId(1L);
        testShipment.setCustomer(testCustomer);
        testShipment.setTrackingNumber("TST12345");
        testShipment.setSourceAddress("123 Source St");
        testShipment.setSourceCity("Source City");
        testShipment.setSourceState("SS");
        testShipment.setSourceCountry("Source Country");
        testShipment.setSourceZipCode("12345");
        testShipment.setDestinationAddress("123 Destination St");
        testShipment.setDestinationCity("Destination City");
        testShipment.setDestinationState("DS");
        testShipment.setDestinationCountry("Destination Country");
        testShipment.setDestinationZipCode("54321");
        testShipment.setStatus(ShipmentStatus.PROCESSING);
        testShipment.setShippingCost(new BigDecimal("25.99"));
        testShipment.setLocation(testLocation);
        testShipment.setCreatedDate(LocalDateTime.now());
        testShipment.setLastModifiedDate(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should retrieve all shipments")
    void testGetAllShipments() {
        // Arrange
        List<WalkInShipment> expectedShipments = Arrays.asList(testShipment);
        when(shipmentRepository.findAll()).thenReturn(expectedShipments);

        // Act
        List<WalkInShipment> results = shipmentService.getAllShipments();

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("TST12345", results.get(0).getTrackingNumber());
        
        verify(shipmentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should retrieve shipment by ID")
    void testGetShipmentById() {
        // Arrange
        when(shipmentRepository.findById(1L)).thenReturn(Optional.of(testShipment));

        // Act
        Optional<WalkInShipment> result = shipmentService.getShipmentById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("TST12345", result.get().getTrackingNumber());
        
        verify(shipmentRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should retrieve shipment by tracking number")
    void testGetShipmentByTrackingNumber() {
        // Arrange
        when(shipmentRepository.findByTrackingNumber("TST12345")).thenReturn(Optional.of(testShipment));

        // Act
        Optional<WalkInShipment> result = shipmentService.getShipmentByTrackingNumber("TST12345");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        
        verify(shipmentRepository, times(1)).findByTrackingNumber("TST12345");
    }

    @Test
    @DisplayName("Should create new shipment")
    void testCreateShipment() {
        // Arrange
        WalkInShipment shipmentToCreate = new WalkInShipment();
        shipmentToCreate.setCustomer(testCustomer);
        shipmentToCreate.setDestinationAddress("456 New Destination St");
        shipmentToCreate.setDestinationCity("New Destination City");
        shipmentToCreate.setDestinationState("ND");
        shipmentToCreate.setDestinationCountry("New Destination Country");
        shipmentToCreate.setDestinationZipCode("54321");
        shipmentToCreate.setShippingCost(new BigDecimal("35.99"));
        shipmentToCreate.setLocation(testLocation);
        
        WalkInShipment savedShipment = new WalkInShipment();
        savedShipment.setId(2L);
        savedShipment.setCustomer(testCustomer);
        savedShipment.setTrackingNumber("TST67890");
        savedShipment.setDestinationAddress("456 New Destination St");
        savedShipment.setDestinationCity("New Destination City");
        savedShipment.setDestinationState("ND");
        savedShipment.setDestinationCountry("New Destination Country");
        savedShipment.setDestinationZipCode("54321");
        savedShipment.setStatus(ShipmentStatus.PROCESSING);
        savedShipment.setShippingCost(new BigDecimal("35.99"));
        savedShipment.setLocation(testLocation);
        savedShipment.setCreatedDate(LocalDateTime.now());
        
        when(shipmentRepository.save(any(WalkInShipment.class))).thenReturn(savedShipment);
        doNothing().when(notificationService).sendShipmentStatusNotification(any(WalkInShipment.class), any(ShipmentStatus.class));

        // Act
        WalkInShipment result = shipmentService.createShipment(shipmentToCreate);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("TST67890", result.getTrackingNumber());
        assertEquals(ShipmentStatus.PROCESSING, result.getStatus());
        
        verify(shipmentRepository, times(1)).save(any(WalkInShipment.class));
        verify(notificationService, times(1)).sendShipmentStatusNotification(any(WalkInShipment.class), any(ShipmentStatus.class));
    }

    @Test
    @DisplayName("Should update shipment status")
    void testUpdateShipmentStatus() {
        // Arrange
        ShipmentStatus newStatus = ShipmentStatus.READY_FOR_PICKUP;
        String notes = "Ready for customer pickup";
        
        WalkInShipment updatedShipment = new WalkInShipment();
        updatedShipment.setId(1L);
        updatedShipment.setCustomer(testCustomer);
        updatedShipment.setTrackingNumber("TST12345");
        updatedShipment.setStatus(newStatus);
        updatedShipment.setNotes(notes);
        
        when(shipmentRepository.findById(1L)).thenReturn(Optional.of(testShipment));
        when(shipmentRepository.save(any(WalkInShipment.class))).thenReturn(updatedShipment);
        doNothing().when(notificationService).sendShipmentStatusNotification(any(WalkInShipment.class), any(ShipmentStatus.class));

        // Act
        WalkInShipment result = shipmentService.updateShipmentStatus(1L, newStatus, notes);

        // Assert
        assertNotNull(result);
        assertEquals(newStatus, result.getStatus());
        assertEquals(notes, result.getNotes());
        
        verify(shipmentRepository, times(1)).findById(1L);
        verify(shipmentRepository, times(1)).save(any(WalkInShipment.class));
        verify(notificationService, times(1)).sendShipmentStatusNotification(any(WalkInShipment.class), any(ShipmentStatus.class));
    }

    @Test
    @DisplayName("Should find shipments by status")
    void testFindShipmentsByStatus() {
        // Arrange
        List<WalkInShipment> expectedShipments = Arrays.asList(testShipment);
        when(shipmentRepository.findByStatus(ShipmentStatus.PROCESSING)).thenReturn(expectedShipments);

        // Act
        List<WalkInShipment> results = shipmentService.findShipmentsByStatus(ShipmentStatus.PROCESSING);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(ShipmentStatus.PROCESSING, results.get(0).getStatus());
        
        verify(shipmentRepository, times(1)).findByStatus(ShipmentStatus.PROCESSING);
    }

    @Test
    @DisplayName("Should find shipments by location")
    void testFindShipmentsByLocation() {
        // Arrange
        List<WalkInShipment> expectedShipments = Arrays.asList(testShipment);
        when(shipmentRepository.findByLocationId(1L)).thenReturn(expectedShipments);

        // Act
        List<WalkInShipment> results = shipmentService.findShipmentsByLocation(1L);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(1L, results.get(0).getLocation().getId());
        
        verify(shipmentRepository, times(1)).findByLocationId(1L);
    }

    @Test
    @DisplayName("Should find shipments by customer")
    void testFindShipmentsByCustomer() {
        // Arrange
        List<WalkInShipment> expectedShipments = Arrays.asList(testShipment);
        when(shipmentRepository.findByCustomerId(1L)).thenReturn(expectedShipments);

        // Act
        List<WalkInShipment> results = shipmentService.findShipmentsByCustomer(1L);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(1L, results.get(0).getCustomer().getId());
        
        verify(shipmentRepository, times(1)).findByCustomerId(1L);
    }

    @Test
    @DisplayName("Should mark shipment as delivered")
    void testMarkShipmentAsDelivered() {
        // Arrange
        String deliveryNotes = "Delivered to customer";
        
        WalkInShipment deliveredShipment = new WalkInShipment();
        deliveredShipment.setId(1L);
        deliveredShipment.setStatus(ShipmentStatus.DELIVERED);
        deliveredShipment.setNotes(deliveryNotes);
        deliveredShipment.setDeliveryDate(LocalDateTime.now());
        
        when(shipmentRepository.findById(1L)).thenReturn(Optional.of(testShipment));
        when(shipmentRepository.save(any(WalkInShipment.class))).thenReturn(deliveredShipment);
        doNothing().when(notificationService).sendShipmentStatusNotification(any(WalkInShipment.class), any(ShipmentStatus.class));

        // Act
        WalkInShipment result = shipmentService.markShipmentAsDelivered(1L, deliveryNotes);

        // Assert
        assertNotNull(result);
        assertEquals(ShipmentStatus.DELIVERED, result.getStatus());
        assertEquals(deliveryNotes, result.getNotes());
        assertNotNull(result.getDeliveryDate());
        
        verify(shipmentRepository, times(1)).findById(1L);
        verify(shipmentRepository, times(1)).save(any(WalkInShipment.class));
        verify(notificationService, times(1)).sendShipmentStatusNotification(any(WalkInShipment.class), any(ShipmentStatus.class));
    }

    @Test
    @DisplayName("Should calculate shipping cost")
    void testCalculateShippingCost() {
        // Arrange
        String sourceZipCode = "12345";
        String destinationZipCode = "54321";
        double weight = 2.5;
        BigDecimal expectedCost = new BigDecimal("25.99");
        
        // This test depends on the actual implementation of the calculation logic
        // We'll assume a simple mock here, but in a real implementation this might
        // involve more complex business logic or external service calls

        // Act
        BigDecimal result = shipmentService.calculateShippingCost(sourceZipCode, destinationZipCode, weight);

        // Assert
        assertEquals(expectedCost, result);
    }

    @Test
    @DisplayName("Should search shipments by destination")
    void testSearchShipmentsByDestination() {
        // Arrange
        String destination = "Destination";
        List<WalkInShipment> expectedShipments = Arrays.asList(testShipment);
        
        when(shipmentRepository.findByDestinationCityContainingIgnoreCaseOrDestinationStateContainingIgnoreCase(
                destination, destination)).thenReturn(expectedShipments);

        // Act
        List<WalkInShipment> results = shipmentService.searchShipmentsByDestination(destination);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertTrue(results.get(0).getDestinationCity().contains(destination) 
                || results.get(0).getDestinationState().contains(destination));
        
        verify(shipmentRepository, times(1)).findByDestinationCityContainingIgnoreCaseOrDestinationStateContainingIgnoreCase(
                destination, destination);
    }
}
