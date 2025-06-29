package com.microecosystem.courier.driver.app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

/**
 * Role entity representing user roles for authorization.
 */
@Entity
@Table(name = "roles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Role name (e.g., ROLE_ADMIN, ROLE_DRIVER)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "name", length = 20, unique = true, nullable = false)
    private RoleName name;
} 