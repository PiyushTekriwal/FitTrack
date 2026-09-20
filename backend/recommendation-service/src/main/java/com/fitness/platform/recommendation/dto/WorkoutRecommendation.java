package com.fitness.platform.recommendation.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutRecommendation {
    private String exercise;
    private String duration;
    private String intensity;
    private Integer sets;
    private Integer reps;
    private String notes;
}
