package com.exalt.courier.location.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.socialecommerceecosystem.location.model.WalkInCustomer;

/**
 * Service interface for managing walk-in customers at physical courier locations.
 * Provides high-level business functions for customer management.
 */
public interface WalkInCustomerService {
    
    /**
     * Get all customers.
     * 
     * @return list of all customers
     */
    List<WalkInCustomer> getAllCustomers();
    
    /**
     * Get all customers with pagination.
     * 
     * @param pageable pagination parameters
     * @return page of customers
     */
    Page<WalkInCustomer> getAllCustomers(Pageable pageable);
    
    /**
     * Get a customer by ID.
     * 
     * @param customerId the ID of the customer
     * @return optional containing the customer if found
     */
    Optional<WalkInCustomer> getCustomerById(Long customerId);
    
    /**
     * Get a customer by phone number.
     * 
     * @param phone the phone number of the customer
     * @return optional containing the customer if found
     */
    Optional<WalkInCustomer> getCustomerByPhone(String phone);
    
    /**
     * Get a customer by email.
     * 
     * @param email the email of the customer
     * @return optional containing the customer if found
     */
    Optional<WalkInCustomer> getCustomerByEmail(String email);
    
    /**
     * Find customers by ID type and ID number.
     * 
     * @param idType the type of ID document
     * @param idNumber the ID number
     * @return list of customers with the specified ID information
     */
    List<WalkInCustomer> findCustomersByIdDocument(String idType, String idNumber);
    
    /**
     * Create a new customer.
     * 
     * @param customer the customer to create
     * @return the created customer
     */
    WalkInCustomer createCustomer(WalkInCustomer customer);
    
    /**
     * Update an existing customer.
     * 
     * @param customerId the ID of the customer to update
     * @param customer the updated customer details
     * @return the updated customer
     */
    WalkInCustomer updateCustomer(Long customerId, WalkInCustomer customer);
    
    /**
     * Delete a customer.
     * 
     * @param customerId the ID of the customer to delete
     */
    void deleteCustomer(Long customerId);
    
    /**
     * Search customers by name.
     * 
     * @param nameQuery the name query to search for
     * @return list of customers with names containing the query
     */
    List<WalkInCustomer> searchCustomersByName(String nameQuery);
    
    /**
     * Find customers by city.
     * 
     * @param city the city to search in
     * @return list of customers in the specified city
     */
    List<WalkInCustomer> findCustomersByCity(String city);
    
    /**
     * Find customers by postal/zip code.
     * 
     * @param zipCode the postal/zip code to search
     * @return list of customers in the specified postal/zip code area
     */
    List<WalkInCustomer> findCustomersByZipCode(String zipCode);
    
    /**
     * Find customers who have consented to marketing communications.
     * 
     * @return list of customers who have consented to marketing
     */
    List<WalkInCustomer> findCustomersWithMarketingConsent();
    
    /**
     * Record a visit for a customer.
     * 
     * @param customerId the ID of the customer
     * @return the updated customer
     */
    WalkInCustomer recordCustomerVisit(Long customerId);
    
    /**
     * Find customers by last visit date range.
     * 
     * @param startDate the start of the date range
     * @param endDate the end of the date range
     * @return list of customers who visited within the specified date range
     */
    List<WalkInCustomer> findCustomersByLastVisitRange(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find customers created within a specific date range.
     * 
     * @param startDate the start of the date range
     * @param endDate the end of the date range
     * @return list of customers created within the specified date range
     */
    List<WalkInCustomer> findCustomersByCreationDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find frequent customers (5 or more visits).
     * 
     * @return list of frequent customers
     */
    List<WalkInCustomer> findFrequentCustomers();
    
    /**
     * Find customers who have not visited since a specific date.
     * 
     * @param date the cutoff date
     * @return list of customers who have not visited since the specified date
     */
    List<WalkInCustomer> findInactiveCustomers(LocalDateTime date);
    
    /**
     * Update a customer's marketing consent status.
     * 
     * @param customerId the ID of the customer
     * @param consentGiven whether consent is given
     * @return the updated customer
     */
    WalkInCustomer updateMarketingConsent(Long customerId, boolean consentGiven);
    
    /**
     * Add notes to a customer's record.
     * 
     * @param customerId the ID of the customer
     * @param notes the notes to add
     * @return the updated customer
     */
    WalkInCustomer addCustomerNotes(Long customerId, String notes);
    
    /**
     * Find recently active customers who visited within the last 30 days.
     * 
     * @return list of recently active customers
     */
    List<WalkInCustomer> findRecentlyActiveCustomers();
    
    /**
     * Find customers who have used a specific shipping service.
     * 
     * @param serviceType the service type to search for
     * @return list of customers who have used the specified service
     */
    List<WalkInCustomer> findCustomersWhoUsedService(String serviceType);
    
    /**
     * Find customers who have shipped to a specific country.
     * 
     * @param country the destination country
     * @return list of customers who have shipped to the specified country
     */
    List<WalkInCustomer> findCustomersWhoShippedToCountry(String country);
    
    /**
     * Get customer statistics by country.
     * 
     * @return map of customer counts by country
     */
    Map<String, Long> getCustomerStatisticsByCountry();
    
    /**
     * Get customer statistics by city.
     * 
     * @return map of customer counts by city
     */
    Map<String, Long> getCustomerStatisticsByCity();
    
    /**
     * Get customer visit statistics by month.
     * 
     * @return map of visit counts by month
     */
    Map<String, Long> getCustomerVisitStatisticsByMonth();
    
    /**
     * Check if a customer exists by phone number.
     * 
     * @param phone the phone number to check
     * @return true if a customer with the specified phone number exists, false otherwise
     */
    boolean existsByPhone(String phone);
    
    /**
     * Check if a customer exists by email.
     * 
     * @param email the email to check
     * @return true if a customer with the specified email exists, false otherwise
     */
    boolean existsByEmail(String email);
}
