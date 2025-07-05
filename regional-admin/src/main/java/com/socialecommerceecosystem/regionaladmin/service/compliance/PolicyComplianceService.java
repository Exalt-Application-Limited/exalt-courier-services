package com.gogidix.courier.regionaladmin.service.compliance;

import com.socialecommerceecosystem.regionaladmin.model.Policy;
import com.socialecommerceecosystem.regionaladmin.model.PolicyStatus;
import com.socialecommerceecosystem.regionaladmin.repository.PolicyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for generating policy compliance reports.
 * Analyzes policy adherence and enforcement within the region.
 */
@Service
public class PolicyComplianceService {

    private static final Logger logger = LoggerFactory.getLogger(PolicyComplianceService.class);

    @Autowired
    private PolicyRepository policyRepository;

    /**
     * Generates a report on policy compliance by type.
     * 
     * @return Map of policy types to compliance statistics
     */
    public Map<String, Map<String, Object>> generatePolicyComplianceByTypeReport() {
        logger.info("Generating policy compliance by type report");
        
        // Get all active policies
        List<Policy> activePolicies = policyRepository.findEffectivePolicies(
                PolicyStatus.ACTIVE, LocalDateTime.now());
        
        // Group policies by type
        Map<String, List<Policy>> policiesByType = activePolicies.stream()
                .collect(Collectors.groupingBy(Policy::getPolicyType));
        
        // Calculate compliance statistics for each type
        Map<String, Map<String, Object>> complianceByType = new HashMap<>();
        
        policiesByType.forEach((type, policies) -> {
            Map<String, Object> stats = new HashMap<>();
            
            // Calculate average time since policy activation
            OptionalDouble avgDaysSinceActivation = policies.stream()
                    .map(p -> p.getUpdatedAt())
                    .filter(Objects::nonNull)
                    .mapToLong(date -> ChronoUnit.DAYS.between(date, LocalDateTime.now()))
                    .average();
            
            stats.put("policyCount", policies.size());
            stats.put("avgDaysSinceActivation", avgDaysSinceActivation.orElse(0));
            stats.put("withExpiryDate", policies.stream().filter(p -> p.getEffectiveTo() != null).count());
            stats.put("withoutExpiryDate", policies.stream().filter(p -> p.getEffectiveTo() == null).count());
            stats.put("highPriorityCount", policies.stream().filter(p -> p.getPriority() != null && p.getPriority() > 8).count());
            
            complianceByType.put(type, stats);
        });
        
        return complianceByType;
    }
    
    /**
     * Analyzes policy enforcement effectiveness.
     * 
     * @return Map containing policy enforcement metrics
     */
    public Map<String, Object> analyzeEnforcementEffectiveness() {
        logger.info("Analyzing policy enforcement effectiveness");
        
        Map<String, Object> analysis = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        
        // Get all policies
        List<Policy> allPolicies = policyRepository.findAll();
        
        // Calculate active vs. inactive policies
        long totalPolicies = allPolicies.size();
        long activePolicies = allPolicies.stream()
                .filter(p -> p.getStatus() == PolicyStatus.ACTIVE)
                .count();
        
        // Calculate expired policies that should be inactive
        long expiredButNotInactive = allPolicies.stream()
                .filter(p -> p.getStatus() == PolicyStatus.ACTIVE)
                .filter(p -> p.getEffectiveTo() != null && p.getEffectiveTo().isBefore(now))
                .count();
        
        // Calculate policies with conflicting priorities (same type, overlapping dates)
        Map<String, List<Policy>> policiesByType = allPolicies.stream()
                .filter(p -> p.getStatus() == PolicyStatus.ACTIVE)
                .collect(Collectors.groupingBy(Policy::getPolicyType));
        
        long conflictingPolicies = 0;
        for (List<Policy> policies : policiesByType.values()) {
            // Check for policies with same priority
            Map<Integer, List<Policy>> policiesByPriority = policies.stream()
                    .filter(p -> p.getPriority() != null)
                    .collect(Collectors.groupingBy(Policy::getPriority));
            
            for (List<Policy> sameTypeSamePriority : policiesByPriority.values()) {
                if (sameTypeSamePriority.size() > 1) {
                    conflictingPolicies += sameTypeSamePriority.size();
                }
            }
        }
        
        // Save results
        analysis.put("totalPolicies", totalPolicies);
        analysis.put("activePolicies", activePolicies);
        analysis.put("inactivePolicies", totalPolicies - activePolicies);
        analysis.put("activeToTotalRatio", totalPolicies > 0 ? (double) activePolicies / totalPolicies : 0);
        analysis.put("expiredButStillActive", expiredButNotInactive);
        analysis.put("policiesWithConflictingPriorities", conflictingPolicies);
        
        // Calculate compliance score based on metrics (0-100)
        double complianceScore = calculateComplianceScore(totalPolicies, expiredButNotInactive, conflictingPolicies);
        analysis.put("overallComplianceScore", complianceScore);
        
        return analysis;
    }
    
    /**
     * Calculates a compliance score based on various metrics.
     * 
     * @param totalPolicies Total number of policies
     * @param expiredButActive Number of expired policies that are still active
     * @param conflictingPolicies Number of policies with conflicts
     * @return A compliance score between 0 and 100
     */
    private double calculateComplianceScore(long totalPolicies, long expiredButActive, long conflictingPolicies) {
        if (totalPolicies == 0) {
            return 100; // No policies means nothing to violate
        }
        
        // Calculate penalties
        double expiredPenalty = 20.0 * ((double) expiredButActive / totalPolicies);
        double conflictPenalty = 20.0 * ((double) conflictingPolicies / totalPolicies);
        
        // Start with perfect score and subtract penalties
        double score = 100.0 - expiredPenalty - conflictPenalty;
        
        // Ensure score is between 0 and 100
        return Math.max(0, Math.min(100, score));
    }
    
    /**
     * Identifies policies that are not being properly enforced.
     * 
     * @return List of policies with enforcement issues
     */
    public List<Map<String, Object>> identifyEnforcementIssues() {
        logger.info("Identifying policy enforcement issues");
        
        List<Map<String, Object>> issues = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        // Check for expired but active policies
        List<Policy> expiredPolicies = policyRepository.findAll().stream()
                .filter(p -> p.getStatus() == PolicyStatus.ACTIVE)
                .filter(p -> p.getEffectiveTo() != null && p.getEffectiveTo().isBefore(now))
                .collect(Collectors.toList());
        
        for (Policy policy : expiredPolicies) {
            Map<String, Object> issue = new HashMap<>();
            issue.put("policyId", policy.getPolicyId());
            issue.put("issueType", "EXPIRED_BUT_ACTIVE");
            issue.put("description", "Policy has expired on " + policy.getEffectiveTo() + " but is still active");
            issue.put("remediation", "Deactivate the policy or extend its effective date");
            issues.add(issue);
        }
        
        // Check for inactive policies that should be active
        List<Policy> inactivePolicies = policyRepository.findAll().stream()
                .filter(p -> p.getStatus() == PolicyStatus.INACTIVE)
                .filter(p -> p.getEffectiveFrom() != null && p.getEffectiveFrom().isBefore(now))
                .filter(p -> p.getEffectiveTo() == null || p.getEffectiveTo().isAfter(now))
                .collect(Collectors.toList());
        
        for (Policy policy : inactivePolicies) {
            Map<String, Object> issue = new HashMap<>();
            issue.put("policyId", policy.getPolicyId());
            issue.put("issueType", "INACTIVE_BUT_CURRENT");
            issue.put("description", "Policy is currently within its effective date range but is inactive");
            issue.put("remediation", "Activate the policy or adjust its effective date range");
            issues.add(issue);
        }
        
        // Check for conflicting priority policies
        Map<String, List<Policy>> policiesByType = policyRepository.findAll().stream()
                .filter(p -> p.getStatus() == PolicyStatus.ACTIVE)
                .collect(Collectors.groupingBy(Policy::getPolicyType));
        
        for (String type : policiesByType.keySet()) {
            List<Policy> policies = policiesByType.get(type);
            
            // Group by priority
            Map<Integer, List<Policy>> policiesByPriority = policies.stream()
                    .filter(p -> p.getPriority() != null)
                    .collect(Collectors.groupingBy(Policy::getPriority));
            
            for (Map.Entry<Integer, List<Policy>> entry : policiesByPriority.entrySet()) {
                if (entry.getValue().size() > 1) {
                    // There are multiple policies with the same type and priority
                    for (Policy policy : entry.getValue()) {
                        Map<String, Object> issue = new HashMap<>();
                        issue.put("policyId", policy.getPolicyId());
                        issue.put("issueType", "PRIORITY_CONFLICT");
                        issue.put("description", "Multiple policies of type '" + type + "' have the same priority (" + entry.getKey() + ")");
                        issue.put("remediation", "Adjust the priority of one of the conflicting policies");
                        issues.add(issue);
                    }
                }
            }
        }
        
        return issues;
    }
}
