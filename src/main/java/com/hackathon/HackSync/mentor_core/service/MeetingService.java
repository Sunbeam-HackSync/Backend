package com.hackathon.HackSync.mentor_core.service;

import com.hackathon.HackSync.mentor_core.dto.MeetingRequestDTO;
import com.hackathon.HackSync.mentor_core.dto.MeetingResponseDTO;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;

@Service
public class MeetingService {

    @Value("${jaas.app-id}")
    private String appId;

    @Value("${jaas.api-key}")
    private String apiKey;

    @Value("${jaas.private-key-location}")
    private Resource privateKeyResource;

    private RSAPrivateKey rsaPrivateKey;

    private static final String JAAS_BASE_URL = "https://8x8.vc/";

    @PostConstruct
    public void init(){
        try {
            String rawKeyContent = new String(privateKeyResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            this.rsaPrivateKey = parsePrivateKeyFromString(rawKeyContent);
            System.out.println("Successfully loaded JaaS Private Key on startup from: " + privateKeyResource.getDescription());
        } catch (IOException e){
            throw new RuntimeException("Could not read JaaS private key file from the configured location" , e);
        } catch (Exception e){
            throw new RuntimeException("Failed to initialize cryptographic key from file", e);
        }
    }

    public MeetingResponseDTO generateSecureMeeting(MeetingRequestDTO requestDto) {
        try {
            String sanitizedName = requestDto.getMeetingName().toLowerCase().replaceAll("[^a-z0-9]", "");
            if (sanitizedName.isEmpty()) sanitizedName = "session";
            String finalRoomName = sanitizedName + "-" + UUID.randomUUID().toString().substring(0, 8);

            Algorithm algorithm = Algorithm.RSA256(null, this.rsaPrivateKey);

            String mentorToken = generateJaasToken(algorithm, finalRoomName, "Mentor", true);
            String mentorLink = JAAS_BASE_URL + appId + "/" + finalRoomName + "?jwt=" + mentorToken;

            String participantToken = generateJaasToken(algorithm, finalRoomName, "Participant", false);
            String participantLink = JAAS_BASE_URL + appId + "/" + finalRoomName + "?jwt=" + participantToken;

            return new MeetingResponseDTO(finalRoomName, mentorLink, participantLink);

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate JaaS Meeting links: " + e.getMessage());
        }
    }

    private String generateJaasToken(Algorithm algorithm, String roomName, String displayName, boolean isModerator) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date exp = new Date(nowMillis + (3 * 60 * 60 * 1000)); // 3 hours

        Map<String, Object> userContext = new HashMap<>();
        userContext.put("name", displayName);
        userContext.put("moderator", isModerator);

        Map<String, Object> context = new HashMap<>();
        context.put("user", userContext);

        return JWT.create()
                .withKeyId(apiKey)
                .withAudience("jitsi")
                .withIssuer("chat")
                .withSubject(appId)
                .withClaim("room", roomName)
                .withClaim("context", context)
                .withNotBefore(now)
                .withExpiresAt(exp)
                .sign(algorithm);
    }

    private RSAPrivateKey parsePrivateKeyFromString(String rawKeyContent) throws Exception {
        String privateKeyPEM = rawKeyContent.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----","")
                .replaceAll("\\s", "");
        byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }
}
