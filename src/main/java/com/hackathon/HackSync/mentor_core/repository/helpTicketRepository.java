package com.hackathon.HackSync.mentor_core.repository;

import com.hackathon.HackSync.mentor_core.entity.HelpTickets;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hackathon.HackSync.mentor_core.entity.TicketStatus;
import com.hackathon.HackSync.auth.entity.Users;
import java.util.List;

@Repository
public interface helpTicketRepository extends JpaRepository<HelpTickets, Long> {
    List<HelpTickets> findByAssignedMentorId(Users mentor);

    List<HelpTickets> findByAssignedMentorIdAndStatus(Users mentor, TicketStatus status);

    List<HelpTickets> findByStatus(TicketStatus status);

    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("SELECT h FROM HelpTickets h WHERE h.id = :id")
    java.util.Optional<HelpTickets> findByIdForUpdate(@org.springframework.data.repository.query.Param("id") Long id);
}
