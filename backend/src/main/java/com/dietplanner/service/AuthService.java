package com.dietplanner.service;

import com.dietplanner.model.User;
import com.dietplanner.repository.UserRepository;
import com.dietplanner.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtil jwtUtil;

    public Map<String, Object> register(Map<String, String> request) {
        if (userRepository.existsByEmail(request.get("email"))) {
            throw new RuntimeException("Email already exists");
        }
        User user = new User();
        user.setName(request.get("name"));
        user.setEmail(request.get("email"));
        user.setPassword(passwordEncoder.encode(request.get("password")));
        userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("token", jwtUtil.generateToken(user.getEmail()));
        response.put("user", sanitizeUser(user));
        return response;
    }

    public Map<String, Object> login(Map<String, String> request) {
        User user = userRepository.findByEmail(request.get("email"))
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.get("password"), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("token", jwtUtil.generateToken(user.getEmail()));
        response.put("user", sanitizeUser(user));
        return response;
    }

    private Map<String, Object> sanitizeUser(User user) {
        Map<String, Object> u = new HashMap<>();
        u.put("id", user.getId());
        u.put("name", user.getName());
        u.put("email", user.getEmail());
        u.put("age", user.getAge());
        u.put("weight", user.getWeight());
        u.put("height", user.getHeight());
        u.put("goal", user.getGoal());
        u.put("dietaryPreference", user.getDietaryPreference());
        u.put("dailyCalorieTarget", user.getDailyCalorieTarget());
        return u;
    }
}
