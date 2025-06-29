package com.exalt.courier.shared.service;

import java.util.List;
import java.util.Optional;

/**
 * Base service interface for CRUD operations that can be reused across courier services.
 *
 * @param <T> the entity type
 * @param <ID> the entity ID type
 */
public interface BaseService<T, ID> {

    /**
     * Creates a new entity.
     *
     * @param entity the entity to create
     * @return the created entity
     */
    T create(T entity);
    
    /**
     * Finds an entity by its ID.
     *
     * @param id the entity ID
     * @return an Optional containing the found entity, or an empty Optional if not found
     */
    Optional<T> findById(ID id);
    
    /**
     * Finds all entities.
     *
     * @return a list of all entities
     */
    List<T> findAll();
    
    /**
     * Updates an existing entity.
     *
     * @param id the entity ID
     * @param entity the updated entity
     * @return the updated entity
     */
    T update(ID id, T entity);
    
    /**
     * Deletes an entity by its ID.
     *
     * @param id the entity ID
     */
    void delete(ID id);
    
    /**
     * Checks if an entity exists by its ID.
     *
     * @param id the entity ID
     * @return true if the entity exists, false otherwise
     */
    boolean existsById(ID id);
}
