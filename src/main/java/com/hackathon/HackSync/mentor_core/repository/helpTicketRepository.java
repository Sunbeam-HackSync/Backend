package com.hackathon.HackSync.mentor_core.repository;

import com.hackathon.HackSync.mentor_core.entity.HelpTickets;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hackathon.HackSync.mentor_core.entity.TicketStatus;

import jakarta.persistence.LockModeType;

import com.hackathon.HackSync.auth.entity.Users;
import java.util.List;
import java.util.Optional;

@Repository
public interface helpTicketRepository extends JpaRepository<HelpTickets, Long> {
    List<HelpTickets> findByAssignedMentorId(Users mentor);

    List<HelpTickets> findByAssignedMentorIdAndStatus(Users mentor, TicketStatus status);

    List<HelpTickets> findByStatus(TicketStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT h FROM HelpTickets h WHERE h.id = :id")
    Optional<HelpTickets> findByIdForUpdate(@Param("id") Long id);

    List<HelpTickets> findByHackathonId_IdAndTeamId_Id(Long hackathonId, Long teamId);
}
