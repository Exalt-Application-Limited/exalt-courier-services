package com.exalt.courierservices.couriertracking.sample;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.exalt.courierservices.couriertracking.model.TrackingRecord;
import com.exalt.courierservices.couriertracking.test.TestDataFactory;

/**
 * Sample showing how to use the TestDataFactory
 */
public class SampleTestDataTest {

    @Test
    void testDataFactoryUsage() {
        // Create a single test entity
        TrackingRecord entity = TestDataFactory.createTrackingRecord();
        assertNotNull(entity);
        
        // Create multiple test entities
        List<TrackingRecord> entities = TestDataFactory.createMultipleTrackingRecords(3);
        assertEquals(3, entities.size());
    }
}