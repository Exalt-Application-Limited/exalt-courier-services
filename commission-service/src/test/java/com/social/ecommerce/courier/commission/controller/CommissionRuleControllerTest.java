package com.gogidix.courierservices.commission.$1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.courier.commission.exception.ResourceNotFoundException;
import com.gogidix.courier.commission.model.CommissionRule;
import com.gogidix.courier.commission.model.PartnerType;
import com.gogidix.courier.commission.model.RateType;
import com.gogidix.courier.commission.service.CommissionRuleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommissionRuleController.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class CommissionRuleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommissionRuleService commissionRuleService;

    @Test
    @DisplayName("GET /api/v1/commission-rules - Should return all commission rules")
    void testGetAllCommissionRules() throws Exception {
        // Arrange
        CommissionRule rule1 = createTestRule(1L, "Rule 1", PartnerType.COURIER);
        CommissionRule rule2 = createTestRule(2L, "Rule 2", PartnerType.VENDOR);
        List<CommissionRule> rules = Arrays.asList(rule1, rule2);
        
        when(commissionRuleService.getAllRules()).thenReturn(rules);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/commission-rules")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Rule 1")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Rule 2")));
        
        verify(commissionRuleService).getAllRules();
    }

    @Test
    @DisplayName("GET /api/v1/commission-rules/{id} - Should return commission rule by ID")
    void testGetCommissionRuleById() throws Exception {
        // Arrange
        CommissionRule rule = createTestRule(1L, "Test Rule", PartnerType.COURIER);
        
        when(commissionRuleService.getRuleById(1L)).thenReturn(Optional.of(rule));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/commission-rules/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Rule")))
                .andExpect(jsonPath("$.partnerType", is("COURIER")))
                .andExpect(jsonPath("$.rateType", is("PERCENTAGE")))
                .andExpect(jsonPath("$.rateValue", is(10.0)))
                .andExpect(jsonPath("$.active", is(true)));
        
        verify(commissionRuleService).getRuleById(1L);
    }

    @Test
    @DisplayName("GET /api/v1/commission-rules/{id} - Should return 404 when commission rule not found")
    void testGetCommissionRuleByIdNotFound() throws Exception {
        // Arrange
        when(commissionRuleService.getRuleById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/commission-rules/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        
        verify(commissionRuleService).getRuleById(999L);
    }

    @Test
    @DisplayName("POST /api/v1/commission-rules - Should create a new commission rule")
    void testCreateCommissionRule() throws Exception {
        // Arrange
        CommissionRule ruleToCreate = createTestRule(null, "New Rule", PartnerType.FRANCHISE);
        CommissionRule createdRule = createTestRule(1L, "New Rule", PartnerType.FRANCHISE);
        
        when(commissionRuleService.createRule(any(CommissionRule.class))).thenReturn(createdRule);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/commission-rules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ruleToCreate)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("New Rule")))
                .andExpect(jsonPath("$.partnerType", is("FRANCHISE")));
        
        verify(commissionRuleService).createRule(any(CommissionRule.class));
    }

    @Test
    @DisplayName("PUT /api/v1/commission-rules/{id} - Should update an existing commission rule")
    void testUpdateCommissionRule() throws Exception {
        // Arrange
        CommissionRule updatedRule = createTestRule(1L, "Updated Rule", PartnerType.COURIER);
        updatedRule.setRateValue(BigDecimal.valueOf(15.0));
        
        when(commissionRuleService.updateRule(eq(1L), any(CommissionRule.class))).thenReturn(updatedRule);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/commission-rules/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedRule)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Updated Rule")))
                .andExpect(jsonPath("$.rateValue", is(15.0)));
        
        verify(commissionRuleService).updateRule(eq(1L), any(CommissionRule.class));
    }

    @Test
    @DisplayName("PUT /api/v1/commission-rules/{id} - Should return 404 when updating non-existent rule")
    void testUpdateCommissionRuleNotFound() throws Exception {
        // Arrange
        CommissionRule updatedRule = createTestRule(999L, "Updated Rule", PartnerType.COURIER);
        
        when(commissionRuleService.updateRule(eq(999L), any(CommissionRule.class)))
                .thenThrow(new ResourceNotFoundException("CommissionRule", "id", 999L));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/commission-rules/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedRule)))
                .andExpect(status().isNotFound());
        
        verify(commissionRuleService).updateRule(eq(999L), any(CommissionRule.class));
    }

    @Test
    @DisplayName("PUT /api/v1/commission-rules/{id}/status - Should update rule status")
    void testUpdateRuleStatus() throws Exception {
        // Arrange
        CommissionRule rule = createTestRule(1L, "Test Rule", PartnerType.COURIER);
        rule.setActive(false);
        
        when(commissionRuleService.updateRuleStatus(1L, false)).thenReturn(rule);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/commission-rules/1/status")
                .param("active", "false")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.active", is(false)));
        
        verify(commissionRuleService).updateRuleStatus(1L, false);
    }

    @Test
    @DisplayName("GET /api/v1/commission-rules/active - Should get active rules for date")
    void testGetActiveRules() throws Exception {
        // Arrange
        CommissionRule rule1 = createTestRule(1L, "Active Rule 1", PartnerType.COURIER);
        CommissionRule rule2 = createTestRule(2L, "Active Rule 2", PartnerType.VENDOR);
        List<CommissionRule> activeRules = Arrays.asList(rule1, rule2);
        
        when(commissionRuleService.getActiveRules(any(LocalDateTime.class))).thenReturn(activeRules);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/commission-rules/active")
                .param("date", "2025-05-14T12:00:00")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Active Rule 1")))
                .andExpect(jsonPath("$[1].name", is("Active Rule 2")));
        
        verify(commissionRuleService).getActiveRules(any(LocalDateTime.class));
    }

    @Test
    @DisplayName("GET /api/v1/commission-rules/applicable - Should find applicable rule")
    void testFindApplicableRule() throws Exception {
        // Arrange
        CommissionRule rule = createTestRule(1L, "Applicable Rule", PartnerType.COURIER);
        
        when(commissionRuleService.findApplicableRule(1L, BigDecimal.valueOf(100.0))).thenReturn(rule);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/commission-rules/applicable")
                .param("partnerId", "1")
                .param("amount", "100.0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Applicable Rule")));
        
        verify(commissionRuleService).findApplicableRule(1L, BigDecimal.valueOf(100.0));
    }

    // Helper method to create test rules
    private CommissionRule createTestRule(Long id, String name, PartnerType partnerType) {
        CommissionRule rule = new CommissionRule();
        rule.setId(id);
        rule.setName(name);
        rule.setDescription("Test commission rule");
        rule.setPartnerType(partnerType);
        rule.setRateType(RateType.PERCENTAGE);
        rule.setRateValue(BigDecimal.valueOf(10.0));
        rule.setMinimumAmount(BigDecimal.valueOf(5.0));
        rule.setMaximumAmount(BigDecimal.valueOf(500.0));
        rule.setStartDate(LocalDateTime.now().minusDays(10));
        rule.setEndDate(LocalDateTime.now().plusDays(10));
        rule.setActive(true);
        return rule;
    }
}

