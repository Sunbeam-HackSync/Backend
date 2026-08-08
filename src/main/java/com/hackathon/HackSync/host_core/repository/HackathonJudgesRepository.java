package com.hackathon.HackSync.host_core.repository;

import com.hackathon.HackSync.host_core.entity.HackathonJudges;
import com.hackathon.HackSync.host_core.entity.Hackathons;
import com.hackathon.HackSync.host_core.entity.JudgeInvitationStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HackathonJudgesRepository extends JpaRepository<HackathonJudges, Long> {
    Optional<HackathonJudges> findByHackathonsId_IdAndJudgeUserId_Id(Long hackathonId, Long judgeId);
    
    List<HackathonJudges> findByHackathonsIdAndStatus(Hackathons hackathon, JudgeInvitationStatus status);
    
    List<HackathonJudges> findByJudgeUserId_Id(Long judgeId);
    
    List<HackathonJudges> findByHackathonsId(Hackathons hackathons);
}
