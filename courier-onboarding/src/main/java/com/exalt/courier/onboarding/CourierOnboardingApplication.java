package com.exalt.courier.onboarding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for the Courier Onboarding Service.
 * 
 * This service handles all aspects of the courier onboarding process, including:
 * - Application submission and review
 * - Document verification
 * - Background checks
 * - Courier profile management
 * - Rating and performance tracking
 */
@SpringBootApplication
@EnableFeignClients
@EnableScheduling
public class CourierOnboardingApplication {

    public static void main(String[] args) {
        SpringApplication.run(CourierOnboardingApplication.class, args);
    }
}
