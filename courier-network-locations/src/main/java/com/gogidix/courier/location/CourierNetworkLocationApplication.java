package com.gogidix.courier.location;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * Main application class for the Courier Network Locations service.
 * This service represents the local/branch level of the courier service hierarchy
 * and is responsible for managing physical courier locations, staff, and walk-in customers.
 */
@SpringBootApplication
@EnableFeignClients
@ComponentScan(basePackages = {"com.socialecommerceecosystem.location", "com.socialecommerceecosystem.shared"})
public class CourierNetworkLocationApplication {

    /**
     * Main method to start the application.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(CourierNetworkLocationApplication.class, args);
    }
}
