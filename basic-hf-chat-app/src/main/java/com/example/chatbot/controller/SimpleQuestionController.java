package com.example.chatbot.controller;

import com.example.chatbot.service.HuggingFaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class SimpleQuestionController {

    @Autowired
    private HuggingFaceService huggingFaceService;

    @GetMapping("/ai/generate")
    public Map<String, String> generate(@RequestParam(value = "ask", defaultValue = "Tell me a joke") String message) {
        String generatedText = huggingFaceService.generateTextFromPrompt(message);
        return Map.of("generation", generatedText);
    }

}
