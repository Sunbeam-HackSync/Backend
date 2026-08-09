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

    public GenAIClient(
            WebClient.Builder webClientBuilder,
            @Value("${genai.base-url}") String genAiBaseUrl) {
        this.webClient = webClientBuilder
                .baseUrl(genAiBaseUrl)
                .build();
    }

    /**
     * Sends the chatbot request to the FastAPI GenAI service.
     *
     * @param request    chatbot request
     * @param authHeader original Authorization header.
     *                   Can be null for public/anonymous requests.
     */
    public ChatResponse chat(
            ChatRequest request,
            String authHeader) {

        return webClient
                .post()
                .uri("/chat")
                .contentType(MediaType.APPLICATION_JSON)

                /*
                 * Forward the user's JWT only when one exists.
                 *
                 * Anonymous request:
                 *
                 * authHeader == null
                 *
                 * Authenticated request:
                 *
                 * Authorization: Bearer <JWT>
                 */
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