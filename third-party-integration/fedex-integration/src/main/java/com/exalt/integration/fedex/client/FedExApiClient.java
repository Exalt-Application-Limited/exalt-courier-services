import java.util.List;
package com.exalt.integration.fedex.client;

import com.exalt.integration.fedex.config.FedExProperties;
import com.exalt.integration.fedex.model.FedExAddressValidationRequest;
import com.exalt.integration.fedex.model.FedExShipmentRequest;
import com.exalt.integration.fedex.model.FedExShipmentResponse;
import com.exalt.integration.fedex.model.FedExTrackingResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Client for interacting with the FedEx Web Services API.
 * This client handles authentication, request formatting, and response parsing.
 */
@Component
@Slf4j
public class FedExApiClient {

    private final RestTemplate restTemplate;
    private final FedExProperties properties;
    private String authToken;
    private long authTokenExpiry;

    @Autowired
    public FedExApiClient(FedExProperties properties, RestTemplateBuilder restTemplateBuilder) {
        this.properties = properties;
        this.restTemplate = restTemplateBuilder.build();
    }

    /**
     * Create a shipment using the FedEx Ship API.
     *
     * @param request the shipment request
     * @return the shipment response
     * @throws Exception if an error occurs
     */
    public FedExShipmentResponse createShipment(FedExShipmentRequest request) throws Exception {
        log.info("Calling FedEx Ship API to create shipment");
        ensureAuthenticated();

        HttpHeaders headers = createAuthenticatedHeaders();
        HttpEntity<FedExShipmentRequest> requestEntity = new HttpEntity<>(request, headers);

        String url = properties.getApiBaseUrl() + "/ship/v1/shipments";

        try {
            return restTemplate.exchange(url, HttpMethod.POST, requestEntity, FedExShipmentResponse.class).getBody();
        } catch (Exception e) {
            log.error("Error creating shipment with FedEx: {}", e.getMessage(), e);
            throw new Exception("Failed to create shipment with FedEx: " + e.getMessage(), e);
        }
    }

    /**
     * Track a shipment using the FedEx Track API.
     *
     * @param trackingNumber the tracking number
     * @return the tracking response
     * @throws Exception if an error occurs
     */
    public FedExTrackingResponse trackShipment(String trackingNumber) throws Exception {
        log.info("Calling FedEx Track API to track shipment: {}", trackingNumber);
        ensureAuthenticated();

        HttpHeaders headers = createAuthenticatedHeaders();
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("trackingInfo", Collections.singletonList(
                Collections.singletonMap("trackingNumberInfo", 
                        Collections.singletonMap("trackingNumber", trackingNumber))));

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        String url = properties.getApiBaseUrl() + "/track/v1/trackingnumbers";

        try {
            return restTemplate.exchange(url, HttpMethod.POST, requestEntity, FedExTrackingResponse.class).getBody();
        } catch (Exception e) {
            log.error("Error tracking shipment with FedEx: {}", e.getMessage(), e);
            throw new Exception("Failed to track shipment with FedEx: " + e.getMessage(), e);
        }
    }

    /**
     * Cancel a shipment using the FedEx Ship API.
     *
     * @param shipmentId the shipment ID
     * @return true if cancelled successfully
     * @throws Exception if an error occurs
     */
    public boolean cancelShipment(String shipmentId) throws Exception {
        log.info("Calling FedEx Ship API to cancel shipment: {}", shipmentId);
        ensureAuthenticated();

        HttpHeaders headers = createAuthenticatedHeaders();
        Map<String, Object> requestBody = Collections.singletonMap("deletionControl", 
                Collections.singletonMap("deletionKey", shipmentId));

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        String url = properties.getApiBaseUrl() + "/ship/v1/shipments/cancel";

        try {
            Map<String, Object> response = restTemplate.exchange(
                    url, HttpMethod.PUT, requestEntity, Map.class).getBody();

            // Check if the cancellation was successful based on the response
            if (response != null && response.containsKey("output")) {
                Map<String, Object> output = (Map<String, Object>) response.get("output");
                return "SUCCESS".equals(output.get("status"));
            }
            return false;
        } catch (Exception e) {
            log.error("Error cancelling shipment with FedEx: {}", e.getMessage(), e);
            throw new Exception("Failed to cancel shipment with FedEx: " + e.getMessage(), e);
        }
    }

    /**
     * Validate an address using the FedEx Address Validation API.
     *
     * @param request the address validation request
     * @return true if the address is valid
     * @throws Exception if an error occurs
     */
    public boolean validateAddress(FedExAddressValidationRequest request) throws Exception {
        log.info("Calling FedEx Address Validation API");
        ensureAuthenticated();

        HttpHeaders headers = createAuthenticatedHeaders();
        HttpEntity<FedExAddressValidationRequest> requestEntity = new HttpEntity<>(request, headers);

        String url = properties.getApiBaseUrl() + "/address/v1/addresses/validate";

        try {
            Map<String, Object> response = restTemplate.exchange(
                    url, HttpMethod.POST, requestEntity, Map.class).getBody();

            // Check if the address is valid based on the response
            if (response != null && response.containsKey("output")) {
                Map<String, Object> output = (Map<String, Object>) response.get("output");
                if (output.containsKey("resolvedAddresses")) {
                    return true; // Address was resolved, so it's valid
                }
            }
            return false;
        } catch (Exception e) {
            log.error("Error validating address with FedEx: {}", e.getMessage(), e);
            throw new Exception("Failed to validate address with FedEx: " + e.getMessage(), e);
        }
    }

    /**
     * Calculate shipping rates using the FedEx Rate API.
     *
     * @param request the shipment request
     * @return map with rate options
     * @throws Exception if an error occurs
     */
    public Map<String, Object> calculateRates(FedExShipmentRequest request) throws Exception {
        log.info("Calling FedEx Rate API");
        ensureAuthenticated();

        HttpHeaders headers = createAuthenticatedHeaders();
        HttpEntity<FedExShipmentRequest> requestEntity = new HttpEntity<>(request, headers);

        String url = properties.getApiBaseUrl() + "/rate/v1/rates/quotes";

        try {
            return restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class).getBody();
        } catch (Exception e) {
            log.error("Error calculating rates with FedEx: {}", e.getMessage(), e);
            throw new Exception("Failed to calculate rates with FedEx: " + e.getMessage(), e);
        }
    }

    /**
     * Schedule a pickup using the FedEx Pickup API.
     *
     * @param pickupData the pickup data
     * @return map with pickup confirmation details
     * @throws Exception if an error occurs
     */
    public Map<String, Object> schedulePickup(Map<String, Object> pickupData) throws Exception {
        log.info("Calling FedEx Pickup API");
        ensureAuthenticated();

        HttpHeaders headers = createAuthenticatedHeaders();
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(pickupData, headers);

        String url = properties.getApiBaseUrl() + "/pickup/v1/pickups";

        try {
            return restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class).getBody();
        } catch (Exception e) {
            log.error("Error scheduling pickup with FedEx: {}", e.getMessage(), e);
            throw new Exception("Failed to schedule pickup with FedEx: " + e.getMessage(), e);
        }
    }

    /**
     * Check if the FedEx API is available.
     *
     * @return true if the API is available
     */
    public boolean checkHealth() {
        try {
            // Try to authenticate as a simple health check
            authenticate();
            return authToken != null;
        } catch (Exception e) {
            log.error("FedEx API health check failed: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Ensure that we have a valid authentication token.
     *
     * @throws Exception if authentication fails
     */
    private void ensureAuthenticated() throws Exception {
        long now = System.currentTimeMillis();
        if (authToken == null || now >= authTokenExpiry) {
            authenticate();
        }
    }

    /**
     * Authenticate with the FedEx API.
     *
     * @throws Exception if authentication fails
     */
    private void authenticate() throws Exception {
        log.info("Authenticating with FedEx API");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("grant_type", "client_credentials");
        requestBody.put("client_id", properties.getApiKey());
        requestBody.put("client_secret", properties.getApiSecret());

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        String url = properties.getAuthUrl() + "/oauth/token";

        try {
            Map<String, Object> response = restTemplate.exchange(
                    url, HttpMethod.POST, requestEntity, Map.class).getBody();

            if (response != null && response.containsKey("access_token")) {
                authToken = (String) response.get("access_token");
                int expiresIn = (Integer) response.get("expires_in");
                // Set expiry slightly before the actual expiry time
                authTokenExpiry = System.currentTimeMillis() + (expiresIn * 1000L) - 60000L;
                log.info("Successfully authenticated with FedEx API");
            } else {
                log.error("Failed to get access token from FedEx API");
                throw new Exception("Failed to authenticate with FedEx API");
            }
        } catch (Exception e) {
            log.error("Error authenticating with FedEx API: {}", e.getMessage(), e);
            throw new Exception("Failed to authenticate with FedEx API: " + e.getMessage(), e);
        }
    }

    /**
     * Create headers with authentication token.
     *
     * @return the headers
     */
    private HttpHeaders createAuthenticatedHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        return headers;
    }
} 
