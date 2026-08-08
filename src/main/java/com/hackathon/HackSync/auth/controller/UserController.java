package com.hackathon.HackSync.auth.controller;

import com.hackathon.HackSync.auth.entity.Users;
import com.hackathon.HackSync.auth.service.UserService;
import com.hackathon.HackSync.utils.dto.ApiResponse;
import com.hackathon.HackSync.utils.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/profiles")
public class UserController {

    @GetMapping("/me")
    public ResponseEntity<?> authenticateUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Unauthorized", HttpStatus.UNAUTHORIZED));
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof Users users) {
            return ResponseEntity.ok(
                    new ApiResponse<>("Profile fetched successfully", HttpStatus.OK,
                            Map.of(
                                    "username", users.getUsername(),
                                    "roles", users.getAuthorities()
                            )
                    )
            );
        }
        return ResponseEntity.badRequest().body(new ErrorResponse("Unexpected principal type", HttpStatus.BAD_REQUEST));
    }

}
