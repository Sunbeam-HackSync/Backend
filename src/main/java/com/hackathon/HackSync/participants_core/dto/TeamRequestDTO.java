package com.hackathon.HackSync.participants_core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class TeamRequestDTO {
    @NotNull
    private Long hackathonId;

    @NotBlank
    private String teamName;

    private boolean isLookingForMembers;

    private String skillsNeeded;

}
