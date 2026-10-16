package com.dietplanner.repository;

import com.dietplanner.model.FoodLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface FoodLogRepository extends JpaRepository<FoodLog, Long> {
    List<FoodLog> findByUserIdAndLogDateOrderByCreatedAtDesc(Long userId, LocalDate logDate);
    List<FoodLog> findByUserIdAndLogDateBetweenOrderByLogDateDesc(Long userId, LocalDate start, LocalDate end);

    @Query("SELECT COALESCE(SUM(f.calories), 0) FROM FoodLog f WHERE f.user.id = :userId AND f.logDate = :date")
    Double sumCaloriesByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Query("SELECT COALESCE(SUM(f.protein), 0) FROM FoodLog f WHERE f.user.id = :userId AND f.logDate = :date")
    Double sumProteinByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Query("SELECT COALESCE(SUM(f.carbs), 0) FROM FoodLog f WHERE f.user.id = :userId AND f.logDate = :date")
    Double sumCarbsByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Query("SELECT COALESCE(SUM(f.fats), 0) FROM FoodLog f WHERE f.user.id = :userId AND f.logDate = :date")
    Double sumFatsByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);
}
