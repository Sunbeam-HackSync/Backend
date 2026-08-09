package com.hackathon.HackSync.participants_core.service;

import com.hackathon.HackSync.auth.entity.Users;
import com.hackathon.HackSync.auth.repository.UserRepository;
import com.hackathon.HackSync.host_core.entity.HackathonStatus;
import com.hackathon.HackSync.host_core.entity.Hackathons;
import com.hackathon.HackSync.host_core.repository.HackathonRepository;
import com.hackathon.HackSync.participants_core.dto.HackathonDetailResponseDTO;
import com.hackathon.HackSync.participants_core.dto.HackathonWinnerResponseDTO;
import com.hackathon.HackSync.participants_core.dto.TeamRequestDTO;
import com.hackathon.HackSync.participants_core.dto.TeamResponseDTO;
import com.hackathon.HackSync.participants_core.entity.TeamMembers;
import com.hackathon.HackSync.participants_core.entity.Teams;
import com.hackathon.HackSync.judge_core.entity.ProjectSubmissions;
import com.hackathon.HackSync.judge_core.entity.ProjectSubmissionStatus;
import com.hackathon.HackSync.judge_core.repository.ProjectSubmissionRepository;
import com.hackathon.HackSync.participants_core.dto.TeamUpdateRequestDTO;
import com.hackathon.HackSync.participants_core.dto.SubmissionRequestDTO;
import com.hackathon.HackSync.participants_core.dto.SubmissionResponseDTO;
import com.hackathon.HackSync.participants_core.dto.ParticipantResponseDTO;
import com.hackathon.HackSync.participants_core.dto.TeamWithParticipantsResponseDTO;
import com.hackathon.HackSync.participants_core.repository.TeamMemberRepository;
import com.hackathon.HackSync.participants_core.repository.TeamRepository;
import com.hackathon.HackSync.judge_core.repository.JudgesScoresRepository;
import com.hackathon.HackSync.judge_core.repository.HackathonWinnersRepository;
import com.hackathon.HackSync.judge_core.entity.JudgesScores;
import com.hackathon.HackSync.judge_core.entity.HackathonWinners;
import com.hackathon.HackSync.participants_core.entity.ParticipantsProfiles;
import com.hackathon.HackSync.participants_core.repository.ParticipantsProfilesRepository;
import com.hackathon.HackSync.participants_core.dto.ParticipantProfileDTO;
import com.hackathon.HackSync.participants_core.dto.ParticipantResultResponseDTO;
import com.hackathon.HackSync.participants_core.dto.HackathonWithTeamDetailsResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
@Transactional
public class ParticipantService {

    private final UserRepository userRepository;
    private final HackathonRepository hackathonRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final ProjectSubmissionRepository projectSubmissionRepository;
    private final JudgesScoresRepository judgesScoresRepository;
    private final HackathonWinnersRepository hackathonWinnersRepository;
    private final ParticipantsProfilesRepository participantsProfilesRepository;

    @Transactional
    public void addMember(Long teamId,
                          String memberEmail,
                          String leaderEmail) {

        // Find team
        Teams team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        // Logged-in user (leader)
        Users leader = userRepository.findByEmail(leaderEmail)
                .orElseThrow(() -> new RuntimeException("Leader not found"));

        // Verify leader
        TeamMembers leaderRecord = teamMemberRepository
                .findByTeamsIdAndUserId(team, leader)
                .orElseThrow(() -> new RuntimeException("You are not a member of this team"));

        if (!leaderRecord.isTeamLeader()) {
            throw new RuntimeException("Only the team leader can add members");
        }

        // User to add
        Users member = userRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new RuntimeException("Participant not found"));

        // Cannot add yourself
        if (leader.getId().equals(member.getId())) {
            throw new RuntimeException("Leader is already a member of the team");
        }

        // Already in this team?
        if (teamMemberRepository.existsByTeamsIdAndUserId(team, member)) {
            throw new RuntimeException("Participant already exists in this team");
        }

        // Already in another team of this hackathon?
        if (teamMemberRepository.existsByTeamsIdHackathonIdIdAndUserIdId(team.getHackathonId().getId(),
                member.getId())) {
            throw new RuntimeException("Participant already belongs to another team in this hackathon");
        }

        // Add member
        TeamMembers teamMember = new TeamMembers();
        teamMember.setTeamsId(team);
        teamMember.setUserId(member);
        teamMember.setTeamLeader(false);
        teamMember.setJoinedAt(LocalDateTime.now());

        teamMemberRepository.save(teamMember);
    }

    public Page<HackathonDetailResponseDTO> getDiscoveryFeed(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Hackathons> hackathons = hackathonRepository.findByHackathonStatusIn(
                List.of(HackathonStatus.ACTIVE, HackathonStatus.APPROVED), pageable);

        return hackathons.map(hackathon -> HackathonDetailResponseDTO.builder()
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
                .build());
    }

    public HackathonDetailResponseDTO getPublicHackathonDetail(Long id) {

        Hackathons hackathon = hackathonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hackathon does not exists"));

        if (hackathon.getHackathonStatus() == HackathonStatus.DRAFT) {
            throw new RuntimeException("Access Denied: Hackathon is not published yet");
        }

        return HackathonDetailResponseDTO.builder()
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
                .build();
    }

    public List<HackathonDetailResponseDTO> getMyHackathons(String authenticatedEmail) {
        List<Hackathons> participatedHackathons = teamMemberRepository
                .findParticipatedHackathonsByEmail(authenticatedEmail);

        return participatedHackathons.stream()
                .map(hackathon -> HackathonDetailResponseDTO.builder()
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
                        .build())
                .toList();
    }

    public TeamWithParticipantsResponseDTO seeMyTeamDetails(Long teamId, String authenticatedEmail) {
        Users user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Teams team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        // Verify user is in the team
        boolean isMember = teamMemberRepository.existsByTeamsIdAndUserId(team, user);
        if (!isMember) {
            throw new RuntimeException("You are not a member of this team");
        }

        List<TeamMembers> members = teamMemberRepository.findByTeamsId(team);

        List<ParticipantResponseDTO> participantDTOs = members.stream()
                .map(m -> {
                    String fullName = participantsProfilesRepository
                            .findByUserId_Id(m.getUserId().getId())
                            .map(ParticipantsProfiles::getFullName)
                            .orElse(m.getUserId().getEmail().split("@")[0]);

                    return ParticipantResponseDTO.builder()
                            .userId(m.getUserId().getId())
                            .email(m.getUserId().getEmail())
                            .fullName(fullName)
                            .teamId(m.getTeamsId().getId())
                            .teamName(m.getTeamsId().getTeamName())
                            .isTeamLeader(m.isTeamLeader())
                            .build();
                })
                .toList();

        return TeamWithParticipantsResponseDTO.builder()
                .teamId(team.getId())
                .teamName(team.getTeamName())
                .participants(participantDTOs)
                .build();
    }

    public TeamResponseDTO createTeam(TeamRequestDTO requestDTO, String authenticatedEmail) {

        // Find logged-in user
        Users user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Find hackathon
        Hackathons hackathon = hackathonRepository.findById(requestDTO.getHackathonId())
                .orElseThrow(() -> new RuntimeException("Hackathon not found"));

        // Validate hackathon status and registration dates
        LocalDateTime now = LocalDateTime.now();

        if (hackathon.getHackathonStatus() != HackathonStatus.APPROVED) {
            throw new RuntimeException("Teams can only be created for APPROVED hackathons");
        }

        if (hackathon.getRegistrationStart() != null && now.isBefore(hackathon.getRegistrationStart())) {
            throw new RuntimeException("Registration for this hackathon has not started yet");
        }

        if (hackathon.getRegistrationEnd() != null && now.isAfter(hackathon.getRegistrationEnd())) {
            throw new RuntimeException("Registration for this hackathon has ended");
        }

        // Check if user already belongs to a team in this hackathon
        boolean alreadyRegistered = teamMemberRepository.existsByTeamsIdHackathonIdIdAndUserIdId(
                hackathon.getId(),
                user.getId());

        if (alreadyRegistered) {
            throw new RuntimeException("You are already registered in a team for this hackathon");
        }

        // Create Team
        Teams team = new Teams();
        team.setHackathonId(hackathon);
        team.setTeamName(requestDTO.getTeamName());
        team.setLookingForMembers(requestDTO.isLookingForMembers());
        team.setSkillsNeeded(requestDTO.getSkillsNeeded());

        Teams savedTeam = teamRepository.save(team);

        // Add creator as team leader
        TeamMembers leader = new TeamMembers();
        leader.setTeamsId(savedTeam);
        leader.setUserId(user);
        leader.setTeamLeader(true);
        leader.setJoinedAt(LocalDateTime.now());

        teamMemberRepository.save(leader);

        // Build response
        TeamResponseDTO response = new TeamResponseDTO();
        response.setTeamId(savedTeam.getId());
        response.setHackathonId(hackathon.getId());
        response.setTeamName(savedTeam.getTeamName());
        response.setLookingForMembers(savedTeam.isLookingForMembers());
        response.setSkillsNeeded(savedTeam.getSkillsNeeded());
        response.setLeaderId(user.getId());

        return response;
    }

    public ParticipantResultResponseDTO getHackathonResult(Long hackathonId, String authenticatedEmail) {
        Users user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Hackathons hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new RuntimeException("Hackathon not found"));

        if (hackathon.getHackathonStatus() != HackathonStatus.PUBLISHED) {
            throw new RuntimeException("Results are not yet published for this hackathon");
        }

        TeamMembers teamMember = teamMemberRepository.findByHackathonIdAndUserId(hackathonId, user.getId())
                .orElseThrow(() -> new RuntimeException("You did not participate in this hackathon"));

        Teams team = teamMember.getTeamsId();

        ProjectSubmissions submission = projectSubmissionRepository.findByTeamsId(team)
                .orElseThrow(() -> new RuntimeException("No project submission found for your team"));

        List<JudgesScores> scores = judgesScoresRepository.findByProjectId_Id(submission.getId());

        double totalScore = 0;
        List<ParticipantResultResponseDTO.ScoreDetailDTO> scoreDetails = new ArrayList<>();
        for (JudgesScores js : scores) {
            scoreDetails.add(ParticipantResultResponseDTO.ScoreDetailDTO.builder()
                    .criteriaName(js.getCriteriaId().getCriteriaName())
                    .maxScore(js.getCriteriaId().getMaxScore())
                    .scoreGiven(js.getScoreGiven())
                    .feedbackNotes(js.getFeedBackNotes())
                    .build());
            totalScore += js.getScoreGiven();
        }

        HackathonWinners winner = hackathonWinnersRepository.findBySubmissionId_Id(submission.getId())
                .orElse(null);

        return ParticipantResultResponseDTO.builder()
                .hackathonId(hackathon.getId())
                .hackathonTitle(hackathon.getTitle())
                .teamName(team.getTeamName())
                .projectName(submission.getProjectTitle())
                .isWinner(winner != null)
                .awardCategory(winner != null ? winner.getCategoryName() : null)
                .scores(scoreDetails)
                .totalScore(totalScore)
                .build();
    }

    public ParticipantProfileDTO createProfile(ParticipantProfileDTO request, String authenticatedEmail) {
        Users user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (participantsProfilesRepository.findByUserId_Id(user.getId()).isPresent()) {
            throw new RuntimeException("Profile already exists. Use update instead.");
        }

        ParticipantsProfiles newProfile = new ParticipantsProfiles();
        newProfile.setUserId(user);
        newProfile.setFullName(request.getFullName());
        newProfile.setAvatarURL(request.getAvatarURL());
        newProfile.setBio(request.getBio());
        newProfile.setGithubURL(request.getGithubURL());
        newProfile.setLinkedInURL(request.getLinkedInURL());
        newProfile.setXURL(request.getXURL());
        newProfile.setTechSkills(request.getTechSkills());

        participantsProfilesRepository.save(newProfile);

        return request;
    }

    public ParticipantProfileDTO getProfile(String authenticatedEmail) {
        Users user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ParticipantsProfiles profile = participantsProfilesRepository.findByUserId_Id(user.getId())
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        return ParticipantProfileDTO.builder()
                .fullName(profile.getFullName())
                .avatarURL(profile.getAvatarURL())
                .bio(profile.getBio())
                .githubURL(profile.getGithubURL())
                .linkedInURL(profile.getLinkedInURL())
                .xURL(profile.getXURL())
                .techSkills(profile.getTechSkills())
                .build();
    }

    public ParticipantProfileDTO updateProfile(ParticipantProfileDTO request, String authenticatedEmail) {
        Users user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ParticipantsProfiles profile = participantsProfilesRepository.findByUserId_Id(user.getId())
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        profile.setFullName(request.getFullName());
        profile.setAvatarURL(request.getAvatarURL());
        profile.setBio(request.getBio());
        profile.setGithubURL(request.getGithubURL());
        profile.setLinkedInURL(request.getLinkedInURL());
        profile.setXURL(request.getXURL());
        profile.setTechSkills(request.getTechSkills());

        participantsProfilesRepository.save(profile);

        return request;
    }

    public TeamResponseDTO updateTeam(Long teamId, TeamUpdateRequestDTO requestDTO, String leaderEmail) {
        Teams team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        Users leader = userRepository.findByEmail(leaderEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        TeamMembers leaderRecord = teamMemberRepository.findByTeamsIdAndUserId(team, leader)
                .orElseThrow(() -> new RuntimeException("You are not a member of this team"));

        if (!leaderRecord.isTeamLeader()) {
            throw new RuntimeException("Only the team leader can update team details");
        }

        if (requestDTO.getIsLookingForMembers() != null) {
            team.setLookingForMembers(requestDTO.getIsLookingForMembers());
        }

        if (requestDTO.getSkillsNeeded() != null) {
            team.setSkillsNeeded(requestDTO.getSkillsNeeded());
        }

        Teams updatedTeam = teamRepository.save(team);

        return TeamResponseDTO.builder()
                .teamId(updatedTeam.getId())
                .hackathonId(updatedTeam.getHackathonId().getId())
                .teamName(updatedTeam.getTeamName())
                .isLookingForMembers(updatedTeam.isLookingForMembers())
                .skillsNeeded(updatedTeam.getSkillsNeeded())
                .leaderId(leader.getId())
                .build();
    }

    public SubmissionResponseDTO createSubmission(SubmissionRequestDTO requestDTO, String authenticatedEmail) {
        // Find logged-in user
        Users user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Find team
        Teams team = teamRepository.findById(requestDTO.getTeamId())
                .orElseThrow(() -> new RuntimeException("Team not found"));

        // Verify user is in the team
        TeamMembers memberRecord = teamMemberRepository.findByTeamsIdAndUserId(team, user)
                .orElseThrow(() -> new RuntimeException("You are not a member of this team"));

        if (projectSubmissionRepository.existsByTeamsId(team)) {
            throw new RuntimeException("Team already has a submission");
        }

        Hackathons hackathon = team.getHackathonId();

        // Check the status of hackathon should be active
        if (hackathon.getHackathonStatus() != HackathonStatus.ACTIVE) {
            throw new RuntimeException("Submissions are not allowed at this time for this hackathon");
        }

        ProjectSubmissions submission = new ProjectSubmissions();
        submission.setTeamsId(team);
        submission.setHackathonId(hackathon);
        submission.setProjectTitle(requestDTO.getProjectTitle());
        submission.setTagLine(requestDTO.getTagLine());
        submission.setDescription(requestDTO.getDescription());
        submission.setGithubRepoUrl(requestDTO.getGithubRepoUrl());
        submission.setLiveDemoUrl(requestDTO.getLiveDemoUrl());
        submission.setYoutubeUrl(requestDTO.getYoutubeUrl());
        submission.setSubmissionStatus(ProjectSubmissionStatus.SUBMITTED);
        submission.setSubmittedAt(LocalDateTime.now());

        ProjectSubmissions savedSubmission = projectSubmissionRepository.save(submission);

        return SubmissionResponseDTO.builder()
                .projectSubmissionId(savedSubmission.getId())
                .teamId(team.getId())
                .hackathonId(hackathon.getId())
                .projectTitle(savedSubmission.getProjectTitle())
                .tagLine(savedSubmission.getTagLine())
                .description(savedSubmission.getDescription())
                .githubRepoUrl(savedSubmission.getGithubRepoUrl())
                .liveDemoUrl(savedSubmission.getLiveDemoUrl())
                .youtubeUrl(savedSubmission.getYoutubeUrl())
                .submissionStatus(savedSubmission.getSubmissionStatus().name())
                .build();
    }

    public HackathonWithTeamDetailsResponseDTO getHackathonWithTeamDetails(Long hackathonId, String email) {
        HackathonDetailResponseDTO hackathonDetails = getPublicHackathonDetail(hackathonId);

        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Find team for this user in this hackathon
        Optional<TeamMembers> teamMember = teamMemberRepository.findByHackathonIdAndUserId(hackathonId,
                user.getId());

        SubmissionResponseDTO projectSubmission = null;
        TeamWithParticipantsResponseDTO teamDetails = null;
        if (teamMember.isPresent()) {
            teamDetails = seeMyTeamDetails(teamMember.get().getTeamsId().getId(), email);
            Optional<ProjectSubmissions> submissionOpt = projectSubmissionRepository
                    .findByTeamsId(teamMember.get().getTeamsId());
            if (submissionOpt.isPresent()) {
                ProjectSubmissions submission = submissionOpt.get();
                projectSubmission = SubmissionResponseDTO.builder()
                        .projectSubmissionId(submission.getId())
                        .teamId(submission.getTeamsId().getId())
                        .hackathonId(hackathonId)
                        .projectTitle(submission.getProjectTitle())
                        .tagLine(submission.getTagLine())
                        .description(submission.getDescription())
                        .githubRepoUrl(submission.getGithubRepoUrl())
                        .liveDemoUrl(submission.getLiveDemoUrl())
                        .youtubeUrl(submission.getYoutubeUrl())
                        .submissionStatus(submission.getSubmissionStatus().name())
                        .build();
            }
        }

        return HackathonWithTeamDetailsResponseDTO.builder()
                .hackathonDetails(hackathonDetails)
                .teamDetails(teamDetails)
                .projectSubmission(projectSubmission)
                .build();
    }

    public List<HackathonWinnerResponseDTO> getHackathonWinners(Long hackathonId) {
        Hackathons hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new RuntimeException("Hackathon not found"));

        if (hackathon.getHackathonStatus() != HackathonStatus.PUBLISHED) {
            throw new RuntimeException("Results are not yet declared for this hackathon");
        }

        List<HackathonWinners> winners = hackathonWinnersRepository.findByHackathonId_Id(hackathonId);

        return winners.stream().map(winner -> HackathonWinnerResponseDTO.builder()
                .categoryName(winner.getCategoryName())
                .teamName(winner.getSubmissionId().getTeamsId().getTeamName())
                .projectName(winner.getSubmissionId().getProjectTitle())
                .projectDescription(winner.getSubmissionId().getDescription())
                .githubUrl(winner.getSubmissionId().getGithubRepoUrl())
                .liveDemoUrl(winner.getSubmissionId().getLiveDemoUrl())
                .build()).toList();
    }
}
