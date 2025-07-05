package com.gogidix.integration.common;

import com.gogidix.integration.common.service.ShippingProviderAdapter;
import com.gogidix.integration.common.service.ShippingProviderAdapterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.List;

/**
 * Main application class for the Third-Party Integration Service.
 * Handles bootstrapping and configuration of the service.
 */
@SpringBootApplication
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.gogidix.integration")
@EntityScan(basePackages = "com.gogidix.integration")
@ComponentScan(basePackages = "com.gogidix.integration")
@Slf4j
public class ThirdPartyIntegrationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ThirdPartyIntegrationServiceApplication.class, args);
    }

    /**
     * Initialize the service after startup.
     * Logs information about configured shipping providers.
     */
    @Bean
    public CommandLineRunner initializeService(ShippingProviderAdapterRegistry adapterRegistry) {
        return args -> {
            log.info("Third-Party Integration Service initialized");
            
            List<ShippingProviderAdapter> adapters = adapterRegistry.getAllAdapters();
            log.info("Configured shipping providers: {}", adapters.size());
            
            adapters.forEach(adapter -> {
                log.info("Provider: {} - Supported features: {}", 
                         adapter.getProviderCode(), 
                         adapter.getSupportedFeatures().size());
            });
            
            adapterRegistry.getDefaultAdapter().ifPresentOrElse(
                adapter -> log.info("Default provider: {}", adapter.getProviderCode()),
                () -> log.warn("No default provider configured")
            );
        };
    }
}
