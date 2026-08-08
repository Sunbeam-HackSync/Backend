package com.hackathon.HackSync.participants_core.dto;

import com.hackathon.HackSync.host_core.entity.HackathonStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HackathonDetailResponseDTO {
    private Long id;
    private String title;
    private String tagline;
    private String description;
    private String bannerImageUrl;
    private String profileImageUrl;
    private Integer minTeamSize;
    private Integer maxTeamSize;
    private LocalDateTime registrationStart;
    private LocalDateTime registrationEnd;
    private LocalDateTime hackathonStart;
    private LocalDateTime hackathonEnd;
    private HackathonStatus hackathonStatus;
    private String faq;
    private String rules;
    private LocalDateTime resultDeclarationDate;
    private String feedBackNotes;
}
