package com.fitness.platform.activity.service;

import com.fitness.platform.activity.domain.Activity;
import com.fitness.platform.activity.dto.ActivityDto;
import com.fitness.platform.activity.dto.ActivityStatsDto;
import com.fitness.platform.activity.dto.CreateActivityRequest;
import com.fitness.platform.activity.dto.PagedResponse;
import com.fitness.platform.activity.event.WorkoutCompletedEvent;
import com.fitness.platform.activity.exception.AccessDeniedException;
import com.fitness.platform.activity.exception.ResourceNotFoundException;
import com.fitness.platform.activity.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final ActivityEventPublisher eventPublisher;

    @Transactional
    @CacheEvict(value = {"activityStats", "recentActivities"}, allEntries = true)
    public ActivityDto createActivity(String userId, CreateActivityRequest request) {
        log.info("Creating activity for user: {}", userId);
        
        Activity activity = Activity.builder()
                .userId(userId)
                .activityType(request.getActivityType())
                .exerciseName(request.getExerciseName())
                .description(request.getDescription())
                .durationMinutes(request.getDurationMinutes())
                .caloriesBurned(request.getCaloriesBurned())
                .intensity(request.getIntensity())
                .startTime(request.getStartTime() != null ? request.getStartTime() : LocalDateTime.now())
                .endTime(request.getEndTime() != null ? request.getEndTime() : LocalDateTime.now())
                .notes(request.getNotes())
                .build();
                
        Activity savedActivity = activityRepository.save(activity);
        
        eventPublisher.publishWorkoutCompletedEvent(
                WorkoutCompletedEvent.builder()
                        .userId(userId)
                        .activityId(String.valueOf(savedActivity.getId()))
                        .activityType(savedActivity.getActivityType())
                        .caloriesBurned(savedActivity.getCaloriesBurned())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
        
        return mapToDto(savedActivity);
    }

    public PagedResponse<ActivityDto> getUserActivities(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Activity> activityPage = activityRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        
        List<ActivityDto> content = activityPage.getContent().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
                
        return PagedResponse.<ActivityDto>builder()
                .content(content)
                .page(activityPage.getNumber())
                .size(activityPage.getSize())
                .totalElements(activityPage.getTotalElements())
                .totalPages(activityPage.getTotalPages())
                .build();
    }

    @Cacheable(value = "recentActivities", key = "#userId + '_' + #days")
    public List<ActivityDto> getRecentActivities(String userId, int days) {
        LocalDateTime afterDate = LocalDateTime.now().minusDays(days);
        return activityRepository.findByUserIdAndCreatedAtAfterOrderByCreatedAtDesc(userId, afterDate)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public ActivityDto getActivityById(Long id, String userId) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found with id: " + id));
                
        if (!activity.getUserId().equals(userId)) {
            throw new AccessDeniedException("You do not have permission to access this activity");
        }
        
        return mapToDto(activity);
    }

    @Cacheable(value = "activityStats", key = "#userId")
    public ActivityStatsDto getActivityStats(String userId) {
        List<Activity> allActivities = activityRepository.findByUserIdOrderByCreatedAtDesc(userId);
        
        if (allActivities.isEmpty()) {
            return ActivityStatsDto.builder()
                    .totalActivities(0)
                    .totalCaloriesBurned(0)
                    .totalDurationMinutes(0)
                    .activitiesByType(Collections.emptyMap())
                    .recentActivities(Collections.emptyList())
                    .averageCaloriesPerWorkout(0.0)
                    .currentStreak(0)
                    .build();
        }

        long totalActivities = allActivities.size();
        long totalCaloriesBurned = allActivities.stream()
                .filter(a -> a.getCaloriesBurned() != null)
                .mapToLong(Activity::getCaloriesBurned)
                .sum();
        long totalDurationMinutes = allActivities.stream()
                .filter(a -> a.getDurationMinutes() != null)
                .mapToLong(Activity::getDurationMinutes)
                .sum();
                
        Map<String, Long> activitiesByType = allActivities.stream()
                .filter(a -> a.getActivityType() != null)
                .collect(Collectors.groupingBy(Activity::getActivityType, Collectors.counting()));
                
        List<ActivityDto> recentActivities = allActivities.stream()
                .limit(5)
                .map(this::mapToDto)
                .collect(Collectors.toList());
                
        double averageCaloriesPerWorkout = totalActivities > 0 ? 
                (double) totalCaloriesBurned / totalActivities : 0.0;
                
        int streak = calculateStreak(allActivities);

        return ActivityStatsDto.builder()
                .totalActivities(totalActivities)
                .totalCaloriesBurned(totalCaloriesBurned)
                .totalDurationMinutes(totalDurationMinutes)
                .activitiesByType(activitiesByType)
                .recentActivities(recentActivities)
                .averageCaloriesPerWorkout(averageCaloriesPerWorkout)
                .currentStreak(streak)
                .build();
    }
    
    private int calculateStreak(List<Activity> activities) {
        if (activities.isEmpty()) return 0;
        
        Set<LocalDate> activityDates = activities.stream()
                .filter(a -> a.getStartTime() != null)
                .map(a -> a.getStartTime().toLocalDate())
                .collect(Collectors.toSet());
                
        LocalDate currentDate = LocalDate.now();
        int streak = 0;
        
        if (activityDates.contains(currentDate)) {
            streak++;
            currentDate = currentDate.minusDays(1);
        } else if (activityDates.contains(currentDate.minusDays(1))) {
            currentDate = currentDate.minusDays(1);
            streak++;
            currentDate = currentDate.minusDays(1);
        } else {
            return 0;
        }
        
        while (activityDates.contains(currentDate)) {
            streak++;
            currentDate = currentDate.minusDays(1);
        }
        
        return streak;
    }

    @Transactional
    @CacheEvict(value = {"activityStats", "recentActivities"}, allEntries = true)
    public void deleteActivity(Long id, String userId) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found with id: " + id));
                
        if (!activity.getUserId().equals(userId)) {
            throw new AccessDeniedException("You do not have permission to delete this activity");
        }
        
        activityRepository.delete(activity);
    }
    
    public long getAllActivityCount() {
        return activityRepository.count();
    }

    private ActivityDto mapToDto(Activity activity) {
        return ActivityDto.builder()
                .id(String.valueOf(activity.getId()))
                .userId(activity.getUserId())
                .activityType(activity.getActivityType())
                .exerciseName(activity.getExerciseName())
                .description(activity.getDescription())
                .durationMinutes(activity.getDurationMinutes())
                .caloriesBurned(activity.getCaloriesBurned())
                .intensity(activity.getIntensity())
                .startTime(activity.getStartTime())
                .endTime(activity.getEndTime())
                .notes(activity.getNotes())
                .createdAt(activity.getCreatedAt())
                .updatedAt(activity.getUpdatedAt())
                .build();
    }
}
