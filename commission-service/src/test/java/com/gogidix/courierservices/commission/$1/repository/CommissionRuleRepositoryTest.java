package com.gogidix.courierservices.commission.$1.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.gogidix.courierservices.commission.$1.model.CommissionRule;

@DataJpaTest
public class CommissionRuleRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CommissionRuleRepository repository;

    @Test
    void testSaveAndFind() {
        // Create test entity
        CommissionRule entity = new CommissionRule();
        // TODO: Set entity properties
        
        // Save entity
        CommissionRule savedEntity = entityManager.persistAndFlush(entity);
        
        // Find by ID
        Optional<CommissionRule> foundEntity = repository.findById(savedEntity.getId());
        
        assertTrue(foundEntity.isPresent());
        assertEquals(savedEntity.getId(), foundEntity.get().getId());
    }

    @Test
    void testFindAll() {
        // Create test entities
        CommissionRule entity1 = new CommissionRule();
        // TODO: Set entity1 properties
        
        CommissionRule entity2 = new CommissionRule();
        // TODO: Set entity2 properties
        
        // Save entities
        entityManager.persistAndFlush(entity1);
        entityManager.persistAndFlush(entity2);
        
        // Find all
        Iterable<CommissionRule> allEntities = repository.findAll();
        
        int count = 0;
        for (@SuppressWarnings("unused") CommissionRule entity : allEntities) {
            count++;
        }
        
        assertTrue(count >= 2);
    }
}