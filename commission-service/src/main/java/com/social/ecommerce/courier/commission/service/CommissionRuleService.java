package com.exalt.courierservices.commission.$1;

import com.exalt.courier.commission.model.CommissionRule;
import com.exalt.courier.commission.model.CommissionStatus;
import com.exalt.courier.commission.model.PartnerType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface CommissionRuleService {
    
    /**
     * Create a new commission rule
     */
    CommissionRule createRule(CommissionRule rule);
    
    /**
     * Get rule by ID
     */
    CommissionRule getRule(String ruleId);
    
    /**
     * Update existing rule
     */
    CommissionRule updateRule(CommissionRule rule);
    
    /**
     * Update rule status
     */
    CommissionRule updateRuleStatus(String ruleId, CommissionStatus status);
    
    /**
     * Delete rule
     */
    void deleteRule(String ruleId);
    
    /**
     * Find rules by partner type
     */
    List<CommissionRule> findRulesByPartnerType(PartnerType partnerType);
    
    /**
     * Find rules by status
     */
    List<CommissionRule> findRulesByStatus(CommissionStatus status);
    
    /**
     * Find active rules by partner type and date
     */
    List<CommissionRule> findActiveRulesByPartnerTypeAndDate(PartnerType partnerType, LocalDate date);
    
    /**
     * Find active rules by date
     */
    List<CommissionRule> findActiveRulesByDate(LocalDate date);
    
    /**
     * Find applicable rule for a specific partner and amount
     * This will consider rule priority, active dates, and min/max amount constraints
     */
    CommissionRule findApplicableRule(String partnerId, double amount, LocalDate date);
    
    /**
     * Calculate commission amount based on rule and order amount
     * 
     * @param rule the commission rule
     * @param orderAmount the order amount
     * @return the calculated commission amount
     */
    BigDecimal calculateCommissionAmount(CommissionRule rule, BigDecimal orderAmount);
}