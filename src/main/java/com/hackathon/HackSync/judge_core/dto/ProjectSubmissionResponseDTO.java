package com.hackathon.HackSync.judge_core.dto;

import com.hackathon.HackSync.judge_core.entity.ProjectSubmissionStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ProjectSubmissionResponseDTO {
    private Long id;
    private String projectTitle;
    private String tagLine;
    private String description;
    private String githubRepoUrl;
    private String liveDemoUrl;
    private ProjectSubmissionStatus submissionStatus;
    private Long teamId;
    private String teamName;
    private LocalDateTime submittedAt;
}
