package com.gogidix.courierservices.commission.$1.service;

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

import com.gogidix.courierservices.commission.$1.repository.PartnerRepository;
import com.gogidix.courierservices.commission.$1.model.Partner;

@ExtendWith(MockitoExtension.class)
public class PartnerServiceTest {

    @Mock
    private PartnerRepository repository;

    @InjectMocks
    private PartnerService service;

    private Partner testEntity;

    @BeforeEach
    void setUp() {
        // TODO: Initialize test data
        testEntity = new Partner();
        // Set properties on testEntity
    }

    @Test
    void testFindAll() {
        when(repository.findAll()).thenReturn(Arrays.asList(testEntity));

        List<Partner> result = service.findAll();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void testFindById() {
        when(repository.findById(any())).thenReturn(Optional.of(testEntity));

        Optional<Partner> result = service.findById(1L);

        assertTrue(result.isPresent());
    }

    @Test
    void testSave() {
        when(repository.save(any())).thenReturn(testEntity);

        Partner result = service.save(testEntity);

        assertNotNull(result);
        verify(repository, times(1)).save(testEntity);
    }

    @Test
    void testDelete() {
        service.deleteById(1L);

        verify(repository, times(1)).deleteById(1L);
    }
}