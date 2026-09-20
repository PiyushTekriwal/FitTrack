package com.fitness.platform.activity.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateActivityRequest {
    @NotBlank(message = "Activity type is required")
    private String activityType;
    
    @NotBlank(message = "Exercise name is required")
    private String exerciseName;
    
    private String description;
    
    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    private Integer durationMinutes;
    
    @NotNull(message = "Calories burned is required")
    @Min(value = 0, message = "Calories cannot be negative")
    private Integer caloriesBurned;
    
    @NotBlank(message = "Intensity is required")
    private String intensity;
    
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Map<String, Object> metrics;
    private String notes;
}
