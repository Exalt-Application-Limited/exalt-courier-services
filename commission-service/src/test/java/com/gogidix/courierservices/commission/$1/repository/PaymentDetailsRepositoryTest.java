package com.gogidix.courierservices.commission.$1.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.gogidix.courierservices.commission.$1.model.PaymentDetails;

@DataJpaTest
public class PaymentDetailsRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PaymentDetailsRepository repository;

    @Test
    void testSaveAndFind() {
        // Create test entity
        PaymentDetails entity = new PaymentDetails();
        // TODO: Set entity properties
        
        // Save entity
        PaymentDetails savedEntity = entityManager.persistAndFlush(entity);
        
        // Find by ID
        Optional<PaymentDetails> foundEntity = repository.findById(savedEntity.getId());
        
        assertTrue(foundEntity.isPresent());
        assertEquals(savedEntity.getId(), foundEntity.get().getId());
    }

    @Test
    void testFindAll() {
        // Create test entities
        PaymentDetails entity1 = new PaymentDetails();
        // TODO: Set entity1 properties
        
        PaymentDetails entity2 = new PaymentDetails();
        // TODO: Set entity2 properties
        
        // Save entities
        entityManager.persistAndFlush(entity1);
        entityManager.persistAndFlush(entity2);
        
        // Find all
        Iterable<PaymentDetails> allEntities = repository.findAll();
        
        int count = 0;
        for (@SuppressWarnings("unused") PaymentDetails entity : allEntities) {
            count++;
        }
        
        assertTrue(count >= 2);
    }
}