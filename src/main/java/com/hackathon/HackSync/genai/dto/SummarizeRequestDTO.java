package com.hackathon.HackSync.genai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SummarizeRequestDTO {
    
    @JsonProperty("git_repo")
    private String gitRepo;
    
    @JsonProperty("hackathon_id")
    private Long hackathonId;
    
    @JsonProperty("criteriaList")
    private java.util.List<GenAICriteriaDTO> criteriaList;
}
