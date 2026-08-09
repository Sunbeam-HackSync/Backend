package com.hackathon.HackSync.genai;

import com.hackathon.HackSync.chat.dto.ChatRequest;
import com.hackathon.HackSync.chat.dto.ChatResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class GenAIClient {

    private final WebClient webClient;

    public GenAIClient(WebClient.Builder webClientBuilder, @Value("${genai.base-url}") String genAiBaseUrl) {
        this.webClient = webClientBuilder.baseUrl(genAiBaseUrl).build();
    }

    public ChatResponse chat(ChatRequest request, String authHeader) {

        return webClient
                .post()
                .uri("/chat")
                .contentType(MediaType.APPLICATION_JSON)


                .headers(headers -> {

                    if (authHeader != null
                            && !authHeader.isBlank()) {

                        headers.set(
                                HttpHeaders.AUTHORIZATION,
                                authHeader);
                    }
                })

                .bodyValue(request)

                .retrieve()

                .bodyToMono(ChatResponse.class)

                .block();
    }
}