package com.gogidix.courier.regional;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * Main application class for the Regional Admin System.
 * Responsible for regional courier service management and operations.
 */
@SpringBootApplication
@EnableFeignClients
@ComponentScan(basePackages = {"com.socialecommerceecosystem.regional", "com.socialecommerceecosystem.shared"})
public class RegionalAdminApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(RegionalAdminApplication.class, args);
    }
}
