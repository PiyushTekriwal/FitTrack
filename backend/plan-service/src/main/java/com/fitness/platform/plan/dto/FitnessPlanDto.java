package com.fitness.platform.plan.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class FitnessPlanDto {
    private Long id;
    private String userId;
    private String name;
    private String description;
    private String type;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer totalItems;
    private Integer completedItems;
    private LocalDateTime createdAt;
    private List<FitnessPlanItemDto> items;
}
