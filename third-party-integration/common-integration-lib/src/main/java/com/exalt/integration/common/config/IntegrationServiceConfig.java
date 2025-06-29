package com.exalt.integration.common.config;

import com.exalt.integration.common.service.ShippingProviderAdapter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.List;

/**
 * Configuration class for the Third-Party Integration Service.
 * Sets up beans and configurations needed for the service.
 */
@Configuration
@EnableAsync
@EnableScheduling
@ConfigurationProperties(prefix = "integration")
@Getter
@Setter
public class IntegrationServiceConfig {

    /**
     * Default provider code to use when no specific provider is specified
     */
    private String defaultProvider;
    
    /**
     * Connection timeout in milliseconds
     */
    private int connectionTimeout = 30000;
    
    /**
     * Read timeout in milliseconds
     */
    private int readTimeout = 30000;
    
    /**
     * Number of retry attempts for failed API calls
     */
    private int retryAttempts = 3;
    
    /**
     * Delay between retry attempts in milliseconds
     */
    private int retryDelay = 2000;

    /**
     * Creates a RestTemplate with configured timeouts for API communication
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofMillis(connectionTimeout))
                .setReadTimeout(Duration.ofMillis(readTimeout))
                .build();
    }
}
