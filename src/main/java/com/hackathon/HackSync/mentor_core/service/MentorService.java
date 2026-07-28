package com.hackathon.HackSync.mentor_core.service;

import com.hackathon.HackSync.auth.entity.Users;
import com.hackathon.HackSync.auth.repository.UserRepository;
import com.hackathon.HackSync.host_core.entity.Hackathons;
import com.hackathon.HackSync.mentor_core.dto.MentorTicketResponseDTO;
import com.hackathon.HackSync.mentor_core.entity.HelpTickets;
import com.hackathon.HackSync.mentor_core.entity.TicketStatus;
import com.hackathon.HackSync.mentor_core.repository.helpTicketRepository;
import com.hackathon.HackSync.mentor_core.repository.HackathonsMentorsRepository;
import com.hackathon.HackSync.mentor_core.entity.HackathonsMentors;
import com.hackathon.HackSync.mentor_core.entity.MentorStatus;
import com.hackathon.HackSync.utils.exception.ResourceNotFoundException;
import com.hackathon.HackSync.mentor_core.dto.MeetingRequestDTO;
import com.hackathon.HackSync.mentor_core.dto.MeetingResponseDTO;
import com.hackathon.HackSync.mentor_core.dto.MentorAssignedHackathonResponseDTO;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class MentorService {

    private final UserRepository userRepository;
    private final helpTicketRepository helpTicketRepository;
    private final HackathonsMentorsRepository hackathonsMentorsRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final MeetingService meetingService;

    public List<MentorTicketResponseDTO> getTicketsByStatus(String authenticatedEmail, String statusString) {
        Users mentor = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Mentor not found"));

        //TODO add this exception class in global exception controller
        TicketStatus status;
        try {
            status = TicketStatus.valueOf(statusString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid ticket status");
        }

        List<HelpTickets> tickets;
        if (status == TicketStatus.OPEN) {
            tickets = helpTicketRepository.findByStatus(TicketStatus.OPEN);
        } else {
            tickets = helpTicketRepository.findByAssignedMentorIdAndStatus(mentor, status);
        }

        List<MentorTicketResponseDTO> responseList = new ArrayList<>();
        for (HelpTickets ticket : tickets) {
            responseList.add(mapToDTO(ticket));
        }

        return responseList;
    }

    public MentorTicketResponseDTO claimTicket(Long ticketId, String authenticatedEmail) {
        Users mentor = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Mentor not found"));

        HelpTickets ticket = helpTicketRepository.findByIdForUpdate(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        //TODO throw ticket invalid exception
        if (ticket.getStatus() != TicketStatus.OPEN) {
            throw new RuntimeException("Ticket is already claimed or resolved");
        }

        // Generate JaaS Meeting
        MeetingRequestDTO requestDto = new MeetingRequestDTO(ticket.getIssueTitle());
        MeetingResponseDTO meetingResponse = meetingService.generateSecureMeeting(requestDto);

        ticket.setAssignedMentorId(mentor);
        ticket.setStatus(TicketStatus.CLAIMED);
        ticket.setClaimedAt(LocalDateTime.now());
        ticket.setParticipantMeetingLink(meetingResponse.getParticipantLink());
        ticket.setMentorMeetingLink(meetingResponse.getMentorLink());

        helpTicketRepository.save(ticket);
        messagingTemplate.convertAndSend("/topic/tickets", /*"TICKET_CLAIMED"*/ TicketStatus.CLAIMED);
        messagingTemplate.convertAndSend("/topic/team/" + ticket.getTeamId().getId() + "/tickets", /*"TICKET_CLAIMED"*/ TicketStatus.CLAIMED);
        return mapToDTO(ticket);
    }

    public MentorTicketResponseDTO resolveTicket(Long ticketId, String authenticatedEmail) {
        Users mentor = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Mentor not found"));

        HelpTickets ticket = helpTicketRepository.findById(ticketId)
                .orElseThrow(() -> new UsernameNotFoundException("Ticket not found"));

        if (ticket.getAssignedMentorId() == null || !ticket.getAssignedMentorId().getId().equals(mentor.getId())) {
            //TODO throw not authorized exception
            throw new RuntimeException("You are not the mentor assigned to this ticket");
        }

        ticket.setStatus(TicketStatus.RESOLVED);
        ticket.setResolvedAt(LocalDateTime.now());

        helpTicketRepository.save(ticket);
        messagingTemplate.convertAndSend("/topic/team/" + ticket.getTeamId().getId() + "/tickets", /*"TICKET_RESOLVED"*/ TicketStatus.RESOLVED);
        return mapToDTO(ticket);
    }

    private MentorTicketResponseDTO mapToDTO(HelpTickets ticket) {
        return MentorTicketResponseDTO.builder()
                .id(ticket.getId())
                .teamId(ticket.getTeamId() != null ? ticket.getTeamId().getId() : null)
                .issueTitle(ticket.getIssueTitle())
                .issueDescription(ticket.getIssueDescription())
                .techTags(ticket.getTechTags())
                .contactLocation(ticket.getContactLocation())
                .participantMeetingLink(ticket.getParticipantMeetingLink())
                .mentorMeetingLink(ticket.getMentorMeetingLink())
                .status(ticket.getStatus())
                .claimedAt(ticket.getClaimedAt())
                .resolvedAt(ticket.getResolvedAt())
                .build();
    }

    public String updateInvitationStatus(Long hackathonId, String statusString, String authenticatedEmail) {
        Users mentor = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Mentor not found"));

        HackathonsMentors hackathonMentor = hackathonsMentorsRepository.findByHackathonId_IdAndMentorsId_Id(hackathonId, mentor.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Hackathon mentor invitation not found"));

        MentorStatus status;
        try {
            status = MentorStatus.valueOf(statusString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid mentor status");
        }

        hackathonMentor.setStatus(status);
        hackathonsMentorsRepository.save(hackathonMentor);

        return "Invitation status updated to " + status.name();
    }

    public List<MentorAssignedHackathonResponseDTO> getMyAssignedHackathons(String authenticatedEmail) {
        Users mentor = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Mentor not found"));

        List<HackathonsMentors> assignments = hackathonsMentorsRepository.findByMentorsId_Id(mentor.getId());

        return assignments.stream().map(assignment -> {
            Hackathons hackathon = assignment.getHackathonId();
            return MentorAssignedHackathonResponseDTO.builder()
                    .hackathonId(hackathon.getId())
                    .title(hackathon.getTitle())
                    .tagline(hackathon.getTagline())
                    .hackathonStatus(hackathon.getHackathonStatus())
                    .hackathonStarts(hackathon.getHackathonStart())
                    .hackathonEnds(hackathon.getHackathonEnd())
                    .invitationStatus(assignment.getStatus())
                    .build();
        }).toList();
    }
}
