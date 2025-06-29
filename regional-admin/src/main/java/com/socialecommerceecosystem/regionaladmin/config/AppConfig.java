package com.exalt.courier.regionaladmin.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class for general application beans.
 */
@Configuration
public class AppConfig {

    /**
     * ModelMapper bean for object mapping between DTOs and entities.
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    /**
     * RestTemplate bean for making HTTP requests to other services.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
