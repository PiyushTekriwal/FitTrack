package com.fitness.platform.user.event;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProfileEventPublisher {
    @Autowired(required = false)
    private RabbitTemplate rabbitTemplate;

    public ProfileEventPublisher() {
    }

    public void publishProfileUpdated(String userId) {
        if (rabbitTemplate != null) {
            ProfileUpdatedEvent event = new ProfileUpdatedEvent(userId, System.currentTimeMillis());
            rabbitTemplate.convertAndSend("fitness.events", "profile.updated", event);
        }
    }
}
