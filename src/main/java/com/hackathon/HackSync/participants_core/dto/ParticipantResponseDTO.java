package com.hackathon.HackSync.participants_core.dto;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class ParticipantResponseDTO {
    private Long userId;
    private String email;
    private Long teamId;
    private String teamName;
    private boolean isTeamLeader;
}
