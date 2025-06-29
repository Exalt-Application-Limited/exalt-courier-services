package com.exalt.integration.common.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Entity for storing provider authentication credentials securely.
 * This entity is separate from Provider to enable different security measures and access controls.
 */
@Entity
@Table(name = "integration_provider_credentials")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderCredential {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Provider reference is required")
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "provider_id", nullable = false, unique = true)
    private Provider provider;
    
    @NotBlank(message = "Credential name is required")
    @Column(name = "credential_name", nullable = false)
    private String credentialName;

    // Fields for common authentication methods
    
    @Column(name = "api_key")
    private String apiKey;
    
    @Column(name = "api_secret")
    private String apiSecret;
    
    @Column(name = "username")
    private String username;
    
    @Column(name = "password")
    private String password;
    
    @Column(name = "access_token")
    private String accessToken;
    
    @Column(name = "refresh_token")
    private String refreshToken;
    
    @Column(name = "token_expiry")
    private LocalDateTime tokenExpiry;
    
    @Column(name = "account_number")
    private String accountNumber;
    
    @Column(name = "account_id")
    private String accountId;

    @Column(name = "client_id")
    private String clientId;
    
    @Column(name = "client_secret")
    private String clientSecret;
    
    // For providers with multiple credentials or special authentication requirements
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "provider_additional_credentials", joinColumns = @JoinColumn(name = "credential_id"))
    @MapKeyColumn(name = "credential_key")
    @Column(name = "credential_value")
    private Map<String, String> additionalCredentials = new HashMap<>();
    
    @Column(name = "is_active")
    private boolean active;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * Returns true if this credential set has expired tokens that need to be refreshed
     * before making API calls to the provider.
     */
    public boolean needsTokenRefresh() {
        if (tokenExpiry == null) {
            return false;
        }
        
        // Add a small buffer (5 minutes) to ensure we refresh before actual expiry
        return LocalDateTime.now().plusMinutes(5).isAfter(tokenExpiry);
    }
}
