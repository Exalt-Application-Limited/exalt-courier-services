package com.gogidix.courier.drivermobileapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Main application class for the Driver Mobile App Backend.
 * This service provides APIs for the courier's mobile application.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class DriverMobileAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(DriverMobileAppApplication.class, args);
    }
} 