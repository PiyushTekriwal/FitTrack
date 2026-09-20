package com.fitness.platform.activity.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutCompletedEvent {
    private String userId;
    private String activityId;
    private String activityType;
    private Integer caloriesBurned;
    private LocalDateTime timestamp;
}
