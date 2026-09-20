package com.fitness.platform.recommendation.client;

import com.fitness.platform.recommendation.config.FeignConfig;
import com.fitness.platform.recommendation.dto.ActivityDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "activity-service", configuration = FeignConfig.class)
public interface ActivityServiceClient {
    @GetMapping("/api/activities/recent")
    List<ActivityDto> getRecentActivities(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestParam(defaultValue = "7") int days
    );
}
