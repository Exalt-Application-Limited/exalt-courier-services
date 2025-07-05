package com.gogidix.courier.location.monitoring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Implementation of NotificationSender that sends alerts via email and optionally to other channels.
 */
@Component
@Slf4j
public class EmailNotificationSender implements NotificationSender {

    private final JavaMailSender mailSender;
    
    @Value("${alerts.email.enabled:true}")
    private boolean emailEnabled;
    
    @Value("${alerts.email.from:alerts@microecommerce.com}")
    private String fromEmail;
    
    @Value("#{'${alerts.email.to:admin@microecommerce.com}'.split(',')}")
    private List<String> toEmails;
    
    @Value("${alerts.email.critical.to:oncall@microecommerce.com}")
    private String criticalToEmail;
    
    @Value("${alerts.slack.enabled:false}")
    private boolean slackEnabled;
    
    @Value("${alerts.slack.webhook:}")
    private String slackWebhook;
    
    public EmailNotificationSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    
    @Override
    public void sendAlertNotification(String subject, String body, boolean critical) {
        // Log all alerts
        if (critical) {
            log.error("CRITICAL ALERT: {}", subject);
        } else {
            log.warn("ALERT: {}", subject);
        }
        
        // Send email if enabled
        if (emailEnabled) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(fromEmail);
                
                // For critical alerts, add the on-call email
                if (critical) {
                    String[] allRecipients = new String[toEmails.size() + 1];
                    toEmails.toArray(allRecipients);
                    allRecipients[toEmails.size()] = criticalToEmail;
                    message.setTo(allRecipients);
                    message.setSubject("[CRITICAL] " + subject);
                } else {
                    message.setTo(toEmails.toArray(new String[0]));
                    message.setSubject("[ALERT] " + subject);
                }
                
                message.setText(body);
                mailSender.send(message);
                
                log.info("Alert email sent to {} recipients", 
                        critical ? toEmails.size() + 1 : toEmails.size());
            } catch (Exception e) {
                log.error("Failed to send alert email: {}", e.getMessage(), e);
            }
        }
        
        // Send to Slack if enabled
        if (slackEnabled && !slackWebhook.isEmpty()) {
            try {
                sendSlackNotification(subject, body, critical);
            } catch (Exception e) {
                log.error("Failed to send Slack notification: {}", e.getMessage(), e);
            }
        }
    }
    
    @Override
    public void sendStatusNotification(String subject, String body) {
        // Log status notifications
        log.info("STATUS NOTIFICATION: {}", subject);
        
        // Send email if enabled
        if (emailEnabled) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(fromEmail);
                message.setTo(toEmails.toArray(new String[0]));
                message.setSubject("[STATUS] " + subject);
                message.setText(body);
                mailSender.send(message);
            } catch (Exception e) {
                log.error("Failed to send status email: {}", e.getMessage(), e);
            }
        }
        
        // Send to Slack if enabled
        if (slackEnabled && !slackWebhook.isEmpty()) {
            try {
                sendSlackNotification(subject, body, false);
            } catch (Exception e) {
                log.error("Failed to send Slack status notification: {}", e.getMessage(), e);
            }
        }
    }
    
    /**
     * Send a notification to Slack.
     * 
     * @param subject the notification subject
     * @param body the notification body
     * @param critical whether the notification is critical
     */
    private void sendSlackNotification(String subject, String body, boolean critical) {
        // This would be implemented with a proper Slack API client
        // For now, just log that we would send to Slack
        String emoji = critical ? ":rotating_light:" : ":information_source:";
        log.info("Would send to Slack webhook: {} {} {}", emoji, subject, body);
    }
}
