package com.gogidix.courier.regionaladmin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for the Regional Admin service.
 * This service acts as a middle-tier between HQ Admin and Branch/Local Courier offices.
 * It aggregates data from multiple branches and provides a regional view.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableScheduling
public class RegionalAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(RegionalAdminApplication.class, args);
    }
}
