package com.microecosystem.courier.driver.app.controller;

import com.microecosystem.courier.driver.app.dto.notification.NotificationRequest;
import com.microecosystem.courier.driver.app.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.concurrent.CompletableFuture;

/**
 * REST controller for notification operations.
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Notifications", description = "APIs for sending push notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    @Operation(summary = "Send notification", description = "Sends a push notification to drivers")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public CompletableFuture<ResponseEntity<String>> sendNotification(@Valid @RequestBody NotificationRequest request) {
        log.info("REST request to send notification: {}", request);
        return notificationService.sendNotification(request)
                .thenApply(messageId -> {
                    if (messageId == null) {
                        return ResponseEntity.badRequest().body("Failed to send notification - no valid recipients");
                    }
                    return ResponseEntity.ok("Notification sent successfully: " + messageId);
                })
                .exceptionally(ex -> {
                    log.error("Error sending notification", ex);
                    return ResponseEntity.badRequest().body("Failed to send notification: " + ex.getMessage());
                });
    }
} 