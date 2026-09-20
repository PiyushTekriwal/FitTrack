package com.fitness.platform.activity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityStatsDto {
    private long totalActivities;
    private long totalCaloriesBurned;
    private long totalDurationMinutes;
    private Map<String, Long> activitiesByType;
    private List<ActivityDto> recentActivities;
    private double averageCaloriesPerWorkout;
    private int currentStreak;
}
