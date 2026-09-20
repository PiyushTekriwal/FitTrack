package com.fitness.platform.plan.repository;

import com.fitness.platform.plan.entity.FitnessPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FitnessPlanRepository extends JpaRepository<FitnessPlan, Long> {
    List<FitnessPlan> findByUserIdOrderByCreatedAtDesc(String userId);
    Optional<FitnessPlan> findByIdAndUserId(Long id, String userId);
    Optional<FitnessPlan> findFirstByUserIdAndStatusOrderByCreatedAtDesc(String userId, String status);
    long countByUserId(String userId);
}
