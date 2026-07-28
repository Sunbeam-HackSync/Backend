package com.hackathon.HackSync.judge_core.service;

import com.hackathon.HackSync.auth.entity.Users;
import com.hackathon.HackSync.auth.repository.UserRepository;
import com.hackathon.HackSync.host_core.entity.HackathonJudges;
import com.hackathon.HackSync.host_core.entity.HackathonStatus;
import com.hackathon.HackSync.host_core.entity.Hackathons;
import com.hackathon.HackSync.host_core.entity.JudgeInvitationStatus;
import com.hackathon.HackSync.host_core.repository.HackathonJudgesRepository;
import com.hackathon.HackSync.judge_core.dto.AssignedHackathonResponseDTO;
import com.hackathon.HackSync.judge_core.dto.EvaluationCriteriaResponseDTO;
import com.hackathon.HackSync.judge_core.dto.JudgeDetailHackathonResponseDTO;
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
import com.hackathon.HackSync.judge_core.dto.ProjectSubmissionResponseDTO;
import com.hackathon.HackSync.judge_core.dto.SuperJudgeSubmissionResponseDTO;
import com.hackathon.HackSync.judge_core.repository.EvaluationCriteriaRepository;
import com.hackathon.HackSync.judge_core.repository.JudgesScoresRepository;
import com.hackathon.HackSync.host_core.repository.HackathonRepository;
import com.hackathon.HackSync.participants_core.dto.HackathonDetailResponseDTO;

import java.util.Comparator;
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
        private final HackathonRepository hackathonRepository;

        public String updateInvitationStatus(Long hackathonId, String statusString, String authenticatedEmail) {
                Users judge = userRepository.findByEmail(authenticatedEmail)
                                .orElseThrow(() -> new UsernameNotFoundException("Judge not found"));

                HackathonJudges hackathonJudge = hackathonJudgesRepository
                                .findByHackathonsId_IdAndJudgeUserId_Id(hackathonId, judge.getId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Hackathon judge invitation not found"));

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

        public void submitWinners(Long hackathonId, List<WinnerSubmissionRequestDTO> winners,
                        String authenticatedEmail) {
                Users judge = userRepository.findByEmail(authenticatedEmail)
                                .orElseThrow(() -> new UsernameNotFoundException("Judge not found"));

                HackathonJudges hackathonJudge = hackathonJudgesRepository
                                .findByHackathonsId_IdAndJudgeUserId_Id(hackathonId, judge.getId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Hackathon judge invitation not found"));

                if (!hackathonJudge.getIsSuperJudge()) {
                        throw new AccessDeniedException("Access Denied: Only SUPER_JUDGE can submit winners");
                }

                Hackathons hackathon = hackathonJudge.getHackathonsId();

                if (hackathon.getHackathonStatus() != HackathonStatus.COMPLETED) {
                        throw new RuntimeException("Hackathon results are not yet completed");
                }

                // Save each winner
                for (WinnerSubmissionRequestDTO winnerDto : winners) {
                        ProjectSubmissions submission = projectSubmissionRepository
                                        .findById(winnerDto.getSubmissionId())
                                        .orElseThrow(() -> new ResourceNotFoundException(
                                                        "Submission not found: " + winnerDto.getSubmissionId()));

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
                                        .isSuperJudge(assignment.getIsSuperJudge() != null
                                                        ? assignment.getIsSuperJudge()
                                                        : false)
                                        .build();
                }).toList();
        }

        public void submitScores(JudgeScoreSubmissionRequestDTO dto, String authenticatedEmail) {
                Users judge = userRepository.findByEmail(authenticatedEmail)
                                .orElseThrow(() -> new UsernameNotFoundException("Judge not found"));

                ProjectSubmissions project = projectSubmissionRepository.findById(dto.getProjectId())
                                .orElseThrow(() -> new ResourceNotFoundException("Project submission not found"));

                if (project.getAssignedJudgeId() == null
                                || !project.getAssignedJudgeId().getId().equals(judge.getId())) {
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
                                        .orElseThrow(() -> new ResourceNotFoundException(
                                                        "Evaluation criteria not found: "
                                                                        + scoreEntry.getCriteriaId()));

                        JudgesScores judgesScore = new JudgesScores();
                        judgesScore.setJudgeId(judge);
                        judgesScore.setProjectId(project);
                        judgesScore.setCriteriaId(criteria);
                        judgesScore.setScoreGiven(scoreEntry.getScoreGiven());
                        judgesScore.setFeedBackNotes(scoreEntry.getFeedbackNotes());

                        judgesScoresRepository.save(judgesScore);
                }
        }

        public List<EvaluationCriteriaResponseDTO> getEvaluationCriteria(Long hackathonId, String authenticatedEmail) {
                Users judge = userRepository.findByEmail(authenticatedEmail)
                                .orElseThrow(() -> new UsernameNotFoundException("Judge not found"));

                // Verify that the judge is assigned to this hackathon
                hackathonJudgesRepository.findByHackathonsId_IdAndJudgeUserId_Id(hackathonId, judge.getId())
                                .orElseThrow(() -> new AccessDeniedException(
                                                "Access Denied: You are not assigned as a judge for this hackathon"));

                List<EvaluationCriteria> criteriaList = evaluationCriteriaRepository.findByHackathonId_Id(hackathonId);

                return criteriaList.stream().map(criteria -> EvaluationCriteriaResponseDTO.builder()
                                .id(criteria.getId())
                                .hackathonId(criteria.getHackathonId().getId())
                                .criteriaName(criteria.getCriteriaName())
                                .description(criteria.getDescription())
                                .maxScore(criteria.getMaxScore())
                                .build()).toList();
        }

        public List<ProjectSubmissionResponseDTO> getAssignedSubmissions(Long hackathonId, String authenticatedEmail) {
                Users judge = userRepository.findByEmail(authenticatedEmail)
                                .orElseThrow(() -> new UsernameNotFoundException("Judge not found"));

                // Verify that the judge is assigned to this hackathon
                hackathonJudgesRepository.findByHackathonsId_IdAndJudgeUserId_Id(hackathonId, judge.getId())
                                .orElseThrow(() -> new AccessDeniedException(
                                                "Access Denied: You are not assigned as a judge for this hackathon"));

                List<ProjectSubmissions> submissions = projectSubmissionRepository
                                .findByHackathonId_IdAndAssignedJudgeId_Id(hackathonId, judge.getId());

                return submissions.stream().map(submission -> {
                        boolean isEvaluated = judgesScoresRepository.existsByProjectId_IdAndJudgeId_Id(submission.getId(), judge.getId());
                        return ProjectSubmissionResponseDTO.builder()
                                .id(submission.getId())
                                .projectTitle(submission.getProjectTitle())
                                .tagLine(submission.getTagLine())
                                .description(submission.getDescription())
                                .githubRepoUrl(submission.getGithubRepoUrl())
                                .liveDemoUrl(submission.getLiveDemoUrl())
                                .youtubeUrl(submission.getYoutubeUrl())
                                .submissionStatus(submission.getSubmissionStatus())
                                .teamId(submission.getTeamsId().getId())
                                .teamName(submission.getTeamsId().getTeamName())
                                .submittedAt(submission.getSubmittedAt())
                                .isEvaluated(isEvaluated)
                                .build();
                }).toList();
        }

        public List<SuperJudgeSubmissionResponseDTO> getAllSubmissionsForSuperJudge(Long hackathonId, String search,
                        String authenticatedEmail) {
                Users judge = userRepository.findByEmail(authenticatedEmail)
                                .orElseThrow(() -> new UsernameNotFoundException("Judge not found"));

                HackathonJudges hackathonJudge = hackathonJudgesRepository
                                .findByHackathonsId_IdAndJudgeUserId_Id(hackathonId, judge.getId())
                                .orElseThrow(() -> new AccessDeniedException(
                                                "Access Denied: You are not assigned as a judge for this hackathon"));

                if (!hackathonJudge.getIsSuperJudge()) {
                        throw new AccessDeniedException("Access Denied: Only SUPER_JUDGE can view all submissions");
                }

                List<ProjectSubmissions> submissions = projectSubmissionRepository.findByHackathonId(hackathonId);

                if (search != null && !search.trim().isEmpty()) {
                        String lowerSearch = search.toLowerCase();
                        submissions = submissions.stream()
                                        .filter(s -> (s.getProjectTitle() != null
                                                        && s.getProjectTitle().toLowerCase().contains(lowerSearch))
                                                        || (s.getTeamsId() != null
                                                                        && s.getTeamsId().getTeamName() != null
                                                                        && s.getTeamsId().getTeamName().toLowerCase()
                                                                                        .contains(lowerSearch))
                                                        || (s.getTagLine() != null && s.getTagLine().toLowerCase()
                                                                        .contains(lowerSearch)))
                                        .toList();
                }

                return submissions.stream().map(submission -> {
                        boolean isEvaluated = judgesScoresRepository.existsByProjectId_IdAndJudgeId_Id(submission.getId(), judge.getId());
                        ProjectSubmissionResponseDTO submissionDTO = ProjectSubmissionResponseDTO.builder()
                                .id(submission.getId())
                                .projectTitle(submission.getProjectTitle())
                                .tagLine(submission.getTagLine())
                                .description(submission.getDescription())
                                .githubRepoUrl(submission.getGithubRepoUrl())
                                .liveDemoUrl(submission.getLiveDemoUrl())
                                .youtubeUrl(submission.getYoutubeUrl())
                                .submissionStatus(submission.getSubmissionStatus())
                                .teamId(submission.getTeamsId().getId())
                                .teamName(submission.getTeamsId().getTeamName())
                                .submittedAt(submission.getSubmittedAt())
                                .isEvaluated(isEvaluated)
                                .build();

                        List<JudgesScores> scores = judgesScoresRepository.findByProjectId_Id(submission.getId());
                        
                        java.util.Map<Users, List<JudgesScores>> groupedScores = scores.stream()
                                .collect(java.util.stream.Collectors.groupingBy(JudgesScores::getJudgeId));

                        List<SuperJudgeSubmissionResponseDTO.JudgeEvaluationDTO> evaluations = new java.util.ArrayList<>();
                        double totalSubmissionScore = 0;

                        for (java.util.Map.Entry<Users, List<JudgesScores>> entry : groupedScores.entrySet()) {
                                Users evalJudge = entry.getKey();
                                List<JudgesScores> judgeScores = entry.getValue();

                                List<SuperJudgeSubmissionResponseDTO.ScoreDetailDTO> scoreDetails = new java.util.ArrayList<>();
                                double judgeTotal = 0;
                                
                                for (JudgesScores js : judgeScores) {
                                        scoreDetails.add(SuperJudgeSubmissionResponseDTO.ScoreDetailDTO.builder()
                                                .criteriaId(js.getCriteriaId().getId())
                                                .criteriaName(js.getCriteriaId().getCriteriaName())
                                                .maxScore(js.getCriteriaId().getMaxScore())
                                                .scoreGiven(js.getScoreGiven())
                                                .feedbackNotes(js.getFeedBackNotes())
                                                .build());
                                        judgeTotal += js.getScoreGiven();
                                }

                                evaluations.add(SuperJudgeSubmissionResponseDTO.JudgeEvaluationDTO.builder()
                                        .judgeId(evalJudge.getId())
                                        .judgeEmail(evalJudge.getEmail())
                                        .scoreDetails(scoreDetails)
                                        .judgeTotalScore(judgeTotal)
                                        .build());
                                
                                totalSubmissionScore += judgeTotal;
                        }

                        return SuperJudgeSubmissionResponseDTO.builder()
                                .submission(submissionDTO)
                                .evaluations(evaluations)
                                .totalScore(totalSubmissionScore)
                                .build();
                }).sorted(Comparator.comparingDouble(SuperJudgeSubmissionResponseDTO::getTotalScore).reversed())
                  .toList();
        }

        public JudgeDetailHackathonResponseDTO getHackathonDetailsById(Long hackathonId, String authenticatedEmail) {
                Users judge = userRepository.findByEmail(authenticatedEmail)
                                .orElseThrow(() -> new UsernameNotFoundException("Judge not found"));

                // Verify that the judge is assigned to this hackathon
                HackathonJudges hackathonJudge = hackathonJudgesRepository
                                .findByHackathonsId_IdAndJudgeUserId_Id(hackathonId, judge.getId())
                                .orElseThrow(() -> new AccessDeniedException(
                                                "Access Denied: You are not assigned as a judge for this hackathon"));

                Hackathons hackathon = hackathonRepository.findById(hackathonId)
                                .orElseThrow(() -> new ResourceNotFoundException("Hackathon does not exist"));

                HackathonDetailResponseDTO hackathonDetails = HackathonDetailResponseDTO.builder()
                                .id(hackathon.getId())
                                .title(hackathon.getTitle())
                                .tagline(hackathon.getTagline())
                                .description(hackathon.getDescription())
                                .bannerImageUrl(hackathon.getBannerImageUrl())
                                .profileImageUrl(hackathon.getProfileImageUrl())
                                .minTeamSize(hackathon.getMinTeamSize())
                                .maxTeamSize(hackathon.getMaxTeamSize())
                                .registrationStart(hackathon.getRegistrationStart())
                                .registrationEnd(hackathon.getRegistrationEnd())
                                .hackathonStart(hackathon.getHackathonStart())
                                .hackathonEnd(hackathon.getHackathonEnd())
                                .hackathonStatus(hackathon.getHackathonStatus())
                                .faq(hackathon.getFaq())
                                .rules(hackathon.getRules())
                                .resultDeclarationDate(hackathon.getResultDeclarationDate())
                                .feedBackNotes(null)
                                .build();

                return JudgeDetailHackathonResponseDTO.builder()
                                .hackathons(hackathonDetails)
                                .judgeUserId(judge.getId())
                                .assignedAt(hackathonJudge.getAssignedAt())
                                .status(hackathonJudge.getStatus())
                                .isSuperJudge(hackathonJudge.getIsSuperJudge())
                                .build();
        }
}
