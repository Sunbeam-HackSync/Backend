package com.hackathon.HackSync.logging.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LogResponseDto {

    private Long id;
    private String service;
    private String severity;
    private String message;
    private Long userId;
    private String createdAt;

    public LogResponseDto() {
    }

    public LogResponseDto(Long id, String service, String severity, String message, Long userId, String createdAt) {
        this.id = id;
        this.service = service;
        this.severity = severity;
        this.message = message;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "LogResponseDto{" +
                "id=" + id +
                ", service='" + service + '\'' +
                ", severity='" + severity + '\'' +
                ", message='" + message + '\'' +
                ", userId=" + userId +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
