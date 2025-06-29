package com.exalt.courier.routing.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import jakarta.sql.DataSource;

/**
 * Configuration class for PostGIS setup.
 * Ensures that the PostGIS extension is enabled in the PostgreSQL database
 * for geospatial operations.
 */
@Configuration
@Slf4j
public class PostGisConfig {

    /**
     * Configure the primary data source properties.
     */
    @Primary
    @Bean
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * Configure the primary data source.
     */
    @Primary
    @Bean
    public DataSource dataSource(DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }

    /**
     * Initialize PostGIS extension in the database if not already enabled.
     */
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        
        log.info("Checking PostGIS extension...");
        
        try {
            // Check if PostGIS extension is already enabled
            Boolean postGisEnabled = jdbcTemplate.queryForObject(
                    "SELECT EXISTS (SELECT 1 FROM pg_extension WHERE extname = 'postgis')",
                    Boolean.class
            );
            
            if (Boolean.FALSE.equals(postGisEnabled)) {
                log.info("Enabling PostGIS extension...");
                
                // Enable PostGIS extension
                jdbcTemplate.execute("CREATE EXTENSION IF NOT EXISTS postgis");
                
                log.info("PostGIS extension enabled successfully");
            } else {
                log.info("PostGIS extension is already enabled");
            }
            
            // Verify PostGIS version
            String postGisVersion = jdbcTemplate.queryForObject(
                    "SELECT postgis_version()",
                    String.class
            );
            
            log.info("PostGIS version: {}", postGisVersion);
            
        } catch (Exception e) {
            log.error("Error initializing PostGIS extension: {}", e.getMessage(), e);
            // We don't want to fail application startup if this initialization fails
            // The application can still work without some geospatial features
        }
        
        return jdbcTemplate;
    }
}
