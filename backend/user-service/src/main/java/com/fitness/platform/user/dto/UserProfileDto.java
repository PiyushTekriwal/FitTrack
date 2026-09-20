package com.fitness.platform.user.dto;
import lombok.Data;
import java.time.LocalDateTime;
@Data
public class UserProfileDto {
    private Long id;
    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private FitnessProfileDto fitnessProfile;
    private LocalDateTime createdAt;
}
