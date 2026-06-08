package com.hackathon.HackSync.host_core.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HackathonRequestDTO {
    private String title;
    private String tagline;
    private String description;
    private String bannerImageUrl;
    private int minTeamSize;
    private int maxTeamSize;
    private LocalDateTime registrationStart;
    private LocalDateTime registrationEnd;
    private LocalDateTime hackathonStart;
    private LocalDateTime hackathonEnd;
}
