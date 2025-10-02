package com.example.chatbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Simple Chat with Flux application.
 * <p>
 * This Spring Boot application provides a reactive chat interface with different modes:
 * - Stateless chat: Each request is independent
 * - Context-aware chat: Maintains conversation context
 * - Smart chat: Uses tools and maintains context for more advanced interactions
 * 
 * The application exposes REST endpoints for both streaming (SSE) and non-streaming chat interactions.
 */
@SpringBootApplication
public class SimpleChatFluxApplication {

    /**
     * Main method to start the Spring Boot application.
     *
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(SimpleChatFluxApplication.class, args);
    }
}
