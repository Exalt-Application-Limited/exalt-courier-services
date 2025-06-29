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
 * Entity representing a third-party shipping provider (e.g., DHL, UPS, FedEx) with their integration details.
 * This entity stores general provider information and configuration.
 */
@Entity
@Table(name = "integration_providers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Provider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Provider code is required")
    @Column(name = "provider_code", nullable = false, unique = true)
    private String providerCode;

    @NotBlank(message = "Provider name is required")
    @Column(name = "provider_name", nullable = false)
    private String providerName;

    @NotNull(message = "Provider type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "provider_type", nullable = false)
    private ProviderType providerType;

    @Column(name = "description")
    private String description;

    @Column(name = "api_base_url")
    private String apiBaseUrl;

    @Column(name = "webhook_url")
    private String webhookUrl;

    @Column(name = "webhook_secret")
    private String webhookSecret;

    @Column(name = "api_version")
    private String apiVersion;

    @Column(name = "test_mode")
    private boolean testMode;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "default_provider")
    private boolean defaultProvider;

    @Column(name = "supports_tracking")
    private boolean supportsTracking;

    @Column(name = "supports_label_generation")
    private boolean supportsLabelGeneration;

    @Column(name = "supports_rate_calculation")
    private boolean supportsRateCalculation;

    @Column(name = "supports_international")
    private boolean supportsInternational;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "provider_supported_countries", joinColumns = @JoinColumn(name = "provider_id"))
    @Column(name = "country_code")
    private Map<String, String> supportedCountries = new HashMap<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "provider_service_mappings", joinColumns = @JoinColumn(name = "provider_id"))
    @MapKeyColumn(name = "service_type")
    @Column(name = "provider_service_code")
    private Map<String, String> serviceMappings = new HashMap<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "provider_config_params", joinColumns = @JoinColumn(name = "provider_id"))
    @MapKeyColumn(name = "param_name")
    @Column(name = "param_value")
    private Map<String, String> configParams = new HashMap<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Enum representing the types of shipping providers supported by the system.
     */
    public enum ProviderType {
        EXPRESS_COURIER,     // International express couriers (DHL, FedEx, UPS)
        POSTAL_SERVICE,      // National postal services
        REGIONAL_CARRIER,    // Regional shipping carriers
        ON_DEMAND_DELIVERY,  // On-demand delivery services
        FREIGHT_CARRIER,     // Freight carriers for larger shipments
        LAST_MILE_DELIVERY   // Last mile delivery services
    }
}
