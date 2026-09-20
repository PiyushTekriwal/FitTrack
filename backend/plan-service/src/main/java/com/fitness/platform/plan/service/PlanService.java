package com.fitness.platform.plan.service;

import com.fitness.platform.plan.dto.*;
import com.fitness.platform.plan.entity.FitnessPlan;
import com.fitness.platform.plan.entity.FitnessPlanItem;
import com.fitness.platform.plan.event.PlanEventPublisher;
import com.fitness.platform.plan.repository.FitnessPlanRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlanService {
    
    private final FitnessPlanRepository planRepository;
    private final PlanEventPublisher eventPublisher;

    public PlanService(FitnessPlanRepository planRepository, PlanEventPublisher eventPublisher) {
        this.planRepository = planRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public FitnessPlanDto createPlan(String userId, CreatePlanRequest request) {
        FitnessPlan plan = new FitnessPlan();
        plan.setUserId(userId);
        plan.setName(request.getName());
        plan.setDescription(request.getDescription());
        plan.setType(request.getType());
        plan.setStartDate(request.getStartDate());
        plan.setEndDate(request.getEndDate());
        plan.setStatus("ACTIVE");
        
        List<FitnessPlanItem> items = new ArrayList<>();
        if (request.getItems() != null) {
            for (CreatePlanItemRequest itemReq : request.getItems()) {
                FitnessPlanItem item = new FitnessPlanItem();
                item.setFitnessPlan(plan);
                item.setDayOfWeek(itemReq.getDayOfWeek());
                item.setExerciseName(itemReq.getExerciseName());
                item.setExerciseType(itemReq.getExerciseType());
                item.setDuration(itemReq.getDuration());
                item.setSets(itemReq.getSets());
                item.setReps(itemReq.getReps());
                item.setNotes(itemReq.getNotes());
                item.setSortOrder(itemReq.getSortOrder());
                item.setCompleted(false);
                items.add(item);
            }
        }
        plan.setItems(items);
        plan.setTotalItems(items.size());
        plan.setCompletedItems(0);
        
        FitnessPlan savedPlan = planRepository.save(plan);
        eventPublisher.publishPlanGenerated(userId, savedPlan.getId());
        
        return mapToDto(savedPlan);
    }

    public FitnessPlanDto getCurrentPlan(String userId) {
        return planRepository.findFirstByUserIdAndStatusOrderByCreatedAtDesc(userId, "ACTIVE")
                .map(this::mapToDto)
                .orElse(null);
    }

    public List<FitnessPlanDto> getUserPlans(String userId) {
        return planRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public FitnessPlanDto getPlanById(Long planId, String userId) {
        return planRepository.findByIdAndUserId(planId, userId)
                .map(this::mapToDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan not found"));
    }

    @Transactional
    public FitnessPlanDto completeItem(Long planId, Long itemId, String userId) {
        FitnessPlan plan = planRepository.findByIdAndUserId(planId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan not found"));
                
        for (FitnessPlanItem item : plan.getItems()) {
            if (item.getId().equals(itemId)) {
                if (!item.getCompleted()) {
                    item.setCompleted(true);
                    item.setCompletedAt(LocalDateTime.now());
                    plan.setCompletedItems(plan.getCompletedItems() + 1);
                }
                break;
            }
        }
        
        FitnessPlan updatedPlan = planRepository.save(plan);
        return mapToDto(updatedPlan);
    }

    @Transactional
    public void archivePlan(Long planId, String userId) {
        FitnessPlan plan = planRepository.findByIdAndUserId(planId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan not found"));
        plan.setStatus("ARCHIVED");
        planRepository.save(plan);
    }
    
    private FitnessPlanDto mapToDto(FitnessPlan plan) {
        FitnessPlanDto dto = new FitnessPlanDto();
        dto.setId(plan.getId());
        dto.setUserId(plan.getUserId());
        dto.setName(plan.getName());
        dto.setDescription(plan.getDescription());
        dto.setType(plan.getType());
        dto.setStatus(plan.getStatus());
        dto.setStartDate(plan.getStartDate());
        dto.setEndDate(plan.getEndDate());
        dto.setTotalItems(plan.getTotalItems());
        dto.setCompletedItems(plan.getCompletedItems());
        dto.setCreatedAt(plan.getCreatedAt());
        
        if (plan.getItems() != null) {
            dto.setItems(plan.getItems().stream().map(item -> {
                FitnessPlanItemDto itemDto = new FitnessPlanItemDto();
                itemDto.setId(item.getId());
                itemDto.setDayOfWeek(item.getDayOfWeek());
                itemDto.setExerciseName(item.getExerciseName());
                itemDto.setExerciseType(item.getExerciseType());
                itemDto.setDuration(item.getDuration());
                itemDto.setSets(item.getSets());
                itemDto.setReps(item.getReps());
                itemDto.setNotes(item.getNotes());
                itemDto.setCompleted(item.getCompleted());
                itemDto.setCompletedAt(item.getCompletedAt());
                itemDto.setSortOrder(item.getSortOrder());
                return itemDto;
            }).collect(Collectors.toList()));
        }
        return dto;
    }
}
