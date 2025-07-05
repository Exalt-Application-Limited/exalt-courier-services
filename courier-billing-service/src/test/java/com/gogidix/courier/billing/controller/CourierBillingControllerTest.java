package com.gogidix.courier.billing.controller;

import com.gogidix.courier.billing.dto.*;
import com.gogidix.courier.billing.model.InvoiceStatus;
import com.gogidix.courier.billing.model.PaymentStatus;
import com.gogidix.courier.billing.service.CourierBillingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourierBillingController.class)
@DisplayName("Courier Billing Controller Tests")
class CourierBillingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourierBillingService billingService;

    @Autowired
    private ObjectMapper objectMapper;

    private CreateShipmentInvoiceRequest createInvoiceRequest;
    private InvoiceResponse invoiceResponse;
    private PaymentResponse paymentResponse;

    @BeforeEach
    void setUp() {
        createInvoiceRequest = new CreateShipmentInvoiceRequest(
                "customer-123",
                "Customer Name",
                "customer@example.com",
                "123 Main St, City, State",
                "shipment-456",
                "STANDARD",
                "Test shipment description",
                new BigDecimal("10.5"),
                "30x20x15",
                "City A",
                "City B",
                new BigDecimal("100.00"),
                "USD"
        );

        invoiceResponse = new InvoiceResponse(
                "INV-20240101-ABC123",
                "customer-123",
                "Customer Name",
                "customer@example.com",
                "123 Main St, City, State",
                "Test shipment description",
                new BigDecimal("25.00"),
                new BigDecimal("2.50"),
                new BigDecimal("1.91"),
                new BigDecimal("24.41"),
                "USD",
                InvoiceStatus.DRAFT,
                LocalDateTime.now().plusDays(30),
                LocalDateTime.now(),
                null,
                null,
                "shipment-456",
                null,
                "SHIPMENT"
        );

        paymentResponse = new PaymentResponse(
                "pay-123",
                "INV-20240101-ABC123",
                "customer-123",
                new BigDecimal("24.41"),
                "USD",
                "CREDIT_CARD",
                PaymentStatus.COMPLETED,
                "txn_123456",
                LocalDateTime.now(),
                null
        );
    }

    @Test
    @DisplayName("POST /api/v1/billing/invoices/shipment - Should create shipment invoice")
    void shouldCreateShipmentInvoice() throws Exception {
        // Given
        when(billingService.createShipmentInvoice(any(CreateShipmentInvoiceRequest.class)))
                .thenReturn(invoiceResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/billing/invoices/shipment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createInvoiceRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.invoiceNumber").value("INV-20240101-ABC123"))
                .andExpect(jsonPath("$.customerId").value("customer-123"))
                .andExpect(jsonPath("$.totalAmount").value(24.41))
                .andExpect(jsonPath("$.status").value("DRAFT"));

        verify(billingService).createShipmentInvoice(any(CreateShipmentInvoiceRequest.class));
    }

    @Test
    @DisplayName("GET /api/v1/billing/invoices/{invoiceId} - Should get invoice by ID")
    void shouldGetInvoiceById() throws Exception {
        // Given
        when(billingService.getInvoice("INV-20240101-ABC123"))
                .thenReturn(invoiceResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/billing/invoices/INV-20240101-ABC123"))
                .andExpect(status().isOk())
                .andExpected(jsonPath("$.invoiceNumber").value("INV-20240101-ABC123"))
                .andExpect(jsonPath("$.customerId").value("customer-123"))
                .andExpect(jsonPath("$.totalAmount").value(24.41));

        verify(billingService).getInvoice("INV-20240101-ABC123");
    }

    @Test
    @DisplayName("POST /api/v1/billing/invoices/{invoiceId}/finalize - Should finalize invoice")
    void shouldFinalizeInvoice() throws Exception {
        // Given
        doNothing().when(billingService).finalizeInvoice("INV-20240101-ABC123");

        // When & Then
        mockMvc.perform(post("/api/v1/billing/invoices/INV-20240101-ABC123/finalize"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Invoice finalized successfully"));

        verify(billingService).finalizeInvoice("INV-20240101-ABC123");
    }

    @Test
    @DisplayName("POST /api/v1/billing/invoices/{invoiceId}/payments/manual - Should record manual payment")
    void shouldRecordManualPayment() throws Exception {
        // Given
        RecordManualPaymentRequest paymentRequest = new RecordManualPaymentRequest(
                new BigDecimal("24.41"),
                "USD",
                "Manual payment via bank transfer",
                "admin-user"
        );

        when(billingService.recordManualPayment(eq("INV-20240101-ABC123"), any(RecordManualPaymentRequest.class)))
                .thenReturn(paymentResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/billing/invoices/INV-20240101-ABC123/payments/manual")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.paymentId").value("pay-123"))
                .andExpect(jsonPath("$.amount").value(24.41))
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        verify(billingService).recordManualPayment(eq("INV-20240101-ABC123"), any(RecordManualPaymentRequest.class));
    }

    @Test
    @DisplayName("POST /api/v1/billing/pricing/calculate - Should calculate pricing")
    void shouldCalculatePricing() throws Exception {
        // Given
        PricingCalculationRequest pricingRequest = new PricingCalculationRequest(
                "STANDARD",
                new BigDecimal("10.5"),
                "30x20x15",
                "City A",
                "City B",
                new BigDecimal("100.00"),
                "customer-123"
        );

        PricingCalculationResponse pricingResponse = new PricingCalculationResponse(
                new BigDecimal("25.00"),
                new BigDecimal("5.00"),
                new BigDecimal("30.00"),
                "STANDARD",
                "STANDARD",
                null
        );

        when(billingService.calculateShippingCharges(any(PricingCalculationRequest.class)))
                .thenReturn(pricingResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/billing/pricing/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pricingRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.baseAmount").value(25.00))
                .andExpect(jsonPath("$.serviceFees").value(5.00))
                .andExpect(jsonPath("$.totalAmount").value(30.00));

        verify(billingService).calculateShippingCharges(any(PricingCalculationRequest.class));
    }

    @Test
    @DisplayName("GET /api/v1/billing/invoices/{invoiceId}/payments - Should get invoice payments")
    void shouldGetInvoicePayments() throws Exception {
        // Given
        when(billingService.getInvoicePayments("INV-20240101-ABC123"))
                .thenReturn(Arrays.asList(paymentResponse));

        // When & Then
        mockMvc.perform(get("/api/v1/billing/invoices/INV-20240101-ABC123/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].paymentId").value("pay-123"))
                .andExpect(jsonPath("$[0].amount").value(24.41));

        verify(billingService).getInvoicePayments("INV-20240101-ABC123");
    }

    @Test
    @DisplayName("GET /api/v1/billing/customers/{customerId}/invoices - Should get customer invoices")
    void shouldGetCustomerInvoices() throws Exception {
        // Given
        when(billingService.getCustomerInvoices(eq("customer-123"), any(InvoiceFilterRequest.class)))
                .thenReturn(Arrays.asList(invoiceResponse));

        // When & Then
        mockMvc.perform(get("/api/v1/billing/customers/customer-123/invoices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].invoiceNumber").value("INV-20240101-ABC123"))
                .andExpect(jsonPath("$[0].customerId").value("customer-123"));

        verify(billingService).getCustomerInvoices(eq("customer-123"), any(InvoiceFilterRequest.class));
    }

    @Test
    @DisplayName("PUT /api/v1/billing/invoices/{invoiceId} - Should update invoice")
    void shouldUpdateInvoice() throws Exception {
        // Given
        UpdateInvoiceRequest updateRequest = new UpdateInvoiceRequest(
                "Updated Customer Name",
                "updated@example.com",
                "456 New St, City, State",
                "Updated description",
                LocalDateTime.now().plusDays(45),
                "USD",
                null,
                "admin-user"
        );

        when(billingService.updateInvoice(eq("INV-20240101-ABC123"), any(UpdateInvoiceRequest.class)))
                .thenReturn(invoiceResponse);

        // When & Then
        mockMvc.perform(put("/api/v1/billing/invoices/INV-20240101-ABC123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.invoiceNumber").value("INV-20240101-ABC123"));

        verify(billingService).updateInvoice(eq("INV-20240101-ABC123"), any(UpdateInvoiceRequest.class));
    }

    @Test
    @DisplayName("DELETE /api/v1/billing/invoices/{invoiceId} - Should cancel invoice")
    void shouldCancelInvoice() throws Exception {
        // Given
        doNothing().when(billingService).cancelInvoice("INV-20240101-ABC123", "Customer request");

        // When & Then
        mockMvc.perform(delete("/api/v1/billing/invoices/INV-20240101-ABC123")
                        .param("reason", "Customer request"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Invoice cancelled successfully"));

        verify(billingService).cancelInvoice("INV-20240101-ABC123", "Customer request");
    }

    @Test
    @DisplayName("POST /api/v1/billing/invoices/{invoiceId}/payments/automatic - Should initiate automatic payment")
    void shouldInitiateAutomaticPayment() throws Exception {
        // Given
        when(billingService.initiateAutomaticPayment("INV-20240101-ABC123"))
                .thenReturn(paymentResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/billing/invoices/INV-20240101-ABC123/payments/automatic"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value("pay-123"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        verify(billingService).initiateAutomaticPayment("INV-20240101-ABC123");
    }

    @Test
    @DisplayName("Should handle validation errors")
    void shouldHandleValidationErrors() throws Exception {
        // Given - Invalid request with missing required fields
        CreateShipmentInvoiceRequest invalidRequest = new CreateShipmentInvoiceRequest(
                null, // Missing customerId
                "",   // Empty customerName
                "invalid-email", // Invalid email format
                null, // Missing billingAddress
                null, // Missing shipmentId
                null, // Missing serviceType
                null, // Missing description
                null, // Missing weight
                null, // Missing dimensions
                null, // Missing origin
                null, // Missing destination
                null, // Missing declaredValue
                null  // Missing currency
        );

        // When & Then
        mockMvc.perform(post("/api/v1/billing/invoices/shipment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(billingService, never()).createShipmentInvoice(any());
    }
}