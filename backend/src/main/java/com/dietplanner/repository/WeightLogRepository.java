package com.dietplanner.repository;

import com.dietplanner.model.WeightLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WeightLogRepository extends JpaRepository<WeightLog, Long> {
    List<WeightLog> findByUserIdOrderByLogDateDesc(Long userId);
    List<WeightLog> findTop30ByUserIdOrderByLogDateDesc(Long userId);
}
