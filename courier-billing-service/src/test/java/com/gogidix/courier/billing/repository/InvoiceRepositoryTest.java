package com.gogidix.courier.billing.repository;

import com.gogidix.courier.billing.model.Invoice;
import com.gogidix.courier.billing.model.InvoiceStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Invoice Repository Tests")
class InvoiceRepositoryTest {

    @Autowired
    private InvoiceRepository invoiceRepository;

    private Invoice testInvoice1;
    private Invoice testInvoice2;

    @BeforeEach
    void setUp() {
        invoiceRepository.deleteAll();

        testInvoice1 = Invoice.builder()
                .id(UUID.randomUUID())
                .invoiceNumber("INV-20240101-TEST001")
                .customerId("customer-123")
                .customerName("Test Customer 1")
                .customerEmail("customer1@example.com")
                .billingAddress("123 Main St, City, State")
                .shipmentId("shipment-001")
                .serviceType("STANDARD")
                .description("Test invoice 1")
                .subtotal(new BigDecimal("100.00"))
                .discountAmount(new BigDecimal("5.00"))
                .taxAmount(new BigDecimal("8.55"))
                .totalAmount(new BigDecimal("103.55"))
                .currency("USD")
                .status(InvoiceStatus.DRAFT)
                .dueDate(LocalDateTime.now().plusDays(30))
                .createdAt(LocalDateTime.now())
                .createdBy("SYSTEM")
                .build();

        testInvoice2 = Invoice.builder()
                .id(UUID.randomUUID())
                .invoiceNumber("INV-20240102-TEST002")
                .customerId("customer-456")
                .customerName("Test Customer 2")
                .customerEmail("customer2@example.com")
                .billingAddress("456 Oak Ave, City, State")
                .shipmentId("shipment-002")
                .serviceType("EXPRESS")
                .description("Test invoice 2")
                .subtotal(new BigDecimal("75.00"))
                .discountAmount(new BigDecimal("0.00"))
                .taxAmount(new BigDecimal("6.38"))
                .totalAmount(new BigDecimal("81.38"))
                .currency("USD")
                .status(InvoiceStatus.SENT)
                .dueDate(LocalDateTime.now().plusDays(15))
                .createdAt(LocalDateTime.now().minusDays(1))
                .sentAt(LocalDateTime.now().minusHours(2))
                .createdBy("SYSTEM")
                .build();
    }

    @Test
    @DisplayName("Should save and retrieve invoice by invoice number")
    void shouldSaveAndRetrieveInvoiceByNumber() {
        // Given
        Invoice savedInvoice = invoiceRepository.save(testInvoice1);

        // When
        Optional<Invoice> foundInvoice = invoiceRepository.findByInvoiceNumber("INV-20240101-TEST001");

        // Then
        assertThat(foundInvoice).isPresent();
        assertThat(foundInvoice.get().getId()).isEqualTo(savedInvoice.getId());
        assertThat(foundInvoice.get().getCustomerId()).isEqualTo("customer-123");
        assertThat(foundInvoice.get().getTotalAmount()).isEqualTo(new BigDecimal("103.55"));
        assertThat(foundInvoice.get().getStatus()).isEqualTo(InvoiceStatus.DRAFT);
    }

    @Test
    @DisplayName("Should find invoices by customer ID ordered by creation date")
    void shouldFindInvoicesByCustomerIdOrderedByCreatedAt() {
        // Given
        testInvoice1.setCustomerId("customer-same");
        testInvoice2.setCustomerId("customer-same");
        testInvoice2.setCreatedAt(LocalDateTime.now().plusHours(1)); // Make it newer

        invoiceRepository.save(testInvoice1);
        invoiceRepository.save(testInvoice2);

        // When
        List<Invoice> customerInvoices = invoiceRepository.findByCustomerIdOrderByCreatedAtDesc("customer-same");

        // Then
        assertThat(customerInvoices).hasSize(2);
        assertThat(customerInvoices.get(0).getInvoiceNumber()).isEqualTo("INV-20240102-TEST002"); // Newer first
        assertThat(customerInvoices.get(1).getInvoiceNumber()).isEqualTo("INV-20240101-TEST001");
    }

    @Test
    @DisplayName("Should find invoices by status and date range")
    void shouldFindInvoicesByStatusAndDateRange() {
        // Given
        LocalDateTime fromDate = LocalDateTime.now().minusDays(2);
        LocalDateTime toDate = LocalDateTime.now().plusDays(1);

        testInvoice1.setStatus(InvoiceStatus.SENT);
        testInvoice1.setCreatedAt(LocalDateTime.now().minusHours(6));
        testInvoice2.setStatus(InvoiceStatus.SENT);
        testInvoice2.setCreatedAt(LocalDateTime.now().minusHours(3));

        invoiceRepository.save(testInvoice1);
        invoiceRepository.save(testInvoice2);

        // When
        List<Invoice> sentInvoices = invoiceRepository.findByStatusAndDateRange(
                InvoiceStatus.SENT, fromDate, toDate);

        // Then
        assertThat(sentInvoices).hasSize(2);
        assertThat(sentInvoices).allMatch(invoice -> invoice.getStatus() == InvoiceStatus.SENT);
        assertThat(sentInvoices).allMatch(invoice -> 
                invoice.getCreatedAt().isAfter(fromDate) && invoice.getCreatedAt().isBefore(toDate));
    }

    @Test
    @DisplayName("Should find overdue invoices")
    void shouldFindOverdueInvoices() {
        // Given - Create overdue invoice
        testInvoice1.setStatus(InvoiceStatus.SENT);
        testInvoice1.setDueDate(LocalDateTime.now().minusDays(5)); // 5 days overdue

        testInvoice2.setStatus(InvoiceStatus.SENT);
        testInvoice2.setDueDate(LocalDateTime.now().plusDays(5)); // Not overdue

        invoiceRepository.save(testInvoice1);
        invoiceRepository.save(testInvoice2);

        // When
        List<Invoice> overdueInvoices = invoiceRepository.findOverdueInvoices(LocalDateTime.now());

        // Then
        assertThat(overdueInvoices).hasSize(1);
        assertThat(overdueInvoices.get(0).getInvoiceNumber()).isEqualTo("INV-20240101-TEST001");
        assertThat(overdueInvoices.get(0).getDueDate()).isBefore(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should find invoices by multiple shipment IDs")
    void shouldFindInvoicesByMultipleShipmentIds() {
        // Given
        List<String> shipmentIds = List.of("shipment-001", "shipment-002");
        
        invoiceRepository.save(testInvoice1);
        invoiceRepository.save(testInvoice2);

        // When
        List<Invoice> invoices = invoiceRepository.findByShipmentIdIn(shipmentIds);

        // Then
        assertThat(invoices).hasSize(2);
        assertThat(invoices).extracting(Invoice::getShipmentId)
                .containsExactlyInAnyOrder("shipment-001", "shipment-002");
    }

    @Test
    @DisplayName("Should find invoices by customer and status")
    void shouldFindInvoicesByCustomerAndStatus() {
        // Given
        testInvoice1.setCustomerId("customer-multi-status");
        testInvoice1.setStatus(InvoiceStatus.DRAFT);
        
        testInvoice2.setCustomerId("customer-multi-status");
        testInvoice2.setStatus(InvoiceStatus.SENT);

        Invoice paidInvoice = Invoice.builder()
                .id(UUID.randomUUID())
                .invoiceNumber("INV-20240103-TEST003")
                .customerId("customer-multi-status")
                .customerName("Test Customer")
                .customerEmail("customer@example.com")
                .billingAddress("789 Pine St, City, State")
                .subtotal(new BigDecimal("50.00"))
                .taxAmount(new BigDecimal("4.25"))
                .totalAmount(new BigDecimal("54.25"))
                .currency("USD")
                .status(InvoiceStatus.PAID)
                .createdAt(LocalDateTime.now())
                .paidAt(LocalDateTime.now())
                .createdBy("SYSTEM")
                .build();

        invoiceRepository.save(testInvoice1);
        invoiceRepository.save(testInvoice2);
        invoiceRepository.save(paidInvoice);

        // When
        List<Invoice> draftInvoices = invoiceRepository.findByCustomerIdAndStatus(
                "customer-multi-status", InvoiceStatus.DRAFT);
        List<Invoice> sentInvoices = invoiceRepository.findByCustomerIdAndStatus(
                "customer-multi-status", InvoiceStatus.SENT);
        List<Invoice> paidInvoices = invoiceRepository.findByCustomerIdAndStatus(
                "customer-multi-status", InvoiceStatus.PAID);

        // Then
        assertThat(draftInvoices).hasSize(1);
        assertThat(draftInvoices.get(0).getInvoiceNumber()).isEqualTo("INV-20240101-TEST001");

        assertThat(sentInvoices).hasSize(1);
        assertThat(sentInvoices.get(0).getInvoiceNumber()).isEqualTo("INV-20240102-TEST002");

        assertThat(paidInvoices).hasSize(1);
        assertThat(paidInvoices.get(0).getInvoiceNumber()).isEqualTo("INV-20240103-TEST003");
    }

    @Test
    @DisplayName("Should calculate total amount for customer invoices")
    void shouldCalculateTotalAmountForCustomerInvoices() {
        // Given
        testInvoice1.setCustomerId("customer-total-calc");
        testInvoice2.setCustomerId("customer-total-calc");
        
        invoiceRepository.save(testInvoice1);
        invoiceRepository.save(testInvoice2);

        // When
        BigDecimal totalAmount = invoiceRepository.calculateTotalAmountByCustomerId("customer-total-calc");

        // Then
        BigDecimal expectedTotal = testInvoice1.getTotalAmount().add(testInvoice2.getTotalAmount());
        assertThat(totalAmount).isEqualTo(expectedTotal);
    }

    @Test
    @DisplayName("Should find invoices by subscription ID")
    void shouldFindInvoicesBySubscriptionId() {
        // Given
        String subscriptionId = "sub-12345";
        testInvoice1.setSubscriptionId(subscriptionId);
        testInvoice1.setInvoiceType("SUBSCRIPTION");
        
        invoiceRepository.save(testInvoice1);
        invoiceRepository.save(testInvoice2); // This one has no subscription

        // When
        List<Invoice> subscriptionInvoices = invoiceRepository.findBySubscriptionId(subscriptionId);

        // Then
        assertThat(subscriptionInvoices).hasSize(1);
        assertThat(subscriptionInvoices.get(0).getSubscriptionId()).isEqualTo(subscriptionId);
        assertThat(subscriptionInvoices.get(0).getInvoiceType()).isEqualTo("SUBSCRIPTION");
    }

    @Test
    @DisplayName("Should return empty when invoice not found by number")
    void shouldReturnEmptyWhenInvoiceNotFoundByNumber() {
        // When
        Optional<Invoice> notFound = invoiceRepository.findByInvoiceNumber("NON-EXISTENT-INVOICE");

        // Then
        assertThat(notFound).isEmpty();
    }

    @Test
    @DisplayName("Should handle invoice persistence with all fields")
    void shouldHandleInvoicePersistenceWithAllFields() {
        // Given
        Invoice complexInvoice = Invoice.builder()
                .id(UUID.randomUUID())
                .invoiceNumber("INV-20240104-COMPLEX")
                .customerId("complex-customer")
                .customerName("Complex Customer")
                .customerEmail("complex@example.com")
                .billingAddress("Complex Address Line 1\nLine 2\nCity, State 12345")
                .shipmentId("complex-shipment")
                .subscriptionId("complex-subscription")
                .serviceType("PREMIUM")
                .description("Complex invoice with all fields populated")
                .subtotal(new BigDecimal("999.99"))
                .discountAmount(new BigDecimal("50.00"))
                .taxAmount(new BigDecimal("80.50"))
                .totalAmount(new BigDecimal("1030.49"))
                .currency("USD")
                .status(InvoiceStatus.PAID)
                .invoiceType("SUBSCRIPTION")
                .dueDate(LocalDateTime.now().plusDays(30))
                .createdAt(LocalDateTime.now().minusDays(5))
                .sentAt(LocalDateTime.now().minusDays(3))
                .paidAt(LocalDateTime.now().minusDays(1))
                .lastSentAt(LocalDateTime.now().minusHours(6))
                .createdBy("SYSTEM")
                .updatedBy("ADMIN")
                .build();

        // When
        Invoice saved = invoiceRepository.save(complexInvoice);
        Optional<Invoice> retrieved = invoiceRepository.findByInvoiceNumber("INV-20240104-COMPLEX");

        // Then
        assertThat(retrieved).isPresent();
        Invoice invoice = retrieved.get();
        
        assertThat(invoice.getId()).isEqualTo(saved.getId());
        assertThat(invoice.getInvoiceNumber()).isEqualTo("INV-20240104-COMPLEX");
        assertThat(invoice.getCustomerId()).isEqualTo("complex-customer");
        assertThat(invoice.getSubscriptionId()).isEqualTo("complex-subscription");
        assertThat(invoice.getInvoiceType()).isEqualTo("SUBSCRIPTION");
        assertThat(invoice.getTotalAmount()).isEqualTo(new BigDecimal("1030.49"));
        assertThat(invoice.getStatus()).isEqualTo(InvoiceStatus.PAID);
        assertThat(invoice.getSentAt()).isNotNull();
        assertThat(invoice.getPaidAt()).isNotNull();
        assertThat(invoice.getLastSentAt()).isNotNull();
        assertThat(invoice.getCreatedBy()).isEqualTo("SYSTEM");
        assertThat(invoice.getUpdatedBy()).isEqualTo("ADMIN");
    }
}