package com.hackathon.HackSync.mentor_core.dto;

import com.hackathon.HackSync.mentor_core.entity.TicketStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MentorTicketResponseDTO {
    private Long id;
    private Long teamId;
    private String issueTitle;
    private String issueDescription;
    private String techTags;
    private String contactLocation;
    private String participantMeetingLink;
    private String mentorMeetingLink;
    private TicketStatus status;
    private LocalDateTime claimedAt;
    private LocalDateTime resolvedAt;
}
