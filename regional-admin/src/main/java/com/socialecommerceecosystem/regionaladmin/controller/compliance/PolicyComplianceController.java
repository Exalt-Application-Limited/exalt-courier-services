package com.exalt.courier.regionaladmin.controller.compliance;

import com.socialecommerceecosystem.regionaladmin.service.compliance.PolicyComplianceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * REST controller for policy compliance reporting.
 * Provides endpoints for analyzing policy compliance and enforcement.
 */
@RestController
@RequestMapping("/api/compliance/policy")
public class PolicyComplianceController {

    private static final Logger logger = LoggerFactory.getLogger(PolicyComplianceController.class);

    @Autowired
    private PolicyComplianceService policyComplianceService;

    /**
     * Get policy compliance statistics by policy type.
     * 
     * @return Map of policy types to compliance statistics
     */
    @GetMapping("/by-type")
    public ResponseEntity<Map<String, Map<String, Object>>> getPolicyComplianceByType() {
        logger.info("Getting policy compliance by type");
        Map<String, Map<String, Object>> report = policyComplianceService.generatePolicyComplianceByTypeReport();
        return ResponseEntity.ok(report);
    }

    /**
     * Get policy enforcement effectiveness analysis.
     * 
     * @return Map containing policy enforcement metrics
     */
    @GetMapping("/enforcement")
    public ResponseEntity<Map<String, Object>> getEnforcementEffectiveness() {
        logger.info("Getting policy enforcement effectiveness");
        Map<String, Object> analysis = policyComplianceService.analyzeEnforcementEffectiveness();
        return ResponseEntity.ok(analysis);
    }

    /**
     * Get a list of policy enforcement issues.
     * 
     * @return List of policies with enforcement issues
     */
    @GetMapping("/issues")
    public ResponseEntity<List<Map<String, Object>>> getEnforcementIssues() {
        logger.info("Getting policy enforcement issues");
        List<Map<String, Object>> issues = policyComplianceService.identifyEnforcementIssues();
        return ResponseEntity.ok(issues);
    }

    /**
     * Get a compliance dashboard summary with key metrics.
     * 
     * @return Map containing key compliance metrics
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getComplianceDashboard() {
        logger.info("Getting compliance dashboard");
        
        // Get enforcement analysis
        Map<String, Object> enforcement = policyComplianceService.analyzeEnforcementEffectiveness();
        
        // Get issues count
        List<Map<String, Object>> issues = policyComplianceService.identifyEnforcementIssues();
        
        // Compile dashboard data
        Map<String, Object> dashboard = Map.of(
            "complianceScore", enforcement.get("overallComplianceScore"),
            "totalPolicies", enforcement.get("totalPolicies"),
            "activePolicies", enforcement.get("activePolicies"),
            "issuesCount", issues.size(),
            "criticalIssuesCount", issues.stream()
                .filter(issue -> "EXPIRED_BUT_ACTIVE".equals(issue.get("issueType")) 
                    || "PRIORITY_CONFLICT".equals(issue.get("issueType")))
                .count()
        );
        
        return ResponseEntity.ok(dashboard);
    }
}
