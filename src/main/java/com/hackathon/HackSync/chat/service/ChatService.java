package com.hackathon.HackSync.chat.service;

import com.hackathon.HackSync.chat.dto.ChatRequest;
import com.hackathon.HackSync.chat.dto.ChatResponse;
import com.hackathon.HackSync.genai.GenAIClient;

import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final GenAIClient genAIClient;

    public ChatService(GenAIClient genAIClient) {
        this.genAIClient = genAIClient;
    }

    /**
     * Processes a chatbot request and forwards it
     * to the GenAI service.
     */
    public ChatResponse chat(
            ChatRequest request,
            String authHeader
    ) {

        return genAIClient.chat(
                request,
                authHeader
        );
    }
}