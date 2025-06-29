package com.exalt.courierservices.management.$1.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.exalt.courierservices.management.$1.model.AssignmentTask;

@DataJpaTest
public class AssignmentTaskRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AssignmentTaskRepository repository;

    @Test
    void testSaveAndFind() {
        // Create test entity
        AssignmentTask entity = new AssignmentTask();
        // TODO: Set entity properties
        
        // Save entity
        AssignmentTask savedEntity = entityManager.persistAndFlush(entity);
        
        // Find by ID
        Optional<AssignmentTask> foundEntity = repository.findById(savedEntity.getId());
        
        assertTrue(foundEntity.isPresent());
        assertEquals(savedEntity.getId(), foundEntity.get().getId());
    }

    @Test
    void testFindAll() {
        // Create test entities
        AssignmentTask entity1 = new AssignmentTask();
        // TODO: Set entity1 properties
        
        AssignmentTask entity2 = new AssignmentTask();
        // TODO: Set entity2 properties
        
        // Save entities
        entityManager.persistAndFlush(entity1);
        entityManager.persistAndFlush(entity2);
        
        // Find all
        Iterable<AssignmentTask> allEntities = repository.findAll();
        
        int count = 0;
        for (@SuppressWarnings("unused") AssignmentTask entity : allEntities) {
            count++;
        }
        
        assertTrue(count >= 2);
    }
}