package com.dietplanner.repository;

import com.dietplanner.model.DietPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DietPlanRepository extends JpaRepository<DietPlan, Long> {
    List<DietPlan> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<DietPlan> findFirstByUserIdOrderByCreatedAtDesc(Long userId);
}
