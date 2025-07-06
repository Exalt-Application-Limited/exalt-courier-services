package com.gogidix.courierservices.customer.onboarding.controller;

import com.gogidix.courierservices.customer.onboarding.dto.CustomerRegistrationRequest;
import com.gogidix.courierservices.customer.onboarding.model.Customer;
import com.gogidix.courierservices.customer.onboarding.service.CustomerOnboardingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * REST controller for customer onboarding
 */
@RestController
@RequestMapping("/api/v1/customer-onboarding")
@RequiredArgsConstructor
public class CustomerOnboardingController {

    private final CustomerOnboardingService customerOnboardingService;

    @PostMapping("/register")
    public ResponseEntity<Customer> registerCustomer(@Valid @RequestBody CustomerRegistrationRequest request) {
        Customer customer = customerOnboardingService.registerCustomer(request);
        return new ResponseEntity<>(customer, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomer(@PathVariable Long id) {
        Customer customer = customerOnboardingService.getCustomer(id);
        return ResponseEntity.ok(customer);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Customer> getCustomerByEmail(@PathVariable String email) {
        Customer customer = customerOnboardingService.getCustomerByEmail(email);
        return ResponseEntity.ok(customer);
    }

    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        List<Customer> customers = customerOnboardingService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Customer> updateCustomerStatus(
            @PathVariable Long id, 
            @RequestParam Customer.OnboardingStatus status) {
        Customer customer = customerOnboardingService.updateCustomerStatus(id, status);
        return ResponseEntity.ok(customer);
    }
}