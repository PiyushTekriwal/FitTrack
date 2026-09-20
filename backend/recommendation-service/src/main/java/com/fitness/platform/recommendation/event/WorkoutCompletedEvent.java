package com.fitness.platform.recommendation.event;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutCompletedEvent {
    private String userId;
    private String activityType;
}
