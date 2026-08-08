package com.hackathon.HackSync.judge_core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuperJudgeSubmissionResponseDTO {
    private ProjectSubmissionResponseDTO submission;
    private List<JudgeEvaluationDTO> evaluations;
    private double totalScore;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JudgeEvaluationDTO {
        private Long judgeId;
        private String judgeEmail;
        private List<ScoreDetailDTO> scoreDetails;
        private double judgeTotalScore;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScoreDetailDTO {
        private Long criteriaId;
        private String criteriaName;
        private double maxScore;
        private double scoreGiven;
        private String feedbackNotes;
    }
}
