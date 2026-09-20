package com.fitness.platform.activity.controller;

import com.fitness.platform.activity.dto.ActivityDto;
import com.fitness.platform.activity.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/internal/activities")
@RequiredArgsConstructor
public class InternalActivityController {

    private final ActivityService activityService;

    @GetMapping
    public ResponseEntity<List<ActivityDto>> getActivitiesForUser(
            @RequestParam String userId,
            @RequestParam(defaultValue = "7") int days) {
        List<ActivityDto> activities = activityService.getRecentActivities(userId, days);
        return ResponseEntity.ok(activities);
    }
    
    @GetMapping("/count")
    public ResponseEntity<Long> getAllActivityCount() {
        return ResponseEntity.ok(activityService.getAllActivityCount());
    }
}
