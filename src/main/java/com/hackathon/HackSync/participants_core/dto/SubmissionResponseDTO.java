package com.hackathon.HackSync.participants_core.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubmissionResponseDTO {
    private Long projectSubmissionId;
    private Long teamId;
    private Long hackathonId;
    /*remove this in future cuz there is no need of track */
    private Long trackId;
    private String projectTitle;
    private String tagLine;
    private String description;
    private String githubRepoUrl;
    private String liveDemoUrl;
    private String youtubeUrl;
    private String submissionStatus;
}
