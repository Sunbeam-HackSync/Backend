package com.hackathon.HackSync.auth.service;

import com.hackathon.HackSync.auth.dto.LoginRequestDto;
import com.hackathon.HackSync.auth.dto.RegistrationRequestDto;
import com.hackathon.HackSync.auth.dto.ResendOtpDto;
import com.hackathon.HackSync.auth.dto.VerifyOtpDto;
import com.hackathon.HackSync.auth.entity.OTPVerification;
import com.hackathon.HackSync.auth.entity.Users;
import com.hackathon.HackSync.auth.repository.OTPRepository;
import com.hackathon.HackSync.auth.repository.UserRepository;
import com.hackathon.HackSync.utils.service.EmailService;
import com.hackathon.HackSync.utils.exception.AlreadyVerifiedException;
import com.hackathon.HackSync.utils.exception.InvalidOTPException;
import com.hackathon.HackSync.utils.exception.ResourceNotFoundException;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final OTPRepository otpRepository;
    private final JWTService jwtService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public Users signUp(RegistrationRequestDto registrationRequestDto) {
        if (userRepository.existsByEmail(registrationRequestDto.getEmail())) {
            throw new AlreadyVerifiedException("Email already Registered");
        }

        Users user = new Users();
        user.setEmail(registrationRequestDto.getEmail());
        user.setPassword_hash(passwordEncoder.encode(registrationRequestDto.getPassword()));
        user.setRole(registrationRequestDto.getRole());
        user = userRepository.save(user);

        String otpCode = generateVerificationCode();
        OTPVerification otp = new OTPVerification();
        otp.setUser(user);
        otp.setOtpCode(otpCode);
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        otpRepository.save(otp);

        sendVerificationEmail(user, otpCode);
        return user;
    }

    @Transactional
    public Users verifyOtp(VerifyOtpDto verifyOtpDto) {
        Users user = userRepository.findByEmail(verifyOtpDto.getEmail())
                .orElseThrow(() -> new InvalidOTPException("user not found"));

        OTPVerification otp = otpRepository.findByUserAndOtpCode(user, verifyOtpDto.getOtpCode())
                .orElseThrow(() -> new InvalidOTPException(("Invalid OTP")));

        if (otp.isUsed()) {
            throw new InvalidOTPException("This OTP has already been used");
        }
        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidOTPException("OTP has expired. Please request a new one.");
        }

        otp.setUsed(true);
        otpRepository.save(otp);

        user.setEmailVerified(true);
        userRepository.save(user);

        return user;
    }

    @Transactional
    public void resendOtp(ResendOtpDto resendOtpDto) {
        Users user = userRepository.findByEmail(resendOtpDto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.isEmailVerified()) {
            throw new AlreadyVerifiedException("Account is already verified. Please log in.");
        }

        List<OTPVerification> existingOtps = otpRepository.findByUserAndIsUsedFalse(user);
        existingOtps.forEach(otp -> otp.setUsed(true));
        otpRepository.saveAll(existingOtps);

        String newOtpCode = generateVerificationCode();
        OTPVerification newOtp = new OTPVerification();
        newOtp.setUser(user);
        newOtp.setOtpCode(newOtpCode);
        newOtp.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        otpRepository.save(newOtp);

        sendVerificationEmail(user, newOtpCode);
    }

    public Users signIn(LoginRequestDto loginRequestDto) {
        try {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDto.getEmail(),
                            loginRequestDto.getPassword()));
        } catch (DisabledException e) {
            throw new RuntimeException("Account not verified. Please verify your email using the OTP sent to you.");
        } catch (BadCredentialsException e) {
            throw new RuntimeException("Invalid Email or Password");
        }

        Users user = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return user;
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }

    private void sendVerificationEmail(Users user, String otpCode) {
        String subject = "Account Verification";
        String verificationCode = "VERIFICATION CODE " + otpCode;
        String htmlMessage = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1.0">
                  <title>Verify your session</title>
                  <style>
                    @media (prefers-color-scheme: dark) {
                      body, .email-bg { background:#111110 !important; }
                      .card { background:#1C1C1A !important; border-color:#2A2A28 !important; box-shadow:none !important; }
                      .text-main { color:#F5F5F3 !important; }
                      .text-muted { color:#8C8C88 !important; }
                      .code-box { background:#111110 !important; color:#F5F5F3 !important; border-color:#2A2A28 !important; }
                      .divider { border-top-color:#2A2A28 !important; }
                      .brand { color:#F5F5F3 !important; }
                      .brand-dot { color:#F5F5F3 !important; }
                      .footer-text { color:#555553 !important; }
                    }
                    @media (max-width:600px) {
                      .container { width:100% !important; padding:16px !important; }
                      .card-body { padding:36px 28px !important; }
                    }
                  </style>
                </head>
                <body class="email-bg" style="margin:0;padding:0;background-color:#F7F7F5;">

                  <div style="display:none;max-height:0;overflow:hidden;opacity:0;">Your one-time verification code — expires in 10 minutes.</div>

                  <table width="100%" cellpadding="0" cellspacing="0" role="presentation" style="background-color:#F7F7F5;min-height:100vh;">
                    <tr>
                      <td align="center" style="padding:48px 16px;">

                        <table width="100%" cellpadding="0" cellspacing="0" role="presentation" class="container" style="max-width:540px;">

                          <tr>
                            <td align="center" style="padding-bottom:28px;">
                              <span class="brand" style="font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Helvetica,Arial,sans-serif;font-size:15px;font-weight:600;letter-spacing:0.04em;color:#0A0A0A;">
                                Hack<span class="brand-dot" style="color:#0A0A0A;">Sync</span>
                              </span>
                            </td>
                          </tr>

                          <tr>
                            <td>
                              <table width="100%" cellpadding="0" cellspacing="0" role="presentation" class="card" style="background:#FFFFFF;border-radius:6px;border:1px solid #EEEEED;box-shadow:0 2px 12px rgba(0,0,0,0.04);">


                                <tr>
                                  <td style="background-color:#0A0A0A;height:3px;border-radius:2px 2px 0 0;"></td>
                                </tr>


                                <tr>
                                  <td class="card-body" style="padding:44px 48px 40px;">


                                    <p style="margin:0 0 36px;font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Helvetica,Arial,sans-serif;font-size:11px;letter-spacing:0.12em;text-transform:uppercase;color:#A3A3A3;font-weight:500;">
                                      HackSync Platform
                                    </p>

                                    <h1 class="text-main" style="margin:0 0 10px;font-family:Georgia,'Times New Roman',serif;font-size:26px;font-weight:400;color:#0A0A0A;line-height:1.25;letter-spacing:-0.01em;">
                                      Verify your identity.
                                    </h1>


                                    <p class="text-muted" style="margin:0 0 36px;font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Helvetica,Arial,sans-serif;font-size:15px;color:#6B6B6B;line-height:1.7;">
                                      Use the code below to access your hackathon dashboard. It expires in <span style="color:#0A0A0A;font-weight:500;">10 minutes</span>.
                                    </p>


                                    <table width="100%" cellpadding="0" cellspacing="0" role="presentation">
                                      <tr><td style="border-top:1px solid #F0F0EE;padding-bottom:32px;"></td></tr>
                                    </table>


                                    <p style="margin:0 0 10px;font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Helvetica,Arial,sans-serif;font-size:11px;letter-spacing:0.12em;text-transform:uppercase;color:#A3A3A3;font-weight:500;">
                                      One-time code
                                    </p>


                                    <div class="code-box" style="font-family:'SFMono-Regular',Consolas,'Courier New',monospace;font-size:30px;font-weight:600;letter-spacing:10px;color:#0A0A0A;background:#FAFAF9;border:1px solid #EEEEED;border-radius:4px;padding:18px 20px;text-align:center;">
                                      {{VERIFICATION_CODE}}
                                    </div>


                                    <table width="100%" cellpadding="0" cellspacing="0" role="presentation">
                                      <tr><td class="divider" style="border-top:1px solid #F0F0EE;padding-top:32px;"></td></tr>
                                    </table>


                                    <p class="text-muted" style="margin:0;font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Helvetica,Arial,sans-serif;font-size:12px;color:#A3A3A3;line-height:1.7;">
                                      Didn't request this? You can safely ignore this email — your account remains secure.
                                    </p>

                                  </td>
                                </tr>

                              </table>
                            </td>
                          </tr>


                          <tr>
                            <td align="center" style="padding:24px 0 0;">
                              <p class="footer-text" style="margin:0;font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Helvetica,Arial,sans-serif;font-size:11px;color:#BCBCBA;line-height:1.6;">
                                © 2026 HackSync Platform · This is an automated message, please do not reply.
                              </p>
                            </td>
                          </tr>

                        </table>

                      </td>
                    </tr>
                  </table>

                </body>
                </html>
                """;

        htmlMessage = htmlMessage.replace("{{VERIFICATION_CODE}}", verificationCode);

        try {
            emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

}
