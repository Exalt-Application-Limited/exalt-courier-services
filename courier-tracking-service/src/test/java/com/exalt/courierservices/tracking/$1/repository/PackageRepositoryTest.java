package com.exalt.courierservices.tracking.$1.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.exalt.courierservices.tracking.$1.model.Package;

@DataJpaTest
public class PackageRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PackageRepository repository;

    @Test
    void testSaveAndFind() {
        // Create test entity
        Package entity = new Package();
        // TODO: Set entity properties
        
        // Save entity
        Package savedEntity = entityManager.persistAndFlush(entity);
        
        // Find by ID
        Optional<Package> foundEntity = repository.findById(savedEntity.getId());
        
        assertTrue(foundEntity.isPresent());
        assertEquals(savedEntity.getId(), foundEntity.get().getId());
    }

    @Test
    void testFindAll() {
        // Create test entities
        Package entity1 = new Package();
        // TODO: Set entity1 properties
        
        Package entity2 = new Package();
        // TODO: Set entity2 properties
        
        // Save entities
        entityManager.persistAndFlush(entity1);
        entityManager.persistAndFlush(entity2);
        
        // Find all
        Iterable<Package> allEntities = repository.findAll();
        
        int count = 0;
        for (@SuppressWarnings("unused") Package entity : allEntities) {
            count++;
        }
        
        assertTrue(count >= 2);
    }
}