package com.microecosystem.courier.driver.app;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for the Driver Mobile App backend service.
 */
@SpringBootApplication
@EnableFeignClients
@EnableScheduling
@OpenAPIDefinition(
    info = @Info(
        title = "Driver Mobile App API",
        version = "1.0",
        description = "API documentation for the Driver Mobile App backend service",
        license = @License(name = "Micro-Social-Ecommerce-Ecosystems")
    )
)
public class DriverMobileAppApplication {

    /**
     * Main method to start the application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(DriverMobileAppApplication.class, args);
    }
} 