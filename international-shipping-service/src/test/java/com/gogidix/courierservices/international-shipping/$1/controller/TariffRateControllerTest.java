package com.gogidix.courierservices.international-shipping.$1.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.gogidix.courierservices.international-shipping.$1.controller.TariffRateController;
import com.gogidix.courierservices.international-shipping.$1.service.TariffRateService;
import com.gogidix.courierservices.international-shipping.$1.model.TariffRate;
import com.gogidix.courierservices.international-shipping.$1.dto.TariffRateDTO;

@WebMvcTest(TariffRateController.class)
public class TariffRateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TariffRateService service;

    @Autowired
    private ObjectMapper objectMapper;

    private TariffRateDTO testDto;
    private TariffRate testEntity;

    @BeforeEach
    void setUp() {
        // TODO: Initialize test data
        testDto = new TariffRateDTO();
        // Set properties on testDto
        
        testEntity = new TariffRate();
        // Set properties on testEntity
    }

    @Test
    void testGetAll() throws Exception {
        List<TariffRate> entities = Arrays.asList(testEntity);
        when(service.findAll()).thenReturn(entities);

        mockMvc.perform(get("/api/tariffrate"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id").exists());
    }

    @Test
    void testGetById() throws Exception {
        when(service.findById(any())).thenReturn(Optional.of(testEntity));

        mockMvc.perform(get("/api/tariffrate/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void testCreate() throws Exception {
        when(service.save(any())).thenReturn(testEntity);

        mockMvc.perform(post("/api/tariffrate")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(testDto)))
               .andExpect(status().isCreated());
    }

    @Test
    void testUpdate() throws Exception {
        when(service.findById(any())).thenReturn(Optional.of(testEntity));
        when(service.save(any())).thenReturn(testEntity);

        mockMvc.perform(put("/api/tariffrate/1")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(testDto)))
               .andExpect(status().isOk());
    }

    @Test
    void testDelete() throws Exception {
        when(service.findById(any())).thenReturn(Optional.of(testEntity));

        mockMvc.perform(delete("/api/tariffrate/1"))
               .andExpect(status().isNoContent());
    }
}