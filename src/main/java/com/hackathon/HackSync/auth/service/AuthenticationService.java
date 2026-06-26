package com.hackathon.HackSync.auth.service;

import com.hackathon.HackSync.auth.dto.LoginRequestDto;
import com.hackathon.HackSync.auth.dto.RegistrationRequestDto;
import com.hackathon.HackSync.auth.dto.ResendOtpDto;
import com.hackathon.HackSync.auth.dto.VerifyOtpDto;
import com.hackathon.HackSync.auth.entity.OTPVerification;
import com.hackathon.HackSync.auth.entity.Users;
import com.hackathon.HackSync.auth.repository.OTPRepository;
import com.hackathon.HackSync.auth.repository.UserRepository;
import jakarta.mail.MessagingException;
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
public class AuthenticationService {

    private final UserRepository userRepository;
    private final OTPRepository otpRepository;
    private final JWTService jwtService;
    //    private final TwilioService twilioService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(UserRepository userRepository, OTPRepository otpRepository, JWTService jwtService, AuthenticationManager authenticationManager,
//                                 TwilioService twilioService
                                 EmailService emailService, PasswordEncoder passwordEncoder
    ) {

        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.otpRepository = otpRepository;
//        this.twilioService = twilioService;
        this.emailService = emailService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Users signUp(RegistrationRequestDto registrationRequestDto) {
        if (userRepository.existsByEmail(registrationRequestDto.getEmail())) {
            throw new RuntimeException("Email already Registered");
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
                .orElseThrow(() -> new RuntimeException("user not found"));

        OTPVerification otp = otpRepository.findByUserAndOtpCode(user, verifyOtpDto.getOtpCode())
                .orElseThrow(() -> new RuntimeException(("Invalid OTP")));

        if (otp.isUsed()) {
            throw new RuntimeException("This OTP has already been used");
        }
        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP has expired. Please request a new one.");
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
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isEmailVerified()) {
            throw new RuntimeException("Account is already verified. Please log in.");
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
                            loginRequestDto.getPassword()
                    )
            );
        } catch (DisabledException e) {
            throw new RuntimeException("Account not verified. Please verify your email using the OTP sent to you.");
        } catch (BadCredentialsException e) {
            throw new RuntimeException("Invalid Email or Password");
        }

        Users user = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
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
        String htmlMessage =
                "<!doctype html><html lang=\"en\"><head>"
                        + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">"
                        + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                        + "<title>Verify your Hackathon Session</title>"
                        + "<style>"
                        + "@media (prefers-color-scheme: dark){"
                        + "body,.email-bg{background:#09090b!important}"
                        + ".card{background:#18181b!important;border:1px solid #27272a!important;box-shadow:none!important}"
                        + ".text-main{color:#f4f4f5!important}"
                        + ".text-muted{color:#a1a1aa!important}"
                        + ".code-box{background:#09090b!important;color:#c084fc!important;border-color:#27272a!important}"
                        + ".border-top{border-top-color:#27272a!important}"
                        + "}"
                        + "@media (max-width:600px){.container{width:100%!important;padding:16px!important}.card{padding:24px!important}}"
                        + "</style>"
                        + "</head><body class=\"email-bg\" style=\"margin:0;padding:0;background:#fafafa;\">"
                        + "<div style=\"display:none;max-height:0;overflow:hidden;opacity:0;\">Your OTP code to access the hackathon platform.</div>"
                        + "<table role=\"presentation\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" style=\"background:#fafafa;\"><tr><td align=\"center\" style=\"padding:40px 12px;\">"
                        + "<table role=\"presentation\" class=\"container\" width=\"520\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" style=\"width:520px;max-width:520px;background:transparent;\">"
                        + "<tr><td align=\"center\" style=\"padding-bottom:24px;\"><div class=\"text-main\" style=\"font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,Helvetica,Arial,sans-serif;font-size:22px;font-weight:800;color:#09090b;letter-spacing:-0.5px;\">"
                        + "Hack<span style=\"color:#8b5cf6;\">Sync</span></div></td></tr>"
                        + "<tr><td><table role=\"presentation\" class=\"card\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" style=\"background:#ffffff;border-radius:12px;box-shadow:0 4px 24px rgba(0,0,0,0.04);overflow:hidden;\">"
                        + "<tr><td style=\"height:4px;background:linear-gradient(135deg,#6366f1,#8b5cf6,#d946ef);\"></td></tr>"
                        + "<tr><td style=\"padding:32px 32px 12px 32px;font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,Helvetica,Arial,sans-serif;\">"
                        + "<h1 class=\"text-main\" style=\"margin:0 0 12px 0;font-size:20px;line-height:1.4;font-weight:700;color:#09090b;letter-spacing:-0.3px;\">Verify your identity</h1>"
                        + "<p class=\"text-muted\" style=\"margin:0;color:#52525b;font-size:15px;line-height:1.6;\">Enter the verification code below to access your hackathon dashboard and continue building. This code expires in 10 minutes.</p></td></tr>"
                        + "<tr><td style=\"padding:20px 32px 12px 32px;\">"
                        + "<div class=\"text-muted\" style=\"font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,Helvetica,Arial,sans-serif;font-size:12px;font-weight:600;text-transform:uppercase;letter-spacing:1px;color:#71717a;margin-bottom:8px;\">Authorization Code</div>"
                        + "<div class=\"code-box\" style=\"font-family:'SFMono-Regular',Consolas,Menlo,Monaco,monospace;font-size:28px;letter-spacing:8px;font-weight:700;color:#7c3aed;background:#f4f4f5;border:1px solid #e4e4e7;border-radius:8px;padding:16px;text-align:center;\">"
                        + "{{VERIFICATION_CODE}}</div></td></tr>"
                        + "<tr><td style=\"padding:12px 32px 28px 32px;\"><p class=\"text-muted\" style=\"margin:0;font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,Helvetica,Arial,sans-serif;font-size:14px;color:#52525b;line-height:1.6;\">If you didn’t request this code, please ignore this email to keep your account secure.</p></td></tr>"
                        + "<tr><td class=\"border-top\" style=\"padding:16px 32px 24px 32px;border-top:1px solid #f4f4f5;\"><p class=\"text-muted\" style=\"margin:0;font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,Helvetica,Arial,sans-serif;font-size:12px;color:#a1a1aa;line-height:1.6;\">This is an automated message from HackSync. Please do not reply.</p></td></tr>"
                        + "</table></td></tr>"
                        + "<tr><td align=\"center\" style=\"padding:20px 8px 0 8px;\"><p class=\"text-muted\" style=\"margin:0;font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,Helvetica,Arial,sans-serif;font-size:12px;color:#a1a1aa;\">© 2026 HackSync Platform</p></td></tr>"
                        + "</table></td></tr></table></body></html>";

        htmlMessage = htmlMessage.replace("{{VERIFICATION_CODE}}", verificationCode);

        try {
            emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


}
