package com.hackathon.HackSync.auth.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;

public class CurrentJwtService {
    public String getBearerToken(HttpServletRequest request) {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header == null ||
                !header.startsWith("Bearer ")) {

            return null;
        }

        return header.substring(7);
    }

}
