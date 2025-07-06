package com.gogidix.courier.billing.repository;

import com.gogidix.courier.billing.model.Invoice;
import com.gogidix.courier.billing.model.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Invoice operations.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {

    /**
     * Find invoice by invoice number.
     */
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    /**
     * Find all invoices for a specific customer.
     */
    List<Invoice> findByCustomerIdOrderByCreatedAtDesc(String customerId);

    /**
     * Find invoices by status.
     */
    List<Invoice> findByStatusOrderByCreatedAtDesc(InvoiceStatus status);

    /**
     * Find invoices by status and date range.
     */
    @Query("SELECT i FROM Invoice i WHERE i.status = :status AND i.createdAt BETWEEN :fromDate AND :toDate ORDER BY i.createdAt DESC")
    List<Invoice> findByStatusAndDateRange(@Param("status") InvoiceStatus status, 
                                         @Param("fromDate") LocalDateTime fromDate, 
                                         @Param("toDate") LocalDateTime toDate);

    /**
     * Find overdue invoices.
     */
    @Query("SELECT i FROM Invoice i WHERE i.status IN ('SENT', 'PARTIALLY_PAID') AND i.dueDate < :currentDate ORDER BY i.dueDate ASC")
    List<Invoice> findOverdueInvoices(@Param("currentDate") LocalDateTime currentDate);

    /**
     * Find invoices by customer and status.
     */
    List<Invoice> findByCustomerIdAndStatusOrderByCreatedAtDesc(String customerId, InvoiceStatus status);

    /**
     * Find invoices by shipment ID.
     */
    List<Invoice> findByShipmentId(String shipmentId);

    /**
     * Find invoices by subscription ID.
     */
    List<Invoice> findBySubscriptionId(String subscriptionId);

    /**
     * Count invoices by customer and status.
     */
    Long countByCustomerIdAndStatus(String customerId, InvoiceStatus status);

    /**
     * Check if invoice number exists.
     */
    boolean existsByInvoiceNumber(String invoiceNumber);
}