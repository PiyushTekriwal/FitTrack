package com.fitness.platform.activity.service;

import com.fitness.platform.activity.event.WorkoutCompletedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ActivityEventPublisher {

    public void publishWorkoutCompletedEvent(WorkoutCompletedEvent event) {
        log.info("Workout completed event recorded for user: {}, activity: {}", event.getUserId(), event.getActivityId());
    }
}
