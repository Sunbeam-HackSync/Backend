package com.hackathon.HackSync.participants_core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParticipantProfileDTO {
    private String fullName;
    private String avatarURL;
    private String bio;
    private String githubURL;
    private String linkedInURL;
    private String xURL;
    private String techSkills;
}
