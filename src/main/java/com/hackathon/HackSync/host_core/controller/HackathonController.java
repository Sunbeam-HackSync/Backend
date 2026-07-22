package com.hackathon.HackSync.host_core.controller;

import com.hackathon.HackSync.host_core.dto.HackathonRequestDTO;
import com.hackathon.HackSync.host_core.dto.InviteRequestDTO;
import com.hackathon.HackSync.host_core.entity.Hackathons;
import com.hackathon.HackSync.host_core.responses.HackathonResponse;
import com.hackathon.HackSync.host_core.service.HackathonService;
import com.hackathon.HackSync.participants_core.dto.HackathonDetailResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import com.hackathon.HackSync.judge_core.dto.ProjectSubmissionResponseDTO;
import com.hackathon.HackSync.participants_core.dto.ParticipantResponseDTO;
import com.hackathon.HackSync.participants_core.dto.TeamWithParticipantsResponseDTO;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;
import io.imagekit.models.files.FileUploadResponse;
import com.hackathon.HackSync.utils.dto.ApiResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/host")
public class HackathonController {

    private final HackathonService hackathonService;

    @PostMapping("/hackathon/create")
    public ResponseEntity<ApiResponse<HackathonResponse>> createHackathon(@RequestBody HackathonRequestDTO hackathon,
            Principal principal) {
        String email = principal.getName();

        HackathonResponse hackathonResponse = hackathonService.createHackathon(hackathon, email);
        return new ResponseEntity<>(
                new ApiResponse<>("Hackathon created successfully", HttpStatus.CREATED, hackathonResponse),
                HttpStatus.CREATED);
    }

    @GetMapping("/hackathon/{id}")
    public ResponseEntity<ApiResponse<HackathonDetailResponseDTO>> getHackathonById(@PathVariable Long id,
            Principal principal) {
        String email = principal.getName();
        HackathonDetailResponseDTO detailResponseDTO = hackathonService.getHackathonById(id, email);
        return new ResponseEntity<>(
                new ApiResponse<>("Hackathon details fetched successfully", HttpStatus.OK, detailResponseDTO),
                HttpStatus.OK);
    }

    @PutMapping("/hackathon/{id}")
    public ResponseEntity<ApiResponse<HackathonResponse>> updateHackathon(
            @PathVariable Long id,
            @RequestBody HackathonRequestDTO hackathonRequestDTO,
            Principal principal) {
        String email = principal.getName();
        HackathonResponse response = hackathonService.updateHackathon(id, hackathonRequestDTO, email);
        return new ResponseEntity<>(new ApiResponse<>("Hackathon updated successfully", HttpStatus.OK, response),
                HttpStatus.OK);
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<HackathonResponse>>> getMyHackathons(Principal principal) {
        String email = principal.getName();
        List<HackathonResponse> hackathons = hackathonService.getMyHackathons(email);
        return new ResponseEntity<>(
                new ApiResponse<>("Fetched your hackathons successfully", HttpStatus.OK, hackathons), HttpStatus.OK);
    }

    @GetMapping("/hackathon/{id}/participants")
    public ResponseEntity<ApiResponse<List<TeamWithParticipantsResponseDTO>>> getHackathonParticipants(
            @PathVariable Long id,
            Principal principal) {
        String email = principal.getName();
        List<TeamWithParticipantsResponseDTO> participants = hackathonService.getHackathonParticipants(id, email);
        return new ResponseEntity<>(new ApiResponse<>("Participants fetched successfully", HttpStatus.OK, participants),
                HttpStatus.OK);
    }

    @GetMapping("/hackathon/{id}/submissions")
    public ResponseEntity<ApiResponse<List<ProjectSubmissionResponseDTO>>> getHackathonSubmissions(
            @PathVariable Long id,
            Principal principal) {
        String email = principal.getName();
        List<ProjectSubmissionResponseDTO> submissions = hackathonService.getHackathonSubmissions(id, email);
        return new ResponseEntity<>(new ApiResponse<>("Submissions fetched successfully", HttpStatus.OK, submissions),
                HttpStatus.OK);
    }

    @PostMapping("/hackathon/{id}/judges")
    public ResponseEntity<ApiResponse<?>> addJudge(@PathVariable Long id, Principal principal,
            @RequestBody InviteRequestDTO requestDTO) {
        String email = principal.getName();
        hackathonService.inviteJudge(id, requestDTO, email);
        return new ResponseEntity<>(new ApiResponse<>("Judge added successfully", HttpStatus.OK, null), HttpStatus.OK);
    }

    @PostMapping("/hackathon/{id}/mentors")
    public ResponseEntity<ApiResponse<?>> addMentor(@PathVariable Long id, Principal principal,
            @RequestBody InviteRequestDTO requestDTO) {
        String email = principal.getName();
        hackathonService.inviteMentor(id, requestDTO, email);
        return new ResponseEntity<>(new ApiResponse<>("Mentor added successfully", HttpStatus.OK, null), HttpStatus.OK);
    }

    @PutMapping("/hackathon/{id}/publish")
    public ResponseEntity<ApiResponse<?>> publishHackathon(
            @PathVariable Long id,
            Principal principal) {
        String email = principal.getName();
        hackathonService.publishHackathonResults(id, email);
        return new ResponseEntity<>(new ApiResponse<>("Hackathon results published successfully", HttpStatus.OK, null),
                HttpStatus.OK);
    }

    @PostMapping("/upload-image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        FileUploadResponse response = hackathonService.uploadImage(file);
        return new ResponseEntity<>(Map.of("fileId", response.fileId(), "url", response.url()), HttpStatus.OK);
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
