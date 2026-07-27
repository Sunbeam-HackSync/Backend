package com.hackathon.HackSync.judge_core.dto;

import com.hackathon.HackSync.host_core.entity.HackathonStatus;
import com.hackathon.HackSync.host_core.entity.JudgeInvitationStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AssignedHackathonResponseDTO {
    private Long hackathonId;
    private String title;
    private String tagline;
    private HackathonStatus hackathonStatus;
    private LocalDateTime hackathonStarts;
    private LocalDateTime hackathonEnds;
    private JudgeInvitationStatus invitationStatus;
    private boolean isSuperJudge;
}
