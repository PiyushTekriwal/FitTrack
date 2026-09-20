package com.fitness.platform.recommendation.repository;

import com.fitness.platform.recommendation.model.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    Optional<Recommendation> findFirstByUserIdOrderByCreatedAtDesc(String userId);
    List<Recommendation> findByUserIdOrderByCreatedAtDesc(String userId);
}
