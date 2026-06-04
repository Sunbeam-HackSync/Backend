package com.hackathon.HackSync.judge_core.entity;

import com.hackathon.HackSync.host_core.entity.HackathonTracks;
import com.hackathon.HackSync.host_core.entity.Hackathons;
import com.hackathon.HackSync.participants_core.entity.Teams;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "project_submissions")
public class ProjectSubmissions {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "hackathon_id")
    private Hackathons hackathonId;

    @Column(name = "track_id")
    private HackathonTracks trackId;

    @Column(name = "team_id")
    private Teams teamsId;

    @Column(name = "project_title")
    private String projectTitle;

    @Column(name = "tag_line")
    private String tagLine;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "github_repo_url")
    private String githubRepoUrl;

    @Column(name = "live_demo_url")
    private String liveDemoUrl;

    @Column(name = "submission_status")
    @Enumerated(EnumType.STRING)
    private ProjectSubmissionStatus submissionStatus;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;
}
