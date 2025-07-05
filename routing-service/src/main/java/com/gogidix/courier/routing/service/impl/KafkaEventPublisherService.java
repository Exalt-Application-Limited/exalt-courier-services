package com.gogidix.courier.routing.service.impl;

import com.gogidix.courier.routing.event.RouteStatusChangedEvent;
import com.gogidix.courier.routing.event.WaypointCompletedEvent;
import com.gogidix.courier.routing.model.Route;
import com.gogidix.courier.routing.model.RouteStatus;
import com.gogidix.courier.routing.model.Waypoint;
import com.gogidix.courier.routing.service.EventPublisherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Implementation of the EventPublisherService using Apache Kafka.
 */
@Service
public class KafkaEventPublisherService implements EventPublisherService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaEventPublisherService.class);
    
    private static final String ROUTE_STATUS_TOPIC = "routing.route-status-changed";
    private static final String WAYPOINT_COMPLETED_TOPIC = "routing.waypoint-completed";
    private static final String ROUTE_CREATED_TOPIC = "routing.route-created";
    private static final String ROUTE_ASSIGNED_TOPIC = "routing.route-assigned";
    private static final String ROUTE_STARTED_TOPIC = "routing.route-started";
    private static final String ROUTE_COMPLETED_TOPIC = "routing.route-completed";
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    @Autowired
    public KafkaEventPublisherService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    
    @Override
    public boolean publishRouteCreatedEvent(Route route) {
        try {
            kafkaTemplate.send(ROUTE_CREATED_TOPIC, route.getId().toString(), route);
            LOGGER.info("Published route created event for route {}", route.getId());
            return true;
        } catch (Exception e) {
            LOGGER.error("Failed to publish route created event", e);
            return false;
        }
    }
    
    @Override
    public boolean publishRouteAssignedEvent(Route route) {
        try {
            kafkaTemplate.send(ROUTE_ASSIGNED_TOPIC, route.getId().toString(), route);
            LOGGER.info("Published route assigned event for route {} to courier {}", 
                    route.getId(), route.getCourierId());
            return true;
        } catch (Exception e) {
            LOGGER.error("Failed to publish route assigned event", e);
            return false;
        }
    }
    
    @Override
    public boolean publishRouteStartedEvent(Route route) {
        try {
            kafkaTemplate.send(ROUTE_STARTED_TOPIC, route.getId().toString(), route);
            LOGGER.info("Published route started event for route {}", route.getId());
            return true;
        } catch (Exception e) {
            LOGGER.error("Failed to publish route started event", e);
            return false;
        }
    }
    
    @Override
    public boolean publishRouteCompletedEvent(Route route) {
        try {
            kafkaTemplate.send(ROUTE_COMPLETED_TOPIC, route.getId().toString(), route);
            LOGGER.info("Published route completed event for route {}", route.getId());
            return true;
        } catch (Exception e) {
            LOGGER.error("Failed to publish route completed event", e);
            return false;
        }
    }
    
    @Override
    public boolean publishRouteStatusChangedEvent(Route route, String previousStatus) {
        try {
            // Simplified event publishing using route directly
            kafkaTemplate.send(ROUTE_STATUS_TOPIC, route.getId().toString(), route);
            LOGGER.info("Published route status changed event for route {}: {} -> {}", 
                    route.getId(), previousStatus, route.getStatus());
            return true;
        } catch (Exception e) {
            LOGGER.error("Failed to publish route status changed event", e);
            return false;
        }
    }
    
    @Override
    public boolean publishWaypointCompletedEvent(Waypoint waypoint, Long routeId, String courierId, String notes) {
        try {
            // Simplified event publishing using waypoint directly
            kafkaTemplate.send(WAYPOINT_COMPLETED_TOPIC, waypoint.getId().toString(), waypoint);
            LOGGER.info("Published waypoint completed event for waypoint {} on route {}", 
                    waypoint.getId(), routeId);
            return true;
        } catch (Exception e) {
            LOGGER.error("Failed to publish waypoint completed event", e);
            return false;
        }
    }
    
    private String[] getPackageIdsFromRoute(Route route) {
        return route.getWaypoints().stream()
                .map(Waypoint::getPackageId)
                .filter(id -> id != null && !id.isEmpty())
                .distinct()
                .toArray(String[]::new);
    }
}
