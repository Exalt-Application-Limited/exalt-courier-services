package com.microecosystem.courier.driver.app.repository;

import com.microecosystem.courier.driver.app.model.Role;
import com.microecosystem.courier.driver.app.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Role entity operations.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    /**
     * Find a role by name.
     *
     * @param name role name
     * @return optional role
     */
    Optional<Role> findByName(RoleName name);
} 