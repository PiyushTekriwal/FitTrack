package com.fitness.platform.recommendation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FitnessProfileDto {
    private Integer age;
    private Double heightCm;
    private Double weightKg;
    private String fitnessLevel;
    private String fitnessGoal;
    private String dietaryPreference;
    private Object activityPreferences;
}
