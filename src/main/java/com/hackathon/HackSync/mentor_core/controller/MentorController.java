package com.hackathon.HackSync.mentor_core.controller;

import com.hackathon.HackSync.mentor_core.dto.MeetingRequestDTO;
import com.hackathon.HackSync.mentor_core.dto.MeetingResponseDTO;
import com.hackathon.HackSync.mentor_core.dto.MentorAssignedHackathonResponseDTO;
import com.hackathon.HackSync.mentor_core.dto.MentorTicketResponseDTO;
import com.hackathon.HackSync.mentor_core.service.MeetingService;
import com.hackathon.HackSync.mentor_core.service.MentorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

import lombok.RequiredArgsConstructor;
import com.hackathon.HackSync.utils.dto.ApiResponse;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@RestController
@RequestMapping("/mentor")
public class MentorController {

    private final MentorService mentorService;
    private final MeetingService meetingService;

    @GetMapping("/tickets")
    public ResponseEntity<ApiResponse<List<MentorTicketResponseDTO>>> getTicketsByStatus(
            @RequestParam(defaultValue = "OPEN") String status,
            Principal principal) {
        List<MentorTicketResponseDTO> tickets = mentorService.getTicketsByStatus(principal.getName(), status);
        return ResponseEntity.ok(new ApiResponse<>("Tickets fetched successfully", HttpStatus.OK, tickets));
    }

    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<MeetingResponseDTO>> generateMeetingLinks(
            @RequestBody MeetingRequestDTO request) {
        MeetingResponseDTO meetingResponse = meetingService.generateSecureMeeting(request);
        return ResponseEntity.ok(new ApiResponse<>("Meeting links generated successfully", HttpStatus.CREATED, meetingResponse));
    }

    @PutMapping("/tickets/{id}/claim")
    public ResponseEntity<ApiResponse<MentorTicketResponseDTO>> claimTicket(
            @PathVariable Long id,
            Principal principal) {
        MentorTicketResponseDTO ticket = mentorService.claimTicket(id, principal.getName());
        return ResponseEntity.ok(new ApiResponse<>("Ticket claimed successfully", HttpStatus.OK, ticket));
    }

    @PutMapping("/tickets/{id}/resolve")
    public ResponseEntity<ApiResponse<MentorTicketResponseDTO>> resolveTicket(
            @PathVariable Long id,
            Principal principal) {
        MentorTicketResponseDTO ticket = mentorService.resolveTicket(id, principal.getName());
        return ResponseEntity.ok(new ApiResponse<>("Ticket resolved successfully", HttpStatus.OK, ticket));
    }


    @PutMapping("/invitations/{hackathonId}/status")
    public ResponseEntity<ApiResponse<String>> updateInvitationStatus(
            @PathVariable Long hackathonId,
            @RequestParam String status,
            Principal principal) {
        String message = mentorService.updateInvitationStatus(hackathonId, status, principal.getName());
        return ResponseEntity.ok(new ApiResponse<>(message, HttpStatus.OK, null));
    }

    @GetMapping("/hackathons")
    public ResponseEntity<ApiResponse<List<MentorAssignedHackathonResponseDTO>>> getMyAssignedHackathons(
            Principal principal) {
        List<MentorAssignedHackathonResponseDTO> hackathons = mentorService.getMyAssignedHackathons(principal.getName());
        return ResponseEntity.ok(new ApiResponse<>("Assigned hackathons retrieved successfully", HttpStatus.OK, hackathons));
    }
}