package com.fitness.platform.user.repository;
import com.fitness.platform.user.entity.FitnessProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FitnessProfileRepository extends JpaRepository<FitnessProfile, Long> {
}
