package com.hackathon.HackSync.mentor_core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeetingResponseDTO {
    private String roomName;
    private String mentorLink;
    private String participantLink;
}
