package com.hackathon.HackSync.admin_core.service;

import com.hackathon.HackSync.host_core.entity.Hackathons;
import com.hackathon.HackSync.host_core.entity.HackathonStatus;
import com.hackathon.HackSync.host_core.repository.HackathonRepository;
import com.hackathon.HackSync.auth.repository.UserRepository;
import com.hackathon.HackSync.auth.entity.Users;
import com.hackathon.HackSync.judge_core.repository.ProjectSubmissionRepository;
import com.hackathon.HackSync.utils.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

import com.hackathon.HackSync.admin_core.dto.PlatformMetricsDto;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final HackathonRepository hackathonRepository;
    private final UserRepository userRepository;
    private final ProjectSubmissionRepository projectSubmissionRepository;

    public List<Hackathons> getPendingHackathons() {
        return hackathonRepository.findByHackathonStatus(HackathonStatus.PENDING_APPROVAL);
    }

    public Hackathons approveHackathon(@NonNull Long id) {
        Hackathons hackathon = hackathonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hackathon not found with id: " + id));
        hackathon.setHackathonStatus(HackathonStatus.APPROVED);
        return hackathonRepository.save(hackathon);
    }

    public Hackathons rejectHackathon(@NonNull Long id) {
        Hackathons hackathon = hackathonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hackathon not found with id: " + id));
        hackathon.setHackathonStatus(HackathonStatus.REJECTED);
        return hackathonRepository.save(hackathon);
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

    public Users banUser(@NonNull Long id) {
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setBanned(true);
        return userRepository.save(user);
    }
}
