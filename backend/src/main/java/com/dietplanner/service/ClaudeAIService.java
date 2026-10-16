package com.dietplanner.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.logging.Logger;

@Service
public class ClaudeAIService {

    private static final Logger logger = Logger.getLogger(ClaudeAIService.class.getName());

    @Value("${groq.api.key}")
    private String GROQ_API_KEY;

    private static final String GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String VISION_MODEL = "qwen/qwen3.8-27b";
    private static final String TEXT_MODEL = "openai/gpt-oss-120b";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public java.util.Map<String, Object> analyzeFoodImage(MultipartFile file) throws Exception {
        String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
        String mediaType = file.getContentType() != null ? file.getContentType() : "image/jpeg";

        String prompt = "Analyze this food image and provide nutritional information. Return ONLY a valid JSON object with this exact structure, no extra text, no markdown:\n{\"foodName\": \"name of the food\", \"quantity\": \"estimated portion size\", \"calories\": 0, \"protein\": 0, \"carbs\": 0, \"fats\": 0, \"fiber\": 0, \"healthScore\": 7, \"tips\": \"one brief health tip about this food\"}\nAll numeric values should be numbers not strings.";

        String requestBody = "{"
            + "\"model\": \"" + VISION_MODEL + "\","
            + "\"messages\": [{"
            + "  \"role\": \"user\","
            + "  \"content\": ["
            + "    {\"type\": \"image_url\", \"image_url\": {\"url\": \"data:" + mediaType + ";base64," + base64Image + "\"}},"
            + "    {\"type\": \"text\", \"text\": " + objectMapper.writeValueAsString(prompt) + "}"
            + "  ]"
            + "}],"
            + "\"max_tokens\": 1000"
            + "}";

        String response = callGroqAPI(requestBody);
        String text = extractTextFromResponse(response);
        text = text.replaceAll("```json", "").replaceAll("```", "").trim();
        return objectMapper.readValue(text, java.util.Map.class);
    }

    public String generateDietPlan(java.util.Map<String, Object> userProfile) throws Exception {
        String prompt = "Create a personalized 7-day meal plan for:\n"
            + "Name: " + userProfile.get("name") + "\n"
            + "Age: " + userProfile.get("age") + ", Gender: " + userProfile.get("gender") + "\n"
            + "Weight: " + userProfile.get("weight") + " kg, Height: " + userProfile.get("height") + " cm\n"
            + "Goal: " + userProfile.get("goal") + "\n"
            + "Activity Level: " + userProfile.get("activityLevel") + "\n"
            + "Dietary Preference: " + userProfile.get("dietaryPreference") + "\n"
            + "Daily Calorie Target: " + userProfile.get("dailyCalorieTarget") + " calories\n\n"
            + "Provide detailed weekly meal plan with breakfast, lunch, dinner and snacks for each day. Include Indian food options. For each meal mention approximate calories, protein, carbs and fats. Include 5 personalized nutrition tips at the end.";

        String requestBody = "{"
            + "\"model\": \"" + TEXT_MODEL + "\","
            + "\"messages\": [{"
            + "  \"role\": \"user\","
            + "  \"content\": " + objectMapper.writeValueAsString(prompt)
            + "}],"
            + "\"max_tokens\": 4000"
            + "}";

        String response = callGroqAPI(requestBody);
        return extractTextFromResponse(response);
    }

    public java.util.Map<String, Object> getNutritionInsights(java.util.Map<String, Object> weeklyData) throws Exception {
        String prompt = "Analyze this weekly nutrition data and provide insights: "
            + objectMapper.writeValueAsString(weeklyData)
            + "\nReturn ONLY a valid JSON object with no extra text or markdown: {\"summary\": \"brief summary\", \"strengths\": [\"s1\",\"s2\",\"s3\"], \"improvements\": [\"i1\",\"i2\",\"i3\"], \"recommendations\": [\"r1\",\"r2\",\"r3\",\"r4\"]}";

        String requestBody = "{"
            + "\"model\": \"" + TEXT_MODEL + "\","
            + "\"messages\": [{"
            + "  \"role\": \"user\","
            + "  \"content\": " + objectMapper.writeValueAsString(prompt)
            + "}],"
            + "\"max_tokens\": 1000"
            + "}";

        String response = callGroqAPI(requestBody);
        String text = extractTextFromResponse(response);
        text = text.replaceAll("```json", "").replaceAll("```", "").trim();
        return objectMapper.readValue(text, java.util.Map.class);
    }

    private String extractTextFromResponse(String rawResponse) throws Exception {
        logger.info("Groq API raw response: " + rawResponse);

        JsonNode root;
        try {
            root = objectMapper.readTree(rawResponse);
        } catch (Exception e) {
            throw new Exception("Groq API returned non-JSON response: " + rawResponse);
        }

        if (root.has("error")) {
            JsonNode error = root.path("error");
            String message = error.path("message").asText("Unknown error");
            throw new Exception("Groq API error: " + message);
        }

        JsonNode choices = root.path("choices");
        if (choices.isMissingNode() || !choices.isArray() || choices.size() == 0) {
            throw new Exception("Groq API returned no choices. Full response: " + rawResponse);
        }

        String text = choices.get(0).path("message").path("content").asText();
        if (text == null || text.isBlank()) {
            throw new Exception("Groq API returned empty content. Full response: " + rawResponse);
        }

        return text;
    }

    private String callGroqAPI(String jsonBody) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GROQ_API_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + GROQ_API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        logger.info("Groq API HTTP status: " + response.statusCode());
        return response.body();
    }
}