package com.hackathon.HackSync.participants_core.repository;

import com.hackathon.HackSync.participants_core.entity.TeamMembers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeamMemberRepository extends JpaRepository<TeamMembers, Long> {
    
    @Query("SELECT tm FROM TeamMembers tm JOIN FETCH tm.userId JOIN FETCH tm.teamsId t WHERE t.hackathonId.id = :hackathonId")
    List<TeamMembers> findByHackathonId(@Param("hackathonId") Long hackathonId);
}
