package com.hackathon.HackSync.judge_core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WinnerSubmissionRequestDTO {
    private Long submissionId;
    private String categoryName;
}
