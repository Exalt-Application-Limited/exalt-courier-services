package com.microecosystem.courier.driver.app.service;

import com.microecosystem.courier.driver.app.dto.notification.NotificationRequest;
import com.microecosystem.courier.driver.app.model.Driver;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Service interface for sending push notifications to drivers.
 */
public interface NotificationService {

    /**
     * Send a notification to a specific driver.
     *
     * @param driver the driver to notify
     * @param title notification title
     * @param body notification body
     * @param data additional data payload
     * @return future with the message ID
     */
    CompletableFuture<String> sendNotification(Driver driver, String title, String body, Map<String, String> data);

    /**
     * Send a notification to multiple drivers.
     *
     * @param drivers list of drivers to notify
     * @param title notification title
     * @param body notification body
     * @param data additional data payload
     * @return future with the message ID
     */
    CompletableFuture<String> sendNotificationToMultipleDrivers(List<Driver> drivers, String title, String body, Map<String, String> data);

    /**
     * Send a notification to drivers with a specific status.
     *
     * @param status driver status
     * @param title notification title
     * @param body notification body
     * @param data additional data payload
     * @return future with the message ID
     */
    CompletableFuture<String> sendNotificationToDriversByStatus(String status, String title, String body, Map<String, String> data);

    /**
     * Send a notification based on a notification request.
     *
     * @param request notification request
     * @return future with the message ID
     */
    CompletableFuture<String> sendNotification(NotificationRequest request);
} 