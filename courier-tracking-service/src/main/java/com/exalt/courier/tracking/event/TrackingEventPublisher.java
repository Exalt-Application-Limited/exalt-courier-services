package com.exalt.courierservices.tracking.$1;

import com.exalt.courierservices.tracking.dto.PackageDTO;
import com.exalt.courierservices.tracking.dto.TrackingEventDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;

/**
 * Publisher for tracking events.
 */
@Component
public class TrackingEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(TrackingEventPublisher.class);
    
    private static final String PACKAGE_STATUS_TOPIC = "package-status-events";
    private static final String TRACKING_EVENTS_TOPIC = "tracking-events";
    private static final String DELIVERY_EVENTS_TOPIC = "delivery-events";
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    @Autowired
    public TrackingEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    
    /**
     * Publish a package status change event.
     *
     * @param packageDTO the package with updated status
     */
    public void publishPackageStatusChange(PackageDTO packageDTO) {
        String key = packageDTO.getTrackingNumber();
        publishEvent(PACKAGE_STATUS_TOPIC, key, packageDTO);
    }
    
    /**
     * Publish a tracking event.
     *
     * @param trackingNumber the tracking number
     * @param trackingEventDTO the tracking event
     */
    public void publishTrackingEvent(String trackingNumber, TrackingEventDTO trackingEventDTO) {
        publishEvent(TRACKING_EVENTS_TOPIC, trackingNumber, trackingEventDTO);
    }
    
    /**
     * Publish a delivery event.
     *
     * @param packageDTO the delivered package
     */
    public void publishDeliveryEvent(PackageDTO packageDTO) {
        String key = packageDTO.getTrackingNumber();
        publishEvent(DELIVERY_EVENTS_TOPIC, key, packageDTO);
    }
    
    private void publishEvent(String topic, String key, Object payload) {
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, key, payload);
        
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Event published to topic {} with key {}, partition {}, offset {}",
                        topic, key, result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
            } else {
                log.error("Failed to publish event to topic {} with key {}: {}", topic, key, ex.getMessage(), ex);
            }
        });
    }
} 