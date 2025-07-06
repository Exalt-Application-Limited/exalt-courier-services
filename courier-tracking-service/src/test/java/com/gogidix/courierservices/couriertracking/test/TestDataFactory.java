package com.gogidix.courierservices.couriertracking.test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.gogidix.courierservices.couriertracking.model.*;

/**
 * Test data factory for couriertracking domain objects.
 * Use this class in tests to generate consistent test data.
 */
public class TestDataFactory {
    
    /**
     * Creates a test TrackingRecord with random data
     */
    public static TrackingRecord createTrackingRecord() {
        TrackingRecord entity = new TrackingRecord();
                entity.setId(1L);
        entity.setOrderId(100L);
        entity.setLatitude(40.7128);
        entity.setCourierId(1L);
        entity.setTimestamp(LocalDateTime.now());
        entity.setStatus(DeliveryStatus.IN_PROGRESS);
        entity.setLongitude(-74.0060);

        return entity;
    }
    
    /**
     * Creates a list of test TrackingRecords
     */
    public static List<TrackingRecord> createMultipleTrackingRecords(int count) {
        List<TrackingRecord> entities = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            entities.add(createTrackingRecord());
        }
        return entities;
    }
    
    
}