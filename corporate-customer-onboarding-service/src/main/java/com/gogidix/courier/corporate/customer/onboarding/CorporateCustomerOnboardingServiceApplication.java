package com.gogidix.courier.corporate.customer.onboarding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for the Corporate Customer Onboarding Service.
 * 
 * This service handles all aspects of the corporate customer onboarding process for businesses using courier services, including:
 * - Corporate customer registration and account creation
 * - KYB (Know Your Business) verification and document processing
 * - Corporate profile management and authentication
 * - Volume discount setup and pricing negotiations
 * - SLA management and corporate billing arrangements
 * - Integration with shared infrastructure services (auth-service, kyc-service, document-verification)
 * - Corporate communication and notification management
 * 
 * This service leverages existing infrastructure to provide corporate customer onboarding for www.exaltcourier.com
 */
@SpringBootApplication
@EnableFeignClients
@EnableScheduling
public class CorporateCustomerOnboardingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CorporateCustomerOnboardingServiceApplication.class, args);
    }
}