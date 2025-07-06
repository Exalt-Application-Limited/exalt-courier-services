package com.gogidix.courierservices.payout.$1.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.gogidix.courierservices.payout.$1.model.EarningsEntry;

@DataJpaTest
public class EarningsEntryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EarningsEntryRepository repository;

    @Test
    void testSaveAndFind() {
        // Create test entity
        EarningsEntry entity = new EarningsEntry();
        // TODO: Set entity properties
        
        // Save entity
        EarningsEntry savedEntity = entityManager.persistAndFlush(entity);
        
        // Find by ID
        Optional<EarningsEntry> foundEntity = repository.findById(savedEntity.getId());
        
        assertTrue(foundEntity.isPresent());
        assertEquals(savedEntity.getId(), foundEntity.get().getId());
    }

    @Test
    void testFindAll() {
        // Create test entities
        EarningsEntry entity1 = new EarningsEntry();
        // TODO: Set entity1 properties
        
        EarningsEntry entity2 = new EarningsEntry();
        // TODO: Set entity2 properties
        
        // Save entities
        entityManager.persistAndFlush(entity1);
        entityManager.persistAndFlush(entity2);
        
        // Find all
        Iterable<EarningsEntry> allEntities = repository.findAll();
        
        int count = 0;
        for (@SuppressWarnings("unused") EarningsEntry entity : allEntities) {
            count++;
        }
        
        assertTrue(count >= 2);
    }
}