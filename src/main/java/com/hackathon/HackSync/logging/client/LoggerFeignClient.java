package com.hackathon.HackSync.logging.client;

import com.hackathon.HackSync.logging.config.FeignConfig;
import com.hackathon.HackSync.logging.dto.LogEntryDto;
import com.hackathon.HackSync.logging.dto.LogResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "dotnet-logger", url = "${dotnet.logger.url:http://localhost:5057}", configuration = FeignConfig.class)
public interface LoggerFeignClient {

    @PostMapping("/api/logs")
    LogResponseDto sendLog(@RequestBody LogEntryDto logEntry);

    @GetMapping("/api/logs")
    List<LogResponseDto> getLogs();
}