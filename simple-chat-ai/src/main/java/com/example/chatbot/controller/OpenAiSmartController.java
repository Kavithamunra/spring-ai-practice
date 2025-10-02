package com.example.chatbot.controller;

import com.example.chatbot.tools.InformationDesk;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for handling advanced chat interactions with OpenAI.
 * This controller provides enhanced functionality with conversation memory and tool integration.
 * It's designed for complex interactions where the AI can use additional tools and maintain context.
 * 
 * @author Your Name
 * @version 1.0
 */
@RestController
public class OpenAiSmartController {
    
    private static final String CONVERSATION_ID = "12345";
    private static final String SYSTEM_PROMPT = 
            "Use all tools available when you can't figure out answer and provide approximate answer if you don't know the answer";
    
    private final ChatClient chatClient;

    /**
     * Constructs a new OpenAiSmartController with the specified ChatClient builder.
     * Initializes the chat client with conversation memory and custom tools.
     *
     * @param builder The ChatClient builder for creating chat client instances
     */
    public OpenAiSmartController(ChatClient.Builder builder) {
        // Initialize chat memory to keep track of the last 2 messages
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .maxMessages(2)
                .build();
                
        // Build the chat client with memory advisor and custom tools
        this.chatClient = builder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .defaultTools(new InformationDesk())
                .build();
    }

    /**
     * Handles smart chat requests with enhanced capabilities.
     * Maintains conversation context and can utilize additional tools when needed.
     *
     * @param query The user's message/query
     * @return The AI's response as a String, potentially enhanced with tool usage
     */
    @GetMapping(value = "/smart-query")
    public String chat(@RequestParam String query) {
        return chatClient.prompt(query)
                .system(SYSTEM_PROMPT)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, CONVERSATION_ID))
                .call()
                .content();
    }
}
