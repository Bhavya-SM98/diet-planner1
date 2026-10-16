package com.dietplanner.controller;

import com.dietplanner.service.FoodLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/food")
public class FoodLogController {

    @Autowired
    private FoodLogService foodLogService;

    @PostMapping("/analyze")
    public ResponseEntity<?> analyzeFood(
            @RequestParam("image") MultipartFile image,
            @RequestParam("mealType") String mealType,
            @RequestParam(value = "logDate", required = false) String logDate,
            Authentication auth) {
        try {
            String date = logDate != null ? logDate : LocalDate.now().toString();
            return ResponseEntity.ok(foodLogService.analyzeAndLogFood(auth.getName(), image, mealType, date));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/daily")
    public ResponseEntity<?> getDailySummary(
            @RequestParam(value = "date", required = false) String date,
            Authentication auth) {
        String logDate = date != null ? date : LocalDate.now().toString();
        return ResponseEntity.ok(foodLogService.getDailySummary(auth.getName(), logDate));
    }

    @GetMapping("/weekly")
    public ResponseEntity<?> getWeeklyLogs(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            Authentication auth) {
        return ResponseEntity.ok(foodLogService.getWeeklyLogs(auth.getName(), startDate, endDate));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLog(@PathVariable Long id) {
        foodLogService.deleteLog(id);
        return ResponseEntity.ok(java.util.Map.of("message", "Deleted successfully"));
    }
}
