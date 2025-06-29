package com.microecosystem.courier.driver.app.controller.api;

import com.microecosystem.courier.driver.app.service.navigation.NavigationService;
import com.microecosystem.courier.driver.app.service.security.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NavigationController.class)
public class NavigationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NavigationService navigationService;

    @MockBean
    private SecurityService securityService;

    private Map<String, Object> mockDirectionsResponse;
    private Map<String, Object> mockRouteResponse;
    private Map<String, Object> mockPrefetchResponse;
    private Map<String, Object> mockGeocodeResponse;
    private Map<String, Object> mockReverseGeocodeResponse;

    @BeforeEach
    void setUp() {
        // Setup mock responses
        mockDirectionsResponse = new HashMap<>();
        mockDirectionsResponse.put("distance", 5.2);
        mockDirectionsResponse.put("duration", 15);
        mockDirectionsResponse.put("trafficUsed", true);
        
        Map<String, Object> startPoint = new HashMap<>();
        startPoint.put("lat", 37.7749);
        startPoint.put("lng", -122.4194);
        startPoint.put("address", "123 Main St");
        mockDirectionsResponse.put("startPoint", startPoint);

        Map<String, Object> endPoint = new HashMap<>();
        endPoint.put("lat", 37.7833);
        endPoint.put("lng", -122.4167);
        endPoint.put("address", "456 Market St");
        mockDirectionsResponse.put("endPoint", endPoint);

        mockRouteResponse = new HashMap<>();
        mockRouteResponse.put("totalDistance", 12.5);
        mockRouteResponse.put("totalDuration", 35);
        mockRouteResponse.put("waypointOrder", List.of(2, 0, 1, 3));
        mockRouteResponse.put("legs", new ArrayList<>());

        mockPrefetchResponse = new HashMap<>();
        mockPrefetchResponse.put("center", Map.of("lat", 37.7749, "lng", -122.4194));
        mockPrefetchResponse.put("radius", 5);
        mockPrefetchResponse.put("dataSizeKB", 2500);
        mockPrefetchResponse.put("tileCount", 100);
        mockPrefetchResponse.put("success", true);

        mockGeocodeResponse = new HashMap<>();
        mockGeocodeResponse.put("address", "123 Main St, San Francisco, CA 94105");
        mockGeocodeResponse.put("lat", 37.7749);
        mockGeocodeResponse.put("lng", -122.4194);
        mockGeocodeResponse.put("accuracy", "ROOFTOP");

        mockReverseGeocodeResponse = new HashMap<>();
        mockReverseGeocodeResponse.put("address", "123 Main St, San Francisco, CA 94105");
        mockReverseGeocodeResponse.put("formattedAddress", "123 Main St, San Francisco, CA 94105, USA");
        mockReverseGeocodeResponse.put("components", Map.of(
            "street", "Main St",
            "streetNumber", "123",
            "city", "San Francisco",
            "state", "CA",
            "postalCode", "94105"
        ));

        // Setup navigation service mocks
        when(navigationService.getDirections(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
            .thenReturn(mockDirectionsResponse);
        when(navigationService.getOptimizedRoute(anyDouble(), anyDouble(), anyList()))
            .thenReturn(mockRouteResponse);
        when(navigationService.prefetchMapData(anyDouble(), anyDouble(), anyInt()))
            .thenReturn(mockPrefetchResponse);
        when(navigationService.geocodeAddress(anyString()))
            .thenReturn(mockGeocodeResponse);
        when(navigationService.reverseGeocode(anyDouble(), anyDouble()))
            .thenReturn(mockReverseGeocodeResponse);
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    void getDirections_ShouldReturnDirectionsData() throws Exception {
        String requestBody = "{\"startLat\": 37.7749, \"startLng\": -122.4194, \"endLat\": 37.7833, \"endLng\": -122.4167}";

        mockMvc.perform(post("/api/v1/navigation/directions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.distance").value(5.2))
                .andExpect(jsonPath("$.duration").value(15))
                .andExpect(jsonPath("$.trafficUsed").value(true))
                .andExpect(jsonPath("$.startPoint").exists())
                .andExpect(jsonPath("$.endPoint").exists());
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    void getOptimizedRoute_ShouldReturnRouteData() throws Exception {
        String requestBody = "{\n" +
                "    \"startLat\": 37.7749,\n" +
                "    \"startLng\": -122.4194,\n" +
                "    \"waypoints\": [\n" +
                "        {\"lat\": 37.7833, \"lng\": -122.4167},\n" +
                "        {\"lat\": 37.7913, \"lng\": -122.4089},\n" +
                "        {\"lat\": 37.7835, \"lng\": -122.4112},\n" +
                "        {\"lat\": 37.7869, \"lng\": -122.4081}\n" +
                "    ]\n" +
                "}";

        mockMvc.perform(post("/api/v1/navigation/route/optimize")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalDistance").value(12.5))
                .andExpect(jsonPath("$.totalDuration").value(35))
                .andExpect(jsonPath("$.waypointOrder").isArray())
                .andExpect(jsonPath("$.legs").isArray());
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    void prefetchMapData_ShouldReturnPrefetchResult() throws Exception {
        String requestBody = "{\"centerLat\": 37.7749, \"centerLng\": -122.4194, \"radiusKm\": 5}";

        mockMvc.perform(post("/api/v1/navigation/map/prefetch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.center").exists())
                .andExpect(jsonPath("$.radius").value(5))
                .andExpect(jsonPath("$.dataSizeKB").exists())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    void geocodeAddress_ShouldReturnGeocodeData() throws Exception {
        String requestBody = "{\"address\": \"123 Main St, San Francisco, CA\"}";

        mockMvc.perform(post("/api/v1/navigation/geocode")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.address").exists())
                .andExpect(jsonPath("$.lat").exists())
                .andExpect(jsonPath("$.lng").exists());
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    void reverseGeocode_ShouldReturnAddressData() throws Exception {
        String requestBody = "{\"lat\": 37.7749, \"lng\": -122.4194}";

        mockMvc.perform(post("/api/v1/navigation/reverse-geocode")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.address").exists())
                .andExpect(jsonPath("$.formattedAddress").exists())
                .andExpect(jsonPath("$.components").exists());
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    void getDirectionsWithError_ShouldReturnBadRequest() throws Exception {
        // Setup error response
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", true);
        errorResponse.put("message", "Invalid coordinates");
        when(navigationService.getDirections(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
            .thenReturn(errorResponse);

        String requestBody = "{\"startLat\": 999.0, \"startLng\": 999.0, \"endLat\": 999.0, \"endLng\": 999.0}";

        mockMvc.perform(post("/api/v1/navigation/directions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(true));
    }
}
