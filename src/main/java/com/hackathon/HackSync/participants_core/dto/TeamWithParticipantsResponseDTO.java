package com.hackathon.HackSync.participants_core.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TeamWithParticipantsResponseDTO {
    private Long teamId;
    private String teamName;
    private List<ParticipantResponseDTO> participants;
}
