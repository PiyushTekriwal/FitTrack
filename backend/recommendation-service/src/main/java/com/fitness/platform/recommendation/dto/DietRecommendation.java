package com.fitness.platform.recommendation.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DietRecommendation {
    private String meal;
    private String description;
    private Integer calories;
    private String protein;
    private String notes;
}
