package com.hackathon.HackSync.judge_core.repository;

import com.hackathon.HackSync.judge_core.entity.ProjectSubmissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ProjectSubmissionRepository extends JpaRepository<ProjectSubmissions, UUID> {
    
    @Query("SELECT ps FROM ProjectSubmissions ps JOIN FETCH ps.teamsId WHERE ps.hackathonId.id = :hackathonId")
    List<ProjectSubmissions> findByHackathonId(@Param("hackathonId") UUID hackathonId);
}
