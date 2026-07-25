package com.hackathon.HackSync.judge_core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EvaluationCriteriaRequestDTO {
    @NotBlank(message = "Criteria name is required")
    private String criteriaName;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Max score is required")
    private Integer maxScore;
}
