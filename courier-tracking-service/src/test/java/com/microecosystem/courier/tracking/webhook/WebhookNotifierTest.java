package com.exalt.courierservices.tracking.$1;

import com.exalt.courierservices.tracking.dto.PackageDTO;
import com.exalt.courierservices.tracking.model.TrackingStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebhookNotifierTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private WebhookRepository webhookRepository;

    private WebhookNotifier webhookNotifier;

    @BeforeEach
    void setUp() {
        webhookNotifier = new WebhookNotifier(restTemplate, webhookRepository);
    }

    @Test
    void notifyPackageStatusChange_shouldSendWebhookNotifications() {
        // Given
        PackageDTO packageDTO = new PackageDTO();
        packageDTO.setTrackingNumber("TRK123456");
        packageDTO.setStatus(TrackingStatus.DELIVERED);
        packageDTO.setRecipientName("John Doe");
        packageDTO.setRecipientAddress("123 Main St, Anytown, USA");
        packageDTO.setActualDeliveryDate(LocalDateTime.now());

        Webhook webhook1 = new Webhook();
        webhook1.setId(1L);
        webhook1.setName("Test Webhook 1");
        webhook1.setUrl("http://example.com/webhook1");
        webhook1.setEventType("PACKAGE_STATUS_CHANGE");
        webhook1.setActive(true);

        Webhook webhook2 = new Webhook();
        webhook2.setId(2L);
        webhook2.setName("Test Webhook 2");
        webhook2.setUrl("http://example.com/webhook2");
        webhook2.setEventType("PACKAGE_STATUS_CHANGE");
        webhook2.setActive(true);

        List<Webhook> webhooks = Arrays.asList(webhook1, webhook2);

        when(webhookRepository.findByEventTypeAndActive("PACKAGE_STATUS_CHANGE", true))
                .thenReturn(webhooks);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>("OK", HttpStatus.OK));

        // When
        webhookNotifier.notifyPackageStatusChange(packageDTO);

        // Then
        ArgumentCaptor<HttpEntity<Map<String, Object>>> captor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate, times(2)).postForEntity(anyString(), captor.capture(), eq(String.class));

        List<HttpEntity<Map<String, Object>>> capturedEntities = captor.getAllValues();
        assertEquals(2, capturedEntities.size());

        Map<String, Object> payload1 = capturedEntities.get(0).getBody();
        assertNotNull(payload1);
        assertEquals("PACKAGE_STATUS_CHANGE", payload1.get("eventType"));
        assertEquals("TRK123456", payload1.get("trackingNumber"));
        assertEquals("DELIVERED", payload1.get("status"));
        assertNotNull(payload1.get("timestamp"));
        assertNotNull(payload1.get("package"));

        Map<String, Object> payload2 = capturedEntities.get(1).getBody();
        assertNotNull(payload2);
        assertEquals("PACKAGE_STATUS_CHANGE", payload2.get("eventType"));
        assertEquals("TRK123456", payload2.get("trackingNumber"));
        assertEquals("DELIVERED", payload2.get("status"));
        assertNotNull(payload2.get("timestamp"));
        assertNotNull(payload2.get("package"));

        verify(webhookRepository).findByEventTypeAndActive("PACKAGE_STATUS_CHANGE", true);
    }
} 
