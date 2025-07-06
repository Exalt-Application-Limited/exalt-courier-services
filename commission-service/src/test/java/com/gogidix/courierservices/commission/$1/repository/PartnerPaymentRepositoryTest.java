package com.gogidix.courierservices.commission.$1.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.gogidix.courierservices.commission.$1.model.PartnerPayment;

@DataJpaTest
public class PartnerPaymentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PartnerPaymentRepository repository;

    @Test
    void testSaveAndFind() {
        // Create test entity
        PartnerPayment entity = new PartnerPayment();
        // TODO: Set entity properties
        
        // Save entity
        PartnerPayment savedEntity = entityManager.persistAndFlush(entity);
        
        // Find by ID
        Optional<PartnerPayment> foundEntity = repository.findById(savedEntity.getId());
        
        assertTrue(foundEntity.isPresent());
        assertEquals(savedEntity.getId(), foundEntity.get().getId());
    }

    @Test
    void testFindAll() {
        // Create test entities
        PartnerPayment entity1 = new PartnerPayment();
        // TODO: Set entity1 properties
        
        PartnerPayment entity2 = new PartnerPayment();
        // TODO: Set entity2 properties
        
        // Save entities
        entityManager.persistAndFlush(entity1);
        entityManager.persistAndFlush(entity2);
        
        // Find all
        Iterable<PartnerPayment> allEntities = repository.findAll();
        
        int count = 0;
        for (@SuppressWarnings("unused") PartnerPayment entity : allEntities) {
            count++;
        }
        
        assertTrue(count >= 2);
    }
}