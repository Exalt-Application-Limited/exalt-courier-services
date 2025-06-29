package com.exalt.courier.courier.branch.repository;

import com.microsocial.courier.branch.model.corporate.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {
    
    List<Branch> findByActive(boolean active);
    
    List<Branch> findByBranchNameContainingIgnoreCase(String branchName);
    
    List<Branch> findByCityIgnoreCase(String city);
    
    List<Branch> findByCountryIgnoreCase(String country);
}
