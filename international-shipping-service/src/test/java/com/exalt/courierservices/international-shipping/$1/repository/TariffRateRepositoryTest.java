package com.exalt.courierservices.international-shipping.$1.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.exalt.courierservices.international-shipping.$1.model.TariffRate;

@DataJpaTest
public class TariffRateRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TariffRateRepository repository;

    @Test
    void testSaveAndFind() {
        // Create test entity
        TariffRate entity = new TariffRate();
        // TODO: Set entity properties
        
        // Save entity
        TariffRate savedEntity = entityManager.persistAndFlush(entity);
        
        // Find by ID
        Optional<TariffRate> foundEntity = repository.findById(savedEntity.getId());
        
        assertTrue(foundEntity.isPresent());
        assertEquals(savedEntity.getId(), foundEntity.get().getId());
    }

    @Test
    void testFindAll() {
        // Create test entities
        TariffRate entity1 = new TariffRate();
        // TODO: Set entity1 properties
        
        TariffRate entity2 = new TariffRate();
        // TODO: Set entity2 properties
        
        // Save entities
        entityManager.persistAndFlush(entity1);
        entityManager.persistAndFlush(entity2);
        
        // Find all
        Iterable<TariffRate> allEntities = repository.findAll();
        
        int count = 0;
        for (@SuppressWarnings("unused") TariffRate entity : allEntities) {
            count++;
        }
        
        assertTrue(count >= 2);
    }
}