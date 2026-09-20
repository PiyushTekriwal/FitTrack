package com.fitness.platform.recommendation.client;

import com.fitness.platform.recommendation.config.FeignConfig;
import com.fitness.platform.recommendation.dto.FitnessProfileDto;
import com.fitness.platform.recommendation.dto.UserProfileDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "user-service", configuration = FeignConfig.class)
public interface UserServiceClient {
    @GetMapping("/api/users/me")
    UserProfileDto getUserProfile(@RequestHeader(value = "X-User-Id", required = false) String userId);
    
    @GetMapping("/api/users/me/fitness-profile")
    FitnessProfileDto getFitnessProfile(@RequestHeader(value = "X-User-Id", required = false) String userId);
}
