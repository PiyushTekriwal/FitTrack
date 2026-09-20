package com.fitness.platform.plan.event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data @NoArgsConstructor @AllArgsConstructor
public class PlanGeneratedEvent {
    private String userId;
    private Long planId;
    private long timestamp;
}
