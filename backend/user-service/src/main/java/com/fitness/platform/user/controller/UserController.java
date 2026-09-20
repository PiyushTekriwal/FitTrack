package com.fitness.platform.user.controller;

import com.fitness.platform.user.dto.FitnessProfileDto;
import com.fitness.platform.user.dto.UpdateFitnessProfileRequest;
import com.fitness.platform.user.dto.UpdateProfileRequest;
import com.fitness.platform.user.dto.UserProfileDto;
import com.fitness.platform.user.entity.UserProfile;
import com.fitness.platform.user.service.UserProfileService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/me")
public class UserController {
    
    private final UserProfileService userProfileService;

    public UserController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    private String getUserId(HttpServletRequest request) {
        String headerId = request.getHeader("X-User-Id");
        if (headerId != null && !headerId.trim().isEmpty()) {
            return headerId.trim();
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwt) {
            return jwt.getToken().getSubject();
        }
        return "piyush@fitness.com";
    }

    @GetMapping
    public UserProfileDto getCurrentProfile(HttpServletRequest request) {
        String userId = getUserId(request);
        String headerEmail = request.getHeader("X-User-Email");
        String email = (headerEmail != null && !headerEmail.isEmpty()) ? headerEmail : userId;
        
        String headerName = request.getHeader("X-User-Name");
        String firstName = "Piyush";
        String lastName = "Tekriwal";
        if (headerName != null && !headerName.isEmpty()) {
            String[] parts = headerName.split(" ", 2);
            firstName = parts[0];
            lastName = parts.length > 1 ? parts[1] : "";
        }
        
        UserProfile profile = userProfileService.getOrCreateProfile(userId, email, firstName, lastName);
        return userProfileService.mapToDto(profile);
    }

    @PutMapping
    public UserProfileDto updateProfile(HttpServletRequest request, @RequestBody UpdateProfileRequest body) {
        String userId = getUserId(request);
        return userProfileService.updateProfile(userId, body);
    }

    @GetMapping("/fitness-profile")
    public FitnessProfileDto getFitnessProfile(HttpServletRequest request) {
        String userId = getUserId(request);
        UserProfile profile;
        try {
            profile = userProfileService.getCurrentUserProfile(userId);
        } catch (Exception e) {
            profile = userProfileService.getOrCreateProfile(userId, userId, "Athlete", "");
        }
        if (profile.getFitnessProfile() == null) {
            return new FitnessProfileDto();
        }
        FitnessProfileDto dto = new FitnessProfileDto();
        dto.setAge(profile.getFitnessProfile().getAge());
        dto.setHeightCm(profile.getFitnessProfile().getHeightCm());
        dto.setWeightKg(profile.getFitnessProfile().getWeightKg());
        dto.setFitnessLevel(profile.getFitnessProfile().getFitnessLevel());
        dto.setFitnessGoal(profile.getFitnessProfile().getFitnessGoal());
        dto.setDietaryPreference(profile.getFitnessProfile().getDietaryPreference());
        dto.setActivityPreferences(profile.getFitnessProfile().getActivityPreferences());
        return dto;
    }

    @PutMapping("/fitness-profile")
    public FitnessProfileDto updateFitnessProfile(HttpServletRequest request, @RequestBody UpdateFitnessProfileRequest body) {
        String userId = getUserId(request);
        userProfileService.getOrCreateProfile(userId, userId, "Athlete", "");
        return userProfileService.updateFitnessProfile(userId, body);
    }
}
