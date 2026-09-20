package com.fitness.platform.user.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "fitness_profiles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FitnessProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_profile_id")
    private UserProfile userProfile;

    private Integer age;
    private Double heightCm;
    private Double weightKg;
    private String fitnessLevel;
    private String fitnessGoal;
    private String dietaryPreference;
    private String activityPreferences;
    private String medicalConditions;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
