package com.example.chatbot.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class OpenAiContextAwareControllerTest {

    @Mock
    private ChatClient.Builder chatClientBuilder;

    @Mock
    private ChatClient chatClient;

    @Mock
    private ChatClient.ChatClientRequestSpec promptRequest;

    @Mock
    private ChatClient.CallResponseSpec response;

    @Mock
    private ChatMemory chatMemory;

    @Mock
    private MessageChatMemoryAdvisor advisor;
    
    @Mock
    private ChatClient.AdvisorSpec advisorSpec;
    
    @Mock
    private ChatClient.StreamResponseSpec streamResponse;

    private OpenAiContextAwareController controller;
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Mock setup for non-streaming
        when(chatClientBuilder.defaultAdvisors(any(MessageChatMemoryAdvisor.class))).thenReturn(chatClientBuilder);
        when(chatClientBuilder.build()).thenReturn(chatClient);
        when(chatClient.prompt(anyString())).thenReturn(promptRequest);
        
        // Mock advisor setup
        when(promptRequest.advisors(any(Consumer.class))).thenAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            Consumer<ChatClient.AdvisorSpec> consumer = invocation.getArgument(0);
            when(advisorSpec.param(anyString(), any())).thenReturn(advisorSpec);
            consumer.accept(advisorSpec);
            return promptRequest;
        });
        
        // Mock streaming response
        when(promptRequest.stream()).thenReturn(streamResponse);
        when(streamResponse.content()).thenReturn(Flux.just("Hello", " ", "World!"));
        
        // Mock non-streaming response
        when(promptRequest.call()).thenReturn(response);
        when(response.content()).thenReturn("Hello World!");
        
        controller = new OpenAiContextAwareController(chatClientBuilder);
        webTestClient = WebTestClient.bindToController(controller).build();
    }

    @Test
    void testChat_WithValidMessage_ReturnsMonoWithResponse() {
        // Given
        String testMessage = "Hello";
        String expectedResponse = "Hello World!";

        // When & Then
        StepVerifier.create(controller.chat(testMessage))
                .expectNext(expectedResponse)
                .verifyComplete();

        verify(chatClient).prompt(testMessage);
        verify(promptRequest).advisors(any(Consumer.class));
        verify(promptRequest).call();
        verify(response).content();
    }

    @Test
    void testChat_WithEmptyMessage_ReturnsEmptyMono() {
        // Given
        String testMessage = "";
        when(response.content()).thenReturn("");

        // When & Then
        StepVerifier.create(controller.chat(testMessage))
                .expectNext("")
                .verifyComplete();

        verify(chatClient).prompt(testMessage);
    }

    @Test
    void testStreamChat_WithValidMessage_ReturnsFluxWithChunks() {
        // Given
        String testMessage = "Hello";
        
        // When
        Flux<String> result = controller.streamChat(testMessage);

        // Then
        StepVerifier.create(result)
                .expectNext("data: Hello\n\n")
                .expectNext("data:  \n\n")
                .expectNext("data: World!\n\n")
                .verifyComplete();

        verify(chatClient).prompt(testMessage);
        verify(promptRequest).advisors(any(Consumer.class));
        verify(promptRequest).stream();
        verify(streamResponse).content();
    }

    @Test
    void testStreamChat_WithEmptyMessage_ReturnsEmptyFlux() {
        // Given
        String testMessage = "";
        when(streamResponse.content()).thenReturn(Flux.empty());

        // When
        Flux<String> result = controller.streamChat(testMessage);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(chatClient).prompt(testMessage);
    }

    @Test
    void testStreamChat_WithWebTestClient_ReturnsServerSentEvents() {
        // Given
        String testMessage = "Hello";

        // When & Then
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/state-query")
                        .queryParam("message", testMessage)
                        .build())
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.TEXT_EVENT_STREAM_VALUE + ";charset=UTF-8")
                .returnResult(String.class)
                .consumeWith(response -> {
                    // Verify the response contains the expected content
                    List<String> responseList = response.getResponseBody()
                            .collectList()
                            .block();
                    assertNotNull(responseList);
                    
                    // Combine all chunks into a single string for easier assertion
                    String combinedResponse = String.join("", responseList);
                    
                    // Check if the response contains the expected content
                    assertTrue(combinedResponse.contains("Hello"), "Response should contain 'Hello'");
                    assertTrue(combinedResponse.contains("World!"), "Response should contain 'World!'");
                    
                    // Verify SSE format is correct
                    //assertTrue(combinedResponse.matches("(?s).*data: .*Hello.*\\n\\n.*"), "Response should be in SSE format");
                    //assertTrue(combinedResponse.matches("(?s).*data: .*World!.*\\n\\n.*"), "Response should be in SSE format");
                });
    }

    @Test
    void testChat_WithWebTestClient_ReturnsJsonResponse() {
        // Given
        String testMessage = "Hello";
        String expectedResponse = "Hello World!";

        // When & Then
        webTestClient.post()
                .uri("/state-query")
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue(testMessage)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo(expectedResponse);
    }
}
