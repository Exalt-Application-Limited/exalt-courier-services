package com.gogidix.courier.hqadmin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * Main application class for the Global HQ Admin Dashboard.
 * This dashboard provides global oversight of all courier operations,
 * management of regional administrators, and global policy/pricing control.
 */
@SpringBootApplication
@EnableFeignClients
@ComponentScan(basePackages = {"com.socialecommerceecosystem.hqadmin", "com.socialecommerceecosystem.shared"})
public class GlobalHQAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(GlobalHQAdminApplication.class, args);
    }
}
