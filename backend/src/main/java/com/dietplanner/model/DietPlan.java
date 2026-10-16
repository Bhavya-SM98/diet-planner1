package com.dietplanner.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "diet_plans")
@Data
@NoArgsConstructor
public class DietPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "plan_name")
    private String planName;

    @Column(name = "plan_content", columnDefinition = "TEXT")
    private String planContent;

    @Column(name = "week_start")
    private LocalDate weekStart;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
