package com.gogidix.courierservices.customer.onboarding.service.impl;

import com.gogidix.courierservices.customer.onboarding.dto.CustomerRegistrationRequest;
import com.gogidix.courierservices.customer.onboarding.model.Customer;
import com.gogidix.courierservices.customer.onboarding.repository.CustomerRepository;
import com.gogidix.courierservices.customer.onboarding.service.CustomerOnboardingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of CustomerOnboardingService
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CustomerOnboardingServiceImpl implements CustomerOnboardingService {

    private final CustomerRepository customerRepository;

    @Override
    public Customer registerCustomer(CustomerRegistrationRequest request) {
        log.info("Registering new customer with email: {}", request.getEmail());
        
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Customer with email already exists");
        }
        
        Customer customer = Customer.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .status(Customer.OnboardingStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        Customer savedCustomer = customerRepository.save(customer);
        log.info("Successfully registered customer with ID: {}", savedCustomer.getId());
        
        return savedCustomer;
    }

    @Override
    @Transactional(readOnly = true)
    public Customer getCustomer(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Customer getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found with email: " + email));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Customer updateCustomerStatus(Long id, Customer.OnboardingStatus status) {
        log.info("Updating customer {} status to {}", id, status);
        
        Customer customer = getCustomer(id);
        customer.setStatus(status);
        customer.setUpdatedAt(LocalDateTime.now());
        
        Customer updatedCustomer = customerRepository.save(customer);
        log.info("Successfully updated customer {} status", id);
        
        return updatedCustomer;
    }
}