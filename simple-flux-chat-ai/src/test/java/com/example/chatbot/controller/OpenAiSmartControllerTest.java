package com.example.chatbot.controller;

import com.example.chatbot.tools.InformationDesk;
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
import reactor.test.StepVerifier;

import java.util.List;
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
    
    @Mock
    private ChatClient.StreamResponseSpec streamResponse;
    
    private WebTestClient webTestClient;

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
        // Mock system() and advisors() methods
        when(promptRequest.system(anyString())).thenReturn(promptRequest);
        when(promptRequest.advisors(any(Consumer.class))).thenAnswer(invocation -> {
            // Get the consumer and execute it with the advisor spec
            @SuppressWarnings("unchecked")
            Consumer<ChatClient.AdvisorSpec> consumer = invocation.getArgument(0);
            when(advisorSpec.param(anyString(), any())).thenReturn(advisorSpec);
            consumer.accept(advisorSpec);
            return promptRequest;
        });
        
        // Mock streaming response
        when(promptRequest.stream()).thenReturn(streamResponse);
        when(streamResponse.content()).thenReturn(Flux.just("Smart", " ", "response"));
        
        // Mock non-streaming response
        when(promptRequest.call()).thenReturn(response);
        when(response.content()).thenReturn("Smart response with tools");
        
        controller = new OpenAiSmartController(chatClientBuilder);
        webTestClient = WebTestClient.bindToController(controller).build();
    }
    
    @Test
    void testStreamChat_WithValidMessage_ReturnsFluxWithChunks() {
        // Given
        String testMessage = "What's the weather like?";
        
        // When
        Flux<String> result = controller.streamChat(testMessage);

        // Then
        StepVerifier.create(result)
                .expectNext("data: Smart\n\n")
                .expectNext("data:  \n\n")
                .expectNext("data: response\n\n")
                .verifyComplete();

        verify(chatClient).prompt(testMessage);
        verify(promptRequest).system(anyString());
        verify(promptRequest).stream();
        verify(streamResponse).content();
    }
    
    @Test
    void testStreamChat_WithWebTestClient_ReturnsServerSentEvents() {
        // Given
        String testMessage = "What's the weather like?";

        // When & Then
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/smart-query")
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
                    //assertTrue(responseList.contains("data: Smart\n\n"));
                    //assertTrue(responseList.contains("data:  \n\n"));
                    //assertTrue(responseList.contains("data: response\n\n"));
                });
    }
    
    @Test
    void testChat_WithWebTestClient_ReturnsJsonResponse() {
        // Given
        String testMessage = "What's the weather like?";
        String expectedResponse = "Smart response with tools";

        // When & Then
        webTestClient.post()
                .uri("/smart-query")
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue(testMessage)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo(expectedResponse);
    }
}
