package com.hackathon.HackSync.auth.service;

import java.util.UUID;

import com.hackathon.HackSync.auth.entity.RefreshToken;
import com.hackathon.HackSync.auth.entity.Users;
import com.hackathon.HackSync.auth.repository.RefreshTokenRepository;
import com.hackathon.HackSync.auth.repository.UserRepository;
import com.hackathon.HackSync.utils.exception.InvalidRefreshTokenException;
import com.hackathon.HackSync.utils.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${security.jwt.refresh-token.expiration-time}")
    private long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;


    @Transactional
    public RefreshToken createRefreshToken(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Revoke all existing tokens for this user before creating a new one (Optional, based on requirement, but good for security)
        refreshTokenRepository.revokeAllUserTokens(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .revoked(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Transactional
    public RefreshToken verifyExpirationAndRevocation(RefreshToken token) {
        if (token.isRevoked()) {
            // Token reuse detected! Revoke all tokens for this user.
            refreshTokenRepository.revokeAllUserTokens(token.getUser());
            throw new InvalidRefreshTokenException("Refresh token was revoked. Please sign in again.");
        }
        
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
            throw new InvalidRefreshTokenException("Refresh token was expired. Please make a new sign in request");
        }
        return token;
    }

    @Transactional
    public void revokeToken(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(refreshToken -> {
            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);
        });
    }
}
