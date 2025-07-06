package com.gogidix.courierservices.tracking.$1;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for webhook subscriptions.
 */
@Repository
public interface WebhookRepository extends JpaRepository<Webhook, Long> {

    /**
     * Find webhooks by event type and active status.
     *
     * @param eventType the event type
     * @param active the active status
     * @return list of matching webhooks
     */
    List<Webhook> findByEventTypeAndActive(String eventType, boolean active);
    
    /**
     * Find webhooks by event type.
     *
     * @param eventType the event type
     * @return list of matching webhooks
     */
    List<Webhook> findByEventType(String eventType);
    
    /**
     * Find webhooks by name containing the given text.
     *
     * @param name the name to search for
     * @return list of matching webhooks
     */
    List<Webhook> findByNameContainingIgnoreCase(String name);
} 