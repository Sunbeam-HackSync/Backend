package com.hackathon.HackSync.participants_core.dto;

import com.hackathon.HackSync.mentor_core.entity.TicketStatus;
import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HelpTicketResponseDTO {

    private String issueTitle;
    private String issueDescription;
    private String techTags;
    private TicketStatus status;
}

