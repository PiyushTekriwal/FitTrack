package com.fitness.platform.recommendation.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "recommendations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recommendation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    private String type;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String workoutRecommendations;

    @Column(columnDefinition = "TEXT")
    private String dietRecommendations;

    @Column(columnDefinition = "TEXT")
    private String weeklyPlan;

    @Column(columnDefinition = "TEXT")
    private String recoveryAdvice;

    @Column(columnDefinition = "TEXT")
    private String rawAiResponse;

    private String status;

    private LocalDateTime createdAt;
}
