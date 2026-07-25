package com.hackathon.HackSync.judge_core.service;

import com.hackathon.HackSync.auth.entity.Users;
import com.hackathon.HackSync.auth.repository.UserRepository;
import com.hackathon.HackSync.host_core.entity.HackathonJudges;
import com.hackathon.HackSync.host_core.entity.JudgeInvitationStatus;
import com.hackathon.HackSync.host_core.repository.HackathonJudgesRepository;
import com.hackathon.HackSync.utils.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class JudgeService {

    private final UserRepository userRepository;
    private final HackathonJudgesRepository hackathonJudgesRepository;

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

}
