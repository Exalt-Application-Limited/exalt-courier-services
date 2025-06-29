package com.exalt.courierservices.management.$1.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.exalt.courierservices.management.$1.model.Courier;

@DataJpaTest
public class CourierRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CourierRepository repository;

    @Test
    void testSaveAndFind() {
        // Create test entity
        Courier entity = new Courier();
        // TODO: Set entity properties
        
        // Save entity
        Courier savedEntity = entityManager.persistAndFlush(entity);
        
        // Find by ID
        Optional<Courier> foundEntity = repository.findById(savedEntity.getId());
        
        assertTrue(foundEntity.isPresent());
        assertEquals(savedEntity.getId(), foundEntity.get().getId());
    }

    @Test
    void testFindAll() {
        // Create test entities
        Courier entity1 = new Courier();
        // TODO: Set entity1 properties
        
        Courier entity2 = new Courier();
        // TODO: Set entity2 properties
        
        // Save entities
        entityManager.persistAndFlush(entity1);
        entityManager.persistAndFlush(entity2);
        
        // Find all
        Iterable<Courier> allEntities = repository.findAll();
        
        int count = 0;
        for (@SuppressWarnings("unused") Courier entity : allEntities) {
            count++;
        }
        
        assertTrue(count >= 2);
    }
}