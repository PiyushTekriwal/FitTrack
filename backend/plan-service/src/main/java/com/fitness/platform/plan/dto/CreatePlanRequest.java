package com.fitness.platform.plan.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class CreatePlanRequest {
    private String name;
    private String description;
    private String type;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<CreatePlanItemRequest> items;
}
