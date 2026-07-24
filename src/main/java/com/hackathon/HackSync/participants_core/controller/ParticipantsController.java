package com.hackathon.HackSync.participants_core.controller;

import com.hackathon.HackSync.participants_core.dto.*;
import com.hackathon.HackSync.participants_core.service.HelpTicketService;
import com.hackathon.HackSync.participants_core.service.ParticipantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import java.security.Principal;
import java.util.List;
import org.springframework.data.domain.Page;
import com.hackathon.HackSync.utils.dto.ApiResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/participants")
public class ParticipantsController {

    private final ParticipantService participantService;
    private final HelpTicketService helpTicketService;

    @GetMapping("/hackathons")
    public ResponseEntity<ApiResponse<Page<HackathonDetailResponseDTO>>> getDiscoveryFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<HackathonDetailResponseDTO> hackathons = participantService.getDiscoveryFeed(page, size);
        return ResponseEntity.ok(new ApiResponse<>("Discovery feed fetched successfully", HttpStatus.OK, hackathons));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HackathonDetailResponseDTO>> getHackathonById(@PathVariable Long id) {
        HackathonDetailResponseDTO details = participantService.getPublicHackathonDetail(id);
        return ResponseEntity.ok(new ApiResponse<>("Hackathon details fetched successfully", HttpStatus.OK, details));
    }

    @GetMapping("/my-hackathons")
    public ResponseEntity<ApiResponse<List<HackathonDetailResponseDTO>>> getMyHackathons(Principal principal) {
        List<HackathonDetailResponseDTO> hackathons = participantService.getMyHackathons(principal.getName());
        return ResponseEntity.ok(new ApiResponse<>("Your hackathons fetched successfully", HttpStatus.OK, hackathons));
    }

    @PostMapping("/createTeam")
    public ResponseEntity<ApiResponse<TeamResponseDTO>> createTeam(@Valid @RequestBody TeamRequestDTO requestDTO,
            Principal principal) {
        TeamResponseDTO response = participantService.createTeam(requestDTO, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Team created successfully", HttpStatus.CREATED, response));
    }

    @PostMapping("/helpTickets")
    public ResponseEntity<ApiResponse<HelpTicketResponseDTO>> createTicket(
            @Valid @RequestBody HelpTicketRequestDTO request,
            Principal principal) {
        HelpTicketResponseDTO response = helpTicketService.createTicket(request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Help ticket created successfully", HttpStatus.CREATED, response));
    }

    @PostMapping("/teams/{id}/join")
    public ResponseEntity<ApiResponse<Object>> addMember(@PathVariable("id") Long teamId,
            @RequestBody AddMemberRequestDTO request,
            Principal principal) {
        // Logged-in team leader adds a participant by email
        participantService.addMember(teamId, request.getEmail(), principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Member added successfully", HttpStatus.CREATED, null));
    }

    @PostMapping("/submissions")
    public ResponseEntity<ApiResponse<SubmissionResponseDTO>> createSubmission(
            @Valid @RequestBody SubmissionRequestDTO request,
            Principal principal) {
        SubmissionResponseDTO response = participantService.createSubmission(request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Submission created successfully", HttpStatus.CREATED, response));
    }

    @PutMapping("/teams/{id}")
    public ResponseEntity<ApiResponse<TeamResponseDTO>> updateTeam(@PathVariable("id") Long teamId,
            @RequestBody TeamUpdateRequestDTO request, Principal principal) {
        TeamResponseDTO response = participantService.updateTeam(teamId, request, principal.getName());
        return ResponseEntity.ok(new ApiResponse<>("Team updated successfully", HttpStatus.OK, response));
    }

    @GetMapping("/teams/{id}")
    public ResponseEntity<ApiResponse<TeamWithParticipantsResponseDTO>> seeMyTeamDetails(
            @PathVariable("id") Long teamId, Principal principal) {
        TeamWithParticipantsResponseDTO response = participantService.seeMyTeamDetails(teamId, principal.getName());
        return ResponseEntity.ok(new ApiResponse<>("Team details fetched successfully", HttpStatus.OK, response));
    }

    /*
     * GET /participants/hackathons - Fetches a paginated list of all hackathons
     * with the status ACTIVE or APPROVED for the discovery feed.
     * 
     * GET /participants/hackathons/{id} - Retrieves the detailed rules, timeline,
     * and description of a specific hackathon.
     * 
     * POST /participants/createTeam - Creates a new team and automatically assigns
     * the creator as the is_team_leader.
     * 
     * POST /participants/helpTickets - Generates a new Help Ticket (Issue + Tech
     * Stack) with an OPEN status.
     * 
     * GET /participants/teams/looking - Fetches all teams within a hackathon where
     * is_looking_for_members is TRUE to populate the matchmaking board.
     * 
     * POST /participants/teams/{id}/join - Adds the participant to the team_members
     * mapping table.
     * 
     * PUT /participants/teams/{id} - or toggle their looking status.
     * 
     * POST /participants/submissions - Creates the final project record (Title,
     * Description, GitHub link, Demo Video link) tied to the team.
     */

}
