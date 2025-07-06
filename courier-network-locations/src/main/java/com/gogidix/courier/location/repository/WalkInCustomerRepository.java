package com.gogidix.courier.location.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.socialecommerceecosystem.location.model.WalkInCustomer;

/**
 * Repository interface for WalkInCustomer entity.
 * Provides methods for accessing and querying walk-in customer data.
 */
@Repository
public interface WalkInCustomerRepository extends JpaRepository<WalkInCustomer, Long> {
    
    /**
     * Find customer by phone number.
     * 
     * @param phone the phone number to search for
     * @return optional customer with the specified phone number
     */
    Optional<WalkInCustomer> findByPhone(String phone);
    
    /**
     * Find customer by email.
     * 
     * @param email the email to search for
     * @return optional customer with the specified email
     */
    Optional<WalkInCustomer> findByEmail(String email);
    
    /**
     * Find customers by ID type and ID number.
     * 
     * @param idType the type of ID document
     * @param idNumber the ID number
     * @return list of customers with the specified ID information
     */
    List<WalkInCustomer> findByIdTypeAndIdNumber(String idType, String idNumber);
    
    /**
     * Search customers by name.
     * 
     * @param firstName the first name to search for (partial match)
     * @param lastName the last name to search for (partial match)
     * @return list of customers matching the name criteria
     */
    List<WalkInCustomer> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String firstName, String lastName);
    
    /**
     * Find customers by postal/zip code.
     * 
     * @param zipCode the postal/zip code to search for
     * @return list of customers in the specified postal/zip code area
     */
    List<WalkInCustomer> findByZipCode(String zipCode);
    
    /**
     * Find customers by city.
     * 
     * @param city the city to search for
     * @return list of customers in the specified city
     */
    List<WalkInCustomer> findByCity(String city);
    
    /**
     * Find customers who have consented to marketing communications.
     * 
     * @return list of customers who have consented to marketing
     */
    List<WalkInCustomer> findByMarketingConsentTrue();
    
    /**
     * Find customers who have consented to marketing with pagination.
     * 
     * @param pageable pagination parameters
     * @return page of customers who have consented to marketing
     */
    Page<WalkInCustomer> findByMarketingConsentTrue(Pageable pageable);
    
    /**
     * Find customers by last visit date range.
     * 
     * @param startDate the start of the date range
     * @param endDate the end of the date range
     * @return list of customers who visited within the specified date range
     */
    List<WalkInCustomer> findByLastVisitBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find customers created within a specific date range.
     * 
     * @param startDate the start of the date range
     * @param endDate the end of the date range
     * @return list of customers created within the specified date range
     */
    List<WalkInCustomer> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find frequent customers (5 or more visits).
     * 
     * @return list of customers with 5 or more visits
     */
    List<WalkInCustomer> findByVisitCountGreaterThanEqual(Integer visitCount);
    
    /**
     * Find customers who have not visited since a specific date.
     * 
     * @param date the cutoff date
     * @return list of customers who have not visited since the specified date
     */
    List<WalkInCustomer> findByLastVisitBefore(LocalDateTime date);
    
    /**
     * Find customers with specific notes.
     * 
     * @param notesContent the content to search for in notes
     * @return list of customers with notes containing the specified content
     */
    List<WalkInCustomer> findByNotesContainingIgnoreCase(String notesContent);
    
    /**
     * Find recently active customers who visited within the last 30 days.
     * 
     * @param cutoffDate the cutoff date (30 days ago)
     * @return list of customers who visited after the cutoff date
     */
    @Query("SELECT c FROM WalkInCustomer c WHERE c.lastVisit >= :cutoffDate")
    List<WalkInCustomer> findRecentlyActiveCustomers(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    /**
     * Find customers who have used a specific shipping service.
     * 
     * @param serviceType the service type to search for
     * @return list of customers who have used the specified service
     */
    @Query("SELECT DISTINCT c FROM WalkInCustomer c JOIN c.shipments s WHERE s.serviceType = :serviceType")
    List<WalkInCustomer> findCustomersWhoUsedService(@Param("serviceType") String serviceType);
    
    /**
     * Find customers who have shipped to a specific country.
     * 
     * @param country the destination country
     * @return list of customers who have shipped to the specified country
     */
    @Query("SELECT DISTINCT c FROM WalkInCustomer c JOIN c.shipments s WHERE s.recipientCountry = :country")
    List<WalkInCustomer> findCustomersWhoShippedToCountry(@Param("country") String country);
    
    /**
     * Count customers by country.
     * 
     * @param country the country to count customers in
     * @return the count of customers in the specified country
     */
    long countByCountry(String country);
    
    /**
     * Count customers by city.
     * 
     * @param city the city to count customers in
     * @return the count of customers in the specified city
     */
    long countByCity(String city);
}
