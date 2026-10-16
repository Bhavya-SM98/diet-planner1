package com.dietplanner.service;

import com.dietplanner.model.FoodLog;
import com.dietplanner.model.User;
import com.dietplanner.repository.FoodLogRepository;
import com.dietplanner.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class FoodLogService {

    @Autowired private FoodLogRepository foodLogRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ClaudeAIService claudeAIService;

    @Value("${app.upload.dir}")
    private String uploadDir;

    public Map<String, Object> analyzeAndLogFood(String email, MultipartFile image, String mealType, String logDate) throws Exception {
        User user = userRepository.findByEmail(email).orElseThrow();

        // Analyze food with AI
        Map<String, Object> analysis = claudeAIService.analyzeFoodImage(image);

        // Save image
        String imageUrl = saveImage(image);

        // Save to DB
        FoodLog log = new FoodLog();
        log.setUser(user);
        log.setMealType(FoodLog.MealType.valueOf(mealType));
        log.setFoodName((String) analysis.get("foodName"));
        log.setCalories(toDouble(analysis.get("calories")));
        log.setProtein(toDouble(analysis.get("protein")));
        log.setCarbs(toDouble(analysis.get("carbs")));
        log.setFats(toDouble(analysis.get("fats")));
        log.setFiber(toDouble(analysis.get("fiber")));
        log.setQuantity((String) analysis.get("quantity"));
        log.setImageUrl(imageUrl);
        log.setLogDate(LocalDate.parse(logDate));
        foodLogRepository.save(log);

        Map<String, Object> response = new HashMap<>(analysis);
        response.put("id", log.getId());
        response.put("imageUrl", imageUrl);
        return response;
    }

    public Map<String, Object> getDailySummary(String email, String date) {
        User user = userRepository.findByEmail(email).orElseThrow();
        LocalDate logDate = LocalDate.parse(date);

        List<FoodLog> logs = foodLogRepository.findByUserIdAndLogDateOrderByCreatedAtDesc(user.getId(), logDate);
        Double calories = foodLogRepository.sumCaloriesByUserIdAndDate(user.getId(), logDate);
        Double protein = foodLogRepository.sumProteinByUserIdAndDate(user.getId(), logDate);
        Double carbs = foodLogRepository.sumCarbsByUserIdAndDate(user.getId(), logDate);
        Double fats = foodLogRepository.sumFatsByUserIdAndDate(user.getId(), logDate);

        Map<String, Object> summary = new HashMap<>();
        summary.put("logs", logs);
        summary.put("totalCalories", calories);
        summary.put("totalProtein", protein);
        summary.put("totalCarbs", carbs);
        summary.put("totalFats", fats);
        summary.put("calorieTarget", user.getDailyCalorieTarget());
        summary.put("remaining", user.getDailyCalorieTarget() != null ? user.getDailyCalorieTarget() - calories : null);
        return summary;
    }

    public List<FoodLog> getWeeklyLogs(String email, String startDate, String endDate) {
        User user = userRepository.findByEmail(email).orElseThrow();
        return foodLogRepository.findByUserIdAndLogDateBetweenOrderByLogDateDesc(
                user.getId(), LocalDate.parse(startDate), LocalDate.parse(endDate));
    }

    public void deleteLog(Long id) {
        foodLogRepository.deleteById(id);
    }

    private String saveImage(MultipartFile file) throws IOException {
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        file.transferTo(new File(uploadDir + filename));
        return "/uploads/" + filename;
    }

    private Double toDouble(Object val) {
        if (val == null) return 0.0;
        if (val instanceof Number) return ((Number) val).doubleValue();
        return Double.parseDouble(val.toString());
    }
}
