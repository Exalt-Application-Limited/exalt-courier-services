package com.exalt.courierservices.management.$1.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.exalt.courierservices.management.$1.model.PerformanceMetric;

@DataJpaTest
public class PerformanceMetricRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PerformanceMetricRepository repository;

    @Test
    void testSaveAndFind() {
        // Create test entity
        PerformanceMetric entity = new PerformanceMetric();
        // TODO: Set entity properties
        
        // Save entity
        PerformanceMetric savedEntity = entityManager.persistAndFlush(entity);
        
        // Find by ID
        Optional<PerformanceMetric> foundEntity = repository.findById(savedEntity.getId());
        
        assertTrue(foundEntity.isPresent());
        assertEquals(savedEntity.getId(), foundEntity.get().getId());
    }

    @Test
    void testFindAll() {
        // Create test entities
        PerformanceMetric entity1 = new PerformanceMetric();
        // TODO: Set entity1 properties
        
        PerformanceMetric entity2 = new PerformanceMetric();
        // TODO: Set entity2 properties
        
        // Save entities
        entityManager.persistAndFlush(entity1);
        entityManager.persistAndFlush(entity2);
        
        // Find all
        Iterable<PerformanceMetric> allEntities = repository.findAll();
        
        int count = 0;
        for (@SuppressWarnings("unused") PerformanceMetric entity : allEntities) {
            count++;
        }
        
        assertTrue(count >= 2);
    }
}