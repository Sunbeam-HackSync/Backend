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
    /* Make this enpoint public */

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HackathonDetailResponseDTO>> getHackathonById(@PathVariable Long id) {
        HackathonDetailResponseDTO details = participantService.getPublicHackathonDetail(id);
        return ResponseEntity.ok(new ApiResponse<>("Hackathon details fetched successfully", HttpStatus.OK, details));
    }

    @GetMapping("/hackathons/{id}/my-details")
    public ResponseEntity<ApiResponse<HackathonWithTeamDetailsResponseDTO>> getHackathonWithTeamDetails(
            @PathVariable Long id, Principal principal) {
        HackathonWithTeamDetailsResponseDTO response = participantService.getHackathonWithTeamDetails(id,
                principal.getName());
        return ResponseEntity
                .ok(new ApiResponse<>("Hackathon and team details fetched successfully", HttpStatus.OK, response));
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

    @GetMapping("/hackathon/{hackathonId}/result")
    public ResponseEntity<ApiResponse<ParticipantResultResponseDTO>> getHackathonResult(
            @PathVariable Long hackathonId,
            Principal principal) {
        ParticipantResultResponseDTO result = participantService.getHackathonResult(hackathonId, principal.getName());
        return ResponseEntity.ok(new ApiResponse<>("Hackathon result retrieved successfully", HttpStatus.OK, result));
    }

    @GetMapping("/hackathon/{hackathonId}/winners")
    public ResponseEntity<ApiResponse<List<HackathonWinnerResponseDTO>>> getHackathonWinners(
            @PathVariable Long hackathonId) {
        List<HackathonWinnerResponseDTO> winners = participantService.getHackathonWinners(hackathonId);
        return ResponseEntity.ok(new ApiResponse<>("Hackathon winners retrieved successfully", HttpStatus.OK, winners));
    }

    @PostMapping("/profile")
    public ResponseEntity<ApiResponse<ParticipantProfileDTO>> createProfile(
            @RequestBody ParticipantProfileDTO request,
            Principal principal) {
        ParticipantProfileDTO createdProfile = participantService.createProfile(request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Profile created successfully", HttpStatus.CREATED, createdProfile));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<ParticipantProfileDTO>> getProfile(Principal principal) {
        ParticipantProfileDTO profile = participantService.getProfile(principal.getName());
        return ResponseEntity.ok(new ApiResponse<>("Profile fetched successfully", HttpStatus.OK, profile));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<ParticipantProfileDTO>> updateProfile(
            @RequestBody ParticipantProfileDTO request,
            Principal principal) {
        ParticipantProfileDTO updatedProfile = participantService.updateProfile(request, principal.getName());
        return ResponseEntity.ok(new ApiResponse<>("Profile updated successfully", HttpStatus.OK, updatedProfile));
    }

    @GetMapping("/tickets/{hackathonId}/{TeamId}")
    public ResponseEntity<ApiResponse<List<GetHelpTicketInfoDTO>>> getHelpTicket(@PathVariable Long hackathonId,
            @PathVariable Long TeamId, Principal principal) {

        List<GetHelpTicketInfoDTO> helpTickets = helpTicketService.getTicketByTeamsAndHackathonId(TeamId, hackathonId,
                principal.getName());
        return ResponseEntity.ok(new ApiResponse<>("Help ticket fetched successfully", HttpStatus.OK, helpTickets));
    }
}
