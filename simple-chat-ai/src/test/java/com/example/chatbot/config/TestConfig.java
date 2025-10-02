package com.example.chatbot.config;

import org.mockito.Mockito;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class TestConfig {

    @Bean
    @Primary
    public ChatClient.Builder testChatClientBuilder() {
        return Mockito.mock(ChatClient.Builder.class, Mockito.RETURNS_DEEP_STUBS);
    }
}
