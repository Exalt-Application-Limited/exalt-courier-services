package com.exalt.courier.shared.controller;

import com.exalt.courier.shared.exception.ResourceNotFoundException;
import com.exalt.courier.shared.model.ApiResponse;
import com.exalt.courier.shared.service.BaseService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import jakarta.validation.Valid;

import lombok.extern.slf4j.Slf4j;

/**
 * Abstract base controller implementation for common CRUD operations.
 * Provides default implementations that can be reused across courier services.
 *
 * @param <T> the entity type
 * @param <ID> the entity ID type
 * @param <S> the service type
 */
@Slf4j
public abstract class AbstractBaseController<T, ID, S extends BaseService<T, ID>> implements BaseController<T, ID> {

    protected final S service;
    protected final String entityName;
    
    /**
     * Creates a new AbstractBaseController.
     *
     * @param service the service to use
     * @param entityName the name of the entity (used in log messages and responses)
     */
    protected AbstractBaseController(S service, String entityName) {
        this.service = service;
        this.entityName = entityName;
    }
    
    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<T>> create(@Valid @RequestBody T entity) {
        log.debug("REST request to create {}: {}", entityName, entity);
        
        T createdEntity = service.create(entity);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdEntity, entityName + " created successfully"));
    }
    
    @Override
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<T>> getById(@PathVariable ID id) {
        log.debug("REST request to get {} with ID: {}", entityName, id);
        
        return service.findById(id)
                .map(entity -> ResponseEntity.ok(ApiResponse.success(entity)))
                .orElseThrow(() -> new ResourceNotFoundException(entityName + " not found with ID: " + id));
    }
    
    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<List<T>>> getAll() {
        log.debug("REST request to get all {}", entityName);
        
        List<T> entities = service.findAll();
        
        return ResponseEntity.ok(ApiResponse.success(entities));
    }
    
    @Override
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<T>> update(@PathVariable ID id, @Valid @RequestBody T entity) {
        log.debug("REST request to update {} with ID: {}", entityName, id);
        
        T updatedEntity = service.update(id, entity);
        
        return ResponseEntity.ok(ApiResponse.success(updatedEntity, entityName + " updated successfully"));
    }
    
    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable ID id) {
        log.debug("REST request to delete {} with ID: {}", entityName, id);
        
        service.delete(id);
        
        return ResponseEntity.ok(ApiResponse.success(null, entityName + " deleted successfully"));
    }
}
