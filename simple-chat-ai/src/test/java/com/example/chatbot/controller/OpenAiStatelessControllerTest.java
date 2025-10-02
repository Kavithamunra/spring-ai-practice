package com.example.chatbot.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ai.chat.client.ChatClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class OpenAiStatelessControllerTest {

    @Mock
    private ChatClient.Builder chatClientBuilder;

    @Mock
    private ChatClient chatClient;

    @Mock
    private ChatClient.ChatClientRequestSpec promptRequest;

    @Mock
    private ChatClient.CallResponseSpec response;

    private OpenAiStatelessController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(chatClientBuilder.build()).thenReturn(chatClient);
        when(chatClient.prompt(anyString())).thenReturn(promptRequest);
        when(promptRequest.call()).thenReturn(response);
        when(response.content()).thenReturn("Test response");
        
        controller = new OpenAiStatelessController(chatClientBuilder);
    }

    @Test
    void testChat() {
        // Given
        String testQuery = "Hello, world!";
        String expectedResponse = "Test response";

        // When
        String actualResponse = controller.chat(testQuery);

        // Then
        assertEquals(expectedResponse, actualResponse);
        verify(chatClient).prompt(testQuery);
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
