package com.gogidix.courierservices.commission.$1.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.gogidix.courierservices.commission.$1.model.Partner;

@DataJpaTest
public class PartnerRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PartnerRepository repository;

    @Test
    void testSaveAndFind() {
        // Create test entity
        Partner entity = new Partner();
        // TODO: Set entity properties
        
        // Save entity
        Partner savedEntity = entityManager.persistAndFlush(entity);
        
        // Find by ID
        Optional<Partner> foundEntity = repository.findById(savedEntity.getId());
        
        assertTrue(foundEntity.isPresent());
        assertEquals(savedEntity.getId(), foundEntity.get().getId());
    }

    @Test
    void testFindAll() {
        // Create test entities
        Partner entity1 = new Partner();
        // TODO: Set entity1 properties
        
        Partner entity2 = new Partner();
        // TODO: Set entity2 properties
        
        // Save entities
        entityManager.persistAndFlush(entity1);
        entityManager.persistAndFlush(entity2);
        
        // Find all
        Iterable<Partner> allEntities = repository.findAll();
        
        int count = 0;
        for (@SuppressWarnings("unused") Partner entity : allEntities) {
            count++;
        }
        
        assertTrue(count >= 2);
    }
}