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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.socialecommerceecosystem.location.model.PaymentMethod;
import com.socialecommerceecosystem.location.model.PaymentStatus;
import com.socialecommerceecosystem.location.model.ShipmentStatus;
import com.socialecommerceecosystem.location.model.WalkInCustomer;
import com.socialecommerceecosystem.location.model.WalkInPayment;
import com.socialecommerceecosystem.location.model.WalkInShipment;
import com.socialecommerceecosystem.location.repository.LocationStaffRepository;
import com.socialecommerceecosystem.location.repository.WalkInPaymentRepository;
import com.socialecommerceecosystem.location.repository.WalkInShipmentRepository;
import com.socialecommerceecosystem.location.service.impl.PaymentProcessingServiceImpl;

/**
 * Integration tests for PaymentProcessingService.
 * These tests verify that the service layer integrates correctly with repositories
 * and other dependent services.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class PaymentProcessingServiceIntegrationTest {

    @Autowired
    private PaymentProcessingService paymentService;

    @MockBean
    private WalkInPaymentRepository paymentRepository;

    @MockBean
    private WalkInShipmentRepository shipmentRepository;

    @MockBean
    private LocationStaffRepository staffRepository;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private ObjectMapper objectMapper;

    private WalkInCustomer testCustomer;
    private WalkInShipment testShipment;
    private WalkInPayment testPayment;

    @BeforeEach
    void setUp() {
        // Create test customer
        testCustomer = new WalkInCustomer();
        testCustomer.setId(1L);
        testCustomer.setName("Test Customer");
        testCustomer.setEmail("test@example.com");
        testCustomer.setPhone("555-1234");

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
        testShipment.setStatus(ShipmentStatus.PAYMENT_PENDING);
        testShipment.setShippingCost(new BigDecimal("25.99"));

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
    @DisplayName("Should retrieve all payments")
    void testGetAllPayments() {
        // Arrange
        List<WalkInPayment> expectedPayments = Arrays.asList(testPayment);
        when(paymentRepository.findAll()).thenReturn(expectedPayments);

        // Act
        List<WalkInPayment> results = paymentService.getAllPayments();

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("TXN-12345", results.get(0).getTransactionId());
        
        verify(paymentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should retrieve payment by ID")
    void testGetPaymentById() {
        // Arrange
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(testPayment));

        // Act
        Optional<WalkInPayment> result = paymentService.getPaymentById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("TXN-12345", result.get().getTransactionId());
        
        verify(paymentRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should retrieve payment by transaction ID")
    void testGetPaymentByTransactionId() {
        // Arrange
        when(paymentRepository.findByTransactionId("TXN-12345")).thenReturn(Optional.of(testPayment));

        // Act
        Optional<WalkInPayment> result = paymentService.getPaymentByTransactionId("TXN-12345");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        
        verify(paymentRepository, times(1)).findByTransactionId("TXN-12345");
    }

    @Test
    @DisplayName("Should create new payment")
    void testCreatePayment() throws Exception {
        // Arrange
        WalkInPayment paymentToCreate = new WalkInPayment();
        paymentToCreate.setShipment(testShipment);
        paymentToCreate.setShipmentId(testShipment.getId());
        paymentToCreate.setAmount(new BigDecimal("25.99"));
        paymentToCreate.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        paymentToCreate.setTransactionId("TXN-NEW");
        paymentToCreate.setReceiptNumber("RCP-NEW");
        
        when(paymentRepository.save(any(WalkInPayment.class))).thenReturn(testPayment);
        doNothing().when(notificationService).sendPaymentNotification(any(WalkInPayment.class));

        // Act
        WalkInPayment result = paymentService.createPayment(paymentToCreate);

        // Assert
        assertNotNull(result);
        assertEquals("TXN-12345", result.getTransactionId());
        assertEquals(PaymentStatus.COMPLETED, result.getStatus());
        
        verify(paymentRepository, times(1)).save(any(WalkInPayment.class));
        verify(notificationService, times(1)).sendPaymentNotification(any(WalkInPayment.class));
    }

    @Test
    @DisplayName("Should process payment for shipment")
    void testProcessPayment() throws Exception {
        // Arrange
        BigDecimal amount = new BigDecimal("25.99");
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        
        when(shipmentRepository.findById(1L)).thenReturn(Optional.of(testShipment));
        when(paymentRepository.save(any(WalkInPayment.class))).thenReturn(testPayment);
        when(objectMapper.writeValueAsString(anyMap())).thenReturn("{\"cardType\":\"VISA\",\"lastFourDigits\":\"1234\"}");
        doNothing().when(notificationService).sendPaymentNotification(any(WalkInPayment.class));

        // Act
        WalkInPayment result = paymentService.processPayment(1L, amount, paymentMethod, 
                Map.of("cardType", "VISA", "lastFourDigits", "1234"));

        // Assert
        assertNotNull(result);
        assertEquals("TXN-12345", result.getTransactionId());
        assertEquals(PaymentStatus.COMPLETED, result.getStatus());
        
        verify(shipmentRepository, times(1)).findById(1L);
        verify(paymentRepository, times(1)).save(any(WalkInPayment.class));
        verify(notificationService, times(1)).sendPaymentNotification(any(WalkInPayment.class));
    }

    @Test
    @DisplayName("Should update payment status")
    void testUpdatePaymentStatus() throws Exception {
        // Arrange
        PaymentStatus newStatus = PaymentStatus.REFUNDED;
        String notes = "Customer requested refund";
        
        WalkInPayment updatedPayment = new WalkInPayment();
        updatedPayment.setId(1L);
        updatedPayment.setShipment(testShipment);
        updatedPayment.setStatus(PaymentStatus.REFUNDED);
        updatedPayment.setNotes(notes);
        updatedPayment.setTransactionId("TXN-12345");
        
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(testPayment));
        when(paymentRepository.save(any(WalkInPayment.class))).thenReturn(updatedPayment);
        doNothing().when(notificationService).sendPaymentStatusUpdateNotification(any(WalkInPayment.class), any(PaymentStatus.class));

        // Act
        WalkInPayment result = paymentService.updatePaymentStatus(1L, newStatus, notes);

        // Assert
        assertNotNull(result);
        assertEquals(PaymentStatus.REFUNDED, result.getStatus());
        assertEquals(notes, result.getNotes());
        
        verify(paymentRepository, times(1)).findById(1L);
        verify(paymentRepository, times(1)).save(any(WalkInPayment.class));
        verify(notificationService, times(1)).sendPaymentStatusUpdateNotification(any(WalkInPayment.class), any(PaymentStatus.class));
    }

    @Test
    @DisplayName("Should process refund")
    void testProcessRefund() throws Exception {
        // Arrange
        BigDecimal refundAmount = new BigDecimal("15.00");
        String reason = "Partial refund requested";
        
        WalkInPayment refundedPayment = new WalkInPayment();
        refundedPayment.setId(1L);
        refundedPayment.setStatus(PaymentStatus.PARTIALLY_REFUNDED);
        refundedPayment.setRefundAmount(refundAmount);
        refundedPayment.setRefundReason(reason);
        refundedPayment.setRefundDate(LocalDateTime.now());
        refundedPayment.setRefundTransactionId("REF-TXN-12345");
        
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(testPayment));
        when(paymentRepository.save(any(WalkInPayment.class))).thenReturn(refundedPayment);
        doNothing().when(notificationService).sendRefundNotification(any(WalkInPayment.class), any(BigDecimal.class));

        // Act
        WalkInPayment result = paymentService.processRefund(1L, refundAmount, reason);

        // Assert
        assertNotNull(result);
        assertEquals(PaymentStatus.PARTIALLY_REFUNDED, result.getStatus());
        assertEquals(refundAmount, result.getRefundAmount());
        assertEquals(reason, result.getRefundReason());
        
        verify(paymentRepository, times(1)).findById(1L);
        verify(paymentRepository, times(1)).save(any(WalkInPayment.class));
        verify(notificationService, times(1)).sendRefundNotification(any(WalkInPayment.class), any(BigDecimal.class));
    }

    @Test
    @DisplayName("Should find payments by status")
    void testFindPaymentsByStatus() {
        // Arrange
        List<WalkInPayment> expectedPayments = Arrays.asList(testPayment);
        when(paymentRepository.findByStatus(PaymentStatus.COMPLETED)).thenReturn(expectedPayments);

        // Act
        List<WalkInPayment> results = paymentService.findPaymentsByStatus(PaymentStatus.COMPLETED);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(PaymentStatus.COMPLETED, results.get(0).getStatus());
        
        verify(paymentRepository, times(1)).findByStatus(PaymentStatus.COMPLETED);
    }

    @Test
    @DisplayName("Should find payments by date range")
    void testFindPaymentsByDateRange() {
        // Arrange
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);
        List<WalkInPayment> expectedPayments = Arrays.asList(testPayment);
        
        when(paymentRepository.findByPaymentDateBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(expectedPayments);

        // Act
        List<WalkInPayment> results = paymentService.findPaymentsByDateRange(startDate, endDate);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("TXN-12345", results.get(0).getTransactionId());
        
        verify(paymentRepository, times(1)).findByPaymentDateBetween(startDate, endDate);
    }

    @Test
    @DisplayName("Should find payments by method and status")
    void testFindPaymentsByMethodAndStatus() {
        // Arrange
        List<WalkInPayment> expectedPayments = Arrays.asList(testPayment);
        
        when(paymentRepository.findByPaymentMethodAndStatus(PaymentMethod.CREDIT_CARD, PaymentStatus.COMPLETED))
                .thenReturn(expectedPayments);

        // Act
        List<WalkInPayment> results = paymentService.findPaymentsByMethodAndStatus(
                PaymentMethod.CREDIT_CARD, PaymentStatus.COMPLETED);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(PaymentMethod.CREDIT_CARD, results.get(0).getPaymentMethod());
        assertEquals(PaymentStatus.COMPLETED, results.get(0).getStatus());
        
        verify(paymentRepository, times(1)).findByPaymentMethodAndStatus(
                PaymentMethod.CREDIT_CARD, PaymentStatus.COMPLETED);
    }

    @Test
    @DisplayName("Should calculate total revenue")
    void testCalculateTotalRevenue() {
        // Arrange
        BigDecimal expectedRevenue = new BigDecimal("1000.00");
        when(paymentRepository.calculateTotalRevenue()).thenReturn(expectedRevenue);

        // Act
        BigDecimal result = paymentService.calculateTotalRevenue();

        // Assert
        assertEquals(expectedRevenue, result);
        verify(paymentRepository, times(1)).calculateTotalRevenue();
    }

    @Test
    @DisplayName("Should calculate total revenue by date range")
    void testCalculateTotalRevenueByDateRange() {
        // Arrange
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = LocalDateTime.now();
        BigDecimal expectedRevenue = new BigDecimal("500.00");
        
        when(paymentRepository.calculateTotalRevenueByDateRange(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(expectedRevenue);

        // Act
        BigDecimal result = paymentService.calculateTotalRevenueByDateRange(startDate, endDate);

        // Assert
        assertEquals(expectedRevenue, result);
        verify(paymentRepository, times(1)).calculateTotalRevenueByDateRange(startDate, endDate);
    }
}
