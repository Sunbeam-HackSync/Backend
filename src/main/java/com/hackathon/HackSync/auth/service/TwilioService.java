package com.hackathon.HackSync.auth.service;

import com.twilio.Twilio;
import com.twilio.exception.TwilioException;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwilioService {

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.verification-sid}")
    private String verificationSid;


    @PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);
    }

    public void generateOtp(String phoneNumber) {
        try {
            Verification verification = Verification.creator(
                    verificationSid,
                    phoneNumber,
                    "sms"
            ).create();

            System.out.println("Twilio OTP Status: " + verification.getStatus());

        } catch (TwilioException e) {
            throw new RuntimeException("Failed to send OTP via Twilio: " + e.getMessage());
        }
    }


    public boolean verifyOtp(String phoneNumber, String code) {
        try {
            VerificationCheck verificationCheck = VerificationCheck.creator(verificationSid)
                    .setTo(phoneNumber)
                    .setCode(code)
                    .create();

            return "approved".equals(verificationCheck.getStatus());

        } catch (TwilioException e) {
            throw new RuntimeException("Failed to verify OTP via Twilio: " + e.getMessage());
        }
    }
}