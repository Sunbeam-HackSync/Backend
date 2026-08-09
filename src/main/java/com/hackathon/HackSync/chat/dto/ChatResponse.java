package com.hackathon.HackSync.chat.dto;

public class ChatResponse {
    private String message;
    private String content;

    public ChatResponse() {
    }

    public ChatResponse(String message, String content) {
        this.message = message;
        this.content = content;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
