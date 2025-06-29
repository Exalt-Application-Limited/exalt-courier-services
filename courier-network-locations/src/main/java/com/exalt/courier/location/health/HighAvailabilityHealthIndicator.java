package com.exalt.courier.location.health;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Custom health indicator for the high availability configuration.
 * Checks the status of both primary and replica databases and provides
 * detailed health information for the monitoring system.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Profile("highavailability")
public class HighAvailabilityHealthIndicator implements HealthIndicator {

    private final JdbcTemplate jdbcTemplate;
    private final javax.sql.DataSource primaryDataSource;
    private final javax.sql.DataSource replicaDataSource;
    
    @Override
    public Health health() {
        Map<String, Object> details = new HashMap<>();
        boolean primaryUp = isDatabaseUp(primaryDataSource, "primary");
        boolean replicaUp = isDatabaseUp(replicaDataSource, "replica");
        
        details.put("primary", primaryUp ? "UP" : "DOWN");
        details.put("replica", replicaUp ? "UP" : "DOWN");
        
        // Check database replication lag
        if (primaryUp && replicaUp) {
            try {
                Long replicationLag = checkReplicationLag();
                details.put("replicationLagSeconds", replicationLag);
                
                if (replicationLag == null) {
                    log.warn("Could not determine replication lag");
                } else if (replicationLag > 300) { // 5 minutes lag
                    log.warn("High replication lag detected: {} seconds", replicationLag);
                    return Health.down()
                            .withDetail("status", "HIGH_REPLICATION_LAG")
                            .withDetails(details)
                            .build();
                }
            } catch (Exception e) {
                log.error("Error checking replication lag", e);
                details.put("replicationLagError", e.getMessage());
            }
        }
        
        // Overall health status
        if (primaryUp) {
            if (replicaUp) {
                return Health.up().withDetails(details).build();
            } else {
                // If replica is down but primary is up, we're in degraded mode
                return Health.status("DEGRADED")
                        .withDetail("status", "REPLICA_DOWN")
                        .withDetails(details)
                        .build();
            }
        } else {
            // Primary down is a critical failure
            return Health.down()
                    .withDetail("status", "PRIMARY_DOWN")
                    .withDetails(details)
                    .build();
        }
    }
    
    /**
     * Check if a database is up by executing a simple query.
     */
    private boolean isDatabaseUp(javax.sql.DataSource dataSource, String name) {
        try {
            JdbcTemplate template = new JdbcTemplate(dataSource);
            Integer result = template.queryForObject("SELECT 1", Integer.class);
            boolean up = result != null && result == 1;
            if (!up) {
                log.error("{} database health check failed", name);
            }
            return up;
        } catch (Exception e) {
            log.error("{} database health check error: {}", name, e.getMessage());
            return false;
        }
    }
    
    /**
     * Check the replication lag between primary and replica.
     */
    private Long checkReplicationLag() {
        try {
            // This is PostgreSQL-specific query
            String sql = "SELECT EXTRACT(EPOCH FROM (now() - pg_last_xact_replay_timestamp()))::bigint AS lag";
            return jdbcTemplate.queryForObject(sql, Long.class);
        } catch (Exception e) {
            log.warn("Could not determine replication lag: {}", e.getMessage());
            return null;
        }
    }
}
