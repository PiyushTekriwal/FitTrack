package com.fitness.platform.recommendation.controller;

import com.fitness.platform.recommendation.config.SecurityUtils;
import com.fitness.platform.recommendation.dto.GenerateRequest;
import com.fitness.platform.recommendation.dto.RecommendationDto;
import com.fitness.platform.recommendation.service.RecommendationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {
    
    private final RecommendationService service;

    public RecommendationController(RecommendationService service) {
        this.service = service;
    }

    private String resolveUserId(HttpServletRequest request) {
        if (request != null) {
            String headerId = request.getHeader("X-User-Id");
            if (headerId != null && !headerId.trim().isEmpty()) {
                return headerId.trim();
            }
        }
        return SecurityUtils.getCurrentUserId();
    }
    
    @PostMapping("/generate")
    public ResponseEntity<RecommendationDto> generate(HttpServletRequest request, @RequestBody(required = false) GenerateRequest body) {
        String userId = resolveUserId(request);
        String type = body != null ? body.getType() : "COMPREHENSIVE";
        RecommendationDto result = service.generateRecommendation(userId, type);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/latest")
    public ResponseEntity<?> getLatest(HttpServletRequest request) {
        String userId = resolveUserId(request);
        RecommendationDto latest = service.getLatestRecommendation(userId);
        if (latest == null) {
            return ResponseEntity.ok(Map.of("message", "No recommendations yet", "available", false));
        }
        return ResponseEntity.ok(latest);
    }
    
    @GetMapping
    public ResponseEntity<List<RecommendationDto>> getAll(HttpServletRequest request) {
        String userId = resolveUserId(request);
        return ResponseEntity.ok(service.getUserRecommendations(userId));
    }
}
