package com.gogidix.integration.common;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Main application class for the Common Integration Library.
 * This library provides common functionality for integrating with 3PL providers.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class CommonIntegrationLibApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommonIntegrationLibApplication.class, args);
    }
} 