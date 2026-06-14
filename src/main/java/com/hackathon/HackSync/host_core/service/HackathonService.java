package com.hackathon.HackSync.host_core.service;

import com.hackathon.HackSync.auth.entity.ROLE;
import com.hackathon.HackSync.auth.entity.Users;
import com.hackathon.HackSync.auth.repository.UserRepository;
import com.hackathon.HackSync.host_core.dto.HackathonRequestDTO;
import com.hackathon.HackSync.host_core.entity.HackathonStatus;
import com.hackathon.HackSync.host_core.entity.Hackathons;
import com.hackathon.HackSync.host_core.repository.HackathonRepository;
import com.hackathon.HackSync.host_core.responses.HackathonResponse;
import org.springframework.stereotype.Service;

import com.hackathon.HackSync.judge_core.dto.ProjectSubmissionResponseDTO;
import com.hackathon.HackSync.judge_core.entity.ProjectSubmissions;
import com.hackathon.HackSync.judge_core.repository.ProjectSubmissionRepository;
import com.hackathon.HackSync.participants_core.dto.ParticipantResponseDTO;
import com.hackathon.HackSync.participants_core.entity.TeamMembers;
import com.hackathon.HackSync.participants_core.repository.TeamMemberRepository;
import com.hackathon.HackSync.host_core.repository.HackathonJudgesRepository;
import com.hackathon.HackSync.mentor_core.repository.HackathonsMentorsRepository;
import com.hackathon.HackSync.auth.service.EmailService;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.hackathon.HackSync.host_core.dto.InviteRequestDTO;
import com.hackathon.HackSync.host_core.entity.HackathonJudges;
import com.hackathon.HackSync.host_core.entity.JudgeInvitationStatus;
import com.hackathon.HackSync.mentor_core.entity.HackathonsMentors;
import com.hackathon.HackSync.mentor_core.entity.MentorStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class HackathonService {

    private final HackathonRepository hackathonRepository;
    private final UserRepository userRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final ProjectSubmissionRepository projectSubmissionRepository;
    private final HackathonJudgesRepository hackathonJudgesRepository;
    private final HackathonsMentorsRepository hackathonsMentorsRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public HackathonService(HackathonRepository hackathonRepository, UserRepository userRepository,
            TeamMemberRepository teamMemberRepository, ProjectSubmissionRepository projectSubmissionRepository,
            HackathonJudgesRepository hackathonJudgesRepository, HackathonsMentorsRepository hackathonsMentorsRepository,
            EmailService emailService, PasswordEncoder passwordEncoder) {
        this.hackathonRepository = hackathonRepository;
        this.userRepository = userRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.projectSubmissionRepository = projectSubmissionRepository;
        this.hackathonJudgesRepository = hackathonJudgesRepository;
        this.hackathonsMentorsRepository = hackathonsMentorsRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    public HackathonResponse createHackathon(HackathonRequestDTO hackathonRequestDTO, String authenticatedEmail) {
        Users host = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new RuntimeException("Host does not exists"));

        if (!host.getRole().equals(ROLE.HOST) && !host.getRole().equals(ROLE.ADMIN)) {
            throw new RuntimeException("Access Denied: Only HOSTs or ADMINs can create a hackathon");
        }

        Hackathons hackathon = new Hackathons();
        hackathon.setHostId(host);
        hackathon.setTitle(hackathonRequestDTO.getTitle());
        hackathon.setTagline(hackathonRequestDTO.getTagline());
        hackathon.setDescription(hackathonRequestDTO.getDescription());

        hackathon.setBannerImageUrl(hackathonRequestDTO.getBannerImageUrl());
        hackathon.setProfileImageUrl(hackathonRequestDTO.getProfileImageUrl());

        hackathon.setMinTeamSize(hackathonRequestDTO.getMinTeamSize());
        hackathon.setMaxTeamSize(hackathonRequestDTO.getMaxTeamSize());

        hackathon.setRegistrationStart(hackathonRequestDTO.getRegistrationStart());
        hackathon.setRegistrationEnd(hackathonRequestDTO.getRegistrationEnd());
        hackathon.setHackathonStart(hackathonRequestDTO.getHackathonStart());
        hackathon.setHackathonEnd(hackathonRequestDTO.getHackathonEnd());

        hackathon.setHackathonStatus(HackathonStatus.DRAFT);

        Hackathons savedHackathon = hackathonRepository.save(hackathon);

        return HackathonResponse.builder()
                .id(savedHackathon.getId())
                .title(savedHackathon.getTitle())
                .tagline(savedHackathon.getTagline())
                .hackathonStatus(savedHackathon.getHackathonStatus())
                .hackathonStarts(savedHackathon.getHackathonStart())
                .hackathonEnds(savedHackathon.getHackathonEnd())
                .build();
    }

    public Hackathons getHackathonById(UUID hackId, String authenticatedEmail) {
        Users host = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new RuntimeException("User does not exists"));

        Hackathons hackathon = hackathonRepository.findById(hackId)
                .orElseThrow(() -> new RuntimeException("Hackathon does not exists"));

        if (!host.getRole().equals(ROLE.ADMIN)) {
            if (host.getRole().equals(ROLE.HOST)) {
                if (!hackathon.getHostId().getId().equals(host.getId())) {
                    throw new RuntimeException("Access Denied: You are not the creator of this hackathon");
                }
            } else {
                throw new RuntimeException("Access Denied: Only HOSTs or ADMINs can view this hackathon details here");
            }
        }

        return hackathon;
    }

    public HackathonResponse updateHackathon(UUID hackId, HackathonRequestDTO hackathonRequestDTO,
            String authenticatedEmail) {
        Users user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new RuntimeException("User does not exists"));
        Hackathons hackathon = hackathonRepository.findById(hackId)
                .orElseThrow(() -> new RuntimeException("Hackathon does not exists"));

        // Role Edge Cases & Authorization
        if (!user.getRole().equals(ROLE.ADMIN)) {
            if (user.getRole().equals(ROLE.HOST)) {
                if (!hackathon.getHostId().getId().equals(user.getId())) {
                    throw new RuntimeException("Access Denied: You are not the creator of this hackathon");
                }
            } else {
                throw new RuntimeException("Access Denied: Participants, Judges, and Mentors cannot update hackathons");
            }
        }

        // Status check
        if (hackathon.getHackathonStatus() == HackathonStatus.COMPLETED) {
            throw new RuntimeException("Cannot update a COMPLETED hackathon");
        }

        // Update fields (partial update)
        if (hackathonRequestDTO.getTitle() != null)
            hackathon.setTitle(hackathonRequestDTO.getTitle());
        if (hackathonRequestDTO.getTagline() != null)
            hackathon.setTagline(hackathonRequestDTO.getTagline());
        if (hackathonRequestDTO.getDescription() != null)
            hackathon.setDescription(hackathonRequestDTO.getDescription());
        if (hackathonRequestDTO.getBannerImageUrl() != null)
            hackathon.setBannerImageUrl(hackathonRequestDTO.getBannerImageUrl());

        if (hackathonRequestDTO.getMinTeamSize() > 0)
            hackathon.setMinTeamSize(hackathonRequestDTO.getMinTeamSize());
        if (hackathonRequestDTO.getMaxTeamSize() > 0)
            hackathon.setMaxTeamSize(hackathonRequestDTO.getMaxTeamSize());

        if (hackathonRequestDTO.getRegistrationStart() != null)
            hackathon.setRegistrationStart(hackathonRequestDTO.getRegistrationStart());
        if (hackathonRequestDTO.getRegistrationEnd() != null)
            hackathon.setRegistrationEnd(hackathonRequestDTO.getRegistrationEnd());
        if (hackathonRequestDTO.getHackathonStart() != null)
            hackathon.setHackathonStart(hackathonRequestDTO.getHackathonStart());
        if (hackathonRequestDTO.getHackathonEnd() != null)
            hackathon.setHackathonEnd(hackathonRequestDTO.getHackathonEnd());

        Hackathons savedHackathon = hackathonRepository.save(hackathon);

        return HackathonResponse.builder()
                .id(savedHackathon.getId())
                .title(savedHackathon.getTitle())
                .tagline(savedHackathon.getTagline())
                .hackathonStatus(savedHackathon.getHackathonStatus())
                .hackathonStarts(savedHackathon.getHackathonStart())
                .hackathonEnds(savedHackathon.getHackathonEnd())
                .build();
    }

    public List<HackathonResponse> getMyHackathons(String authenticatedEmail) {
        Users user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new RuntimeException("User does not exists"));

        if (!user.getRole().equals(ROLE.HOST) && !user.getRole().equals(ROLE.ADMIN)) {
            throw new RuntimeException("Access Denied: Only HOSTs can view their hackathons");
        }

        List<Hackathons> hackathons = hackathonRepository.findByHostId(user);
        return hackathons.stream().map(h -> HackathonResponse.builder()
                .id(h.getId())
                .title(h.getTitle())
                .tagline(h.getTagline())
                .hackathonStatus(h.getHackathonStatus())
                .hackathonStarts(h.getHackathonStart())
                .hackathonEnds(h.getHackathonEnd())
                .build()).toList();
    }

    public List<ParticipantResponseDTO> getHackathonParticipants(UUID hackId, String authenticatedEmail) {
        Users user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new RuntimeException("User does not exists"));
        Hackathons hackathon = hackathonRepository.findById(hackId)
                .orElseThrow(() -> new RuntimeException("Hackathon does not exists"));

        if (!user.getRole().equals(ROLE.ADMIN)) {
            if (user.getRole().equals(ROLE.HOST)) {
                if (!hackathon.getHostId().getId().equals(user.getId())) {
                    throw new RuntimeException("Access Denied: You are not the creator of this hackathon");
                }
            } else {
                throw new RuntimeException("Access Denied: Only HOSTs or ADMINs can view all participants");
            }
        }

        List<TeamMembers> members = teamMemberRepository.findByHackathonId(hackId);
        return members.stream().map(m -> ParticipantResponseDTO.builder()
                .userId(m.getUserId().getId())
                .email(m.getUserId().getEmail())
                .teamId(m.getTeamsId().getId())
                .teamName(m.getTeamsId().getTeamName())
                .isTeamLeader(m.isTeamLeader())
                .build()).toList();
    }

    public List<ProjectSubmissionResponseDTO> getHackathonSubmissions(UUID hackId, String authenticatedEmail) {
        Users user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new RuntimeException("User does not exists"));
        Hackathons hackathon = hackathonRepository.findById(hackId)
                .orElseThrow(() -> new RuntimeException("Hackathon does not exists"));

        if (!user.getRole().equals(ROLE.ADMIN)) {
            if (user.getRole().equals(ROLE.HOST)) {
                if (!hackathon.getHostId().getId().equals(user.getId())) {
                    throw new RuntimeException("Access Denied: You are not the creator of this hackathon");
                }
            } else {
                throw new RuntimeException("Access Denied: Only HOSTs or ADMINs can view all submissions");
            }
        }

        List<ProjectSubmissions> submissions = projectSubmissionRepository.findByHackathonId(hackId);
        return submissions.stream().map(s -> ProjectSubmissionResponseDTO.builder()
                .id(s.getId())
                .projectTitle(s.getProjectTitle())
                .tagLine(s.getTagLine())
                .description(s.getDescription())
                .githubRepoUrl(s.getGithubRepoUrl())
                .liveDemoUrl(s.getLiveDemoUrl())
                .submissionStatus(s.getSubmissionStatus())
                .teamId(s.getTeamsId().getId())
                .teamName(s.getTeamsId().getTeamName())
                .submittedAt(s.getSubmittedAt())
                .build()).toList();
    }

    private void sendInvitationEmail(String email, String role, String hackathonTitle) {
        String subject = "Invitation to be a " + role + " at " + hackathonTitle;
        String htmlMessage = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\">"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"><title>Invitation</title>"
                + "</head><body style=\"margin:0;padding:0;background:#fafafa;font-family:sans-serif;\">"
                + "<div style=\"max-width:600px;margin:40px auto;background:#fff;padding:40px;border-radius:12px;box-shadow:0 4px 6px rgba(0,0,0,0.05);text-align:center;\">"
                + "<h1 style=\"color:#09090b;font-size:24px;margin-bottom:16px;\">You're Invited!</h1>"
                + "<p style=\"color:#52525b;font-size:16px;line-height:1.6;margin-bottom:24px;\">"
                + "You have been invited to be a " + role + " for the hackathon <strong>" + hackathonTitle + "</strong>.</p>"
                + "<a href=\"http://hackathon-platform.com/register?email=" + email + "&role=" + role + "\" "
                + "style=\"display:inline-block;padding:12px 24px;background-color:#18181b;color:#fff;text-decoration:none;border-radius:6px;font-weight:600;font-size:16px;\">"
                + "Accept Invitation</a>"
                + "</div></body></html>";
        try {
            emailService.sendVerificationEmail(email, subject, htmlMessage);
        } catch (Exception e) {
            System.err.println("Failed to send invitation email to " + email);
        }
    }

    public void inviteJudge(UUID hackathonId, InviteRequestDTO inviteRequestDTO, String authenticatedEmail) {
        Users host = userRepository.findByEmail(authenticatedEmail).orElseThrow(() -> new RuntimeException("User does not exists"));
        Hackathons hackathon = hackathonRepository.findById(hackathonId).orElseThrow(() -> new RuntimeException("Hackathon does not exists"));

        if (!host.getRole().equals(ROLE.ADMIN)) {
            if (host.getRole().equals(ROLE.HOST)) {
                if (!hackathon.getHostId().getId().equals(host.getId())) {
                    throw new RuntimeException("Access Denied: You are not the creator of this hackathon");
                }
            } else {
                throw new RuntimeException("Access Denied: Only HOSTs or ADMINs can invite judges");
            }
        }

        Users judgeUser = userRepository.findByEmail(inviteRequestDTO.getEmail()).orElse(null);
        if (judgeUser == null) {
            judgeUser = new Users();
            judgeUser.setEmail(inviteRequestDTO.getEmail());
            judgeUser.setPassword_hash(passwordEncoder.encode(UUID.randomUUID().toString()));
            judgeUser.setRole(ROLE.JUDGE);
            judgeUser.setEmailVerified(false);
            judgeUser = userRepository.save(judgeUser);
        }

        HackathonJudges hackathonJudge = new HackathonJudges();
        hackathonJudge.setHackathonsId(hackathon);
        hackathonJudge.setJudgeUserId(judgeUser);
        hackathonJudge.setStatus(JudgeInvitationStatus.INVITED);
        hackathonJudge.setAssignedAt(LocalDateTime.now());
        hackathonJudgesRepository.save(hackathonJudge);

        sendInvitationEmail(inviteRequestDTO.getEmail(), "Judge", hackathon.getTitle());
    }

    public void inviteMentor(UUID hackathonId, InviteRequestDTO inviteRequestDTO, String authenticatedEmail) {
        Users host = userRepository.findByEmail(authenticatedEmail).orElseThrow(() -> new RuntimeException("User does not exists"));
        Hackathons hackathon = hackathonRepository.findById(hackathonId).orElseThrow(() -> new RuntimeException("Hackathon does not exists"));

        if (!host.getRole().equals(ROLE.ADMIN)) {
            if (host.getRole().equals(ROLE.HOST)) {
                if (!hackathon.getHostId().getId().equals(host.getId())) {
                    throw new RuntimeException("Access Denied: You are not the creator of this hackathon");
                }
            } else {
                throw new RuntimeException("Access Denied: Only HOSTs or ADMINs can invite mentors");
            }
        }

        Users mentorUser = userRepository.findByEmail(inviteRequestDTO.getEmail()).orElse(null);
        if (mentorUser == null) {
            mentorUser = new Users();
            mentorUser.setEmail(inviteRequestDTO.getEmail());
            mentorUser.setPassword_hash(passwordEncoder.encode(UUID.randomUUID().toString()));
            mentorUser.setRole(ROLE.MENTOR);
            mentorUser.setEmailVerified(false);
            mentorUser = userRepository.save(mentorUser);
        }

        HackathonsMentors hackathonMentor = new HackathonsMentors();
        hackathonMentor.setHackathonId(hackathon);
        hackathonMentor.setMentorsId(mentorUser);
        hackathonMentor.setStatus(MentorStatus.INVITED);
        hackathonsMentorsRepository.save(hackathonMentor);

        sendInvitationEmail(inviteRequestDTO.getEmail(), "Mentor", hackathon.getTitle());
    }

    public void publishHackathonResults(UUID hackathonId, String authenticatedEmail) {
        Users host = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new RuntimeException("User does not exists"));
        Hackathons hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new RuntimeException("Hackathon does not exists"));

        if (!host.getRole().equals(ROLE.ADMIN)) {
            if (host.getRole().equals(ROLE.HOST)) {
                if (!hackathon.getHostId().getId().equals(host.getId())) {
                    throw new RuntimeException("Access Denied: You are not the creator of this hackathon");
                }
            } else {
                throw new RuntimeException("Access Denied: Only HOSTs or ADMINs can publish results");
            }
        }

        if (hackathon.getHackathonStatus() == HackathonStatus.COMPLETED) {
            throw new RuntimeException("Hackathon results are already published");
        }

        hackathon.setHackathonStatus(HackathonStatus.COMPLETED);
        hackathonRepository.save(hackathon);
    }
}
