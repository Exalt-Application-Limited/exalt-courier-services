package com.exalt.courierservices.commission.$1;

import com.exalt.courier.commission.model.CommissionStatus;
import com.exalt.courier.commission.model.PaymentStatus;
import com.exalt.courier.commission.service.CommissionService;
import com.exalt.courier.commission.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Scheduled tasks for commission processing and payment generation.
 */
@Component
public class CommissionScheduler {
    
    private static final Logger logger = LoggerFactory.getLogger(CommissionScheduler.class);
    
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
        logger.info("Starting scheduled job: Process pending payments at {}", LocalDateTime.now());
        try {
            paymentService.processPendingPayments();
            logger.info("Successfully processed pending payments");
        } catch (Exception e) {
            logger.error("Error processing pending payments: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Weekly job to generate partner payments for unpaid commissions.
     * Runs at 1:00 AM every Monday.
     */
    @Scheduled(cron = "0 0 1 ? * MON")
    public void generateWeeklyPayments() {
        logger.info("Starting scheduled job: Generate weekly payments at {}", LocalDateTime.now());
        try {
            paymentService.generateAllPayments();
            logger.info("Successfully generated payments");
        } catch (Exception e) {
            logger.error("Error generating weekly payments: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Monthly job to recalculate commissions for the previous month if rules changed.
     * Runs at 3:00 AM on the 1st day of each month.
     */
    @Scheduled(cron = "0 0 3 1 * ?")
    public void recalculateMonthlyCommissions() {
        logger.info("Starting scheduled job: Recalculate monthly commissions at {}", LocalDateTime.now());
        try {
            LocalDate startDate = LocalDate.now().minusMonths(1).withDayOfMonth(1);
            LocalDate endDate = LocalDate.now().withDayOfMonth(1).minusDays(1);
            
            int recalculatedCount = commissionService.recalculateCommissions(
                    startDate.atStartOfDay(), 
                    endDate.atTime(23, 59, 59),
                    CommissionStatus.CALCULATED
            );
            
            logger.info("Successfully recalculated {} commissions for period: {} to {}", 
                    recalculatedCount, startDate, endDate);
        } catch (Exception e) {
            logger.error("Error recalculating monthly commissions: {}", e.getMessage(), e);
        }
    }
}
