package com.fitness.platform.plan.controller;

import com.fitness.platform.plan.dto.CreatePlanRequest;
import com.fitness.platform.plan.dto.FitnessPlanDto;
import com.fitness.platform.plan.service.PlanService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RestController
@RequestMapping("/api/plans")
public class PlanController {

    private final PlanService planService;

    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    private String getUserId() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String headerId = request.getHeader("X-User-Id");
                if (headerId != null && !headerId.trim().isEmpty()) {
                    return headerId.trim();
                }
            }
        } catch (Exception ignored) {}

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwt) {
            return jwt.getToken().getSubject();
        }
        return "piyush@fitness.com";
    }

    @PostMapping
    public org.springframework.http.ResponseEntity<FitnessPlanDto> createPlan(@RequestBody CreatePlanRequest request) {
        return new org.springframework.http.ResponseEntity<>(planService.createPlan(getUserId(), request), org.springframework.http.HttpStatus.CREATED);
    }

    @GetMapping
    public List<FitnessPlanDto> getUserPlans() {
        return planService.getUserPlans(getUserId());
    }

    @GetMapping("/current")
    public FitnessPlanDto getCurrentPlan() {
        return planService.getCurrentPlan(getUserId());
    }

    @GetMapping("/{id}")
    public FitnessPlanDto getPlanById(@PathVariable Long id) {
        return planService.getPlanById(id, getUserId());
    }

    @PatchMapping("/{id}/items/{itemId}/complete")
    public FitnessPlanDto completeItem(@PathVariable Long id, @PathVariable Long itemId) {
        return planService.completeItem(id, itemId, getUserId());
    }

    @PatchMapping("/{id}/archive")
    public void archivePlan(@PathVariable Long id) {
        planService.archivePlan(id, getUserId());
    }
}
