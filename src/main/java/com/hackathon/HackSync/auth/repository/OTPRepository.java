package com.hackathon.HackSync.auth.repository;

import com.hackathon.HackSync.auth.entity.Users;
import com.hackathon.HackSync.auth.entity.OTPVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface OTPRepository extends JpaRepository<OTPVerification, Long> {
    Optional<OTPVerification> findByUserAndOtpCode(Users user, String opt);

    List<OTPVerification> findByUserAndIsUsedFalse(Users user);


}
