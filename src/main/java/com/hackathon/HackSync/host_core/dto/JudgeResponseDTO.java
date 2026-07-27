package com.hackathon.HackSync.host_core.dto;

import com.hackathon.HackSync.host_core.entity.JudgeInvitationStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class JudgeResponseDTO {
    private Long id;
    private Long userId;
    private String email;
    private JudgeInvitationStatus status;
    private boolean isSuperJudge;
    private LocalDateTime assignedAt;
}
