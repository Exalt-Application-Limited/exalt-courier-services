package com.exalt.courierservices.payout.$1.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.exalt.courierservices.payout.$1.model.Payout;

@DataJpaTest
public class PayoutRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PayoutRepository repository;

    @Test
    void testSaveAndFind() {
        // Create test entity
        Payout entity = new Payout();
        // TODO: Set entity properties
        
        // Save entity
        Payout savedEntity = entityManager.persistAndFlush(entity);
        
        // Find by ID
        Optional<Payout> foundEntity = repository.findById(savedEntity.getId());
        
        assertTrue(foundEntity.isPresent());
        assertEquals(savedEntity.getId(), foundEntity.get().getId());
    }

    @Test
    void testFindAll() {
        // Create test entities
        Payout entity1 = new Payout();
        // TODO: Set entity1 properties
        
        Payout entity2 = new Payout();
        // TODO: Set entity2 properties
        
        // Save entities
        entityManager.persistAndFlush(entity1);
        entityManager.persistAndFlush(entity2);
        
        // Find all
        Iterable<Payout> allEntities = repository.findAll();
        
        int count = 0;
        for (@SuppressWarnings("unused") Payout entity : allEntities) {
            count++;
        }
        
        assertTrue(count >= 2);
    }
}