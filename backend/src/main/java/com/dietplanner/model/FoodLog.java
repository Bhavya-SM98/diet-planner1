package com.dietplanner.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "food_logs")
@Data
@NoArgsConstructor
public class FoodLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type", nullable = false)
    private MealType mealType;

    @Column(name = "food_name", nullable = false)
    private String foodName;

    private Double calories = 0.0;
    private Double protein = 0.0;
    private Double carbs = 0.0;
    private Double fats = 0.0;
    private Double fiber = 0.0;
    private String quantity;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "log_date", nullable = false)
    private LocalDate logDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum MealType { breakfast, lunch, dinner, snack }
}
