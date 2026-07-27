package com.hackathon.HackSync.participants_core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HackathonWinnerResponseDTO {
    private String categoryName;
    private String teamName;
    private String projectName;
    private String projectDescription;
    private String githubUrl;
    private String liveDemoUrl;
}
