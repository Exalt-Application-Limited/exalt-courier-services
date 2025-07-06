package com.gogidix.courierservices.management.$1.controller;

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

import com.gogidix.courierservices.management.$1.controller.TaskSequencingController;
import com.gogidix.courierservices.management.$1.service.TaskSequencingService;
import com.gogidix.courierservices.management.$1.model.TaskSequencing;
import com.gogidix.courierservices.management.$1.dto.TaskSequencingDTO;

@WebMvcTest(TaskSequencingController.class)
public class TaskSequencingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskSequencingService service;

    @Autowired
    private ObjectMapper objectMapper;

    private TaskSequencingDTO testDto;
    private TaskSequencing testEntity;

    @BeforeEach
    void setUp() {
        // TODO: Initialize test data
        testDto = new TaskSequencingDTO();
        // Set properties on testDto
        
        testEntity = new TaskSequencing();
        // Set properties on testEntity
    }

    @Test
    void testGetAll() throws Exception {
        List<TaskSequencing> entities = Arrays.asList(testEntity);
        when(service.findAll()).thenReturn(entities);

        mockMvc.perform(get("/api/tasksequencing"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id").exists());
    }

    @Test
    void testGetById() throws Exception {
        when(service.findById(any())).thenReturn(Optional.of(testEntity));

        mockMvc.perform(get("/api/tasksequencing/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void testCreate() throws Exception {
        when(service.save(any())).thenReturn(testEntity);

        mockMvc.perform(post("/api/tasksequencing")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(testDto)))
               .andExpect(status().isCreated());
    }

    @Test
    void testUpdate() throws Exception {
        when(service.findById(any())).thenReturn(Optional.of(testEntity));
        when(service.save(any())).thenReturn(testEntity);

        mockMvc.perform(put("/api/tasksequencing/1")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(testDto)))
               .andExpect(status().isOk());
    }

    @Test
    void testDelete() throws Exception {
        when(service.findById(any())).thenReturn(Optional.of(testEntity));

        mockMvc.perform(delete("/api/tasksequencing/1"))
               .andExpect(status().isNoContent());
    }
}