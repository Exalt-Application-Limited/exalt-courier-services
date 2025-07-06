package com.gogidix.courierservices.management.$1.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.gogidix.courierservices.management.$1.model.Assignment;

@DataJpaTest
public class AssignmentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AssignmentRepository repository;

    @Test
    void testSaveAndFind() {
        // Create test entity
        Assignment entity = new Assignment();
        // TODO: Set entity properties
        
        // Save entity
        Assignment savedEntity = entityManager.persistAndFlush(entity);
        
        // Find by ID
        Optional<Assignment> foundEntity = repository.findById(savedEntity.getId());
        
        assertTrue(foundEntity.isPresent());
        assertEquals(savedEntity.getId(), foundEntity.get().getId());
    }

    @Test
    void testFindAll() {
        // Create test entities
        Assignment entity1 = new Assignment();
        // TODO: Set entity1 properties
        
        Assignment entity2 = new Assignment();
        // TODO: Set entity2 properties
        
        // Save entities
        entityManager.persistAndFlush(entity1);
        entityManager.persistAndFlush(entity2);
        
        // Find all
        Iterable<Assignment> allEntities = repository.findAll();
        
        int count = 0;
        for (@SuppressWarnings("unused") Assignment entity : allEntities) {
            count++;
        }
        
        assertTrue(count >= 2);
    }
}