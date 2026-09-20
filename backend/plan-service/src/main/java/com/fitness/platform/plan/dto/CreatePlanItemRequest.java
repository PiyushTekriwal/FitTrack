package com.fitness.platform.plan.dto;

import lombok.Data;

@Data
public class CreatePlanItemRequest {
    private String dayOfWeek;
    private String exerciseName;
    private String exerciseType;
    private Integer duration;
    private Integer sets;
    private Integer reps;
    private String notes;
    private Integer sortOrder;
}
