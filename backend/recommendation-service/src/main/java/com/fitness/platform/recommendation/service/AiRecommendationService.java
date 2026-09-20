package com.fitness.platform.recommendation.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.platform.recommendation.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@Slf4j
public class AiRecommendationService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PromptBuilder promptBuilder;
    private final RestTemplate restTemplate;

    public AiRecommendationService(PromptBuilder promptBuilder) {
        this.promptBuilder = promptBuilder;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(1500);
        factory.setReadTimeout(3500);
        this.restTemplate = new RestTemplate(factory);
    }

    public String generateRecommendation(UserProfileDto userProfile, FitnessProfileDto fitnessProfile, List<ActivityDto> recentActivities) {
        if (fitnessProfile == null && userProfile != null && userProfile.getFitnessProfile() != null) {
            fitnessProfile = userProfile.getFitnessProfile();
        }

        // 1. Probe real LLM (e.g. Ollama) if online
        String llmResult = tryGenerateWithOllama(userProfile, fitnessProfile, recentActivities);
        if (llmResult != null && !llmResult.isBlank()) {
            log.info("Successfully generated recommendation using local LLM inference engine.");
            return llmResult;
        }

        // 2. Intelligent Sports-Science Adaptive Metabolic Engine
        log.info("Employing adaptive clinical sports-science metabolic engine.");
        return generateAdaptiveSportsScienceRecommendation(userProfile, fitnessProfile, recentActivities);
    }

    private String tryGenerateWithOllama(UserProfileDto userProfile, FitnessProfileDto fitnessProfile, List<ActivityDto> recentActivities) {
        try {
            String prompt = promptBuilder.buildComprehensivePrompt(userProfile, fitnessProfile, recentActivities);
            Map<String, Object> requestBody = Map.of(
                    "model", "tinyllama",
                    "prompt", prompt,
                    "stream", false,
                    "format", "json"
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            String ollamaUrl = "http://localhost:11434/api/generate";
            String response = restTemplate.postForObject(ollamaUrl, entity, String.class);
            if (response != null) {
                JsonNode root = objectMapper.readTree(response);
                if (root.has("response")) {
                    String actualContent = root.get("response").asText();
                    objectMapper.readTree(actualContent);
                    return actualContent;
                }
            }
        } catch (Exception e) {
            log.debug("Local LLM probe skipped: {}", e.getMessage());
        }
        return null;
    }

    private String generateAdaptiveSportsScienceRecommendation(UserProfileDto userProfile, FitnessProfileDto fitnessProfile, List<ActivityDto> recentActivities) {
        String name = (userProfile != null && userProfile.getFirstName() != null && !userProfile.getFirstName().isBlank())
                ? userProfile.getFirstName()
                : "Athlete";
        String goal = (fitnessProfile != null && fitnessProfile.getFitnessGoal() != null && !fitnessProfile.getFitnessGoal().isBlank())
                ? fitnessProfile.getFitnessGoal().replace("_", " ")
                : "GENERAL FITNESS";
        String level = (fitnessProfile != null && fitnessProfile.getFitnessLevel() != null && !fitnessProfile.getFitnessLevel().isBlank())
                ? fitnessProfile.getFitnessLevel()
                : "BEGINNER";
        String diet = (fitnessProfile != null && fitnessProfile.getDietaryPreference() != null && !fitnessProfile.getDietaryPreference().isBlank())
                ? fitnessProfile.getDietaryPreference()
                : "BALANCED";
        Double weight = (fitnessProfile != null && fitnessProfile.getWeightKg() != null && fitnessProfile.getWeightKg() > 0)
                ? fitnessProfile.getWeightKg()
                : 70.0;
        Double height = (fitnessProfile != null && fitnessProfile.getHeightCm() != null && fitnessProfile.getHeightCm() > 0)
                ? fitnessProfile.getHeightCm()
                : 175.0;
        Integer age = (fitnessProfile != null && fitnessProfile.getAge() != null && fitnessProfile.getAge() > 0)
                ? fitnessProfile.getAge()
                : 25;
        String userPreferences = (fitnessProfile != null && fitnessProfile.getActivityPreferences() != null)
                ? fitnessProfile.getActivityPreferences().toString().toLowerCase()
                : "";

        int recentWorkoutsCount = recentActivities != null ? recentActivities.size() : 0;
        int totalMinutes = recentActivities != null ? recentActivities.stream().mapToInt(a -> a.getDurationMinutes() != null ? a.getDurationMinutes() : 0).sum() : 0;
        int totalBurned = recentActivities != null ? recentActivities.stream().mapToInt(a -> a.getCaloriesBurned() != null ? a.getCaloriesBurned() : 0).sum() : 0;

        double palMultiplier;
        String activityTier;
        if (totalMinutes >= 240 || recentWorkoutsCount >= 5) {
            palMultiplier = 1.725;
            activityTier = "High Volume Training";
        } else if (totalMinutes >= 120 || recentWorkoutsCount >= 3) {
            palMultiplier = 1.55;
            activityTier = "Moderate Consistent Training";
        } else if (totalMinutes > 0 || recentWorkoutsCount > 0) {
            palMultiplier = 1.375;
            activityTier = "Light Baseline Conditioning";
        } else {
            palMultiplier = 1.2;
            activityTier = "Sedentary Baseline";
        }

        double bmr = (10.0 * weight) + (6.25 * height) - (5.0 * age) + 5.0;
        double tdee = bmr * palMultiplier;

        double targetCalories;
        double proteinMultiplier;
        double carbPct;
        double fatPct;

        if (goal.contains("WEIGHT LOSS")) {
            targetCalories = tdee - 450;
            proteinMultiplier = 2.2;
            carbPct = 0.35;
            fatPct = 0.30;
        } else if (goal.contains("MUSCLE")) {
            targetCalories = tdee + 350;
            proteinMultiplier = 2.0;
            carbPct = 0.45;
            fatPct = 0.25;
        } else if (goal.contains("ENDURANCE")) {
            targetCalories = tdee + 150;
            proteinMultiplier = 1.6;
            carbPct = 0.55;
            fatPct = 0.25;
        } else {
            targetCalories = tdee;
            proteinMultiplier = 1.6;
            carbPct = 0.40;
            fatPct = 0.30;
        }

        if (diet.equalsIgnoreCase("KETO")) {
            carbPct = 0.05;
            fatPct = 0.70;
        }

        int targetCaloriesInt = Math.max(1200, (int) Math.round(targetCalories));
        int proteinGrams = (int) Math.round(weight * proteinMultiplier);
        int fatGrams = (int) Math.round((targetCaloriesInt * fatPct) / 9.0);
        int carbGrams = Math.max(20, (int) Math.round((targetCaloriesInt * carbPct) / 4.0));

        String summary = String.format(
                "Hello %s! Based on your biometric profile (%.1f kg, %.0f cm, %s level) and current %s activity tier (%d sessions, %d mins, %d kcal burned in the last 7 days), your metabolic TDEE is calculated at %.0f kcal. For your %s goal, your personalized target is %d kcal/day (%dg Protein, %dg Carbs, %dg Healthy Fats) aligned with %s nutrition.",
                name, weight, height, level.toLowerCase(), activityTier.toLowerCase(), recentWorkoutsCount, totalMinutes, totalBurned, tdee, goal, targetCaloriesInt, proteinGrams, carbGrams, fatGrams, diet
        );

        List<Map<String, Object>> workouts = buildWorkouts(goal, level, userPreferences);
        List<Map<String, Object>> diets = buildDiets(diet, targetCaloriesInt, proteinGrams);
        List<Map<String, Object>> weekly = buildWeeklySplit(goal, level, recentWorkoutsCount);
        String recovery = buildRecoveryAdvice(weight, recentWorkoutsCount, totalMinutes, goal);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("summary", summary);
        result.put("workoutRecommendations", workouts);
        result.put("dietRecommendations", diets);
        result.put("weeklyPlan", weekly);
        result.put("recoveryAdvice", recovery);

        try {
            return objectMapper.writeValueAsString(result);
        } catch (Exception e) {
            log.error("Failed to serialize adaptive recommendation", e);
            return "{}";
        }
    }

    private List<Map<String, Object>> buildWorkouts(String goal, String level, String preferences) {
        List<Map<String, Object>> list = new ArrayList<>();
        boolean hasYoga = preferences.contains("yoga");
        boolean hasRunning = preferences.contains("running") || preferences.contains("jog");

        if (goal.contains("WEIGHT LOSS")) {
            list.add(Map.of(
                    "exercise", hasRunning ? "Interval Sprint Aerobics" : "High-Intensity Interval Circuit (HIIT)",
                    "duration", "25 mins",
                    "intensity", "HIGH",
                    "sets", 5, "reps", 1,
                    "notes", "30s sprint / 60s recovery interval to elevate EPOC post-exercise oxygen consumption."
            ));
            list.add(Map.of(
                    "exercise", "Compound Dumbbell Thrusters & Lunges",
                    "duration", "20 mins",
                    "intensity", "HIGH",
                    "sets", 4, "reps", 12,
                    "notes", "Activates large multi-joint muscle groups to maximize intra-workout caloric burn."
            ));
            list.add(Map.of(
                    "exercise", "Kettlebell Swings & Mountain Climbers",
                    "duration", "15 mins",
                    "intensity", "MODERATE",
                    "sets", 3, "reps", 20,
                    "notes", "Strengthens posterior chain and hip hinge mechanics with sustained cardiovascular elevation."
            ));
            list.add(Map.of(
                    "exercise", hasYoga ? "Vinyasa Flow Active Cool-Down" : "Isometric Plank & Deadbug Core Finisher",
                    "duration", "10 mins",
                    "intensity", "LOW",
                    "sets", 3, "reps", 45,
                    "notes", "Stabilizes lumbar spine and decompresses hip flexors."
            ));
        } else if (goal.contains("MUSCLE")) {
            list.add(Map.of(
                    "exercise", "Barbell / Hex-Bar Deadlifts or Squats",
                    "duration", "25 mins",
                    "intensity", "HIGH",
                    "sets", 4, "reps", 6,
                    "notes", "Foundational compound progressive overload. 90-120s rest between working sets."
            ));
            list.add(Map.of(
                    "exercise", "Incline Dumbbell Press & Dips",
                    "duration", "20 mins",
                    "intensity", "HIGH",
                    "sets", 4, "reps", 10,
                    "notes", "Target upper clavicular pectoral fibers with controlled 3-second eccentric tempo."
            ));
            list.add(Map.of(
                    "exercise", "Chest-Supported Row & Lat Pulldowns",
                    "duration", "20 mins",
                    "intensity", "MODERATE",
                    "sets", 4, "reps", 10,
                    "notes", "Builds scapular retraction and mid-back density while sparing spinal fatigue."
            ));
            list.add(Map.of(
                    "exercise", hasYoga ? "Mobility Yoga & Thoracic Openers" : "Overhead Dumbbell Extension & Bicep Curls",
                    "duration", "15 mins",
                    "intensity", "MODERATE",
                    "sets", 3, "reps", 12,
                    "notes", "Direct arm isolation and joint range of motion restoration."
            ));
        } else {
            list.add(Map.of(
                    "exercise", hasRunning ? "Zone 2 Base Aerobic Run" : "Steady-State Aerobic Row / Cycling",
                    "duration", "30 mins",
                    "intensity", "MODERATE",
                    "sets", 1, "reps", 1,
                    "notes", "Maintains 65-72% max heart rate to build mitochondrial density and aerobic base."
            ));
            list.add(Map.of(
                    "exercise", "Push-ups & Pull-ups Supersets",
                    "duration", "20 mins",
                    "intensity", "MODERATE",
                    "sets", 4, "reps", 12,
                    "notes", "Bodyweight calisthenic balance for functional upper body symmetry."
            ));
            list.add(Map.of(
                    "exercise", "Goblet Squats & Bulgarian Split Squats",
                    "duration", "20 mins",
                    "intensity", "MODERATE",
                    "sets", 3, "reps", 12,
                    "notes", "Unilateral lower body stability and knee tracking alignment."
            ));
            list.add(Map.of(
                    "exercise", "Dynamic Functional Mobility & Yoga Flow",
                    "duration", "15 mins",
                    "intensity", "LOW",
                    "sets", 1, "reps", 1,
                    "notes", "Hip flexor opening and hamstring elongation for daily posture optimization."
            ));
        }
        return list;
    }

    private List<Map<String, Object>> buildDiets(String diet, int targetCalories, int proteinGrams) {
        List<Map<String, Object>> meals = new ArrayList<>();
        int pMeal1 = (int) (proteinGrams * 0.25);
        int pMeal2 = (int) (proteinGrams * 0.35);
        int pSnack = (int) (proteinGrams * 0.15);
        int pMeal3 = (int) (proteinGrams * 0.25);

        int cMeal1 = (int) (targetCalories * 0.25);
        int cMeal2 = (int) (targetCalories * 0.35);
        int cSnack = (int) (targetCalories * 0.15);
        int cMeal3 = (int) (targetCalories * 0.25);

        if (diet.equalsIgnoreCase("VEGETARIAN") || diet.equalsIgnoreCase("VEGAN")) {
            meals.add(Map.of(
                    "meal", "Power Plant Oats & Chia Bowl",
                    "description", "Rolled oats simmered with almond milk, chia seeds, walnuts, and organic plant isolate protein",
                    "calories", cMeal1,
                    "protein", pMeal1 + "g",
                    "notes", "Complex low-glycemic carbohydrates with rich omega-3 fatty acids for cognitive drive."
            ));
            meals.add(Map.of(
                    "meal", "Crispy Tofu & Edamame Quinoa Skillet",
                    "description", "Pan-seared spiced firm tofu, edamame, steamed broccoli, red bell peppers, and warm quinoa with tahini",
                    "calories", cMeal2,
                    "protein", pMeal2 + "g",
                    "notes", "Complete essential amino acid profile with high fiber to maintain satiety and gut health."
            ));
            meals.add(Map.of(
                    "meal", "Greek Yogurt / Almond Protein Parfait",
                    "description", "Unsweetened plant or Greek yogurt layered with wild berries, hemp hearts, and pumpkin seeds",
                    "calories", cSnack,
                    "protein", pSnack + "g",
                    "notes", "Quick-digesting snack 60-90 minutes pre- or post-workout."
            ));
            meals.add(Map.of(
                    "meal", "Spiced Lentil Dal & Roasted Vegetables",
                    "description", "Slow-cooked golden lentil dal with turmeric, roasted sweet potatoes, and baby spinach",
                    "calories", cMeal3,
                    "protein", pMeal3 + "g",
                    "notes", "Anti-inflammatory turmeric curcumins to aid overnight connective tissue recovery."
            ));
        } else if (diet.equalsIgnoreCase("KETO")) {
            meals.add(Map.of(
                    "meal", "Avocado & Spinach Pastured Omelette",
                    "description", "3 whole pasture-raised eggs cooked in butter with sliced avocado, feta cheese, and wilted spinach",
                    "calories", cMeal1,
                    "protein", pMeal1 + "g",
                    "notes", "Zero net sugars, high healthy monounsaturated fats to prime ketogenic fat adaptation."
            ));
            meals.add(Map.of(
                    "meal", "Wild Salmon & Asparagus Bowl",
                    "description", "Pan-crisped wild salmon fillet over olive oil tossed asparagus, zucchini noodles, and walnuts",
                    "calories", cMeal2,
                    "protein", pMeal2 + "g",
                    "notes", "Rich in EPA/DHA marine omega-3s to optimize cellular membrane integrity."
            ));
            meals.add(Map.of(
                    "meal", "MCT & Macadamia Energy Boost",
                    "description", "Cold-pressed macadamia nuts, celery sticks with cream cheese, and collagen peptides",
                    "calories", cSnack,
                    "protein", pSnack + "g",
                    "notes", "Sustained clean ketone production without insulin spikes."
            ));
            meals.add(Map.of(
                    "meal", "Grass-Fed Ribeye & Garlic Butter Greens",
                    "description", "Seared grass-fed steak slices served alongside roasted broccoli florets drizzled with garlic butter",
                    "calories", cMeal3,
                    "protein", pMeal3 + "g",
                    "notes", "High zinc, iron, and conjugated linoleic acid for hormonal balance."
            ));
        } else {
            meals.add(Map.of(
                    "meal", "Performance Fuel Breakfast",
                    "description", "Cage-free scrambled eggs, whole-grain sourdough toast with avocado, and a bowl of fresh berries",
                    "calories", cMeal1,
                    "protein", pMeal1 + "g",
                    "notes", "Balanced macronutrient release providing steady morning mental clarity and physical energy."
            ));
            meals.add(Map.of(
                    "meal", "Grilled Herb Chicken & Sweet Potato Plate",
                    "description", "Marinated grilled chicken breast, roasted sweet potato wedges, and steamed green beans with olive oil",
                    "calories", cMeal2,
                    "protein", pMeal2 + "g",
                    "notes", "Clean lean protein paired with slow-burning low-GI complex carbohydrates."
            ));
            meals.add(Map.of(
                    "meal", "Post-Workout Recovery Shake",
                    "description", "Whey or pea protein isolate blended with half a banana, cinnamon, and tablespoon of natural peanut butter",
                    "calories", cSnack,
                    "protein", pSnack + "g",
                    "notes", "Stimulates muscle protein synthesis within the post-workout metabolic window."
            ));
            meals.add(Map.of(
                    "meal", "Baked White Fish & Brown Rice Medley",
                    "description", "Herb-crusted white fish fillet with warm brown rice, roasted asparagus, and lemon vinaigrette",
                    "calories", cMeal3,
                    "protein", pMeal3 + "g",
                    "notes", "Light, easily digestible evening meal to avoid sleep disturbances while supplying micronutrients."
            ));
        }
        return meals;
    }

    private List<Map<String, Object>> buildWeeklySplit(String goal, String level, int workoutCount) {
        if (level.equalsIgnoreCase("BEGINNER")) {
            return List.of(
                    Map.of("day", "Monday", "workout", "Full Body Functional Strength (Session A)", "diet", "Balanced Energy & Post-Workout Protein", "rest", false),
                    Map.of("day", "Tuesday", "workout", "Active Recovery / 30-Min Brisk Outdoor Walk", "diet", "Hydration Focus & Anti-inflammatory Greens", "rest", true),
                    Map.of("day", "Wednesday", "workout", "Full Body Conditioning & Core (Session B)", "diet", "Sustained Complex Carbohydrate Fuel", "rest", false),
                    Map.of("day", "Thursday", "workout", "Dynamic Joint Mobility & Postural Yoga", "diet", "Digestive Rest & Moderate Caloric Intake", "rest", true),
                    Map.of("day", "Friday", "workout", "Full Body Functional Strength (Session C)", "diet", "Glycogen Replenishment Post-Training", "rest", false),
                    Map.of("day", "Saturday", "workout", "Light Cardio / Recreational Swimming or Cycling", "diet", "Protein & Healthy Fats Satiety", "rest", false),
                    Map.of("day", "Sunday", "workout", "Complete CNS Rest & Foam Rolling", "diet", "Tissue Repair & Regeneration Micronutrients", "rest", true)
            );
        } else {
            return List.of(
                    Map.of("day", "Monday", "workout", "Lower Body Compound Power (Squat / Hinge)", "diet", "High Carbohydrates & Targeted Protein", "rest", false),
                    Map.of("day", "Tuesday", "workout", "Upper Body Push/Pull Hypertrophy", "diet", "Clean Protein & Micronutrient Balance", "rest", false),
                    Map.of("day", "Wednesday", "workout", "Zone 2 Cardio & Core Stability", "diet", "Hydration Focus & Electrolytes", "rest", false),
                    Map.of("day", "Thursday", "workout", "Active Recovery / Low-Intensity Mobility", "diet", "Moderate Caloric Maintenance", "rest", true),
                    Map.of("day", "Friday", "workout", "Posterior Chain & Unilateral Strength", "diet", "Glycogen Replenishment Post-Workout", "rest", false),
                    Map.of("day", "Saturday", "workout", "High-Intensity Functional Conditioning (HIIT)", "diet", "Electrolytes & High Protein Dinner", "rest", false),
                    Map.of("day", "Sunday", "workout", "Complete Rest & Myofascial Release", "diet", "Cellular Repair & Sleep Optimization", "rest", true)
            );
        }
    }

    private String buildRecoveryAdvice(Double weight, int recentWorkoutsCount, int totalMinutes, String goal) {
        double recommendedWater = weight * 0.038;
        if (recentWorkoutsCount >= 4 || totalMinutes >= 180) {
            return String.format(
                    "High training volume detected (%d sessions, %d mins logged). Prioritize 8.0 to 8.5 hours of sleep for growth hormone and CNS restoration. Hydrate with at least %.1f L of water daily plus electrolytes. Schedule non-weight-bearing recovery days to avoid overreaching.",
                    recentWorkoutsCount, totalMinutes, recommendedWater
            );
        } else if (recentWorkoutsCount >= 1) {
            return String.format(
                    "Solid training rhythm (%d session logged). Ensure 7.5 to 8.0 hours of quality sleep to facilitate tissue adaptation. Aim for %.1f L of fluid daily. Continue progressive loading with 48 hours between strenuous sessions on the same muscle groups.",
                    recentWorkoutsCount, recommendedWater
            );
        } else {
            return String.format(
                    "Fresh baseline state (no recent fatigue accumulated). Ideal window to initiate progressive loading. Aim for 7.5 to 8 hours of sleep and %.1f L of daily water intake. Begin with moderate volume and prioritize warm-up mobility before high-intensity efforts.",
                    recommendedWater
            );
        }
    }
}
