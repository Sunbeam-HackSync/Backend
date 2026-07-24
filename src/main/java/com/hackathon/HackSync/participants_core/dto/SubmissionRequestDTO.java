package com.hackathon.HackSync.participants_core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmissionRequestDTO {
    
    @NotNull(message = "Team ID is required")
    private Long teamId;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    private String githubLink;
    private String demoVideoLink;
}
