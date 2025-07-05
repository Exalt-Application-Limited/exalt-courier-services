package com.gogidix.courier.billing.integration;

import com.gogidix.courier.billing.dto.*;
import com.gogidix.courier.billing.model.*;
import com.gogidix.courier.billing.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Courier Billing Integration Tests")
class CourierBillingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BillingAuditRepository auditRepository;

    private CreateShipmentInvoiceRequest createInvoiceRequest;

    @BeforeEach
    void setUp() {
        // Clean repositories
        paymentRepository.deleteAll();
        invoiceRepository.deleteAll();
        auditRepository.deleteAll();

        createInvoiceRequest = new CreateShipmentInvoiceRequest(
                "customer-integration-123",
                "Integration Test Customer",
                "integration@example.com",
                "123 Integration St, Test City, Test State",
                "shipment-integration-456",
                "STANDARD",
                "Integration test shipment description",
                new BigDecimal("10.5"),
                "30x20x15",
                "Test City A",
                "Test City B",
                new BigDecimal("100.00"),
                "USD"
        );
    }

    @Test
    @DisplayName("Should create and process complete invoice lifecycle")
    void shouldCreateAndProcessCompleteInvoiceLifecycle() throws Exception {
        // Step 1: Create shipment invoice
        String createResponse = mockMvc.perform(post("/api/v1/billing/invoices/shipment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createInvoiceRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerId").value("customer-integration-123"))
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        InvoiceResponse createdInvoice = objectMapper.readValue(createResponse, InvoiceResponse.class);
        String invoiceNumber = createdInvoice.invoiceNumber();

        // Verify invoice was saved to database
        assertThat(invoiceRepository.findByInvoiceNumber(invoiceNumber)).isPresent();

        // Step 2: Finalize the invoice
        mockMvc.perform(post("/api/v1/billing/invoices/{invoiceId}/finalize", invoiceNumber))
                .andExpected(status().isOk());

        // Verify invoice status changed to SENT
        Invoice finalizedInvoice = invoiceRepository.findByInvoiceNumber(invoiceNumber).get();
        assertThat(finalizedInvoice.getStatus()).isEqualTo(InvoiceStatus.SENT);
        assertThat(finalizedInvoice.getSentAt()).isNotNull();

        // Step 3: Record manual payment
        RecordManualPaymentRequest paymentRequest = new RecordManualPaymentRequest(
                createdInvoice.totalAmount(),
                "USD",
                "Integration test manual payment",
                "integration-test-user"
        );

        String paymentResponse = mockMvc.perform(post("/api/v1/billing/invoices/{invoiceId}/payments/manual", invoiceNumber)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(createdInvoice.totalAmount()))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        PaymentResponse recordedPayment = objectMapper.readValue(paymentResponse, PaymentResponse.class);

        // Verify payment was saved and invoice status updated
        assertThat(paymentRepository.findByPaymentId(recordedPayment.paymentId())).isPresent();
        
        Invoice paidInvoice = invoiceRepository.findByInvoiceNumber(invoiceNumber).get();
        assertThat(paidInvoice.getStatus()).isEqualTo(InvoiceStatus.PAID);
        assertThat(paidInvoice.getPaidAt()).isNotNull();

        // Step 4: Get invoice payments
        mockMvc.perform(get("/api/v1/billing/invoices/{invoiceId}/payments", invoiceNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].paymentId").value(recordedPayment.paymentId()));

        // Step 5: Get customer invoices
        mockMvc.perform(get("/api/v1/billing/customers/{customerId}/invoices", "customer-integration-123"))
                .andExpect(status().isOk())
                .andExpected(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].invoiceNumber").value(invoiceNumber));

        // Verify audit trail was created
        assertThat(auditRepository.findByEntityId(paidInvoice.getId())).isNotEmpty();
    }

    @Test
    @DisplayName("Should handle pricing calculation with discounts")
    void shouldHandlePricingCalculationWithDiscounts() throws Exception {
        // Given
        PricingCalculationRequest pricingRequest = new PricingCalculationRequest(
                "STANDARD",
                new BigDecimal("15.0"),
                "40x30x20",
                "Integration City A",
                "Integration City B",
                new BigDecimal("200.00"),
                "customer-integration-456"
        );

        // When & Then
        mockMvc.perform(post("/api/v1/billing/pricing/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pricingRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.baseAmount").exists())
                .andExpect(jsonPath("$.serviceFees").exists())
                .andExpect(jsonPath("$.totalAmount").exists())
                .andExpect(jsonPath("$.serviceType").value("STANDARD"));
    }

    @Test
    @DisplayName("Should handle invoice update workflow")
    void shouldHandleInvoiceUpdateWorkflow() throws Exception {
        // Step 1: Create invoice
        String createResponse = mockMvc.perform(post("/api/v1/billing/invoices/shipment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createInvoiceRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        InvoiceResponse createdInvoice = objectMapper.readValue(createResponse, InvoiceResponse.class);
        String invoiceNumber = createdInvoice.invoiceNumber();

        // Step 2: Update invoice
        UpdateInvoiceRequest updateRequest = new UpdateInvoiceRequest(
                "Updated Integration Customer Name",
                "updated-integration@example.com",
                "456 Updated Integration St, New Test City, New State",
                "Updated integration test description",
                LocalDateTime.now().plusDays(45),
                "USD",
                null,
                "integration-admin-user"
        );

        mockMvc.perform(put("/api/v1/billing/invoices/{invoiceId}", invoiceNumber)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.invoiceNumber").value(invoiceNumber));

        // Verify updates were applied
        Invoice updatedInvoice = invoiceRepository.findByInvoiceNumber(invoiceNumber).get();
        assertThat(updatedInvoice.getCustomerName()).isEqualTo("Updated Integration Customer Name");
        assertThat(updatedInvoice.getCustomerEmail()).isEqualTo("updated-integration@example.com");
        assertThat(updatedInvoice.getDescription()).isEqualTo("Updated integration test description");
    }

    @Test
    @DisplayName("Should handle invoice cancellation workflow")
    void shouldHandleInvoiceCancellationWorkflow() throws Exception {
        // Step 1: Create invoice
        String createResponse = mockMvc.perform(post("/api/v1/billing/invoices/shipment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createInvoiceRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        InvoiceResponse createdInvoice = objectMapper.readValue(createResponse, InvoiceResponse.class);
        String invoiceNumber = createdInvoice.invoiceNumber();

        // Step 2: Cancel invoice
        mockMvc.perform(delete("/api/v1/billing/invoices/{invoiceId}", invoiceNumber)
                        .param("reason", "Integration test cancellation"))
                .andExpect(status().isOk());

        // Verify invoice was cancelled
        Invoice cancelledInvoice = invoiceRepository.findByInvoiceNumber(invoiceNumber).get();
        assertThat(cancelledInvoice.getStatus()).isEqualTo(InvoiceStatus.CANCELLED);
    }

    @Test
    @DisplayName("Should validate business rules and constraints")
    void shouldValidateBusinessRulesAndConstraints() throws Exception {
        // Create and finalize an invoice
        String createResponse = mockMvc.perform(post("/api/v1/billing/invoices/shipment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createInvoiceRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        InvoiceResponse createdInvoice = objectMapper.readValue(createResponse, InvoiceResponse.class);
        String invoiceNumber = createdInvoice.invoiceNumber();

        // Finalize the invoice
        mockMvc.perform(post("/api/v1/billing/invoices/{invoiceId}/finalize", invoiceNumber))
                .andExpect(status().isOk());

        // Try to finalize again - should fail
        mockMvc.perform(post("/api/v1/billing/invoices/{invoiceId}/finalize", invoiceNumber))
                .andExpect(status().isBadRequest());

        // Record payment to make it paid
        RecordManualPaymentRequest paymentRequest = new RecordManualPaymentRequest(
                createdInvoice.totalAmount(),
                "USD",
                "Full payment",
                "test-user"
        );

        mockMvc.perform(post("/api/v1/billing/invoices/{invoiceId}/payments/manual", invoiceNumber)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isCreated());

        // Try to cancel paid invoice - should fail
        mockMvc.perform(delete("/api/v1/billing/invoices/{invoiceId}", invoiceNumber)
                        .param("reason", "Should fail"))
                .andExpect(status().isBadRequest());

        // Try to record another payment on paid invoice - should fail
        mockMvc.perform(post("/api/v1/billing/invoices/{invoiceId}/payments/manual", invoiceNumber)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle partial payment scenarios")
    void shouldHandlePartialPaymentScenarios() throws Exception {
        // Create and finalize invoice
        String createResponse = mockMvc.perform(post("/api/v1/billing/invoices/shipment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createInvoiceRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        InvoiceResponse createdInvoice = objectMapper.readValue(createResponse, InvoiceResponse.class);
        String invoiceNumber = createdInvoice.invoiceNumber();

        mockMvc.perform(post("/api/v1/billing/invoices/{invoiceId}/finalize", invoiceNumber))
                .andExpect(status().isOk());

        // Record partial payment (50% of total)
        BigDecimal partialAmount = createdInvoice.totalAmount().divide(new BigDecimal("2"));
        RecordManualPaymentRequest partialPayment = new RecordManualPaymentRequest(
                partialAmount,
                "USD",
                "Partial payment - 50%",
                "test-user"
        );

        mockMvc.perform(post("/api/v1/billing/invoices/{invoiceId}/payments/manual", invoiceNumber)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partialPayment)))
                .andExpected(status().isCreated());

        // Verify invoice status is PARTIALLY_PAID
        Invoice partiallyPaidInvoice = invoiceRepository.findByInvoiceNumber(invoiceNumber).get();
        assertThat(partiallyPaidInvoice.getStatus()).isEqualTo(InvoiceStatus.PARTIALLY_PAID);

        // Record remaining payment
        RecordManualPaymentRequest remainingPayment = new RecordManualPaymentRequest(
                createdInvoice.totalAmount().subtract(partialAmount),
                "USD",
                "Remaining payment - 50%",
                "test-user"
        );

        mockMvc.perform(post("/api/v1/billing/invoices/{invoiceId}/payments/manual", invoiceNumber)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(remainingPayment)))
                .andExpect(status().isCreated());

        // Verify invoice is now fully paid
        Invoice fullyPaidInvoice = invoiceRepository.findByInvoiceNumber(invoiceNumber).get();
        assertThat(fullyPaidInvoice.getStatus()).isEqualTo(InvoiceStatus.PAID);
        assertThat(fullyPaidInvoice.getPaidAt()).isNotNull();

        // Verify both payments exist
        mockMvc.perform(get("/api/v1/billing/invoices/{invoiceId}/payments", invoiceNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}