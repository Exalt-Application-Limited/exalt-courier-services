package com.exalt.courierservices.commission.$1;

import com.exalt.courier.commission.model.CommissionRule;
import com.exalt.courier.commission.model.CommissionStatus;
import com.exalt.courier.commission.model.PartnerType;
import com.exalt.courier.commission.service.CommissionRuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/commission-rules")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class CommissionRuleController {

    
    
    @Autowired
    private CommissionRuleService commissionRuleService;
    
    @PostMapping
    public ResponseEntity<CommissionRule> createRule(@Valid @RequestBody CommissionRule rule) {
        logger.info("REST request to create a commission rule");
        CommissionRule createdRule = commissionRuleService.createRule(rule);
        return new ResponseEntity<>(createdRule, HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CommissionRule> getRule(@PathVariable String id) {
        logger.info("REST request to get commission rule with ID: {}", id);
        CommissionRule rule = commissionRuleService.getRule(id);
        return ResponseEntity.ok(rule);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CommissionRule> updateRule(@PathVariable String id, @Valid @RequestBody CommissionRule rule) {
        logger.info("REST request to update commission rule with ID: {}", id);
        
        // Ensure the ID in the path matches the ID in the body
        if (!id.equals(rule.getId())) {
            return ResponseEntity.badRequest().build();
        }
        
        CommissionRule updatedRule = commissionRuleService.updateRule(rule);
        return ResponseEntity.ok(updatedRule);
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<CommissionRule> updateRuleStatus(
            @PathVariable String id, 
            @RequestParam CommissionStatus status) {
        logger.info("REST request to update status to {} for commission rule with ID: {}", status, id);
        CommissionRule updatedRule = commissionRuleService.updateRuleStatus(id, status);
        return ResponseEntity.ok(updatedRule);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRule(@PathVariable String id) {
        logger.info("REST request to delete commission rule with ID: {}", id);
        commissionRuleService.deleteRule(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping
    public ResponseEntity<List<CommissionRule>> getAllRules(
            @RequestParam(required = false) PartnerType partnerType,
            @RequestParam(required = false) CommissionStatus status) {
        logger.info("REST request to get all commission rules with partner type: {}, status: {}", 
                partnerType, status);
        
        List<CommissionRule> rules;
        
        if (partnerType != null) {
            rules = commissionRuleService.findRulesByPartnerType(partnerType);
        } else if (status != null) {
            rules = commissionRuleService.findRulesByStatus(status);
        } else {
            // Get all rules
            rules = commissionRuleService.findRulesByStatus(null);
        }
        
        return ResponseEntity.ok(rules);
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<CommissionRule>> getActiveRules(
            @RequestParam(required = false) PartnerType partnerType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        logger.info("REST request to get active commission rules with partner type: {}, date: {}", 
                partnerType, date);
        
        List<CommissionRule> rules;
        
        if (partnerType != null) {
            rules = commissionRuleService.findActiveRulesByPartnerTypeAndDate(partnerType, date);
        } else {
            rules = commissionRuleService.findActiveRulesByDate(date);
        }
        
        return ResponseEntity.ok(rules);
    }
    
    @GetMapping("/applicable")
    public ResponseEntity<CommissionRule> getApplicableRule(
            @RequestParam String partnerId, 
            @RequestParam double amount,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        logger.info("REST request to get applicable commission rule for partner ID: {}, amount: {}, date: {}", 
                partnerId, amount, date);
        
        CommissionRule rule = commissionRuleService.findApplicableRule(partnerId, amount, date);
        return ResponseEntity.ok(rule);
    }
}

