package com.exalt.courierservices.couriermanagement.sample;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.exalt.courierservices.couriermanagement.model.Courier;
import com.exalt.courierservices.couriermanagement.test.TestDataFactory;

/**
 * Sample showing how to use the TestDataFactory
 */
public class SampleTestDataTest {

    @Test
    void testDataFactoryUsage() {
        // Create a single test entity
        Courier entity = TestDataFactory.createCourier();
        assertNotNull(entity);
        
        // Create multiple test entities
        List<Courier> entities = TestDataFactory.createMultipleCouriers(3);
        assertEquals(3, entities.size());
    }
}