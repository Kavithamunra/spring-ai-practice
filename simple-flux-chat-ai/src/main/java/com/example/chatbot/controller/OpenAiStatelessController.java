package com.example.chatbot.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Controller for handling stateless chat interactions with OpenAI.
 * Each request is treated independently without maintaining conversation context.
 */
@RestController
public class OpenAiStatelessController {
    
    // ChatClient instance for interacting with the AI model
    private final ChatClient chatClient;

    /**
     * Constructs a new controller with the specified ChatClient builder.
     * 
     * @param builder The ChatClient builder for creating chat client instances
     */
    public OpenAiStatelessController(ChatClient.Builder builder) {
        // Initialize the chat client without any additional configuration
        this.chatClient = builder.build();
    }

    /**
     * Handles streaming chat requests with Server-Sent Events (SSE).
     * Each request is processed independently without maintaining conversation state.
     * 
     * @param message The user's message to process
     * @return A Flux of strings containing SSE-formatted responses
     */
    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE, value = "/no-state-query")
    public Flux<String> streamChat(@RequestParam String message) {
        // Process the message and stream the response as Server-Sent Events
        return chatClient.prompt(message)
                .stream()
                .content()
                // Format the response as Server-Sent Events
                .map(content -> "data: " + content + "\n\n");
    }

    /**
     * Handles non-streaming chat requests.
     * Each request is processed independently without maintaining conversation state.
     * 
     * @param message The user's message to process
     * @return A Mono containing the AI's response
     */
    @PostMapping(value = "/no-state-query")
    public Mono<String> chat(@RequestBody String message) {
        // Process the message and return a single response
        return Mono.fromCallable(() ->
                chatClient.prompt(message)
                        .call()
                        .content()
        );
    }
}

