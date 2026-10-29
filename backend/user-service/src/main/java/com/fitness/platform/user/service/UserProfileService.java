package com.fitness.platform.user.service;

import com.fitness.platform.user.dto.*;
import com.fitness.platform.user.entity.FitnessProfile;
import com.fitness.platform.user.entity.UserProfile;
import com.fitness.platform.user.repository.FitnessProfileRepository;
import com.fitness.platform.user.repository.UserProfileRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final FitnessProfileRepository fitnessProfileRepository;

    public UserProfileService(UserProfileRepository userProfileRepository, FitnessProfileRepository fitnessProfileRepository) {
        this.userProfileRepository = userProfileRepository;
        this.fitnessProfileRepository = fitnessProfileRepository;
    }

    @Transactional
    @CacheEvict(value = "userProfiles", key = "#userId")
    public UserProfile getOrCreateProfile(String userId, String email, String firstName, String lastName) {
        return userProfileRepository.findByUserId(userId).orElseGet(() -> {
            UserProfile profile = UserProfile.builder()
                    .userId(userId)
                    .email(email)
                    .firstName(firstName)
                    .lastName(lastName)
                    .build();
            return userProfileRepository.save(profile);
        });
    }

    @Cacheable(value = "userProfiles", key = "#userId")
    public UserProfile getCurrentUserProfile(String userId) {
        return userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User profile not found"));
    }

    @Transactional
    @CacheEvict(value = "userProfiles", key = "#userId")
    public UserProfileDto updateProfile(String userId, UpdateProfileRequest request) {
        UserProfile profile = getCurrentUserProfile(userId);
        if (request.getFirstName() != null) profile.setFirstName(request.getFirstName());
        if (request.getLastName() != null) profile.setLastName(request.getLastName());
        if (request.getEmail() != null) profile.setEmail(request.getEmail());
        userProfileRepository.save(profile);
        return mapToDto(profile);
    }

    @Transactional
    @CacheEvict(value = "userProfiles", key = "#userId")
    public FitnessProfileDto updateFitnessProfile(String userId, UpdateFitnessProfileRequest request) {
        UserProfile userProfile = getCurrentUserProfile(userId);
        FitnessProfile fitnessProfile = userProfile.getFitnessProfile();
        
        if (fitnessProfile == null) {
            fitnessProfile = new FitnessProfile();
            fitnessProfile.setUserProfile(userProfile);
            userProfile.setFitnessProfile(fitnessProfile);
        }
        
        fitnessProfile.setAge(request.getAge());
        fitnessProfile.setHeightCm(request.getHeightCm());
        fitnessProfile.setWeightKg(request.getWeightKg());
        fitnessProfile.setFitnessLevel(request.getFitnessLevel());
        fitnessProfile.setFitnessGoal(request.getFitnessGoal());
        fitnessProfile.setDietaryPreference(request.getDietaryPreference());
        fitnessProfile.setActivityPreferences(request.getActivityPreferences());
        fitnessProfile.setMedicalConditions(request.getMedicalConditions());
        
        fitnessProfileRepository.save(fitnessProfile);
        userProfileRepository.save(userProfile);
        return mapFitnessToDto(fitnessProfile);
    }

    public UserProfileDto getUserProfileForInternal(String userId) {
        return mapToDto(getCurrentUserProfile(userId));
    }
    
    public UserProfileDto mapToDto(UserProfile profile) {
        UserProfileDto dto = new UserProfileDto();
        dto.setId(profile.getId());
        dto.setUserId(profile.getUserId());
        dto.setEmail(profile.getEmail());
        dto.setFirstName(profile.getFirstName());
        dto.setLastName(profile.getLastName());
        dto.setCreatedAt(profile.getCreatedAt());
        if (profile.getFitnessProfile() != null) {
            dto.setFitnessProfile(mapFitnessToDto(profile.getFitnessProfile()));
        }
        return dto;
    }
    
    private FitnessProfileDto mapFitnessToDto(FitnessProfile profile) {
        FitnessProfileDto dto = new FitnessProfileDto();
        dto.setAge(profile.getAge());
        dto.setHeightCm(profile.getHeightCm());
        dto.setWeightKg(profile.getWeightKg());
        dto.setFitnessLevel(profile.getFitnessLevel());
        dto.setFitnessGoal(profile.getFitnessGoal());
        dto.setDietaryPreference(profile.getDietaryPreference());
        dto.setActivityPreferences(profile.getActivityPreferences());
        return dto;
    }
}

