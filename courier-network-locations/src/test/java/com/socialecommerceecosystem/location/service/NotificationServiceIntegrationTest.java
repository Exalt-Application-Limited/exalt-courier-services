package com.exalt.courier.location.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.socialecommerceecosystem.location.model.LocationType;
import com.socialecommerceecosystem.location.model.PaymentMethod;
import com.socialecommerceecosystem.location.model.PaymentStatus;
import com.socialecommerceecosystem.location.model.PhysicalLocation;
import com.socialecommerceecosystem.location.model.ShipmentStatus;
import com.socialecommerceecosystem.location.model.WalkInCustomer;
import com.socialecommerceecosystem.location.model.WalkInPayment;
import com.socialecommerceecosystem.location.model.WalkInShipment;
import com.socialecommerceecosystem.location.repository.WalkInCustomerRepository;
import com.socialecommerceecosystem.location.service.impl.NotificationServiceImpl;

/**
 * Integration tests for NotificationService.
 * These tests verify that the notification service integrates correctly with
 * external notification systems and other services.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class NotificationServiceIntegrationTest {

    @Autowired
    private NotificationService notificationService;

    @MockBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @MockBean
    private WalkInCustomerRepository customerRepository;

    private PhysicalLocation testLocation;
    private WalkInCustomer testCustomer;
    private WalkInShipment testShipment;
    private WalkInPayment testPayment;

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
        testShipment.setDestinationAddress("123 Destination St");
        testShipment.setDestinationCity("Destination City");
        testShipment.setDestinationState("DS");
        testShipment.setDestinationCountry("Destination Country");
        testShipment.setDestinationZipCode("54321");
        testShipment.setStatus(ShipmentStatus.READY_FOR_PICKUP);
        testShipment.setShippingCost(new BigDecimal("25.99"));
        testShipment.setLocation(testLocation);

        // Create test payment
        testPayment = new WalkInPayment();
        testPayment.setId(1L);
        testPayment.setShipment(testShipment);
        testPayment.setShipmentId(testShipment.getId());
        testPayment.setAmount(new BigDecimal("25.99"));
        testPayment.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        testPayment.setStatus(PaymentStatus.COMPLETED);
        testPayment.setTransactionId("TXN-12345");
        testPayment.setReceiptNumber("RCP-12345");
        testPayment.setPaymentDate(LocalDateTime.now());
        testPayment.setLastModifiedDate(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should send shipment status notification")
    void testSendShipmentStatusNotification() {
        // Arrange
        ShipmentStatus oldStatus = ShipmentStatus.PAYMENT_PENDING;
        doReturn(null).when(kafkaTemplate).send(anyString(), any(Object.class));

        // Act
        notificationService.sendShipmentStatusNotification(testShipment, oldStatus);

        // Assert
        verify(kafkaTemplate, times(1)).send(eq("shipment-status-notifications"), any(Object.class));
    }

    @Test
    @DisplayName("Should send customer notification")
    void testSendCustomerNotification() {
        // Arrange
        String subject = "Test Subject";
        String message = "Test Message";
        doReturn(null).when(kafkaTemplate).send(anyString(), any(Object.class));

        // Act
        notificationService.sendCustomerNotification(testCustomer, subject, message);

        // Assert
        verify(kafkaTemplate, times(1)).send(eq("customer-notifications"), any(Object.class));
    }

    @Test
    @DisplayName("Should send payment notification")
    void testSendPaymentNotification() {
        // Arrange
        doReturn(null).when(kafkaTemplate).send(anyString(), any(Object.class));

        // Act
        notificationService.sendPaymentNotification(testPayment);

        // Assert
        verify(kafkaTemplate, times(1)).send(eq("payment-notifications"), any(Object.class));
    }

    @Test
    @DisplayName("Should send payment status update notification")
    void testSendPaymentStatusUpdateNotification() {
        // Arrange
        PaymentStatus oldStatus = PaymentStatus.PENDING;
        doReturn(null).when(kafkaTemplate).send(anyString(), any(Object.class));

        // Act
        notificationService.sendPaymentStatusUpdateNotification(testPayment, oldStatus);

        // Assert
        verify(kafkaTemplate, times(1)).send(eq("payment-status-notifications"), any(Object.class));
    }

    @Test
    @DisplayName("Should send refund notification")
    void testSendRefundNotification() {
        // Arrange
        BigDecimal refundAmount = new BigDecimal("15.00");
        doReturn(null).when(kafkaTemplate).send(anyString(), any(Object.class));

        // Act
        notificationService.sendRefundNotification(testPayment, refundAmount);

        // Assert
        verify(kafkaTemplate, times(1)).send(eq("refund-notifications"), any(Object.class));
    }

    @Test
    @DisplayName("Should send SMS notification")
    void testSendSmsNotification() {
        // Arrange
        String phoneNumber = "555-1234";
        String message = "Test SMS Message";
        
        // Mock external SMS service call if needed
        // This depends on the actual implementation of NotificationServiceImpl

        // Act
        notificationService.sendSmsNotification(phoneNumber, message);

        // Assert the appropriate verification based on implementation
        // For example, if it uses a Kafka topic for SMS messages:
        verify(kafkaTemplate, times(1)).send(eq("sms-notifications"), any(Object.class));
    }

    @Test
    @DisplayName("Should send email notification")
    void testSendEmailNotification() {
        // Arrange
        String email = "test@example.com";
        String subject = "Test Email Subject";
        String message = "Test Email Message";
        
        // Act
        notificationService.sendEmailNotification(email, subject, message);

        // Assert
        verify(kafkaTemplate, times(1)).send(eq("email-notifications"), any(Object.class));
    }

    @Test
    @DisplayName("Should send location update notification")
    void testSendLocationUpdateNotification() {
        // Arrange
        doReturn(null).when(kafkaTemplate).send(anyString(), any(Object.class));

        // Act
        notificationService.sendLocationUpdateNotification(testLocation);

        // Assert
        verify(kafkaTemplate, times(1)).send(eq("location-notifications"), any(Object.class));
    }
}
