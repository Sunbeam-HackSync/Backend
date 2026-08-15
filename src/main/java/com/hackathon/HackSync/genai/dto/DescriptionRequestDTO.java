package com.hackathon.HackSync.genai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DescriptionRequestDTO {
    private String title;
    private String tagline;
    
    @JsonProperty("powered_by")
    private String poweredBy;

    @JsonProperty("problem_worth_solving")
    private String problemWorthSolving;
    
    @JsonProperty("hackathon_overview")
    private String hackathonOverview;

    @JsonProperty("total_prize_pool")
    private String totalPrizePool;
    
    @JsonProperty("prize_breakdown")
    private String prizeBreakdown;
    
    @JsonProperty("categories_tracks")
    private String categoriesTracks;

    @JsonProperty("start_date")
    private String startDate;
    
    @JsonProperty("end_date")
    private String endDate;
    
    @JsonProperty("winners_announced")
    private String winnersAnnounced;

    @JsonProperty("steps_to_participate")
    private String stepsToParticipate;
    
    @JsonProperty("important_links")
    private String importantLinks;

    @JsonProperty("evaluation_criteria")
    private String evaluationCriteria;
    
    @JsonProperty("judges_details")
    private String judgesDetails;

    @JsonProperty("who_should_join")
    private String whoShouldJoin;
    
    @JsonProperty("perks_of_participating")
    private String perksOfParticipating;

    private List<Map<String, String>> faq;

    @JsonProperty("sponsor_description")
    private String sponsorDescription;
}
