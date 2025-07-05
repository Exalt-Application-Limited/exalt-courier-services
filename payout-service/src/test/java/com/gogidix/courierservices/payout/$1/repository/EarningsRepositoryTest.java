package com.gogidix.courierservices.payout.$1.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.gogidix.courierservices.payout.$1.model.Earnings;

@DataJpaTest
public class EarningsRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EarningsRepository repository;

    @Test
    void testSaveAndFind() {
        // Create test entity
        Earnings entity = new Earnings();
        // TODO: Set entity properties
        
        // Save entity
        Earnings savedEntity = entityManager.persistAndFlush(entity);
        
        // Find by ID
        Optional<Earnings> foundEntity = repository.findById(savedEntity.getId());
        
        assertTrue(foundEntity.isPresent());
        assertEquals(savedEntity.getId(), foundEntity.get().getId());
    }

    @Test
    void testFindAll() {
        // Create test entities
        Earnings entity1 = new Earnings();
        // TODO: Set entity1 properties
        
        Earnings entity2 = new Earnings();
        // TODO: Set entity2 properties
        
        // Save entities
        entityManager.persistAndFlush(entity1);
        entityManager.persistAndFlush(entity2);
        
        // Find all
        Iterable<Earnings> allEntities = repository.findAll();
        
        int count = 0;
        for (@SuppressWarnings("unused") Earnings entity : allEntities) {
            count++;
        }
        
        assertTrue(count >= 2);
    }
}