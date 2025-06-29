package com.microecosystem.globalhq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for branch data.
 */
@Repository
public interface BranchRepository extends JpaRepository<Object, String> {
    // Add your custom query methods here
}
