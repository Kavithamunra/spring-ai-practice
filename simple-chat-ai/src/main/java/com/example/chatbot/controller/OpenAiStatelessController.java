package com.example.chatbot.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for handling stateless chat interactions with OpenAI.
 * This controller treats each request independently without maintaining any conversation history.
 * It's ideal for simple question-answer scenarios where context from previous interactions is not required.
 * 
 * @author Your Name
 * @version 1.0
 */
@RestController
public class OpenAiStatelessController {
    
    private final ChatClient chatClient;

    /**
     * Constructs a new OpenAiStatelessController with the specified ChatClient builder.
     * Initializes a stateless chat client without any conversation memory.
     *
     * @param builder The ChatClient builder for creating chat client instances
     */
    public OpenAiStatelessController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    /**
     * Handles stateless chat requests.
     * Each query is processed independently without any context from previous interactions.
     *
     * @param query The user's message/query
     * @return The AI's response as a String
     */
    @GetMapping(value = "/no-state-query")
    public String chat(@RequestParam String query) {
        return chatClient.prompt(query)
                .call()
                .content();
    }
}
