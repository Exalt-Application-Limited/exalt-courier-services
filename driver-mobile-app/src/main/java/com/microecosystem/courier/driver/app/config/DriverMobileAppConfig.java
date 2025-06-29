package com.microecosystem.courier.driver.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class for Driver Mobile App.
 * Provides beans and configuration settings for the application.
 */
@Configuration
public class DriverMobileAppConfig {

    /**
     * Creates a RestTemplate bean for making HTTP requests.
     * 
     * @return RestTemplate instance
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
