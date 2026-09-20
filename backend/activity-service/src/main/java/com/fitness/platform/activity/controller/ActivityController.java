package com.fitness.platform.activity.controller;

import com.fitness.platform.activity.dto.ActivityDto;
import com.fitness.platform.activity.dto.ActivityStatsDto;
import com.fitness.platform.activity.dto.CreateActivityRequest;
import com.fitness.platform.activity.dto.PagedResponse;
import com.fitness.platform.activity.security.SecurityUtils;
import com.fitness.platform.activity.service.ActivityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    @PostMapping
    public ResponseEntity<ActivityDto> createActivity(@Valid @RequestBody CreateActivityRequest request) {
        String userId = SecurityUtils.getCurrentUserId();
        ActivityDto activity = activityService.createActivity(userId, request);
        return new ResponseEntity<>(activity, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<PagedResponse<ActivityDto>> getUserActivities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String userId = SecurityUtils.getCurrentUserId();
        PagedResponse<ActivityDto> activities = activityService.getUserActivities(userId, page, size);
        return ResponseEntity.ok(activities);
    }
    
    @GetMapping("/history")
    public ResponseEntity<PagedResponse<ActivityDto>> getHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        String userId = SecurityUtils.getCurrentUserId();
        PagedResponse<ActivityDto> activities = activityService.getUserActivities(userId, page, size);
        return ResponseEntity.ok(activities);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<ActivityDto>> getRecentActivities(
            @RequestParam(defaultValue = "7") int days) {
        String userId = SecurityUtils.getCurrentUserId();
        List<ActivityDto> activities = activityService.getRecentActivities(userId, days);
        return ResponseEntity.ok(activities);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActivityDto> getActivityById(@PathVariable Long id) {
        String userId = SecurityUtils.getCurrentUserId();
        ActivityDto activity = activityService.getActivityById(id, userId);
        return ResponseEntity.ok(activity);
    }

    @GetMapping("/stats")
    public ResponseEntity<ActivityStatsDto> getActivityStats() {
        String userId = SecurityUtils.getCurrentUserId();
        ActivityStatsDto stats = activityService.getActivityStats(userId);
        return ResponseEntity.ok(stats);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long id) {
        String userId = SecurityUtils.getCurrentUserId();
        activityService.deleteActivity(id, userId);
        return ResponseEntity.noContent().build();
    }
}
