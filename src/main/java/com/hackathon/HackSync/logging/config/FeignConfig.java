package com.hackathon.HackSync.logging.config;

import feign.Logger;
import feign.codec.ErrorDecoder;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(FeignConfig.class);

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            log.error(".NET Logger service error on method {}. HTTP Status: {}", methodKey, response.status());
            return new RuntimeException(".NET Logger service call failed with HTTP status " + response.status());
        };
    }
}