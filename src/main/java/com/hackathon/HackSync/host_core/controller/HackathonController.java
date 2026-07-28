package com.hackathon.HackSync.host_core.controller;

import com.hackathon.HackSync.host_core.dto.HackathonRequestDTO;
import com.hackathon.HackSync.host_core.dto.InviteRequestDTO;
import com.hackathon.HackSync.host_core.responses.HackathonResponse;
import com.hackathon.HackSync.host_core.dto.HackathonFullDetailResponseDTO;
import com.hackathon.HackSync.host_core.service.HackathonService;
import com.hackathon.HackSync.participants_core.dto.HackathonDetailResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import com.hackathon.HackSync.judge_core.dto.EvaluationCriteriaRequestDTO;
import com.hackathon.HackSync.judge_core.dto.EvaluationCriteriaResponseDTO;
import com.hackathon.HackSync.judge_core.dto.ProjectSubmissionResponseDTO;
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
        public ResponseEntity<ApiResponse<HackathonResponse>> createHackathon(
                        @RequestBody HackathonRequestDTO hackathon,
                        Principal principal) {
                String email = principal.getName();

                HackathonResponse hackathonResponse = hackathonService.createHackathon(hackathon, email);
                return new ResponseEntity<>(
                                new ApiResponse<>("Hackathon created successfully", HttpStatus.CREATED,
                                                hackathonResponse),
                                HttpStatus.CREATED);
        }

        @GetMapping("/hackathon/{id}")
        public ResponseEntity<ApiResponse<HackathonDetailResponseDTO>> getHackathonById(@PathVariable Long id,
                        Principal principal) {
                String email = principal.getName();
                HackathonDetailResponseDTO detailResponseDTO = hackathonService.getHackathonById(id, email);
                return new ResponseEntity<>(
                                new ApiResponse<>("Hackathon details fetched successfully", HttpStatus.OK,
                                                detailResponseDTO),
                                HttpStatus.OK);
        }

        @PutMapping("/hackathon/{id}")
        public ResponseEntity<ApiResponse<HackathonResponse>> updateHackathon(
                        @PathVariable Long id,
                        @RequestBody HackathonRequestDTO hackathonRequestDTO,
                        Principal principal) {
                String email = principal.getName();
                HackathonResponse response = hackathonService.updateHackathon(id, hackathonRequestDTO, email);
                return new ResponseEntity<>(
                                new ApiResponse<>("Hackathon updated successfully", HttpStatus.OK, response),
                                HttpStatus.OK);
        }

        @GetMapping("/me")
        public ResponseEntity<ApiResponse<List<HackathonResponse>>> getMyHackathons(Principal principal) {
                String email = principal.getName();
                List<HackathonResponse> hackathons = hackathonService.getMyHackathons(email);
                return new ResponseEntity<>(
                                new ApiResponse<>("Fetched your hackathons successfully", HttpStatus.OK, hackathons),
                                HttpStatus.OK);
        }

        @GetMapping("me/hackathons/details")
        public ResponseEntity<ApiResponse<List<HackathonFullDetailResponseDTO>>> getMyHackathonDetails(
                        Principal principal) {
                String email = principal.getName();
                List<HackathonFullDetailResponseDTO> hackathons = hackathonService.getMyHackathonsDetails(email);
                return new ResponseEntity<>(
                                new ApiResponse<>("Fetched your hackathons details successfully", HttpStatus.OK,
                                                hackathons),
                                HttpStatus.OK);
        }

        @GetMapping("/hackathon/{id}/participants")
        public ResponseEntity<ApiResponse<List<TeamWithParticipantsResponseDTO>>> getHackathonParticipants(
                        @PathVariable Long id,
                        Principal principal) {
                String email = principal.getName();
                List<TeamWithParticipantsResponseDTO> participants = hackathonService.getHackathonParticipants(id,
                                email);
                return new ResponseEntity<>(
                                new ApiResponse<>("Participants fetched successfully", HttpStatus.OK, participants),
                                HttpStatus.OK);
        }

        @GetMapping("/hackathon/{id}/submissions")
        public ResponseEntity<ApiResponse<List<ProjectSubmissionResponseDTO>>> getHackathonSubmissions(
                        @PathVariable Long id,
                        Principal principal) {
                String email = principal.getName();
                List<ProjectSubmissionResponseDTO> submissions = hackathonService.getHackathonSubmissions(id, email);
                return new ResponseEntity<>(
                                new ApiResponse<>("Submissions fetched successfully", HttpStatus.OK, submissions),
                                HttpStatus.OK);
        }

        @PostMapping("/hackathon/{id}/judges")
        public ResponseEntity<ApiResponse<?>> addJudge(@PathVariable Long id, Principal principal,
                        @RequestBody InviteRequestDTO requestDTO) {
                String email = principal.getName();
                hackathonService.inviteJudge(id, requestDTO, email);
                return new ResponseEntity<>(new ApiResponse<>("Judge invited successfully", HttpStatus.OK, null),
                                HttpStatus.OK);
        }

        @PostMapping("/hackathon/{id}/mentors")
        public ResponseEntity<ApiResponse<?>> addMentor(@PathVariable Long id, Principal principal,
                        @RequestBody InviteRequestDTO requestDTO) {
                String email = principal.getName();
                hackathonService.inviteMentor(id, requestDTO, email);
                return new ResponseEntity<>(new ApiResponse<>("Mentor invited successfully", HttpStatus.OK, null),
                                HttpStatus.OK);
        }

        @PutMapping("/hackathon/{id}/publish")
        public ResponseEntity<ApiResponse<?>> publishHackathon(
                        @PathVariable Long id,
                        Principal principal) {
                String email = principal.getName();
                hackathonService.publishHackathonResults(id, email);
                return new ResponseEntity<>(
                                new ApiResponse<>("Hackathon results published successfully", HttpStatus.OK, null),
                                HttpStatus.OK);
        }

        @PostMapping("/upload-image")
        public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
                FileUploadResponse response = hackathonService.uploadImage(file);
                return new ResponseEntity<>(Map.of("fileId", response.fileId(), "url", response.url()), HttpStatus.OK);
        }

        @PostMapping("/hackathon/{id}/evaluation-criteria")
        public ResponseEntity<ApiResponse<EvaluationCriteriaResponseDTO>> createEvaluationCriteria(
                        @PathVariable Long id,
                        @RequestBody EvaluationCriteriaRequestDTO requestDTO,
                        Principal principal) {
                String email = principal.getName();
                EvaluationCriteriaResponseDTO response = hackathonService.createEvaluationCriteria(id, requestDTO,
                                email);
                return new ResponseEntity<>(
                                new ApiResponse<>("Evaluation criteria created successfully", HttpStatus.CREATED,
                                                response),
                                HttpStatus.CREATED);
        }

        @PutMapping("/hackathon/{id}/evaluation-criteria/{criteriaId}")
        public ResponseEntity<ApiResponse<EvaluationCriteriaResponseDTO>> updateEvaluationCriteria(
                        @PathVariable Long id,
                        @PathVariable Long criteriaId, @RequestBody EvaluationCriteriaRequestDTO requestDTO,
                        Principal principal) {
                String email = principal.getName();
                EvaluationCriteriaResponseDTO response = hackathonService.updateEvaluationCriteria(id, criteriaId,
                                requestDTO,
                                email);
                return new ResponseEntity<>(
                                new ApiResponse<>("Evaluation criteria updated successfully", HttpStatus.OK, response),
                                HttpStatus.OK);
        }

        @PutMapping("/hackathon/{id}/submissions/{submissionId}/disqualify")
        public ResponseEntity<ApiResponse<ProjectSubmissionResponseDTO>> disqualifySubmission(@PathVariable Long id,
                        @PathVariable Long submissionId, Principal principal) {
                String email = principal.getName();
                ProjectSubmissionResponseDTO response = hackathonService.disqualifySubmission(id, submissionId, email);
                return new ResponseEntity<>(
                                new ApiResponse<>("Submission disqualified successfully", HttpStatus.OK, response),
                                HttpStatus.OK);
        }

        @GetMapping("/hackathon/{id}/criteria")
        public ResponseEntity<ApiResponse<List<EvaluationCriteriaResponseDTO>>> getEvaluationCriteria(
                        @PathVariable Long id,
                        Principal principal) {
                String email = principal.getName();
                List<EvaluationCriteriaResponseDTO> criteria = hackathonService.getEvaluationCriteria(id, email);
                return new ResponseEntity<>(
                                new ApiResponse<>("Evaluation criteria retrieved successfully", HttpStatus.OK,
                                                criteria),
                                HttpStatus.OK);
        }

        @PutMapping("/hackathon/{id}/judges/{judgeEmail}/assign-super-judge")
        public ResponseEntity<ApiResponse<Void>> assignSuperJudge(
                        @PathVariable Long id,
                        @PathVariable String judgeEmail,
                        Principal principal) {
                String email = principal.getName();
                hackathonService.assignSuperJudge(id, judgeEmail, email);
                return new ResponseEntity<>(new ApiResponse<>("Super judge assigned successfully", HttpStatus.OK, null),
                                HttpStatus.OK);
        }

}