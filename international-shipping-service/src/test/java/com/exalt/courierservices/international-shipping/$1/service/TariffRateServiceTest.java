package com.exalt.courierservices.international-shipping.$1.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exalt.courierservices.international-shipping.$1.repository.TariffRateRepository;
import com.exalt.courierservices.international-shipping.$1.model.TariffRate;

@ExtendWith(MockitoExtension.class)
public class TariffRateServiceTest {

    @Mock
    private TariffRateRepository repository;

    @InjectMocks
    private TariffRateService service;

    private TariffRate testEntity;

    @BeforeEach
    void setUp() {
        // TODO: Initialize test data
        testEntity = new TariffRate();
        // Set properties on testEntity
    }

    @Test
    void testFindAll() {
        when(repository.findAll()).thenReturn(Arrays.asList(testEntity));

        List<TariffRate> result = service.findAll();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void testFindById() {
        when(repository.findById(any())).thenReturn(Optional.of(testEntity));

        Optional<TariffRate> result = service.findById(1L);

        assertTrue(result.isPresent());
    }

    @Test
    void testSave() {
        when(repository.save(any())).thenReturn(testEntity);

        TariffRate result = service.save(testEntity);

        assertNotNull(result);
        verify(repository, times(1)).save(testEntity);
    }

    @Test
    void testDelete() {
        service.deleteById(1L);

        verify(repository, times(1)).deleteById(1L);
    }
}