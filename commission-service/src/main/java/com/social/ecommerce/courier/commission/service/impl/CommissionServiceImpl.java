package com.gogidix.courierservices.commission.$1;

import com.gogidix.courier.commission.model.*;
import com.gogidix.courier.commission.repository.CommissionEntryRepository;
import com.gogidix.courier.commission.service.CommissionRuleService;
import com.gogidix.courier.commission.service.CommissionService;
import com.gogidix.courier.commission.service.PartnerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class CommissionServiceImpl implements CommissionService {

    
    
    @Autowired
    private CommissionEntryRepository commissionEntryRepository;
    
    @Autowired
    private PartnerService partnerService;
    
    @Autowired
    private CommissionRuleService commissionRuleService;
    
    @Override
    @Transactional
    public CommissionEntry calculateCommission(String partnerId, String orderId, double amount, LocalDateTime transactionDate) {
        logger.info("Calculating commission for partner ID: {}, order ID: {}, amount: {}", 
                partnerId, orderId, amount);
        
        Partner partner = partnerService.getPartner(partnerId);
        CommissionRule rule = commissionRuleService.findApplicableRule(partnerId, amount, transactionDate.toLocalDate());
        
        BigDecimal baseAmount = BigDecimal.valueOf(amount);
        BigDecimal commissionAmount = rule.calculateCommission(baseAmount);
        
        CommissionEntry entry = new CommissionEntry();
        entry.setPartner(partner);
        entry.setRule(rule);
        entry.setOrderId(orderId);
        entry.setBaseAmount(baseAmount);
        entry.setCommissionAmount(commissionAmount);
        entry.setTransactionDate(transactionDate);
        entry.setStatus(CommissionStatus.CALCULATED);
        
        return commissionEntryRepository.save(entry);
    }
    
    @Override
    public CommissionEntry getCommissionEntry(String entryId) {
        logger.debug("Getting commission entry with ID: {}", entryId);
        return commissionEntryRepository.findById(entryId)
                .orElseThrow(() -> new EntityNotFoundException("Commission entry not found with ID: " + entryId));
    }
    
    @Override
    @Transactional
    public CommissionEntry updateCommissionStatus(String entryId, CommissionStatus status) {
        logger.info("Updating commission entry status to {} for entry ID: {}", status, entryId);
        
        CommissionEntry entry = getCommissionEntry(entryId);
        entry.setStatus(status);
        
        return commissionEntryRepository.save(entry);
    }
    
    @Override
    @Transactional
    public void deleteCommissionEntry(String entryId) {
        logger.info("Deleting commission entry with ID: {}", entryId);
        
        // Verify entry exists
        if (!commissionEntryRepository.existsById(entryId)) {
            throw new EntityNotFoundException("Commission entry not found with ID: " + entryId);
        }
        
        commissionEntryRepository.deleteById(entryId);
    }
    
    @Override
    public List<CommissionEntry> findCommissionsByPartner(String partnerId) {
        logger.debug("Finding commission entries for partner ID: {}", partnerId);
        return commissionEntryRepository.findByPartnerId(partnerId);
    }
    
    @Override
    public List<CommissionEntry> findCommissionsByPartnerAndDateRange(String partnerId, LocalDateTime startDate, LocalDateTime endDate) {
        logger.debug("Finding commission entries for partner ID: {} between {} and {}", 
                partnerId, startDate, endDate);
        return commissionEntryRepository.findByPartnerIdAndTransactionDateBetween(partnerId, startDate, endDate);
    }
    
    @Override
    public List<CommissionEntry> findCommissionsByStatus(CommissionStatus status) {
        logger.debug("Finding commission entries with status: {}", status);
        return commissionEntryRepository.findByStatus(status);
    }
    
    @Override
    public List<CommissionEntry> findCommissionsByOrderId(String orderId) {
        logger.debug("Finding commission entries for order ID: {}", orderId);
        return commissionEntryRepository.findByOrderId(orderId);
    }
    
    @Override
    public List<CommissionEntry> findUnpaidCommissions() {
        logger.debug("Finding unpaid commission entries");
        return commissionEntryRepository.findUnpaidCommissions(CommissionStatus.APPROVED);
    }
    
    @Override
    public List<CommissionEntry> findUnpaidCommissionsByPartner(String partnerId) {
        logger.debug("Finding unpaid commission entries for partner ID: {}", partnerId);
        return commissionEntryRepository.findUnpaidCommissionsByPartner(partnerId, CommissionStatus.APPROVED);
    }
    
    @Override
    @Transactional
    public void recalculateCommissions(LocalDate startDate, LocalDate endDate) {
        logger.info("Recalculating commissions between {} and {}", startDate, endDate);
        
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        
        List<CommissionEntry> entries = commissionEntryRepository.findByTransactionDateBetween(startDateTime, endDateTime);
        
        for (CommissionEntry entry : entries) {
            try {
                CommissionRule rule = commissionRuleService.findApplicableRule(
                        entry.getPartner().getId(), 
                        entry.getBaseAmount().doubleValue(), 
                        entry.getTransactionDate().toLocalDate());
                
                BigDecimal recalculatedAmount = rule.calculateCommission(entry.getBaseAmount());
                
                // Update if amount changed
                if (!recalculatedAmount.equals(entry.getCommissionAmount())) {
                    entry.setRule(rule);
                    entry.setCommissionAmount(recalculatedAmount);
                    entry.setStatus(CommissionStatus.CALCULATED); // Reset to calculated
                    commissionEntryRepository.save(entry);
                    
                    logger.info("Updated commission entry ID: {} with new amount: {}", 
                            entry.getId(), recalculatedAmount);
                }
            } catch (Exception e) {
                logger.error("Error recalculating commission for entry ID: {}: {}", entry.getId(), e.getMessage());
            }
        }
    }
    
    @Override
    @Transactional
    public List<CommissionEntry> bulkCalculateCommissions(List<OrderData> orderDataList) {
        logger.info("Performing bulk commission calculation for {} orders", orderDataList.size());
        
        List<CommissionEntry> result = new ArrayList<>();
        
        for (OrderData orderData : orderDataList) {
            try {
                CommissionEntry entry = calculateCommission(
                        orderData.getPartnerId(),
                        orderData.getOrderId(),
                        orderData.getAmount(),
                        orderData.getTransactionDate());
                
                result.add(entry);
            } catch (Exception e) {
                logger.error("Error calculating commission for order ID: {}: {}", 
                        orderData.getOrderId(), e.getMessage());
            }
        }
        
        return result;
    }
    
    @Override
    @Transactional
    public int recalculateCommissions(LocalDateTime startDateTime, LocalDateTime endDateTime, CommissionStatus status) {
        logger.info("Recalculating commissions from {} to {} with status {}", startDateTime, endDateTime, status);
        
        // Find all commission entries in the date range with the specified status
        List<CommissionEntry> entries = commissionEntryRepository.findByTransactionDateBetweenAndStatus(
                startDateTime, endDateTime, status);
                
        int recalculatedCount = 0;
        for (CommissionEntry entry : entries) {
            try {
                // Recalculate the commission using current rules
                BigDecimal newCommissionAmount = calculateCommissionAmount(
                        entry.getPartner().getId(), 
                        entry.getBaseAmount(), 
                        entry.getTransactionDate());
                
                if (!entry.getCommissionAmount().equals(newCommissionAmount)) {
                    entry.setCommissionAmount(newCommissionAmount);
                    entry.setUpdatedAt(LocalDateTime.now());
                    commissionEntryRepository.save(entry);
                    recalculatedCount++;
                    logger.debug("Recalculated commission for entry {}: {} -> {}", 
                            entry.getId(), entry.getCommissionAmount(), newCommissionAmount);
                }
            } catch (Exception e) {
                logger.error("Error recalculating commission for entry {}: {}", entry.getId(), e.getMessage());
            }
        }
        
        logger.info("Successfully recalculated {} commission entries", recalculatedCount);
        return recalculatedCount;
    }
    
    private BigDecimal calculateCommissionAmount(String partnerId, BigDecimal baseAmount, LocalDateTime transactionDate) {
        // Simplified calculation - in reality would use commission rules
        // For now just return a default percentage
        return baseAmount.multiply(new BigDecimal("0.05")); // 5% commission
    }
}

