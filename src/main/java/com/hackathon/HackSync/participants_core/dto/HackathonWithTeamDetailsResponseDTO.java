package com.hackathon.HackSync.participants_core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HackathonWithTeamDetailsResponseDTO {
    private HackathonDetailResponseDTO hackathonDetails;
    private TeamWithParticipantsResponseDTO teamDetails;
}
