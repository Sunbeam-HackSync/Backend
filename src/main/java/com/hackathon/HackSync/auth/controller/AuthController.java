package com.hackathon.HackSync.auth.controller;

import com.hackathon.HackSync.auth.entity.RefreshToken;
import com.hackathon.HackSync.auth.entity.Users;
import com.hackathon.HackSync.auth.responses.LoginResponse;
import com.hackathon.HackSync.auth.service.AuthenticationService;
import com.hackathon.HackSync.auth.service.JWTService;
import com.hackathon.HackSync.auth.service.RefreshTokenService;
import com.hackathon.HackSync.auth.dto.LoginRequestDto;
import com.hackathon.HackSync.auth.dto.RegistrationRequestDto;
import com.hackathon.HackSync.auth.dto.ResendOtpDto;
import com.hackathon.HackSync.auth.dto.VerifyOtpDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JWTService jwtService;
    private final AuthenticationService authenticationService;
    private final RefreshTokenService refreshTokenService;

    @Value("${security.jwt.refresh-token.expiration-time}")
    private int refreshTokenDurationMs;

    public AuthController(JWTService jwtService, AuthenticationService authenticationService,
            RefreshTokenService refreshTokenService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.refreshTokenService = refreshTokenService;
    }

    @GetMapping()
    public String welcomeText() {
        return "Welcome to the Authentication App";
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationRequestDto registerUserDto) {
        try {
            Users registeredUser = authenticationService.signUp(registerUserDto);
            return ResponseEntity.ok(registeredUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        try {
            Users user = authenticationService.signIn(loginRequestDto);
            String jwtToken = jwtService.generateToken(user);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

            setRefreshTokenCookie(response, refreshToken.getToken());

            LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());
            return ResponseEntity.ok(loginResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtpAndLogin(@RequestBody VerifyOtpDto verifyOtpDto, HttpServletResponse response) {
        try {
            Users user = authenticationService.verifyOtp(verifyOtpDto);
            String jwtToken = jwtService.generateToken(user);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

            setRefreshTokenCookie(response, refreshToken.getToken());

            LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());
            return ResponseEntity.ok(loginResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/resend")
    public ResponseEntity<?> resendVerificationCode(@RequestBody ResendOtpDto resendOtpDto) {
        try {
            authenticationService.resendOtp(resendOtpDto);
            return ResponseEntity.ok("OTP send to register mobile number");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(
            @CookieValue(name = "refresh_token", required = false) String refreshTokenString,
            HttpServletResponse response) {
        if (refreshTokenString == null) {
            return ResponseEntity.status(401).body("Refresh Token is missing");
        }

        try {
            RefreshToken refreshToken = refreshTokenService.findByToken(refreshTokenString)
                    .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));

            refreshToken = refreshTokenService.verifyExpirationAndRevocation(refreshToken);

            Users user = refreshToken.getUser();

            // Rotate refresh token
            RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user.getId());
            setRefreshTokenCookie(response, newRefreshToken.getToken());

            String jwtToken = jwtService.generateToken(user);
            LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());

            return ResponseEntity.ok(loginResponse);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(name = "refresh_token", required = false) String refreshTokenString,
            HttpServletResponse response) {
        if (refreshTokenString != null) {
            refreshTokenService.revokeToken(refreshTokenString);
        }

        Cookie cookie = new Cookie("refresh_token", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        // Using response.addHeader to manually set SameSite=Strict if preferred, but
        // basic clear is fine
        response.addCookie(cookie);

        return ResponseEntity.ok("Logged out successfully");
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("refresh_token", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(refreshTokenDurationMs / 1000);
        // Add explicit SameSite header since standard Cookie class doesn't support it
        // directly
        response.addHeader("Set-Cookie", String.format("%s=%s; Max-Age=%d; Path=/; Secure; HttpOnly; SameSite=Strict",
                cookie.getName(), cookie.getValue(), cookie.getMaxAge()));
    }
}
