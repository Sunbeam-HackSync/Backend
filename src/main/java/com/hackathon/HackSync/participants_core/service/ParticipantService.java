package com.hackathon.HackSync.participants_core.service;

import com.hackathon.HackSync.auth.entity.Users;
import com.hackathon.HackSync.auth.repository.UserRepository;
import com.hackathon.HackSync.host_core.entity.HackathonStatus;
import com.hackathon.HackSync.host_core.entity.Hackathons;
import com.hackathon.HackSync.host_core.repository.HackathonRepository;
import com.hackathon.HackSync.participants_core.dto.HackathonDetailResponseDTO;
import com.hackathon.HackSync.participants_core.dto.TeamRequestDTO;
import com.hackathon.HackSync.participants_core.dto.TeamResponseDTO;
import com.hackathon.HackSync.participants_core.entity.TeamMembers;
import com.hackathon.HackSync.participants_core.entity.Teams;
import com.hackathon.HackSync.judge_core.entity.ProjectSubmissions;
import com.hackathon.HackSync.judge_core.entity.ProjectSubmissionStatus;
import com.hackathon.HackSync.judge_core.repository.ProjectSubmissionRepository;
import com.hackathon.HackSync.host_core.entity.HackathonTracks;
import com.hackathon.HackSync.host_core.repository.HackathonTrackRepository;
import com.hackathon.HackSync.participants_core.dto.TeamUpdateRequestDTO;
import com.hackathon.HackSync.participants_core.dto.SubmissionRequestDTO;
import com.hackathon.HackSync.participants_core.dto.SubmissionResponseDTO;
import com.hackathon.HackSync.participants_core.dto.ParticipantResponseDTO;
import com.hackathon.HackSync.participants_core.dto.TeamWithParticipantsResponseDTO;
import com.hackathon.HackSync.participants_core.repository.TeamMemberRepository;
import com.hackathon.HackSync.participants_core.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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
        private final HackathonTrackRepository hackathonTrackRepository;

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
                                .map(m -> ParticipantResponseDTO.builder()
                                                .userId(m.getUserId().getId())
                                                .email(m.getUserId().getEmail())
                                                .teamId(m.getTeamsId().getId())
                                                .teamName(m.getTeamsId().getTeamName())
                                                .isTeamLeader(m.isTeamLeader())
                                                .build())
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

                // Validate hackathon status
                if (hackathon.getHackathonStatus() != HackathonStatus.ACTIVE
                        ) {

                        throw new RuntimeException("Teams can only be created for ACTIVE or PUBLISHED hackathons");
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
                HackathonTracks track = null;
                if (requestDTO.getTrackId() != null) {
                        track = hackathonTrackRepository.findById(requestDTO.getTrackId())
                                        .orElseThrow(() -> new RuntimeException("Track not found"));
                }

                ProjectSubmissions submission = new ProjectSubmissions();
                submission.setTeamsId(team);
                submission.setHackathonId(hackathon);
                submission.setTrackId(track);
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
                                .trackId(track != null ? track.getId() : null)
                                .projectTitle(savedSubmission.getProjectTitle())
                                .tagLine(savedSubmission.getTagLine())
                                .description(savedSubmission.getDescription())
                                .githubRepoUrl(savedSubmission.getGithubRepoUrl())
                                .liveDemoUrl(savedSubmission.getLiveDemoUrl())
                                .youtubeUrl(savedSubmission.getYoutubeUrl())
                                .submissionStatus(savedSubmission.getSubmissionStatus().name())
                                .build();
        }
}
