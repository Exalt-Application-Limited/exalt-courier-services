package com.gogidix.courier.hqadmin.service.policy.impl;

import com.socialecommerceecosystem.hqadmin.model.GlobalRegion;
import com.socialecommerceecosystem.hqadmin.model.policy.ApprovalStatus;
import com.socialecommerceecosystem.hqadmin.model.policy.GlobalPolicy;
import com.socialecommerceecosystem.hqadmin.model.policy.PolicyType;
import com.socialecommerceecosystem.hqadmin.repository.GlobalRegionRepository;
import com.socialecommerceecosystem.hqadmin.repository.policy.GlobalPolicyRepository;
import com.socialecommerceecosystem.hqadmin.service.policy.GlobalPolicyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of the GlobalPolicyService interface.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class GlobalPolicyServiceImpl implements GlobalPolicyService {

    private final GlobalPolicyRepository globalPolicyRepository;
    private final GlobalRegionRepository globalRegionRepository;

    @Override
    public List<GlobalPolicy> getAllPolicies() {
        return globalPolicyRepository.findAll();
    }

    @Override
    public Optional<GlobalPolicy> getPolicyById(Long id) {
        return globalPolicyRepository.findById(id);
    }

    @Override
    public Optional<GlobalPolicy> getPolicyByKey(String policyKey) {
        return globalPolicyRepository.findByPolicyKey(policyKey);
    }

    @Override
    @Transactional
    public GlobalPolicy createPolicy(GlobalPolicy policy) {
        log.info("Creating new global policy with key: {}", policy.getPolicyKey());
        
        // Verify that the policy key is unique
        if (globalPolicyRepository.findByPolicyKey(policy.getPolicyKey()).isPresent()) {
            throw new IllegalArgumentException("A policy with key " + policy.getPolicyKey() + " already exists");
        }
        
        // Verify that the global region exists if provided
        if (policy.getGlobalRegion() != null && policy.getGlobalRegion().getId() != null) {
            GlobalRegion region = globalRegionRepository.findById(policy.getGlobalRegion().getId())
                .orElseThrow(() -> new IllegalArgumentException("Global region not found with id: " + policy.getGlobalRegion().getId()));
            policy.setGlobalRegion(region);
        }
        
        // Set default values if not provided
        if (policy.getApprovalStatus() == null) {
            policy.setApprovalStatus(ApprovalStatus.DRAFT);
        }
        
        if (policy.getVersionNumber() == null) {
            policy.setVersionNumber("1.0");
        }
        
        return globalPolicyRepository.save(policy);
    }

    @Override
    @Transactional
    public GlobalPolicy updatePolicy(Long id, GlobalPolicy policyDetails) {
        log.info("Updating global policy with id: {}", id);
        
        GlobalPolicy existingPolicy = globalPolicyRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Global policy not found with id: " + id));
        
        // If the approval status is APPROVED, we shouldn't allow updates
        if (ApprovalStatus.APPROVED.equals(existingPolicy.getApprovalStatus())) {
            throw new IllegalArgumentException("Cannot update an approved policy. Create a new version instead.");
        }
        
        // Update fields
        existingPolicy.setPolicyKey(policyDetails.getPolicyKey());
        existingPolicy.setName(policyDetails.getName());
        existingPolicy.setDescription(policyDetails.getDescription());
        existingPolicy.setPolicyContent(policyDetails.getPolicyContent());
        existingPolicy.setPolicyType(policyDetails.getPolicyType());
        existingPolicy.setIsActive(policyDetails.getIsActive());
        existingPolicy.setIsMandatory(policyDetails.getIsMandatory());
        existingPolicy.setEffectiveDate(policyDetails.getEffectiveDate());
        existingPolicy.setExpirationDate(policyDetails.getExpirationDate());
        existingPolicy.setVersionNumber(policyDetails.getVersionNumber());
        existingPolicy.setLastUpdatedBy(policyDetails.getLastUpdatedBy());
        
        // Update global region if provided
        if (policyDetails.getGlobalRegion() != null && policyDetails.getGlobalRegion().getId() != null) {
            GlobalRegion newRegion = globalRegionRepository.findById(policyDetails.getGlobalRegion().getId())
                .orElseThrow(() -> new IllegalArgumentException("Global region not found with id: " + policyDetails.getGlobalRegion().getId()));
            existingPolicy.setGlobalRegion(newRegion);
        } else {
            existingPolicy.setGlobalRegion(null);
        }
        
        // When updating, set the approval status back to DRAFT unless specified otherwise
        if (policyDetails.getApprovalStatus() != null) {
            existingPolicy.setApprovalStatus(policyDetails.getApprovalStatus());
        } else {
            existingPolicy.setApprovalStatus(ApprovalStatus.DRAFT);
        }
        
        return globalPolicyRepository.save(existingPolicy);
    }

    @Override
    @Transactional
    public void deletePolicy(Long id) {
        log.info("Deleting global policy with id: {}", id);
        
        GlobalPolicy policy = globalPolicyRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Global policy not found with id: " + id));
        
        // Don't allow deletion of approved and active policies
        if (ApprovalStatus.APPROVED.equals(policy.getApprovalStatus()) && Boolean.TRUE.equals(policy.getIsActive())) {
            throw new IllegalArgumentException("Cannot delete an approved and active policy. Deactivate it first.");
        }
        
        globalPolicyRepository.delete(policy);
    }

    @Override
    public List<GlobalPolicy> getAllActivePolicies() {
        return globalPolicyRepository.findByIsActiveTrue();
    }

    @Override
    public List<GlobalPolicy> getPoliciesByType(PolicyType policyType) {
        return globalPolicyRepository.findByPolicyType(policyType);
    }

    @Override
    public List<GlobalPolicy> getPoliciesByRegion(Long regionId) {
        log.debug("Getting policies for region id: {}", regionId);
        
        GlobalRegion region = globalRegionRepository.findById(regionId)
            .orElseThrow(() -> new IllegalArgumentException("Global region not found with id: " + regionId));
        
        return globalPolicyRepository.findByGlobalRegion(region);
    }

    @Override
    public List<GlobalPolicy> getPoliciesByRegionAndType(Long regionId, PolicyType policyType) {
        log.debug("Getting policies for region id: {} and type: {}", regionId, policyType);
        
        GlobalRegion region = globalRegionRepository.findById(regionId)
            .orElseThrow(() -> new IllegalArgumentException("Global region not found with id: " + regionId));
        
        return globalPolicyRepository.findByGlobalRegionAndPolicyType(region, policyType);
    }

    @Override
    public List<GlobalPolicy> getGlobalPolicies() {
        return globalPolicyRepository.findByGlobalRegionIsNull();
    }

    @Override
    public List<GlobalPolicy> getMandatoryPolicies() {
        return globalPolicyRepository.findByIsMandatoryTrue();
    }

    @Override
    public List<GlobalPolicy> getPoliciesByApprovalStatus(ApprovalStatus approvalStatus) {
        return globalPolicyRepository.findByApprovalStatus(approvalStatus);
    }

    @Override
    public List<GlobalPolicy> searchPoliciesByName(String searchText) {
        return globalPolicyRepository.findByNameContainingIgnoreCase(searchText);
    }

    @Override
    public List<GlobalPolicy> searchPoliciesByContent(String searchText) {
        return globalPolicyRepository.findByPolicyContentContainingIgnoreCase(searchText);
    }

    @Override
    public List<GlobalPolicy> getEffectivePolicies() {
        return globalPolicyRepository.findEffectivePolicies(LocalDateTime.now());
    }

    @Override
    public List<GlobalPolicy> getFuturePolicies() {
        return globalPolicyRepository.findFuturePolicies(LocalDateTime.now());
    }

    @Override
    public List<GlobalPolicy> getExpiredPolicies() {
        return globalPolicyRepository.findExpiredPolicies(LocalDateTime.now());
    }

    @Override
    public Map<PolicyType, Long> countPoliciesByType() {
        List<Object[]> results = globalPolicyRepository.countPoliciesByType();
        Map<PolicyType, Long> countMap = new HashMap<>();
        
        for (Object[] result : results) {
            PolicyType policyType = (PolicyType) result[0];
            Long count = ((Number) result[1]).longValue();
            countMap.put(policyType, count);
        }
        
        return countMap;
    }

    @Override
    @Transactional
    public GlobalPolicy approvePolicy(Long id, String approvedBy) {
        log.info("Approving global policy with id: {}", id);
        
        GlobalPolicy policy = globalPolicyRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Global policy not found with id: " + id));
        
        // Only policies in PENDING_APPROVAL status can be approved
        if (!ApprovalStatus.PENDING_APPROVAL.equals(policy.getApprovalStatus())) {
            throw new IllegalArgumentException("Only policies in PENDING_APPROVAL status can be approved");
        }
        
        policy.setApprovalStatus(ApprovalStatus.APPROVED);
        policy.setApprovedBy(approvedBy);
        policy.setApprovedAt(LocalDateTime.now());
        
        return globalPolicyRepository.save(policy);
    }

    @Override
    @Transactional
    public GlobalPolicy rejectPolicy(Long id, String rejectedBy, String rejectionReason) {
        log.info("Rejecting global policy with id: {}", id);
        
        GlobalPolicy policy = globalPolicyRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Global policy not found with id: " + id));
        
        // Only policies in PENDING_APPROVAL status can be rejected
        if (!ApprovalStatus.PENDING_APPROVAL.equals(policy.getApprovalStatus())) {
            throw new IllegalArgumentException("Only policies in PENDING_APPROVAL status can be rejected");
        }
        
        policy.setApprovalStatus(ApprovalStatus.REJECTED);
        policy.setLastUpdatedBy(rejectedBy);
        
        // Add rejection reason to the description
        String updatedDescription = policy.getDescription();
        if (updatedDescription == null) {
            updatedDescription = "";
        }
        updatedDescription += "\n\nRejection Reason: " + rejectionReason;
        policy.setDescription(updatedDescription);
        
        return globalPolicyRepository.save(policy);
    }

    @Override
    @Transactional
    public GlobalPolicy submitPolicyForApproval(Long id) {
        log.info("Submitting global policy with id: {} for approval", id);
        
        GlobalPolicy policy = globalPolicyRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Global policy not found with id: " + id));
        
        // Only policies in DRAFT or REJECTED status can be submitted for approval
        if (!ApprovalStatus.DRAFT.equals(policy.getApprovalStatus()) && 
            !ApprovalStatus.REJECTED.equals(policy.getApprovalStatus())) {
            throw new IllegalArgumentException("Only policies in DRAFT or REJECTED status can be submitted for approval");
        }
        
        policy.setApprovalStatus(ApprovalStatus.PENDING_APPROVAL);
        
        return globalPolicyRepository.save(policy);
    }

    @Override
    @Transactional
    public GlobalPolicy createNewPolicyVersion(Long existingPolicyId, GlobalPolicy newPolicyDetails) {
        log.info("Creating new version of global policy with id: {}", existingPolicyId);
        
        GlobalPolicy existingPolicy = globalPolicyRepository.findById(existingPolicyId)
            .orElseThrow(() -> new IllegalArgumentException("Global policy not found with id: " + existingPolicyId));
        
        // Create a new policy based on the existing one
        GlobalPolicy newPolicy = GlobalPolicy.builder()
            .policyKey(existingPolicy.getPolicyKey())
            .name(newPolicyDetails.getName() != null ? newPolicyDetails.getName() : existingPolicy.getName())
            .description(newPolicyDetails.getDescription() != null ? newPolicyDetails.getDescription() : existingPolicy.getDescription())
            .policyContent(newPolicyDetails.getPolicyContent() != null ? newPolicyDetails.getPolicyContent() : existingPolicy.getPolicyContent())
            .policyType(newPolicyDetails.getPolicyType() != null ? newPolicyDetails.getPolicyType() : existingPolicy.getPolicyType())
            .isActive(false) // New versions are inactive by default
            .isMandatory(newPolicyDetails.getIsMandatory() != null ? newPolicyDetails.getIsMandatory() : existingPolicy.getIsMandatory())
            .effectiveDate(newPolicyDetails.getEffectiveDate())
            .expirationDate(newPolicyDetails.getExpirationDate())
            .globalRegion(existingPolicy.getGlobalRegion())
            .approvalStatus(ApprovalStatus.DRAFT) // New versions start as drafts
            .lastUpdatedBy(newPolicyDetails.getLastUpdatedBy())
            .build();
        
        // Increment version number
        String currentVersion = existingPolicy.getVersionNumber();
        if (currentVersion != null) {
            try {
                String[] parts = currentVersion.split("\\.");
                int major = Integer.parseInt(parts[0]);
                int minor = Integer.parseInt(parts[1]);
                
                if (newPolicyDetails.getVersionNumber() != null) {
                    newPolicy.setVersionNumber(newPolicyDetails.getVersionNumber());
                } else {
                    // Increment minor version by default
                    newPolicy.setVersionNumber(major + "." + (minor + 1));
                }
            } catch (Exception e) {
                // If parsing fails, just use 1.0 as the version
                newPolicy.setVersionNumber("1.0");
            }
        } else {
            newPolicy.setVersionNumber("1.0");
        }
        
        // Mark the existing policy as superseded if it's approved
        if (ApprovalStatus.APPROVED.equals(existingPolicy.getApprovalStatus())) {
            existingPolicy.setApprovalStatus(ApprovalStatus.SUPERSEDED);
            globalPolicyRepository.save(existingPolicy);
        }
        
        return globalPolicyRepository.save(newPolicy);
    }

    @Override
    @Transactional
    public GlobalPolicy applyGlobalPolicyToRegion(String policyKey, Long regionId) {
        log.info("Applying global policy with key: {} to region with id: {}", policyKey, regionId);
        
        GlobalPolicy globalPolicy = globalPolicyRepository.findByPolicyKey(policyKey)
            .filter(policy -> policy.getGlobalRegion() == null)
            .orElseThrow(() -> new IllegalArgumentException("Global policy not found with key: " + policyKey));
        
        GlobalRegion region = globalRegionRepository.findById(regionId)
            .orElseThrow(() -> new IllegalArgumentException("Global region not found with id: " + regionId));
        
        // Check if this policy already exists for this region
        Optional<GlobalPolicy> existingRegionalPolicy = globalPolicyRepository.findAll().stream()
            .filter(policy -> policy.getPolicyKey().equals(policyKey) && 
                   region.equals(policy.getGlobalRegion()))
            .findFirst();
        
        if (existingRegionalPolicy.isPresent()) {
            throw new IllegalArgumentException("This policy already exists for the specified region");
        }
        
        // Create a new regional policy based on the global one
        GlobalPolicy regionalPolicy = GlobalPolicy.builder()
            .policyKey(globalPolicy.getPolicyKey())
            .name(globalPolicy.getName())
            .description(globalPolicy.getDescription())
            .policyContent(globalPolicy.getPolicyContent())
            .policyType(globalPolicy.getPolicyType())
            .isActive(globalPolicy.getIsActive())
            .isMandatory(globalPolicy.getIsMandatory())
            .effectiveDate(globalPolicy.getEffectiveDate())
            .expirationDate(globalPolicy.getExpirationDate())
            .globalRegion(region)
            .versionNumber(globalPolicy.getVersionNumber())
            .approvalStatus(ApprovalStatus.DRAFT) // New regional policies start as drafts
            .lastUpdatedBy(globalPolicy.getLastUpdatedBy())
            .build();
            
        return globalPolicyRepository.save(regionalPolicy);
    }

    @Override
    @Transactional
    public List<GlobalPolicy> applyGlobalPoliciesToRegion(List<String> policyKeys, Long regionId) {
        log.info("Applying global policies to region id: {}", regionId);
        
        GlobalRegion region = globalRegionRepository.findById(regionId)
            .orElseThrow(() -> new IllegalArgumentException("Global region not found with id: " + regionId));
        
        List<GlobalPolicy> newPolicies = new ArrayList<>();
        
        for (String policyKey : policyKeys) {
            try {
                GlobalPolicy newPolicy = applyGlobalPolicyToRegion(policyKey, regionId);
                newPolicies.add(newPolicy);
            } catch (IllegalArgumentException e) {
                log.warn("Skipping policy with key {} for region {}: {}", policyKey, regionId, e.getMessage());
            }
        }
        
        return newPolicies;
    }
}
