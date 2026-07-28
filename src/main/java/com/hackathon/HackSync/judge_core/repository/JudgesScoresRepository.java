package com.hackathon.HackSync.judge_core.repository;

import com.hackathon.HackSync.judge_core.entity.JudgesScores;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JudgesScoresRepository extends JpaRepository<JudgesScores, Long> {
    List<JudgesScores> findByProjectId_Id(Long projectId);
    List<JudgesScores> findByJudgeId_Id(Long judgeId);
    boolean existsByProjectId_IdAndJudgeId_Id(Long projectId, Long judgeId);
}
