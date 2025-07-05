package com.gogidix.courier.courier.config;

import feign.Logger;
import feign.Request;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Configuration for Feign clients
 */
@Slf4j
@Configuration
@EnableFeignClients(basePackages = "com.microsocial.courier.integration")
public class FeignConfiguration {

    @Value("${branch-courier-app.courier-management.integration.connect-timeout-seconds:5}")
    private int connectTimeout;

    @Value("${branch-courier-app.courier-management.integration.read-timeout-seconds:30}")
    private int readTimeout;

    @Value("${branch-courier-app.courier-management.integration.retry-attempts:3}")
    private int maxRetryAttempts;

    /**
     * Configures Feign logging level
     */
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    /**
     * Configures request timeouts
     */
    @Bean
    public Request.Options requestOptions() {
        return new Request.Options(
                connectTimeout, TimeUnit.SECONDS,
                readTimeout, TimeUnit.SECONDS,
                true);
    }

    /**
     * Configures retry policy
     */
    @Bean
    public Retryer retryer() {
        return new Retryer.Default(100, TimeUnit.SECONDS.toMillis(1), maxRetryAttempts);
    }

    /**
     * Custom error decoder for handling Feign errors
     */
    @Bean
    public ErrorDecoder errorDecoder() {
        return new FeignErrorDecoder();
    }

    /**
     * Custom error decoder implementation
     */
    public class FeignErrorDecoder implements ErrorDecoder {
        private final ErrorDecoder defaultErrorDecoder = new Default();

        @Override
        public Exception decode(String methodKey, feign.Response response) {
            log.error("Error in Feign client call: {} - Status: {}", methodKey, response.status());
            
            if (response.status() >= 500) {
                log.error("Server error in Feign client call. Service may be unavailable.");
            }
            
            return defaultErrorDecoder.decode(methodKey, response);
        }
    }
}