package com.hackathon.HackSync.genai;

import com.hackathon.HackSync.chat.dto.ChatRequest;
import com.hackathon.HackSync.chat.dto.ChatResponse;
import com.hackathon.HackSync.genai.dto.SummarizeRequestDTO;
import com.hackathon.HackSync.genai.dto.SummarizeResponseDTO;

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

    public SummarizeResponseDTO summarize(SummarizeRequestDTO request, String authHeader) {
        return webClient
                .post()
                .uri("/summarize")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers -> {
                    if (authHeader != null && !authHeader.isBlank()) {
                        headers.set(HttpHeaders.AUTHORIZATION, authHeader);
                    }
                })
                .bodyValue(request)
                .retrieve()
                .bodyToMono(SummarizeResponseDTO.class)
                .block();
    }

    public com.hackathon.HackSync.genai.dto.DescriptionResponseDTO generateDescription(com.hackathon.HackSync.genai.dto.DescriptionRequestDTO request, String authHeader) {
        return webClient
                .post()
                .uri("/description")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers -> {
                    if (authHeader != null && !authHeader.isBlank()) {
                        headers.set(HttpHeaders.AUTHORIZATION, authHeader);
                    }
                })
                .bodyValue(request)
                .retrieve()
                .bodyToMono(com.hackathon.HackSync.genai.dto.DescriptionResponseDTO.class)
                .block();
    }
}