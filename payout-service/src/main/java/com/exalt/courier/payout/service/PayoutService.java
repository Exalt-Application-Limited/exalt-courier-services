package com.exalt.courierservices.payout.$1;

import com.exalt.courier.payout.model.Payout;
import java.time.LocalDate;
import java.util.List;

public interface PayoutService {
    
    /**
     * Calculate payouts for all eligible couriers for the given date range
     */
    void calculatePayouts(LocalDate startDate, LocalDate endDate);
    
    /**
     * Process pending payouts and mark them as paid
     */
    void processPendingPayouts();
    
    /**
     * Get payout history for a specific courier
     */
    List<Payout> getPayoutHistory(String courierId);
    
    /**
     * Get payout details
     */
    Payout getPayoutDetails(String payoutId);
}
