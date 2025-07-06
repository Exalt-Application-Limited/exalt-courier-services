package com.gogidix.courierservices.customer.onboarding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Main application class for Customer Onboarding Service
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class CustomerOnboardingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerOnboardingServiceApplication.class, args);
    }
}