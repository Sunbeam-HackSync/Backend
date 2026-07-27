package com.hackathon.HackSync.judge_core.controller;

import com.hackathon.HackSync.judge_core.dto.AssignedHackathonResponseDTO;
import com.hackathon.HackSync.judge_core.dto.JudgeScoreSubmissionRequestDTO;
import com.hackathon.HackSync.judge_core.dto.WinnerSubmissionRequestDTO;
import com.hackathon.HackSync.judge_core.service.JudgeService;
import com.hackathon.HackSync.utils.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("judge")
@RequiredArgsConstructor
public class JudgeController {

    private final JudgeService judgeService;

    @PutMapping("/invitations/{hackathonId}/status")
    public ResponseEntity<ApiResponse<String>> updateInvitationStatus(
            @PathVariable Long hackathonId,
            @RequestParam String status,
            Principal principal) {
        String message = judgeService.updateInvitationStatus(hackathonId, status, principal.getName());
        return ResponseEntity.ok(new ApiResponse<>(message, HttpStatus.OK, null));
    }

    @PostMapping("/hackathon/{hackathonId}/submit-winners")
    public ResponseEntity<ApiResponse<Void>> submitWinners(
            @PathVariable Long hackathonId,
            @RequestBody List<WinnerSubmissionRequestDTO> winners,
            Principal principal) {
        judgeService.submitWinners(hackathonId, winners, principal.getName());
        return ResponseEntity.ok(new ApiResponse<>("Hackathon winners submitted successfully", HttpStatus.OK, null));
    }

    @GetMapping("/hackathons")
    public ResponseEntity<ApiResponse<List<AssignedHackathonResponseDTO>>> getMyAssignedHackathons(
            Principal principal) {
        List<AssignedHackathonResponseDTO> hackathons = judgeService.getMyAssignedHackathons(principal.getName());
        return ResponseEntity.ok(new ApiResponse<>("Assigned hackathons retrieved successfully", HttpStatus.OK, hackathons));
    }

    @PostMapping("/project/submit-scores")
    public ResponseEntity<ApiResponse<Void>> submitScores(
            @RequestBody JudgeScoreSubmissionRequestDTO dto,
            Principal principal) {
        judgeService.submitScores(dto, principal.getName());
        return ResponseEntity.ok(new ApiResponse<>("Scores submitted successfully", HttpStatus.OK, null));
    }
}
