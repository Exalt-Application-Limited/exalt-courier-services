package com.exalt.courierservices.tracking.$1.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.exalt.courierservices.tracking.$1.model.Webhook;

@DataJpaTest
public class WebhookRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private WebhookRepository repository;

    @Test
    void testSaveAndFind() {
        // Create test entity
        Webhook entity = new Webhook();
        // TODO: Set entity properties
        
        // Save entity
        Webhook savedEntity = entityManager.persistAndFlush(entity);
        
        // Find by ID
        Optional<Webhook> foundEntity = repository.findById(savedEntity.getId());
        
        assertTrue(foundEntity.isPresent());
        assertEquals(savedEntity.getId(), foundEntity.get().getId());
    }

    @Test
    void testFindAll() {
        // Create test entities
        Webhook entity1 = new Webhook();
        // TODO: Set entity1 properties
        
        Webhook entity2 = new Webhook();
        // TODO: Set entity2 properties
        
        // Save entities
        entityManager.persistAndFlush(entity1);
        entityManager.persistAndFlush(entity2);
        
        // Find all
        Iterable<Webhook> allEntities = repository.findAll();
        
        int count = 0;
        for (@SuppressWarnings("unused") Webhook entity : allEntities) {
            count++;
        }
        
        assertTrue(count >= 2);
    }
}