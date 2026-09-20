package com.fitness.platform.recommendation.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.platform.recommendation.client.ActivityServiceClient;
import com.fitness.platform.recommendation.client.UserServiceClient;
import com.fitness.platform.recommendation.dto.*;
import com.fitness.platform.recommendation.model.Recommendation;
import com.fitness.platform.recommendation.repository.RecommendationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RecommendationService {
    private final UserServiceClient userClient;
    private final ActivityServiceClient activityClient;
    private final AiRecommendationService aiService;
    private final AiResponseParser responseParser;
    private final RecommendationRepository repository;
    private final ObjectMapper objectMapper;
    
    public RecommendationService(UserServiceClient userClient, ActivityServiceClient activityClient,
                                 AiRecommendationService aiService,
                                 AiResponseParser responseParser, RecommendationRepository repository,
                                 ObjectMapper objectMapper) {
        this.userClient = userClient;
        this.activityClient = activityClient;
        this.aiService = aiService;
        this.responseParser = responseParser;
        this.repository = repository;
        this.objectMapper = objectMapper;
    }
    
    @CacheEvict(value = "latestRecommendation", key = "#userId")
    public RecommendationDto generateRecommendation(String userId, String type) {
        UserProfileDto userProfile = null;
        FitnessProfileDto fitnessProfile = null;
        try {
            userProfile = userClient.getUserProfile(userId);
            fitnessProfile = userClient.getFitnessProfile(userId);
            if (fitnessProfile == null && userProfile != null && userProfile.getFitnessProfile() != null) {
                fitnessProfile = userProfile.getFitnessProfile();
            }
        } catch (Exception e) {
            log.warn("Could not fetch user profile for {}: {}", userId, e.getMessage());
        }
        
        List<ActivityDto> recentActivities = List.of();
        try {
            recentActivities = activityClient.getRecentActivities(userId, 7);
        } catch (Exception e) {
            log.warn("Could not fetch activities for {}: {}", userId, e.getMessage());
        }
        
        String aiResponse = aiService.generateRecommendation(userProfile, fitnessProfile, recentActivities);
        RecommendationDto parsed = responseParser.parseResponse(aiResponse);
        
        Recommendation entity = new Recommendation();
        entity.setUserId(userId);
        entity.setType(type != null ? type : "COMPREHENSIVE");
        entity.setSummary(parsed.getSummary());
        entity.setWorkoutRecommendations(toJson(parsed.getWorkoutRecommendations()));
        entity.setDietRecommendations(toJson(parsed.getDietRecommendations()));
        entity.setWeeklyPlan(toJson(parsed.getWeeklyPlan()));
        entity.setRecoveryAdvice(parsed.getRecoveryAdvice());
        entity.setRawAiResponse(aiResponse);
        entity.setStatus("COMPLETED");
        entity.setCreatedAt(LocalDateTime.now());
        Recommendation saved = repository.save(entity);
        
        return mapToDto(saved);
    }
    
    @Cacheable(value = "latestRecommendation", key = "#userId")
    public RecommendationDto getLatestRecommendation(String userId) {
        return repository.findFirstByUserIdOrderByCreatedAtDesc(userId)
            .map(this::mapToDto)
            .orElse(null);
    }
    
    public List<RecommendationDto> getUserRecommendations(String userId) {
        return repository.findByUserIdOrderByCreatedAtDesc(userId)
            .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private String toJson(Object obj) {
        try {
            return obj != null ? objectMapper.writeValueAsString(obj) : null;
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize object to JSON", e);
            return null;
        }
    }

    private RecommendationDto mapToDto(Recommendation entity) {
        RecommendationDto dto = new RecommendationDto();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setType(entity.getType());
        dto.setSummary(entity.getSummary());
        dto.setRecoveryAdvice(entity.getRecoveryAdvice());
        dto.setCreatedAt(entity.getCreatedAt());

        try {
            if (entity.getWorkoutRecommendations() != null) {
                dto.setWorkoutRecommendations(objectMapper.readValue(entity.getWorkoutRecommendations(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, WorkoutRecommendation.class)));
            }
            if (entity.getDietRecommendations() != null) {
                dto.setDietRecommendations(objectMapper.readValue(entity.getDietRecommendations(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, DietRecommendation.class)));
            }
            if (entity.getWeeklyPlan() != null) {
                dto.setWeeklyPlan(objectMapper.readValue(entity.getWeeklyPlan(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, WeeklyPlanDay.class)));
            }
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize JSON from database", e);
        }

        return dto;
    }
}
