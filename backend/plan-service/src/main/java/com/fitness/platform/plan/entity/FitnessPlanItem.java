package com.fitness.platform.plan.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "fitness_plan_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FitnessPlanItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "fitness_plan_id")
    private FitnessPlan fitnessPlan;

    private String dayOfWeek;
    private String exerciseName;
    private String exerciseType;
    private Integer duration;
    private Integer sets;
    private Integer reps;
    private String notes;
    private Boolean completed;
    private LocalDateTime completedAt;
    private Integer sortOrder;

    @PrePersist
    protected void onCreate() {
        if (completed == null) completed = false;
    }
}
