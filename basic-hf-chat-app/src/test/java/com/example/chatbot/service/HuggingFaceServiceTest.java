package com.example.chatbot.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class HuggingFaceServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private HuggingFaceService huggingFaceService;

    @Captor
    private ArgumentCaptor<HttpEntity<String>> httpEntityCaptor;

    private final String testApiKey = "test-api-key";
    private final String testApiUrl = "http://test-api-url";
    private final String testMessage = "Test message";

    @BeforeEach
    void setUp() {
        // Initialize the service with mock dependencies
        huggingFaceService = new HuggingFaceService(restTemplate, testApiKey, testApiUrl);
    }

    @Test
    void generateTextFromPrompt_Success() {
        // Arrange
        String expectedResponse = "{\"choices\":[{\"messages\":{\"content\":\"Test response\"}}]}";
        String expectedContent = "Test response";

        // Mock the response
        when(restTemplate.exchange(
                eq(testApiUrl),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(ResponseEntity.ok(expectedResponse));

        // Act
        String result = huggingFaceService.generateTextFromPrompt(testMessage);

        // Assert
        assertNotNull(result);
        assertEquals(expectedContent, result);
        
        verify(restTemplate).exchange(
            eq(testApiUrl),
            eq(HttpMethod.POST),
            httpEntityCaptor.capture(),
            eq(String.class)
        );
        
        HttpEntity<String> capturedEntity = httpEntityCaptor.getValue();
        assertNotNull(capturedEntity);
        assertEquals("Bearer " + testApiKey, capturedEntity.getHeaders().getFirst("Authorization"));
        assertTrue(capturedEntity.getBody().contains(testMessage));
    }

    @Test
    void generateTextFromPrompt_ErrorResponse() {
        // Mock the response
        when(restTemplate.exchange(
                eq(testApiUrl),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(ResponseEntity.internalServerError().build());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () ->
            huggingFaceService.generateTextFromPrompt(testMessage)
        );
        
        assertNotNull(exception);
        assertNotNull(exception.getMessage());
    }

    @Test
    void parseResponse_ValidResponse() {
        // Arrange
        String jsonResponse = "{\"choices\":[{\"messages\":{\"content\":\"Test response\"}}]}";
        String expected = "Test response";

        // Act
        String result = ReflectionTestUtils.invokeMethod(huggingFaceService, "parseResponse", jsonResponse);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void parseResponse_InvalidResponse() {
        // Arrange
        String invalidJson = "invalid json";

        // Act
        String result = ReflectionTestUtils.invokeMethod(huggingFaceService, "parseResponse", invalidJson);

        // Assert
        assertEquals("Error parsing response", result);
    }
}
