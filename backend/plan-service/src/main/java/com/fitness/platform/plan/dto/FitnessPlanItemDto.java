package com.fitness.platform.plan.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FitnessPlanItemDto {
    private Long id;
    private String dayOfWeek;
    private String exerciseName;
    private String exerciseType;
    private Integer duration;
    private Integer sets;
    private Integer reps;
    private String notes;
    private Boolean completed;
    private LocalDateTime completedAt;
    private Integer sortOrder;
}
