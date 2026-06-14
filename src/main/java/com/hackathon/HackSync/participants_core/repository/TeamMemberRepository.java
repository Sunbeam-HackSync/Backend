package com.hackathon.HackSync.participants_core.repository;

import com.hackathon.HackSync.participants_core.entity.TeamMembers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TeamMemberRepository extends JpaRepository<TeamMembers, UUID> {
    
    @Query("SELECT tm FROM TeamMembers tm JOIN FETCH tm.userId JOIN FETCH tm.teamsId t WHERE t.hackathonId.id = :hackathonId")
    List<TeamMembers> findByHackathonId(@Param("hackathonId") UUID hackathonId);
}
