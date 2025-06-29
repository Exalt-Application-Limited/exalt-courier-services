package com.exalt.courier.tracking.webhook;

import com.exalt.courier.tracking.dto.PackageDTO;
import com.exalt.courier.tracking.dto.TrackingEventDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Service for sending webhook notifications to external systems.
 */
@Service
public class WebhookNotifier {

    private static final Logger log = LoggerFactory.getLogger(WebhookNotifier.class);
    
    private final RestTemplate restTemplate;
    private final WebhookRepository webhookRepository;
    
    @Autowired
    public WebhookNotifier(RestTemplate restTemplate, WebhookRepository webhookRepository) {
        this.restTemplate = restTemplate;
        this.webhookRepository = webhookRepository;
    }
    
    /**
     * Notify all registered webhooks about a package status change.
     *
     * @param packageDTO the package with updated status
     */
    public void notifyPackageStatusChange(PackageDTO packageDTO) {
        webhookRepository.findByEventTypeAndActive("PACKAGE_STATUS_CHANGE", true)
                .forEach(webhook -> sendWebhookAsync(webhook.getUrl(), createStatusChangePayload(packageDTO)));
    }
    
    /**
     * Notify all registered webhooks about a new tracking event.
     *
     * @param trackingNumber the tracking number
     * @param trackingEventDTO the tracking event
     */
    public void notifyTrackingEvent(String trackingNumber, TrackingEventDTO trackingEventDTO) {
        webhookRepository.findByEventTypeAndActive("TRACKING_EVENT", true)
                .forEach(webhook -> sendWebhookAsync(webhook.getUrl(), createTrackingEventPayload(trackingNumber, trackingEventDTO)));
    }
    
    /**
     * Notify all registered webhooks about a package delivery.
     *
     * @param packageDTO the delivered package
     */
    public void notifyDelivery(PackageDTO packageDTO) {
        webhookRepository.findByEventTypeAndActive("DELIVERY", true)
                .forEach(webhook -> sendWebhookAsync(webhook.getUrl(), createDeliveryPayload(packageDTO)));
    }
    
    private Map<String, Object> createStatusChangePayload(PackageDTO packageDTO) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("eventType", "PACKAGE_STATUS_CHANGE");
        payload.put("trackingNumber", packageDTO.getTrackingNumber());
        payload.put("status", packageDTO.getStatus().name());
        payload.put("timestamp", System.currentTimeMillis());
        payload.put("package", packageDTO);
        return payload;
    }
    
    private Map<String, Object> createTrackingEventPayload(String trackingNumber, TrackingEventDTO eventDTO) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("eventType", "TRACKING_EVENT");
        payload.put("trackingNumber", trackingNumber);
        payload.put("status", eventDTO.getStatus().name());
        payload.put("timestamp", System.currentTimeMillis());
        payload.put("event", eventDTO);
        return payload;
    }
    
    private Map<String, Object> createDeliveryPayload(PackageDTO packageDTO) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("eventType", "DELIVERY");
        payload.put("trackingNumber", packageDTO.getTrackingNumber());
        payload.put("deliveryTime", packageDTO.getActualDeliveryDate().toString());
        payload.put("timestamp", System.currentTimeMillis());
        payload.put("package", packageDTO);
        return payload;
    }
    
    private void sendWebhookAsync(String url, Map<String, Object> payload) {
        CompletableFuture.runAsync(() -> {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                
                HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
                ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
                
                if (response.getStatusCode().is2xxSuccessful()) {
                    log.info("Successfully sent webhook notification to {}", url);
                } else {
                    log.warn("Failed to send webhook notification to {}, status: {}", url, response.getStatusCode());
                }
            } catch (Exception e) {
                log.error("Error sending webhook notification to {}: {}", url, e.getMessage(), e);
            }
        });
    }
} 