package com.hackathon.HackSync.chat.controller;

import com.hackathon.HackSync.chat.dto.ChatRequest;
import com.hackathon.HackSync.chat.dto.ChatResponse;
import com.hackathon.HackSync.chat.service.ChatService;

import jakarta.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ResponseEntity<ChatResponse> chat(
            @Valid @RequestBody ChatRequest request,

            @RequestHeader(
                    value = HttpHeaders.AUTHORIZATION,
                    required = false
            )
            String authHeader
    ) {

        ChatResponse response =
                chatService.chat(
                        request,
                        authHeader
                );

        return ResponseEntity.ok(response);
    }
}