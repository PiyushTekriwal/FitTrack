package com.fitness.platform.recommendation.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationDto {
    private Long id;
    private String userId;
    private String type;
    private String summary;
    private List<WorkoutRecommendation> workoutRecommendations;
    private List<DietRecommendation> dietRecommendations;
    private List<WeeklyPlanDay> weeklyPlan;
    private String recoveryAdvice;
    private LocalDateTime createdAt;
}
