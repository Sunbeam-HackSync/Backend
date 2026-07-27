package com.hackathon.HackSync.participants_core.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ParticipantResultResponseDTO {
    private Long hackathonId;
    private String hackathonTitle;
    private String teamName;
    private String projectName;
    private boolean isWinner;
    private String awardCategory;
    private List<ScoreDetailDTO> scores;
    private double totalScore;

    @Data
    @Builder
    public static class ScoreDetailDTO {
        private String criteriaName;
        private double maxScore;
        private double scoreGiven;
        private String feedbackNotes;
    }
}
