package com.fitness.platform.user.service;

import com.fitness.platform.user.dto.AdminStatisticsDto;
import com.fitness.platform.user.repository.FitnessProfileRepository;
import com.fitness.platform.user.repository.UserProfileRepository;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
    private final UserProfileRepository userProfileRepository;
    private final FitnessProfileRepository fitnessProfileRepository;

    public AdminService(UserProfileRepository userProfileRepository, FitnessProfileRepository fitnessProfileRepository) {
        this.userProfileRepository = userProfileRepository;
        this.fitnessProfileRepository = fitnessProfileRepository;
    }

    public AdminStatisticsDto getStatistics() {
        AdminStatisticsDto stats = new AdminStatisticsDto();
        stats.setTotalUsers(userProfileRepository.count());
        stats.setTotalProfiles(fitnessProfileRepository.count());
        stats.setRecentRegistrations(userProfileRepository.count()); // simplified
        return stats;
    }
}
