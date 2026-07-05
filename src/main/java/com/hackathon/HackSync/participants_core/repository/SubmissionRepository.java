package com.hackathon.HackSync.participants_core.repository;

import com.hackathon.HackSync.participants_core.entity.Submissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submissions, Long> {
    Optional<Submissions> findByTeamId(Long teamId);
    boolean existsByTeamId(Long teamId);
}
