package com.gogidix.courier.international;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * Main application class for the International Shipping Service.
 * Handles international shipping requirements, customs documentation,
 * and cross-border logistics management.
 */
@SpringBootApplication
@EnableFeignClients
public class InternationalShippingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InternationalShippingServiceApplication.class, args);
    }
    
    /**
     * Create a RestTemplate bean for use in service implementations
     * to communicate with other services such as the Third-Party Integration Service.
     * @return A new RestTemplate instance
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
