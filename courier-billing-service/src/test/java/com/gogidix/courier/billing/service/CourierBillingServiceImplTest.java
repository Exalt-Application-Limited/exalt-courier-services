package com.gogidix.courier.billing.service;

import com.gogidix.courier.billing.client.PaymentProcessingServiceClient;
import com.gogidix.courier.billing.client.NotificationServiceClient;
import com.gogidix.courier.billing.client.TaxCalculationServiceClient;
import com.gogidix.courier.billing.client.CurrencyExchangeServiceClient;
import com.gogidix.courier.billing.dto.*;
import com.gogidix.courier.billing.exception.BillingException;
import com.gogidix.courier.billing.exception.ResourceNotFoundException;
import com.gogidix.courier.billing.model.*;
import com.gogidix.courier.billing.repository.*;
import com.gogidix.courier.billing.service.impl.CourierBillingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Courier Billing Service Implementation Tests")
class CourierBillingServiceImplTest {

    @Mock
    private InvoiceRepository invoiceRepository;
    
    @Mock
    private PaymentRepository paymentRepository;
    
    @Mock
    private SubscriptionRepository subscriptionRepository;
    
    @Mock
    private CustomerCreditRepository customerCreditRepository;
    
    @Mock
    private BillingDisputeRepository disputeRepository;
    
    @Mock
    private BillingAuditRepository auditRepository;
    
    @Mock
    private PricingTierRepository pricingTierRepository;
    
    @Mock
    private PaymentProcessingServiceClient paymentProcessingClient;
    
    @Mock
    private NotificationServiceClient notificationServiceClient;
    
    @Mock
    private TaxCalculationServiceClient taxCalculationClient;
    
    @Mock
    private CurrencyExchangeServiceClient currencyExchangeClient;

    @InjectMocks
    private CourierBillingServiceImpl billingService;

    private CreateShipmentInvoiceRequest shipmentInvoiceRequest;
    private Invoice testInvoice;
    private Payment testPayment;

    @BeforeEach
    void setUp() {
        shipmentInvoiceRequest = new CreateShipmentInvoiceRequest(
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

        testInvoice = Invoice.builder()
                .id(UUID.randomUUID())
                .invoiceNumber("INV-20240101-ABC123")
                .customerId("customer-123")
                .customerName("Customer Name")
                .customerEmail("customer@example.com")
                .billingAddress("123 Main St, City, State")
                .shipmentId("shipment-456")
                .serviceType("STANDARD")
                .description("Test shipment description")
                .subtotal(new BigDecimal("25.00"))
                .discountAmount(new BigDecimal("2.50"))
                .taxAmount(new BigDecimal("1.91"))
                .totalAmount(new BigDecimal("24.41"))
                .currency("USD")
                .status(InvoiceStatus.DRAFT)
                .dueDate(LocalDateTime.now().plusDays(30))
                .createdAt(LocalDateTime.now())
                .createdBy("SYSTEM")
                .build();

        testPayment = Payment.builder()
                .id(UUID.randomUUID())
                .paymentId("pay-123")
                .invoice(testInvoice)
                .customerId("customer-123")
                .amount(new BigDecimal("24.41"))
                .currency("USD")
                .paymentMethodType("AUTOMATIC")
                .status(PaymentStatus.COMPLETED)
                .processedAt(LocalDateTime.now())
                .createdBy("SYSTEM_AUTO")
                .build();
    }

    @Test
    @DisplayName("Should create shipment invoice successfully")
    void shouldCreateShipmentInvoiceSuccessfully() {
        // Given
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(testInvoice);

        // When
        InvoiceResponse response = billingService.createShipmentInvoice(shipmentInvoiceRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.invoiceNumber()).isEqualTo("INV-20240101-ABC123");
        assertThat(response.customerId()).isEqualTo("customer-123");
        assertThat(response.totalAmount()).isEqualTo(new BigDecimal("24.41"));
        assertThat(response.status()).isEqualTo(InvoiceStatus.DRAFT);

        verify(invoiceRepository).save(any(Invoice.class));
    }

    @Test
    @DisplayName("Should get invoice by ID successfully")
    void shouldGetInvoiceByIdSuccessfully() {
        // Given
        when(invoiceRepository.findByInvoiceNumber("INV-20240101-ABC123"))
                .thenReturn(Optional.of(testInvoice));

        // When
        InvoiceResponse response = billingService.getInvoice("INV-20240101-ABC123");

        // Then
        assertThat(response).isNotNull();
        assertThat(response.invoiceNumber()).isEqualTo("INV-20240101-ABC123");
        assertThat(response.customerId()).isEqualTo("customer-123");
        verify(invoiceRepository).findByInvoiceNumber("INV-20240101-ABC123");
    }

    @Test
    @DisplayName("Should throw exception when invoice not found")
    void shouldThrowExceptionWhenInvoiceNotFound() {
        // Given
        when(invoiceRepository.findByInvoiceNumber("INVALID-ID"))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> billingService.getInvoice("INVALID-ID"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Invoice not found: INVALID-ID");

        verify(invoiceRepository).findByInvoiceNumber("INVALID-ID");
    }

    @Test
    @DisplayName("Should finalize draft invoice successfully")
    void shouldFinalizeDraftInvoiceSuccessfully() {
        // Given
        when(invoiceRepository.findByInvoiceNumber("INV-20240101-ABC123"))
                .thenReturn(Optional.of(testInvoice));
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(testInvoice);

        // When
        billingService.finalizeInvoice("INV-20240101-ABC123");

        // Then
        verify(invoiceRepository).findByInvoiceNumber("INV-20240101-ABC123");
        verify(invoiceRepository).save(any(Invoice.class));
    }

    @Test
    @DisplayName("Should throw exception when trying to finalize non-draft invoice")
    void shouldThrowExceptionWhenFinalizingNonDraftInvoice() {
        // Given
        testInvoice.setStatus(InvoiceStatus.PAID);
        when(invoiceRepository.findByInvoiceNumber("INV-20240101-ABC123"))
                .thenReturn(Optional.of(testInvoice));

        // When & Then
        assertThatThrownBy(() -> billingService.finalizeInvoice("INV-20240101-ABC123"))
                .isInstanceOf(BillingException.class)
                .hasMessageContaining("Only draft invoices can be finalized");
    }

    @Test
    @DisplayName("Should record manual payment successfully")
    void shouldRecordManualPaymentSuccessfully() {
        // Given
        RecordManualPaymentRequest paymentRequest = new RecordManualPaymentRequest(
                new BigDecimal("24.41"),
                "USD",
                "Manual payment via bank transfer",
                "admin-user"
        );

        when(invoiceRepository.findByInvoiceNumber("INV-20240101-ABC123"))
                .thenReturn(Optional.of(testInvoice));
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);
        when(paymentRepository.findByInvoiceNumber("INV-20240101-ABC123"))
                .thenReturn(Arrays.asList());

        // When
        PaymentResponse response = billingService.recordManualPayment("INV-20240101-ABC123", paymentRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.amount()).isEqualTo(new BigDecimal("24.41"));
        assertThat(response.currency()).isEqualTo("USD");
        assertThat(response.status()).isEqualTo(PaymentStatus.COMPLETED);

        verify(invoiceRepository).findByInvoiceNumber("INV-20240101-ABC123");
        verify(paymentRepository).save(any(Payment.class));
        verify(invoiceRepository).save(any(Invoice.class));
    }

    @Test
    @DisplayName("Should throw exception when recording payment on paid invoice")
    void shouldThrowExceptionWhenRecordingPaymentOnPaidInvoice() {
        // Given
        testInvoice.setStatus(InvoiceStatus.PAID);
        RecordManualPaymentRequest paymentRequest = new RecordManualPaymentRequest(
                new BigDecimal("24.41"),
                "USD",
                "Manual payment",
                "admin-user"
        );

        when(invoiceRepository.findByInvoiceNumber("INV-20240101-ABC123"))
                .thenReturn(Optional.of(testInvoice));

        // When & Then
        assertThatThrownBy(() -> billingService.recordManualPayment("INV-20240101-ABC123", paymentRequest))
                .isInstanceOf(BillingException.class)
                .hasMessageContaining("Cannot record payment for invoice in status: PAID");
    }

    @Test
    @DisplayName("Should calculate shipping charges correctly")
    void shouldCalculateShippingChargesCorrectly() {
        // Given
        PricingCalculationRequest request = new PricingCalculationRequest(
                "STANDARD",
                new BigDecimal("10.5"),
                "30x20x15",
                "City A",
                "City B",
                new BigDecimal("100.00"),
                "customer-123"
        );

        // When
        PricingCalculationResponse response = billingService.calculateShippingCharges(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.baseAmount()).isNotNull();
        assertThat(response.totalAmount()).isNotNull();
        assertThat(response.serviceType()).isEqualTo("STANDARD");
        assertThat(response.baseAmount().compareTo(BigDecimal.ZERO)).isGreaterThan(0);
    }

    @Test
    @DisplayName("Should apply volume discounts correctly")
    void shouldApplyVolumeDiscountsCorrectly() {
        // Given
        BigDecimal baseAmount = new BigDecimal("100.00");
        int shipmentCount = 25; // Should get 5% discount

        // When
        DiscountApplicationResponse response = billingService.applyVolumeDiscounts(
                "customer-123", baseAmount, shipmentCount);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.discountPercentage()).isEqualTo(new BigDecimal("5.0"));
        assertThat(response.discountAmount()).isEqualTo(new BigDecimal("5.00"));
        assertThat(response.finalAmount()).isEqualTo(new BigDecimal("95.00"));
        assertThat(response.discountType()).isEqualTo("VOLUME_DISCOUNT");
    }

    @Test
    @DisplayName("Should validate invoice status transition correctly")
    void shouldValidateInvoiceStatusTransitionCorrectly() {
        // Valid transitions
        assertThat(billingService.validateInvoiceStatusTransition(InvoiceStatus.DRAFT, InvoiceStatus.SENT))
                .isTrue();
        assertThat(billingService.validateInvoiceStatusTransition(InvoiceStatus.SENT, InvoiceStatus.PAID))
                .isTrue();
        assertThat(billingService.validateInvoiceStatusTransition(InvoiceStatus.PAID, InvoiceStatus.REFUNDED))
                .isTrue();

        // Invalid transitions
        assertThat(billingService.validateInvoiceStatusTransition(InvoiceStatus.PAID, InvoiceStatus.DRAFT))
                .isFalse();
        assertThat(billingService.validateInvoiceStatusTransition(InvoiceStatus.CANCELLED, InvoiceStatus.PAID))
                .isFalse();
    }

    @Test
    @DisplayName("Should calculate due date correctly")
    void shouldCalculateDueDateCorrectly() {
        // Given
        LocalDateTime invoiceDate = LocalDateTime.of(2024, 1, 1, 10, 0);

        // When & Then
        LocalDateTime net15 = billingService.calculateDueDate(invoiceDate, "NET_15");
        assertThat(net15).isEqualTo(invoiceDate.plusDays(15));

        LocalDateTime net30 = billingService.calculateDueDate(invoiceDate, "NET_30");
        assertThat(net30).isEqualTo(invoiceDate.plusDays(30));

        LocalDateTime immediate = billingService.calculateDueDate(invoiceDate, "IMMEDIATE");
        assertThat(immediate).isEqualTo(invoiceDate.plusHours(24));

        LocalDateTime defaultTerms = billingService.calculateDueDate(invoiceDate, "UNKNOWN");
        assertThat(defaultTerms).isEqualTo(invoiceDate.plusDays(30));
    }

    @Test
    @DisplayName("Should check invoice overdue status correctly")
    void shouldCheckInvoiceOverdueStatusCorrectly() {
        // Given - Overdue invoice
        testInvoice.setStatus(InvoiceStatus.SENT);
        testInvoice.setDueDate(LocalDateTime.now().minusDays(1));
        
        when(invoiceRepository.findByInvoiceNumber("INV-20240101-ABC123"))
                .thenReturn(Optional.of(testInvoice));

        // When
        boolean isOverdue = billingService.isInvoiceOverdue("INV-20240101-ABC123");

        // Then
        assertThat(isOverdue).isTrue();

        // Given - Not overdue invoice
        testInvoice.setDueDate(LocalDateTime.now().plusDays(1));

        // When
        boolean isNotOverdue = billingService.isInvoiceOverdue("INV-20240101-ABC123");

        // Then
        assertThat(isNotOverdue).isFalse();
    }

    @Test
    @DisplayName("Should update invoice successfully")
    void shouldUpdateInvoiceSuccessfully() {
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

        when(invoiceRepository.findByInvoiceNumber("INV-20240101-ABC123"))
                .thenReturn(Optional.of(testInvoice));
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(testInvoice);

        // When
        InvoiceResponse response = billingService.updateInvoice("INV-20240101-ABC123", updateRequest);

        // Then
        assertThat(response).isNotNull();
        verify(invoiceRepository).findByInvoiceNumber("INV-20240101-ABC123");
        verify(invoiceRepository).save(any(Invoice.class));
    }

    @Test
    @DisplayName("Should cancel invoice successfully")
    void shouldCancelInvoiceSuccessfully() {
        // Given
        when(invoiceRepository.findByInvoiceNumber("INV-20240101-ABC123"))
                .thenReturn(Optional.of(testInvoice));
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(testInvoice);

        // When
        billingService.cancelInvoice("INV-20240101-ABC123", "Customer request");

        // Then
        verify(invoiceRepository).findByInvoiceNumber("INV-20240101-ABC123");
        verify(invoiceRepository).save(any(Invoice.class));
    }

    @Test
    @DisplayName("Should throw exception when cancelling paid invoice")
    void shouldThrowExceptionWhenCancellingPaidInvoice() {
        // Given
        testInvoice.setStatus(InvoiceStatus.PAID);
        when(invoiceRepository.findByInvoiceNumber("INV-20240101-ABC123"))
                .thenReturn(Optional.of(testInvoice));

        // When & Then
        assertThatThrownBy(() -> billingService.cancelInvoice("INV-20240101-ABC123", "Reason"))
                .isInstanceOf(BillingException.class)
                .hasMessageContaining("Cannot cancel paid invoice");
    }

    @Test
    @DisplayName("Should get customer pricing tier")
    void shouldGetCustomerPricingTier() {
        // When
        PricingTierResponse response = billingService.getCustomerPricingTier("customer-123");

        // Then
        assertThat(response).isNotNull();
        assertThat(response.tierName()).isEqualTo("STANDARD");
        assertThat(response.discountPercentage()).isEqualTo(new BigDecimal("5.0"));
        assertThat(response.description()).isEqualTo("Standard customer pricing");
    }

    @Test
    @DisplayName("Should calculate taxes correctly")
    void shouldCalculateTaxesCorrectly() {
        // Given
        TaxCalculationRequest request = new TaxCalculationRequest(
                "123 Main St, City, CA",
                new BigDecimal("100.00"),
                "STANDARD",
                "SHIPMENT"
        );

        // When
        TaxCalculationResponse response = billingService.calculateTaxes(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.totalTax()).isEqualTo(new BigDecimal("8.50"));
        assertThat(response.taxRate()).isEqualTo(new BigDecimal("8.5"));
        assertThat(response.taxJurisdiction()).isEqualTo("California, USA");
    }
}