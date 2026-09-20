package com.fitness.platform.recommendation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityDto {
    private String activityType;
    private String exerciseName;
    private Integer durationMinutes;
    private Integer caloriesBurned;
    private String intensity;
    private Object startTime;
}
