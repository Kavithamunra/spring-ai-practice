package com.example.chatbot.controller;

import com.example.chatbot.tools.InformationDesk;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class OpenAiSmartControllerTest {

    @Mock
    private ChatClient.Builder chatClientBuilder;

    @Mock
    private ChatClient chatClient;

    @Mock
    private ChatClient.ChatClientRequestSpec promptRequest;

    @Mock
    private ChatClient.CallResponseSpec response;

    @Mock
    private InformationDesk informationDesk;

    @Mock
    private ChatMemory chatMemory;

    private OpenAiSmartController controller;

    @Mock
    private ChatClient.AdvisorSpec advisorSpec;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        when(chatClientBuilder.defaultAdvisors(any(MessageChatMemoryAdvisor.class)))
                .thenReturn(chatClientBuilder);
        when(chatClientBuilder.defaultTools(any(InformationDesk.class)))
                .thenReturn(chatClientBuilder);
        when(chatClientBuilder.build())
                .thenReturn(chatClient);
        when(chatClient.prompt(anyString()))
                .thenReturn(promptRequest);
        when(promptRequest.system(anyString()))
                .thenReturn(promptRequest);
        when(promptRequest.advisors(any(Consumer.class))).thenAnswer(invocation -> {
            // Get the consumer and execute it with the advisor spec
            @SuppressWarnings("unchecked")
            Consumer<ChatClient.AdvisorSpec> consumer = invocation.getArgument(0);
            when(advisorSpec.param(anyString(), any())).thenReturn(advisorSpec);
            consumer.accept(advisorSpec);
            return promptRequest;
        });
        when(promptRequest.call())
                .thenReturn(response);
        when(response.content())
                .thenReturn("Smart response with tools");
        
        controller = new OpenAiSmartController(chatClientBuilder);
    }

    @Test
    void testChat() {
        // Given
        String testQuery = "What's the weather like?";
        String expectedResponse = "Smart response with tools";

        // When
        String actualResponse = controller.chat(testQuery);

        // Then
        assertEquals(expectedResponse, actualResponse);
        verify(chatClient).prompt(testQuery);
        verify(promptRequest).system(anyString());
        verify(promptRequest).call();
        verify(response).content();
    }

    @Test
    void testChatWithEmptyQuery() {
        // Given
        String testQuery = "";
        String expectedResponse = "";
        when(response.content()).thenReturn("");

        // When
        String actualResponse = controller.chat(testQuery);

        // Then
        assertEquals(expectedResponse, actualResponse);
        verify(chatClient).prompt(testQuery);
    }
}
