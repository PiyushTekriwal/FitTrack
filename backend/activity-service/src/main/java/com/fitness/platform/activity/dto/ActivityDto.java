package com.fitness.platform.activity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityDto {
    private String id;
    private String userId;
    private String activityType;
    private String exerciseName;
    private String description;
    private Integer durationMinutes;
    private Integer caloriesBurned;
    private String intensity;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Map<String, Object> metrics;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
