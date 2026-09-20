package com.fitness.platform.user.dto;
import lombok.Data;
@Data
public class UpdateFitnessProfileRequest {
    private Integer age;
    private Double heightCm;
    private Double weightKg;
    private String fitnessLevel;
    private String fitnessGoal;
    private String dietaryPreference;
    private String activityPreferences;
    private String medicalConditions;
}
