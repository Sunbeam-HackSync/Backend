package com.hackathon.HackSync.participants_core.dto;

import lombok.Data;

@Data
public class TeamUpdateRequestDTO {
    private Boolean isLookingForMembers;
    private String skillsNeeded;
}
