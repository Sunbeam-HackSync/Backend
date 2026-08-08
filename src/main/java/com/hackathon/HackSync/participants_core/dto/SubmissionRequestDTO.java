package com.hackathon.HackSync.participants_core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmissionRequestDTO {

    @NotNull(message = "Team ID is required")
    private Long teamId;

    @NotBlank(message = "Project Title is required")
    private String projectTitle;

    private String tagLine;

    @NotBlank(message = "Description is required")
    private String description;

    private String githubRepoUrl;
    private String liveDemoUrl;
    private String youtubeUrl;

}
