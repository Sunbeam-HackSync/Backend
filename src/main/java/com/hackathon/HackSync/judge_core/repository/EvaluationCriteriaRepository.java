package com.hackathon.HackSync.judge_core.repository;

import com.hackathon.HackSync.judge_core.entity.EvaluationCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluationCriteriaRepository extends JpaRepository<EvaluationCriteria, Long> {
    List<EvaluationCriteria> findByHackathonId_Id(Long hackathonId);
}
