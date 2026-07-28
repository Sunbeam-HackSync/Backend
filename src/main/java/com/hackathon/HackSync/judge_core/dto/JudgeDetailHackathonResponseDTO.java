package com.hackathon.HackSync.judge_core.dto;

import java.time.LocalDateTime;

import com.hackathon.HackSync.participants_core.dto.HackathonDetailResponseDTO;
import com.hackathon.HackSync.host_core.entity.JudgeInvitationStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JudgeDetailHackathonResponseDTO {
    private HackathonDetailResponseDTO hackathons;
    private Long judgeUserId;
    private LocalDateTime assignedAt;
    private JudgeInvitationStatus status;
    private Boolean isSuperJudge;
}