package bd.edu.seu.pulse.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Service
public class AIService {

    @Value("${openai.api.key:}")
    private String apiKey;

    private final WebClient webClient = WebClient.create();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String generateContent(String prompt) {
        if (apiKey == null || apiKey.isEmpty()) {
            return "AI service is currently unavailable. Please try again later.";
        }

        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-3.5-turbo");
            requestBody.put("messages", new Object[]{
                Map.of("role", "user", "content", "Generate a blog post about: " + prompt)
            });
            requestBody.put("max_tokens", 300);
            requestBody.put("temperature", 0.7);

            String response = webClient.post()
                .uri("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            if (response != null) {
                JsonNode jsonResponse = objectMapper.readTree(response);
                
                if (jsonResponse.has("error")) {
                    String errorMessage = jsonResponse.path("error").path("message").asText();
                    return errorMessage;
                }
                
                String content = jsonResponse.path("choices")
                    .path(0)
                    .path("message")
                    .path("content")
                    .asText();
                return content;
            }

        } catch (Exception e) {
            return e.getMessage();
        }

        return "Sorry, couldn't generate content right now. Please try again.";
    }
} 