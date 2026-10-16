package com.dietplanner.controller;

import com.dietplanner.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication auth) {
        return ResponseEntity.ok(userService.getProfile(auth.getName()));
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, Object> data, Authentication auth) {
        return ResponseEntity.ok(userService.updateProfile(auth.getName(), data));
    }

    @PostMapping("/diet-plan")
    public ResponseEntity<?> generateDietPlan(Authentication auth) {
        try {
            return ResponseEntity.ok(userService.generateDietPlan(auth.getName()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/diet-plans")
    public ResponseEntity<?> getDietPlans(Authentication auth) {
        return ResponseEntity.ok(userService.getDietPlans(auth.getName()));
    }

    @PostMapping("/weight")
    public ResponseEntity<?> logWeight(@RequestBody Map<String, Object> data, Authentication auth) {
        Double weight = Double.parseDouble(data.get("weight").toString());
        String notes = (String) data.getOrDefault("notes", "");
        return ResponseEntity.ok(userService.logWeight(auth.getName(), weight, notes));
    }

    @GetMapping("/weight")
    public ResponseEntity<?> getWeightHistory(Authentication auth) {
        return ResponseEntity.ok(userService.getWeightHistory(auth.getName()));
    }
}
