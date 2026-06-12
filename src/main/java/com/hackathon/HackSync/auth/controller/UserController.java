package com.hackathon.HackSync.auth.controller;

import com.hackathon.HackSync.auth.entity.Users;
import com.hackathon.HackSync.auth.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/profiles")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/me")
    public ResponseEntity<?> authenticateUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof Users users) {
            return ResponseEntity.ok(
                    Map.of(
                            "username", users.getUsername(),
                            "roles", users.getAuthorities()
                    )
            );
        }
        return ResponseEntity.badRequest().body("Unexpected principal type");
    }

}
