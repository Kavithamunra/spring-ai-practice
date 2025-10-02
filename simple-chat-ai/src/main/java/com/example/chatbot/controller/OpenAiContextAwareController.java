package com.example.chatbot.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for handling context-aware chat interactions with OpenAI.
 * This controller maintains conversation history to provide context-aware responses.
 * It uses MessageWindowChatMemory to keep track of the last 2 messages in the conversation.
 * 
 * @author Your Name
 * @version 1.0
 */
@RestController
public class OpenAiContextAwareController {
    
    private final ChatClient chatClient;
    //we can use a user id for conversation id, to remember per user conversation
    private static final String CONVERSATION_ID = "12345";

    /**
     * Constructs a new OpenAiContextAwareController with the specified ChatClient builder.
     * Initializes a chat memory that retains the last 2 messages in the conversation.
     *
     * @param builder The ChatClient builder for creating chat client instances
     */
    public OpenAiContextAwareController(ChatClient.Builder builder) {
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .maxMessages(2)  // Keep the last 2 messages for context
                .build();
                
        this.chatClient = builder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    /**
     * Handles chat requests with conversation context.
     * Maintains the conversation state using the configured chat memory.
     *
     * @param query The user's message/query
     * @return The AI's response as a String
     */
    @GetMapping(value = "/state-query")
    public String chat(@RequestParam String query) {
        return chatClient.prompt(query)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, CONVERSATION_ID))
                .call()
                .content();
    }
}
