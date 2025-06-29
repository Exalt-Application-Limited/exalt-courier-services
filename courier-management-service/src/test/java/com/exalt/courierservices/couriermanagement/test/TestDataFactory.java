package com.exalt.courierservices.couriermanagement.test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.exalt.courierservices.couriermanagement.model.*;

/**
 * Test data factory for couriermanagement domain objects.
 * Use this class in tests to generate consistent test data.
 */
public class TestDataFactory {
    
    /**
     * Creates a test Courier with random data
     */
    public static Courier createCourier() {
        Courier entity = new Courier();
                entity.setId(1L);
        entity.setName("Test Courier");
        entity.setPhone("+1234567890");
        entity.setActive(true);
        entity.setEmail("test.courier@example.com");
        entity.setRating(4.5);

        return entity;
    }
    
    /**
     * Creates a list of test Couriers
     */
    public static List<Courier> createMultipleCouriers(int count) {
        List<Courier> entities = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            entities.add(createCourier());
        }
        return entities;
    }
    
        
    /**
     * Creates a test Assignment with random data
     */
    public static Assignment createAssignment() {
        Assignment entity = new Assignment();
        entity.setOrderId(100L);
        entity.setStatus(AssignmentStatus.PENDING);
        entity.setCourierId(1L);
        entity.setId(1L);
        entity.setCreatedAt(LocalDateTime.now());

        return entity;
    }
    
    /**
     * Creates a list of test Assignments
     */
    public static List<Assignment> createMultipleAssignments(int count) {
        List<Assignment> entities = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            entities.add(createAssignment());
        }
        return entities;
    }
}