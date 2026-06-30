package com.hackathon.HackSync.participants_core.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeamResponseDTO {
    private Long teamId;
    private Long hackathonId;
    private String teamName;
    private boolean isLookingForMembers;
    private String skillsNeeded;
    private Long leaderId;
}
