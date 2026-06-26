package com.hackathon.HackSync.host_core.controller;

import com.hackathon.HackSync.host_core.dto.HackathonRequestDTO;
import com.hackathon.HackSync.host_core.dto.InviteRequestDTO;
import com.hackathon.HackSync.host_core.entity.Hackathons;
import com.hackathon.HackSync.host_core.responses.HackathonResponse;
import com.hackathon.HackSync.host_core.service.HackathonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import com.hackathon.HackSync.judge_core.dto.ProjectSubmissionResponseDTO;
import com.hackathon.HackSync.participants_core.dto.ParticipantResponseDTO;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/host/hackathon")
public class HackathonController {

    private final HackathonService hackathonService;

    @PostMapping("/create")
    public ResponseEntity<HackathonResponse> createHackathon(@RequestBody HackathonRequestDTO hackathon,
            Principal principal) {
        String email = principal.getName();

        HackathonResponse hackathonResponse = hackathonService.createHackathon(hackathon, email);
        return new ResponseEntity<>(hackathonResponse, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getHackathonById(@PathVariable Long id, Principal principal) {
        String email = principal.getName();
        Hackathons hackathon = hackathonService.getHackathonById(id, email);
        return new ResponseEntity<>(hackathon, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HackathonResponse> updateHackathon(
            @PathVariable Long id,
            @RequestBody HackathonRequestDTO hackathonRequestDTO,
            Principal principal) {
        String email = principal.getName();
        HackathonResponse response = hackathonService.updateHackathon(id, hackathonRequestDTO, email);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/me")
    public ResponseEntity<List<HackathonResponse>> getMyHackathons(Principal principal) {
        String email = principal.getName();
        List<HackathonResponse> hackathons = hackathonService.getMyHackathons(email);
        return new ResponseEntity<>(hackathons, HttpStatus.OK);
    }

    @GetMapping("/{id}/participants")
    public ResponseEntity<List<ParticipantResponseDTO>> getHackathonParticipants(
            @PathVariable Long id,
            Principal principal) {
        String email = principal.getName();
        List<ParticipantResponseDTO> participants = hackathonService.getHackathonParticipants(id, email);
        return new ResponseEntity<>(participants, HttpStatus.OK);
    }

    @GetMapping("/{id}/submissions")
    public ResponseEntity<List<ProjectSubmissionResponseDTO>> getHackathonSubmissions(@PathVariable Long id,
            Principal principal) {
        String email = principal.getName();
        List<ProjectSubmissionResponseDTO> submissions = hackathonService.getHackathonSubmissions(id, email);
        return new ResponseEntity<>(submissions, HttpStatus.OK);
    }

    @PostMapping("/{id}/judges")
    public ResponseEntity<String> addJudge(@PathVariable Long id, Principal principal,
            @RequestBody InviteRequestDTO requestDTO) {
        String email = principal.getName();
        hackathonService.inviteJudge(id, requestDTO, email);
        return new ResponseEntity<>("Judge added successfully", HttpStatus.OK);
    }

    @PostMapping("/{id}/mentors")
    public ResponseEntity<String> addMentor(@PathVariable Long id, Principal principal,
            @RequestBody InviteRequestDTO requestDTO) {
        String email = principal.getName();
        hackathonService.inviteMentor(id, requestDTO, email);
        return new ResponseEntity<>("Mentor added successfully", HttpStatus.OK);
    }

    @PutMapping("/{id}/publish")
    public ResponseEntity<String> publishHackathon(
            @PathVariable Long id,
            Principal principal) {
        String email = principal.getName();
        hackathonService.publishHackathonResults(id, email);
        return new ResponseEntity<>("Hackathon results published successfully", HttpStatus.OK);
    }

}
/*
 * 1. PUT /api/v1/host/hackathons/{id} - Updates the details of a drafted or
 * active
 * hackathon.
 * 2. GET /api/v1/host/hackathons/me - Lists all hackathons created by this
 * specific host.
 * 3. GET /api/v1/host/hackathons/{id}/participants - Returns a list of all
 * registered users for their specific event.
 * 4. GET /api/v1/host/hackathons/{id}/submissions - Returns all static project
 * data submitted before the deadline.
 * 5. POST /api/v1/hackathons/{hackathonId}/judges - host will invite the judge
 * by sending the custom email.
 * 6. POST /api/v1/hackathons/{hackathonId}/mentors - host will invite the
 * mentor by sending the custom email.
 * 7. PUT /api/v1/host/hackathons/{id}/publish - Changes the hackathon status to
 * COMPLETED and makes the winning results public.
 */
