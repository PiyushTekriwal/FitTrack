package com.fitness.platform.recommendation.service;

import com.fitness.platform.recommendation.dto.ActivityDto;
import com.fitness.platform.recommendation.dto.FitnessProfileDto;
import com.fitness.platform.recommendation.dto.UserProfileDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PromptBuilder {
    
    public String buildComprehensivePrompt(UserProfileDto userProfile, FitnessProfileDto fitnessProfile, List<ActivityDto> recentActivities) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are an expert fitness coach and nutritionist. Provide personalized recommendations.\n\n");
        prompt.append("IMPORTANT: Respond ONLY with valid JSON. No markdown, no code blocks, no extra text.\n\n");
        
        // Add user profile info
        if (fitnessProfile != null) {
            prompt.append("USER PROFILE:\n");
            prompt.append("- Age: ").append(fitnessProfile.getAge()).append("\n");
            prompt.append("- Height: ").append(fitnessProfile.getHeightCm()).append(" cm\n");
            prompt.append("- Weight: ").append(fitnessProfile.getWeightKg()).append(" kg\n");
            prompt.append("- Fitness Level: ").append(fitnessProfile.getFitnessLevel()).append("\n");
            prompt.append("- Goal: ").append(fitnessProfile.getFitnessGoal()).append("\n");
            prompt.append("- Dietary Preference: ").append(fitnessProfile.getDietaryPreference()).append("\n\n");
        }
        
        // Add recent activities
        if (recentActivities != null && !recentActivities.isEmpty()) {
            prompt.append("RECENT ACTIVITIES (last 7 days):\n");
            for (var activity : recentActivities) {
                prompt.append("- ").append(activity.getExerciseName())
                    .append(" (").append(activity.getActivityType()).append("): ")
                    .append(activity.getDurationMinutes()).append(" min, ")
                    .append(activity.getCaloriesBurned()).append(" cal, ")
                    .append(activity.getIntensity()).append(" intensity\n");
            }
            prompt.append("\n");
        } else {
            prompt.append("RECENT ACTIVITIES: No activities recorded yet.\n\n");
        }
        
        prompt.append("Provide your response as a JSON object with this exact structure:\n");
        prompt.append("{\n");
        prompt.append("  \"summary\": \"Brief overall assessment and recommendations\",\n");
        prompt.append("  \"workoutRecommendations\": [{\"exercise\": \"name\", \"duration\": \"time\", \"intensity\": \"level\", \"sets\": 0, \"reps\": 0, \"notes\": \"tip\"}],\n");
        prompt.append("  \"dietRecommendations\": [{\"meal\": \"name\", \"description\": \"details\", \"calories\": 0, \"protein\": \"amount\", \"notes\": \"tip\"}],\n");
        prompt.append("  \"weeklyPlan\": [{\"day\": \"Monday\", \"workout\": \"description\", \"diet\": \"focus\", \"rest\": false}],\n");
        prompt.append("  \"recoveryAdvice\": \"Recovery recommendations\"\n");
        prompt.append("}\n");
        
        return prompt.toString();
    }
}
