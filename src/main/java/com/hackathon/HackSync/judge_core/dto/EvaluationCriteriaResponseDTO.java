package com.hackathon.HackSync.judge_core.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EvaluationCriteriaResponseDTO {
    private Long id;
    private Long hackathonId;
    private String criteriaName;
    private String description;
    private int maxScore;
}
