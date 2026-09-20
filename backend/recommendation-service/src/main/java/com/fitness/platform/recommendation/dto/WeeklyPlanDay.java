package com.fitness.platform.recommendation.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyPlanDay {
    private String day;
    private String workout;
    private String diet;
    private Boolean rest;
}
