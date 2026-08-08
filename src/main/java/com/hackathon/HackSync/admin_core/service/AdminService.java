package com.hackathon.HackSync.admin_core.service;

import com.hackathon.HackSync.host_core.entity.Hackathons;
import com.hackathon.HackSync.host_core.entity.HackathonStatus;
import com.hackathon.HackSync.host_core.repository.HackathonRepository;
import com.hackathon.HackSync.auth.repository.UserRepository;
import com.hackathon.HackSync.auth.entity.Users;
import com.hackathon.HackSync.judge_core.repository.ProjectSubmissionRepository;
import com.hackathon.HackSync.utils.exception.ResourceNotFoundException;
import com.hackathon.HackSync.participants_core.dto.HackathonDetailResponseDTO;
import com.hackathon.HackSync.admin_core.entity.HackathonReviews;
import com.hackathon.HackSync.admin_core.entity.HackathonReviewStatus;

import lombok.RequiredArgsConstructor;

import com.hackathon.HackSync.admin_core.dto.PlatformMetricsDto;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

        private final HackathonRepository hackathonRepository;
        private final UserRepository userRepository;
        private final ProjectSubmissionRepository projectSubmissionRepository;
        private final com.hackathon.HackSync.admin_core.repository.HackathonReviewsRepository hackathonReviewsRepository;

        public List<HackathonDetailResponseDTO> getPendingHackathons() {
                return hackathonRepository.findByHackathonStatus(HackathonStatus.DRAFT)
                                .stream()
                                .map(this::mapToDTO)
                                .collect(Collectors.toList());
        }

        public HackathonDetailResponseDTO approveHackathon(@NonNull Long id, String adminEmail, String feedbackNotes) {
                Hackathons hackathon = hackathonRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Hackathon not found with id: " + id));
                hackathon.setHackathonStatus(HackathonStatus.APPROVED);
                Hackathons savedHackathon = hackathonRepository.save(hackathon);

                Users admin = userRepository.findByEmail(adminEmail)
                                .orElseThrow(() -> new ResourceNotFoundException("Admin user not found"));

                HackathonReviews review = new HackathonReviews();
                review.setHackathonId(savedHackathon);
                review.setAdminId(admin);
                review.setReviewStatus(HackathonReviewStatus.APPROVED);
                review.setFeedbackNotes(feedbackNotes);
                review.setReviewedAt(LocalDateTime.now());
                hackathonReviewsRepository.save(review);

                return mapToDTO(savedHackathon);
        }

        public HackathonDetailResponseDTO rejectHackathon(@NonNull Long id, String adminEmail, String feedbackNotes) {
                Hackathons hackathon = hackathonRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Hackathon not found with id: " + id));
                hackathon.setHackathonStatus(HackathonStatus.REJECTED);
                Hackathons savedHackathon = hackathonRepository.save(hackathon);

                Users admin = userRepository.findByEmail(adminEmail)
                                .orElseThrow(() -> new ResourceNotFoundException("Admin user not found"));

                HackathonReviews review = new HackathonReviews();
                review.setHackathonId(savedHackathon);
                review.setAdminId(admin);
                review.setReviewStatus(HackathonReviewStatus.REJECTED);
                review.setFeedbackNotes(feedbackNotes);
                review.setReviewedAt(LocalDateTime.now());
                hackathonReviewsRepository.save(review);

                return mapToDTO(savedHackathon);
        }

        public PlatformMetricsDto getPlatformMetrics() {
                long totalActiveHackathons = hackathonRepository.countByHackathonStatus(HackathonStatus.ACTIVE);
                long totalRegisteredUsers = userRepository.count();
                long totalSubmissions = projectSubmissionRepository.count();
                return PlatformMetricsDto.builder()
                                .totalActiveHackathons(totalActiveHackathons)
                                .totalRegisteredUsers(totalRegisteredUsers)
                                .totalSubmissions(totalSubmissions)
                                .build();
        }

        public Users banUser(@NonNull String email) {
                Users user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "User not found with email: " + email));
                user.setBanned(true);
                return userRepository.save(user);
        }

        public Users unbanUser(@NonNull String email) {
                Users user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "User not found with email: " + email));
                user.setBanned(false);
                return userRepository.save(user);
        }

        private HackathonDetailResponseDTO mapToDTO(Hackathons hackathon) {
                String feedbackNotes = null;
                if (hackathon.getHackathonStatus() == HackathonStatus.REJECTED) {
                        feedbackNotes = hackathonReviewsRepository.findByHackathonId(hackathon)
                                        .map(HackathonReviews::getFeedbackNotes)
                                        .orElse(null);
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
                                .feedBackNotes(feedbackNotes)
                                .faq(hackathon.getFaq())
                                .rules(hackathon.getRules())
                                .resultDeclarationDate(hackathon.getResultDeclarationDate())
                                .build();
        }
}
