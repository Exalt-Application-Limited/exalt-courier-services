package com.microecosystem.courier.driver.app.integration;

import com.microecosystem.courier.driver.app.dto.routing.NavigationInstructionDTO;
import com.microecosystem.courier.driver.app.dto.routing.RouteDTO;
import com.microecosystem.courier.driver.app.dto.routing.WaypointDTO;
import com.microecosystem.courier.driver.app.service.navigation.NavigationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for NavigationController API endpoints.
 * 
 * @author Courier Services Migration Team
 * @version 1.0
 * @since 2025-05-25
 */
@DisplayName("Navigation Controller Integration Tests")
class NavigationControllerIntegrationTest extends BaseIntegrationTest {

    @MockBean
    private NavigationService navigationService;

    @Test
    @WithMockUser(roles = "DRIVER")
    @DisplayName("Should get directions between two points")
    void shouldGetDirections() throws Exception {
        // Given
        RouteDTO mockRoute = createMockRoute();
        when(navigationService.getDirections(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(mockRoute);

        String requestBody = """
                {
                    "originLat": 40.7128,
                    "originLng": -74.0060,
                    "destinationLat": 40.7589,
                    "destinationLng": -73.9851
                }
                """;

        // When & Then
        mockMvc.perform(post("/api/v1/navigation/directions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.distance").value(mockRoute.getDistance()))
                .andExpect(jsonPath("$.duration").value(mockRoute.getDuration()))
                .andExpect(jsonPath("$.instructions").isArray())
                .andExpect(jsonPath("$.instructions[0].instruction").exists());
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    @DisplayName("Should optimize route for multiple waypoints")
    void shouldOptimizeRoute() throws Exception {
        // Given
        RouteDTO mockRoute = createMockRoute();
        when(navigationService.optimizeRoute(any()))
                .thenReturn(mockRoute);

        String requestBody = """
                {
                    "waypoints": [
                        {"lat": 40.7128, "lng": -74.0060, "address": "New York, NY"},
                        {"lat": 40.7589, "lng": -73.9851, "address": "Times Square, NY"},
                        {"lat": 40.7831, "lng": -73.9712, "address": "Central Park, NY"}
                    ]
                }
                """;

        // When & Then
        mockMvc.perform(post("/api/v1/navigation/route/optimize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.distance").value(mockRoute.getDistance()))
                .andExpect(jsonPath("$.duration").value(mockRoute.getDuration()))
                .andExpect(jsonPath("$.waypoints").isArray());
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    @DisplayName("Should prefetch map data for offline use")
    void shouldPrefetchMapData() throws Exception {
        // Given
        when(navigationService.prefetchMapData(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(true);

        String requestBody = """
                {
                    "boundingBox": {
                        "northLat": 40.8,
                        "southLat": 40.7,
                        "eastLng": -73.9,
                        "westLng": -74.1
                    },
                    "zoomLevel": 15
                }
                """;

        // When & Then
        mockMvc.perform(post("/api/v1/navigation/map/prefetch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    @DisplayName("Should geocode address to coordinates")
    void shouldGeocodeAddress() throws Exception {
        // Given
        when(navigationService.geocodeAddress(anyString()))
                .thenReturn(Arrays.asList(40.7128, -74.0060));

        String requestBody = """
                {
                    "address": "New York, NY, USA"
                }
                """;

        // When & Then
        mockMvc.perform(post("/api/v1/navigation/geocode")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.coordinates").isArray())
                .andExpect(jsonPath("$.coordinates[0]").value(40.7128))
                .andExpect(jsonPath("$.coordinates[1]").value(-74.0060));
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    @DisplayName("Should reverse geocode coordinates to address")
    void shouldReverseGeocode() throws Exception {
        // Given
        when(navigationService.reverseGeocode(anyDouble(), anyDouble()))
                .thenReturn("New York, NY, USA");

        String requestBody = """
                {
                    "lat": 40.7128,
                    "lng": -74.0060
                }
                """;

        // When & Then
        mockMvc.perform(post("/api/v1/navigation/reverse-geocode")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.address").value("New York, NY, USA"));
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    @DisplayName("Should handle invalid coordinates gracefully")
    void shouldHandleInvalidCoordinates() throws Exception {
        // Given
        String requestBody = """
                {
                    "originLat": 200.0,
                    "originLng": 300.0,
                    "destinationLat": 40.7589,
                    "destinationLng": -73.9851
                }
                """;

        // When & Then
        mockMvc.perform(post("/api/v1/navigation/directions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should require authentication for navigation endpoints")
    void shouldRequireAuthentication() throws Exception {
        mockMvc.perform(post("/api/v1/navigation/directions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Helper method to create mock route data
     */
    private RouteDTO createMockRoute() {
        RouteDTO route = new RouteDTO();
        route.setDistance(5.2);
        route.setDuration(900); // 15 minutes
        
        // Create navigation instructions
        NavigationInstructionDTO instruction1 = new NavigationInstructionDTO();
        instruction1.setInstruction("Head north on Broadway");
        instruction1.setDistance(1.2);
        instruction1.setDuration(180);
        
        NavigationInstructionDTO instruction2 = new NavigationInstructionDTO();
        instruction2.setInstruction("Turn right on Times Square");
        instruction2.setDistance(4.0);
        instruction2.setDuration(720);
        
        List<NavigationInstructionDTO> instructions = Arrays.asList(instruction1, instruction2);
        route.setInstructions(instructions);
        
        // Create waypoints
        WaypointDTO waypoint1 = new WaypointDTO();
        waypoint1.setLat(40.7128);
        waypoint1.setLng(-74.0060);
        waypoint1.setAddress("New York, NY");
        
        WaypointDTO waypoint2 = new WaypointDTO();
        waypoint2.setLat(40.7589);
        waypoint2.setLng(-73.9851);
        waypoint2.setAddress("Times Square, NY");
        
        List<WaypointDTO> waypoints = Arrays.asList(waypoint1, waypoint2);
        route.setWaypoints(waypoints);
        
        return route;
    }
}
