package com.gogidix.courierservices.customer.onboarding.service;

import com.gogidix.courierservices.customer.onboarding.dto.CustomerRegistrationRequest;
import com.gogidix.courierservices.customer.onboarding.model.Customer;

import java.util.List;

/**
 * Service interface for customer onboarding
 */
public interface CustomerOnboardingService {
    
    /**
     * Register a new customer
     */
    Customer registerCustomer(CustomerRegistrationRequest request);
    
    /**
     * Get customer by ID
     */
    Customer getCustomer(Long id);
    
    /**
     * Get customer by email
     */
    Customer getCustomerByEmail(String email);
    
    /**
     * Get all customers
     */
    List<Customer> getAllCustomers();
    
    /**
     * Update customer status
     */
    Customer updateCustomerStatus(Long id, Customer.OnboardingStatus status);
}