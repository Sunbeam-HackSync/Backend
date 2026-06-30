package com.hackathon.HackSync.participants_core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HelpTicketRequestDTO {

    @NotNull(message = "Team Id is required")
    private Long teamId;

    @NotBlank(message = "Issue title is required")
    private String issueTitle;

    @NotBlank(message = "Issue description is required")
    private String issueDescription;

    @NotBlank(message = "Tech tags are required")
    private String techTags;
}