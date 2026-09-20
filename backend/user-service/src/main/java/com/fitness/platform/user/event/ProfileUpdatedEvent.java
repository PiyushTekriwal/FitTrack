package com.fitness.platform.user.event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data @NoArgsConstructor @AllArgsConstructor
public class ProfileUpdatedEvent {
    private String userId;
    private long timestamp;
}
