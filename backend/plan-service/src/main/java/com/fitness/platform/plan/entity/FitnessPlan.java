package com.fitness.platform.plan.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "fitness_plans")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FitnessPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    private String type;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer totalItems;
    private Integer completedItems;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "fitnessPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<FitnessPlanItem> items = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (completedItems == null) completedItems = 0;
        if (status == null) status = "ACTIVE";
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
