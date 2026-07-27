package com.hackathon.HackSync.participants_core.service;

import com.hackathon.HackSync.auth.entity.Users;
import com.hackathon.HackSync.auth.repository.UserRepository;
import com.hackathon.HackSync.host_core.entity.Hackathons;
import com.hackathon.HackSync.host_core.repository.HackathonRepository;
import com.hackathon.HackSync.mentor_core.entity.HelpTickets;
import com.hackathon.HackSync.mentor_core.entity.TicketStatus;
import com.hackathon.HackSync.mentor_core.repository.helpTicketRepository;
import com.hackathon.HackSync.participants_core.dto.GetHelpTicketInfoDTO;
import com.hackathon.HackSync.participants_core.dto.HelpTicketRequestDTO;
import com.hackathon.HackSync.participants_core.dto.HelpTicketResponseDTO;
import com.hackathon.HackSync.participants_core.entity.Teams;
import com.hackathon.HackSync.participants_core.repository.TeamMemberRepository;
import com.hackathon.HackSync.participants_core.repository.TeamRepository;
import com.hackathon.HackSync.utils.exception.ResourceNotFoundException;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    private final HackathonRepository hackathonRepository;

    public List<GetHelpTicketInfoDTO> getTicketByTeamsAndHackathonId(Long TeamId, Long HackathonId,
            String authenticatedEmail) {

        Users user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Teams teams = teamRepository.findById(TeamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));
        Hackathons hackathons = hackathonRepository.findById(HackathonId)
                .orElseThrow(() -> new ResourceNotFoundException("Hackathon do not exis"));

        List<HelpTickets> helpTickets = helpTicketRepository.findByHackathonId_IdAndTeamId_Id(HackathonId, TeamId);

        return helpTickets.stream().map(ticket -> GetHelpTicketInfoDTO.builder()
                .TicketId(ticket.getId())
                .hackathonId(ticket.getHackathonId().getId())
                .teamId(ticket.getTeamId().getId())
                .creatorId(ticket.getCreatorId().getId())
                .assignedMentorId(ticket.getAssignedMentorId() != null ? ticket.getAssignedMentorId().getId() : null)
                .issueTitle(ticket.getIssueTitle())
                .issueDescription(ticket.getIssueDescription())
                .techTags(ticket.getTechTags())
                .contactLocation(ticket.getContactLocation())
                .participantMeetingLink(ticket.getParticipantMeetingLink())
                .mentorMeetingLink(ticket.getMentorMeetingLink())
                .status(ticket.getStatus() != null ? ticket.getStatus().name() : null)
                .claimedAt(ticket.getClaimedAt() != null ? ticket.getClaimedAt().toString() : null)
                .resolvedAt(ticket.getResolvedAt() != null ? ticket.getResolvedAt().toString() : null)
                .build()).toList();
    }

    public HelpTicketResponseDTO createTicket(@Valid HelpTicketRequestDTO request, String authenticatedEmail) {

        // Find logged-in participant
        Users user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Find Team
        Teams team = teamRepository.findById(request.getTeamId())
                .orElseThrow(() -> new RuntimeException("Team not found"));

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

        // Build Response
        HelpTicketResponseDTO response = new HelpTicketResponseDTO();

        response.setTicketId(savedTicket.getId());
        response.setIssueTitle(savedTicket.getIssueTitle());
        response.setIssueDescription(savedTicket.getIssueDescription());
        response.setTechTags(savedTicket.getTechTags());
        response.setStatus(savedTicket.getStatus());

        // Broadcast to Mentors that a new ticket is available
        messagingTemplate.convertAndSend("/topic/tickets", response);

        return response;
    }
}
