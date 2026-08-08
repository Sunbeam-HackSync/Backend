package com.hackathon.HackSync.participants_core.repository;

import com.hackathon.HackSync.auth.entity.Users;
import com.hackathon.HackSync.host_core.entity.Hackathons;
import com.hackathon.HackSync.participants_core.entity.TeamMembers;
import com.hackathon.HackSync.participants_core.entity.Teams;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMembers, Long> {

    @Query("SELECT tm FROM TeamMembers tm JOIN FETCH tm.userId JOIN FETCH tm.teamsId t WHERE t.hackathonId.id = :hackathonId")
    List<TeamMembers> findByHackathonId(@Param("hackathonId") Long hackathonId);

    @Query("SELECT tm FROM TeamMembers tm JOIN FETCH tm.teamsId t WHERE t.hackathonId.id = :hackathonId AND tm.userId.id = :userId")
    Optional<TeamMembers> findByHackathonIdAndUserId(@Param("hackathonId") Long hackathonId, @Param("userId") Long userId);

    boolean existsByTeamsIdIdAndUserIdId(Long id, Long id1);

    boolean existsByTeamsIdHackathonIdIdAndUserIdId(Long id, Long id1);

    Optional<TeamMembers> findByTeamsIdAndUserId(Teams team, Users user);

    boolean existsByTeamsIdAndUserId(Teams team, Users member);

    @Query("SELECT DISTINCT h FROM TeamMembers tm JOIN tm.teamsId t JOIN t.hackathonId h WHERE tm.userId.email = :email")
    List<Hackathons> findParticipatedHackathonsByEmail(@Param("email") String email);

    List<TeamMembers> findByTeamsId(Teams teamsId);

}
