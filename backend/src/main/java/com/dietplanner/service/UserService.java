package com.dietplanner.service;

import com.dietplanner.model.DietPlan;
import com.dietplanner.model.User;
import com.dietplanner.model.WeightLog;
import com.dietplanner.repository.DietPlanRepository;
import com.dietplanner.repository.UserRepository;
import com.dietplanner.repository.WeightLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    @Autowired private UserRepository userRepository;
    @Autowired private DietPlanRepository dietPlanRepository;
    @Autowired private WeightLogRepository weightLogRepository;
    @Autowired private ClaudeAIService claudeAIService;

    public User updateProfile(String email, Map<String, Object> data) {
        User user = userRepository.findByEmail(email).orElseThrow();
        if (data.containsKey("age")) user.setAge((Integer) data.get("age"));
        if (data.containsKey("weight")) user.setWeight(toDouble(data.get("weight")));
        if (data.containsKey("height")) user.setHeight(toDouble(data.get("height")));
        if (data.containsKey("gender")) user.setGender(User.Gender.valueOf((String) data.get("gender")));
        if (data.containsKey("goal")) user.setGoal(User.Goal.valueOf((String) data.get("goal")));
        if (data.containsKey("activityLevel")) user.setActivityLevel(User.ActivityLevel.valueOf((String) data.get("activityLevel")));
        if (data.containsKey("dietaryPreference")) user.setDietaryPreference(User.DietaryPreference.valueOf((String) data.get("dietaryPreference")));

        // Calculate TDEE
        if (user.getWeight() != null && user.getHeight() != null && user.getAge() != null) {
            int tdee = calculateTDEE(user);
            user.setDailyCalorieTarget(tdee);
        }
        return userRepository.save(user);
    }

    public Map<String, Object> generateDietPlan(String email) throws Exception {
        User user = userRepository.findByEmail(email).orElseThrow();

        Map<String, Object> profile = new HashMap<>();
        profile.put("name", user.getName());
        profile.put("age", user.getAge());
        profile.put("gender", user.getGender());
        profile.put("weight", user.getWeight());
        profile.put("height", user.getHeight());
        profile.put("goal", user.getGoal());
        profile.put("activityLevel", user.getActivityLevel());
        profile.put("dietaryPreference", user.getDietaryPreference());
        profile.put("dailyCalorieTarget", user.getDailyCalorieTarget());

        String planContent = claudeAIService.generateDietPlan(profile);

        DietPlan plan = new DietPlan();
        plan.setUser(user);
        plan.setPlanName("Weekly Plan - " + LocalDate.now());
        plan.setPlanContent(planContent);
        plan.setWeekStart(LocalDate.now());
        dietPlanRepository.save(plan);

        Map<String, Object> response = new HashMap<>();
        response.put("id", plan.getId());
        response.put("planContent", planContent);
        response.put("weekStart", plan.getWeekStart());
        return response;
    }

    public List<DietPlan> getDietPlans(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        return dietPlanRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    public WeightLog logWeight(String email, Double weight, String notes) {
        User user = userRepository.findByEmail(email).orElseThrow();
        WeightLog log = new WeightLog();
        log.setUser(user);
        log.setWeight(weight);
        log.setLogDate(LocalDate.now());
        log.setNotes(notes);
        // Update user current weight
        user.setWeight(weight);
        userRepository.save(user);
        return weightLogRepository.save(log);
    }

    public List<WeightLog> getWeightHistory(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        return weightLogRepository.findTop30ByUserIdOrderByLogDateDesc(user.getId());
    }

    public User getProfile(String email) {
        return userRepository.findByEmail(email).orElseThrow();
    }

    private int calculateTDEE(User user) {
        double bmr;
        if (user.getGender() == User.Gender.male) {
            bmr = 88.362 + (13.397 * user.getWeight()) + (4.799 * user.getHeight()) - (5.677 * user.getAge());
        } else {
            bmr = 447.593 + (9.247 * user.getWeight()) + (3.098 * user.getHeight()) - (4.330 * user.getAge());
        }
        double multiplier = switch (user.getActivityLevel()) {
            case sedentary -> 1.2;
            case light -> 1.375;
            case moderate -> 1.55;
            case active -> 1.725;
            case very_active -> 1.9;
        };
        double tdee = bmr * multiplier;
        return switch (user.getGoal()) {
            case weight_loss -> (int) (tdee - 500);
            case muscle_gain -> (int) (tdee + 300);
            case maintenance -> (int) tdee;
        };
    }

    private Double toDouble(Object val) {
        if (val == null) return null;
        if (val instanceof Number) return ((Number) val).doubleValue();
        return Double.parseDouble(val.toString());
    }
}
