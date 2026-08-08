package com.hackathon.HackSync.host_core.dto;

import com.hackathon.HackSync.host_core.entity.HackathonStatus;
import com.hackathon.HackSync.judge_core.dto.ProjectSubmissionResponseDTO;
import com.hackathon.HackSync.participants_core.dto.TeamWithParticipantsResponseDTO;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class HackathonFullDetailResponseDTO {
    private Long id;
    private String title;
    private String tagline;
    private HackathonStatus hackathonStatus;
    private LocalDateTime hackathonStarts;
    private LocalDateTime hackathonEnds;
    private String feedBackNotes;
    
    private List<TeamWithParticipantsResponseDTO> teams;
    private List<JudgeResponseDTO> judges;
    private List<MentorResponseDTO> mentors;
    private List<ProjectSubmissionResponseDTO> submissions;
}
