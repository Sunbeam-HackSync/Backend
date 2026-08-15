package com.hackathon.HackSync.genai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenAICriteriaDTO {
    @JsonProperty("criteriaId")
    private String criteriaId;
    
    @JsonProperty("criteriaName")
    private String criteriaName;
    
    @JsonProperty("criteriaDescription")
    private String criteriaDescription;
    
    @JsonProperty("maxScore")
    private int maxScore;
}
