package com.hackathon.HackSync.auth.controller;

import com.hackathon.HackSync.auth.entity.Users;
import com.hackathon.HackSync.auth.responses.LoginResponse;
import com.hackathon.HackSync.auth.service.AuthenticationService;
import com.hackathon.HackSync.auth.service.JWTService;
import com.hackathon.HackSync.auth.dto.LoginRequestDto;
import com.hackathon.HackSync.auth.dto.RegistrationRequestDto;
import com.hackathon.HackSync.auth.dto.ResendOtpDto;
import com.hackathon.HackSync.auth.dto.VerifyOtpDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JWTService jwtService;
    private final AuthenticationService authenticationService;

    public AuthController(JWTService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
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
    public ResponseEntity<?> authenticate(@RequestBody LoginRequestDto loginRequestDto) {
        try {
            String jwtToken = authenticationService.signIn(loginRequestDto);
            LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());
            return ResponseEntity.ok(loginResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtpAndLogin(@RequestBody VerifyOtpDto verifyOtpDto) {
        try {
            String jwtToken = authenticationService.verifyOtp(verifyOtpDto);
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
}
