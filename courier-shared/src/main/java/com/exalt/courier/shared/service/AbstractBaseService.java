package com.exalt.courier.shared.service;

import com.exalt.courier.shared.exception.ResourceNotFoundException;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

/**
 * Abstract base service implementation for common CRUD operations.
 * Provides default implementations that can be reused across courier services.
 *
 * @param <T> the entity type
 * @param <ID> the entity ID type
 * @param <R> the repository type
 */
@Slf4j
public abstract class AbstractBaseService<T, ID, R extends JpaRepository<T, ID>> implements BaseService<T, ID> {

    protected final R repository;
    protected final String entityName;
    
    /**
     * Creates a new AbstractBaseService.
     *
     * @param repository the repository to use
     * @param entityName the name of the entity (used in log messages and exceptions)
     */
    protected AbstractBaseService(R repository, String entityName) {
        this.repository = repository;
        this.entityName = entityName;
    }
    
    @Override
    @Transactional
    public T create(T entity) {
        log.debug("Creating new {}: {}", entityName, entity);
        
        T savedEntity = repository.save(entity);
        
        log.debug("{} created: {}", entityName, savedEntity);
        return savedEntity;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<T> findById(ID id) {
        log.debug("Finding {} by ID: {}", entityName, id);
        
        return repository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<T> findAll() {
        log.debug("Finding all {}", entityName);
        
        return repository.findAll();
    }
    
    @Override
    @Transactional
    public T update(ID id, T entity) {
        log.debug("Updating {} with ID: {}", entityName, id);
        
        if (!repository.existsById(id)) {
            log.error("{} not found with ID: {}", entityName, id);
            throw new ResourceNotFoundException(entityName + " not found with ID: " + id);
        }
        
        T updatedEntity = repository.save(entity);
        
        log.debug("{} updated: {}", entityName, updatedEntity);
        return updatedEntity;
    }
    
    @Override
    @Transactional
    public void delete(ID id) {
        log.debug("Deleting {} with ID: {}", entityName, id);
        
        if (!repository.existsById(id)) {
            log.error("{} not found with ID: {}", entityName, id);
            throw new ResourceNotFoundException(entityName + " not found with ID: " + id);
        }
        
        repository.deleteById(id);
        log.debug("{} deleted with ID: {}", entityName, id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsById(ID id) {
        return repository.existsById(id);
    }
}
