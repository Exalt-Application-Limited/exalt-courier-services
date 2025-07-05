package com.gogidix.courier.regionaladmin.service;

import com.socialecommerceecosystem.regionaladmin.model.Policy;
import com.socialecommerceecosystem.regionaladmin.model.PolicyStatus;
import com.socialecommerceecosystem.regionaladmin.repository.PolicyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PolicySyncServiceTest {

    @Mock
    private PolicyRepository policyRepository;
    
    @Captor
    private ArgumentCaptor<Policy> policyCaptor;
    
    private PolicySyncService policySyncService;

    @BeforeEach
    void setUp() {
        policySyncService = new PolicySyncService();
        // Use reflection to set the autowired field
        try {
            java.lang.reflect.Field field = PolicySyncService.class.getDeclaredField("policyRepository");
            field.setAccessible(true);
            field.set(policySyncService, policyRepository);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set mock repository", e);
        }
    }

    @Test
    void handlePolicyCreated_NewPolicy_SavedSuccessfully() {
        // Arrange
        Map<String, Object> policyData = new HashMap<>();
        policyData.put("id", 1L);
        policyData.put("policyType", "DELIVERY_SLA");
        policyData.put("description", "Test Policy");
        policyData.put("content", "Policy content");
        policyData.put("status", "ACTIVE");
        policyData.put("priority", 5);
        
        when(policyRepository.findByPolicyId(1L)).thenReturn(Optional.empty());
        when(policyRepository.save(any(Policy.class))).thenAnswer(i -> i.getArgument(0));
        
        // Act
        policySyncService.handlePolicyCreated(policyData);
        
        // Assert
        verify(policyRepository).save(policyCaptor.capture());
        Policy savedPolicy = policyCaptor.getValue();
        
        assertEquals(1L, savedPolicy.getPolicyId());
        assertEquals("DELIVERY_SLA", savedPolicy.getPolicyType());
        assertEquals("Test Policy", savedPolicy.getDescription());
        assertEquals("Policy content", savedPolicy.getContent());
        assertEquals(PolicyStatus.ACTIVE, savedPolicy.getStatus());
        assertEquals(5, savedPolicy.getPriority());
    }

    @Test
    void handlePolicyCreated_ExistingPolicy_UpdatedSuccessfully() {
        // Arrange
        Map<String, Object> policyData = new HashMap<>();
        policyData.put("id", 1L);
        policyData.put("policyType", "DELIVERY_SLA");
        policyData.put("description", "Updated Policy");
        policyData.put("content", "Updated content");
        
        Policy existingPolicy = new Policy();
        existingPolicy.setPolicyId(1L);
        existingPolicy.setPolicyType("DELIVERY_SLA");
        existingPolicy.setDescription("Original Policy");
        existingPolicy.setContent("Original content");
        
        when(policyRepository.findByPolicyId(1L)).thenReturn(Optional.of(existingPolicy));
        when(policyRepository.save(any(Policy.class))).thenAnswer(i -> i.getArgument(0));
        
        // Act
        policySyncService.handlePolicyCreated(policyData);
        
        // Assert
        verify(policyRepository).save(policyCaptor.capture());
        Policy savedPolicy = policyCaptor.getValue();
        
        assertEquals(1L, savedPolicy.getPolicyId());
        assertEquals("DELIVERY_SLA", savedPolicy.getPolicyType());
        assertEquals("Updated Policy", savedPolicy.getDescription());
        assertEquals("Updated content", savedPolicy.getContent());
    }

    @Test
    void handlePolicyUpdated_ExistingPolicy_UpdatedSuccessfully() {
        // Arrange
        Map<String, Object> policyData = new HashMap<>();
        policyData.put("id", 1L);
        policyData.put("policyType", "DELIVERY_SLA");
        policyData.put("description", "Updated Policy");
        policyData.put("content", "Updated content");
        
        Policy existingPolicy = new Policy();
        existingPolicy.setPolicyId(1L);
        existingPolicy.setPolicyType("DELIVERY_SLA");
        existingPolicy.setDescription("Original Policy");
        existingPolicy.setContent("Original content");
        
        when(policyRepository.findByPolicyId(1L)).thenReturn(Optional.of(existingPolicy));
        when(policyRepository.save(any(Policy.class))).thenAnswer(i -> i.getArgument(0));
        
        // Act
        policySyncService.handlePolicyUpdated(policyData);
        
        // Assert
        verify(policyRepository).save(policyCaptor.capture());
        Policy savedPolicy = policyCaptor.getValue();
        
        assertEquals(1L, savedPolicy.getPolicyId());
        assertEquals("DELIVERY_SLA", savedPolicy.getPolicyType());
        assertEquals("Updated Policy", savedPolicy.getDescription());
        assertEquals("Updated content", savedPolicy.getContent());
    }

    @Test
    void handlePolicyUpdated_NonExistingPolicy_CreatedSuccessfully() {
        // Arrange
        Map<String, Object> policyData = new HashMap<>();
        policyData.put("id", 1L);
        policyData.put("policyType", "DELIVERY_SLA");
        policyData.put("description", "New Policy");
        policyData.put("content", "Policy content");
        
        when(policyRepository.findByPolicyId(1L)).thenReturn(Optional.empty());
        when(policyRepository.save(any(Policy.class))).thenAnswer(i -> i.getArgument(0));
        
        // Act
        policySyncService.handlePolicyUpdated(policyData);
        
        // Assert
        verify(policyRepository).save(policyCaptor.capture());
        Policy savedPolicy = policyCaptor.getValue();
        
        assertEquals(1L, savedPolicy.getPolicyId());
        assertEquals("DELIVERY_SLA", savedPolicy.getPolicyType());
        assertEquals("New Policy", savedPolicy.getDescription());
        assertEquals("Policy content", savedPolicy.getContent());
    }

    @Test
    void handlePolicyDeleted_ExistingPolicy_DeletedSuccessfully() {
        // Arrange
        Map<String, Object> policyData = new HashMap<>();
        policyData.put("id", 1L);
        
        Policy existingPolicy = new Policy();
        existingPolicy.setId(1L);
        existingPolicy.setPolicyId(1L);
        
        when(policyRepository.findByPolicyId(1L)).thenReturn(Optional.of(existingPolicy));
        
        // Act
        policySyncService.handlePolicyDeleted(policyData);
        
        // Assert
        verify(policyRepository).delete(existingPolicy);
    }

    @Test
    void handlePolicyDeleted_NonExistingPolicy_NoAction() {
        // Arrange
        Map<String, Object> policyData = new HashMap<>();
        policyData.put("id", 1L);
        
        when(policyRepository.findByPolicyId(1L)).thenReturn(Optional.empty());
        
        // Act
        policySyncService.handlePolicyDeleted(policyData);
        
        // Assert
        verify(policyRepository, never()).delete(any(Policy.class));
    }

    @Test
    void handlePolicyActivated_ExistingPolicy_ActivatedSuccessfully() {
        // Arrange
        Map<String, Object> policyData = new HashMap<>();
        policyData.put("id", 1L);
        
        Policy existingPolicy = new Policy();
        existingPolicy.setPolicyId(1L);
        existingPolicy.setStatus(PolicyStatus.INACTIVE);
        
        when(policyRepository.findByPolicyId(1L)).thenReturn(Optional.of(existingPolicy));
        when(policyRepository.save(any(Policy.class))).thenAnswer(i -> i.getArgument(0));
        
        // Act
        policySyncService.handlePolicyActivated(policyData);
        
        // Assert
        verify(policyRepository).save(policyCaptor.capture());
        Policy savedPolicy = policyCaptor.getValue();
        
        assertEquals(PolicyStatus.ACTIVE, savedPolicy.getStatus());
    }

    @Test
    void handlePolicyDeactivated_ExistingPolicy_DeactivatedSuccessfully() {
        // Arrange
        Map<String, Object> policyData = new HashMap<>();
        policyData.put("id", 1L);
        
        Policy existingPolicy = new Policy();
        existingPolicy.setPolicyId(1L);
        existingPolicy.setStatus(PolicyStatus.ACTIVE);
        
        when(policyRepository.findByPolicyId(1L)).thenReturn(Optional.of(existingPolicy));
        when(policyRepository.save(any(Policy.class))).thenAnswer(i -> i.getArgument(0));
        
        // Act
        policySyncService.handlePolicyDeactivated(policyData);
        
        // Assert
        verify(policyRepository).save(policyCaptor.capture());
        Policy savedPolicy = policyCaptor.getValue();
        
        assertEquals(PolicyStatus.INACTIVE, savedPolicy.getStatus());
    }
}
