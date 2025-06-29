package com.exalt.courierservices.tracking.$1;

import com.exalt.courierservices.tracking.webhook.Webhook;
import com.exalt.courierservices.tracking.webhook.WebhookRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * Controller for managing webhook subscriptions.
 */
@RestController
@RequestMapping("/api/v1/tracking/webhooks")
@Tag(name = "webhooks", description = "Webhook subscription operations")
@Slf4j
public class WebhookController {

    private final WebhookRepository webhookRepository;

    @Autowired
    public WebhookController(WebhookRepository webhookRepository) {
        this.webhookRepository = webhookRepository;
    }

    @GetMapping
    @Operation(summary = "Get all webhooks", description = "Get all webhook subscriptions")
    public List<Webhook> getAllWebhooks() {
        return webhookRepository.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get webhook by ID", description = "Get a webhook subscription by its ID")
    public ResponseEntity<Webhook> getWebhookById(@PathVariable Long id) {
        return webhookRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Webhook not found with ID: " + id));
    }

    @PostMapping
    @Operation(summary = "Create webhook", description = "Create a new webhook subscription")
    @ResponseStatus(HttpStatus.CREATED)
    public Webhook createWebhook(@Valid @RequestBody Webhook webhook) {
        return webhookRepository.save(webhook);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update webhook", description = "Update an existing webhook subscription")
    public Webhook updateWebhook(@PathVariable Long id, @Valid @RequestBody Webhook webhook) {
        if (!webhookRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Webhook not found with ID: " + id);
        }
        webhook.setId(id);
        return webhookRepository.save(webhook);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete webhook", description = "Delete a webhook subscription")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWebhook(@PathVariable Long id) {
        if (!webhookRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Webhook not found with ID: " + id);
        }
        webhookRepository.deleteById(id);
    }
} 
