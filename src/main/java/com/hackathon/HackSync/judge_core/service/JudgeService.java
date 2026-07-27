package com.hackathon.HackSync.judge_core.service;

import com.hackathon.HackSync.auth.entity.Users;
import com.hackathon.HackSync.auth.repository.UserRepository;
import com.hackathon.HackSync.host_core.entity.HackathonJudges;
import com.hackathon.HackSync.host_core.entity.HackathonStatus;
import com.hackathon.HackSync.host_core.entity.Hackathons;
import com.hackathon.HackSync.host_core.entity.JudgeInvitationStatus;
import com.hackathon.HackSync.host_core.repository.HackathonJudgesRepository;
import com.hackathon.HackSync.judge_core.dto.AssignedHackathonResponseDTO;
import com.hackathon.HackSync.judge_core.dto.WinnerSubmissionRequestDTO;
import com.hackathon.HackSync.judge_core.entity.HackathonWinners;
import com.hackathon.HackSync.judge_core.entity.ProjectSubmissions;
import com.hackathon.HackSync.judge_core.repository.HackathonWinnersRepository;
import com.hackathon.HackSync.judge_core.repository.ProjectSubmissionRepository;
import com.hackathon.HackSync.utils.exception.AccessDeniedException;
import com.hackathon.HackSync.utils.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import com.hackathon.HackSync.judge_core.entity.EvaluationCriteria;
import com.hackathon.HackSync.judge_core.entity.JudgesScores;
import com.hackathon.HackSync.judge_core.dto.JudgeScoreSubmissionRequestDTO;
import com.hackathon.HackSync.judge_core.repository.EvaluationCriteriaRepository;
import com.hackathon.HackSync.judge_core.repository.JudgesScoresRepository;

import java.util.List;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class JudgeService {

    private final UserRepository userRepository;
    private final HackathonJudgesRepository hackathonJudgesRepository;
    private final HackathonWinnersRepository hackathonWinnersRepository;
    private final ProjectSubmissionRepository projectSubmissionRepository;
    private final JudgesScoresRepository judgesScoresRepository;
    private final EvaluationCriteriaRepository evaluationCriteriaRepository;

    public String updateInvitationStatus(Long hackathonId, String statusString, String authenticatedEmail) {
        Users judge = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Judge not found"));

        HackathonJudges hackathonJudge = hackathonJudgesRepository.findByHackathonsId_IdAndJudgeUserId_Id(hackathonId, judge.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Hackathon judge invitation not found"));

        JudgeInvitationStatus status;
        try {
            status = JudgeInvitationStatus.valueOf(statusString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid judge status");
        }

        hackathonJudge.setStatus(status);
        hackathonJudgesRepository.save(hackathonJudge);

        return "Judge invitation status updated to " + status.name();
    }

    public void submitWinners(Long hackathonId, List<WinnerSubmissionRequestDTO> winners, String authenticatedEmail) {
        Users judge = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Judge not found"));

        HackathonJudges hackathonJudge = hackathonJudgesRepository.findByHackathonsId_IdAndJudgeUserId_Id(hackathonId, judge.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Hackathon judge invitation not found"));

        if (!hackathonJudge.isSuperJudge()) {
            throw new AccessDeniedException("Access Denied: Only SUPER_JUDGE can submit winners");
        }

        Hackathons hackathon = hackathonJudge.getHackathonsId();
        
        if (hackathon.getHackathonStatus() != HackathonStatus.COMPLETED) {
            throw new RuntimeException("Hackathon results are not yet completed");
        }

        // Save each winner
        for (WinnerSubmissionRequestDTO winnerDto : winners) {
            ProjectSubmissions submission = projectSubmissionRepository.findById(winnerDto.getSubmissionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Submission not found: " + winnerDto.getSubmissionId()));

            if (!submission.getHackathonId().getId().equals(hackathonId)) {
                throw new RuntimeException("Submission does not belong to this hackathon");
            }

            HackathonWinners winner = new HackathonWinners();
            winner.setHackathonId(hackathon);
            winner.setSubmissionId(submission);
            winner.setCategoryName(winnerDto.getCategoryName());
            
            hackathonWinnersRepository.save(winner);
        }
    }

    public List<AssignedHackathonResponseDTO> getMyAssignedHackathons(String authenticatedEmail) {
        Users judge = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Judge not found"));

        List<HackathonJudges> assignments = hackathonJudgesRepository.findByJudgeUserId_Id(judge.getId());

        return assignments.stream().map(assignment -> {
            Hackathons hackathon = assignment.getHackathonsId();
            return AssignedHackathonResponseDTO.builder()
                    .hackathonId(hackathon.getId())
                    .title(hackathon.getTitle())
                    .tagline(hackathon.getTagline())
                    .hackathonStatus(hackathon.getHackathonStatus())
                    .hackathonStarts(hackathon.getHackathonStart())
                    .hackathonEnds(hackathon.getHackathonEnd())
                    .invitationStatus(assignment.getStatus())
                    .isSuperJudge(assignment.isSuperJudge())
                    .build();
        }).toList();
    }

    public void submitScores(JudgeScoreSubmissionRequestDTO dto, String authenticatedEmail) {
        Users judge = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Judge not found"));

        ProjectSubmissions project = projectSubmissionRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project submission not found"));

        if (project.getAssignedJudgeId() == null || !project.getAssignedJudgeId().getId().equals(judge.getId())) {
            throw new AccessDeniedException("You are not assigned to evaluate this project");
        }

        List<JudgesScores> existingScores = judgesScoresRepository.findByProjectId_Id(project.getId());
        for (JudgesScores score : existingScores) {
            if (score.getJudgeId().getId().equals(judge.getId())) {
                throw new RuntimeException("You have already scored this project.");
            }
        }

        for (JudgeScoreSubmissionRequestDTO.ScoreEntryDTO scoreEntry : dto.getScores()) {
            EvaluationCriteria criteria = evaluationCriteriaRepository.findById(scoreEntry.getCriteriaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Evaluation criteria not found: " + scoreEntry.getCriteriaId()));

            JudgesScores judgesScore = new JudgesScores();
            judgesScore.setJudgeId(judge);
            judgesScore.setProjectId(project);
            judgesScore.setCriteriaId(criteria);
            judgesScore.setScoreGiven(scoreEntry.getScoreGiven());
            judgesScore.setFeedBackNotes(scoreEntry.getFeedbackNotes());
            
            judgesScoresRepository.save(judgesScore);
        }
    }
}
