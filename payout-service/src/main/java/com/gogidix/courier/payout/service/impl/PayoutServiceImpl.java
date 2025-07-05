package com.gogidix.courierservices.payout.$1;

import com.gogidix.courier.payout.model.Earning;
import com.gogidix.courier.payout.model.EarningStatus;
import com.gogidix.courier.payout.model.Payout;
import com.gogidix.courier.payout.model.PayoutStatus;
import com.gogidix.courier.payout.repository.EarningsRepository;
import com.gogidix.courier.payout.repository.PayoutRepository;
import com.gogidix.courier.payout.service.PayoutService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class PayoutServiceImpl implements PayoutService {
    
    
    
    @Autowired
    private EarningsRepository earningsRepository;
    
    @Autowired
    private PayoutRepository payoutRepository;
    
    @Override
    @Transactional
    public void calculatePayouts(LocalDate startDate, LocalDate endDate) {
        logger.info("Calculating payouts for period: {} to {}", startDate, endDate);
        
        // Get all pending earnings for the date range
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        
        List<Earning> pendingEarnings = earningsRepository.findByStatusAndEarnedAtBetween(
                EarningStatus.PENDING, startDateTime, endDateTime);
        
        if (pendingEarnings.isEmpty()) {
            logger.info("No pending earnings found for the period");
            return;
        }
        
        // Group earnings by courier
        Map<String, List<Earning>> earningsByCourier = pendingEarnings.stream()
                .collect(Collectors.groupingBy(Earning::getCourierId));
        
        // Create payout for each courier
        for (Map.Entry<String, List<Earning>> entry : earningsByCourier.entrySet()) {
            String courierId = entry.getKey();
            List<Earning> courierEarnings = entry.getValue();
            
            // Calculate total amount
            BigDecimal totalAmount = courierEarnings.stream()
                    .map(Earning::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // Create payout
            Payout payout = new Payout();
            payout.setCourierId(courierId);
            payout.setAmount(totalAmount);
            payout.setStatus(PayoutStatus.PENDING);
            payout.setPayoutPeriodStart(startDate);
            payout.setPayoutPeriodEnd(endDate);
            payout.setScheduledAt(LocalDateTime.now().plusDays(1)); // Schedule for tomorrow
            
            // Link earnings to payout
            payout.setEarnings(new java.util.HashSet<>(courierEarnings));
            
            // Save payout
            payoutRepository.save(payout);
            
            // Update earnings status
            courierEarnings.forEach(earning -> {
                earning.setStatus(EarningStatus.PROCESSED);
                earningsRepository.save(earning);
            });
            
            logger.info("Created payout for courier {}: {} earnings, total amount: {}", 
                    courierId, courierEarnings.size(), totalAmount);
        }
    }
    
    @Override
    @Transactional
    @Scheduled(cron = "0 0 5 * * ?") // Run at 5:00 AM every day
    public void processPendingPayouts() {
        logger.info("Processing pending payouts");
        
        List<Payout> pendingPayouts = payoutRepository.findByStatus(PayoutStatus.PENDING);
        
        if (pendingPayouts.isEmpty()) {
            logger.info("No pending payouts to process");
            return;
        }
        
        for (Payout payout : pendingPayouts) {
            try {
                // Only process payouts that are scheduled for today or earlier
                if (payout.getScheduledAt().isAfter(LocalDateTime.now())) {
                    logger.info("Skipping payout {} as it's scheduled for later: {}", 
                            payout.getId(), payout.getScheduledAt());
                    continue;
                }
                
                // Here we would integrate with a payment provider to process the actual payment
                // For now, we'll simulate a successful payment
                
                // Update payout status
                payout.setStatus(PayoutStatus.PROCESSING);
                payout.setProcessedAt(LocalDateTime.now());
                payout.setPaymentReference("PAY-" + UUID.randomUUID().toString().substring(0, 8));
                payoutRepository.save(payout);
                
                // Simulate payment processing
                simulatePayment(payout);
                
                // Update earnings status
                for (Earning earning : payout.getEarnings()) {
                    earning.setStatus(EarningStatus.PAID);
                    earningsRepository.save(earning);
                }
                
                // Mark payout as completed
                payout.setStatus(PayoutStatus.COMPLETED);
                payoutRepository.save(payout);
                
                logger.info("Successfully processed payout {}: {} for courier {}", 
                        payout.getId(), payout.getAmount(), payout.getCourierId());
            } catch (Exception e) {
                logger.error("Failed to process payout {}: {}", payout.getId(), e.getMessage(), e);
                
                // Mark payout as failed
                payout.setStatus(PayoutStatus.FAILED);
                payout.setNotes("Failed to process: " + e.getMessage());
                payoutRepository.save(payout);
            }
        }
    }
    
    // Simulate payment integration with external provider
    private void simulatePayment(Payout payout) {
        // In a real implementation, this would make API calls to a payment provider
        logger.info("Simulating payment for payout {}: {} to courier {}", 
                payout.getId(), payout.getAmount(), payout.getCourierId());
        
        // Add random delay to simulate processing time
        try {
            Thread.sleep((long) (Math.random() * 1000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // For testing, let's randomly fail some payments
        if (Math.random() < 0.05) { // 5% failure rate for testing
            throw new RuntimeException("Payment provider rejected the transaction");
        }
    }
    
    @Override
    public List<Payout> getPayoutHistory(String courierId) {
        return payoutRepository.findByCourierId(courierId);
    }
    
    @Override
    public Payout getPayoutDetails(String payoutId) {
        return payoutRepository.findById(payoutId)
                .orElseThrow(() -> new RuntimeException("Payout not found: " + payoutId));
    }
    
    // Weekly payout calculation job
    @Scheduled(cron = "0 0 1 * * MON") // Run at 1:00 AM every Monday
    public void weeklyPayoutCalculation() {
        logger.info("Running weekly payout calculation");
        
        // Calculate for the previous week (Monday to Sunday)
        LocalDate endDate = LocalDate.now().minusDays(1); // Yesterday (Sunday)
        LocalDate startDate = endDate.minusDays(6); // Previous Monday
        
        calculatePayouts(startDate, endDate);
    }
}

