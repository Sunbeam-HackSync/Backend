package com.hackathon.HackSync.judge_core.repository;

import com.hackathon.HackSync.judge_core.entity.HackathonWinners;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HackathonWinnersRepository extends JpaRepository<HackathonWinners, Long> {
    List<HackathonWinners> findByHackathonId_Id(Long hackathonId);
    Optional<HackathonWinners> findBySubmissionId_Id(Long submissionId);
}
