package com.gogidix.courier.commission.service;

import com.gogidix.courier.commission.model.CommissionRule;
import com.gogidix.courier.commission.model.CommissionStatus;
import com.gogidix.courier.commission.model.Partner;
import com.gogidix.courier.commission.model.PartnerType;
import com.gogidix.courier.commission.model.RateType;
import com.gogidix.courier.commission.repository.CommissionRuleRepository;
import com.gogidix.courier.commission.service.CommissionRuleService;
import com.gogidix.courier.commission.service.PartnerService;
// import com.socialecommerceecosystem.utils.money.MoneyUtils; // TODO: Add shared money utils
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CommissionRuleServiceImpl implements CommissionRuleService {

    
    
    @Autowired
    private CommissionRuleRepository ruleRepository;
    
    @Autowired
    private PartnerService partnerService;
    
    @Override
    public CommissionRule createRule(CommissionRule rule) {
        log.info("Creating new commission rule: {}", rule.getName());
        return ruleRepository.save(rule);
    }
    
    @Override
    public CommissionRule getRule(String ruleId) {
        log.debug("Getting commission rule with ID: {}", ruleId);
        return ruleRepository.findById(ruleId)
                .orElseThrow(() -> new EntityNotFoundException("Commission rule not found with ID: " + ruleId));
    }
    
    @Override
    public CommissionRule updateRule(CommissionRule rule) {
        log.info("Updating commission rule with ID: {}", rule.getId());
        
        // Verify rule exists
        if (!ruleRepository.existsById(rule.getId())) {
            throw new EntityNotFoundException("Commission rule not found with ID: " + rule.getId());
        }
        
        return ruleRepository.save(rule);
    }
    
    @Override
    public CommissionRule updateRuleStatus(String ruleId, CommissionStatus status) {
        log.info("Updating status to {} for commission rule with ID: {}", status, ruleId);
        
        CommissionRule rule = getRule(ruleId);
        rule.setStatus(status);
        
        return ruleRepository.save(rule);
    }    
    @Override
    public void deleteRule(String ruleId) {
        log.info("Deleting commission rule with ID: {}", ruleId);
        
        // Verify rule exists
        if (!ruleRepository.existsById(ruleId)) {
            throw new EntityNotFoundException("Commission rule not found with ID: " + ruleId);
        }
        
        ruleRepository.deleteById(ruleId);
    }
    
    @Override
    public List<CommissionRule> findRulesByPartnerType(PartnerType partnerType) {
        log.debug("Finding commission rules by partner type: {}", partnerType);
        return ruleRepository.findByPartnerType(partnerType);
    }
    
    @Override
    public List<CommissionRule> findRulesByStatus(CommissionStatus status) {
        log.debug("Finding commission rules by status: {}", status);
        return ruleRepository.findByStatus(status);
    }
    
    @Override
    public List<CommissionRule> findActiveRulesByPartnerTypeAndDate(PartnerType partnerType, LocalDate date) {
        log.debug("Finding active commission rules by partner type: {} and date: {}", partnerType, date);
        return ruleRepository.findActiveRulesByPartnerTypeAndDate(partnerType, CommissionStatus.APPROVED, date);
    }
    
    @Override
    public List<CommissionRule> findActiveRulesByDate(LocalDate date) {
        log.debug("Finding active commission rules by date: {}", date);
        return ruleRepository.findActiveRulesByDate(CommissionStatus.APPROVED, date);
    }
    
    @Override
    public CommissionRule findApplicableRule(String partnerId, double amount, LocalDate date) {
        log.debug("Finding applicable commission rule for partner ID: {}, amount: {}, date: {}", 
                partnerId, amount, date);
        
        Partner partner = partnerService.getPartner(partnerId);
        PartnerType partnerType = partner.getPartnerType();
        BigDecimal amountAsBigDecimal = BigDecimal.valueOf(amount);
        
        List<CommissionRule> activeRules = findActiveRulesByPartnerTypeAndDate(partnerType, date);
        
        // Find the first applicable rule based on priority and amount constraints
        Optional<CommissionRule> applicableRule = activeRules.stream()
                .filter(rule -> {
                    boolean meetsMinAmount = rule.getMinAmount() == null || 
                            amountAsBigDecimal.compareTo(rule.getMinAmount()) >= 0;
                    boolean meetsMaxAmount = rule.getMaxAmount() == null || 
                            amountAsBigDecimal.compareTo(rule.getMaxAmount()) <= 0;
                    return meetsMinAmount && meetsMaxAmount;
                })
                .sorted(Comparator.comparing(CommissionRule::getPriority).reversed())
                .findFirst();
        
        return applicableRule.orElseThrow(() -> 
                new IllegalStateException("No applicable commission rule found for partner ID: " + 
                        partnerId + ", amount: " + amount + ", date: " + date));
    }    /**
     * Calculate commission amount based on rule and order amount.
     * Performs commission calculation based on the rate type.
     * 
     * @param rule the commission rule
     * @param orderAmount the order amount
     * @return the calculated commission amount
     */
    @Override
    public BigDecimal calculateCommissionAmount(CommissionRule rule, BigDecimal orderAmount) {
        log.debug("Calculating commission amount for rule ID: {} and order amount: {}", 
                rule.getId(), orderAmount);
        
        BigDecimal result;
        
        // Calculate commission based on rate type
        if (rule.getRateType() == RateType.PERCENTAGE) {
            // For percentage rates, calculate the percentage of the order amount
            // Simple percentage calculation: (orderAmount * rateValue) / 100
            result = orderAmount.multiply(rule.getRateValue()).divide(BigDecimal.valueOf(100));
        } else {
            // For fixed rates, just use the fixed rate value
            result = rule.getRateValue();
        }
        
        // Apply minimum amount constraint if set
        if (rule.getMinimumAmount() != null && result.compareTo(rule.getMinimumAmount()) < 0) {
            result = rule.getMinimumAmount();
        }
        
        // Apply maximum amount constraint if set
        if (rule.getMaximumAmount() != null && result.compareTo(rule.getMaximumAmount()) > 0) {
            result = rule.getMaximumAmount();
        }
        
        return result;
    }
}
