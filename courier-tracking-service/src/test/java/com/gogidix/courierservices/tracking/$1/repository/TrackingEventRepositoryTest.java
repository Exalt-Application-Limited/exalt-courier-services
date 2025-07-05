package com.gogidix.courierservices.tracking.$1.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.gogidix.courierservices.tracking.$1.model.TrackingEvent;

@DataJpaTest
public class TrackingEventRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TrackingEventRepository repository;

    @Test
    void testSaveAndFind() {
        // Create test entity
        TrackingEvent entity = new TrackingEvent();
        // TODO: Set entity properties
        
        // Save entity
        TrackingEvent savedEntity = entityManager.persistAndFlush(entity);
        
        // Find by ID
        Optional<TrackingEvent> foundEntity = repository.findById(savedEntity.getId());
        
        assertTrue(foundEntity.isPresent());
        assertEquals(savedEntity.getId(), foundEntity.get().getId());
    }

    @Test
    void testFindAll() {
        // Create test entities
        TrackingEvent entity1 = new TrackingEvent();
        // TODO: Set entity1 properties
        
        TrackingEvent entity2 = new TrackingEvent();
        // TODO: Set entity2 properties
        
        // Save entities
        entityManager.persistAndFlush(entity1);
        entityManager.persistAndFlush(entity2);
        
        // Find all
        Iterable<TrackingEvent> allEntities = repository.findAll();
        
        int count = 0;
        for (@SuppressWarnings("unused") TrackingEvent entity : allEntities) {
            count++;
        }
        
        assertTrue(count >= 2);
    }
}