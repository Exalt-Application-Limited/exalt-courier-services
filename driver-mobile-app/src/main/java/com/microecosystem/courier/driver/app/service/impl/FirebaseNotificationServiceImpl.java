package com.microecosystem.courier.driver.app.service.impl;

import com.google.firebase.messaging.*;
import com.microecosystem.courier.driver.app.dto.notification.NotificationRequest;
import com.microecosystem.courier.driver.app.model.Driver;
import com.microecosystem.courier.driver.app.model.DriverStatus;
import com.microecosystem.courier.driver.app.repository.DriverRepository;
import com.microecosystem.courier.driver.app.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Implementation of the NotificationService interface using Firebase Cloud Messaging.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FirebaseNotificationServiceImpl implements NotificationService {

    private final DriverRepository driverRepository;

    @Async
    @Override
    public CompletableFuture<String> sendNotification(Driver driver, String title, String body, Map<String, String> data) {
        if (driver == null || driver.getDeviceToken() == null || driver.getDeviceToken().isEmpty()) {
            log.warn("Cannot send notification to driver with ID: {} - no device token", 
                    driver != null ? driver.getId() : "null");
            return CompletableFuture.completedFuture(null);
        }

        try {
            Message message = buildMessage(driver.getDeviceToken(), title, body, data);
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("Successfully sent notification to driver ID: {}, message ID: {}", driver.getId(), response);
            return CompletableFuture.completedFuture(response);
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send notification to driver ID: {}", driver.getId(), e);
            CompletableFuture<String> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    @Async
    @Override
    public CompletableFuture<String> sendNotificationToMultipleDrivers(List<Driver> drivers, String title, String body, Map<String, String> data) {
        if (drivers == null || drivers.isEmpty()) {
            log.warn("Cannot send notification - driver list is empty");
            return CompletableFuture.completedFuture(null);
        }

        List<String> tokens = drivers.stream()
                .filter(driver -> driver.getDeviceToken() != null && !driver.getDeviceToken().isEmpty())
                .map(Driver::getDeviceToken)
                .collect(Collectors.toList());

        if (tokens.isEmpty()) {
            log.warn("No valid device tokens found for the provided drivers");
            return CompletableFuture.completedFuture(null);
        }

        try {
            MulticastMessage message = buildMulticastMessage(tokens, title, body, data);
            BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);
            log.info("Successfully sent multicast notification to {} drivers, success count: {}, failure count: {}", 
                    tokens.size(), response.getSuccessCount(), response.getFailureCount());
            return CompletableFuture.completedFuture(response.getSuccessCount() + "/" + tokens.size());
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send multicast notification", e);
            CompletableFuture<String> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    @Async
    @Override
    public CompletableFuture<String> sendNotificationToDriversByStatus(String status, String title, String body, Map<String, String> data) {
        try {
            DriverStatus driverStatus = DriverStatus.valueOf(status);
            
            // Process in batches to handle large numbers of drivers
            int page = 0;
            int size = 500;
            long totalSent = 0;
            long totalFailed = 0;
            
            while (true) {
                Pageable pageable = PageRequest.of(page, size);
                Page<Driver> driversPage = driverRepository.findByStatus(driverStatus, pageable);
                
                if (driversPage.isEmpty()) {
                    break;
                }
                
                List<Driver> drivers = driversPage.getContent();
                List<String> tokens = drivers.stream()
                        .filter(driver -> driver.getDeviceToken() != null && !driver.getDeviceToken().isEmpty())
                        .map(Driver::getDeviceToken)
                        .collect(Collectors.toList());
                
                if (!tokens.isEmpty()) {
                    MulticastMessage message = buildMulticastMessage(tokens, title, body, data);
                    BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);
                    totalSent += response.getSuccessCount();
                    totalFailed += response.getFailureCount();
                }
                
                if (!driversPage.hasNext()) {
                    break;
                }
                
                page++;
            }
            
            log.info("Completed sending notifications to drivers with status {}, success: {}, failures: {}", 
                    status, totalSent, totalFailed);
            return CompletableFuture.completedFuture(totalSent + "/" + (totalSent + totalFailed));
        } catch (IllegalArgumentException e) {
            log.error("Invalid driver status: {}", status, e);
            CompletableFuture<String> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send notifications to drivers with status: {}", status, e);
            CompletableFuture<String> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    @Async
    @Override
    public CompletableFuture<String> sendNotification(NotificationRequest request) {
        if (request.getDriverId() != null) {
            // Send to a single driver
            return driverRepository.findById(request.getDriverId())
                    .map(driver -> sendNotification(driver, request.getTitle(), request.getBody(), request.getData()))
                    .orElseGet(() -> {
                        log.warn("Driver not found with ID: {}", request.getDriverId());
                        return CompletableFuture.completedFuture(null);
                    });
        } else if (request.getDriverIds() != null && !request.getDriverIds().isEmpty()) {
            // Send to multiple drivers by IDs
            List<Driver> drivers = driverRepository.findAllById(request.getDriverIds());
            return sendNotificationToMultipleDrivers(drivers, request.getTitle(), request.getBody(), request.getData());
        } else if (request.getDriverStatus() != null) {
            // Send to drivers with specific status
            return sendNotificationToDriversByStatus(request.getDriverStatus(), request.getTitle(), request.getBody(), request.getData());
        } else if (request.getTopic() != null && !request.getTopic().isEmpty()) {
            // Send to a topic
            return sendTopicNotification(request.getTopic(), request.getTitle(), request.getBody(), request.getData(), 
                    request.isHighPriority(), request.getTimeToLive());
        } else {
            log.warn("No valid target specified in notification request");
            return CompletableFuture.completedFuture(null);
        }
    }

    /**
     * Send a notification to a topic.
     *
     * @param topic topic name
     * @param title notification title
     * @param body notification body
     * @param data additional data payload
     * @param highPriority whether to send as high priority
     * @param timeToLive time to live in seconds
     * @return future with the message ID
     */
    private CompletableFuture<String> sendTopicNotification(String topic, String title, String body, 
                                                          Map<String, String> data, boolean highPriority, Long timeToLive) {
        try {
            Message message = Message.builder()
                    .setTopic(topic)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .putAllData(data != null ? data : Collections.emptyMap())
                    .setAndroidConfig(AndroidConfig.builder()
                            .setPriority(highPriority ? AndroidConfig.Priority.HIGH : AndroidConfig.Priority.NORMAL)
                            .setTtl(timeToLive != null ? timeToLive * 1000 : 2419200000L) // Default 4 weeks
                            .build())
                    .setApnsConfig(ApnsConfig.builder()
                            .setAps(Aps.builder()
                                    .setSound("default")
                                    .build())
                            .build())
                    .build();
            
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("Successfully sent topic notification to {}, message ID: {}", topic, response);
            return CompletableFuture.completedFuture(response);
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send topic notification to {}", topic, e);
            CompletableFuture<String> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    /**
     * Build a Firebase message for a single recipient.
     *
     * @param token device token
     * @param title notification title
     * @param body notification body
     * @param data additional data payload
     * @return Firebase message
     */
    private Message buildMessage(String token, String title, String body, Map<String, String> data) {
        return Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .putAllData(data != null ? data : Collections.emptyMap())
                .setAndroidConfig(AndroidConfig.builder()
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .setNotification(AndroidNotification.builder()
                                .setSound("default")
                                .setClickAction("FLUTTER_NOTIFICATION_CLICK")
                                .build())
                        .build())
                .setApnsConfig(ApnsConfig.builder()
                        .setAps(Aps.builder()
                                .setSound("default")
                                .build())
                        .build())
                .build();
    }

    /**
     * Build a Firebase multicast message for multiple recipients.
     *
     * @param tokens list of device tokens
     * @param title notification title
     * @param body notification body
     * @param data additional data payload
     * @return Firebase multicast message
     */
    private MulticastMessage buildMulticastMessage(List<String> tokens, String title, String body, Map<String, String> data) {
        return MulticastMessage.builder()
                .addAllTokens(tokens)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .putAllData(data != null ? data : Collections.emptyMap())
                .setAndroidConfig(AndroidConfig.builder()
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .setNotification(AndroidNotification.builder()
                                .setSound("default")
                                .setClickAction("FLUTTER_NOTIFICATION_CLICK")
                                .build())
                        .build())
                .setApnsConfig(ApnsConfig.builder()
                        .setAps(Aps.builder()
                                .setSound("default")
                                .build())
                        .build())
                .build();
    }
} 