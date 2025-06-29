package com.exalt.courier.shared.controller;

import com.exalt.courier.shared.model.ApiResponse;

import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Base controller interface for CRUD operations that can be reused across courier services.
 *
 * @param <T> the entity type
 * @param <ID> the entity ID type
 */
public interface BaseController<T, ID> {

    /**
     * Creates a new entity.
     *
     * @param entity the entity to create
     * @return a ResponseEntity containing the created entity
     */
    ResponseEntity<ApiResponse<T>> create(T entity);
    
    /**
     * Finds an entity by its ID.
     *
     * @param id the entity ID
     * @return a ResponseEntity containing the found entity
     */
    ResponseEntity<ApiResponse<T>> getById(ID id);
    
    /**
     * Finds all entities.
     *
     * @return a ResponseEntity containing a list of all entities
     */
    ResponseEntity<ApiResponse<List<T>>> getAll();
    
    /**
     * Updates an existing entity.
     *
     * @param id the entity ID
     * @param entity the updated entity
     * @return a ResponseEntity containing the updated entity
     */
    ResponseEntity<ApiResponse<T>> update(ID id, T entity);
    
    /**
     * Deletes an entity by its ID.
     *
     * @param id the entity ID
     * @return a ResponseEntity with no content
     */
    ResponseEntity<ApiResponse<Void>> delete(ID id);
}
