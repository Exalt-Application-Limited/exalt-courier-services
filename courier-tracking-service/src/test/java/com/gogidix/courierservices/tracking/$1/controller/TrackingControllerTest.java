package com.gogidix.courierservices.tracking.$1.controller;

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

import com.gogidix.courierservices.tracking.$1.controller.TrackingController;
import com.gogidix.courierservices.tracking.$1.service.TrackingService;
import com.gogidix.courierservices.tracking.$1.model.Tracking;
import com.gogidix.courierservices.tracking.$1.dto.TrackingDTO;

@WebMvcTest(TrackingController.class)
public class TrackingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrackingService service;

    @Autowired
    private ObjectMapper objectMapper;

    private TrackingDTO testDto;
    private Tracking testEntity;

    @BeforeEach
    void setUp() {
        // TODO: Initialize test data
        testDto = new TrackingDTO();
        // Set properties on testDto
        
        testEntity = new Tracking();
        // Set properties on testEntity
    }

    @Test
    void testGetAll() throws Exception {
        List<Tracking> entities = Arrays.asList(testEntity);
        when(service.findAll()).thenReturn(entities);

        mockMvc.perform(get("/api/tracking"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id").exists());
    }

    @Test
    void testGetById() throws Exception {
        when(service.findById(any())).thenReturn(Optional.of(testEntity));

        mockMvc.perform(get("/api/tracking/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void testCreate() throws Exception {
        when(service.save(any())).thenReturn(testEntity);

        mockMvc.perform(post("/api/tracking")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(testDto)))
               .andExpect(status().isCreated());
    }

    @Test
    void testUpdate() throws Exception {
        when(service.findById(any())).thenReturn(Optional.of(testEntity));
        when(service.save(any())).thenReturn(testEntity);

        mockMvc.perform(put("/api/tracking/1")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(testDto)))
               .andExpect(status().isOk());
    }

    @Test
    void testDelete() throws Exception {
        when(service.findById(any())).thenReturn(Optional.of(testEntity));

        mockMvc.perform(delete("/api/tracking/1"))
               .andExpect(status().isNoContent());
    }
}