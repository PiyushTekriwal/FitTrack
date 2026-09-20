package com.fitness.platform.plan.service;

import com.fitness.platform.plan.dto.CreatePlanRequest;
import com.fitness.platform.plan.dto.FitnessPlanDto;
import com.fitness.platform.plan.entity.FitnessPlan;
import com.fitness.platform.plan.event.PlanEventPublisher;
import com.fitness.platform.plan.repository.FitnessPlanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PlanServiceTest {

    @Mock
    private FitnessPlanRepository planRepository;
    
    @Mock
    private PlanEventPublisher eventPublisher;

    @InjectMocks
    private PlanService planService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreatePlan() {
        CreatePlanRequest req = new CreatePlanRequest();
        req.setName("Test Plan");
        
        FitnessPlan plan = new FitnessPlan();
        plan.setId(1L);
        plan.setName("Test Plan");
        
        when(planRepository.save(any())).thenReturn(plan);

        FitnessPlanDto dto = planService.createPlan("user1", req);
        assertEquals("Test Plan", dto.getName());
        verify(eventPublisher).publishPlanGenerated("user1", 1L);
    }
    
    @Test
    void testGetPlanById_NotFound() {
        when(planRepository.findByIdAndUserId(1L, "user1")).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> planService.getPlanById(1L, "user1"));
    }
}
