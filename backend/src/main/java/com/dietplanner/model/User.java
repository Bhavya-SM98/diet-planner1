package com.dietplanner.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private Integer age;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Double weight;
    private Double height;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_level")
    private ActivityLevel activityLevel = ActivityLevel.moderate;

    @Enumerated(EnumType.STRING)
    private Goal goal = Goal.maintenance;

    @Enumerated(EnumType.STRING)
    @Column(name = "dietary_preference")
    private DietaryPreference dietaryPreference = DietaryPreference.non_veg;

    @Column(name = "daily_calorie_target")
    private Integer dailyCalorieTarget;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum Gender { male, female, other }
    public enum ActivityLevel { sedentary, light, moderate, active, very_active }
    public enum Goal { weight_loss, muscle_gain, maintenance }
    public enum DietaryPreference { veg, non_veg, vegan }
}
