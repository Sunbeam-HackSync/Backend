package com.hackathon.HackSync.mentor_core.controller;

import com.hackathon.HackSync.mentor_core.dto.MeetingRequestDTO;
import com.hackathon.HackSync.mentor_core.dto.MeetingResponseDTO;
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

    /*
     * 
     * 
     * PUT /mentor/tickets/{id}/claim - Updates the ticket by adding the mentor_id,
     * pasting the manual Google Meet link, and changing the status to CLAIMED.
     * 
     * PUT /mentor/tickets/{id}/resolve - Updates the resolved_at timestamp and
     * closes out the ticket once the mentor finishes the call.
     * 
     * GET /mentor/tickets/me - Fetches a history of all tickets claimed and
     * resolved by this specific mentor.
     */
}
