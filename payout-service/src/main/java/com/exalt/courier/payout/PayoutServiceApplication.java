package com.exalt.courierservices.payout.$1;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for the Payout Service.
 * This service manages courier payouts and earnings calculations.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableScheduling
@OpenAPIDefinition(
    info = @Info(
        title = "Payout Service API",
        version = "1.0",
        description = "API for managing courier payouts and earnings",
        license = @License(name = "Micro-Social-Ecommerce-Ecosystems")
    )
)
@Slf4j
public class PayoutServiceApplication {

    /**
     * Main method to start the application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(PayoutServiceApplication.class, args);
    }
}

