package com.hackathon.HackSync.logging.service;

import com.hackathon.HackSync.logging.client.LoggerFeignClient;
import com.hackathon.HackSync.logging.dto.LogEntryDto;
import com.hackathon.HackSync.logging.dto.LogResponseDto;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class LogProxyService {

    private static final Logger log = LoggerFactory.getLogger(LogProxyService.class);
    private final LoggerFeignClient loggerFeignClient;

    public LogProxyService(LoggerFeignClient loggerFeignClient) {
        this.loggerFeignClient = loggerFeignClient;
    }

    /**
     * Proxies a log payload to the .NET logging service.
     */
    public LogResponseDto log(LogEntryDto payload) {
        return proxyLog("POST", null, payload);
    }

    /**
     * Proxies a log with specified HTTP method context (POST, PUT, PATCH).
     */
    public LogResponseDto proxyLog(String httpMethod, LogEntryDto payload) {
        return proxyLog(httpMethod, null, payload);
    }

    /**
     * Proxies a log with HTTP method and resource ID context.
     */
    public LogResponseDto proxyLog(String httpMethod, String resourceId, LogEntryDto payload) {
        try {
            if (payload == null) {
                throw new IllegalArgumentException("Log payload must not be null");
            }

            // Normalize severity
            if (payload.getSeverity() != null) {
                payload.setSeverity(payload.getSeverity().trim().toUpperCase());
            } else {
                payload.setSeverity("INFO");
            }

            LogResponseDto response = loggerFeignClient.sendLog(payload);
            log.info("Successfully proxied [{}] log for resource [{}] to .NET service (userId={})",
                    httpMethod != null ? httpMethod : "POST",
                    resourceId != null ? resourceId : "N/A",
                    payload.getUserId());
            return response;
        } catch (FeignException ex) {
            log.error("Failed to forward [{}] log to .NET service via OpenFeign: {}", httpMethod, ex.getMessage());
            throw new RuntimeException("Failed to forward log to .NET logging service: " + ex.getMessage(), ex);
        }
    }

    /**
     * Direct helpers for logging POST actions.
     */
    public LogResponseDto logPost(String service, String message) {
        return logPost(service, message, (Long) null);
    }

    public LogResponseDto logPost(String service, String message, Long userId) {
        return proxyLog("POST", new LogEntryDto(userId, service, "INFO", message));
    }

    /**
     * Direct helpers for logging PUT actions.
     */
    public LogResponseDto logPut(String service, String message) {
        return logPut(service, message, (Long) null);
    }

    public LogResponseDto logPut(String service, String message, Long userId) {
        return proxyLog("PUT", new LogEntryDto(userId, service, "INFO", message));
    }

    /**
     * Direct helpers for logging PATCH actions.
     */
    public LogResponseDto logPatch(String service, String message) {
        return logPatch(service, message, (Long) null);
    }

    public LogResponseDto logPatch(String service, String message, Long userId) {
        return proxyLog("PATCH", new LogEntryDto(userId, service, "INFO", message));
    }

    /**
     * Generalized helper for logging any operation with specified severity and userId.
     */
    public LogResponseDto log(String httpMethod, String service, String severity, String message, Long userId) {
        return proxyLog(httpMethod, new LogEntryDto(userId, service, severity, message));
    }

    /**
     * Fetch logs from the .NET logging service.
     */
    public List<LogResponseDto> getLogs() {
        try {
            return loggerFeignClient.getLogs();
        } catch (FeignException ex) {
            log.error("Failed to retrieve logs from .NET service via OpenFeign: {}", ex.getMessage());
            return Collections.emptyList();
        }
    }
}