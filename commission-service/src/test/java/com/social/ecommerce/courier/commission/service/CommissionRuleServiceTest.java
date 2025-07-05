package com.gogidix.courierservices.commission.$1;

import com.gogidix.courier.commission.exception.ResourceNotFoundException;
import com.gogidix.courier.commission.model.CommissionRule;
import com.gogidix.courier.commission.model.Partner;
import com.gogidix.courier.commission.model.PartnerType;
import com.gogidix.courier.commission.model.RateType;
import com.gogidix.courier.commission.repository.CommissionRuleRepository;
import com.gogidix.courier.commission.repository.PartnerRepository;
import com.gogidix.courier.commission.service.impl.CommissionRuleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class CommissionRuleServiceTest {

    @Mock
    private CommissionRuleRepository commissionRuleRepository;

    @Mock
    private PartnerRepository partnerRepository;

    @InjectMocks
    private CommissionRuleServiceImpl commissionRuleService;

    private CommissionRule testRule;
    private Partner testPartner;

    @BeforeEach
    void setUp() {
        // Set up test data
        testPartner = new Partner();
        testPartner.setId(1L);
        testPartner.setName("Test Partner");
        testPartner.setType(PartnerType.COURIER);
        testPartner.setActive(true);

        testRule = new CommissionRule();
        testRule.setId(1L);
        testRule.setName("Test Rule");
        testRule.setDescription("Test commission rule");
        testRule.setPartnerType(PartnerType.COURIER);
        testRule.setRateType(RateType.PERCENTAGE);
        testRule.setRateValue(BigDecimal.valueOf(10.0));
        testRule.setMinimumAmount(BigDecimal.valueOf(5.0));
        testRule.setMaximumAmount(BigDecimal.valueOf(500.0));
        testRule.setStartDate(LocalDateTime.now().minusDays(10));
        testRule.setEndDate(LocalDateTime.now().plusDays(10));
        testRule.setActive(true);
    }

    @Test
    @DisplayName("Should find active commission rule for partner and amount")
    void testFindApplicableRule() {
        // Arrange
        when(partnerRepository.findById(anyLong())).thenReturn(Optional.of(testPartner));
        when(commissionRuleRepository.findActiveRuleForPartnerType(
                any(PartnerType.class), 
                any(LocalDateTime.class), 
                any(BigDecimal.class))
        ).thenReturn(Optional.of(testRule));

        // Act
        CommissionRule result = commissionRuleService.findApplicableRule(1L, BigDecimal.valueOf(100.0));

        // Assert
        assertNotNull(result);
        assertEquals(testRule.getId(), result.getId());
        assertEquals(testRule.getRateValue(), result.getRateValue());
        
        verify(partnerRepository).findById(1L);
        verify(commissionRuleRepository).findActiveRuleForPartnerType(
                eq(PartnerType.COURIER), 
                any(LocalDateTime.class), 
                eq(BigDecimal.valueOf(100.0))
        );
    }

    @Test
    @DisplayName("Should throw exception when no partner found for applicable rule")
    void testFindApplicableRulePartnerNotFound() {
        // Arrange
        when(partnerRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            commissionRuleService.findApplicableRule(999L, BigDecimal.valueOf(100.0));
        });
        
        verify(partnerRepository).findById(999L);
        verify(commissionRuleRepository, never()).findActiveRuleForPartnerType(
                any(PartnerType.class), 
                any(LocalDateTime.class), 
                any(BigDecimal.class)
        );
    }

    @Test
    @DisplayName("Should throw exception when no applicable rule found")
    void testFindApplicableRuleNoRuleFound() {
        // Arrange
        when(partnerRepository.findById(anyLong())).thenReturn(Optional.of(testPartner));
        when(commissionRuleRepository.findActiveRuleForPartnerType(
                any(PartnerType.class), 
                any(LocalDateTime.class), 
                any(BigDecimal.class))
        ).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            commissionRuleService.findApplicableRule(1L, BigDecimal.valueOf(1000.0));
        });
        
        verify(partnerRepository).findById(1L);
        verify(commissionRuleRepository).findActiveRuleForPartnerType(
                eq(PartnerType.COURIER), 
                any(LocalDateTime.class), 
                eq(BigDecimal.valueOf(1000.0))
        );
    }

    @Test
    @DisplayName("Should find all active rules for specific date")
    void testGetActiveRules() {
        // Arrange
        LocalDateTime testDate = LocalDateTime.now();
        List<CommissionRule> expectedRules = Arrays.asList(testRule);
        
        when(commissionRuleRepository.findActiveRulesForDate(any(LocalDateTime.class)))
                .thenReturn(expectedRules);

        // Act
        List<CommissionRule> result = commissionRuleService.getActiveRules(testDate);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testRule.getId(), result.get(0).getId());
        
        verify(commissionRuleRepository).findActiveRulesForDate(testDate);
    }

    @Test
    @DisplayName("Should calculate commission amount correctly with percentage rate")
    void testCalculateCommissionAmountPercentage() {
        // Arrange - rule is already set up with 10% rate in setUp()
        
        // Act - For $100 order with 10% rate
        BigDecimal result = commissionRuleService.calculateCommissionAmount(testRule, BigDecimal.valueOf(100.0));
        
        // Assert - Should be $10
        assertEquals(BigDecimal.valueOf(10.0), result);
    }

    @Test
    @DisplayName("Should calculate commission amount correctly with fixed rate")
    void testCalculateCommissionAmountFixed() {
        // Arrange
        testRule.setRateType(RateType.FIXED);
        testRule.setRateValue(BigDecimal.valueOf(15.0));
        
        // Act - For any order with $15 fixed rate
        BigDecimal result = commissionRuleService.calculateCommissionAmount(testRule, BigDecimal.valueOf(100.0));
        
        // Assert - Should be $15
        assertEquals(BigDecimal.valueOf(15.0), result);
    }

    @Test
    @DisplayName("Should apply minimum amount when calculated amount is lower")
    void testCalculateCommissionAmountWithMinimum() {
        // Arrange
        testRule.setRateType(RateType.PERCENTAGE);
        testRule.setRateValue(BigDecimal.valueOf(1.0)); // 1%
        testRule.setMinimumAmount(BigDecimal.valueOf(5.0));
        
        // Act - For $100 order with 1% rate (would be $1, but minimum is $5)
        BigDecimal result = commissionRuleService.calculateCommissionAmount(testRule, BigDecimal.valueOf(100.0));
        
        // Assert - Should be $5 (minimum amount)
        assertEquals(BigDecimal.valueOf(5.0), result);
    }

    @Test
    @DisplayName("Should apply maximum amount when calculated amount is higher")
    void testCalculateCommissionAmountWithMaximum() {
        // Arrange
        testRule.setRateType(RateType.PERCENTAGE);
        testRule.setRateValue(BigDecimal.valueOf(50.0)); // 50%
        testRule.setMaximumAmount(BigDecimal.valueOf(100.0));
        
        // Act - For $500 order with 50% rate (would be $250, but maximum is $100)
        BigDecimal result = commissionRuleService.calculateCommissionAmount(testRule, BigDecimal.valueOf(500.0));
        
        // Assert - Should be $100 (maximum amount)
        assertEquals(BigDecimal.valueOf(100.0), result);
    }
}

