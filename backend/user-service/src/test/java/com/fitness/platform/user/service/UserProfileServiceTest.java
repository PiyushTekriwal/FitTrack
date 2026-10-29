package com.fitness.platform.user.service;

import com.fitness.platform.user.dto.UpdateProfileRequest;
import com.fitness.platform.user.dto.UserProfileDto;
import com.fitness.platform.user.entity.UserProfile;
import com.fitness.platform.user.repository.FitnessProfileRepository;
import com.fitness.platform.user.repository.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserProfileServiceTest {

    @Mock
    private UserProfileRepository userProfileRepository;
    
    @Mock
    private FitnessProfileRepository fitnessProfileRepository;

    @InjectMocks
    private UserProfileService userProfileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetOrCreateProfile_Existing() {
        UserProfile profile = new UserProfile();
        profile.setUserId("user1");
        when(userProfileRepository.findByUserId("user1")).thenReturn(Optional.of(profile));

        UserProfile result = userProfileService.getOrCreateProfile("user1", "email@t.com", "F", "L");
        assertEquals("user1", result.getUserId());
        verify(userProfileRepository, never()).save(any());
    }
    
    @Test
    void testUpdateProfile() {
        UserProfile profile = new UserProfile();
        profile.setUserId("user1");
        when(userProfileRepository.findByUserId("user1")).thenReturn(Optional.of(profile));
        
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFirstName("NewName");
        
        UserProfileDto dto = userProfileService.updateProfile("user1", request);
        assertEquals("NewName", dto.getFirstName());
        verify(userProfileRepository).save(profile);
    }
}

