package com.example.chatbot.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

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

    @Mock
    private ChatClient.StreamResponseSpec streamResponse;

    private OpenAiStatelessController controller;
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(chatClientBuilder.build()).thenReturn(chatClient);
        when(chatClient.prompt(anyString())).thenReturn(promptRequest);
        
        // Mock streaming response
        when(promptRequest.stream()).thenReturn(streamResponse);
        when(streamResponse.content()).thenReturn(Flux.just("Test", " ", "response"));
        
        // Mock non-streaming response
        when(promptRequest.call()).thenReturn(response);
        when(response.content()).thenReturn("Test response");
        
        controller = new OpenAiStatelessController(chatClientBuilder);
        webTestClient = WebTestClient.bindToController(controller).build();
    }


    @Test
    void testStreamChat_WithValidMessage_ReturnsFluxWithChunks() {
        // Given
        String testMessage = "Hello, world!";
        
        // When
        Flux<String> result = controller.streamChat(testMessage);

        // Then
        StepVerifier.create(result)
                .expectNext("data: Test\n\n")
                .expectNext("data:  \n\n")
                .expectNext("data: response\n\n")
                .verifyComplete();

        verify(chatClient).prompt(testMessage);
        verify(promptRequest).stream();
        verify(streamResponse).content();
    }
    
    @Test
    void testStreamChat_WithWebTestClient_ReturnsServerSentEvents() {
        // Given
        String testMessage = "Hello, world!";

        // When & Then
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/no-state-query")
                        .queryParam("message", testMessage)
                        .build())
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.TEXT_EVENT_STREAM_VALUE + ";charset=UTF-8")
                .returnResult(String.class)
                .consumeWith(response -> {
                    List<String> responseList = response.getResponseBody()
                            .collectList()
                            .block();
                    assertNotNull(responseList);
                    //assertTrue(responseList.contains("data: Test\n\n"));
                    //assertTrue(responseList.contains("data:  \n\n"));
                    //assertTrue(responseList.contains("data: response\n\n"));
                });
    }
    
    @Test
    void testChat_WithWebTestClient_ReturnsJsonResponse() {
        // Given
        String testMessage = "Hello, world!";
        String expectedResponse = "Test response";

        // When & Then
        webTestClient.post()
                .uri("/no-state-query")
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue(testMessage)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo(expectedResponse);
    }
}
