package com.example.chatbot.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Controller for handling context-aware chat interactions with OpenAI.
 * Maintains conversation state to provide contextual responses.
 */
@RestController
public class OpenAiContextAwareController {
    
    // ChatClient instance for interacting with the AI model
    private final ChatClient chatClient;
    
    // Conversation ID used to maintain chat context
    private static final String CONVERSATION_ID = "12345";

    /**
     * Constructs a new controller with the specified ChatClient builder.
     * Initializes chat memory to maintain conversation context.
     * 
     * @param builder The ChatClient builder for creating chat client instances
     */
    public OpenAiContextAwareController(ChatClient.Builder builder) {
        // Initialize chat memory to maintain the last 4 messages for context
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .maxMessages(4)
                .build();
                
        // Configure the chat client with memory advisor to maintain conversation state
        this.chatClient = builder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    /**
     * Handles streaming chat requests with Server-Sent Events (SSE).
     * Returns a stream of responses for real-time updates.
     * 
     * @param message The user's message to process
     * @return A Flux of strings containing SSE-formatted responses
     */
    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE, value = "/state-query")
    public Flux<String> streamChat(@RequestParam String message) {
        return chatClient.prompt(message)
                // Associate the request with a conversation for context
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, CONVERSATION_ID))
                .stream()
                .content()
                // Format the response as Server-Sent Events
                .map(content -> "data: " + content + "\n\n");
    }

    /**
     * Handles non-streaming chat requests.
     * Returns a single response for the given message.
     * 
     * @param message The user's message to process
     * @return A Mono containing the AI's response
     */
    @PostMapping(value = "/state-query")
    public Mono<String> chat(@RequestBody String message) {
        return Mono.fromCallable(() ->
                chatClient.prompt(message)
                        // Associate the request with a conversation for context
                        .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, CONVERSATION_ID))
                        .call()
                        .content()
        );
    }
}


