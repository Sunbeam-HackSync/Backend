package com.hackathon.HackSync.participants_core.service;

import com.hackathon.HackSync.auth.entity.Users;
import com.hackathon.HackSync.auth.repository.UserRepository;

import com.hackathon.HackSync.mentor_core.entity.HelpTickets;
import com.hackathon.HackSync.mentor_core.entity.TicketStatus;
import com.hackathon.HackSync.mentor_core.repository.helpTicketRepository;
import com.hackathon.HackSync.participants_core.dto.HelpTicketRequestDTO;
import com.hackathon.HackSync.participants_core.dto.HelpTicketResponseDTO;
import com.hackathon.HackSync.participants_core.entity.Teams;
import com.hackathon.HackSync.participants_core.repository.TeamMemberRepository;
import com.hackathon.HackSync.participants_core.repository.TeamRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.messaging.simp.SimpMessagingTemplate;

@Service
@RequiredArgsConstructor
@Transactional
public class HelpTicketService {
    private final UserRepository userRepository;

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final helpTicketRepository helpTicketRepository;
    private final SimpMessagingTemplate messagingTemplate;


    public HelpTicketResponseDTO createTicket(@Valid HelpTicketRequestDTO request, String authenticatedEmail) {

        // Find logged-in participant
        Users user = userRepository.findByEmail(authenticatedEmail).orElseThrow(() -> new RuntimeException("User not found"));

        // Find Team
        Teams team = teamRepository.findById(request.getTeamId()).orElseThrow(() -> new RuntimeException("Team not found"));

        // Check whether participant belongs to this team
        boolean isMember = teamMemberRepository.existsByTeamsIdIdAndUserIdId(team.getId(), user.getId());

        if (!isMember) {
            throw new RuntimeException("You are not a member of this team");
        }

        // Create Help Ticket
        HelpTickets ticket = new HelpTickets();

        // Team & Hackathon
        ticket.setTeamId(team);
        ticket.setHackathonId(team.getHackathonId());

        // Creator
        ticket.setCreatorId(user);

        // Ticket Details
        ticket.setIssueTitle(request.getIssueTitle());
        ticket.setIssueDescription(request.getIssueDescription());
        ticket.setTechTags(request.getTechTags());

        // Default Values
        ticket.setAssignedMentorId(null);
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setClaimedAt(null);
        ticket.setResolvedAt(null);

        // Save Ticket
        HelpTickets savedTicket = helpTicketRepository.save(ticket);

        // Broadcast to Mentors that a new ticket is available
        messagingTemplate.convertAndSend("/topic/tickets", "NEW_TICKET");

        // Build Response
        HelpTicketResponseDTO response = new HelpTicketResponseDTO();


        response.setIssueTitle(savedTicket.getIssueTitle());
        response.setIssueDescription(savedTicket.getIssueDescription());
        response.setTechTags(savedTicket.getTechTags());
        response.setStatus(savedTicket.getStatus());

        return response;
    }
}
