package com.hackathon.HackSync.participants_core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hackathon.HackSync.participants_core.entity.ParticipantsProfiles;

import java.util.Optional;

@Repository
public interface ParticipantsProfilesRepository extends JpaRepository<ParticipantsProfiles, Long> {
    Optional<ParticipantsProfiles> findByUserId_Id(Long userId);
    Optional<ParticipantsProfiles> findByUserId_Email(String email);
}
