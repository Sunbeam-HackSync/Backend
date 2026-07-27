package com.hackathon.HackSync.participants_core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GetHelpTicketInfoDTO {
    private Long TicketId;
    private Long hackathonId;
    private Long teamId;
    private Long creatorId;
    private Long assignedMentorId;
    private String issueTitle;
    private String issueDescription;
    private String techTags;
    private String contactLocation;
    private String participantMeetingLink;
    private String mentorMeetingLink;
    private String status;
    private String claimedAt;
    private String resolvedAt;
}
