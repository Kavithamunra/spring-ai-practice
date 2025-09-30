package com.example.chatbot.controller;

import com.example.chatbot.service.HuggingFaceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SimpleQuestionControllerTest {

    @Mock
    private HuggingFaceService huggingFaceService;

    @InjectMocks
    private SimpleQuestionController controller;

    @Test
    void generate_WithMessage_ReturnsGeneratedText() {
        // Arrange
        String testMessage = "Tell me a joke";
        String expectedResponse = "Why don't scientists trust atoms? Because they make up everything!";
        doReturn(expectedResponse).when(huggingFaceService).generateTextFromPrompt(testMessage);

        // Act
        Map<String, String> response = controller.generate(testMessage);

        // Assert
        assertNotNull(response);
        assertTrue(response.containsKey("generation"));
        assertEquals(expectedResponse, response.get("generation"));
        verify(huggingFaceService, times(1)).generateTextFromPrompt(testMessage);
    }

    @Test
    void generate_WithDefaultMessage_ReturnsGeneratedText() {
        // Arrange
        String expectedResponse = "Default joke response";
        doReturn(expectedResponse).when(huggingFaceService).generateTextFromPrompt("Tell me a joke");

        // Act
        Map<String, String> response = controller.generate("Tell me a joke");

        // Assert
        assertNotNull(response);
        assertTrue(response.containsKey("generation"));
        assertEquals(expectedResponse, response.get("generation"));
    }

    @Test
    void generate_WithEmptyMessage_ReturnsGeneratedText() {
        // Arrange
        String expectedResponse = "Empty message response";
        doReturn(expectedResponse).when(huggingFaceService).generateTextFromPrompt("");

        // Act
        Map<String, String> response = controller.generate("");

        // Assert
        assertNotNull(response);
        assertTrue(response.containsKey("generation"));
        assertEquals(expectedResponse, response.get("generation"));
    }

    @Test
    void generate_WithSpecialCharacters_HandlesCorrectly() {
        // Arrange
        String testMessage = "What's 2 + 2?";
        String expectedResponse = "The answer is 4";
        doReturn(expectedResponse).when(huggingFaceService).generateTextFromPrompt(testMessage);

        // Act
        Map<String, String> response = controller.generate(testMessage);

        // Assert
        assertNotNull(response);
        assertEquals(expectedResponse, response.get("generation"));
    }
}
