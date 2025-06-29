package com.exalt.courier.location.controller;

import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.socialecommerceecosystem.location.model.WalkInCustomer;
import com.socialecommerceecosystem.location.service.NotificationService;
import com.socialecommerceecosystem.location.service.WalkInCustomerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for customer operations in the courier network.
 * Provides endpoints for managing walk-in customers and their preferences.
 */
@RestController
@RequestMapping("/api/customers")
@Tag(name = "Customer Management", description = "API for managing walk-in customers in the courier network")
@Slf4j
public class CustomerController {

    private final WalkInCustomerService customerService;
    private final NotificationService notificationService;

    @Autowired
    public CustomerController(
            WalkInCustomerService customerService,
            NotificationService notificationService) {
        this.customerService = customerService;
        this.notificationService = notificationService;
    }

    @GetMapping
    @Operation(summary = "Get all customers", description = "Retrieves all walk-in customers")
    @ApiResponse(responseCode = "200", description = "Customers retrieved successfully", 
            content = @Content(schema = @Schema(implementation = WalkInCustomer.class)))
    public ResponseEntity<List<WalkInCustomer>> getAllCustomers() {
        log.debug("REST request to get all customers");
        List<WalkInCustomer> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID", description = "Retrieves a specific customer by ID")
    @ApiResponse(responseCode = "200", description = "Customer found", 
            content = @Content(schema = @Schema(implementation = WalkInCustomer.class)))
    @ApiResponse(responseCode = "404", description = "Customer not found")
    public ResponseEntity<WalkInCustomer> getCustomerById(
            @Parameter(description = "ID of the customer", required = true) @PathVariable Long id) {
        log.debug("REST request to get customer with ID: {}", id);
        return customerService.getCustomerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a new customer", description = "Creates a new walk-in customer")
    @ApiResponse(responseCode = "201", description = "Customer created successfully", 
            content = @Content(schema = @Schema(implementation = WalkInCustomer.class)))
    public ResponseEntity<WalkInCustomer> createCustomer(
            @Parameter(description = "Customer details", required = true) 
            @Valid @RequestBody WalkInCustomer customer) {
        log.debug("REST request to create a new customer: {}", customer.getName());
        WalkInCustomer createdCustomer = customerService.createCustomer(customer);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCustomer);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a customer", description = "Updates an existing customer")
    @ApiResponse(responseCode = "200", description = "Customer updated successfully", 
            content = @Content(schema = @Schema(implementation = WalkInCustomer.class)))
    @ApiResponse(responseCode = "404", description = "Customer not found")
    public ResponseEntity<WalkInCustomer> updateCustomer(
            @Parameter(description = "ID of the customer", required = true) @PathVariable Long id,
            @Parameter(description = "Updated customer details", required = true) 
            @Valid @RequestBody WalkInCustomer customer) {
        log.debug("REST request to update customer with ID: {}", id);
        try {
            WalkInCustomer updatedCustomer = customerService.updateCustomer(id, customer);
            return ResponseEntity.ok(updatedCustomer);
        } catch (IllegalArgumentException e) {
            log.error("Customer not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a customer", description = "Deletes a customer")
    @ApiResponse(responseCode = "204", description = "Customer deleted successfully")
    @ApiResponse(responseCode = "404", description = "Customer not found")
    public ResponseEntity<Void> deleteCustomer(
            @Parameter(description = "ID of the customer", required = true) @PathVariable Long id) {
        log.debug("REST request to delete customer with ID: {}", id);
        try {
            customerService.deleteCustomer(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("Customer not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get customer by email", description = "Retrieves a customer by email address")
    @ApiResponse(responseCode = "200", description = "Customer found", 
            content = @Content(schema = @Schema(implementation = WalkInCustomer.class)))
    @ApiResponse(responseCode = "404", description = "Customer not found")
    public ResponseEntity<WalkInCustomer> getCustomerByEmail(
            @Parameter(description = "Email address", required = true) @PathVariable String email) {
        log.debug("REST request to get customer with email: {}", email);
        return customerService.getCustomerByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/phone/{phone}")
    @Operation(summary = "Get customer by phone", description = "Retrieves a customer by phone number")
    @ApiResponse(responseCode = "200", description = "Customer found", 
            content = @Content(schema = @Schema(implementation = WalkInCustomer.class)))
    @ApiResponse(responseCode = "404", description = "Customer not found")
    public ResponseEntity<WalkInCustomer> getCustomerByPhone(
            @Parameter(description = "Phone number", required = true) @PathVariable String phone) {
        log.debug("REST request to get customer with phone: {}", phone);
        return customerService.getCustomerByPhone(phone)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    @Operation(summary = "Search customers", description = "Searches for customers by name")
    public ResponseEntity<List<WalkInCustomer>> searchCustomersByName(
            @Parameter(description = "Name to search for") @RequestParam String name) {
        log.debug("REST request to search for customers with name: {}", name);
        List<WalkInCustomer> customers = customerService.searchCustomersByName(name);
        return ResponseEntity.ok(customers);
    }

    @PutMapping("/{id}/preferences")
    @Operation(summary = "Update customer preferences", 
            description = "Updates notification preferences for a customer")
    @ApiResponse(responseCode = "200", description = "Preferences updated successfully", 
            content = @Content(schema = @Schema(implementation = WalkInCustomer.class)))
    @ApiResponse(responseCode = "404", description = "Customer not found")
    public ResponseEntity<WalkInCustomer> updateCustomerPreferences(
            @Parameter(description = "ID of the customer", required = true) @PathVariable Long id,
            @Parameter(description = "Opt-in for email notifications") @RequestParam(required = false) Boolean optInEmail,
            @Parameter(description = "Opt-in for SMS notifications") @RequestParam(required = false) Boolean optInSms,
            @Parameter(description = "Opt-in for marketing messages") @RequestParam(required = false) Boolean optInMarketing) {
        log.debug("REST request to update preferences for customer with ID: {}", id);
        try {
            WalkInCustomer customer = customerService.getCustomerById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + id));
            
            if (optInEmail != null) {
                customer.setOptInEmail(optInEmail);
            }
            if (optInSms != null) {
                customer.setOptInSms(optInSms);
            }
            if (optInMarketing != null) {
                customer.setOptInMarketing(optInMarketing);
            }
            
            WalkInCustomer updatedCustomer = customerService.updateCustomer(id, customer);
            return ResponseEntity.ok(updatedCustomer);
        } catch (IllegalArgumentException e) {
            log.error("Customer not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/send-notification")
    @Operation(summary = "Send custom notification", 
            description = "Sends a custom notification to a customer")
    @ApiResponse(responseCode = "200", description = "Notification sent successfully")
    @ApiResponse(responseCode = "404", description = "Customer not found")
    public ResponseEntity<Map<String, Object>> sendCustomNotification(
            @Parameter(description = "ID of the customer", required = true) @PathVariable Long id,
            @Parameter(description = "Notification subject", required = true) @RequestParam String subject,
            @Parameter(description = "Notification message", required = true) @RequestParam String message,
            @Parameter(description = "High priority flag") @RequestParam(required = false, defaultValue = "false") boolean highPriority) {
        log.debug("REST request to send notification to customer with ID: {}", id);
        try {
            WalkInCustomer customer = customerService.getCustomerById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + id));
            
            boolean sent = notificationService.sendCustomerNotification(customer, subject, message, highPriority);
            
            Map<String, Object> response = Map.of(
                    "success", sent,
                    "message", sent ? "Notification sent successfully" : "Failed to send notification"
            );
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Customer not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/send-marketing")
    @Operation(summary = "Send marketing message", 
            description = "Sends a marketing message to multiple customers")
    public ResponseEntity<Map<String, Object>> sendMarketingMessage(
            @Parameter(description = "List of customer IDs", required = true) @RequestBody List<Long> customerIds,
            @Parameter(description = "Message title", required = true) @RequestParam String title,
            @Parameter(description = "Message content", required = true) @RequestParam String content) {
        log.debug("REST request to send marketing message to {} customers", customerIds.size());
        
        List<WalkInCustomer> customers = customerService.getCustomersByIds(customerIds);
        int sentCount = notificationService.sendMarketingMessage(customers, title, content);
        
        Map<String, Object> response = Map.of(
                "totalCustomers", customerIds.size(),
                "sentCount", sentCount,
                "failedCount", customerIds.size() - sentCount
        );
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/top-customers")
    @Operation(summary = "Get top customers", 
            description = "Retrieves top customers by shipment count or total spent")
    public ResponseEntity<List<Map<String, Object>>> getTopCustomers(
            @Parameter(description = "Maximum number of customers to return") 
            @RequestParam(required = false, defaultValue = "10") int limit,
            @Parameter(description = "Sort by 'shipments' or 'spent'") 
            @RequestParam(required = false, defaultValue = "shipments") String sortBy) {
        log.debug("REST request to get top {} customers sorted by {}", limit, sortBy);
        
        List<Map<String, Object>> topCustomers;
        if ("spent".equalsIgnoreCase(sortBy)) {
            topCustomers = customerService.getTopCustomersByAmountSpent(limit);
        } else {
            topCustomers = customerService.getTopCustomersByShipmentCount(limit);
        }
        
        return ResponseEntity.ok(topCustomers);
    }

    @GetMapping("/{id}/notification-status")
    @Operation(summary = "Get notification status", 
            description = "Checks if a customer can receive notifications")
    public ResponseEntity<Map<String, Boolean>> getCustomerNotificationStatus(
            @Parameter(description = "ID of the customer", required = true) @PathVariable Long id) {
        log.debug("REST request to get notification status for customer with ID: {}", id);
        try {
            WalkInCustomer customer = customerService.getCustomerById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + id));
            
            Map<String, Boolean> status = Map.of(
                    "canReceiveEmail", notificationService.canReceiveEmailNotifications(customer),
                    "canReceiveSms", notificationService.canReceiveSmsNotifications(customer)
            );
            
            return ResponseEntity.ok(status);
        } catch (IllegalArgumentException e) {
            log.error("Customer not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }
}
