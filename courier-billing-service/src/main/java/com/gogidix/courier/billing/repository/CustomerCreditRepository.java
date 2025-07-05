package com.gogidix.courier.billing.repository;

import com.gogidix.courier.billing.model.CustomerCredit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Customer Credit operations.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Repository
public interface CustomerCreditRepository extends JpaRepository<CustomerCredit, UUID> {

    /**
     * Find customer credit by customer ID.
     */
    Optional<CustomerCredit> findByCustomerId(String customerId);

    /**
     * Check if customer credit exists for customer ID.
     */
    boolean existsByCustomerId(String customerId);
}