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
import com.hackathon.HackSync.utils.dto.ApiResponse;
import com.hackathon.HackSync.utils.dto.ErrorResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JWTService jwtService;
    private final AuthenticationService authenticationService;
    private final RefreshTokenService refreshTokenService;

    @Value("${security.jwt.refresh-token.expiration-time}")
    private int refreshTokenDurationMs;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Users>> register(@RequestBody RegistrationRequestDto registerUserDto) {
        Users registeredUser = authenticationService.signUp(registerUserDto);
        return ResponseEntity.ok(new ApiResponse<>("User registered successfully", HttpStatus.OK, registeredUser));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> authenticate(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        Users user = authenticationService.signIn(loginRequestDto);
        String jwtToken = jwtService.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        setRefreshTokenCookie(response, refreshToken.getToken());

        LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());
        return ResponseEntity.ok(new ApiResponse<>("Login successful", HttpStatus.OK, loginResponse));
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<LoginResponse>> verifyOtpAndLogin(@RequestBody VerifyOtpDto verifyOtpDto, HttpServletResponse response) {
        Users user = authenticationService.verifyOtp(verifyOtpDto);
        String jwtToken = jwtService.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        setRefreshTokenCookie(response, refreshToken.getToken());

        LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());
        return ResponseEntity.ok(new ApiResponse<>("OTP verified successfully", HttpStatus.OK, loginResponse));
    }

    @PostMapping("/resend")
    public ResponseEntity<ApiResponse<Object>> resendVerificationCode(@RequestBody ResendOtpDto resendOtpDto) {
        authenticationService.resendOtp(resendOtpDto);
        return ResponseEntity.ok(new ApiResponse<>("OTP sent to register mobile number", HttpStatus.OK, null));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(
            @CookieValue(name = "refresh_token", required = false) String refreshTokenString,
            HttpServletResponse response) {
        if (refreshTokenString == null) {
            throw new RuntimeException("Refresh Token is missing");
        }

        RefreshToken refreshToken = refreshTokenService.findByToken(refreshTokenString)
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));

        refreshToken = refreshTokenService.verifyExpirationAndRevocation(refreshToken);

        Users user = refreshToken.getUser();

        // Rotate refresh token
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user.getId());
        setRefreshTokenCookie(response, newRefreshToken.getToken());

        String jwtToken = jwtService.generateToken(user);
        LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());

        return ResponseEntity.ok(new ApiResponse<>("Token refreshed successfully", HttpStatus.OK, loginResponse));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Object>> logout(@CookieValue(name = "refresh_token", required = false) String refreshTokenString,
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

        return ResponseEntity.ok(new ApiResponse<>("Logged out successfully", HttpStatus.OK, null));
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
