package com.hackathon.HackSync.host_core.responses;

import com.hackathon.HackSync.host_core.entity.HackathonStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class HackathonResponse {
    private Long id;
    private String title;
    private String tagline;
    private HackathonStatus hackathonStatus;
    private LocalDateTime hackathonStarts;
    private LocalDateTime hackathonEnds;
}
