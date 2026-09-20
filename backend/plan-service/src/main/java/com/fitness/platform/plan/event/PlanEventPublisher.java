package com.fitness.platform.plan.event;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlanEventPublisher {
    @Autowired(required = false)
    private RabbitTemplate rabbitTemplate;

    public PlanEventPublisher() {
    }

    public void publishPlanGenerated(String userId, Long planId) {
        if (rabbitTemplate != null) {
            PlanGeneratedEvent event = new PlanGeneratedEvent(userId, planId, System.currentTimeMillis());
            rabbitTemplate.convertAndSend("fitness.events", "plan.generated", event);
        }
    }
}
