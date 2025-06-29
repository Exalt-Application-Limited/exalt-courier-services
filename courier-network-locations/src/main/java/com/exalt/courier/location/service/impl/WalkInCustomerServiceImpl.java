package com.exalt.courier.location.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.socialecommerceecosystem.location.model.WalkInCustomer;
import com.socialecommerceecosystem.location.model.WalkInShipment;
import com.socialecommerceecosystem.location.repository.WalkInCustomerRepository;
import com.socialecommerceecosystem.location.repository.WalkInShipmentRepository;
import com.socialecommerceecosystem.location.service.WalkInCustomerService;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of the WalkInCustomerService interface.
 * Provides business logic for managing walk-in customers at physical courier locations.
 */
@Service
@Slf4j
public class WalkInCustomerServiceImpl implements WalkInCustomerService {

    private final WalkInCustomerRepository customerRepository;
    private final WalkInShipmentRepository shipmentRepository;

    @Autowired
    public WalkInCustomerServiceImpl(
            WalkInCustomerRepository customerRepository,
            WalkInShipmentRepository shipmentRepository) {
        this.customerRepository = customerRepository;
        this.shipmentRepository = shipmentRepository;
    }

    @Override
    public List<WalkInCustomer> getAllCustomers() {
        log.debug("Getting all walk-in customers");
        return customerRepository.findAll();
    }

    @Override
    public Page<WalkInCustomer> getAllCustomers(Pageable pageable) {
        log.debug("Getting walk-in customers with pagination: {}", pageable);
        return customerRepository.findAll(pageable);
    }

    @Override
    public Optional<WalkInCustomer> getCustomerById(Long customerId) {
        log.debug("Getting walk-in customer with ID: {}", customerId);
        return customerRepository.findById(customerId);
    }

    @Override
    public Optional<WalkInCustomer> getCustomerByPhone(String phone) {
        log.debug("Getting walk-in customer by phone: {}", phone);
        return customerRepository.findByPhone(phone);
    }

    @Override
    public Optional<WalkInCustomer> getCustomerByEmail(String email) {
        log.debug("Getting walk-in customer by email: {}", email);
        return customerRepository.findByEmail(email);
    }

    @Override
    public List<WalkInCustomer> findCustomersByIdDocument(String idType, String idNumber) {
        log.debug("Finding walk-in customers by ID type: {} and ID number: {}", idType, idNumber);
        return customerRepository.findByIdTypeAndIdNumber(idType, idNumber);
    }

    @Override
    @Transactional
    public WalkInCustomer createCustomer(WalkInCustomer customer) {
        log.info("Creating new walk-in customer: {} {}", customer.getFirstName(), customer.getLastName());
        
        // Set default values if not provided
        if (customer.getCreatedAt() == null) {
            customer.setCreatedAt(LocalDateTime.now());
        }
        if (customer.getUpdatedAt() == null) {
            customer.setUpdatedAt(LocalDateTime.now());
        }
        if (customer.getLastVisitDate() == null) {
            customer.setLastVisitDate(LocalDateTime.now());
        }
        if (customer.getVisitCount() == null) {
            customer.setVisitCount(1);
        }
        
        WalkInCustomer savedCustomer = customerRepository.save(customer);
        log.info("Successfully created walk-in customer with ID: {}", savedCustomer.getId());
        
        return savedCustomer;
    }

    @Override
    @Transactional
    public WalkInCustomer updateCustomer(Long customerId, WalkInCustomer customer) {
        log.info("Updating walk-in customer with ID: {}", customerId);
        
        return customerRepository.findById(customerId)
                .map(existingCustomer -> {
                    // Update basic information
                    existingCustomer.setFirstName(customer.getFirstName());
                    existingCustomer.setLastName(customer.getLastName());
                    existingCustomer.setEmail(customer.getEmail());
                    existingCustomer.setPhone(customer.getPhone());
                    existingCustomer.setAddress(customer.getAddress());
                    existingCustomer.setCity(customer.getCity());
                    existingCustomer.setState(customer.getState());
                    existingCustomer.setCountry(customer.getCountry());
                    existingCustomer.setPostalCode(customer.getPostalCode());
                    existingCustomer.setIdType(customer.getIdType());
                    existingCustomer.setIdNumber(customer.getIdNumber());
                    existingCustomer.setMarketingConsentGiven(customer.isMarketingConsentGiven());
                    existingCustomer.setNotes(customer.getNotes());
                    existingCustomer.setUpdatedAt(LocalDateTime.now());
                    
                    WalkInCustomer updatedCustomer = customerRepository.save(existingCustomer);
                    log.info("Successfully updated walk-in customer with ID: {}", updatedCustomer.getId());
                    
                    return updatedCustomer;
                })
                .orElseThrow(() -> {
                    log.error("Walk-in customer with ID: {} not found", customerId);
                    return new IllegalArgumentException("Walk-in customer not found with ID: " + customerId);
                });
    }

    @Override
    @Transactional
    public void deleteCustomer(Long customerId) {
        log.info("Deleting walk-in customer with ID: {}", customerId);
        
        if (customerRepository.existsById(customerId)) {
            customerRepository.deleteById(customerId);
            log.info("Successfully deleted walk-in customer with ID: {}", customerId);
        } else {
            log.error("Walk-in customer with ID: {} not found", customerId);
            throw new IllegalArgumentException("Walk-in customer not found with ID: " + customerId);
        }
    }

    @Override
    public List<WalkInCustomer> searchCustomersByName(String nameQuery) {
        log.debug("Searching walk-in customers by name query: {}", nameQuery);
        return customerRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(nameQuery, nameQuery);
    }

    @Override
    public List<WalkInCustomer> findCustomersByCity(String city) {
        log.debug("Finding walk-in customers by city: {}", city);
        return customerRepository.findByCity(city);
    }

    @Override
    public List<WalkInCustomer> findCustomersByZipCode(String zipCode) {
        log.debug("Finding walk-in customers by postal/zip code: {}", zipCode);
        return customerRepository.findByPostalCode(zipCode);
    }

    @Override
    public List<WalkInCustomer> findCustomersWithMarketingConsent() {
        log.debug("Finding walk-in customers with marketing consent");
        return customerRepository.findByMarketingConsentGivenTrue();
    }

    @Override
    @Transactional
    public WalkInCustomer recordCustomerVisit(Long customerId) {
        log.info("Recording visit for walk-in customer with ID: {}", customerId);
        
        return customerRepository.findById(customerId)
                .map(customer -> {
                    // Update visit count and date
                    Integer currentVisitCount = customer.getVisitCount() != null ? customer.getVisitCount() : 0;
                    customer.setVisitCount(currentVisitCount + 1);
                    customer.setLastVisitDate(LocalDateTime.now());
                    customer.setUpdatedAt(LocalDateTime.now());
                    
                    WalkInCustomer updatedCustomer = customerRepository.save(customer);
                    log.info("Successfully recorded visit for walk-in customer with ID: {}", customerId);
                    
                    return updatedCustomer;
                })
                .orElseThrow(() -> {
                    log.error("Walk-in customer with ID: {} not found", customerId);
                    return new IllegalArgumentException("Walk-in customer not found with ID: " + customerId);
                });
    }

    @Override
    public List<WalkInCustomer> findCustomersByLastVisitRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Finding walk-in customers by last visit date range: {} to {}", startDate, endDate);
        return customerRepository.findByLastVisitDateBetween(startDate, endDate);
    }

    @Override
    public List<WalkInCustomer> findCustomersByCreationDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Finding walk-in customers by creation date range: {} to {}", startDate, endDate);
        return customerRepository.findByCreatedAtBetween(startDate, endDate);
    }

    @Override
    public List<WalkInCustomer> findFrequentCustomers() {
        log.debug("Finding frequent walk-in customers (5 or more visits)");
        return customerRepository.findByVisitCountGreaterThanEqual(5);
    }

    @Override
    public List<WalkInCustomer> findInactiveCustomers(LocalDateTime date) {
        log.debug("Finding inactive walk-in customers since: {}", date);
        return customerRepository.findByLastVisitDateBefore(date);
    }

    @Override
    @Transactional
    public WalkInCustomer updateMarketingConsent(Long customerId, boolean consentGiven) {
        log.info("Updating marketing consent for walk-in customer with ID: {} to: {}", customerId, consentGiven);
        
        return customerRepository.findById(customerId)
                .map(customer -> {
                    customer.setMarketingConsentGiven(consentGiven);
                    customer.setUpdatedAt(LocalDateTime.now());
                    
                    WalkInCustomer updatedCustomer = customerRepository.save(customer);
                    log.info("Successfully updated marketing consent for walk-in customer with ID: {}", customerId);
                    
                    return updatedCustomer;
                })
                .orElseThrow(() -> {
                    log.error("Walk-in customer with ID: {} not found", customerId);
                    return new IllegalArgumentException("Walk-in customer not found with ID: " + customerId);
                });
    }

    @Override
    @Transactional
    public WalkInCustomer addCustomerNotes(Long customerId, String notes) {
        log.info("Adding notes for walk-in customer with ID: {}", customerId);
        
        return customerRepository.findById(customerId)
                .map(customer -> {
                    String existingNotes = customer.getNotes() != null ? customer.getNotes() + "\n" : "";
                    customer.setNotes(existingNotes + notes);
                    customer.setUpdatedAt(LocalDateTime.now());
                    
                    WalkInCustomer updatedCustomer = customerRepository.save(customer);
                    log.info("Successfully added notes for walk-in customer with ID: {}", customerId);
                    
                    return updatedCustomer;
                })
                .orElseThrow(() -> {
                    log.error("Walk-in customer with ID: {} not found", customerId);
                    return new IllegalArgumentException("Walk-in customer not found with ID: " + customerId);
                });
    }

    @Override
    public List<WalkInCustomer> findRecentlyActiveCustomers() {
        log.debug("Finding recently active walk-in customers (last 30 days)");
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        return customerRepository.findByLastVisitDateAfter(thirtyDaysAgo);
    }

    @Override
    public List<WalkInCustomer> findCustomersWhoUsedService(String serviceType) {
        log.debug("Finding walk-in customers who used service: {}", serviceType);
        List<WalkInShipment> shipments = shipmentRepository.findByServiceTypeContainingIgnoreCase(serviceType);
        
        return shipments.stream()
                .map(WalkInShipment::getCustomer)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<WalkInCustomer> findCustomersWhoShippedToCountry(String country) {
        log.debug("Finding walk-in customers who shipped to country: {}", country);
        List<WalkInShipment> shipments = shipmentRepository.findByDestinationCountryIgnoreCase(country);
        
        return shipments.stream()
                .map(WalkInShipment::getCustomer)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Long> getCustomerStatisticsByCountry() {
        log.debug("Getting walk-in customer statistics by country");
        
        Map<String, Long> statsByCountry = new HashMap<>();
        List<WalkInCustomer> allCustomers = customerRepository.findAll();
        
        return allCustomers.stream()
                .filter(customer -> customer.getCountry() != null && !customer.getCountry().isEmpty())
                .collect(Collectors.groupingBy(
                        WalkInCustomer::getCountry,
                        Collectors.counting()));
    }

    @Override
    public Map<String, Long> getCustomerStatisticsByCity() {
        log.debug("Getting walk-in customer statistics by city");
        
        Map<String, Long> statsByCity = new HashMap<>();
        List<WalkInCustomer> allCustomers = customerRepository.findAll();
        
        return allCustomers.stream()
                .filter(customer -> customer.getCity() != null && !customer.getCity().isEmpty())
                .collect(Collectors.groupingBy(
                        WalkInCustomer::getCity,
                        Collectors.counting()));
    }

    @Override
    public Map<String, Long> getCustomerVisitStatisticsByMonth() {
        log.debug("Getting walk-in customer visit statistics by month");
        
        Map<String, Long> statsByMonth = new HashMap<>();
        List<WalkInCustomer> allCustomers = customerRepository.findAll();
        
        return allCustomers.stream()
                .filter(customer -> customer.getLastVisitDate() != null)
                .collect(Collectors.groupingBy(
                        customer -> customer.getLastVisitDate().getYear() + "-" + 
                                   String.format("%02d", customer.getLastVisitDate().getMonthValue()),
                        Collectors.counting()));
    }

    @Override
    public boolean existsByPhone(String phone) {
        log.debug("Checking if walk-in customer exists with phone: {}", phone);
        return customerRepository.existsByPhone(phone);
    }

    @Override
    public boolean existsByEmail(String email) {
        log.debug("Checking if walk-in customer exists with email: {}", email);
        return customerRepository.existsByEmail(email);
    }
}
