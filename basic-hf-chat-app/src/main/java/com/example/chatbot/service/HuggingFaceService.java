package com.example.chatbot.service;

import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HuggingFaceService {

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String apiUrl;

    public HuggingFaceService(
            RestTemplate restTemplate,
            @Value("${spring.ai.huggingface.chat.api-key}") String apiKey,
            @Value("${spring.ai.huggingface.chat.url}") String apiUrl) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
    }

    public String generateTextFromPrompt(String message) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Content-Type", "application/json");
        Prompt prompt = new Prompt(message);
        String requestBody = "{\"messages\": [{\"role\": \"user\", \"content\": \"" + prompt.getInstructions() + "\"}], \"model\": \"HuggingFaceTB/SmolLM3-3B:hf-inference\",\"stream\": false}";
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);

        // Parse the response (Hugging Face returns JSON like [{"generated_text": "..."}])
        return parseResponse(response.getBody().toString());
    }

    private String parseResponse(String responseBody) {
        // Basic parsing; adjust based on actual response structure
        return responseBody.contains("content") ? responseBody.split("\"content\":\"")[1].split("\"")[0] : "Error parsing response";
    }
}

