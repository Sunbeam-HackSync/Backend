package com.hackathon.HackSync.judge_core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JudgeScoreSubmissionRequestDTO {
    private Long projectId;
    private List<ScoreEntryDTO> scores;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ScoreEntryDTO {
        private Long criteriaId;
        private double scoreGiven;
        private String feedbackNotes;
    }
}
