package com.gogidix.courierservices.commission.$1;

import com.gogidix.courier.commission.model.CommissionEntry;
import com.gogidix.courier.commission.model.CommissionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface CommissionService {
    
    /**
     * Calculate commission for an order
     */
    CommissionEntry calculateCommission(String partnerId, String orderId, double amount, LocalDateTime transactionDate);
    
    /**
     * Get commission entry by ID
     */
    CommissionEntry getCommissionEntry(String entryId);
    
    /**
     * Update commission entry status
     */
    CommissionEntry updateCommissionStatus(String entryId, CommissionStatus status);
    
    /**
     * Delete commission entry
     */
    void deleteCommissionEntry(String entryId);
    
    /**
     * Find commission entries by partner ID
     */
    List<CommissionEntry> findCommissionsByPartner(String partnerId);
    
    /**
     * Find commission entries by partner ID and date range
     */
    List<CommissionEntry> findCommissionsByPartnerAndDateRange(
            String partnerId, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find commission entries by status
     */
    List<CommissionEntry> findCommissionsByStatus(CommissionStatus status);
    
    /**
     * Find commission entries by order ID
     */
    List<CommissionEntry> findCommissionsByOrderId(String orderId);
    
    /**
     * Find unpaid commission entries (ready for payment)
     */
    List<CommissionEntry> findUnpaidCommissions();
    
    /**
     * Find unpaid commission entries for a specific partner
     */
    List<CommissionEntry> findUnpaidCommissionsByPartner(String partnerId);
    
    /**
     * Recalculate all commissions for a specific date range
     * This is useful when commission rules change and need to be applied retroactively
     */
    void recalculateCommissions(LocalDate startDate, LocalDate endDate);
    
    /**
     * Recalculate commissions with specific datetime range and status filter
     * Used by scheduler for more precise control
     */
    int recalculateCommissions(LocalDateTime startDateTime, LocalDateTime endDateTime, CommissionStatus status);
    
    /**
     * Bulk calculate commissions from order data
     * This is useful for batch processing of orders
     */
    List<CommissionEntry> bulkCalculateCommissions(List<OrderData> orderDataList);
    
    /**
     * Inner class to represent order data for bulk commission calculation
     * Converted to use Lombok annotations for reduced boilerplate.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    class OrderData {
        private String partnerId;
        private String orderId;
        private double amount;
        private LocalDateTime transactionDate;
    }
}
