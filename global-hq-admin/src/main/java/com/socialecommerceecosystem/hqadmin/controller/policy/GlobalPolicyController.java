package com.gogidix.courier.hqadmin.controller.policy;

import com.socialecommerceecosystem.hqadmin.model.policy.ApprovalStatus;
import com.socialecommerceecosystem.hqadmin.model.policy.GlobalPolicy;
import com.socialecommerceecosystem.hqadmin.model.policy.PolicyType;
import com.socialecommerceecosystem.hqadmin.service.policy.GlobalPolicyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * REST controller for managing global policies.
 */
@RestController
@RequestMapping("/api/v1/policies")
@RequiredArgsConstructor
@Slf4j
public class GlobalPolicyController {

    private final GlobalPolicyService globalPolicyService;

    /**
     * GET /api/v1/policies : Get all policies
     * 
     * @return the ResponseEntity with status 200 (OK) and the list of policies
     */
    @GetMapping
    public ResponseEntity<List<GlobalPolicy>> getAllPolicies() {
        log.debug("REST request to get all global policies");
        return ResponseEntity.ok(globalPolicyService.getAllPolicies());
    }

    /**
     * GET /api/v1/policies/active : Get all active policies
     * 
     * @return the ResponseEntity with status 200 (OK) and the list of active policies
     */
    @GetMapping("/active")
    public ResponseEntity<List<GlobalPolicy>> getAllActivePolicies() {
        log.debug("REST request to get all active global policies");
        return ResponseEntity.ok(globalPolicyService.getAllActivePolicies());
    }

    /**
     * GET /api/v1/policies/{id} : Get a policy by id
     * 
     * @param id the id of the policy to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the policy, or with status 404 (Not Found)
     */
    @GetMapping("/{id}")
    public ResponseEntity<GlobalPolicy> getPolicy(@PathVariable Long id) {
        log.debug("REST request to get global policy : {}", id);
        return globalPolicyService.getPolicyById(id)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Policy not found with id: " + id));
    }

    /**
     * GET /api/v1/policies/key/{policyKey} : Get a policy by key
     * 
     * @param policyKey the key of the policy to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the policy, or with status 404 (Not Found)
     */
    @GetMapping("/key/{policyKey}")
    public ResponseEntity<GlobalPolicy> getPolicyByKey(@PathVariable String policyKey) {
        log.debug("REST request to get global policy by key: {}", policyKey);
        return globalPolicyService.getPolicyByKey(policyKey)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Policy not found with key: " + policyKey));
    }

    /**
     * GET /api/v1/policies/type/{policyType} : Get policies by type
     * 
     * @param policyType the type of policies to retrieve
     * @return the ResponseEntity with status 200 (OK) and the list of policies
     */
    @GetMapping("/type/{policyType}")
    public ResponseEntity<List<GlobalPolicy>> getPoliciesByType(@PathVariable PolicyType policyType) {
        log.debug("REST request to get global policies by type: {}", policyType);
        return ResponseEntity.ok(globalPolicyService.getPoliciesByType(policyType));
    }

    /**
     * GET /api/v1/policies/region/{regionId} : Get policies for a region
     * 
     * @param regionId the id of the region to filter by
     * @return the ResponseEntity with status 200 (OK) and the list of policies for the region
     */
    @GetMapping("/region/{regionId}")
    public ResponseEntity<List<GlobalPolicy>> getPoliciesByRegion(@PathVariable Long regionId) {
        log.debug("REST request to get global policies for region: {}", regionId);
        
        try {
            List<GlobalPolicy> policies = globalPolicyService.getPoliciesByRegion(regionId);
            return ResponseEntity.ok(policies);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * GET /api/v1/policies/region/{regionId}/type/{policyType} : Get policies for a region and type
     * 
     * @param regionId the id of the region to filter by
     * @param policyType the type to filter by
     * @return the ResponseEntity with status 200 (OK) and the list of policies for the region and type
     */
    @GetMapping("/region/{regionId}/type/{policyType}")
    public ResponseEntity<List<GlobalPolicy>> getPoliciesByRegionAndType(
            @PathVariable Long regionId,
            @PathVariable PolicyType policyType) {
        log.debug("REST request to get global policies for region: {} and type: {}", regionId, policyType);
        
        try {
            List<GlobalPolicy> policies = globalPolicyService.getPoliciesByRegionAndType(regionId, policyType);
            return ResponseEntity.ok(policies);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * GET /api/v1/policies/global : Get global policies (not associated with any region)
     * 
     * @return the ResponseEntity with status 200 (OK) and the list of global policies
     */
    @GetMapping("/global")
    public ResponseEntity<List<GlobalPolicy>> getGlobalPolicies() {
        log.debug("REST request to get global policies (not associated with any region)");
        return ResponseEntity.ok(globalPolicyService.getGlobalPolicies());
    }

    /**
     * GET /api/v1/policies/mandatory : Get mandatory policies
     * 
     * @return the ResponseEntity with status 200 (OK) and the list of mandatory policies
     */
    @GetMapping("/mandatory")
    public ResponseEntity<List<GlobalPolicy>> getMandatoryPolicies() {
        log.debug("REST request to get mandatory global policies");
        return ResponseEntity.ok(globalPolicyService.getMandatoryPolicies());
    }

    /**
     * GET /api/v1/policies/status/{approvalStatus} : Get policies by approval status
     * 
     * @param approvalStatus the approval status to filter by
     * @return the ResponseEntity with status 200 (OK) and the list of policies with the specified approval status
     */
    @GetMapping("/status/{approvalStatus}")
    public ResponseEntity<List<GlobalPolicy>> getPoliciesByApprovalStatus(@PathVariable ApprovalStatus approvalStatus) {
        log.debug("REST request to get global policies by approval status: {}", approvalStatus);
        return ResponseEntity.ok(globalPolicyService.getPoliciesByApprovalStatus(approvalStatus));
    }

    /**
     * GET /api/v1/policies/search/name : Search policies by name
     * 
     * @param searchText the text to search for in policy names
     * @return the ResponseEntity with status 200 (OK) and the list of matching policies
     */
    @GetMapping("/search/name")
    public ResponseEntity<List<GlobalPolicy>> searchPoliciesByName(@RequestParam String searchText) {
        log.debug("REST request to search global policies by name containing: {}", searchText);
        return ResponseEntity.ok(globalPolicyService.searchPoliciesByName(searchText));
    }

    /**
     * GET /api/v1/policies/search/content : Search policies by content
     * 
     * @param searchText the text to search for in policy content
     * @return the ResponseEntity with status 200 (OK) and the list of matching policies
     */
    @GetMapping("/search/content")
    public ResponseEntity<List<GlobalPolicy>> searchPoliciesByContent(@RequestParam String searchText) {
        log.debug("REST request to search global policies by content containing: {}", searchText);
        return ResponseEntity.ok(globalPolicyService.searchPoliciesByContent(searchText));
    }

    /**
     * GET /api/v1/policies/effective : Get currently effective policies
     * 
     * @return the ResponseEntity with status 200 (OK) and the list of effective policies
     */
    @GetMapping("/effective")
    public ResponseEntity<List<GlobalPolicy>> getEffectivePolicies() {
        log.debug("REST request to get currently effective global policies");
        return ResponseEntity.ok(globalPolicyService.getEffectivePolicies());
    }

    /**
     * GET /api/v1/policies/future : Get policies that will be effective in the future
     * 
     * @return the ResponseEntity with status 200 (OK) and the list of future policies
     */
    @GetMapping("/future")
    public ResponseEntity<List<GlobalPolicy>> getFuturePolicies() {
        log.debug("REST request to get future global policies");
        return ResponseEntity.ok(globalPolicyService.getFuturePolicies());
    }

    /**
     * GET /api/v1/policies/expired : Get expired policies
     * 
     * @return the ResponseEntity with status 200 (OK) and the list of expired policies
     */
    @GetMapping("/expired")
    public ResponseEntity<List<GlobalPolicy>> getExpiredPolicies() {
        log.debug("REST request to get expired global policies");
        return ResponseEntity.ok(globalPolicyService.getExpiredPolicies());
    }

    /**
     * GET /api/v1/policies/count-by-type : Count policies by type
     * 
     * @return the ResponseEntity with status 200 (OK) and the map of type to policy count
     */
    @GetMapping("/count-by-type")
    public ResponseEntity<Map<PolicyType, Long>> countPoliciesByType() {
        log.debug("REST request to count global policies by type");
        return ResponseEntity.ok(globalPolicyService.countPoliciesByType());
    }

    /**
     * POST /api/v1/policies : Create a new policy
     * 
     * @param policy the policy to create
     * @return the ResponseEntity with status 201 (Created) and with body the new policy
     */
    @PostMapping
    public ResponseEntity<GlobalPolicy> createPolicy(@Valid @RequestBody GlobalPolicy policy) {
        log.debug("REST request to save global policy : {}", policy);
        if (policy.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A new policy cannot already have an ID");
        }
        
        try {
            GlobalPolicy result = globalPolicyService.createPolicy(policy);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * PUT /api/v1/policies/{id} : Update an existing policy
     * 
     * @param id the id of the policy to update
     * @param policy the policy to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated policy
     */
    @PutMapping("/{id}")
    public ResponseEntity<GlobalPolicy> updatePolicy(
            @PathVariable Long id, 
            @Valid @RequestBody GlobalPolicy policy) {
        log.debug("REST request to update global policy : {}", policy);
        if (policy.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Policy ID must not be null");
        }
        if (!id.equals(policy.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "IDs don't match");
        }
        
        try {
            GlobalPolicy result = globalPolicyService.updatePolicy(id, policy);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * DELETE /api/v1/policies/{id} : Delete a policy
     * 
     * @param id the id of the policy to delete
     * @return the ResponseEntity with status 204 (NO_CONTENT)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePolicy(@PathVariable Long id) {
        log.debug("REST request to delete global policy : {}", id);
        try {
            globalPolicyService.deletePolicy(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * POST /api/v1/policies/{id}/approve : Approve a policy
     * 
     * @param id the id of the policy to approve
     * @param approvedBy the user approving the policy
     * @return the ResponseEntity with status 200 (OK) and with body the approved policy
     */
    @PostMapping("/{id}/approve")
    public ResponseEntity<GlobalPolicy> approvePolicy(
            @PathVariable Long id,
            @RequestParam String approvedBy) {
        log.debug("REST request to approve global policy : {}", id);
        
        try {
            GlobalPolicy result = globalPolicyService.approvePolicy(id, approvedBy);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * POST /api/v1/policies/{id}/reject : Reject a policy
     * 
     * @param id the id of the policy to reject
     * @param rejectedBy the user rejecting the policy
     * @param rejectionReason the reason for rejection
     * @return the ResponseEntity with status 200 (OK) and with body the rejected policy
     */
    @PostMapping("/{id}/reject")
    public ResponseEntity<GlobalPolicy> rejectPolicy(
            @PathVariable Long id,
            @RequestParam String rejectedBy,
            @RequestParam String rejectionReason) {
        log.debug("REST request to reject global policy : {}", id);
        
        try {
            GlobalPolicy result = globalPolicyService.rejectPolicy(id, rejectedBy, rejectionReason);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * POST /api/v1/policies/{id}/submit : Submit a policy for approval
     * 
     * @param id the id of the policy to submit
     * @return the ResponseEntity with status 200 (OK) and with body the submitted policy
     */
    @PostMapping("/{id}/submit")
    public ResponseEntity<GlobalPolicy> submitPolicyForApproval(@PathVariable Long id) {
        log.debug("REST request to submit global policy for approval : {}", id);
        
        try {
            GlobalPolicy result = globalPolicyService.submitPolicyForApproval(id);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * POST /api/v1/policies/{id}/version : Create a new version of an existing policy
     * 
     * @param id the id of the existing policy
     * @param newPolicyDetails the new policy details
     * @return the ResponseEntity with status 201 (Created) and with body the new policy version
     */
    @PostMapping("/{id}/version")
    public ResponseEntity<GlobalPolicy> createNewPolicyVersion(
            @PathVariable Long id,
            @Valid @RequestBody GlobalPolicy newPolicyDetails) {
        log.debug("REST request to create new version of global policy : {}", id);
        
        try {
            GlobalPolicy result = globalPolicyService.createNewPolicyVersion(id, newPolicyDetails);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * POST /api/v1/policies/apply-to-region/{regionId} : Apply a global policy to a region
     * 
     * @param regionId the id of the region to apply the policy to
     * @param policyKey the key of the policy to apply
     * @return the ResponseEntity with status 201 (Created) and with body the new regional policy
     */
    @PostMapping("/apply-to-region/{regionId}")
    public ResponseEntity<GlobalPolicy> applyGlobalPolicyToRegion(
            @PathVariable Long regionId,
            @RequestParam String policyKey) {
        log.debug("REST request to apply global policy {} to region {}", policyKey, regionId);
        
        try {
            GlobalPolicy result = globalPolicyService.applyGlobalPolicyToRegion(policyKey, regionId);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * POST /api/v1/policies/apply-multiple-to-region/{regionId} : Apply multiple global policies to a region
     * 
     * @param regionId the id of the region to apply policies to
     * @param policyKeys the list of policy keys to apply
     * @return the ResponseEntity with status 201 (Created) and with body the list of new regional policies
     */
    @PostMapping("/apply-multiple-to-region/{regionId}")
    public ResponseEntity<List<GlobalPolicy>> applyGlobalPoliciesToRegion(
            @PathVariable Long regionId,
            @RequestBody List<String> policyKeys) {
        log.debug("REST request to apply global policies to region {}: {}", regionId, policyKeys);
        
        try {
            List<GlobalPolicy> result = globalPolicyService.applyGlobalPoliciesToRegion(policyKeys, regionId);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
