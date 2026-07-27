package com.hackathon.HackSync.host_core.dto;

import com.hackathon.HackSync.mentor_core.entity.MentorStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MentorResponseDTO {
    private Long id;
    private Long userId;
    private String email;
    private String expertiseTags;
    private MentorStatus status;
}
