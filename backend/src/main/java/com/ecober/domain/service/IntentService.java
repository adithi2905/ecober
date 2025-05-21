package com.ecober.domain.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class IntentService {

    @Value("${openrouter.api.key}")
    private String openRouterApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String getIntent(String userMessage) {
        String prompt = String.format("""
                You are an intent extraction bot for Ecober.

                Supported intents:
                - get_last_ride_emission
                - get_weekly_emission
                - get_monthly_emission
                - get_all_trips
                - unknown

                Respond only in JSON like:
                { "intent": "get_weekly_emission", "period": "last week" }

                User: %s
            """, userMessage);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(openRouterApiKey); 
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Title", "Ecober Chatbot");
        headers.set("HTTP-Referer", "http://localhost:3000");

        Map<String, Object> body = Map.of(
            "model", "openai/gpt-3.5-turbo",
            "messages", List.of(
                Map.of("role", "user", "content", prompt)
            )
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
            "https://openrouter.ai/api/v1/chat/completions",
            request,
            Map.class
        );

        try {
            Map<String, Object> message = (Map<String, Object>)
                ((Map<String, Object>) ((List<?>) response.getBody().get("choices")).get(0)).get("message");

            return message.get("content").toString();
        } catch (Exception e) {
            return "{ \"intent\": \"unknown\" }";
        }
    }
}
