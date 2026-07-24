package com.hackathon.HackSync.participants_core.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubmissionResponseDTO {
    private Long submissionId;
    private Long teamId;
    private String title;
    private String description;
    private String githubLink;
    private String demoVideoLink;
}
