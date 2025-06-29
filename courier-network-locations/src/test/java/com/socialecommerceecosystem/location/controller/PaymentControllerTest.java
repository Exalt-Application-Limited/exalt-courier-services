package com.exalt.courier.location.controller;

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
import com.socialecommerceecosystem.location.model.PaymentMethod;
import com.socialecommerceecosystem.location.model.PaymentStatus;
import com.socialecommerceecosystem.location.model.WalkInCustomer;
import com.socialecommerceecosystem.location.model.WalkInPayment;
import com.socialecommerceecosystem.location.model.WalkInShipment;
import com.socialecommerceecosystem.location.service.NotificationService;
import com.socialecommerceecosystem.location.service.PaymentProcessingService;
import com.socialecommerceecosystem.location.service.ShipmentProcessingService;

/**
 * Unit tests for the PaymentController class.
 */
@WebMvcTest(PaymentController.class)
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaymentProcessingService paymentService;
    
    @MockBean
    private ShipmentProcessingService shipmentService;
    
    @MockBean
    private NotificationService notificationService;

    private WalkInPayment testPayment;
    private WalkInShipment testShipment;
    private WalkInCustomer testCustomer;

    @BeforeEach
    void setUp() {
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
        testShipment.setCustomer(testCustomer);
        testShipment.setLocationId(1L);
        testShipment.setEstimatedCost(BigDecimal.valueOf(25.99));
        
        // Create test payment
        testPayment = new WalkInPayment();
        testPayment.setId(1L);
        testPayment.setShipmentId(1L);
        testPayment.setCustomerId(1L);
        testPayment.setTransactionId("PAY123456789");
        testPayment.setAmount(BigDecimal.valueOf(25.99));
        testPayment.setStatus(PaymentStatus.COMPLETED);
        testPayment.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        testPayment.setLastFourDigits("4242");
        testPayment.setPaymentDate(LocalDateTime.now());
        testPayment.setCreatedAt(LocalDateTime.now());
        testPayment.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("GET /api/payments - Should return all payments")
    void testGetAllPayments() throws Exception {
        // Arrange
        List<WalkInPayment> payments = Arrays.asList(testPayment);
        when(paymentService.getAllPayments()).thenReturn(payments);

        // Act & Assert
        mockMvc.perform(get("/api/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].transactionId", is("PAY123456789")));

        verify(paymentService, times(1)).getAllPayments();
    }

    @Test
    @DisplayName("GET /api/payments/{id} - Should return payment by ID")
    void testGetPaymentById() throws Exception {
        // Arrange
        when(paymentService.getPaymentById(1L)).thenReturn(Optional.of(testPayment));

        // Act & Assert
        mockMvc.perform(get("/api/payments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.transactionId", is("PAY123456789")))
                .andExpect(jsonPath("$.status", is("COMPLETED")));

        verify(paymentService, times(1)).getPaymentById(1L);
    }

    @Test
    @DisplayName("GET /api/payments/{id} - Should return 404 if payment not found")
    void testGetPaymentByIdNotFound() throws Exception {
        // Arrange
        when(paymentService.getPaymentById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/payments/999"))
                .andExpect(status().isNotFound());

        verify(paymentService, times(1)).getPaymentById(999L);
    }

    @Test
    @DisplayName("GET /api/payments/transaction/{transactionId} - Should return payment by transaction ID")
    void testGetPaymentByTransactionId() throws Exception {
        // Arrange
        when(paymentService.getPaymentByTransactionId("PAY123456789")).thenReturn(Optional.of(testPayment));

        // Act & Assert
        mockMvc.perform(get("/api/payments/transaction/PAY123456789"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.transactionId", is("PAY123456789")));

        verify(paymentService, times(1)).getPaymentByTransactionId("PAY123456789");
    }

    @Test
    @DisplayName("POST /api/payments - Should create a new payment")
    void testCreatePayment() throws Exception {
        // Arrange
        when(paymentService.createPayment(any(WalkInPayment.class))).thenReturn(testPayment);
        when(shipmentService.getShipmentById(1L)).thenReturn(Optional.of(testShipment));
        when(notificationService.sendPaymentConfirmation(any(WalkInPayment.class))).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/api/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testPayment)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.transactionId", is("PAY123456789")))
                .andExpect(jsonPath("$.status", is("COMPLETED")));

        verify(paymentService, times(1)).createPayment(any(WalkInPayment.class));
        verify(notificationService, times(1)).sendPaymentConfirmation(any(WalkInPayment.class));
    }

    @Test
    @DisplayName("PUT /api/payments/{id}/status - Should update payment status")
    void testUpdatePaymentStatus() throws Exception {
        // Arrange
        Map<String, Object> statusUpdate = new HashMap<>();
        statusUpdate.put("status", "REFUNDED");
        statusUpdate.put("notes", "Customer requested refund");
        
        WalkInPayment updatedPayment = new WalkInPayment();
        updatedPayment.setId(1L);
        updatedPayment.setTransactionId("PAY123456789");
        updatedPayment.setStatus(PaymentStatus.REFUNDED);
        updatedPayment.setNotes("Customer requested refund");
        
        when(paymentService.updatePaymentStatus(eq(1L), eq(PaymentStatus.REFUNDED), eq("Customer requested refund")))
                .thenReturn(updatedPayment);
        when(paymentService.getPaymentById(1L)).thenReturn(Optional.of(testPayment));
        when(notificationService.sendPaymentStatusUpdateNotification(any(WalkInPayment.class), any(PaymentStatus.class)))
                .thenReturn(true);

        // Act & Assert
        mockMvc.perform(put("/api/payments/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("REFUNDED")))
                .andExpect(jsonPath("$.notes", is("Customer requested refund")));

        verify(paymentService, times(1)).updatePaymentStatus(1L, PaymentStatus.REFUNDED, "Customer requested refund");
        verify(notificationService, times(1)).sendPaymentStatusUpdateNotification(any(WalkInPayment.class), eq(PaymentStatus.COMPLETED));
    }

    @Test
    @DisplayName("POST /api/payments/{id}/refund - Should refund a payment")
    void testRefundPayment() throws Exception {
        // Arrange
        Map<String, Object> refundRequest = new HashMap<>();
        refundRequest.put("amount", 15.99);
        refundRequest.put("reason", "Partial refund requested by customer");
        
        WalkInPayment refundedPayment = new WalkInPayment();
        refundedPayment.setId(1L);
        refundedPayment.setTransactionId("REF123456789");
        refundedPayment.setStatus(PaymentStatus.PARTIALLY_REFUNDED);
        refundedPayment.setAmount(BigDecimal.valueOf(10.0)); // Remaining amount after refund
        refundedPayment.setRefundedAmount(BigDecimal.valueOf(15.99));
        refundedPayment.setRefundReason("Partial refund requested by customer");
        
        when(paymentService.refundPayment(eq(1L), eq(BigDecimal.valueOf(15.99)), eq("Partial refund requested by customer")))
                .thenReturn(refundedPayment);
        when(paymentService.getPaymentById(1L)).thenReturn(Optional.of(testPayment));
        when(notificationService.sendRefundConfirmation(any(WalkInPayment.class), any(BigDecimal.class)))
                .thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/api/payments/1/refund")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refundRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("PARTIALLY_REFUNDED")))
                .andExpect(jsonPath("$.refundedAmount", closeTo(15.99, 0.001)));

        verify(paymentService, times(1)).refundPayment(1L, BigDecimal.valueOf(15.99), "Partial refund requested by customer");
        verify(notificationService, times(1)).sendRefundConfirmation(any(WalkInPayment.class), eq(BigDecimal.valueOf(15.99)));
    }

    @Test
    @DisplayName("GET /api/payments/shipment/{shipmentId} - Should return payments by shipment")
    void testGetPaymentsByShipment() throws Exception {
        // Arrange
        List<WalkInPayment> payments = Arrays.asList(testPayment);
        when(paymentService.getPaymentsByShipmentId(1L)).thenReturn(payments);

        // Act & Assert
        mockMvc.perform(get("/api/payments/shipment/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].shipmentId", is(1)));

        verify(paymentService, times(1)).getPaymentsByShipmentId(1L);
    }

    @Test
    @DisplayName("GET /api/payments/customer/{customerId} - Should return payments by customer")
    void testGetPaymentsByCustomer() throws Exception {
        // Arrange
        List<WalkInPayment> payments = Arrays.asList(testPayment);
        when(paymentService.getPaymentsByCustomerId(1L)).thenReturn(payments);

        // Act & Assert
        mockMvc.perform(get("/api/payments/customer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].customerId", is(1)));

        verify(paymentService, times(1)).getPaymentsByCustomerId(1L);
    }

    @Test
    @DisplayName("GET /api/payments/statistics - Should return payment statistics")
    void testGetPaymentStatistics() throws Exception {
        // Arrange
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalRevenue", 10000.50);
        statistics.put("transactionCount", 250);
        statistics.put("avgTransactionValue", 40.00);
        statistics.put("refundPercentage", 3.5);
        
        when(paymentService.getPaymentStatistics()).thenReturn(statistics);

        // Act & Assert
        mockMvc.perform(get("/api/payments/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRevenue", is(10000.50)))
                .andExpect(jsonPath("$.transactionCount", is(250)))
                .andExpect(jsonPath("$.avgTransactionValue", is(40.00)))
                .andExpect(jsonPath("$.refundPercentage", is(3.5)));

        verify(paymentService, times(1)).getPaymentStatistics();
    }
}
