package com.gogidix.courierservices.tracking.$1;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * RestTemplate configuration for the tracking service.
 */
@Configuration
public class RestTemplateConfig {

    /**
     * RestTemplate bean with timeout configuration.
     *
     * @param builder the RestTemplateBuilder
     * @return the RestTemplate
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(5))
                .build();
    }
} 