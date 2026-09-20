package com.fitness.platform.recommendation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProfileDto {
    private Long id;
    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private FitnessProfileDto fitnessProfile;
    private Object createdAt;

    public UserProfileDto(String userId, String firstName, String lastName, FitnessProfileDto fitnessProfile) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fitnessProfile = fitnessProfile;
    }
}
