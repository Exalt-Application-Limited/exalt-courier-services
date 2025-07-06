package com.gogidix.courier.billing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for the Courier Billing Service.
 * 
 * This service manages all billing and invoicing operations for courier customers, including:
 * - Customer billing profile management
 * - Invoice generation and processing
 * - Payment tracking and reconciliation
 * - Credit management for corporate customers
 * - Billing cycle management (monthly, weekly, pay-per-use)
 * - Tax calculation and compliance
 * - Integration with payment processing service
 * - Volume discount calculations
 * - Late payment management and dunning processes
 * - Financial reporting and analytics
 * 
 * This service handles billing for both individual and corporate customers using courier services
 */
@SpringBootApplication
@EnableFeignClients
@EnableScheduling
@EnableKafka
public class CourierBillingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CourierBillingServiceApplication.class, args);
    }
}