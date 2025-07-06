package com.gogidix.courier.routing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Main application class for the Routing Service.
 * This service provides optimal route calculation, geolocation integration,
 * and delivery time estimation for courier services.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class RoutingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RoutingServiceApplication.class, args);
    }
} 