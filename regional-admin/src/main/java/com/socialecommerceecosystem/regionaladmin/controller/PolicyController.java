package com.gogidix.courier.regionaladmin.controller;

import com.socialecommerceecosystem.regionaladmin.model.Policy;
import com.socialecommerceecosystem.regionaladmin.model.PolicyStatus;
import com.socialecommerceecosystem.regionaladmin.repository.PolicyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing policies in the Regional Admin application.
 */
@RestController
@RequestMapping("/api/policies")
public class PolicyController {

    private static final Logger logger = LoggerFactory.getLogger(PolicyController.class);

    @Autowired
    private PolicyRepository policyRepository;

    /**
     * Get all policies.
     * 
     * @return List of all policies
     */
    @GetMapping
    public ResponseEntity<List<Policy>> getAllPolicies() {
        logger.info("Getting all policies");
        List<Policy> policies = policyRepository.findAll();
        return ResponseEntity.ok(policies);
    }

    /**
     * Get a policy by ID.
     * 
     * @param id The policy ID
     * @return The policy if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Policy> getPolicyById(@PathVariable Long id) {
        logger.info("Getting policy with ID: {}", id);
        Optional<Policy> policy = policyRepository.findById(id);
        return policy.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get policies by type.
     * 
     * @param type The policy type
     * @return List of policies of the specified type
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Policy>> getPoliciesByType(@PathVariable String type) {
        logger.info("Getting policies of type: {}", type);
        List<Policy> policies = policyRepository.findByPolicyType(type);
        return ResponseEntity.ok(policies);
    }

    /**
     * Get effective policies that are currently active.
     * 
     * @return List of active and effective policies
     */
    @GetMapping("/effective")
    public ResponseEntity<List<Policy>> getEffectivePolicies() {
        logger.info("Getting effective policies");
        List<Policy> policies = policyRepository.findEffectivePolicies(PolicyStatus.ACTIVE, LocalDateTime.now());
        return ResponseEntity.ok(policies);
    }

    /**
     * Get effective policies of a specific type that are currently active.
     * 
     * @param type The policy type
     * @return List of active and effective policies of the specified type
     */
    @GetMapping("/effective/type/{type}")
    public ResponseEntity<List<Policy>> getEffectivePoliciesByType(@PathVariable String type) {
        logger.info("Getting effective policies of type: {}", type);
        List<Policy> policies = policyRepository.findEffectivePoliciesByType(type, PolicyStatus.ACTIVE, LocalDateTime.now());
        return ResponseEntity.ok(policies);
    }

    /**
     * Get the counts of policies by status.
     * 
     * @return Map of policy status counts
     */
    @GetMapping("/stats")
    public ResponseEntity<String> getPolicyStats() {
        logger.info("Getting policy statistics");
        
        long totalPolicies = policyRepository.count();
        long activePolicies = policyRepository.findAll().stream()
                                           .filter(p -> p.getStatus() == PolicyStatus.ACTIVE)
                                           .count();
        long inactivePolicies = policyRepository.findAll().stream()
                                             .filter(p -> p.getStatus() == PolicyStatus.INACTIVE)
                                             .count();
        long draftPolicies = policyRepository.findAll().stream()
                                          .filter(p -> p.getStatus() == PolicyStatus.DRAFT)
                                          .count();
        
        String stats = String.format("Total Policies: %d\nActive: %d\nInactive: %d\nDraft: %d",
                                    totalPolicies, activePolicies, inactivePolicies, draftPolicies);
        
        return ResponseEntity.ok(stats);
    }
}
