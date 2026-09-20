package com.fitness.platform.recommendation.service;

import com.fitness.platform.recommendation.dto.ActivityDto;
import com.fitness.platform.recommendation.dto.FitnessProfileDto;
import com.fitness.platform.recommendation.dto.UserProfileDto;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PromptBuilderTest {

    private final PromptBuilder promptBuilder = new PromptBuilder();

    @Test
    void testBuildComprehensivePrompt() {
        UserProfileDto userProfile = new UserProfileDto("user1", "John", "Doe", null);
        
        FitnessProfileDto fitnessProfile = new FitnessProfileDto();
        fitnessProfile.setAge(30);
        fitnessProfile.setHeightCm(180.0);
        fitnessProfile.setWeightKg(80.0);
        fitnessProfile.setFitnessLevel("INTERMEDIATE");
        fitnessProfile.setFitnessGoal("MUSCLE_GAIN");
        fitnessProfile.setDietaryPreference("HIGH_PROTEIN");
        
        ActivityDto activity = new ActivityDto("RUNNING", "Morning Run", 30, 300, "HIGH", LocalDateTime.now());
        
        String prompt = promptBuilder.buildComprehensivePrompt(userProfile, fitnessProfile, List.of(activity));
        
        assertTrue(prompt.contains("Age: 30"));
        assertTrue(prompt.contains("Height: 180.0 cm"));
        assertTrue(prompt.contains("Weight: 80.0 kg"));
        assertTrue(prompt.contains("Morning Run (RUNNING)"));
    }
}
