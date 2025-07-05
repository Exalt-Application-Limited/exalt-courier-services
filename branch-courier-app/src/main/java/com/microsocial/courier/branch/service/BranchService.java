package com.gogidix.courier.courier.branch.service;

import com.microsocial.courier.branch.model.corporate.Branch;

import java.util.List;
import java.util.Optional;

public interface BranchService {
    List<Branch> getAllBranches();
    Optional<Branch> getBranchById(Long id);
    Branch saveBranch(Branch branch);
    void deleteBranch(Long id);
    boolean existsById(Long id);
}
