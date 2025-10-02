package com.example.chatbot.controller;

import com.example.chatbot.tools.InformationDesk;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Controller for handling smart chat interactions with OpenAI.
 * Extends the context-aware controller with additional tools and capabilities.
 */
@RestController
public class OpenAiSmartController {
    
    // ChatClient instance for interacting with the AI model
    private final ChatClient chatClient;
    
    // Conversation ID used to maintain chat context
    private static final String CONVERSATION_ID = "12345";
    
    // System message to guide AI behavior
    private static final String SYSTEM_MESSAGE = 
            "Use all tools available when you can't figure out answer and provide approximate answer if you don't know the answer";

    /**
     * Constructs a new smart controller with the specified ChatClient builder.
     * Initializes chat memory and configures available tools.
     * 
     * @param builder The ChatClient builder for creating chat client instances
     */
    public OpenAiSmartController(ChatClient.Builder builder) {
        // Initialize chat memory to maintain the last 2 messages for context
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .maxMessages(2)
                .build();
                
        // Configure the chat client with memory advisor and tools
        this.chatClient = builder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .defaultTools(new InformationDesk())
                .build();
    }

    /**
     * Handles streaming chat requests with Server-Sent Events (SSE).
     * Uses tools and maintains conversation context for smarter responses.
     * 
     * @param message The user's message to process
     * @return A Flux of strings containing SSE-formatted responses
     */
    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE, value = "/smart-query")
    public Flux<String> streamChat(@RequestParam String message) {
        return chatClient.prompt(message)
                // Associate the request with a conversation for context
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, CONVERSATION_ID))
                // Set system message to guide AI behavior
                .system(SYSTEM_MESSAGE)
                // Stream the response
                .stream()
                .content()
                // Format the response as Server-Sent Events
                .map(content -> "data: " + content + "\n\n");
    }

    /**
     * Handles non-streaming chat requests.
     * Uses tools and maintains conversation context for smarter responses.
     * 
     * @param message The user's message to process
     * @return A Mono containing the AI's response
     */
    @PostMapping(value = "/smart-query")
    public Mono<String> chat(@RequestBody String message) {
        return Mono.fromCallable(() ->
                chatClient.prompt(message)
                        // Associate the request with a conversation for context
                        .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, CONVERSATION_ID))
                        // Set system message to guide AI behavior
                        .system(SYSTEM_MESSAGE)
                        // Get the response
                        .call()
                        .content()
        );
    }
}