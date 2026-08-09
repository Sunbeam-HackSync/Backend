package com.hackathon.HackSync.chat.dto;
import jakarta.validation.constraints.NotBlank;

public class ChatRequest {
    @NotBlank(message = "Message cannot be blank")
    private String message;

    public ChatRequest() {
    }

    public ChatRequest(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
