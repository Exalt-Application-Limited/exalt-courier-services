package com.exalt.courier.courier.branch.service.impl;

import com.microsocial.courier.branch.model.corporate.Branch;
import com.microsocial.courier.branch.repository.BranchRepository;
import com.microsocial.courier.branch.service.BranchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;

    @Autowired
    public BranchServiceImpl(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Branch> getAllBranches() {
        return branchRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Branch> getBranchById(Long id) {
        return branchRepository.findById(id);
    }

    @Override
    public Branch saveBranch(Branch branch) {
        LocalDateTime now = LocalDateTime.now();
        
        if (branch.getId() == null) {
            branch.setCreatedAt(now);
        }
        
        branch.setUpdatedAt(now);
        return branchRepository.save(branch);
    }

    @Override
    public void deleteBranch(Long id) {
        branchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return branchRepository.existsById(id);
    }
}
