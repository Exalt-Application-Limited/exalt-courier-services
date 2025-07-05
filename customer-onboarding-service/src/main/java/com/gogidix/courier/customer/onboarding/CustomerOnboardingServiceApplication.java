package com.gogidix.courier.customer.onboarding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for the Customer Onboarding Service.
 * 
 * This service handles all aspects of the customer onboarding process for non-social commerce users, including:
 * - Individual customer registration and account creation
 * - KYC (Know Your Customer) verification and document processing
 * - Customer profile management and authentication
 * - Integration with shared infrastructure services (auth-service, kyc-service, document-verification)
 * - Customer communication and notification management
 * - Billing and payment setup coordination
 * 
 * This service leverages existing infrastructure to provide customer onboarding for www.exaltcourier.com
 */
@SpringBootApplication
@EnableFeignClients
@EnableScheduling
public class CustomerOnboardingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerOnboardingServiceApplication.class, args);
    }
}