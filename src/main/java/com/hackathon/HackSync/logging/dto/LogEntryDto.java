package com.hackathon.HackSync.logging.dto;

public class LogEntryDto {

    private Long userId;
    private String service;
    private String severity;
    private String message;

    public LogEntryDto() {
    }

    public LogEntryDto(Long userId, String service, String severity, String message) {
        this.userId = userId;
        this.service = service;
        this.severity = severity;
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
}