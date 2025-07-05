package com.gogidix.courier.location.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.slf4j.Slf4j;

/**
 * High availability database configuration.
 * Implements read/write splitting using a routing datasource.
 * This directs write operations to the primary database and
 * read operations to the replica for load balancing.
 */
@Configuration
@Profile("highavailability")
@Slf4j
public class HighAvailabilityDatabaseConfig {

    /**
     * Create the primary (writer) datasource.
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.primary.hikari")
    public HikariDataSource primaryDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    /**
     * Create the replica (reader) datasource.
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.replica.hikari")
    public HikariDataSource replicaDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    /**
     * Create the routing datasource that switches between primary and replica.
     */
    @Bean
    public DataSource routingDataSource(
            @Qualifier("primaryDataSource") DataSource primaryDataSource,
            @Qualifier("replicaDataSource") DataSource replicaDataSource) {
        
        ReadWriteRoutingDataSource routingDataSource = new ReadWriteRoutingDataSource();
        
        // Set the default datasource to primary
        routingDataSource.setDefaultTargetDataSource(primaryDataSource);
        
        // Configure datasource routing
        routingDataSource.setTargetDataSources(
                java.util.Map.of(
                    DataSourceType.PRIMARY, primaryDataSource,
                    DataSourceType.REPLICA, replicaDataSource
                )
        );
        
        return routingDataSource;
    }

    /**
     * Create a lazy connection data source proxy to delay datasource lookup
     * until a connection is actually needed.
     */
    @Primary
    @Bean
    public DataSource dataSource(@Qualifier("routingDataSource") DataSource routingDataSource) {
        return new LazyConnectionDataSourceProxy(routingDataSource);
    }

    /**
     * Enum for datasource types.
     */
    public enum DataSourceType {
        PRIMARY, REPLICA
    }

    /**
     * Custom routing datasource that selects between primary and replica
     * based on the current transaction and operation type.
     */
    public static class ReadWriteRoutingDataSource extends org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource {
        
        @Override
        protected Object determineCurrentLookupKey() {
            // If we're in a transaction, use the primary datasource
            if (TransactionSynchronizationManager.isActualTransactionActive()) {
                // Check if it's a read-only transaction
                if (TransactionSynchronizationManager.isCurrentTransactionReadOnly()) {
                    log.debug("Routing database request to REPLICA");
                    return DataSourceType.REPLICA;
                }
                
                log.debug("Routing database request to PRIMARY");
                return DataSourceType.PRIMARY;
            }
            
            // For non-transactional operations, use the replica
            log.debug("Routing non-transactional database request to REPLICA");
            return DataSourceType.REPLICA;
        }
    }
}
