package com.gogidix.integration.common.controller;

import com.gogidix.integration.common.model.ProviderCredential;
import com.gogidix.integration.common.service.ProviderCredentialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * REST controller for managing shipping provider credentials
 */
@RestController
@RequestMapping("/api/v1/provider-credentials")
@RequiredArgsConstructor
@Slf4j
public class ProviderCredentialController {

    private final ProviderCredentialService credentialService;

    /**
     * Get all provider credentials
     * @return List of all provider credentials
     */
    @GetMapping
    public ResponseEntity<List<ProviderCredential>> getAllCredentials() {
        log.info("REST request to get all provider credentials");
        return ResponseEntity.ok(credentialService.getAllProviderCredentials());
    }

    /**
     * Get provider credential by ID
     * @param id Credential ID
     * @return Credential if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProviderCredential> getCredentialById(@PathVariable Long id) {
        log.info("REST request to get provider credential with id: {}", id);
        return credentialService.getCredentialById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get credential by provider code
     * @param providerCode Provider code
     * @return Credential if found
     */
    @GetMapping("/provider/{providerCode}")
    public ResponseEntity<ProviderCredential> getCredentialByProviderCode(@PathVariable String providerCode) {
        log.info("REST request to get provider credential for provider code: {}", providerCode);
        return credentialService.getCredentialByProviderCode(providerCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create new provider credentials
     * @param credential Credentials to create
     * @return Created credentials
     */
    @PostMapping
    public ResponseEntity<ProviderCredential> createCredential(@Valid @RequestBody ProviderCredential credential) {
        log.info("REST request to create provider credential for provider: {}", 
                credential.getProvider() != null ? credential.getProvider().getProviderCode() : "unknown");
        
        if (credential.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new credential cannot already have an ID").build();
        }
        
        ProviderCredential result = credentialService.createCredential(credential);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * Update existing provider credentials
     * @param id Credential ID
     * @param credential Updated credential data
     * @return Updated credentials
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProviderCredential> updateCredential(
            @PathVariable Long id,
            @Valid @RequestBody ProviderCredential credential) {
        log.info("REST request to update provider credential with id: {}", id);
        
        if (credential.getId() == null) {
            return ResponseEntity.badRequest().header("Failure", "ID must be provided for update").build();
        }
        
        if (!id.equals(credential.getId())) {
            return ResponseEntity.badRequest().header("Failure", "ID in path must match ID in request body").build();
        }
        
        ProviderCredential result = credentialService.updateCredential(id, credential);
        return ResponseEntity.ok(result);
    }

    /**
     * Delete provider credentials
     * @param id Credential ID
     * @return No content if successful
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCredential(@PathVariable Long id) {
        log.info("REST request to delete provider credential with id: {}", id);
        credentialService.deleteCredential(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get all active provider credentials
     * @return List of active credentials
     */
    @GetMapping("/active")
    public ResponseEntity<List<ProviderCredential>> getActiveCredentials() {
        log.info("REST request to get all active provider credentials");
        return ResponseEntity.ok(credentialService.getActiveCredentials());
    }

    /**
     * Validate credentials by testing the connection
     * @param id Credential ID
     * @return Result of validation
     */
    @PostMapping("/{id}/validate")
    public ResponseEntity<Boolean> validateCredential(@PathVariable Long id) {
        log.info("REST request to validate provider credential with id: {}", id);
        boolean valid = credentialService.validateCredential(id);
        return ResponseEntity.ok(valid);
    }

    /**
     * Refresh access tokens for OAuth-based providers
     * @param id Credential ID
     * @return Updated credentials with refreshed tokens
     */
    @PostMapping("/{id}/refresh-tokens")
    public ResponseEntity<ProviderCredential> refreshTokens(@PathVariable Long id) {
        log.info("REST request to refresh tokens for provider credential with id: {}", id);
        ProviderCredential result = credentialService.refreshTokens(id);
        return ResponseEntity.ok(result);
    }
    
    /**
     * Set credential active or inactive
     * @param id Credential ID
     * @param active true to activate, false to deactivate
     * @return Updated credentials
     */
    @PutMapping("/{id}/active")
    public ResponseEntity<ProviderCredential> setCredentialActive(
            @PathVariable Long id,
            @RequestParam boolean active) {
        log.info("REST request to set provider credential with id {} active status to: {}", id, active);
        ProviderCredential result = credentialService.setCredentialActive(id, active);
        return ResponseEntity.ok(result);
    }
}
