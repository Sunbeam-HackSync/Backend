package com.hackathon.HackSync.participants_core.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ParticipantResponseDTO {
    private UUID userId;
    private String email;
    private UUID teamId;
    private String teamName;
    private boolean isTeamLeader;
}
