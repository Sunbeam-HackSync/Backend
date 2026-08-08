package com.hackathon.HackSync.mentor_core.dto;

import com.hackathon.HackSync.host_core.entity.HackathonStatus;
import com.hackathon.HackSync.mentor_core.entity.MentorStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MentorAssignedHackathonResponseDTO {
    private Long hackathonId;
    private String title;
    private String tagline;
    private HackathonStatus hackathonStatus;
    private LocalDateTime hackathonStarts;
    private LocalDateTime hackathonEnds;
    private MentorStatus invitationStatus;
}
