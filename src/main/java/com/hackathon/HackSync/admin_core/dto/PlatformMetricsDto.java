package com.hackathon.HackSync.admin_core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlatformMetricsDto {
    private long totalActiveHackathons;
    private long totalRegisteredUsers;
    private long totalSubmissions;
}
