package com.microecosystem.globalhq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for delivery data.
 */
@Repository
public interface DeliveryRepository extends JpaRepository<Object, String> {
    // Add your custom query methods here
}
