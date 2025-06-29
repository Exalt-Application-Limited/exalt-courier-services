package com.exalt.courierservices.regionalcourier.test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.exalt.courierservices.regionalcourier.model.*;

/**
 * Test data factory for regionalcourier domain objects.
 * Use this class in tests to generate consistent test data.
 */
public class TestDataFactory {
    
    /**
     * Creates a test RegionalCourier with random data
     */
    public static RegionalCourier createRegionalCourier() {
        RegionalCourier entity = new RegionalCourier();
                entity.setRegion("North East");
        entity.setActive(true);
        entity.setName("Regional Test Courier");
        entity.setId(1L);
        entity.setMaxDeliveryDistance(50.0);

        return entity;
    }
    
    /**
     * Creates a list of test RegionalCouriers
     */
    public static List<RegionalCourier> createMultipleRegionalCouriers(int count) {
        List<RegionalCourier> entities = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            entities.add(createRegionalCourier());
        }
        return entities;
    }
    
    
}