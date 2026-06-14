package com.hackathon.HackSync.judge_core.dto;

import com.hackathon.HackSync.judge_core.entity.ProjectSubmissionStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ProjectSubmissionResponseDTO {
    private UUID id;
    private String projectTitle;
    private String tagLine;
    private String description;
    private String githubRepoUrl;
    private String liveDemoUrl;
    private ProjectSubmissionStatus submissionStatus;
    private UUID teamId;
    private String teamName;
    private LocalDateTime submittedAt;
}
