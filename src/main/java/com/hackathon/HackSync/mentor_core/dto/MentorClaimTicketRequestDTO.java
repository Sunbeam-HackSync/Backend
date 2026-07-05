package com.hackathon.HackSync.mentor_core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MentorClaimTicketRequestDTO {
    private String contactLocation;
    private String meetingLink;
}
