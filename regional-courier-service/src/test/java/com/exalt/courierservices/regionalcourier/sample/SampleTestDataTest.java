package com.exalt.courierservices.regionalcourier.sample;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.exalt.courierservices.regionalcourier.model.RegionalCourier;
import com.exalt.courierservices.regionalcourier.test.TestDataFactory;

/**
 * Sample showing how to use the TestDataFactory
 */
public class SampleTestDataTest {

    @Test
    void testDataFactoryUsage() {
        // Create a single test entity
        RegionalCourier entity = TestDataFactory.createRegionalCourier();
        assertNotNull(entity);
        
        // Create multiple test entities
        List<RegionalCourier> entities = TestDataFactory.createMultipleRegionalCouriers(3);
        assertEquals(3, entities.size());
    }
}