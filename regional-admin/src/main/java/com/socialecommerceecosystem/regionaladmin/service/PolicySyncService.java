package com.exalt.courier.regionaladmin.service;

import com.socialecommerceecosystem.regionaladmin.model.Policy;
import com.socialecommerceecosystem.regionaladmin.model.PolicyStatus;
import com.socialecommerceecosystem.regionaladmin.repository.PolicyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

/**
 * Service for synchronizing policy data from HQ Admin.
 * Handles policy events and updates the local policy database.
 */
@Service
public class PolicySyncService {

    private static final Logger logger = LoggerFactory.getLogger(PolicySyncService.class);

    @Autowired
    private PolicyRepository policyRepository;

    /**
     * Handles a policy created event from Kafka.
     * 
     * @param policyData The policy event data
     */
    @Transactional
    public void handlePolicyCreated(Map<String, Object> policyData) {
        logger.info("Processing policy created event");
        
        Long policyId = Long.valueOf(policyData.get("id").toString());
        String policyType = policyData.get("policyType").toString();
        String description = policyData.get("description").toString();
        String content = policyData.get("content").toString();
        
        // Check if the policy already exists (shouldn't normally happen)
        Optional<Policy> existingPolicy = policyRepository.findByPolicyId(policyId);
        if (existingPolicy.isPresent()) {
            logger.warn("Policy already exists with ID: {}. Updating instead of creating.", policyId);
            updateExistingPolicy(existingPolicy.get(), policyData);
            return;
        }
        
        // Create and save a new policy
        Policy policy = new Policy();
        policy.setPolicyId(policyId);
        policy.setPolicyType(policyType);
        policy.setDescription(description);
        policy.setContent(content);
        
        // Set optional fields if present
        if (policyData.containsKey("effectiveFrom")) {
            policy.setEffectiveFrom(parseDateTime(policyData.get("effectiveFrom").toString()));
        }
        
        if (policyData.containsKey("effectiveTo")) {
            policy.setEffectiveTo(parseDateTime(policyData.get("effectiveTo").toString()));
        }
        
        if (policyData.containsKey("priority")) {
            policy.setPriority(Integer.parseInt(policyData.get("priority").toString()));
        }
        
        if (policyData.containsKey("status")) {
            policy.setStatus(PolicyStatus.valueOf(policyData.get("status").toString()));
        } else {
            policy.setStatus(PolicyStatus.DRAFT);
        }
        
        // Save the policy
        policy = policyRepository.save(policy);
        logger.info("Created new policy: {}", policy);
    }

    /**
     * Handles a policy updated event from Kafka.
     * 
     * @param policyData The policy event data
     */
    @Transactional
    public void handlePolicyUpdated(Map<String, Object> policyData) {
        logger.info("Processing policy updated event");
        
        Long policyId = Long.valueOf(policyData.get("id").toString());
        
        // Find the policy
        Optional<Policy> optionalPolicy = policyRepository.findByPolicyId(policyId);
        if (!optionalPolicy.isPresent()) {
            logger.warn("Cannot update policy with ID: {}. Policy not found. Creating instead.", policyId);
            handlePolicyCreated(policyData);
            return;
        }
        
        // Update the policy
        Policy policy = optionalPolicy.get();
        updateExistingPolicy(policy, policyData);
    }

    /**
     * Handles a policy deleted event from Kafka.
     * 
     * @param policyData The policy event data
     */
    @Transactional
    public void handlePolicyDeleted(Map<String, Object> policyData) {
        logger.info("Processing policy deleted event");
        
        Long policyId = Long.valueOf(policyData.get("id").toString());
        
        // Find the policy
        Optional<Policy> optionalPolicy = policyRepository.findByPolicyId(policyId);
        if (!optionalPolicy.isPresent()) {
            logger.warn("Cannot delete policy with ID: {}. Policy not found.", policyId);
            return;
        }
        
        // Delete the policy
        policyRepository.delete(optionalPolicy.get());
        logger.info("Deleted policy with ID: {}", policyId);
    }

    /**
     * Handles a policy activated event from Kafka.
     * 
     * @param policyData The policy event data
     */
    @Transactional
    public void handlePolicyActivated(Map<String, Object> policyData) {
        logger.info("Processing policy activated event");
        
        Long policyId = Long.valueOf(policyData.get("id").toString());
        
        // Find the policy
        Optional<Policy> optionalPolicy = policyRepository.findByPolicyId(policyId);
        if (!optionalPolicy.isPresent()) {
            logger.warn("Cannot activate policy with ID: {}. Policy not found.", policyId);
            return;
        }
        
        // Activate the policy
        Policy policy = optionalPolicy.get();
        policy.setStatus(PolicyStatus.ACTIVE);
        policy = policyRepository.save(policy);
        logger.info("Activated policy with ID: {}", policyId);
    }

    /**
     * Handles a policy deactivated event from Kafka.
     * 
     * @param policyData The policy event data
     */
    @Transactional
    public void handlePolicyDeactivated(Map<String, Object> policyData) {
        logger.info("Processing policy deactivated event");
        
        Long policyId = Long.valueOf(policyData.get("id").toString());
        
        // Find the policy
        Optional<Policy> optionalPolicy = policyRepository.findByPolicyId(policyId);
        if (!optionalPolicy.isPresent()) {
            logger.warn("Cannot deactivate policy with ID: {}. Policy not found.", policyId);
            return;
        }
        
        // Deactivate the policy
        Policy policy = optionalPolicy.get();
        policy.setStatus(PolicyStatus.INACTIVE);
        policy = policyRepository.save(policy);
        logger.info("Deactivated policy with ID: {}", policyId);
    }

    /**
     * Helper method to update an existing policy.
     * 
     * @param policy The policy to update
     * @param policyData The policy data
     * @return The updated policy
     */
    private Policy updateExistingPolicy(Policy policy, Map<String, Object> policyData) {
        // Update basic fields
        if (policyData.containsKey("policyType")) {
            policy.setPolicyType(policyData.get("policyType").toString());
        }
        
        if (policyData.containsKey("description")) {
            policy.setDescription(policyData.get("description").toString());
        }
        
        if (policyData.containsKey("content")) {
            policy.setContent(policyData.get("content").toString());
        }
        
        // Update optional fields if present
        if (policyData.containsKey("effectiveFrom")) {
            policy.setEffectiveFrom(parseDateTime(policyData.get("effectiveFrom").toString()));
        }
        
        if (policyData.containsKey("effectiveTo")) {
            policy.setEffectiveTo(parseDateTime(policyData.get("effectiveTo").toString()));
        }
        
        if (policyData.containsKey("priority")) {
            policy.setPriority(Integer.parseInt(policyData.get("priority").toString()));
        }
        
        if (policyData.containsKey("status")) {
            policy.setStatus(PolicyStatus.valueOf(policyData.get("status").toString()));
        }
        
        // Save the policy
        policy = policyRepository.save(policy);
        logger.info("Updated policy: {}", policy);
        return policy;
    }

    /**
     * Helper method to parse a datetime string.
     * 
     * @param dateTimeStr The datetime string
     * @return The LocalDateTime
     */
    private LocalDateTime parseDateTime(String dateTimeStr) {
        try {
            return LocalDateTime.parse(dateTimeStr);
        } catch (Exception e) {
            logger.warn("Failed to parse datetime string: {}", dateTimeStr, e);
            return null;
        }
    }
}
