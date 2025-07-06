package com.gogidix.courier.commission.scheduler;

import com.gogidix.courier.commission.model.CommissionStatus;
import com.gogidix.courier.commission.model.PaymentStatus;
import com.gogidix.courier.commission.service.CommissionService;
import com.gogidix.courier.commission.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Scheduled tasks for commission processing and payment generation.
 */
@Component
@Slf4j
public class CommissionScheduler {
    
    private final CommissionService commissionService;
    private final PaymentService paymentService;
    
    @Autowired
    public CommissionScheduler(CommissionService commissionService, PaymentService paymentService) {
        this.commissionService = commissionService;
        this.paymentService = paymentService;
    }
    
    /**
     * Daily job to process pending payments.
     * Runs at 2:00 AM every day.
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void processPendingPayments() {
        log.info("Starting scheduled job: Process pending payments at {}", LocalDateTime.now());
        try {
            paymentService.processPendingPayments();
            log.info("Successfully processed pending payments");
        } catch (Exception e) {
            log.error("Error processing pending payments: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Weekly job to generate partner payments for unpaid commissions.
     * Runs at 1:00 AM every Monday.
     */
    @Scheduled(cron = "0 0 1 ? * MON")
    public void generateWeeklyPayments() {
        log.info("Starting scheduled job: Generate weekly payments at {}", LocalDateTime.now());
        try {
            paymentService.generateAllPayments();
            log.info("Successfully generated payments");
        } catch (Exception e) {
            log.error("Error generating weekly payments: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Monthly job to recalculate commissions for the previous month if rules changed.
     * Runs at 3:00 AM on the 1st day of each month.
     */
    @Scheduled(cron = "0 0 3 1 * ?")
    public void recalculateMonthlyCommissions() {
        log.info("Starting scheduled job: Recalculate monthly commissions at {}", LocalDateTime.now());
        try {
            LocalDate startDate = LocalDate.now().minusMonths(1).withDayOfMonth(1);
            LocalDate endDate = LocalDate.now().withDayOfMonth(1).minusDays(1);
            
            int recalculatedCount = commissionService.recalculateCommissions(
                    startDate.atStartOfDay(), 
                    endDate.atTime(23, 59, 59),
                    CommissionStatus.CALCULATED
            );
            
            log.info("Successfully recalculated {} commissions for period: {} to {}", 
                    recalculatedCount, startDate, endDate);
        } catch (Exception e) {
            log.error("Error recalculating monthly commissions: {}", e.getMessage(), e);
        }
    }
}
