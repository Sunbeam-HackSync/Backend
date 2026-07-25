package com.hackathon.HackSync.judge_core.controller;

import com.hackathon.HackSync.judge_core.service.JudgeService;
import com.hackathon.HackSync.utils.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

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

    /*
     * GET /judge/assignments - Fetches the clean queue of projects assigned to this
     * specific judge where is_evaluated is FALSE.
     * POST /judge/scores - Submits the numerical score_value and optional text
     * feedback into the scores table, and flips the assignment flag to TRUE.
     */
}
