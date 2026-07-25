package com.hackathon.HackSync.judge_core.repository;

import com.hackathon.HackSync.judge_core.entity.ProjectSubmissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectSubmissionRepository extends JpaRepository<ProjectSubmissions, Long> {
    
    @Query("SELECT ps FROM ProjectSubmissions ps JOIN FETCH ps.teamsId WHERE ps.hackathonId.id = :hackathonId")
    List<ProjectSubmissions> findByHackathonId(@Param("hackathonId") Long hackathonId);

    boolean existsByTeamsId(com.hackathon.HackSync.participants_core.entity.Teams teamsId);
}
