package com.gogidix.courierservices.commission.$1.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.gogidix.courierservices.commission.$1.model.CommissionEntry;

@DataJpaTest
public class CommissionEntryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CommissionEntryRepository repository;

    @Test
    void testSaveAndFind() {
        // Create test entity
        CommissionEntry entity = new CommissionEntry();
        // TODO: Set entity properties
        
        // Save entity
        CommissionEntry savedEntity = entityManager.persistAndFlush(entity);
        
        // Find by ID
        Optional<CommissionEntry> foundEntity = repository.findById(savedEntity.getId());
        
        assertTrue(foundEntity.isPresent());
        assertEquals(savedEntity.getId(), foundEntity.get().getId());
    }

    @Test
    void testFindAll() {
        // Create test entities
        CommissionEntry entity1 = new CommissionEntry();
        // TODO: Set entity1 properties
        
        CommissionEntry entity2 = new CommissionEntry();
        // TODO: Set entity2 properties
        
        // Save entities
        entityManager.persistAndFlush(entity1);
        entityManager.persistAndFlush(entity2);
        
        // Find all
        Iterable<CommissionEntry> allEntities = repository.findAll();
        
        int count = 0;
        for (@SuppressWarnings("unused") CommissionEntry entity : allEntities) {
            count++;
        }
        
        assertTrue(count >= 2);
    }
}