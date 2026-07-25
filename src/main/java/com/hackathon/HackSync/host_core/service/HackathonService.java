package com.hackathon.HackSync.host_core.service;

import java.util.UUID;
import java.util.stream.Collectors;

import com.hackathon.HackSync.auth.entity.ROLE;
import com.hackathon.HackSync.auth.entity.Users;
import com.hackathon.HackSync.auth.repository.UserRepository;
import com.hackathon.HackSync.host_core.dto.HackathonRequestDTO;
import com.hackathon.HackSync.host_core.entity.HackathonStatus;
import com.hackathon.HackSync.host_core.entity.Hackathons;
import com.hackathon.HackSync.host_core.repository.HackathonRepository;
import com.hackathon.HackSync.host_core.responses.HackathonResponse;
import com.hackathon.HackSync.participants_core.dto.HackathonDetailResponseDTO;
import org.springframework.stereotype.Service;

import com.hackathon.HackSync.judge_core.dto.ProjectSubmissionResponseDTO;
import com.hackathon.HackSync.judge_core.entity.ProjectSubmissions;
import com.hackathon.HackSync.judge_core.repository.ProjectSubmissionRepository;
import com.hackathon.HackSync.judge_core.dto.EvaluationCriteriaRequestDTO;
import com.hackathon.HackSync.judge_core.dto.EvaluationCriteriaResponseDTO;
import com.hackathon.HackSync.judge_core.entity.EvaluationCriteria;
import com.hackathon.HackSync.judge_core.entity.ProjectSubmissionStatus;
import com.hackathon.HackSync.judge_core.repository.EvaluationCriteriaRepository;
import com.hackathon.HackSync.participants_core.dto.ParticipantResponseDTO;
import com.hackathon.HackSync.participants_core.dto.TeamWithParticipantsResponseDTO;
import com.hackathon.HackSync.participants_core.entity.TeamMembers;
import com.hackathon.HackSync.participants_core.entity.Teams;
import com.hackathon.HackSync.participants_core.repository.TeamMemberRepository;
import com.hackathon.HackSync.utils.exception.AccessDeniedException;
import com.hackathon.HackSync.utils.exception.ResourceNotFoundException;
import com.hackathon.HackSync.utils.service.ImageKitService;

import lombok.RequiredArgsConstructor;

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
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;
import io.imagekit.models.files.FileUploadResponse;

@Service
@RequiredArgsConstructor
public class HackathonService {

    private final HackathonRepository hackathonRepository;
    private final UserRepository userRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final ProjectSubmissionRepository projectSubmissionRepository;
    private final HackathonJudgesRepository hackathonJudgesRepository;
    private final HackathonsMentorsRepository hackathonsMentorsRepository;
    private final EvaluationCriteriaRepository evaluationCriteriaRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final ImageKitService imageKitService;

    public HackathonResponse createHackathon(HackathonRequestDTO hackathonRequestDTO, String authenticatedEmail) {
        Users host = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Host does not exists"));

        if (!host.getRole().equals(ROLE.HOST) && !host.getRole().equals(ROLE.ADMIN)) {
            throw new AccessDeniedException("Access Denied: Only HOSTs or ADMINs can create a hackathon");
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

    public HackathonDetailResponseDTO getHackathonById(Long hackId, String authenticatedEmail) {
        Users host = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User does not exists"));

        Hackathons hackathon = hackathonRepository.findById(hackId)
                .orElseThrow(() -> new ResourceNotFoundException("Hackathon does not exists"));

        if (!host.getRole().equals(ROLE.ADMIN)) {
            if (host.getRole().equals(ROLE.HOST)) {
                if (!hackathon.getHostId().getId().equals(host.getId())) {
                    throw new AccessDeniedException("Access Denied: You are not the creator of this hackathon");
                }
            } else {
                throw new AccessDeniedException(
                        "Access Denied: Only HOSTs or ADMINs can view this hackathon details here");
            }
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

    public HackathonResponse updateHackathon(Long hackId, HackathonRequestDTO hackathonRequestDTO,
            String authenticatedEmail) {
        Users user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User does not exists"));
        Hackathons hackathon = hackathonRepository.findById(hackId)
                .orElseThrow(() -> new ResourceNotFoundException("Hackathon does not exists"));
        if (!user.getRole().equals(ROLE.ADMIN)) {
            if (user.getRole().equals(ROLE.HOST)) {
                if (!hackathon.getHostId().getId().equals(user.getId())) {
                    throw new AccessDeniedException("Access Denied: You are not the creator of this hackathon");
                }
            } else {
                throw new AccessDeniedException(
                        "Access Denied: Participants, Judges, and Mentors cannot update hackathons");
            }
        }
        if (hackathon.getHackathonStatus() == HackathonStatus.COMPLETED) {
            throw new RuntimeException("Cannot update a COMPLETED hackathon"); // did i need to create a seperate
                                                                               // exception class for this
        }

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
                .orElseThrow(() -> new ResourceNotFoundException("User does not exists"));

        if (!user.getRole().equals(ROLE.HOST) && !user.getRole().equals(ROLE.ADMIN)) {
            throw new AccessDeniedException("Access Denied: Only HOSTs can view their hackathons");
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

    public List<TeamWithParticipantsResponseDTO> getHackathonParticipants(Long hackId, String authenticatedEmail) {
        Users user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User does not exists"));
        Hackathons hackathon = hackathonRepository.findById(hackId)
                .orElseThrow(() -> new ResourceNotFoundException("Hackathon does not exists"));

        if (!user.getRole().equals(ROLE.ADMIN)) {
            if (user.getRole().equals(ROLE.HOST)) {
                if (!hackathon.getHostId().getId().equals(user.getId())) {
                    throw new AccessDeniedException("Access Denied: You are not the creator of this hackathon");
                }
            } else {
                throw new AccessDeniedException("Access Denied: Only HOSTs or ADMINs can view all participants");
            }
        }

        List<TeamMembers> members = teamMemberRepository.findByHackathonId(hackId);

        Map<Teams, List<TeamMembers>> groupedByTeam = members.stream()
                .collect(Collectors.groupingBy(TeamMembers::getTeamsId));

        return groupedByTeam.entrySet().stream()
                .map(entry -> TeamWithParticipantsResponseDTO.builder()
                        .teamId(entry.getKey().getId())
                        .teamName(entry.getKey().getTeamName())
                        .participants(entry.getValue().stream().map(m -> ParticipantResponseDTO.builder()
                                .userId(m.getUserId().getId())
                                .email(m.getUserId().getEmail())
                                .teamId(m.getTeamsId().getId())
                                .teamName(m.getTeamsId().getTeamName())
                                .isTeamLeader(m.isTeamLeader())
                                .build()).toList())
                        .build())
                .toList();
    }

    public List<ProjectSubmissionResponseDTO> getHackathonSubmissions(Long hackId, String authenticatedEmail) {
        Users user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User does not exists"));
        Hackathons hackathon = hackathonRepository.findById(hackId)
                .orElseThrow(() -> new ResourceNotFoundException("Hackathon does not exists"));

        if (!user.getRole().equals(ROLE.ADMIN)) {
            if (user.getRole().equals(ROLE.HOST)) {
                if (!hackathon.getHostId().getId().equals(user.getId())) {
                    throw new AccessDeniedException("Access Denied: You are not the creator of this hackathon");
                }
            } else {
                throw new AccessDeniedException("Access Denied: Only HOSTs or ADMINs can view all submissions");
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

    private void sendInvitationEmail(String email, ROLE role, String hackathonTitle) {
        String subject = "Invitation to be a " + role.name() + " at " + hackathonTitle;
        String htmlTemplate = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1.0">
                  <title>You're Invited</title>
                </head>
                <body style="margin:0;padding:0;background-color:#F7F7F5;font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Helvetica,Arial,sans-serif;">

                  <table width="100%" cellpadding="0" cellspacing="0" role="presentation" style="background-color:#F7F7F5;min-height:100vh;">
                    <tr>
                      <td align="center" style="padding:48px 16px;">
                        <table width="100%" cellpadding="0" cellspacing="0" role="presentation" style="max-width:540px;">
                          <tr>
                            <td style="background-color:#D97706;height:3px;border-radius:2px 2px 0 0;"></td>
                          </tr>
                          <tr>
                            <td style="background-color:#FFFFFF;border-radius:0 0 6px 6px;padding:52px 52px 48px;">

                              <p style="margin:0 0 40px;font-size:11px;letter-spacing:0.12em;text-transform:uppercase;color:#A3A3A3;font-weight:500;">
                                Hackathon Platform
                              </p>

                              <h1 style="margin:0 0 12px;font-family:Georgia,'Times New Roman',serif;font-size:28px;font-weight:400;color:#0A0A0A;line-height:1.25;letter-spacing:-0.01em;">
                                You've been invited.
                              </h1>

                              <p style="margin:0 0 36px;font-size:15px;color:#6B6B6B;line-height:1.7;">
                                You've been selected as a <span style="color:#0A0A0A;font-weight:500;">{{role}}</span> for
                                <span style="color:#0A0A0A;font-weight:500;">{{hackathonTitle}}</span>.
                                We'd love to have you on board.
                              </p>

                              <table width="100%" cellpadding="0" cellspacing="0" role="presentation">
                                <tr>
                                  <td style="border-top:1px solid #F0F0EE;padding-bottom:36px;"></td>
                                </tr>
                              </table>
                              <table cellpadding="0" cellspacing="0" role="presentation">
                                <tr>
                                  <td style="border-radius:4px;background-color:#0A0A0A;">
                                    <a href="http://hackathon-platform.com/register?email={{email}}&role={{role}}"
                                       style="display:inline-block;padding:13px 28px;font-size:14px;font-weight:500;color:#FFFFFF;text-decoration:none;letter-spacing:0.02em;border-radius:4px;">
                                      Accept Invitation &rarr;
                                    </a>
                                  </td>
                                </tr>
                              </table>>
                              <p style="margin:28px 0 0;font-size:12px;color:#A3A3A3;line-height:1.6;">
                                Or copy this link into your browser:<br>
                                <span style="color:#6B6B6B;word-break:break-all;">
                                  http://hackathon-platform.com/register?email={{email}}&role={{role}}
                                </span>
                              </p>

                            </td>
                          </tr>
                          <tr>
                            <td style="padding:24px 52px 0;">
                              <p style="margin:0;font-size:11px;color:#BCBCBA;line-height:1.6;">
                                You received this because you were nominated by an organiser.
                                If this was a mistake, you can safely ignore this email.
                              </p>
                            </td>
                          </tr>

                        </table>
                      </td>
                    </tr>
                  </table>

                </body>
                </html>
                """;

        String htmlMessage = htmlTemplate
                .replace("{{hackathonTitle}}", hackathonTitle)
                .replace("{{email}}", email)
                .replace("{{role}}", role.name());
        try {
            emailService.sendVerificationEmail(email, subject, htmlMessage);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send invitation email to " + e + " " + email);
        }
    }

    public void inviteJudge(Long hackathonId, InviteRequestDTO inviteRequestDTO, String authenticatedEmail) {
        Users host = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User does not exists"));
        Hackathons hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new ResourceNotFoundException("Hackathon does not exists"));

        if (!host.getRole().equals(ROLE.ADMIN)) {
            if (host.getRole().equals(ROLE.HOST)) {
                if (!hackathon.getHostId().getId().equals(host.getId())) {
                    throw new AccessDeniedException("Access Denied: You are not the creator of this hackathon");
                }
            } else {
                throw new AccessDeniedException("Access Denied: Only HOSTs or ADMINs can invite judges");
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

        // I have to send Email ROLE_JUDGE
        sendInvitationEmail(inviteRequestDTO.getEmail(), ROLE.JUDGE, hackathon.getTitle());
    }

    public void inviteMentor(Long hackathonId, InviteRequestDTO inviteRequestDTO, String authenticatedEmail) {
        Users host = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User does not exists"));
        Hackathons hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new ResourceNotFoundException("Hackathon does not exists"));

        if (!host.getRole().equals(ROLE.ADMIN)) {
            if (host.getRole().equals(ROLE.HOST)) {
                if (!hackathon.getHostId().getId().equals(host.getId())) {
                    throw new AccessDeniedException("Access Denied: You are not the creator of this hackathon");
                }
            } else {
                throw new AccessDeniedException("Access Denied: Only HOSTs or ADMINs can invite mentors");
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

        // I have to send Email to ROLE_MENTOR
        sendInvitationEmail(inviteRequestDTO.getEmail(), ROLE.MENTOR, hackathon.getTitle());
    }

    public void assignSuperJudge(Long hackathonId, Long judgeUserId, String authenticatedEmail) {
        Users host = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User does not exists"));
        Hackathons hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new ResourceNotFoundException("Hackathon does not exists"));

        if (!host.getRole().equals(ROLE.ADMIN)) {
            if (host.getRole().equals(ROLE.HOST)) {
                if (!hackathon.getHostId().getId().equals(host.getId())) {
                    throw new AccessDeniedException("Access Denied: You are not the creator of this hackathon");
                }
            } else {
                throw new AccessDeniedException("Access Denied: Only HOSTs or ADMINs can assign super judges");
            }
        }

        HackathonJudges hackathonJudge = hackathonJudgesRepository.findByHackathonsId_IdAndJudgeUserId_Id(hackathonId, judgeUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Judge is not assigned to this hackathon"));
        
        hackathonJudge.setSuperJudge(true);
        hackathonJudgesRepository.save(hackathonJudge);
    }

    // Note : This Endpoint should return the top 3 teams after publishing the
    // hackathon results. the status will be changing form COMPLETED to
    // PUBLISHED once the results are published by the judges after evaluating all
    // the teams submissions.
    public void publishHackathonResults(Long hackathonId, String authenticatedEmail) {
        Users host = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User does not exists"));
        Hackathons hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new ResourceNotFoundException("Hackathon does not exists"));

        if (!host.getRole().equals(ROLE.ADMIN)) {
            if (host.getRole().equals(ROLE.HOST)) {
                if (!hackathon.getHostId().getId().equals(host.getId())) {
                    throw new AccessDeniedException("Access Denied: You are not the creator of this hackathon");
                }
            } else {
                throw new AccessDeniedException("Access Denied: Only HOSTs or ADMINs can publish results");
            }
        }

        // check hackathon approved status then publish it
        if (hackathon.getHackathonStatus() != HackathonStatus.COMPLETED) {
            throw new RuntimeException("Hackathon results are not yet completed"); // HackathonException
        }

        hackathon.setHackathonStatus(HackathonStatus.PUBLISHED);
        hackathonRepository.save(hackathon);
    }

    public FileUploadResponse uploadImage(MultipartFile file) {
        try {
            return imageKitService.uploadImage(file);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload image", e);
        }
    }

    public EvaluationCriteriaResponseDTO createEvaluationCriteria(Long hackathonId, EvaluationCriteriaRequestDTO dto,
            String authenticatedEmail) {
        Users host = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User does not exists"));
        Hackathons hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new ResourceNotFoundException("Hackathon does not exists"));

        if (!host.getRole().equals(ROLE.ADMIN)) {
            if (host.getRole().equals(ROLE.HOST)) {
                if (!hackathon.getHostId().getId().equals(host.getId())) {
                    throw new AccessDeniedException("Access Denied: You are not the creator of this hackathon");
                }
            } else {
                throw new AccessDeniedException("Access Denied: Only HOSTs or ADMINs can create evaluation criteria");
            }
        }

        EvaluationCriteria criteria = new EvaluationCriteria();
        criteria.setHackathonId(hackathon);
        criteria.setCriteriaName(dto.getCriteriaName());
        criteria.setDescription(dto.getDescription());
        criteria.setMaxScore(dto.getMaxScore());

        EvaluationCriteria savedCriteria = evaluationCriteriaRepository.save(criteria);

        return EvaluationCriteriaResponseDTO.builder()
                .id(savedCriteria.getId())
                .hackathonId(hackathon.getId())
                .criteriaName(savedCriteria.getCriteriaName())
                .description(savedCriteria.getDescription())
                .maxScore(savedCriteria.getMaxScore())
                .build();
    }

    public EvaluationCriteriaResponseDTO updateEvaluationCriteria(Long hackathonId, Long criteriaId,
            EvaluationCriteriaRequestDTO dto, String authenticatedEmail) {
        Users host = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User does not exists"));
        Hackathons hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new ResourceNotFoundException("Hackathon does not exists"));

        if (!host.getRole().equals(ROLE.ADMIN)) {
            if (host.getRole().equals(ROLE.HOST)) {
                if (!hackathon.getHostId().getId().equals(host.getId())) {
                    throw new AccessDeniedException("Access Denied: You are not the creator of this hackathon");
                }
            } else {
                throw new AccessDeniedException("Access Denied: Only HOSTs or ADMINs can update evaluation criteria");
            }
        }

        EvaluationCriteria criteria = evaluationCriteriaRepository.findById(criteriaId)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluation criteria does not exists"));

        if (!criteria.getHackathonId().getId().equals(hackathonId)) {
            throw new RuntimeException("Evaluation criteria does not belong to this hackathon");
        }

        if (dto.getCriteriaName() != null)
            criteria.setCriteriaName(dto.getCriteriaName());
        if (dto.getDescription() != null)
            criteria.setDescription(dto.getDescription());
        if (dto.getMaxScore() != null)
            criteria.setMaxScore(dto.getMaxScore());

        EvaluationCriteria savedCriteria = evaluationCriteriaRepository.save(criteria);

        return EvaluationCriteriaResponseDTO.builder()
                .id(savedCriteria.getId())
                .hackathonId(hackathon.getId())
                .criteriaName(savedCriteria.getCriteriaName())
                .description(savedCriteria.getDescription())
                .maxScore(savedCriteria.getMaxScore())
                .build();
    }

    public ProjectSubmissionResponseDTO disqualifySubmission(Long hackathonId, Long submissionId,
            String authenticatedEmail) {
        Users host = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User does not exists"));
        Hackathons hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new ResourceNotFoundException("Hackathon does not exists"));

        if (!host.getRole().equals(ROLE.ADMIN)) {
            if (host.getRole().equals(ROLE.HOST)) {
                if (!hackathon.getHostId().getId().equals(host.getId())) {
                    throw new AccessDeniedException("Access Denied: You are not the creator of this hackathon");
                }
            } else {
                throw new AccessDeniedException("Access Denied: Only HOSTs or ADMINs can disqualify submissions");
            }
        }

        ProjectSubmissions submission = projectSubmissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Project submission does not exists"));

        if (!submission.getHackathonId().getId().equals(hackathonId)) {
            throw new RuntimeException("Project submission does not belong to this hackathon");
        }

        submission.setSubmissionStatus(ProjectSubmissionStatus.DISQUALIFIED);
        projectSubmissionRepository.save(submission);

        return ProjectSubmissionResponseDTO.builder()
                .id(submission.getId())
                .projectTitle(submission.getProjectTitle())
                .tagLine(submission.getTagLine())
                .description(submission.getDescription())
                .liveDemoUrl(submission.getLiveDemoUrl())
                .githubRepoUrl(submission.getGithubRepoUrl())
                .submissionStatus(submission.getSubmissionStatus())
                .teamId(submission.getTeamsId().getId())
                .teamName(submission.getTeamsId().getTeamName())
                .submittedAt(submission.getSubmittedAt())
                .build();
    }
}
